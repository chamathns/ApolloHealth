package com.example.apollohealth.unlockcounter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class LockerService extends Service {
    public static final String LOG_TAG = "UC_SERVICE";

    private BroadcastReceiver mReceiver = null;

    public LockerService(Context appContext) {
        super();
        Log.d(LOG_TAG, "Service constructor called");
    }

    public LockerService() {

    }

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
        Log.d(LOG_TAG, "Unlock counter service started");

        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new BroadReceiver();

        registerReceiver(mReceiver, filter);
        Log.d(LOG_TAG, "Service onCreate: mReceiver is registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "Unlock counter, on destroy");
        Intent broadcastIntent =new Intent(this,BroadReceiver.class);
        sendBroadcast(broadcastIntent);
    }
}
