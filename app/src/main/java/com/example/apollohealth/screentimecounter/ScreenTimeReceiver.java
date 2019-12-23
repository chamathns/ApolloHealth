package com.example.apollohealth.screentimecounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.apollohealth.DatabaseHandler;

public class ScreenTimeReceiver extends BroadcastReceiver {
    private long startTime = System.currentTimeMillis();
    private long endTime;
    private int screenOnTime;
    public static final String LOG_TAG = "STC_SERVICE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "ScreenTimeService onReceive");

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startTime = System.currentTimeMillis();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            endTime = System.currentTimeMillis();
            screenOnTime = Integer.parseInt(String.valueOf((endTime - startTime)/1000));
            Log.d(LOG_TAG, "Screen on time: " + screenOnTime + " seconds");

            DatabaseHandler myDB = new DatabaseHandler(context);
            myDB.updateHealthData(System.currentTimeMillis(), screenOnTime, 0, 0, 0, 0, 0);
            myDB.close();
        }
    }
}
