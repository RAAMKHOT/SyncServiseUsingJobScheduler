package com.teamtreehouse.jobschedulerexample;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;

    // Called by the Android system when it's time to run the job
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e(TAG, "Job started!");

        isWorking = true;
        // We need 'jobParameters' so we can call 'jobFinished'
        startWorkOnNewThread(jobParameters); // Services do NOT run on a separate thread

        return isWorking;
    }

    // Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.e(TAG, "Job cancelled before being completed.");
        Toast.makeText(this, "Job Stoped!", Toast.LENGTH_SHORT).show();
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }



    private void startWorkOnNewThread(final JobParameters jobParameters) {
        new Thread(new Runnable() {
            public void run() {
                doWork(jobParameters);
            }
        }).start();
    }

    private void doWork(JobParameters jobParameters) {
        // 10 seconds of 'working' (1000*10ms)
        for (int i = 0; i <= 100; i++) {
            // If the job has been cancelled, stop working; the job will be rescheduled.
            if (jobCancelled)
                return;

            Log.e("Status", i + "%");
            sendMessageToActivity(this, i);
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
        }

        Log.e(TAG, "Job finished!");
        isWorking = false;
        boolean needsReschedule = true;
        jobFinished(jobParameters, needsReschedule);
    }

    private static void sendMessageToActivity(Context context, int status) {
        Intent intent = new Intent("ServiceStatus");
        // You can also include some extra data.
        intent.putExtra("Status", status);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
