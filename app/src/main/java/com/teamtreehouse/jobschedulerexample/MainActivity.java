package com.teamtreehouse.jobschedulerexample;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simplymadeapps.quickperiodicjobscheduler.QuickPeriodicJobScheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int JOBID = 12;
    private JobInfo jobInfo;
    private ProgressBar progressBar;
    private TextView textViewPresentege;
    private ListView recordsListView;
    ArrayAdapter<String> adapter;

    public ArrayList<String> mRecordsTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuickPeriodicJobScheduler jobScheduler = new QuickPeriodicJobScheduler(this);
        jobScheduler.start(JOBID, 20 * 1000); // Run job with jobId=1 every 60 seconds
        //jobScheduler.stop(JOBID);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewPresentege = (TextView) findViewById(R.id.textViewPresentege);
        recordsListView = (ListView) findViewById(R.id.recordsListView);
        mRecordsTitles = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mRecordsTitles);
        recordsListView.setAdapter(adapter);


        findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJob();
            }
        });

        findViewById(R.id.buttonStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopJob();
            }
        });
        findViewById(R.id.buttonStart).setVisibility(View.GONE);
        findViewById(R.id.buttonStop).setVisibility(View.GONE);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("ServiceStatus"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("Status", 0);
            progressBar.setProgress(message);
            textViewPresentege.setText(message + "%");
            textViewPresentege.setTextColor(getResources().getColor(message > 50 ? android.R.color.black : android.R.color.white));
            Log.e("RAAM", "" + message);
            if (message == 0)
                setRecordOfJobs("START");

            if (message == 100)
                setRecordOfJobs("END   ");

        }
    };

    public void startJob() {
        /*if (isJobServiceOn(this)) {
            Toast.makeText(this, "Job is in running stata!!", Toast.LENGTH_SHORT).show();
            return;
        }

        ComponentName componentName = new ComponentName(this, MyJobService.class);
        jobInfo = new JobInfo.Builder(JOBID, componentName)
                //.setRequiresCharging(false)
                //.setRequiresDeviceIdle(false) // if this is "true" device should be idle
                .setPeriodic(15 * 1000) //10 sec
                //.setPersisted(true)
                //.setMinimumLatency(1 * 1000).setOverrideDeadline(3 * 1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Toast.makeText(this, "Job started!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Job not started!", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void stopJob() {
        if (!isJobServiceOn(this)) {
            Toast.makeText(this, "Job is not yet started!", Toast.LENGTH_SHORT).show();
            return;
        }

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOBID);
        int message = 0;
        progressBar.setProgress(message);
        textViewPresentege.setText(message + "%");
    }

    public static boolean isJobServiceOn(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;

        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JOBID) {
                hasBeenScheduled = true;
                break;
            }
        }

        return hasBeenScheduled;
    }

    private void setRecordOfJobs(String status) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        mRecordsTitles.add(status+" - > "+dateFormat.format(date));
        adapter.notifyDataSetChanged();
    }
}
