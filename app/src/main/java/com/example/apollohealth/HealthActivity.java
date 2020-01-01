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
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import com.example.apollohealth.db.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HealthActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button startBtn;
    private TextView sensorText;
    private TextView caloryText;
    private Spinner typeSpinner;
    private Spinner durationSpinner;

    private float initHeight;
    private int flights = 0;
    private int duration = 0;
    private int dbColumn = 0;
    private int steps;
    String tempFlight;
    public int height = 10;
//    public Integer height = 10;

    private MetricGenerator metrics;
    private DatabaseHandler myDB;
    private Cursor physicalData;
    private AnyChartView anyChartView;
    private Cartesian cartesian;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        addBottomNavigation();
        addItemsSpinner();

        anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        cartesian = AnyChart.column();

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
        if (adapterView.getId() == R.id.typeSpinner) {
//            Log.i("Spinner", "Changeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            flights = 0;
            switch (pos) {
                case 0:
                    dbColumn = 1;
                    Log.i("Spinner", "Changeeeeeeeeeeeeeeeeeeeeeeeeeeeee1");
                    break;
                case 1:
                    dbColumn = 2;
                    Log.i("Spinner", "Changeeeeeeeeeeeeeeeeeeeeeeeeeeeee2");
                    break;
                case 2:
                    break;
//                    duration = 30;
            }
        } else if (adapterView.getId() == R.id.durationSpinner) {
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
        }

        Log.i("DB", "Reading from database");
        physicalData = myDB.getPhysicalData(duration);

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();

        if (physicalData != null) {
//            Log.i("DB", "ifffffffffffffffffffffffffffffffffffff");
            physicalData.moveToFirst();
            for (int i = 0; i < physicalData.getCount(); i++) {
                String tempFlight = physicalData.getString(dbColumn);
                height = Integer.parseInt(tempFlight);
                Log.i("DB", "looooooooooooooooooooooooooop   " + i + "  " + tempFlight + "  " + dbColumn);
                data.add(new ValueDataEntry(i + 1, height));
//                flights += height;
//                flight.add(height);
                physicalData.moveToNext();
            }
        }

        Log.i("DB", "cheeeeeeeeeeeeeeeeeeeeeeeeeeeeeeck   " + data.size());
        if (dbColumn == 1) {
            sensorText.setText(String.format("Steps taken: %d", flights));
            caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned(0, flights)));
        } else if (dbColumn == 2)
            sensorText.setText(String.format("Flights climbed: %d", flights));
            caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned(0, flights)));

        Column column = cartesian.column(data);

        anyChartView.setChart(cartesian);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
