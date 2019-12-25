package com.example.apollohealth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.apollohealth.db.DatabaseHandler;

import static java.lang.Math.abs;

public class HealthActivity extends Activity {

    private Button startBtn;
    private TextView pressureText;
    private TextView caloryText;

    private float initHeight;
    private int flights = 0;
    private MetricGenerator metrics;
    private DatabaseHandler myDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        addBottomNavigation();

        initHeight = 0;
        metrics = new MetricGenerator(193, 88);

        pressureText = (TextView) findViewById(R.id.pressureText);
        caloryText = (TextView) findViewById(R.id.caloryText);
        startBtn = (Button) findViewById(R.id.startBtn);

        myDB = new DatabaseHandler(this);

        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.i("DB", "Reading from database");
                Cursor physicalData =  myDB.getPhysicalData(3);
                physicalData.moveToFirst();
                String height = physicalData.getString(2);

                pressureText.setText(String.format("Flights climbed: %s", height));

                caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned(0, Integer.parseInt(height))));

            }
        });

    }

    public void addBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final int result = 1;
                switch (menuItem.getItemId()) {

                    case R.id.action_profile:
                        Intent profileIntent = new Intent(HealthActivity.this, MainActivity.class);
                        startActivityForResult(profileIntent, result);
                        break;

                    case R.id.action_health:
                        Intent healthIntent = new Intent(HealthActivity.this, HealthActivity.class);
                        startActivityForResult(healthIntent, result);
                        break;

                    case R.id.action_journal:
                        Intent journalIntent = new Intent(HealthActivity.this, JournalActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myDB.close();
    }
}
