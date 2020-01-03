package com.example.apollohealth.unlockcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.apollohealth.db.DatabaseHandler;

public class UnlockReceiver extends BroadcastReceiver {
    public static final String TAG = "UC_SCREENEVENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "UnlockCounterService onReceive");

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i(TAG, "Screen locked");
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            DatabaseHandler myDB = new DatabaseHandler(context);
            myDB.updateHealthData(System.currentTimeMillis(), 0, 1, 0, 0, 0, 0);
            myDB.close();
            Log.i(TAG, "Screen unlocked");
        }
    }
}
