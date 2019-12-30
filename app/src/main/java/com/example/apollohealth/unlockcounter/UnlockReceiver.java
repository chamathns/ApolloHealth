package com.example.apollohealth.unlockcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.apollohealth.db.DatabaseHandler;

public class UnlockReceiver extends BroadcastReceiver {
    public static final String LOG_TAG = "UC_SCREENEVENT";
//    Context ctx;
//
//    public UnlockReceiver(Context appCtx) {
//        this.ctx = appCtx;
//    }
//
//    public UnlockReceiver() {
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "UnlockCounterService onReceive");

//        if (!isServiceRunning(UnlockCounterService.class)) {
//            context.startService(new Intent(context, UnlockCounterService.class));
//        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i(LOG_TAG, "Screen locked");
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            DatabaseHandler myDB = new DatabaseHandler(context);
            myDB.updateHealthData(System.currentTimeMillis(), 0, 1, 0, 0, 0, 0);
            myDB.close();
            Log.i(LOG_TAG, "Screen unlocked");
        }
    }

//    private boolean isServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
//
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Log.i(LOG_TAG, "ServiceRunning: TRUE");
//                return true;
//            }
//        }
//        Log.i(LOG_TAG, "ServiceRunning: FALSE");
//        return false;
//    }
}
