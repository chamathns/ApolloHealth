package com.example.apollohealth;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apollohealth.restarter.SensorRestarterBroadcastReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class JournalActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;

    private Button btnNavToAppMonitor;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
//
        setContentView(R.layout.activity_journal);
//        mSensorService = new SensorService((getCtx()));
//        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
//        if(!isMyServiceRunning(mSensorService.getClass())){
//            startService(mServiceIntent);
//        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SensorRestarterBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
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
