package com.example.apollohealth;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class AppMonitorActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 0;

    private class UsageStat {
        private String packageName;
        private long usageTime;

        private UsageStat(String packageName, long usageTime) {
            this.packageName = packageName;
            this.usageTime = usageTime;
        }

        public String getPackageName() {
            return this.packageName;
        }

        public long getUsageTime() {
            return this.usageTime;
        }

        @NonNull
        @Override
        public String toString() {
            return this.packageName + ": " + this.usageTime;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_monitor);

        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());

        Log.d("MAIN_TAG", "------------------------------------");

        if (mode == AppOpsManager.MODE_ALLOWED) {
            Log.d("MAIN_TAG", "MODE_ALLOWED");
        } else {
            Log.d("MAIN_TAG", "MODE_NOT_ALLOWED");
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }

        Log.d("MAIN_TAG", "------------------------------------");

        long endMillis = System.currentTimeMillis();
        long startMillis = endMillis - 24 * 60 * 60 * 1000L;

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(startMillis, endMillis);

        ArrayList<UsageStat> usageStats = new ArrayList<>();

        Log.d("MAIN_TAG", String.valueOf(lUsageStatsMap.size()));

        Log.d("MAIN_TAG", "------------------------------------");

        for (Map.Entry<String, UsageStats> entry : lUsageStatsMap.entrySet()) {
            String packageName = entry.getKey();
            long totalTimeUsageInMillis = lUsageStatsMap.get(packageName).getTotalTimeInForeground();

            usageStats.add(new UsageStat(packageName, totalTimeUsageInMillis));
        }

        Collections.sort(usageStats, new Comparator<UsageStat>() {
            @Override
            public int compare(UsageStat us1, UsageStat us2) {
                return Long.compare(us2.getUsageTime(), us1.getUsageTime());
            }
        });

        for (UsageStat usageStat:usageStats){
            Log.d("MAIN_TAG", usageStat.toString());
        }

        Log.d("MAIN_TAG", "------------------------------------");



        addBottomNavigation();



//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
    }

    public void addBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final int result = 1;
                switch (menuItem.getItemId()) {

                    case R.id.action_profile:
                        Intent profileIntent = new Intent(AppMonitorActivity.this, MainActivity.class);
                        startActivityForResult(profileIntent, result);
                        break;

                    case R.id.action_health:
                        Intent healthIntent = new Intent(AppMonitorActivity.this, HealthActivity.class);
                        startActivityForResult(healthIntent, result);
                        break;

                    case R.id.action_journal:
                        Intent journalIntent = new Intent(AppMonitorActivity.this, JournalActivity.class);
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
