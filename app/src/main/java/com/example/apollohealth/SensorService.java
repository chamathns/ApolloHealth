package com.example.apollohealth;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.apollohealth.db.DatabaseHandler;
import com.example.apollohealth.db.UserProfile;

import java.util.Timer;
import java.util.TimerTask;
import static java.lang.Math.abs;

public class SensorService extends Service implements SensorEventListener {

    protected static final int NOTIFICATION_ID = 1337;
    protected static final int NOTIFICATION_ID_FLIGHT = 1338;
    protected static final int NOTIFICATION_ID_STEP = 1339;
    private static String TAG = "Service";
    private static Service mCurrentService;
    private int counter = 0;

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Sensor stepDetector;

    private float initHeight;
    private int flights = 0;
    private MetricGenerator metrics;
    private float mDistance = (float) 0.0;
    // Steps counted in current session
    private int mSteps = 0;
    private UserProfile userProfile;
    public SensorService() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("User Data", "On crreaaaaaaaaaaaaaaaaaaaaaaaaaaaaate get user data");

        DatabaseHandler myDB = new DatabaseHandler(this);

        if (!myDB.getUserData().moveToFirst()) {
            myDB.insertUserData("John Doe", 20, "Male", 60, 170);
        } else {
            userProfile = myDB.getUserProfile();
        }
        myDB.close();
        metrics = new MetricGenerator(userProfile.getuHeight(),userProfile.getuWeight());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        mCurrentService = this;
        Log.i("Important", "On crreaaaaaaaaaaaaaaaaaaaaaaaaaaaaate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");
        counter = 0;
//        flights = 0;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL );

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        startTimer();

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Physical Tracker", "Your movements are being tracked by ApolloHealth", R.drawable.heart));
                Log.i(TAG, "restarting foreground successful");
                startTimer();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        Log.i("DB", "Writing steps");
        DatabaseHandler myDB = new DatabaseHandler(this);
        myDB.updateHealthData(System.currentTimeMillis(), 0, 0, 0, 0, mSteps, 0);
        myDB.close();
//
        sensorManager.unregisterListener(this);
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }


    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        Log.i(TAG, "Starting timer");

        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        Log.i(TAG, "Scheduling...");
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        Log.i(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
//                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static Service getmCurrentService() {
        return mCurrentService;
    }

    public static void setmCurrentService(Service mCurrentService) {
        SensorService.mCurrentService = mCurrentService;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float[] pValues = event.values;
            float cHeight = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pValues[0]);
            if (initHeight == 0) {
                initHeight = cHeight;
            }
            int heightDiff = abs((int) (cHeight - initHeight));
            if (1 <= heightDiff / 3 && heightDiff / 3 <= 2) {
                flights += heightDiff / 3;
                Log.i("DB", "Writing flights climbed");
                DatabaseHandler myDB = new DatabaseHandler(this);
                myDB.updateHealthData(System.currentTimeMillis(), 0, 0, 0, 0, 0, 1);

                Cursor flightData = myDB.getPhysicalData(1);
                flightData.moveToFirst();
                int f = Integer.parseInt(flightData.getString(2));
                if(f >= 8) {
                    try {
                        Notification notification = new Notification();
                        startForeground(NOTIFICATION_ID_FLIGHT, notification.setNotification(this, "Target Achieved!", "You just completed the daily target for flights climbed.", R.drawable.heart));
                    } catch (Exception e) {
                        Log.e("Error", "Error in notification " + e.getMessage());
                    }
                }


                myDB.close();
                initHeight = cHeight;
            } else {
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            mSteps += event.values.length;

            Log.i(TAG,
                    "New step detected by STEP_DETECTOR sensor. Total step count: " + mSteps);

            if (mSteps > 10){

                mDistance = metrics.stepsToKm((float) mSteps)*1000;
                Log.i("DB", "Writing steps and distance = " + mDistance);
                DatabaseHandler myDB = new DatabaseHandler(this);
                myDB.updateHealthData(System.currentTimeMillis(), 0, 0, 0, Math.round(mDistance), mSteps, 0);

                Cursor steptData = myDB.getPhysicalData(1);
                steptData.moveToFirst();
                int s = Integer.parseInt(steptData.getString(1));
                if(s == 5000) {
                    try {
                        Notification notification = new Notification();
                        startForeground(NOTIFICATION_ID_STEP, notification.setNotification(this, "Target Achieved!", "You just completed the daily target for steps.", R.drawable.heart));
                    } catch (Exception e) {
                        Log.e("Error", "Error in notification " + e.getMessage());
                    }
                }

                myDB.close();
                mSteps = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
