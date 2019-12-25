package com.example.apollohealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apollohealth.db.DatabaseHandler;
import com.example.apollohealth.restarter.SensorRestarterBroadcastReceiver;
import com.example.apollohealth.screentimecounter.ScreenTimeService;
import com.example.apollohealth.unlockcounter.LockerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.apollohealth.MetricGenerator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String LOG_TAG = "APOLLOHEALTH_MAIN";

    DatabaseHandler myDB;

    private Spinner timeSpinner;
    private TextView physicalTextView;
    private TextView emotionalTextView;
    private CircleImageView profileImage;
    private TextView flightText;
    private TextView caloriesText;
    private TextView stepsText;

    private MetricGenerator metrics;

    private String height = "0";
    private int duration;
//    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(MainActivity.this, LockerService.class));
        startService(new Intent(MainActivity.this, ScreenTimeService.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHandler(this);
        metrics = new MetricGenerator(193, 88);

        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        flightText = (TextView) findViewById(R.id.flightText);
        caloriesText = (TextView) findViewById(R.id.caloriesText);


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

        Cursor physicalData = myDB.getPhysicalData(duration);
        physicalData.moveToFirst();
        if (physicalData.moveToFirst()) {
            height = physicalData.getString(2);
        }

        flightText.setText(String.valueOf(height));

        caloriesText.setText(String.valueOf(metrics.caloriesBurned(0, Integer.parseInt(height))));
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

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        switch (pos){
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

        Cursor physicalData = myDB.getPhysicalData(duration);
        physicalData.moveToFirst();
        if (physicalData.moveToFirst()) {
            height = physicalData.getString(2);
        }

        flightText.setText(String.valueOf(height));

        caloriesText.setText(String.valueOf(metrics.caloriesBurned(0, Integer.parseInt(height))));

        physicalTextView.setText(parent.getItemAtPosition(pos).toString());
        emotionalTextView.setText(parent.getItemAtPosition(pos).toString());

        Toast.makeText(parent.getContext(),
                "Report Type : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
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
}
