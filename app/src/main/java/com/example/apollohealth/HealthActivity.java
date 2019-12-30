package com.example.apollohealth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.apollohealth.db.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class HealthActivity extends Activity {

    private Button startBtn;
    private TextView pressureText;
    private TextView caloryText;

    private float initHeight;
    private int flights = 0;
    private String height = "0";
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

            }
        });

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);

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

        Log.i("DB", "Reading from database");
        Cursor physicalData = myDB.getPhysicalData(3);
        physicalData.moveToFirst();
        if (physicalData.moveToFirst()) {
            height = physicalData.getString(2);
        }

        pressureText.setText(String.format("Flights climbed: %s", height));

        caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned(0, Integer.parseInt(height))));
    }

    @Override
    protected void onPause() {
        super.onPause();
        myDB.close();
    }
}
