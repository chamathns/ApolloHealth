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

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.apollohealth.db.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class JournalActivity extends AppCompatActivity {
    public static final String TAG = "J_ACTIVITY";
    private static final Format dateFormat = new SimpleDateFormat("yyyyMMdd");

    Intent mServiceIntent;
    Context ctx;
    private SensorService mSensorService;
    private DatabaseHandler myDB;

    Button btnNavToAppMonitor;
    Spinner durationSpinner;
    TextView numUnlocksText;
    TextView screenTimeText;
    AnyChartView usageTimeChart;
    AnyChartView numUnlocksChart;

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

        usageTimeChart = findViewById(R.id.usage_time_chart);
        APIlib.getInstance().setActiveAnyChartView(usageTimeChart);
        usageTimeChart.setProgressBar(findViewById(R.id.time_progress_bar));
        Cartesian timeCartesian = AnyChart.column();
        Column timeColumn = timeCartesian.column(getChartData(0));
        timeColumn.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");
        timeCartesian.animation(true);
        timeCartesian.title("OnScreen time during the past week");
        timeCartesian.yScale().minimum(0d);
        timeCartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        timeCartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        timeCartesian.interactivity().hoverMode(HoverMode.BY_X);
        timeCartesian.xAxis(0).title("Date");
        timeCartesian.yAxis(0).title("Time(s)");
        usageTimeChart.setChart(timeCartesian);

        numUnlocksChart = findViewById(R.id.num_unlocks_chart);
        APIlib.getInstance().setActiveAnyChartView(numUnlocksChart);
        numUnlocksChart.setProgressBar(findViewById(R.id.unlocks_progress_bar));
        Cartesian unlocksCartesian = AnyChart.column();
        Column unlocksColumn = unlocksCartesian.column(getChartData(1));
        unlocksColumn.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");
        unlocksCartesian.animation(true);
        unlocksCartesian.title("Number of phone unlocks during the past week");
        unlocksCartesian.yScale().minimum(0d);
        unlocksCartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        unlocksCartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        unlocksCartesian.interactivity().hoverMode(HoverMode.BY_X);
        unlocksCartesian.xAxis(0).title("Date");
        unlocksCartesian.yAxis(0).title("Number of Unlocks");
        numUnlocksChart.setChart(unlocksCartesian);

        addBottomNavigation();
    }

    private ArrayList getChartData(int col) {
        ArrayList<DataEntry> entries = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            String timestampDate = String.valueOf(Long.parseLong(dateFormat.format(System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L))));
            String chartTime = timestampDate.substring(timestampDate.length() - 2) + "/" + timestampDate.substring(timestampDate.length() - 4, timestampDate.length() - 2);

            Cursor res = myDB.getEmotionDataDays(timestampDate);

            if (res.getCount() == 0) {
                entries.add(new ValueDataEntry(chartTime, 0));
            } else if (res.getCount() == 1) {
                res.moveToFirst();
                int value = Integer.parseInt(res.getString(col));
                entries.add(new ValueDataEntry(chartTime, value));
            }
        }

        return entries;
    }

    private void getData(int numDays) {
        Cursor emotionData = myDB.getEmotionData(numDays);

        int totalTime = 0;
        int totalUnlocks = 0;

        if (emotionData == null) {
            screenTimeText.setText("OnScreen Time: No data available");
            numUnlocksText.setText("Phone Unlocks: No data available");
        } else {
            Log.d(TAG, "getChartData: Getting data for " + numDays + " days");

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

            if (timeHrs > 0) {
                timeString = timeHrs + "hrs " + timeMin + "mins" + timeSec + "sec";
            } else if (timeMin > 0) {
                timeString = timeMin + "mins" + timeSec + "sec";
            } else if (timeSec > 0) {
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
