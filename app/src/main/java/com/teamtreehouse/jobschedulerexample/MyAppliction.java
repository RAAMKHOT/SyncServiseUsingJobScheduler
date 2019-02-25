package com.teamtreehouse.jobschedulerexample;

import android.app.Application;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.simplymadeapps.quickperiodicjobscheduler.PeriodicJob;
import com.simplymadeapps.quickperiodicjobscheduler.QuickJobFinishedCallback;
import com.simplymadeapps.quickperiodicjobscheduler.QuickPeriodicJob;
import com.simplymadeapps.quickperiodicjobscheduler.QuickPeriodicJobCollection;

//TODO : reference Link : https://github.com/simplymadeapps/QuickPeriodicJobScheduler
public class MyAppliction extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initJobs();
    }

    public void initJobs() {
        int jobId = 12;
        QuickPeriodicJob job = new QuickPeriodicJob(jobId, new PeriodicJob() {
            @Override
            public void execute(QuickJobFinishedCallback callback) {
                Toast.makeText(MyAppliction.this, "Jos start!", Toast.LENGTH_SHORT).show();
                startWorkOnNewThread();

                // When you have done all your work in the job, call jobFinished to release the resources
                callback.jobFinished();
            }
        });

        QuickPeriodicJobCollection.addJob(job);
    }


    private void startWorkOnNewThread() {
        new Thread(new Runnable() {
            public void run() {
                doWork();
            }
        }).start();
    }

    private void doWork() {
        // 10 seconds of 'working' (1000*10ms)
        for (int i = 0; i <= 100; i++) {
            // If the job has been cancelled, stop working; the job will be rescheduled.

            Log.e("Status", i + "%");
            sendMessageToActivity(this, i);
            try {
                Thread.sleep(150);
            } catch (Exception e) {
            }
        }

    }

    private static void sendMessageToActivity(Context context, int status) {
        Intent intent = new Intent("ServiceStatus");
        // You can also include some extra data.
        intent.putExtra("Status", status);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}
