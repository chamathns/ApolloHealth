package com.example.apollohealth;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apollohealth.db.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class JournalActivity extends AppCompatActivity {
    public static final String TAG = "J_ACTIVITY";

    Intent mServiceIntent;
    Context ctx;
    private SensorService mSensorService;
    private DatabaseHandler myDB;

    Button btnNavToAppMonitor;
    Spinner durationSpinner;
    TextView numUnlocksText;
    TextView screenTimeText;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        ctx = this;

//        mSensorService = new SensorService((getCtx()));
//        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
//        if(!isMyServiceRunning(mSensorService.getClass())){
//            startService(mServiceIntent);
//        }

        numUnlocksText = findViewById(R.id.numUnlocksText);
        screenTimeText = findViewById(R.id.screenTimeText);

        myDB = new DatabaseHandler(this);
        getData(1);

        durationSpinner = findViewById(R.id.durationSpinner);
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Log.d(TAG, "onItemSelected: Loading data for 1 day");
                    getData(1);
                } else if (i == 1) {
                    Log.d(TAG, "onItemSelected: Loading data for 3 days");
                    getData(3);
                } else if (i == 2) {
                    Log.d(TAG, "onItemSelected: Loading data for 7 days");
                    getData(7);
                } else if (i == 3) {
                    Log.d(TAG, "onItemSelected: Loading data for 30 days");
                    getData(30);
                } else if (i == 4) {
                    Log.d(TAG, "onItemSelected: Loading data for 365 days");
                    getData(365);
                } else {
                    Log.d(TAG, "onItemSelected: Invalid selection");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnNavToAppMonitor = findViewById(R.id.btn_nav_app_monitor);
        btnNavToAppMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAppMonitor();
            }
        });

        addBottomNavigation();
    }

    private void getData(int numDays) {
        Cursor emotionData = myDB.getEmotionData(numDays);

        int totalTime = 0;
        int totalUnlocks = 0;

        if (emotionData == null) {
            screenTimeText.setText("OnScreen Time: No data available");
            numUnlocksText.setText("Phone Unlocks: No data available");
        } else {
            Log.d(TAG, "getData: Getting data for " + numDays + " days");

            emotionData.moveToFirst();

            for (int i = 0; i < emotionData.getCount(); i++) {
                totalTime += Integer.parseInt(emotionData.getString(0));
                totalUnlocks += Integer.parseInt(emotionData.getString(1));

                emotionData.moveToNext();
            }

            int timeSec = totalTime % 60;
            int timeMin = (totalTime / 60) % 60;
            int timeHrs = (totalTime / 60) / 60;

            String timeString = "0";

            if(timeHrs > 0){
                timeString = timeHrs + "hrs " + timeMin + "mins" + timeSec + "sec";
            } else if (timeMin > 0){
                timeString = timeMin + "mins" + timeSec + "sec";
            } else if (timeSec > 0){
                timeString = timeSec + "sec";
            }

            screenTimeText.setText("OnScreen Time:\n " + timeString);
            numUnlocksText.setText("Phone Unlocks: " + totalUnlocks);
        }
    }

    public void goToAppMonitor() {
        Intent intent = new Intent(JournalActivity.this, AppMonitorActivity.class);
        startActivity(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    protected void onDestroy() {
//        stopService(mServiceIntent);
//        Log.i("MAINACT", "onDestroy!");
//        super.onDestroy();
//
//    }


    public void addBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final int result = 1;
                switch (menuItem.getItemId()) {
                    case R.id.action_profile:
                        Intent profileIntent = new Intent(JournalActivity.this, MainActivity.class);
                        startActivityForResult(profileIntent, result);
                        break;

                    case R.id.action_health:
                        Intent healthIntent = new Intent(JournalActivity.this, HealthActivity.class);
                        startActivityForResult(healthIntent, result);
                        break;

                    case R.id.action_journal:
                        Intent journalIntent = new Intent(JournalActivity.this, JournalActivity.class);
                        startActivityForResult(journalIntent, result);
                        break;
//                        startActivity(journalIntent);
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
                return false;
            }
        });
    }
}
