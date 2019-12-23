package com.example.apollohealth.screentimecounter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ScreenTimeService extends Service {
    public static final String LOG_TAG = "STC_SERVICE";

    private BroadcastReceiver mReceiver = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Screen time service started");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new ScreenTimeReceiver();
        registerReceiver(mReceiver, filter);

        Log.d(LOG_TAG, "ScreenTimeService onCreate: mReceiver is registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            Log.d(LOG_TAG, "ScreenTimeService onDestroy: mReceiver is unregistered.");
        }
        mReceiver = null;

        Log.d(LOG_TAG, "Screen time service stopped");
    }
}
