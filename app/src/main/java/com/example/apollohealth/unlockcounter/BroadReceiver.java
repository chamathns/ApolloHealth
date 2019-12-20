package com.example.apollohealth.unlockcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.apollohealth.DatabaseHandler;

public class BroadReceiver extends BroadcastReceiver {
    public static final String LOG_TAG = "UC_SCREENEVENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LockerService.class));

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d(LOG_TAG, "Screen locked");
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            DatabaseHandler myDB = new DatabaseHandler(context);
            myDB.updateHealthData(System.currentTimeMillis(), 0, 1, 0, 0, 0, 0);
            myDB.close();
            Log.d(LOG_TAG, "Screen unlocked");
        }
    }
}
