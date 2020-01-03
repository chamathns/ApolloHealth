package com.example.apollohealth;

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
import com.anychart.core.cartesian.series.Column;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import com.example.apollohealth.db.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HealthActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button startBtn;
    private TextView sensorText;
    private TextView caloryText;
    private TextView dailyText;
    private Spinner typeSpinner;
    private Spinner durationSpinner;
    private TextView textViewSteps;

    private int totalValue = 0;
    private int duration = 3;
    private int dbColumn = 1;
    public int currentValue = 0;

    private MetricGenerator metrics;
    private DatabaseHandler myDB;
    private Cursor physicalData;
    private AnyChartView anyChartView;
    private Cartesian cartesian;
    private Set set;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        addBottomNavigation();
        addItemsSpinner();


//        anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
//        cartesian = AnyChart.column();

//        initHeight = 0;
        metrics = new MetricGenerator(193, 88);

        sensorText = (TextView) findViewById(R.id.sensorText);
        caloryText = (TextView) findViewById(R.id.caloryText);
        dailyText = (TextView) findViewById(R.id.dailyText);
//        startBtn = (Button) findViewById(R.id.startBtn);

        myDB = new DatabaseHandler(this);

//        anyChartView = (AnyChartView) findViewById(R.id.any_chart_view1);

        physicalData = myDB.getPhysicalData(duration);
        anyChartView = (AnyChartView) findViewById(R.id.any_chart_view1);

        cartesian = AnyChart.column();
        set = Set.instantiate();

        List<DataEntry> data1 = new ArrayList<>();
//        List<DataEntry> data2 = new ArrayList<>();

        if (physicalData != null) {
            physicalData.moveToFirst();
            for (int i = 0; i < physicalData.getCount(); i++) {
                String tempValue = physicalData.getString(dbColumn);
                currentValue = Integer.parseInt(tempValue);
                data1.add(new ValueDataEntry(i + 1, currentValue));
                physicalData.moveToNext();
            }
        }

        set.data(data1);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");

        Column column = cartesian.column(series1Data);

        column.color("#641783");

        column.tooltip()
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}");

        cartesian.animation(true);
//        cartesian.background().stroke("5 #2393B7");
//        cartesian.background().fill("#000000");
        cartesian.dataArea().background().enabled(true);
        cartesian.dataArea().background().fill("#2393B7 0.2");

        anyChartView.setChart(cartesian);


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
            ;
            totalValue = 0;
            switch (pos) {
                case 0:
                    dbColumn = 1;
                    break;
                case 1:
                    dbColumn = 2;
                    break;
                case 2:
                    dbColumn = 0;
                    break;
            }
        } else if (adapterView.getId() == R.id.durationSpinner) {
            totalValue = 0;
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
//        anyChartView = (AnyChartView) findViewById(R.id.any_chart_view1);
//        anyChartView.clear();
//        APIlib.getInstance().setActiveAnyChartView(anyChartView);
//        anyChartView.setVisibility(View.GONE);
//        cartesian = AnyChart.column();
//        set = Set.instantiate();

//        List<DataEntry> data1 = new ArrayList<>();
        List<DataEntry> data2 = new ArrayList<>();

        if (physicalData != null) {
//            Log.i("DB", "ifffffffffffffffffffffffffffffffffffff");
            physicalData.moveToFirst();
            for (int i = 0; i < physicalData.getCount(); i++) {
                String tempFlight = physicalData.getString(dbColumn);
                currentValue = Integer.parseInt(tempFlight);
                Log.i("DB", "looooooooooooooooooooooooooop   " + i + "  " + tempFlight + "  " + dbColumn);
                data2.add(new ValueDataEntry(i + 1, currentValue));
                totalValue += currentValue;
                physicalData.moveToNext();
            }
        }

        set.data(data2);
//        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");

//        Column column = cartesian.column(series1Data);

//        Log.i("DB", "cheeeeeeeeeeeeeeeeeeeeeeeeeeeeeeck   " + data2.size());
        if (dbColumn == 1) {
            sensorText.setText(String.format("Steps taken: %d", totalValue));
            dailyText.setText(String.format("Steps taken today: %d", currentValue));
            caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned(totalValue, 0)));
        } else if (dbColumn == 2) {
            sensorText.setText(String.format("Flights climbed: %d", totalValue));
            dailyText.setText(String.format("Flights climbed today: %d", currentValue));
            caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned(0, totalValue)));
        } else if (dbColumn == 0) {
            float f = (float) totalValue;
            sensorText.setText(String.format("Distance travelled: %d", totalValue));
            dailyText.setText(String.format("Distance travelled today: %d", currentValue));
            caloryText.setText(String.format("Calories burned: %.2f", metrics.caloriesBurned((int) metrics.kmsToSteps(f), 0)));
        }


//        Column column = cartesian.column(data);

//        switch (dbColumn){
//            case 1:
//                cartesian.data(data1);
//                break;
//            case 2:
//                cartesian.data(data2);
//                break;
//        }
//        cartesian.data(data1);

//        anyChartView.setChart(cartesian);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
