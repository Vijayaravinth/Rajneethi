package com.software.cb.rajneethi.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.OpinionPollAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.ExportDatabase;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.MainMenuDetails;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.SurveyDetails;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.moviesurvey.TheatrePopleOpinionActivity1;
import com.software.cb.rajneethi.moviesurvey.TheatreProfilingAdministrationActivity;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.S3Utils;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static android.view.View.GONE;

/**
 * Created by MVIJAYAR on 07-07-2017.
 */

public class OpinionPollActivity extends UtilActivity implements checkInternet.ConnectivityReceiverListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.booth_spinner)
    Spinner spinner;

    @BindView(R.id.opinionpoll_recyclerview)
    RecyclerView recyclerView;

    String booth_name = "";

    ArrayList<String> booth_list = new ArrayList<>();
    ArrayList<String> title_list = new ArrayList<>();

    private String TAG = "opinion poll";
    SharedPreferenceManager sharedPreferenceManager;
    private GPSTracker gpsTracker;

    @BindString(R.string.opinionPoll)
    String opinionPoll;
    private IOSDialog dialog;

    @BindString(R.string.noInternet)
    String noInternet;

    private MyDatabase db;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.progresslayout)
    FrameLayout progressLayout;


    @BindView(R.id.txtUploadData)
    TextView txtUploadData;

    /* @BindView(R.id.txt_no_of_survey)
     TextView txt_no_of_survey;
 */
    private boolean isUpoadingData = false;
    private int file_count = 0;

    private TransferObserver transferObserver;

    private static final String S3_BUCKETNAME = "rn-assets";
    private static final String S3_CONSTITUENCIES_FOLDER = "constituencies";
    private String S3_RICHASSETS_FOLDER = "richassets";
    private static final String S3_CONSTITUENCY_DESTINATION_FOLDER = "offline/data/";
    private static volatile S3Utils instance;
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;
    private TransferUtility transferUtility;

    String file_name;

    ExportDatabase ex_db;

    int[] image_ids = new int[]{R.drawable.quetionair, R.drawable.caste_estimate, R.drawable.caste_estimation, R.drawable.influentil_pepole, R.drawable.issues, R.drawable.summary, R.drawable.sync_online2, R.drawable.camera};


    private MediaPlayer mp;

    String dir;
    File newDir;


    private ArrayList<SurveyDetails> surveyDetailsList = new ArrayList<>();


    private NotificationManager mNotifyManager;
    private android.support.v4.app.NotificationCompat.Builder build;
    int id = 1;

    @BindString(R.string.upload)
    String upload;

    @BindString(R.string.uploadInProgress)
    String uploadInProgress;

    @BindString(R.string.uploadComplete)
    String uploadComplete;

    ArrayList<MainMenuDetails> list = new ArrayList<>();

    String userType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion_poll);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, opinionPoll);

        if (!is_enabled_gps(OpinionPollActivity.this)) {
            mEnableGps();
        }

        dialog = show_dialog(OpinionPollActivity.this, false);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        gpsTracker = new GPSTracker(OpinionPollActivity.this);

        db = new MyDatabase(OpinionPollActivity.this);

        userType = sharedPreferenceManager.getUserType();

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/";
        File foldir = new File(dir);
        if (!foldir.exists()) {
            foldir.mkdirs();
        }

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

     /*  final FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        config.setConfigSettings(configSettings);
        // config.setDefaults(R.xml.defaults);
        config.fetch(0).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.w(TAG, "firebase success");
                    config.activateFetched();
                } else {
                    Log.w(TAG, "firebase failed");
                }


                String playStoreVersionCode = FirebaseRemoteConfig.getInstance().getString("app_version_code");
                Log.w(TAG, "play store version : " + playStoreVersionCode);
            }
        });*/
        //

        String code = FirebaseRemoteConfig.getInstance().getString("app_version_code");
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentAppVersionCode = pInfo.versionName;

        Log.w(TAG, "PLay store version : " + code + " curren version " + currentAppVersionCode);


        file_name = sharedPreferenceManager.get_user_id() + "_" + sharedPreferenceManager.get_constituency_id() + "_" + get_current_date() + "_" + get_current_time() + ".db";
        file_name = file_name.replaceAll(" ", "_");

        ex_db = new ExportDatabase(this, file_name);


        add_values_to_list();

        set_grid_layout_manager(recyclerView, this, 3);
        set_adapter();


        Log.w(TAG, "date and time " + DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id());

        if (!userType.equals("TSP")) {
            if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {

                if (checkInternet.isConnected()) {
                    dialog.show();
                    get_booth_details();
                } else {

                    try {
                        Cursor c = db.getBooths();
                        Log.w(TAG, "count : " + c.getCount());
                        if (c.getCount() > 0) {
                            if (c.moveToFirst()) {
                                do {
                                    booth_list.add(c.getString(0));
                                } while (c.moveToNext());
                            }
                            c.close();
                            loadDataToSpinner();
                        } else {
                            loadDefaultBooth();
                        }
                    } catch (CursorIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        loadDefaultBooth();
                    }
                    Toastmsg(OpinionPollActivity.this, noInternet);
                    //  finish_activity();
                    // loadDefaultBooth();
                }

            } else {
                try {
                    Cursor c = db.getBooths();
                    Log.w(TAG, "count : " + c.getCount());
                    if (c.getCount() > 0) {
                        if (c.moveToFirst()) {
                            do {
                                booth_list.add(c.getString(0));
                            } while (c.moveToNext());
                        }
                        c.close();
                        loadDataToSpinner();
                    } else {
                        if (checkInternet.isConnected()) {
                            dialog.show();
                            get_booth_details();
                        } else {
                            loadDefaultBooth();
                            Toastmsg(OpinionPollActivity.this, noInternet);
                            // finish_activity();
                        }
                    }
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    if (checkInternet.isConnected()) {
                        dialog.show();
                        get_booth_details();
                    } else {
                        loadDefaultBooth();
                        Toastmsg(OpinionPollActivity.this, noInternet);
                        //finish_activity();
                    }
                }

            }

        }


        if (userType.equals("TSP")) {
            spinner.setVisibility(GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }


        JSONArray notificationArray = new JSONArray();

        try {
            Cursor c = db.getNotification();
            if (c.moveToFirst()) {

                do {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("surveyid", c.getString(0));
                        notificationArray.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } while (c.moveToNext());


                if (notificationArray.length() > 0) {

                    if (checkInternet.isConnected()) {
                        new sendNotification().execute(notificationArray.toString());
                    }
                }
            }
            Log.w(TAG, "Notification count : " + c.getCount());
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    private class sendNotification extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... voids) {

            final String surveyid = voids[0];

            Log.w(TAG, "Survey id :" + surveyid);


            StringRequest request = new StringRequest(Request.Method.POST, "http://bloodambulance.com/bloodapp/php/sent_notification.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.w(TAG, "Response :" + response);

                    db.deleteNotification();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", sharedPreferenceManager.get_username());
                    map.put("id", sharedPreferenceManager.get_user_id());
                    map.put("surveyid", surveyid);
                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(OpinionPollActivity.this);
            queue.add(request);
            queue.getCache().clear();

            return null;
        }


    }


    private void loadDefaultBooth() {
        //  booth_list.add(selectBooth);
        booth_list.add("Default");
        loadDataToSpinner();
    }

    private void createNotification() {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        build = (android.support.v4.app.NotificationCompat.Builder) new android.support.v4.app.NotificationCompat.Builder(this, "MyChannelId")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(upload)
                .setContentText(uploadInProgress);

        build.setProgress(100, 0, false);
        mNotifyManager.notify(id, build.build());

    }

    private void updateNotification(int percentage) {

        Log.w(TAG, " notification percentage : " + percentage);

        build.setProgress(100, percentage, false);
        mNotifyManager.notify(id, build.build());
    }

    private void completeNotification(String msg) {
        build.setContentText(msg);
        // Removes the progress bar
        build.setProgress(0, 0, false);
        mNotifyManager.notify(id, build.build());
    }

    @BindView(R.id.txtUploadFinish)
    TextView txtUploadFinish;


    @BindString(R.string.gettingFiles)
    String gettingFiles;

    @BindString(R.string.noRecordFound)
    String noRecordFound;

    private JSONArray get_upload_data() {
        JSONArray payload = new JSONArray();
        try {

            Cursor c = db.get_voter_atrributes();
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        try {
                            JSONObject object = getJSONForBulkUpload(c.getString(1), c.getString(2), c.getString(3));
                            Log.w(TAG, "csa 590 " + object);
                            addDataToSurveyList(c.getString(3));
                            payload.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while (c.moveToNext());
                }
            }
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        Log.w(TAG, "csa 601 " + payload);
        return payload;
    }

    private void addDataToSurveyList(String data) {
        Log.w(TAG, "data : " + data);
        try {


            JSONObject object = new JSONObject(data);

            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(object.getString("surveyDate")));
            Log.w(TAG, "date  :" + date);

            Log.w(TAG, "pwname : " + object.getString("pwname") + "audioFileName : " + object.getString("audioFileName"));
            surveyDetailsList.add(new SurveyDetails(object.getString("surveyid"),
                    object.getString("latitude"), object.getString("longitude"),
                    object.getString("audioFileName"), date,
                    object.getString("partyWorker"), object.getString("userType"),
                    object.getString("projectId"), object.getString("booth_name"), object.getString("surveyType"), object.getString("respondantname"), object.getString("mobile"), object.getString("pwname")));

        } catch (JSONException e) {
            e.printStackTrace();

            JSONObject object = null;
            try {
                object = new JSONObject(data);

                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(object.getString("surveyDate")));
                Log.w(TAG, "date  :" + date);
                surveyDetailsList.add(new SurveyDetails(object.getString("surveyid"),
                        object.getString("latitude"), object.getString("longitude"),
                        "empty", date,
                        object.getString("partyWorker"), object.getString("userType"),
                        object.getString("projectId"), object.getString("booth_name"), "survey", "empty", "empty", sharedPreferenceManager.get_username()));

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }
    }

    private int surveyCount = 0;


    private void success(final String surveyId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                surveyCount++;
                db.update_attribute(surveyId);

                if (surveyCount <= surveyDetailsList.size() - 1) {
                    updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                    uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                }

                if (surveyCount == surveyDetailsList.size()) {
                    completeNotification(uploadComplete);
                    dialog.dismiss();
                    isUpoadingData = false;
                    Toastmsg(OpinionPollActivity.this, successfullyUploaded);
                    surveyCount = 0;
                    surveyDetailsList.clear();
                    updateMenu();

                    Log.w(TAG, "upload survey :" + uploadSurvey.toString());
                }

            }
        });
    }

    private void failed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                completeNotification(uploadfailed);
                clearAll();
                Toastmsg(OpinionPollActivity.this, uploadfailed);
            }
        });
    }

    JSONArray uploadSurvey = new JSONArray();

    private void uploadSurveyOnebyOne(final SurveyDetails details) {


        try {
            JSONObject object = new JSONObject();
            object.put("projectId", details.getProjectId());
            object.put("cid", sharedPreferenceManager.get_constituency_id());
            object.put("userId", sharedPreferenceManager.get_user_id());
            object.put("usertype", details.getUserType());
            object.put("surveyid", details.getSurveyId());
            object.put("surveytype", details.getSurveyType());
            object.put("boothname", details.getBoothNmae());
            object.put("latitude", details.getLatitude());
            object.put("longitude", details.getLongitude());
            object.put("audiofilename", details.getAudioFileName());
            object.put("surveydate", details.getSurveyDate());
            object.put("respondantname", details.getName());
            object.put("mobile", details.getMobile());
            object.put("pwname", details.getPwName());

            uploadSurvey.put(object);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(API.UPLOAD_SURVEY + "&projectID=" + details.getProjectId() +
                "&cid=" + sharedPreferenceManager.get_constituency_id() + "&userId=" + details.getUserId() + "&usertype=" + details.getUserType() +
                "&surveyid=" + details.getSurveyId() + "&surveytype=" + details.getSurveyType() + "&boothname=" + details.getBoothNmae() +
                "&lattitude=" + details.getLatitude() + "&longitude=" + details.getLongitude() + "&audiofilename=" + details.getAudioFileName() +
                "&surveydate=" + details.getSurveyDate() + "&respondantname=" + details.getName() + "&mobile=" + details.getMobile() + "&pwname=" + details.getPwName()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                //
                dialog.dismiss();
                failed();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                final String myResponse = response.body().string();
                // dialog.dismiss();
                success(details.getSurveyId());

            }
        });

        /*StringRequest request = new StringRequest(Request.Method.GET, API.UPLOAD_SURVEY + "&projectID=" + details.getProjectId() +
                "&cid=" + sharedPreferenceManager.get_constituency_id() + "&userId=" + details.getUserId() + "&usertype=" + details.getUserType() +
                "&surveyid=" + details.getSurveyId() + "&surveytype=" + details.getSurveyType() + "&boothname=" + details.getBoothNmae() +
                "&lattitude=" + details.getLatitude() + "&longitude=" + details.getLongitude() + "&audiofilename=" + details.getAudioFileName() +
                "&surveydate=" + details.getSurveyDate()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "Response : " + response);
                surveyCount++;

                Log.w(TAG, "Survey id " + details.getSurveyId());
                db.update_attribute(details.getSurveyId());
                if (surveyCount <= surveyDetailsList.size() - 1) {
                    updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                    uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                }

                if (surveyCount == surveyDetailsList.size()) {
                    completeNotification(uploadComplete);
                    dialog.dismiss();
                    isUpoadingData = false;
                    Toastmsg(OpinionPollActivity.this, successfullyUploaded);
                    surveyCount = 0;
                    surveyDetailsList.clear();

                    updateMenu();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                completeNotification(uploadfailed);

                Log.w(TAG, "Error : " + error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
*/
    }

    //update menu
    private void updateMenu() {

        int count = getSurveyCount();

        Log.w(TAG, "Count :" + count);
        if (count > 0) {
            updateMenuImageId(R.drawable.sync_onlinered, count, true);

        } else {
            updateMenuImageId(R.drawable.sync_online2, count, false);
        }


    }

    //update id of the menu image
    private void updateMenuImageId(int id, int count, boolean isHaveCount) {
        for (int i = 0; i <= list.size() - 1; i++) {
            MainMenuDetails details = list.get(i);
            if (details.getTitle().equals(syncOnline)) {
                details.setId(id);
                details.setDataToUpload(isHaveCount);
                details.setCount(count);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @BindString(R.string.uploadFailed)
    String uploadfailed;

    public JSONObject getJSONForBulkUpload(String voterCardNumber, String attributeName, String attributeValue) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("voterCardNumber", voterCardNumber);
        json.put("propName", attributeName);
        //String[] temp = attributeValue.replaceAll("\\\\(.)","").split("\\\\");
        String value = "";
        char[] tempc = attributeValue.replaceAll("\\\\(.)", "").toCharArray();

        for (int i = 0; i < tempc.length; i++) {
            if (!(tempc[i] == '\\'))
                value += tempc[i];
        }
        json.put("propValue", value);
        Log.w(TAG, "CSA 484 " + value);
//        json.put("propValue", attributeValue.replaceAll("\\\\(.)",""));

        return json;
    }

    private void upload_to_server() {





        JSONObject object = new JSONObject();
        JSONArray array = get_upload_data();
        //array.get
        try {
            object.put("payload", array);
            object.put("username", sharedPreferenceManager.get_username());
            object.put("date", get_current_date());
            object.put("constituencyId", sharedPreferenceManager.get_constituency_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toastmsg(ConductSurveyActivity.this,"CSA 489");
        //finish();
        Log.w(TAG, "object " + object.toString());
        if (array.length() > 0) {
            if (checkInternet.isConnected()) {
                progressLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(10);
                txtUploadData.setText(uploading);

                isUpoadingData = true;
                //  dialog.show();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API.UPLOAD_DATA, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  dialog.dismiss();
                        try {
                            Log.w(TAG, response.getString("message"));

                            if (response.getString("message").equals("Inserted Successfully")) {
                                //      Toastmsg(ConductSurveyActivity.this, " Successfully Uploaded .");

                                callToUploadImage();
                            } else {
                                clearAll();
                                Toastmsg(OpinionPollActivity.this, response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.w(TAG, "response557 " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  dialog.dismiss();
                        clearAll();
                        Toast.makeText(OpinionPollActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error " + error.toString());

                    }
                });

                VolleySingleton.getInstance(this).addToRequestQueue(request);
            } else {
                Toastmsg(OpinionPollActivity.this, noInternet);
            }
        } else {
            Toastmsg(OpinionPollActivity.this, noRecordFound);
            callToUploadImage();
        }
    }

    //call to upload image
    private void callToUploadImage() {
        progressLayout.setVisibility(View.VISIBLE);
        isUpoadingData = true;
        //   progressBar.setProgress(30);
        //db.update_attribute();
        progressBar.setProgress(50);
        txtUploadFinish.setText(getResources().getString(R.string.successfullyUploaded));
        txtUploadData.setText(gettingFiles);
        upload_image();
    }

    private void clearAll() {
        isUpoadingData = false;
        progressLayout.setVisibility(GONE);
        txtUploadData.setText("");
        txtUploadFinish.setText("");
        progressBar.setProgress(0);
        file_count = 0;

    }


    File[] file;

    public void upload_image() {

        try {
            File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/");
            file = parentDir.listFiles();

            Log.w(TAG, "list file length " + file.length);


            if (file.length > 0) {
                if (checkInternet.isConnected()) {
                    //   dialog.show();
                    txtUploadData.setText(getResources().getString(R.string.uploadFiles, "0", file.length + ""));

                    // txtUploadData.setText("Uploading files (0/" + file.length + ")");

                    upload_image_onebyone(file[file_count]);
                } else {
                    Toastmsg(OpinionPollActivity.this, noInternet);
                }
            } else {

                if (sharedPreferenceManager.getUserType().equals("TSP")) {
                    clearAll();
                    if (checkInternet.isConnected()) {
                        if (surveyDetailsList.size() > 0) {
                            dialog.show();
                            createNotification();
                            isUpoadingData = true;
                            updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                            uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                        }
                    }
                } else {
                    Toastmsg(OpinionPollActivity.this, getResources().getString(R.string.noImagefound));
                    clearAll();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            Toastmsg(OpinionPollActivity.this, getResources().getString(R.string.noImagefound));
            clearAll();
            updateProgressbar();
            clearAll();

            if (checkInternet.isConnected()) {
                if (surveyDetailsList.size() > 0) {
                    dialog.show();
                    createNotification();
                    isUpoadingData = true;
                    updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                    uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                }
            }

        }

    }


    private void upload_image_onebyone(final File filetoBeUploaded) {


        String fileName = "";
        switch (getFileType(filetoBeUploaded.getAbsolutePath())) {

            case "mp3":
                fileName = filetoBeUploaded.getName();
                if (fileName.contains("AUD-SUAU_")) {
                    S3_RICHASSETS_FOLDER = Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.SUMMARY_AUDIO_PATH;

                } else {
                    S3_RICHASSETS_FOLDER = Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.AUDIO_PATH;
                }
                Log.w(TAG, "type : mp3 " + S3_RICHASSETS_FOLDER);


                break;

            case "jpg":

                S3_RICHASSETS_FOLDER = Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.IMAGE_PATH;
                Log.w(TAG, "type : jpg " + S3_RICHASSETS_FOLDER);
                fileName = filetoBeUploaded.getName();
                break;

            case "txt":
                if (filetoBeUploaded.getName().contains("issues")) {

                    S3_RICHASSETS_FOLDER = Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.ISSUES_PATH;
                    Log.w(TAG, "type : issues " + S3_RICHASSETS_FOLDER);
                    fileName = get_current_date() + "_" + get_current_time() + "_" + filetoBeUploaded.getName();
                } else if (filetoBeUploaded.getName().contains("summary")) {

                    S3_RICHASSETS_FOLDER = Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.SUMMARY_PATH;
                    Log.w(TAG, "type : summary " + S3_RICHASSETS_FOLDER);
                    fileName = get_current_date() + "_" + get_current_time() + "_" + filetoBeUploaded.getName();

                } else if (filetoBeUploaded.getName().contains("caste")) {

                    S3_RICHASSETS_FOLDER = Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.CASTE;
                    Log.w(TAG, "type : summary " + S3_RICHASSETS_FOLDER);
                    fileName = get_current_date() + "_" + get_current_time() + "_" + filetoBeUploaded.getName();

                } else if (filetoBeUploaded.getName().contains("influentialPeople")) {

                    S3_RICHASSETS_FOLDER = Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.INFLUENTIAL_PEOPLE;
                    Log.w(TAG, "type : summary " + S3_RICHASSETS_FOLDER);
                    fileName = get_current_date() + "_" + get_current_time() + "_" + filetoBeUploaded.getName();

                }
                break;
        }


        TransferObserver observer = transferUtility.upload(S3_BUCKETNAME + "/" + S3_RICHASSETS_FOLDER, fileName, filetoBeUploaded);

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int i, TransferState transferState) {
                switch (transferState) {
                    case COMPLETED:
                        try {
                            filetoBeUploaded.delete();

                            file_count++;

                            Log.w(TAG, " task completed");
                            //  dialog.dismiss();
                            if (file_count <= file.length - 1) {
                                if (checkInternet.isConnected()) {

                                    //  dialog.show();

                                    updateData();
                                    upload_image_onebyone(file[file_count]);
                                } else {
                                    Toastmsg(OpinionPollActivity.this, noInternet);
                                }
                            }

                            if (file_count == file.length) {
                                updateProgressbar();
                                clearAll();

                                if (checkInternet.isConnected()) {
                                    if (surveyDetailsList.size() > 0) {
                                        dialog.show();
                                        createNotification();
                                        isUpoadingData = true;
                                        updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                                        uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case FAILED:
                        //   dialog.dismiss();
                        if (checkInternet.isConnected()) {
                            //  dialog.show();
                            updateData();
                            upload_image_onebyone(file[file_count]);
                        } else {
                            Toastmsg(OpinionPollActivity.this, noInternet);
                        }
                        break;

                }
            }

            @Override
            public void onProgressChanged(int i, long l, long l1) {

            }

            @Override
            public void onError(int i, Exception e) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opinionpoll_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //change language
    private void changeLanguage(String languageToLoad) {

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        sharedPreferenceManager.set_Language(languageToLoad);
    }

    /*refresh activity*/
    private void refreshActivity() {
        Intent i = new Intent(OpinionPollActivity.this, OpinionPollActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.logout:
                sharedPreferenceManager.set_keep_login(false);
                sharedPreferenceManager.set_keep_login_role("");
                startActivity(new Intent(OpinionPollActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                OpinionPollActivity.this.finish();
                return true;
            case R.id.get_questions:
                sharedPreferenceManager.set_is_qus_download(false);
                db.delete_question();
                db.delete_hierarchy_hash();
                get_question();
                return true;


            case R.id.kannada:
                if (!sharedPreferenceManager.getLanguage().equals("kn")) {
                    changeLanguage("kn");
                    refreshActivity();
                }
                return true;
            case R.id.english:
                if (!sharedPreferenceManager.getLanguage().equals("en")) {
                    changeLanguage("en");
                    refreshActivity();
                }
                return true;

            case R.id.marati:
                if (!sharedPreferenceManager.getLanguage().equals("mr")) {
                    changeLanguage("mr");
                    refreshActivity();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /*get questions*/
    public void get_question() {
        if (checkInternet.isConnected()) {
            if (!sharedPreferenceManager.get_is_qus_downloaded()) {
                dialog.show();
                get_Question();
            }
        }
    }

    private void get_Question() {
        Log.w(TAG, "project id " + sharedPreferenceManager.get_project_id());
        String pid;
        if (!sharedPreferenceManager.get_project_id().isEmpty()) {
            pid = sharedPreferenceManager.get_project_id();
        } else {
            pid = "3";
        }

        Log.w(TAG, "question url :" + API.GET_QUESTIONS + sharedPreferenceManager.get_constituency_id() + "/" + pid);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API.GET_QUESTIONS + sharedPreferenceManager.get_constituency_id() + "/" + pid, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                Log.w(TAG, "Response209 " + response);
                try {
                    JSONArray hierarchiesarray = response.getJSONArray("hierarchies");
                    JSONArray array = response.getJSONArray("questions");
                    dialog.dismiss();
                    Log.w(TAG, " question length " + array.length());
                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject object = array.getJSONObject(i);
                        Questions questions = new Questions(object.getLong("id") + "", object.getLong("constituencyId") + "", object.getString("name"), object.getString("data"));
                        db.insert_questions(questions);
                    }


                    for (int i = 0; i <= hierarchiesarray.length() - 1; i++) {
                        JSONObject object = hierarchiesarray.getJSONObject(i);
                        String ward_details = object.getString("level1No") + " : " + object.getString("level1Name");
                        String booth_detials = object.getString("level2No") + " : " + object.getString("level2Name");

                        Log.w(TAG, "ward details " + ward_details + " booth details " + booth_detials);

                        db.insert_poll_details(object.getString("hierarchyHash"), ward_details, booth_detials);
                    }

                    sharedPreferenceManager.set_is_qus_download(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    @BindString(R.string.errorUploading)
    String errorUploading;

    //get upload data
    private void get_hard_upload_data() {


        int count = 0;

        try {
            Cursor c = db.get_all_survey_data();

            count = c.getCount();
            Log.w(TAG, "cursor count :" + c.getCount());
            if (c.moveToFirst()) {

                do {

                    boolean is_sync = Boolean.parseBoolean(c.getString(5));
                    VoterAttribute attribute = new VoterAttribute(c.getString(1), c.getString(2), c.getString(3), c.getString(4), is_sync, c.getString(6));
                    ex_db.insert_voter_attribute(attribute);

                } while (c.moveToNext());
            }
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        if (count > 0) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/" + file_name);
            Log.w(TAG, "file is exist " + file.exists());
            if (file.exists()) {
                Log.w(TAG, "file exxist");
                dialog.show();
                S3Utils.getInstance(OpinionPollActivity.this).upload_export_db(file, S3_BUCKETNAME + "/" + Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.SURVEY_PATH, dialog);

                //upload_export_db(file);

            } else {
                //dismiss_dialog();
                Toastmsg(OpinionPollActivity.this, errorUploading);
            }

        } else {
            Toastmsg(OpinionPollActivity.this, noRecordFound);
        }
    }


    @BindString(R.string.successfullyUploaded)
    String successfullyUploaded;

    @BindString(R.string.dataUploading)
    String dataUploading;


    private void updateData() {

        txtUploadData.setText(getResources().getString(R.string.uploadFiles, (file_count + 1) + "", file.length + ""));
        //txtUploadData.setText("Uploading Files (" + (file_count + 1) + "/" + file.length + ")");

        Log.w(TAG, "file count : " + file_count + "file length : " + file.length);

        int progresscount = ((file_count * 50) / file.length);


        Log.w(TAG, "Progress bar state : " + progresscount);
        progressBar.setProgress(progresscount + 50);
    }

    private void updateProgressbar() {
        progressBar.setProgress(100);
    }


    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    @BindString(R.string.Error)
    String Error;

    //finishh the activity
    private void finish_activity() {
        OpinionPollActivity.super.onBackPressed();
        OpinionPollActivity.this.finish();
    }


    /*load data to spinner*/
    private void loadDataToSpinner() {
        if (booth_list.size() > 0) {
            spinner.setVisibility(View.VISIBLE);
            booth_list.add(0, selectBooth);
            set_adapter_for_spinner(booth_list);
        } else {
            //   hide_question_layout();
            spinner.setVisibility(GONE);
            Toastmsg(OpinionPollActivity.this, noBoothAllocated);
            // finish_activity();
        }
    }


    /*get booth details*/
    private void get_booth_details() {
        StringRequest request = new StringRequest(Request.Method.GET, API.BOOTH_INFO + "userId=" + sharedPreferenceManager.get_user_id() + "&constituencyId=" + sharedPreferenceManager.get_constituency_id() + "&projectId=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                dialog.dismiss();
                Log.w(TAG, "Response " + response);
                try {
                    JSONArray array = new JSONArray(response);

                    if (array.length() > 0) {

                        db.deleteBooths();

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {
                                int status = object.getInt("astatus");
                                if (status != 0) {
                                    db.insertBooths(object.getString("booth"));
                                    booth_list.add(object.getString("booth"));
                                }
                            } else {
                                db.insertBooths(object.getString("booth"));
                                booth_list.add(object.getString("booth"));
                            }

                        }

                        loadDataToSpinner();

                    } else {
                        loadDefaultBooth();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());

                dialog.dismiss();
                loadDefaultBooth();

                Toastmsg(OpinionPollActivity.this, Error);

            }
        });

        //  VolleySingleton.getInstance(this).addToRequestQueue(request);

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @BindString(R.string.questionare)
    String questionare;
    @BindString(R.string.casteEstimate)
    String casteEstimate;
    @BindString(R.string.casteEstimation)
    String casteEstimation;
    @BindString(R.string.influentialPeople)
    String influentialPeople;
    @BindString(R.string.issues)
    String issues;
    @BindString(R.string.summary)
    String summary;

    @BindString(R.string.syncOnline)
    String syncOnline;

    @BindString(R.string.uploading)
    String uploading;

    @BindString(R.string.photo)
    String photo;

    @BindString(R.string.audio)
    String audio;

    @BindString(R.string.telecalling)
    String teleCalling;

    /*add values in title list*/
    private void add_values_to_list() {

        if (sharedPreferenceManager.getUserType().equals("TSP")) {
            list.add(new MainMenuDetails(R.drawable.quetionair, questionare, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
        } else if (sharedPreferenceManager.getUserType().equalsIgnoreCase("kp")) {
            list.add(new MainMenuDetails(R.drawable.quetionair, questionare, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, audio, false, 0));

        } else {
            list.add(new MainMenuDetails(R.drawable.quetionair, questionare, false, 0));
            list.add(new MainMenuDetails(R.drawable.caste_estimate, casteEstimate, false, 0));
            list.add(new MainMenuDetails(R.drawable.caste_estimation, casteEstimation, false, 0));
            list.add(new MainMenuDetails(R.drawable.influentil_pepole, influentialPeople, false, 0));
            list.add(new MainMenuDetails(R.drawable.issues, issues, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, summary, false, 0));
            list.add(new MainMenuDetails(R.drawable.cameramenu, photo, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, audio, false, 0));
        }

    }


    private int getSurveyCount() {
        int isHaveCount = 0;
        try {

            Cursor c = db.get_voter_atrributes();
            Log.w(TAG, "count  : " + c.getCount());

            if (c.getCount() > 0) {
                isHaveCount = c.getCount();
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return isHaveCount;
    }

    @Override
    public void onBackPressed() {
        if (isUpoadingData) {
            Toastmsg(OpinionPollActivity.this, uploading);
        } else {
            super.onBackPressed();
        }
    }

    @BindString(R.string.selectBooth)
    String selectBooth;


    //stop media player
    private void stop() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    //play audio
    private void playAudio() {

        stop();

        try {
            mp = MediaPlayer.create(this, R.raw.erro);
            // mp.prepare();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mp.start();
    }

    OpinionPollAdapter adapter;

    //set adapter
    private void set_adapter() {

        adapter = new OpinionPollAdapter(this, list, OpinionPollActivity.this);
        recyclerView.setAdapter(adapter);
    }

    //alert dialog with multiple opition
    private void alert() {

        String[] options = {"Theatre Survey", "Public Survey"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select one");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which) {
                    case 1:
                        startActivity(new Intent(OpinionPollActivity.this, TheatrePopleOpinionActivity1.class));
                        break;
                    case 0:
                        startActivity(new Intent(OpinionPollActivity.this, TheatreProfilingAdministrationActivity.class));

                        break;
                }
            }
        });
        builder.show();
    }

    /*click option from menu*/
    public void btn_click_from_menu(int position, String title) {


        if (userType.equals("TSP")) {
            switch (position) {
                case 0:
                    //   alert();
                    if (checkPermissionGranted(Constants.LOCATION_PERMISSION, this)) {

                        if (!is_enabled_gps(OpinionPollActivity.this)) {

                            mEnableGps();
                            // showSettingsAlert(ConductSurveyActivity.this);
                        } else {
                            if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.RECORD_AUDIO, this)) {
                                alert();
                                //  startActivity(new Intent(OpinionPollActivity.this, QuestionareActivity.class).putExtra("userType", "opcp").putExtra("boothName", booth_name));

                            } else {
                                askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.RECORD_AUDIO});
                            }
                        }
                    } else {
                        askPermission(new String[]{Constants.LOCATION_PERMISSION});
                    }
                    break;

                case 1:
                    if (checkInternet.isConnected()) {
                        upload_to_server();
                    } else {
                        Toastmsg(OpinionPollActivity.this, noInternet);
                    }
                    break;
            }
        } else {

            if (booth_list.size() > 0) {

                switch (position) {
                    case 0:
                        if (checkPermissionGranted(Constants.LOCATION_PERMISSION, this)) {

                            if (!is_enabled_gps(OpinionPollActivity.this)) {

                                mEnableGps();
                                // showSettingsAlert(ConductSurveyActivity.this);
                            } else {
                                if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.RECORD_AUDIO, this)) {
                                    if (!booth_name.isEmpty()) {
                                        startActivity(new Intent(OpinionPollActivity.this, QuestionareActivity.class).putExtra("userType", "opcp").putExtra("boothName", booth_name));
                                    } else {
                                        Toastmsg(OpinionPollActivity.this, selectBooth);
                                    }
                                } else {
                                    askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.RECORD_AUDIO});
                                }
                            }
                        } else {
                            askPermission(new String[]{Constants.LOCATION_PERMISSION});
                        }
                        break;
                    case 1:
                        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("kp")) {
                            if (checkPermissionGranted(Constants.RECORD_AUDIO, this)) {

                                if (!booth_name.isEmpty()) {
                                    startActivity(new Intent(OpinionPollActivity.this, SummaryAudioActivity.class).putExtra("booth_name", booth_name).putExtra("userType", "opcp"));
                                  //  startActivity(new Intent(OpinionPollActivity.this, SurveyStatsActivity.class));
                                } else {
                                    Toastmsg(OpinionPollActivity.this, selectBooth);
                                }
                            } else {
                                askPermission(new String[]{Constants.RECORD_AUDIO});
                            }

                        } else {
                            if (!booth_name.isEmpty()) {
                                startActivity(new Intent(OpinionPollActivity.this, CasteEstimationActivity.class).putExtra("title", "Caste Estimate").putExtra("booth_name", booth_name).putExtra("userType", "opcp"));
                            } else {
                                Toastmsg(OpinionPollActivity.this, selectBooth);
                            }
                        }
                        break;
                    case 2:
                        if (!booth_name.isEmpty()) {
                            startActivity(new Intent(OpinionPollActivity.this, CasteEstimationActivity.class).putExtra("title", "Caste Estimation").putExtra("booth_name", booth_name).putExtra("userType", "opcp"));
                        } else {
                            Toastmsg(OpinionPollActivity.this, selectBooth);
                        }
                        break;
                    case 3:
                        if (!booth_name.isEmpty()) {
                            startActivity(new Intent(OpinionPollActivity.this, InfluentialPeopleActivity.class).putExtra("title", title).putExtra("booth_name", booth_name).putExtra("userType", "opcp"));
                        } else {
                            Toastmsg(OpinionPollActivity.this, selectBooth);
                        }
                        break;
                    case 4:
                        startActivity(new Intent(OpinionPollActivity.this, BoothSummaryActivity.class).putExtra("userType", "opcp").putExtra("booth_name", booth_name));
                        break;
                    case 5:
                        if (!booth_name.isEmpty()) {
                            startActivity(new Intent(OpinionPollActivity.this, SummaryActivity.class).putExtra("title", title).putExtra("booth_name", booth_name).putExtra("userType", "opcp"));
                        } else {
                            Toastmsg(OpinionPollActivity.this, selectBooth);
                        }
                        break;


                    case 6:
                        if (!booth_name.isEmpty()) {
                            startActivity(new Intent(OpinionPollActivity.this, PhotoActivity.class).putExtra("title", title).putExtra("booth_name", booth_name).putExtra("userType", "opcp"));
                        } else {
                            Toastmsg(OpinionPollActivity.this, selectBooth);
                        }
                        break;

                    case 7:
                        if (checkPermissionGranted(Constants.RECORD_AUDIO, this)) {

                            if (!booth_name.isEmpty()) {
                                startActivity(new Intent(OpinionPollActivity.this, SummaryAudioActivity.class).putExtra("booth_name", booth_name).putExtra("userType", "opcp"));
                            } else {
                                Toastmsg(OpinionPollActivity.this, selectBooth);
                            }
                        } else {
                            askPermission(new String[]{Constants.RECORD_AUDIO});
                        }

                        break;
                    default:
                        break;
                }
            } else {
                Toastmsg(OpinionPollActivity.this, noBoothAllocated);
                playAudio();
            }
        }
    }

    @BindString(R.string.processing)
    String processing;

    private void set_adapter_for_spinner(ArrayList<String> options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    if (is_enabled_gps(OpinionPollActivity.this)) {

                        if (!recyclerView.isShown()) {
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        booth_name = spinner.getSelectedItem().toString();
                        sharedPreferenceManager.set_booth(booth_name);

                    } else {
                        showSettingsAlert(OpinionPollActivity.this);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    booth_name = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @BindString(R.string.noOfSurveydone)
    String noOfSurveyDone;


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        updateMenu();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
