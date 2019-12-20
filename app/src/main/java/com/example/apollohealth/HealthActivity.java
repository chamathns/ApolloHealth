package com.example.apollohealth;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static java.lang.Math.abs;

public class HealthActivity extends Activity implements SensorEventListener, StepListener {

    private TextView tvSteps;
    private Button startBtn;
    private Button endBtn;
    private TextView pressureText;
    private TextView caloryText;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private Sensor pressureSensor;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private float initHeight;
    private int flights = 0;
    private MetricGenerator metrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        addBottomNavigation();

        initHeight = 0;
        metrics = new MetricGenerator(193, 88);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        tvSteps = (TextView) findViewById(R.id.test_text);
        pressureText = (TextView) findViewById(R.id.pressureText);
        caloryText = (TextView) findViewById(R.id.caloryText);
        startBtn = (Button) findViewById(R.id.startBtn);
        endBtn = (Button) findViewById(R.id.endBtn);


        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                sensorManager.registerListener(HealthActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        endBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(HealthActivity.this);

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
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        } else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float[] pValues = event.values;
            float cHeight = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pValues[0]);
            if (initHeight == 0) {
                initHeight = cHeight;
            }
            int heightDiff = abs((int) (cHeight - initHeight));
            if (1 <= heightDiff / 3 && heightDiff / 3 <= 2) {
                flights += heightDiff / 3;
//            pressureText.setText(String.format("%.3f mbar", pValues[0]));
                pressureText.setText(
                        String.format("Flights climbed: %d Diff: %d \nInitial: %.2f \nCurrent: %.2f", flights, heightDiff, initHeight, cHeight));
                initHeight = cHeight;
                caloryText.setText(Float.toString(metrics.caloriesBurned(numSteps, flights)));
            } else {
                pressureText.setText(
                        String.format("Flights climbed: %d Diff: %d \nInitial: %.2f \nCurrent: %.2f", flights, heightDiff, initHeight, cHeight));

                caloryText.setText(Float.toString(metrics.caloriesBurned(numSteps, flights)));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        tvSteps.setText(TEXT_NUM_STEPS + numSteps);
        caloryText.setText(Float.toString(metrics.caloriesBurned(numSteps, flights)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
