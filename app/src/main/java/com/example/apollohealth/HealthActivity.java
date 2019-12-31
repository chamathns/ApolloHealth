package com.example.apollohealth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.apollohealth.db.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class HealthActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private Button startBtn;
    private TextView sensorText;
    private TextView caloryText;
    private Spinner typeSpinner;
    private Spinner durationSpinner;

    private float initHeight;
    private int flights = 0;
    private int duration;
    private int column;
    private int steps;
    private String height = "0";

    private MetricGenerator metrics;
    private DatabaseHandler myDB;
    private Cursor physicalData;
    GraphView graph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        addBottomNavigation();
        addItemsSpinner();

        initHeight = 0;
        metrics = new MetricGenerator(193, 88);

        sensorText = (TextView) findViewById(R.id.sensorText);
        caloryText = (TextView) findViewById(R.id.caloryText);
        startBtn = (Button) findViewById(R.id.startBtn);

        myDB = new DatabaseHandler(this);

        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });

        graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<>(new DataPoint[]{
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        BarGraphSeries<DataPoint> barSeries = new BarGraphSeries<>(new DataPoint[]{
//                new DataPoint(0, -1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6),
//                new DataPoint(5, 2)
//        });

//        graph.addSeries(barSeries);
//        barSeries.setSpacing(25);
//        barSeries.setDrawValuesOnTop(true);

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

    public void addItemsSpinner() {

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        typeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.statistic_type_array, android.R.layout.simple_spinner_item);

        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeSpinner.setAdapter(adapterType);

        durationSpinner = (Spinner) findViewById(R.id.durationSpinner);
        durationSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(this,
                R.array.timeframe_report_array, android.R.layout.simple_spinner_item);

        adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        durationSpinner.setAdapter(adapterDuration);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if(adapterView.getId() == R.id.typeSpinner){
            Log.i("Spinner", "Changeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            switch (pos) {
                case 0:
                    column = 2;
                    break;
                case 1:
                    column = 1;
                    break;
                case 2:
//                    duration = 30;
            }
        }
        else if (adapterView.getId() == R.id.durationSpinner){
            flights = 0;
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

            Log.i("DB", "Reading from database");
            physicalData = myDB.getPhysicalData(duration);
            DataPoint dp[] = new DataPoint[duration];

            if (physicalData != null) {
                Log.i("DB", "ifffffffffffffffffffffffffffffffffffff");
                physicalData.moveToFirst();
                for (int i = 0; i < physicalData.getCount(); i++) {
                    Log.i("DB", "looooooooooooooooooooooooooop" + physicalData.getCount());
                    String tempFlight = physicalData.getString(column);
                    flights += Integer.parseInt(tempFlight);
                    dp[i] = new DataPoint(i+1,Double.parseDouble(tempFlight));
                    height = tempFlight;

                    physicalData.moveToNext();
                }
            }

            sensorText.setText(String.format("Flights climbed: %d     %s", flights, height));

            caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned(0, flights)));

            BarGraphSeries barSeries = new BarGraphSeries<>(dp);
            graph.addSeries(barSeries);
            barSeries.setSpacing(10);
            barSeries.setDrawValuesOnTop(true);


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
