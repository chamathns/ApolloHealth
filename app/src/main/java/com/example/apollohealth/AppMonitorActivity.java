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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AppMonitorActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 0;
    public static final String TAG = "AM_ACTIVITY";
    public static final String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    public static final int NUM_APPS = 10;

    LinearLayout container;
    AnyChartView anyChartView;
    Spinner timePeriodSpinner;

    Pie pie;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_monitor);


        anyChartView = findViewById(R.id.app_monitor_chart);
        pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
//        dummy data
        data.add(new ValueDataEntry("a", 100));
        data.add(new ValueDataEntry("b", 100));
        pie.data(data);
        pie.labels().position("outside");
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
        anyChartView.setChart(pie);

        container = findViewById(R.id.container);
        try {
            loadData(container, 1, NUM_APPS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timePeriodSpinner = findViewById(R.id.durationSpinner);
//        List<String> timePeriods = new ArrayList<String>();
//        timePeriods.add("1-day Report");
//        timePeriods.add("3-day Report");
//        timePeriods.add("Weekly Report");
//        timePeriods.add("Monthly Report");
//        timePeriods.add("Yearly Report");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, timePeriods);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        timePeriodSpinner.setAdapter(dataAdapter);
        timePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    try {
                        Log.d(TAG, "onItemSelected: Loading data for 1 day");
                        loadData(container, 1, NUM_APPS);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (i == 1) {
                    try {
                        Log.d(TAG, "onItemSelected: Loading data for 3 days");
                        loadData(container, 3, NUM_APPS);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (i == 2) {
                    try {
                        Log.d(TAG, "onItemSelected: Loading data for 7 days");
                        loadData(container, 7, NUM_APPS);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (i == 3) {
                    try {
                        Log.d(TAG, "onItemSelected: Loading data for 30 days");
                        loadData(container, 30, NUM_APPS);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (i == 4) {
                    try {
                        Log.d(TAG, "onItemSelected: Loading data for 365 days");
                        loadData(container, 365, NUM_APPS);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "Invalid selection");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addBottomNavigation();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStat> getTopUsedApps(int numDays, int numApps) {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_ALLOWED) {
            Log.i(TAG, "MODE_ALLOWED");
        } else {
            Log.i(TAG, "MODE_NOT_ALLOWED");
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

            if (!((totalTimeUsageInMillis / 1000) > 0)) {
                continue;
            }

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

        if (usageStats.size() <= numApps) {
            return usageStats;
        }

        return usageStats.subList(0, numApps);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadData(LinearLayout container, int numDays, int numApps) throws ExecutionException, InterruptedException {
        container.removeAllViews();

        List<UsageStat> usageStats = getTopUsedApps(numDays, numApps);
        createUIApps(container, usageStats);
    }

    private void createUIApps(LinearLayout container, List<UsageStat> appUsageList) throws ExecutionException, InterruptedException {
        HashMap<String, Long> categoryTimes = new HashMap<String, Long>();

        for (UsageStat stat : appUsageList) {
            stat.setCategory();

            if (categoryTimes.containsKey(stat.getCategory())) {
                categoryTimes.put(stat.getCategory(), categoryTimes.get(stat.getCategory()) + stat.getUsageTime());
            } else {
                categoryTimes.put(stat.getCategory(), stat.getUsageTime());
            }

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
            LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(100, 100);
            imgViewParams.setMargins(35, 35, 35, 35);
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

        View separator = new View(this);
        separator.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                5
        ));
        separator.setBackgroundColor(Color.parseColor("#C0C0C0"));
        container.addView(separator);

        List<DataEntry> data = new ArrayList<>();
        for (String name : categoryTimes.keySet()) {
            data.add(new ValueDataEntry(name, categoryTimes.get(name)));
        }
        pie.data(data);

////        radar chart
//        Radar radar = AnyChart.radar();
//        radar.yScale().minimum(0d);
//        radar.yScale().minimumGap(0d);
//        radar.yScale().ticks().interval(50d);
//        radar.xAxis().labels().padding(5d, 5d, 5d, 5d);
//        radar.legend()
//                .align(Align.CENTER)
//                .enabled(true);
//        List<DataEntry> data = new ArrayList<>();
//        for (String name : categoryTimes.keySet()) {
//            data.add(new ValueDataEntry(name, categoryTimes.get(name)/1000));
//        }
//        Set set = Set.instantiate();
//        set.data(data);
//        Mapping appUsageData = set.mapAs("{ x: 'x', value: 'value' }");
//        Line appUsageLine = radar.line(appUsageData);
//        appUsageLine.name("App Usage Time");
//        appUsageLine.markers()
//                .enabled(true)
//                .type(MarkerType.CIRCLE)
//                .size(3d);
//        radar.tooltip().format("Value: {%Value}");
//        AnyChartView anyChartView = findViewById(R.id.app_monitor_chart);
//        anyChartView.setChart(radar);
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

    private static class FetchCategoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... packageName) {
            return getCategory(packageName[0]);
        }

        private String getCategory(String packageName) {
            String queryUrl = GOOGLE_URL + packageName;

            try {
                Document doc = Jsoup.connect(queryUrl).get();
//                Elements link = doc.select("a[class=\"hrTbp R8zArc\"]");
                Elements link = doc.select("a[itemprop=\"genre\"]");
                return link.text();
            } catch (HttpStatusException e) {
                return "Other";
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return e.toString();
            }
        }
    }

    private class UsageStat {
        private String packageName;
        private long usageTime;
        private String appName;
        private Drawable icon;
        private String category;

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

        public void setCategory() throws ExecutionException, InterruptedException {
            this.category = new FetchCategoryTask().execute(this.packageName).get();
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

        public String getCategory() {
            return category;
        }

        @NonNull
        @Override
        public String toString() {
            return "\nPackage: " + this.packageName + "\nApp: " + this.appName + "\nIcon: " + this.icon + "\nUsage Time: " + this.getUsageTimeString();
        }
    }
}
