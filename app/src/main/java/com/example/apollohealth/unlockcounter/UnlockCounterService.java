package com.example.apollohealth.unlockcounter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class UnlockCounterService extends Service {
    public static final String LOG_TAG = "UC_SERVICE";
    private BroadcastReceiver mReceiver = null;
//    Context ctx;

    public UnlockCounterService(Context appCtx) {
        super();
//        this.ctx = appCtx;
        Log.i(LOG_TAG, "Constructor called");
    }

    public UnlockCounterService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startUnlockCounter();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");

        Intent broadcastIntent = new Intent(this, UnlockReceiver.class);
        sendBroadcast(broadcastIntent);
        stopUnlockCounter();
    }

    public void startUnlockCounter() {
        Log.i(LOG_TAG, "Unlock counter service started");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

//        mReceiver = new UnlockReceiver(this.ctx);
        mReceiver = new UnlockReceiver();
        registerReceiver(mReceiver, filter);

        Log.i(LOG_TAG, "UnlockCounterService start: mReceiver is registered.");
    }

    public void stopUnlockCounter() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            Log.i(LOG_TAG, "UnlockCounterService stop: mReceiver is unregistered.");
        }
        mReceiver = null;

        Log.i(LOG_TAG, "Unlock counter service stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.i(LOG_TAG, "Unlock counter service started");
//
//        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//
//        mReceiver = new UnlockReceiver();
//        registerReceiver(mReceiver, filter);
//
//        Log.i(LOG_TAG, "UnlockCounterService onCreate: mReceiver is registered.");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
//            Log.i(LOG_TAG, "UnlockCounterService onDestroy: mReceiver is unregistered.");
//        }
//        mReceiver = null;
//
//        Log.i(LOG_TAG, "Unlock counter service stopped");
//    }

}
