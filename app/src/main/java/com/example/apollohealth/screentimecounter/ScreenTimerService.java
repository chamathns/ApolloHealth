package com.example.apollohealth.screentimecounter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ScreenTimerService extends Service {
    public static final String LOG_TAG = "ST_SERVICE";
    private BroadcastReceiver mReceiver = null;
    Context ctx;

    public ScreenTimerService(Context appCtx) {
        super();
        this.ctx = appCtx;
        Log.i(LOG_TAG, "Constructor called");
    }

    public ScreenTimerService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startScreenTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");

        Intent broadcastIntent = new Intent(this, ScreenTimerRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stopScreenTimer();
    }

    public void startScreenTimer() {
        Log.i(LOG_TAG, "Screen timer service started");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new ScreenTimeReceiver();
        registerReceiver(mReceiver, filter);

        Log.i(LOG_TAG, "ScreenTimerService start: mReceiver is registered.");
    }

    public void stopScreenTimer() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            Log.i(LOG_TAG, "ScreenTimerService stop: mReceiver is unregistered.");
        }
        mReceiver = null;

        Log.i(LOG_TAG, "Screen timer service stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
