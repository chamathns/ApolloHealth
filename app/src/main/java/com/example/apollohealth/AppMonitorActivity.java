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
import android.widget.ImageView;
import android.widget.TextView;
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

    ImageView app1Icon;
    ImageView app2Icon;
    ImageView app3Icon;
    ImageView app4Icon;
    ImageView app5Icon;
    ImageView app6Icon;
    ImageView app7Icon;
    ImageView app8Icon;
    ImageView app9Icon;
    ImageView app10Icon;

    TextView app1Name;
    TextView app2Name;
    TextView app3Name;
    TextView app4Name;
    TextView app5Name;
    TextView app6Name;
    TextView app7Name;
    TextView app8Name;
    TextView app9Name;
    TextView app10Name;

    TextView app1Usage;
    TextView app2Usage;
    TextView app3Usage;
    TextView app4Usage;
    TextView app5Usage;
    TextView app6Usage;
    TextView app7Usage;
    TextView app8Usage;
    TextView app9Usage;
    TextView app10Usage;

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

        public String getUsageTimeString() {
            int seconds = (int) this.usageTime / 1000;

            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            seconds = (seconds % 3600) % 60;

            if (hours > 0) {
                return hours + "hrs " + minutes + "mins " + seconds + "seconds";
            }

            if (minutes > 0) {
                return minutes + "mins " + seconds + "seconds";
            }

            if (seconds > 0) {
                return seconds + "seconds";
            }

            return "No usage";
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
            return "\nPackage: " + this.packageName + "\nApp: " + this.appName + "\nIcon: " + this.icon + "\nUsage Time: " + this.getUsageTimeString();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_monitor);

        app1Icon = findViewById(R.id.app1Icon);
        app1Name = findViewById(R.id.app1Name);
        app1Usage = findViewById(R.id.app1Usage);

        app2Icon = findViewById(R.id.app2Icon);
        app2Name = findViewById(R.id.app2Name);
        app2Usage = findViewById(R.id.app2Usage);

        app3Icon = findViewById(R.id.app3Icon);
        app3Name = findViewById(R.id.app3Name);
        app3Usage = findViewById(R.id.app3Usage);

        app4Icon = findViewById(R.id.app4Icon);
        app4Name = findViewById(R.id.app4Name);
        app4Usage = findViewById(R.id.app4Usage);

        app5Icon = findViewById(R.id.app5Icon);
        app5Name = findViewById(R.id.app5Name);
        app5Usage = findViewById(R.id.app5Usage);

        app6Icon = findViewById(R.id.app6Icon);
        app6Name = findViewById(R.id.app6Name);
        app6Usage = findViewById(R.id.app6Usage);

        app7Icon = findViewById(R.id.app7Icon);
        app7Name = findViewById(R.id.app7Name);
        app7Usage = findViewById(R.id.app7Usage);

        app8Icon = findViewById(R.id.app8Icon);
        app8Name = findViewById(R.id.app8Name);
        app8Usage = findViewById(R.id.app8Usage);

        app9Icon = findViewById(R.id.app9Icon);
        app9Name = findViewById(R.id.app9Name);
        app9Usage = findViewById(R.id.app9Usage);

        app10Icon = findViewById(R.id.app10Icon);
        app10Name = findViewById(R.id.app10Name);
        app10Usage = findViewById(R.id.app10Usage);

        List<UsageStat> usageStats = getTopUsedApps(3, 10);

        app1Icon.setImageDrawable(usageStats.get(0).getIcon());
        app1Name.setText(usageStats.get(0).getAppName());
        app1Usage.setText(usageStats.get(0).getUsageTimeString());

        app2Icon.setImageDrawable(usageStats.get(1).getIcon());
        app2Name.setText(usageStats.get(1).getAppName());
        app2Usage.setText(usageStats.get(1).getUsageTimeString());

        app3Icon.setImageDrawable(usageStats.get(2).getIcon());
        app3Name.setText(usageStats.get(2).getAppName());
        app3Usage.setText(usageStats.get(2).getUsageTimeString());

        app4Icon.setImageDrawable(usageStats.get(3).getIcon());
        app4Name.setText(usageStats.get(3).getAppName());
        app4Usage.setText(usageStats.get(3).getUsageTimeString());

        app5Icon.setImageDrawable(usageStats.get(4).getIcon());
        app5Name.setText(usageStats.get(4).getAppName());
        app5Usage.setText(usageStats.get(4).getUsageTimeString());

        app6Icon.setImageDrawable(usageStats.get(5).getIcon());
        app6Name.setText(usageStats.get(5).getAppName());
        app6Usage.setText(usageStats.get(5).getUsageTimeString());

        app7Icon.setImageDrawable(usageStats.get(6).getIcon());
        app7Name.setText(usageStats.get(6).getAppName());
        app7Usage.setText(usageStats.get(6).getUsageTimeString());

        app8Icon.setImageDrawable(usageStats.get(7).getIcon());
        app8Name.setText(usageStats.get(7).getAppName());
        app8Usage.setText(usageStats.get(7).getUsageTimeString());

        app9Icon.setImageDrawable(usageStats.get(8).getIcon());
        app9Name.setText(usageStats.get(8).getAppName());
        app9Usage.setText(usageStats.get(8).getUsageTimeString());

        app10Icon.setImageDrawable(usageStats.get(9).getIcon());
        app10Name.setText(usageStats.get(9).getAppName());
        app10Usage.setText(usageStats.get(9).getUsageTimeString());

//        for (UsageStat stat : usageStats) {
//            Log.v(LOG_TAG, stat.toString());
//            Log.v(LOG_TAG, "\n");
//
//            app1Icon.setImageDrawable(stat.getIcon());
//        }

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
            Log.i(LOG_TAG, "MODE_ALLOWED");
        } else {
            Log.i(LOG_TAG, "MODE_NOT_ALLOWED");
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
