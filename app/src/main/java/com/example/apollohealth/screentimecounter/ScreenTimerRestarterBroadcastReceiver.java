package com.example.apollohealth.screentimecounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenTimerRestarterBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "ST_RESTARTER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Service Stopped");
        context.startService(new Intent(context, ScreenTimerService.class));
    }
}
