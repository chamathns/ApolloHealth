package com.example.apollohealth;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apollohealth.db.DatabaseHandler;
import com.example.apollohealth.restarter.SensorRestarterBroadcastReceiver;
import com.example.apollohealth.screentimecounter.ScreenTimerService;
import com.example.apollohealth.unlockcounter.UnlockCounterService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import customfonts.MyTextView_Roboto_Regular;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String LOG_TAG = "APOLLOHEALTH_MAIN";

    Context ctx;
    DatabaseHandler myDB;

    private Spinner timeSpinner;
    private TextView physicalTextView;
    private TextView emotionalTextView;
    private CircleImageView profileImage;
    private TextView flightText;
    private TextView caloriesText;
    private TextView stepsText;
    private TextView physicalStatusText;
    private TextView emotionalStatusText;
    private MyTextView_Roboto_Regular displayName;

    private MetricGenerator metrics;
    private Cursor physicalData;

    private int flight = 0;
    private int duration;
    private int steps = 0;
    private String displayText = "John";
//    private View mainView;

    Intent unlockCounterServiceIntent;
    private UnlockCounterService unlockCounterService;

    Intent screenTimeServiceIntent;
    private ScreenTimerService screenTimerService;

    public Context getCtx() {
        return ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
////        startService(new Intent(MainActivity.this, UnlockCounterService.class));
////        startService(new Intent(MainActivity.this, ScreenTimerService.class));

        setContentView(R.layout.activity_main);

        myDB = new DatabaseHandler(this);
        metrics = new MetricGenerator(193, 88);

        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        flightText = (TextView) findViewById(R.id.flightText);
        stepsText = (TextView) findViewById(R.id.stepsText);
        caloriesText = (TextView) findViewById(R.id.caloriesText);
        physicalStatusText = (TextView) findViewById(R.id.physicalStatusText);
        emotionalStatusText = (TextView) findViewById(R.id.emotionalStatusText);
        displayName = (MyTextView_Roboto_Regular) findViewById((R.id.displayName));

        unlockCounterService = new UnlockCounterService(getCtx());
        unlockCounterServiceIntent = new Intent(getCtx(), unlockCounterService.getClass());
        if (!isServiceRunning(unlockCounterService.getClass())) {
            startService(unlockCounterServiceIntent);
        }

        screenTimerService = new ScreenTimerService(getCtx());
        screenTimeServiceIntent = new Intent(getCtx(), screenTimerService.getClass());
        if (!isServiceRunning(screenTimerService.getClass())) {
            startService(screenTimeServiceIntent);
        }

        addItemsSpinner();
        addBottomNavigation();
    }

    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SensorRestarterBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }

        Cursor userData = myDB.getUserData();
        userData.moveToFirst();
        if (userData.moveToFirst()) {
            displayText = userData.getString(1);
            displayName.setText(String.format("Hi, %s", displayText));
        } else {
            displayName.setText(String.format("Hi, %s", displayText));
        }

        physicalData = myDB.getPhysicalData(duration);

        if (physicalData != null) {
            physicalData.moveToFirst();
            for (int i = 0; i < physicalData.getCount(); i++) {
                String tempFlight = physicalData.getString(2);
                flight += Integer.parseInt(tempFlight);

                physicalData.moveToNext();
            }

            physicalData.moveToFirst();
            for (int j = 0; j < physicalData.getCount(); j++) {
                steps += Integer.parseInt(physicalData.getString(1));

                physicalData.moveToNext();
            }
        }
//        if (physicalData.moveToFirst()) {
//            flight = physicalData.getString(2);
//        }

        flightText.setText(String.valueOf(flight));
        stepsText.setText(String.valueOf(steps));
        caloriesText.setText(String.format("%.2f", metrics.caloriesBurned(steps, flight)));

        getStatus();

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(LOG_TAG, "ServiceRunning: TRUE");
                return true;
            }
        }
        Log.i(LOG_TAG, "ServiceRunning: FALSE");
        return false;
    }

    private void getStatus() {
        int status = metrics.getPhysicalStatus(steps, flight, duration);
        switch (status){
            case 0:
                physicalStatusText.setText("Not Good");
                physicalStatusText.setTextColor(Color.parseColor("#ba031c"));
                break;
            case 1:
                physicalStatusText.setText("Moderate");
                physicalStatusText.setTextColor(Color.parseColor("#dbc20b"));
                break;
            case 2:
                physicalStatusText.setText("Good");
                physicalStatusText.setTextColor(Color.parseColor("#1bbc44"));
                break;
        }

        emotionalStatusText.setText("Not Good");
        emotionalStatusText.setTextColor(Color.parseColor("#ba031c"));
    }

    public void addBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final int result = 1;
                switch (menuItem.getItemId()) {

                    case R.id.action_profile:
                        Intent profileIntent = new Intent(MainActivity.this, MainActivity.class);
                        startActivityForResult(profileIntent, result);
                        break;

                    case R.id.action_health:
                        Intent healthIntent = new Intent(MainActivity.this, HealthActivity.class);
                        startActivityForResult(healthIntent, result);
                        break;

                    case R.id.action_journal:
                        Intent journalIntent = new Intent(MainActivity.this, JournalActivity.class);
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

    public void addItemsSpinner() {
//        mainView = (View) findViewById(R.id.view);
        physicalTextView = (TextView) findViewById(R.id.physical1);
        emotionalTextView = (TextView) findViewById(R.id.emotional1);

        timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
        timeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.timeframe_report_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeSpinner.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        flight = 0;
        switch (pos) {
            case 0:
                duration = 3;
                break;
            case 1:
                duration = 7;
                break;
            case 2:
                duration = 30;
                break;
            case 3:
                duration = 365;
        }

        physicalData = myDB.getPhysicalData(duration);
        if (physicalData != null) {
            physicalData.moveToFirst();
            for (int i = 0; i < physicalData.getCount(); i++) {
                flight += Integer.parseInt(physicalData.getString(2));

                physicalData.moveToNext();
            }

            physicalData.moveToFirst();
            for (int j = 0; j < physicalData.getCount(); j++) {
                steps += Integer.parseInt(physicalData.getString(1));

                physicalData.moveToNext();
            }
        }

        flightText.setText(String.valueOf(flight));
        stepsText.setText(String.valueOf(steps));
        caloriesText.setText(String.format("%.2f", metrics.caloriesBurned(steps, flight)));

        getStatus();

        physicalTextView.setText(parent.getItemAtPosition(pos).toString());
        emotionalTextView.setText(parent.getItemAtPosition(pos).toString());

        Toast.makeText(
                parent.getContext(),
                "Report Type : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT)
                .show();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void dpClick(View view) {
        Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
        startActivityForResult(aboutIntent, 1);
    }

    protected void onPause() {
        super.onPause();
        myDB.close();
    }

    @Override
    protected void onDestroy() {
        stopService(unlockCounterServiceIntent);
        stopService(screenTimeServiceIntent);
        Log.i(LOG_TAG, "onDestroy");
        super.onDestroy();
    }
}
