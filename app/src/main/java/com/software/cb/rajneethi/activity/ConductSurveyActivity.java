package com.software.cb.rajneethi.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.MainMenuAdapter1;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.ExportDatabase;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.MainMenuDetails;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.SurveyDetails;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.S3Utils;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

//import com.blanche.carte.rajneethi.adapter.MainMenuAdapter;

/**
 * Created by Monica on 11-03-2017.
 */

public class ConductSurveyActivity extends UtilActivity implements checkInternet.ConnectivityReceiverListener {

    @BindView(R.id.main_recyclerview)
    RecyclerView recyclerView;


    private int image_ids[] = {R.drawable.search2, R.drawable.sync_online2, R.drawable.survey_data2, R.drawable.upload_image2,
            R.drawable.caste_equation2};

    private ArrayList<String> name_list = new ArrayList<>();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MainMenuAdapter1 adapter;

    private static final String TAG = "MainActivity";

    private TransferObserver transferObserver;


    private static final String S3_BUCKETNAME = "rn-assets";
    private static final String S3_CONSTITUENCIES_FOLDER = "constituencies";
    private String S3_RICHASSETS_FOLDER = "richassets";
    private static final String S3_CONSTITUENCY_DESTINATION_FOLDER = "offline/data/";
    private static volatile S3Utils instance;
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;
    private TransferUtility transferUtility;


       SharedPreferenceManager sharedPreferenceManager;
    private MyDatabase db;

    private VotersDatabase votersDatabase;
    ExportDatabase ex_db;

    @BindView(R.id.txt_no_of_survey)
    TextView txt_no_of_survey;

    private int file_count = 0;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 101;
    private static final int PERMISSION_REQUEST_LOCATION = 102;

    String file_name;
    @BindString(R.string.conductSuvey)
    String toolbarTitle;
    private IOSDialog dialog;

