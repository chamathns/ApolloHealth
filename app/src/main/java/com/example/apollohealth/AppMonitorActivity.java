package com.example.apollohealth;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AppMonitorActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 0;
    public static final String LOG_TAG = "AM_ACTIVITY";

    private class UsageStat {
        private String packageName;
        private long usageTime;
        private String appName;
        private Drawable icon;

        private UsageStat(String packageName, long usageTime) {
            this.packageName = packageName;
            this.usageTime = usageTime;
        }

        public void setAppName(ApplicationInfo app, PackageManager packageManager) {
            this.appName = (String) packageManager.getApplicationLabel(app);
        }

        public void setIcon(ApplicationInfo app, PackageManager packageManager) {
            this.icon = packageManager.getApplicationIcon(app);
        }

        public String getPackageName() {
            return this.packageName;
        }

        public long getUsageTime() {
            return this.usageTime;
        }

        public String getAppName() {
            return this.appName;
        }

        public Drawable getIcon() {
            return this.icon;
        }

        @NonNull
        @Override
        public String toString() {
            return "Package: " + this.packageName + "\nApp: " + this.appName + "\nIcon: " + this.icon + "\nUsage Time (ms): " + this.usageTime;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_monitor);

        List<UsageStat> usageStats = getTopUsedApps(3, 5);

        for (UsageStat stat : usageStats) {
            Log.d(LOG_TAG, stat.toString());
            Log.d(LOG_TAG, "\n");
        }

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStat> getTopUsedApps(int numDays, int numApps) {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_ALLOWED) {
            Log.d(LOG_TAG, "MODE_ALLOWED");
        } else {
            Log.d(LOG_TAG, "MODE_NOT_ALLOWED");
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }

        long endMillis = System.currentTimeMillis();
        long startMillis = endMillis - numDays * 24 * 60 * 60 * 1000L;

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(startMillis, endMillis);

        ArrayList<UsageStat> usageStats = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        for (Map.Entry<String, UsageStats> entry : lUsageStatsMap.entrySet()) {
            String packageName = entry.getKey();
            long totalTimeUsageInMillis = lUsageStatsMap.get(packageName).getTotalTimeInForeground();

            UsageStat stat = new UsageStat(packageName, totalTimeUsageInMillis);

            try {
                ApplicationInfo app = this.getPackageManager().getApplicationInfo(stat.getPackageName(), 0);

                stat.setAppName(app, packageManager);
                stat.setIcon(app, packageManager);
            } catch (PackageManager.NameNotFoundException e) {
                Toast toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT);
                toast.show();
                e.printStackTrace();
            }

            usageStats.add(stat);
        }

        Collections.sort(usageStats, new Comparator<UsageStat>() {
            @Override
            public int compare(UsageStat us1, UsageStat us2) {
                return Long.compare(us2.getUsageTime(), us1.getUsageTime());
            }
        });

        return usageStats.subList(0, numApps);
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
