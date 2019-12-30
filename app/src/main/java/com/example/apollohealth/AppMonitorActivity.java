package com.example.apollohealth;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    LinearLayout container;

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

        container = findViewById(R.id.container);
        List<UsageStat> usageStats = getTopUsedApps(3, 10);
        createUIApps(container, usageStats);

        addBottomNavigation();
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

    public void createUIApps(LinearLayout container, List<UsageStat> appUsageList) {
        for (UsageStat stat : appUsageList) {
            View separator = new View(this);
            separator.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    5
            ));
            separator.setBackgroundColor(Color.parseColor("#C0C0C0"));
            container.addView(separator);

            TextView nameText = new TextView(this);
            nameText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            nameText.setGravity(Gravity.START);
            nameText.setText(stat.getAppName());
            nameText.setTextColor(Color.parseColor("#000000"));
            nameText.setTextSize(25);

            TextView usageText = new TextView(this);
            usageText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            usageText.setGravity(Gravity.START);
            usageText.setText(stat.getUsageTimeString());
            usageText.setTextColor(Color.parseColor("#555555"));
            usageText.setTextSize(20);

            LinearLayout vLayout = new LinearLayout(this);
            vLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams vLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            vLayoutParams.setMargins(15, 15, 15, 15);
            vLayout.setLayoutParams(vLayoutParams);

            vLayout.addView(nameText);
            vLayout.addView(usageText);

            ImageView appIcon = new ImageView(this);
            LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(120, 120);
            imgViewParams.setMargins(25, 25, 25, 25);
            appIcon.setLayoutParams(imgViewParams);
            appIcon.setImageDrawable(stat.getIcon());

            LinearLayout hLayout = new LinearLayout(this);
            hLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams hLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            hLayout.setLayoutParams(hLayoutParams);

            hLayout.addView(appIcon);
            hLayout.addView(vLayout);

            container.addView(hLayout);
        }

        View end = new View(this);
        end.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
        ));
        end.setBackgroundColor(Color.parseColor("#FFFFFF"));
        container.addView(end);
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