    private boolean bskMode;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.progresslayout)
    FrameLayout progressLayout;


    @BindView(R.id.txtUploadData)
    TextView txtUploadData;

    private boolean isUpoadingData = false;

    private NotificationManager mNotifyManager;
    private android.support.v4.app.NotificationCompat.Builder build;
    int id = 1;

    @BindString(R.string.upload)
    String upload;

    @BindString(R.string.uploadInProgress)
    String uploadInProgress;

    @BindString(R.string.uploadComplete)
    String uploadComplete;

    private ArrayList<SurveyDetails> surveyDetailsList = new ArrayList<>();

    private ArrayList<MainMenuDetails> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conduct_survey);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,toolbarTitle);


        if (getIntent().getExtras() != null) {
            bskMode = getIntent().getExtras().getBoolean("bskMode");
        }

        dialog = show_dialog(ConductSurveyActivity.this, false);
        sharedPreferenceManager = new SharedPreferenceManager(this);


        if (sharedPreferenceManager.get_keep_login_role().equals("PartyWorker")) {
            image_ids = new int[]{R.drawable.search2, R.drawable.upload_image2, R.drawable.get_survey2, R.drawable.upload_image2, R.drawable.survey_data2, R.drawable.election_history2, R.drawable.election_history2};
        }

        db = new MyDatabase(this);

        //  db.update_attribute("20180110_011704_121");
        votersDatabase = new VotersDatabase(this);
        add_name_in_menulist();
        // get_election_history();

        file_name = sharedPreferenceManager.get_user_id() + "_" + sharedPreferenceManager.get_constituency_id() + "_" + get_current_date() + "_" + get_current_time() + ".db";
        file_name = file_name.replaceAll(" ", "_");

        ex_db = new ExportDatabase(this, file_name);


        set_grid_layout_manager(recyclerView, this, 3);
        set_adapter();



        get_question();

    }

    /*get questions*/
    public void get_question() {
        if (checkInternet.isConnected()) {
            if (!sharedPreferenceManager.get_is_qus_downloaded()) {
                dialog.show();
                get_booth_details();
            }
        }
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


    private void addDataToSurveyList(String data) {

        Log.w(TAG, "data : " + data);
        try {

            JSONObject object = new JSONObject(data);
            surveyDetailsList.add(new SurveyDetails(object.getString("surveyid"),
                    object.getString("latitude"), object.getString("longitude"),
                    object.getString("audioFileName"), object.getString("surveyDate"),
                    object.getString("partyWorker"), object.getString("userType"),
                    object.getString("projectId"), object.getString("booth_name"), object.getString("surveyType")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int surveyCount = 0;

    private void uploadSurveyOnebyOne(final SurveyDetails details) {

        StringRequest request = new StringRequest(Request.Method.GET, API.UPLOAD_SURVEY + "&projectID=" + details.getProjectId() +
                "&cid=" + sharedPreferenceManager.get_constituency_id() + "&userId=" + details.getUserId() + "&usertype=" + details.getUserType() +
                "&surveyid=" + details.getSurveyId() + "&surveytype=" + details.getSurveyType() + "&boothname=" + details.getBoothNmae() +
                "&lattitude=" + details.getLatitude() + "&longitude=" + details.getLongitude() + "&audiofilename=" + details.getAudioFileName() +
                "&surveydate=" + details.getSurveyDate()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "Response : " + response);
                surveyCount++;
                db.update_attribute(details.getSurveyId());

                if (surveyCount <= surveyDetailsList.size() - 1) {
                    updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                    uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                }

                if (surveyCount == surveyDetailsList.size()) {
                    completeNotification(uploadComplete);
                    dialog.dismiss();
                    isUpoadingData = false;
                    Toastmsg(ConductSurveyActivity.this, successfullyUploaded);
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

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @BindString(R.string.uploadFailed)
    String uploadfailed;

    private void get_booth_details() {
        Log.w(TAG, "project id " + sharedPreferenceManager.get_project_id());
        String pid;
        if (!sharedPreferenceManager.get_project_id().isEmpty()) {
            pid = sharedPreferenceManager.get_project_id();
        } else {
            pid = "2";
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API.GET_QUESTIONS + sharedPreferenceManager.get_constituency_id() + "/" + pid, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                Log.w(TAG, "Response206 " + response);
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


    private void set_adapter() {
        adapter = new MainMenuAdapter1(this, list, ConductSurveyActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.fab_addvoter)
    public void add_new_voter() {

        if (isUpoadingData) {

            Toastmsg(ConductSurveyActivity.this, dataUploading);
        } else {

            if (checkPermissionGranted(Constants.LOCATION_PERMISSION, this)) {

                if (!is_enabled_gps(ConductSurveyActivity.this)) {

                    mEnableGps();
                    // showSettingsAlert(ConductSurveyActivity.this);
                } else {
                    if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.RECORD_AUDIO, this)) {
                        startActivity(new Intent(ConductSurveyActivity.this, AddVoterActivity.class).putExtra("userType", "d2d"));
                    } else {
                        askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.RECORD_AUDIO});
                    }
                }
            } else {
                askPermission(new String[]{Constants.LOCATION_PERMISSION});
            }
        }
    }

    @BindString(R.string.noInternet)
    String noInternet;

    public void goto_next_activity(String position) {

        if (isUpoadingData) {
            Toastmsg(ConductSurveyActivity.this, dataUploading);
        } else {

            if (position.equals(search)) {
                startActivity(new Intent(ConductSurveyActivity.this, VoterSearchActivity.class).putExtra("bskMode", bskMode));
            } else if (position.equals(syncOnline)) {

                upload_to_server();

            } else if (position.equals(getSurvyData)) {
                if (checkInternet.isConnected()) {
                    new get_survey_data().execute();
                } else {
                    Toastmsg(ConductSurveyActivity.this, noInternet);
                }
            } else if (position.equals(uploadImage)) {
                // upload_image();
                upload_to_server();
            }

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

    File[] file;

    public void upload_image() {
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
                Toastmsg(ConductSurveyActivity.this, noInternet);
            }
        } else {
            Toastmsg(ConductSurveyActivity.this, getResources().getString(R.string.noImagefound));
            clearAll();
        }


    }

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

    @BindString(R.string.successfullyUploaded)
    String successfullyUploaded;

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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        file_count++;
                        // Toastmsg(ConductSurveyActivity.this, "Success");
                        /*Intent intent = new Intent(ConductSurveyActivity.this, ToastMessageActivity.class);
                        intent.putExtra("MESSAGE", String.valueOf(" Sucessfully Uploaded ."));
                        intent.putExtra("CLASSNAME", String.valueOf("ConductSurveyActivity.class"));
                        startActivity(intent);*/

                        // Toastmsg(ConductSurveyActivity.this, successfullyUploaded);
                        Log.w(TAG, " task completed");
                        //  dialog.dismiss();
                        if (file_count <= file.length - 1) {
                            if (checkInternet.isConnected()) {

                                //  dialog.show();

                                updateData();
                                upload_image_onebyone(file[file_count]);
                            } else {
                                Toastmsg(ConductSurveyActivity.this, noInternet);
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

                        break;
                    case FAILED:
                        //   dialog.dismiss();
                        if (checkInternet.isConnected()) {
                            //  dialog.show();
                            updateData();
                            upload_image_onebyone(file[file_count]);
                        } else {
                            Toastmsg(ConductSurveyActivity.this, noInternet);
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

    @BindView(R.id.txtUploadFinish)
    TextView txtUploadFinish;

    @BindString(R.string.uploading)
    String uploading;

    @BindString(R.string.gettingFiles)
    String gettingFiles;

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

                                progressBar.setProgress(30);
                                progressBar.setProgress(50);
                                txtUploadFinish.setText(getResources().getString(R.string.successfullyUploaded));
                                txtUploadData.setText(gettingFiles);
                                upload_image();
                            } else {
                                clearAll();
                                Toastmsg(ConductSurveyActivity.this, response.getString("message"));
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
                        Toast.makeText(ConductSurveyActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error " + error.toString());

                    }
                });

                VolleySingleton.getInstance(this).addToRequestQueue(request);
            } else {
                Toastmsg(ConductSurveyActivity.this, noInternet);
            }
        } else {
            callToUploadImage();
            Toastmsg(ConductSurveyActivity.this, noRecordFound);
        }
    }

    private void clearAll() {
        isUpoadingData = false;
        progressLayout.setVisibility(GONE);
        txtUploadData.setText("");
        txtUploadFinish.setText("");
        progressBar.setProgress(0);
        file_count = 0;
        S3_RICHASSETS_FOLDER = "richassets";

    }

    @Override
    public void onBackPressed() {
        if (isUpoadingData) {
            Toastmsg(ConductSurveyActivity.this, dataUploading);
        } else {
            super.onBackPressed();
        }
    }

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

    @BindString(R.string.search)
    String search;
    @BindString(R.string.syncOnline)
    String syncOnline;
    @BindString(R.string.getSurveyData)
    String getSurvyData;
    @BindString(R.string.uploadImage)
    String uploadImage;

    //update menu
    private void updateMenu() {

        int count = getSurveyCount();
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

    //load names into array list
    private void add_name_in_menulist() {
        // if ((sharedPreferenceManager.get_keep_login_role().equals("Client"))) {
        list.add(new MainMenuDetails(R.drawable.search2, search, false, 0));
        list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
        list.add(new MainMenuDetails(R.drawable.get_survey2, getSurvyData, false, 0));
        // list.add(new MainMenuDetails(R.drawable.upload_image2,uploadImage,false));


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conduct_survey, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @BindString(R.string.dataUploading)
    String dataUploading;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sharedPreferenceManager.set_keep_login(false);
                // new delete().execute();
                startActivity(new Intent(ConductSurveyActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                ConductSurveyActivity.this.finish();
                return true;
            case R.id.get_questions:
                sharedPreferenceManager.set_is_qus_download(false);
                db.delete_question();
                db.delete_hierarchy_hash();
                get_question();
                return true;
            case R.id.upload:
                if (checkInternet.isConnected()) {
                    Toastmsg(ConductSurveyActivity.this, dataUploading);

                    get_hard_upload_data();
                } else {
                    Toastmsg(ConductSurveyActivity.this, noInternet);
                    alert_for_no_internet();
                }
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

                S3Utils.getInstance(ConductSurveyActivity.this).upload_export_db(file, S3_BUCKETNAME + "/" + Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.SURVEY_PATH, dialog);
                // upload_export_db(file);

            } else {
                //dismiss_dialog();
                Toastmsg(ConductSurveyActivity.this, errorUploading);
            }

        } else {
            Toastmsg(ConductSurveyActivity.this, noRecordFound);
        }
    }

    @BindString(R.string.errorUploading)
    String errorUploading;

    /*   //uplosd export db_file
       private void upload_export_db(final File filetoBeUploaded) {

           Log.w(TAG, "file name " + filetoBeUploaded.getName());

           TransferObserver observer = transferUtility.upload(S3_BUCKETNAME + "/" + Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.SURVEY_PATH, filetoBeUploaded.getName(), filetoBeUploaded);

           observer.setTransferListener(new TransferListener() {
               @Override
               public void onStateChanged(int i, TransferState transferState) {
                   switch (transferState) {
                       case COMPLETED:
                           dialog.dismiss();
                           Toastmsg(ConductSurveyActivity.this, successfullyUploaded);

                           Log.w(TAG, " task completed");


                           break;
                       case FAILED:
                           dialog.dismiss();
                           Toastmsg(ConductSurveyActivity.this, Error);
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
   */
    @BindString(R.string.Error)
    String Error;

    private class delete extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            votersDatabase.delete_voters_data();
            return null;
        }
    }


    private class get_survey_data extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.w(TAG, "Url :" + API.GET_SURVEY_DATA + sharedPreferenceManager.get_constituency_id());

            StringRequest request = new StringRequest(Request.Method.GET, API.GET_SURVEY_DATA + sharedPreferenceManager.get_constituency_id(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONArray array = new JSONArray(response);

                        dialog.dismiss();

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int sos;
                            switch (obj.getString("value")) {
                                case "Weak":
                                    sos = 1;
                                    break;
                                case "Moderate":
                                    sos = 2;
                                    break;
                                case "Strong":
                                    sos = 3;
                                    break;
                                case "No support":
                                    sos = 4;
                                    break;
                                default:
                                    sos = 5;
                                    break;
                            }

                            if (obj.getString("serialNo").equals("0")) {

                                votersDatabase.insertNewVoterData(obj.getString("voterCardNumber"), sos);
                            } else {
                                votersDatabase.updateAllSurveyData(obj.getString("voterCardNumber"), sos);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Log.w(TAG, "Response741 " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Toast.makeText(ConductSurveyActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error " + error.toString());
                }
            });

            VolleySingleton.getInstance(ConductSurveyActivity.this).addToRequestQueue(request);
            return null;
        }
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

        try {
            int count = db.get_survey_count();
            String text = noOfSurveyDone + count;
            txt_no_of_survey.setText(text);
        } catch (Exception e) {
            txt_no_of_survey.setText(noOfSurveyDone + "0");
        }

        updateMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Until you grant the permission, you cannot select a contact", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Until you grant the permission, you cannot send message", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Until you grant the permission, you cannot get a location", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
