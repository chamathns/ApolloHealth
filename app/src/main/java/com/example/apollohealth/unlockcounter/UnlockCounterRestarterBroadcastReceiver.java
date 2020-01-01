package com.example.apollohealth.unlockcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UnlockCounterRestarterBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "UC_RESTARTER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Service Stopped");
        context.startService(new Intent(context, UnlockCounterService.class));
    }
}
