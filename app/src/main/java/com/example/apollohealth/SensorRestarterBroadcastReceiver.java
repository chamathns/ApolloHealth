package com.example.apollohealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! ooooooooooooooooohhhhhhhhhweeeeeeeeeeeeee!!!!");
        context.startService(new Intent(context, SensorService.class));;
    }
}
