package com.example.apollohealth;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class JournalActivity extends AppCompatActivity {
    public static final String TAG = "J_ACTIVITY";

    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;

    private Button btnNavToAppMonitor;
    Spinner durationSpinner;

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

        durationSpinner = findViewById(R.id.durationSpinner);
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Log.d(TAG, "onItemSelected: Loading data for 1 day");

                } else if (i == 1) {
                    Log.d(TAG, "onItemSelected: Loading data for 3 days");

                } else if (i == 2) {
                    Log.d(TAG, "onItemSelected: Loading data for 7 days");

                } else if (i == 3) {
                    Log.d(TAG, "onItemSelected: Loading data for 30 days");

                } else if (i == 4) {
                    Log.d(TAG, "onItemSelected: Loading data for 365 days");

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
