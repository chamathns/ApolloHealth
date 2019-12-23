package com.example.apollohealth.restarter;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import androidx.annotation.RequiresApi;

import com.example.apollohealth.ProcessMainClass;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = SensorRestarterBroadcastReceiver.class.getSimpleName();
    private static JobScheduler jobScheduler;
    private SensorRestarterBroadcastReceiver restartSensorServiceReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
//        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! ooooooooooooooooohhhhhhhhhweeeeeeeeeeeeee!!!!");
//        context.startService(new Intent(context, SensorService.class));
        Log.d("Broadcast message", "about to start timer" + context.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob(context);
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(context);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context
                    .getSystemService(JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(context,
                JobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                // setOverrideDeadline runs it immediately - you must have at least one constraint
                // https://stackoverflow.com/questions/51064731/firing-jobservice-without-constraints
                .setOverrideDeadline(0)
                .setPersisted(true).build();
        jobScheduler.schedule(jobInfo);
    }
}
