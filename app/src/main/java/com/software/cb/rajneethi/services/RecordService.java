package com.software.cb.rajneethi.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

/**
 * Created by DELL on 06-01-2018.
 */

public class RecordService extends Service {

    public String mFileName = null;
    private MediaRecorder mRecorder = null;
    private String TAG = "Record service";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "on create work");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "on  start work");

        if (intent != null) {
            if (intent.getStringExtra("fileName") != null) {
                mFileName = intent.getStringExtra("fileName");
                Log.w(TAG, "file name : " + mFileName);
                onRecord(true);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

   /* @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG, "on un bind work");
        return super.onUnbind(intent);
    }*/

    //start or stop record
    public void onRecord(boolean start) {
        Log.w(TAG, "Inside on record");
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    //start recording
    private void startRecording() {
        Log.w(TAG, "Start recording");
        //  mFileName += "AUD-" + sharedPreferenceManager.get_username() + "_" + boothName + "_" + votercardnumber + "_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        try {
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //stop recording
    private void stopRecording() {

        Log.w(TAG, "Stop recording method");
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        Log.w(TAG, "on destroy work file name :" + mFileName);
        onRecord(false);
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "on bind work");
        return null;
    }
}
