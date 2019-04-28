package com.software.cb.rajneethi.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by w7 on 12/7/2017.
 */

public class SummaryAudioActivity extends Util implements LocationListener {


    private String TAG = "SummaryAudio";
    String boothName;

    @BindView(R.id.txtRecord)
    TextView txtRecord;

    @BindView(R.id.txtRecordTime)
    TextView txtRecordTime;

    @BindView(R.id.imgRecord)
    ImageView imgRecord;

    @BindView(R.id.imgRecordStop)
    ImageView imgRecordStop;

    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.imgDelete)
    ImageView imgDelete;

    @BindString(R.string.successfullyAdded)
    String successfullyAdded;

    @BindString(R.string.click)
    String recordMsg;
    @BindString(R.string.recording)
    String recording;
    @BindString(R.string.recordingStoppped)
    String recordingStopped;

    @BindString(R.string.recordingTime)
    String recordTime;

    @BindString(R.string.stopRecord)
    String stopRecord;
    @BindString(R.string.startRecord)
    String startRecord;

    CountDownTimer t;
    private String mFileName = null;
    private MediaRecorder mRecorder = null;

    String userType;

    private SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.txtStartStopRecord)
    TextView txtStartStopRecord;

    boolean isRecording = false;

    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_audio);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);


        gpsTracker = new GPSTracker(this);

        getLocation();

        if (getIntent().getExtras() != null) {
            boothName = getIntent().getExtras().getString("booth_name");
            userType = getIntent().getExtras().getString("userType");
        }
        makeDir();

        sharedPreferenceManager = new SharedPreferenceManager(this);
    }


    //start or stop record
    private void onRecord(boolean start) {
        Log.w(TAG, "Inside on record");
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void hideView(View v) {
        if (v.isShown()) {
            v.setVisibility(GONE);
        }
    }

    private void showView(View v) {
        if (!v.isShown()) {
            v.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.imgRecord)
    public void record() {

        Recording();
    }


    @OnClick(R.id.txtStartStopRecord)
    public void txtStartRecord() {
        if (!isRecording) {
            Recording();
        } else {
            onRecord(false);
        }
    }

    private void Recording() {
        if (checkPermissionGranted(Constants.RECORD_AUDIO, this)) {
            onRecord(true);
        } else {
            askPermission(new String[]{Constants.RECORD_AUDIO});
        }
    }

    @OnClick(R.id.imgRecordStop)
    public void stopRecord() {
        onRecord(false);
    }

    @OnClick(R.id.imgSave)
    public void save() {

        JSONObject survey_result = new JSONObject();

        try {
            String surveyId = DateFormat.format("yyyyMMdd_hhmmss a", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
            survey_result.put("surveyType", "Questionaire");

            survey_result.put("userType", userType);
            //added for new 4 attribute
            survey_result.put("surveyDate", new Date().toString());
            survey_result.put("projectId", sharedPreferenceManager.get_project_id());
            survey_result.put("partyWorker", sharedPreferenceManager.get_user_id());
            survey_result.put("pwname", sharedPreferenceManager.get_username());

            // survey_result.put("booth", sharedPreferenceManager.get_survey_booth());
            survey_result.put("audioFileName", mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));

            // Log.w(TAG, "Location latest " + "latitude : " + location.getLatitude() + " longitude " + location.getLongitude());

            try {
                survey_result.put("latitude", latitude);
                survey_result.put("longitude", longitude);
            } catch (Exception e) {
                survey_result.put("latitude", "Not Available");
                survey_result.put("longitude", "Not Available");
                e.printStackTrace();
            }
            survey_result.put("booth_name", boothName);
            survey_result.put("surveyid", surveyId);

            survey_result.put("respondantname", "Empty");
            survey_result.put("mobile", "Empty");
            survey_result.put("duplicate_survey", "no");
            survey_result.put("time_taken", txtRecordTime.getText().toString());
            MyDatabase db = new MyDatabase(SummaryAudioActivity.this);

            VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", survey_result.toString(), get_current_date(), false, surveyId);


            BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                    boothName, "Questionnaire", userType);

            db.insertBoothStats(stats);
            //VotersDatabase votersDatabase = new VotersDatabase(AddVoterActivity.this);
            db.insert_voter_attribute(attribute);
            //  votersDatabase.update_survey(attribute.getVoterCardNumber());

            Toastmsg(SummaryAudioActivity.this, successfullyAdded);
            SummaryAudioActivity.super.onBackPressed();
            SummaryAudioActivity.this.finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.w(TAG, "ojbject :" + survey_result.toString());

    /*    MyDatabase db = new MyDatabase(SummaryAudioActivity.this);

        BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                boothName, "Summary Audio", userType);
        db.insertBoothStats(stats);

        Toastmsg(this, successfullyAdded);
        SummaryAudioActivity.super.onBackPressed();
        SummaryAudioActivity.this.finish();*/
    }


    @OnClick(R.id.imgDelete)
    public void delete() {

        deleteAudioFile();
        hideView(bottomLayout);
        showView(imgRecord);

        //txtRecord.setText(recordMsg);
        hideView(txtRecord);
        txtRecordTime.setText(recordTime);
        txtStartStopRecord.setText(startRecord);
        showView(txtStartStopRecord);
    }

    //timer for record
    private void start_timer() {
        t = new CountDownTimer(Long.MAX_VALUE, 1000) {
            int cnt = 0;
            int min = 0;
            int hour = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                cnt++;
                Log.w(TAG, "Ontick count :" + cnt);

                String time = Integer.valueOf(cnt).toString();
                long millis = cnt;
                //  int seconds = (int) (millis / 60);
                //   int minutes = seconds / 60;

                if (cnt == 60) {
                    cnt = 0;
                    Log.w(TAG, "inside condition :" + cnt);
                    min++;
                }

                if (min == 60) {
                    min = 0;
                    hour++;
                }
                //  seconds = seconds % 60;


                txtRecordTime.setText(String.format("%02d:%02d:%02d", hour, min, millis));
            }

            @Override
            public void onFinish() {
            }
        };
    }


    /*delete audio file*/
    private void deleteAudioFile() {
        try {

            File newDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + "/compressimage");
            if (mFileName != null) {
                File file = new File(newDir, mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));

                Log.w(TAG, "File path : " + file.getAbsolutePath());

                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {

        if (isRecording) {
            alertForRecording();
            Log.w(TAG, "Recording is going");
        } else {
            super.onBackPressed();
            try {

                deleteAudioFile();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //start recording
    private void startRecording() {

        Log.w(TAG, "Start recording");
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/";
        mFileName += "AUD-QS_" + sharedPreferenceManager.get_username() + "_" + boothName + "_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        start_timer();

        try {
            mRecorder.prepare();
            t.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        try {
            isRecording = true;
            showView(txtRecord);
            txtRecord.setText(recording);
            mRecorder.start();
            txtStartStopRecord.setText(stopRecord);
            hideView(imgRecord);
            showView(imgRecordStop);

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

            isRecording = false;

            hideView(imgRecordStop);
            showView(bottomLayout);
            //  hideView(txtRecord);
            hideView(txtStartStopRecord);
            txtRecord.setText(recordingStopped);
            t.cancel();

        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "Ondestroy working");
        if (mRecorder != null) {
            onRecord(false);
        }
    }

    double latitude = 0, longitude = 0;
    android.location.Location location; // location
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 0 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 60000;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;

    public Location getLocation() {
        try {


            Log.w(TAG, "inside get location called ");
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                Log.w(TAG, "GPS Enabled");
                canGetLocation = true;
                isGPSEnabled = true;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) SummaryAudioActivity.this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        //if (latitude == 0L && longitude != 0L) {

                        //   getLocationLayout.setVisibility(GONE);


                        Log.w(TAG, " Latitude " + latitude + " Longitude " + longitude);
                    }
                }

            } else {
                Toastmsg(SummaryAudioActivity.this, "please Enable gps");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



    public void alertForRecording() {
        new AlertDialog.Builder(this)
                .setTitle("Recording")
                .setMessage("Please stop the recording before go to back.....")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
    }
}
