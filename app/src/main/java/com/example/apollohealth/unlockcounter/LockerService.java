package com.example.apollohealth.unlockcounter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class LockerService extends Service {
    public static final String LOG_TAG = "UC_SERVICE";

    private final BroadcastReceiver mReceiver = new BroadReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Unlock counter service started");

        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

//        BroadcastReceiver mReceiver = new BroadReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
        Log.d(LOG_TAG, "Unlock counter service stopped");
    }
}
