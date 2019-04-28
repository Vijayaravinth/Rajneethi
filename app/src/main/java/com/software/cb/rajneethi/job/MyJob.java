package com.software.cb.rajneethi.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

/**
 * Created by DELL on 24-01-2018.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJob extends JobService {

    private String TAG = "My Job";

    private TransferObserver transferObserver;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.w(TAG, "Job crated");

        PersistableBundle args = jobParameters.getExtras();

       // Toast.makeText(getApplicationContext(), "Job started", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "url : " + args.getString("url"));
        new UploadDataAutomatically(getApplicationContext()).execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.w(TAG, "Job finished̥");
        return true;
    }
}
