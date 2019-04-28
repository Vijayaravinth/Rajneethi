package com.software.cb.rajneethi.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.BoothInfoAdapter;
import com.software.cb.rajneethi.adapter.MainMenuAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.database.ExportDatabase;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.job.MyJob;
import com.software.cb.rajneethi.models.MainMenuDetails;
import com.software.cb.rajneethi.models.NavigationItems;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.SurveyDetails;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.qc.QCUsersActivity;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.LocationCalculator;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.S3Utils;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.vansuita.library.CheckNewAppVersion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static android.view.View.GONE;

/**
 * Created by Monica on 11-03-2017.
 */

public class OfflineConstituencyDashBoardActivity extends UtilActivity implements Handler.Callback, checkInternet.ConnectivityReceiverListener, View.OnTouchListener {

    @BindView(R.id.main_recyclerview)
    RecyclerView recyclerView;
    @BindString(R.string.dataUploading)
    String dataUploading;
    @BindString(R.string.syncOnline)
    String syncOnline;

    @BindString(R.string.fundManagement)
    String fundManagement;
    @BindString(R.string.grievanceManagement)
    String grievanceManagement;
    @BindString(R.string.beneficiaryManagement)
    String beneficiaryManagement;

    JobScheduler scheduler;

    private int image_ids[] = {R.drawable.search2, R.drawable.survey_data2,
            R.drawable.caste_equation2, R.drawable.event2,
            R.drawable.benificiary2,
            R.drawable.election_history2, R.drawable.grievience2, R.drawable.fund_management2,
            R.drawable.communication2, R.drawable.benificiary2, R.drawable.whatsapp2};
    private ArrayList<String> name_list = new ArrayList<>();

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    MainMenuAdapter adapter;

    @BindString(R.string.films)
    String films;

  /*  @BindView(R.id.theatreStatistics)
    View theatreStaistics;
*/
    /*@BindView(R.id.txtTheatreTotal)
    CircularTextView txtTheatreTotal;
*/
   /* @BindView(R.id.txtPeopleTotal)
    CircularTextView txtPeopleTotal;
    @BindView(R.id.txtTotal)
    CircularTextView txtTotal;*/


   /* @BindView(R.id.imgInfo)
    FloatingActionButton imgInfo;*/

    @BindView(R.id.booth_recyclerview)
    RecyclerView boothRecyclerView;

    @BindView(R.id.txtNoOfSurveys)
    TextView txtNoOfSurveysDone;

    @BindView(R.id.txtNoOfSurveysToComplete)
    TextView txtNoOFSurveysToComplete;

    @BindView(R.id.progresslayout)
    FrameLayout progressLayout;

    @BindString(R.string.processing)
    String processing;

    @BindView(R.id.txtUploadData)
    TextView txtUploadData;

    private boolean isUpoadingData = false;

    private ThreadPoolExecutor executor;

    int NUM_OF_CORES = 0;

    boolean isUploadAgain = false;

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


    private static final String TAG = "MainActivity";


    SharedPreferenceManager sharedPreferenceManager;
    private MyDatabase db;

    private VotersDatabase votersDatabase;


    JSONArray theatreStatisticsArray;

    //navigation itema
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;


    private ActionBarDrawerToggle mDrawerToggle;


    @BindString(R.string.offlineDashboard)
    String toolbartitle;

    private ArrayList<MainMenuDetails> list = new ArrayList<>();

    private IOSDialog dialog;

    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;


    private boolean isUpdating = false;

    @BindString(R.string.allocatedBooth)
    String allocatedBooth;
    @BindString(R.string.numofdone)
    String numOfDoneSurvey;
    @BindString(R.string.numofyetcomplete)
    String numYetToComplete;

    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    @BindString(R.string.live_track)
    String liveTrack;

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    private void createJob() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            myReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.w(TAG, intent.toString());
                    Bundle args = intent.getExtras();
                    Log.w(TAG, "on message received working ");
                    if (args.getString("job").equalsIgnoreCase("Done")) {
                        updateMenu();
                    }
                }
            };

            registerReceiver(myReceiver, new IntentFilter("INTENT_FILTER"));


            Log.w(TAG, "version greater than lollipop");
            JobInfo.Builder info = null;


            info = new JobInfo.Builder(jobId, new ComponentName(OfflineConstituencyDashBoardActivity.this, MyJob.class));
            info.setMinimumLatency(1000);
            info.setOverrideDeadline(3 * 1000);
            info.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            info.setRequiresDeviceIdle(true);
            info.setRequiresCharging(true);


            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("url", API.GET_SURVEY_DATA + sharedPreferenceManager.get_constituency_id());

            info.setExtras(bundle);
            int result = scheduler.schedule(info.build());

            if (result == 1) {
                Log.w(TAG, "Schedule success");
            } else {
                Log.w(TAG, "Schedule failed");
            }


            jobId++;
        }
    }


    //ONMlAbbs/q046OKwnoM8+gyUMK8otpu54r2vON+x68k5Xx990mNewkLMWmW7OBrbuXTDZx4XCMm6mzPM+8nUHg==
//   V5zcNC4087AGdwxNLlq14g==

    private void initialize() {
        String project = mFirebaseRemoteConfig.getString("project_id");
        String users = mFirebaseRemoteConfig.getString("users");


        Log.w(TAG, "remote value :" + project + " " + users);
        try {
            if (!project.isEmpty()) {


                String[] list = project.split(",");

                for (int i = 0; i <= list.length - 1; i++) {
                    Log.w(TAG, "val :" + list[i]);

                    if (sharedPreferenceManager.get_project_id().equalsIgnoreCase(list[i])) {
                        sharedPreferenceManager.set_keep_login(false);
                        startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, LoginActivity.class));
                        OfflineConstituencyDashBoardActivity.this.finish();
                        break;
                    }
                }

              /*  JSONObject object = new JSONObject(project);

                Iterator iterator = object.keys();
                while (iterator.hasNext()){

                    Log.w(TAG,"val :"+ object.getString("id"));
                }*/

            }

            if (!users.isEmpty()) {


                String[] list = users.split(",");

                for (int i = 0; i <= list.length - 1; i++) {

                    if (sharedPreferenceManager.get_username().equalsIgnoreCase(list[i]) || sharedPreferenceManager.get_user_id().equalsIgnoreCase(list[i])) {
                        sharedPreferenceManager.set_keep_login(false);
                        startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, LoginActivity.class));
                        OfflineConstituencyDashBoardActivity.this.finish();
                        break;
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()

                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);


        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.w(TAG, "Task failure :");
                        }
                        initialize();
                    }
                });
    }


    private void logoutUser() {
        startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

        OfflineConstituencyDashBoardActivity.this.finish();
    }


    private boolean checkExpiryDate() {

        String currentDate = get_current_date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date current = format.parse(currentDate);
            Date expiryDate = format.parse(sharedPreferenceManager.getExpiryDate());

            if (current.after(expiryDate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        if (!sharedPreferenceManager.getLanguage().isEmpty()) {
            changeLanguage(sharedPreferenceManager.getLanguage());

        }


       /* if (!sharedPreferenceManager.getLanguage().isEmpty()) {
            changeLanguage(sharedPreferenceManager.getLanguage());
        }*/
        //  sharedPreferenceManager.set_keep_login_role("Client");
        //sharedPreferenceManager.set_user_type("d2d");

        //dSYQOdhlF_8:APA91bH1h9FBLFBjF8Vs0TXmUAe-0bVQaVs6gt42RLE8Xr5QsfGvhHogH7Au1OwXJdOLoc-cBrrrPMx2svdgqeAh8JPtaeBFR-Wx-hHQoARm7OmKqIBiA8opFixGv-H4HTzgQISuSyP_
        if (!sharedPreferenceManager.get_keep_login()) {
            // startActivity(new Intent(LoginActivity.this, QCUsersActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

            logoutUser();

        } else {

            if (sharedPreferenceManager.getUserType().equalsIgnoreCase("pc")) {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, FamilyDataActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                OfflineConstituencyDashBoardActivity.this.finish();
            } else {

                setContentView(R.layout.activity_offlineconstituenxy_dashboard);
                ButterKnife.bind(this);
                setup_toolbar(toolbar, toolbartitle);

                dialog = show_dialog(OfflineConstituencyDashBoardActivity.this, false);

                db = new MyDatabase(this);
                votersDatabase = new VotersDatabase(this);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                }


                //   get_question();


                if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {

                    // if (!sharedPreferenceManager.get_project_id().equalsIgnoreCase("3")){

                    if (checkExpiryDate()) {
                        sharedPreferenceManager.set_keep_login(false);
                        sharedPreferenceManager.set_keep_login_role("");
                        db.delete_question();
                        Toastmsg(OfflineConstituencyDashBoardActivity.this, "You are no longer valid to access this app");
                        logoutUser();
                    }

                    // }

                    if (checkInternet.isConnected()) {
                        new getBoothDetails().execute();
                    }
                    //l̥ }
                } else {
                    try {
                        Cursor c = db.getBooths();
                        if (c.getCount() == 0) {
                            if (checkInternet.isConnected()) {
                                new getBoothDetails().execute();
                            }
                        } else {
                            Log.w(TAG, "Cursor not empty");
                        }

                        c.close();
                    } catch (CursorIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                loadHomeMenu();


                hideStatistics();

                initializeRemoteConfig();

                AWSMobileClient.getInstance().initialize(this, new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails result) {
                        Log.w(TAG, "AWSMobileClient initialized. User State is " + result.getUserState());

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.w(TAG, "Initialization error.", e);
                    }
                });


            }
        }

    }

    private void hideStatistics() {
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();

                hideInfoLayout();
                return true;
            }
        });

        mainLayout.performClick();


    }

    private void hideInfoLayout() {
        if (infoLayout.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(OfflineConstituencyDashBoardActivity.this, R.anim.menu_hide_anim);
            infoLayout.startAnimation(anim);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideView(infoLayout);
                }
            }, 400);

            hideView(imgTriangle);
            imgShowData.setImageDrawable(ContextCompat.getDrawable(OfflineConstituencyDashBoardActivity.this, R.mipmap.ic_keyboard_arrow_left_white_24dp));
        }
    }

    @BindView(R.id.imgShowData)
    ImageView imgShowData;

    @OnClick(R.id.fab_addvoter)
    public void add_new_voter() {

        if (isUpoadingData) {
            Toastmsg(OfflineConstituencyDashBoardActivity.this, dataUploading);
        } else {

            if (checkPermissionGranted(Constants.LOCATION_PERMISSION, this)) {

                if (!is_enabled_gps(OfflineConstituencyDashBoardActivity.this)) {

                    mEnableGps();
                    // showSettingsAlert(OfflineConstituencyDashBoardActivity.this);
                } else {
                    if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.RECORD_AUDIO, this)) {
                        startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, AddVoterActivity.class).putExtra("userType", "d2d"));
                    } else {
                        askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.RECORD_AUDIO});
                    }
                }
            } else {
                askPermission(new String[]{Constants.LOCATION_PERMISSION});
            }
        }
    }

    @BindView(R.id.infoLayout)
    RelativeLayout infoLayout;
    @BindView(R.id.triangle)
    ImageView imgTriangle;


    @BindView(R.id.txtAllocatedBooths)
    TextView txtAllocateBooths;

    @OnClick(R.id.imgShowData)
    public void showInfo() {
        if (infoLayout.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.menu_hide_anim);
            infoLayout.startAnimation(anim);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideView(infoLayout);
                }
            }, 400);

            hideView(imgTriangle);
            imgShowData.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_keyboard_arrow_left_white_24dp));
        } else {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.menu_anim);
            infoLayout.startAnimation(anim);
            if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker") || sharedPreferenceManager.get_keep_login_role().equals("Supervisor")) {
                getInfo();
            } else {
                showView(imgTriangle);
                showView(infoLayout);
            }
            imgShowData.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_keyboard_arrow_right_black_24dp));
        }
    }

    //get info details
    private void getInfo() {

        showView(imgTriangle);
        showView(infoLayout);
        Log.w(TAG, "booth size  " + booth_list.size());
        if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker") || sharedPreferenceManager.get_keep_login_role().equals("Supervisor")) {
            if (booth_list.size() > 0) {
                addSurveyInfo();
            } else {

                try {
                    Cursor c = db.getBooths();
                    if (c.moveToFirst()) {
                        do {
                            booth_list.add(c.getString(0));
                        } while (c.moveToNext());
                    }
                    if (booth_list.size() > 0) {
                        addSurveyInfo();
                    } else {
                        txtAllocateBooths.setText(noBoothAllocated);
                        txtNoOFSurveysToComplete.setText(numYetToComplete + "0");
                        int surveyCount = db.get_survey_count();
                        //number of survey's done
                        String values = numOfDoneSurvey + surveyCount;
                        txtNoOfSurveysDone.setText(values);
                    }

                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }


        } else {
            int surveyCount = db.get_survey_count();
            //number of survey's done
            String values = numOfDoneSurvey + surveyCount;
            txtNoOfSurveysDone.setText(values);
            txtAllocateBooths.setText(noBoothAllocated);
            hideView(boothRecyclerView);

            if (sharedPreferenceManager.getUserType().equalsIgnoreCase("d2d")) {
                try {
                    Cursor yetToComplete = votersDatabase.getCount();
                    yetToComplete.moveToFirst();
                    int count = yetToComplete.getInt(0) - surveyCount;
                    String yetCompleteCount = numYetToComplete + count;
                    txtNoOFSurveysToComplete.setText(yetCompleteCount);

                    yetToComplete.close();
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    String yetCompleteCount = numYetToComplete + "0";
                    txtNoOFSurveysToComplete.setText(yetCompleteCount);
                }

            }
        }
    }


    private void addSurveyInfo() {
        Log.w(TAG, "booth size  inside party worker");
        setBoothInfoadapter();

        int surveyCount = db.get_survey_count();

        String boothNames = "";
        for (String name : booth_list) {
            name = name.replaceAll("-", "_");
            boothNames += "'" + name + "',";
            //  boothNames.append(",");
        }
        //number of survey's done
        String values = numOfDoneSurvey + surveyCount;
        txtNoOfSurveysDone.setText(values);

        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("d2d")) {
            try {
                Cursor yetToComplete = votersDatabase.getCount(boothNames.substring(0, boothNames.length() - 1));
                if (yetToComplete != null) {
                    yetToComplete.moveToFirst();

                    int count = yetToComplete.getInt(0) - surveyCount;
                    String yetCompleteCount = numYetToComplete + count;
                    txtNoOFSurveysToComplete.setText(yetCompleteCount);
                    yetToComplete.close();
                }

            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
                String yetCompleteCount = numYetToComplete + " 0";
                txtNoOFSurveysToComplete.setText(yetCompleteCount);
            }
        }
    }


    ArrayList<String> booth_list = new ArrayList<>();

    //set adapter for booth info
    private void setBoothInfoadapter() {

        showView(boothRecyclerView);
        // boothRecyclerView.setAdapter(null);
        if (boothInfoAdapter == null) {
            boothRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            boothRecyclerView.setHasFixedSize(true);
            boothInfoAdapter = new BoothInfoAdapter(booth_list, this);
            boothRecyclerView.setAdapter(boothInfoAdapter);
        }
    }

    BoothInfoAdapter boothInfoAdapter;

    private void showView(View view) {
        if (!view.isShown()) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hideView(View view) {
        if (view.isShown()) {
            view.setVisibility(View.GONE);
        }
    }

    //change data after refreshing
    private void changeValueAfterRefreshing() {
        isRefreshing = false;
        // statisticsLayout.setVisibility(View.VISIBLE);
        //  progressBar.setVisibility(View.GONE);
        //  imgRefresh.clearAnimation();
    }

    //get survey data for maps
   /* private void getTheatreSurveyDetails() {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_SURVEY_MAP + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "Map response :" + response);
                try {
                    theatreStatisticsArray = new JSONArray(response);
                    int theatreSurveyCount = 0, peopleSurveyCount = 0;

                    for (int i = 0; i <= theatreStatisticsArray.length() - 1; i++) {
                        JSONObject obj = theatreStatisticsArray.getJSONObject(i);

                        if (obj.getString("surveytype").equals("People opinion")) {
                            peopleSurveyCount++;
                        } else if (obj.getString("surveytype").equals("Theatre")) {
                            theatreSurveyCount++;
                        }
                    }

                    updateTheatreStatistics(theatreSurveyCount + "", peopleSurveyCount + "", (theatreSurveyCount + peopleSurveyCount) + "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
                changeValueAfterRefreshing();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }*/

    //update theatre statistics
 /*   private void updateTheatreStatistics(String theatre, String people, String total) {

        showView();
        txtTheatreTotal.setText(theatre);
        txtPeopleTotal.setText(people);
        txtTotal.setText(total);

        setBackground(txtTheatreTotal);
        setBackground(txtPeopleTotal);
        setBackground(txtTotal);

        changeValueAfterRefreshing();
    }
*/
 /*   private void showView() {
        if (!theatreStaistics.isShown()) {
            theatreStaistics.setVisibility(View.VISIBLE);
        }
    }*/


    private void setBackground(CircularTextView textView) {
        textView.setStrokeWidth(1);
        textView.setStrokeColor("#ffffff");
        textView.setSolidColor("#0087d8");
        // textView.setSolidColor("#CABBBBBB");
    }

    //call to get constituency stats
    private void callConstituencyStatsApi() {

        Log.w(TAG, "Constituency stats api called");

        if (checkInternet.isConnected()) {
            Log.w(TAG, "Constituency stats api called inside network connected");

            new get_constituencystats().execute();
        }
    }

    //lock drawer
    private void lock_drawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            //scheduler.cancelAll();

            List<JobInfo> pendingJobs = scheduler.getAllPendingJobs();
            Log.w(TAG, "pending jobs :" + pendingJobs.size());
            int count = getSurveyCount();
            if (pendingJobs.size() == 0 && count >= 1) {
                createJob();
            }
        }
*/

    }

    @BindString(R.string.search)
    String search;
    @BindString(R.string.conductSuvey)
    String conductSurvey;
    @BindString(R.string.casteEquation)
    String casteEquaion;
    @BindString(R.string.events)
    String events;
    @BindString(R.string.opinionPoll)
    String opinionPoll;
    @BindString(R.string.userManagement)
    String userManagement;
    @BindString(R.string.bsk)
    String bsk;
    @BindString(R.string.gotv)
    String gotv;

    @BindString(R.string.exitpoll)
    String exitPoll;

    @BindString(R.string.listenToPeople)
    String listenToPeople;

    @BindString(R.string.maps)
    String maps;

    @BindString(R.string.enquire_people)
    String enquirePeople;

    @BindString(R.string.surveyStats)
    String surveyStats;

    @BindString(R.string.summary)
    String summary;

    @BindString(R.string.qc)
    String qc;

    @BindString(R.string.getSurveyData)
    String getSurveydata;

    @BindString(R.string.statistics)
    String statistics;

    @BindString(R.string.communication)
    String communication;

    @BindString(R.string.grievanceManagement)
    String grienvanceManagement;


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    //load home menu data
    private void loadHomeMenu() {

        String role = sharedPreferenceManager.get_keep_login_role();
        String userType = sharedPreferenceManager.getUserType();
        Log.w(TAG, "Role and user type  : " + role + "  " + userType);

        if (role.equals("Client") && userType.equalsIgnoreCase("d2d")) {


            list.add(new MainMenuDetails(R.drawable.search2, search, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            // list.add(new MainMenuDetails(R.drawable.survey_data2, conductSurvey, false, 0));
            // list.add(new MainMenuDetails(R.drawable.caste_equation2, casteEquaion, false, 0));
            list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            list.add(new MainMenuDetails(R.drawable.event2, events, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));

            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.map, liveTrack, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, mgmtActivities, false, 0));
            //    list.add(new MainMenuDetails(R.drawable.summary, beneficiaryManagement, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.summary, grienvanceManagement, false, 0));

            //  list.add(new MainMenuDetails(R.drawable.map, maps));

            //list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata, false, 0));
            list.add(new MainMenuDetails(R.drawable.election_history2, gotv, false, 0));
            // list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));


            list.add(new MainMenuDetails(R.drawable.communication2, communication, false, 0));
            list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata, false, 0));
            // list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));

            //  role.equals("Client") && ((userType.equals("d2d") || userType.equals("opcp"))) list.add(new MainMenuDetails(R.drawable.election_history2, gotv, false, 0));

            initiate_drawer();


        } else if (role.equals("Client") && userType.equalsIgnoreCase("opcp")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.event2, events, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));
            list.add(new MainMenuDetails(R.drawable.communication2, communication, false, 0));

            initiate_drawer();

        } else if (role.equals("Manager") && userType.equals("d2d")) {

            list.add(new MainMenuDetails(R.drawable.search2, search, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));

            // list.add(new MainMenuDetails(R.drawable.survey_data2, conductSurvey, false, 0));
            //    list.add(new MainMenuDetails(R.drawable.caste_equation2, casteEquaion, false, 0));
            list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.map, maps));
            // list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata, false, 0));
            list.add(new MainMenuDetails(R.drawable.election_history2, gotv, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            //   list.add(new MainMenuDetails(R.drawable.election_history2, gotv, false, 0));
            //  name_list.add(enquirePeople);
            // name_list.add(gotv);
            //image_ids = new int[]{R.drawable.search2, R.drawable.survey_data2, R.drawable.grievience2, R.drawable.caste_equation2, R.drawable.benificiary2, R.drawable.election_history2};

            //  initiate_drawer();

            lock_drawer();

        } else if (role.equals("Supervisor") && userType.equals("d2d")) {

            list.add(new MainMenuDetails(R.drawable.search2, search, false, 0));
            // list.add(new MainMenuDetails(R.drawable.survey_data2, conductSurvey, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata, false, 0));
            list.add(new MainMenuDetails(R.drawable.election_history2, gotv, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            // list.add(new MainMenuDetails(R.drawable.election_history2, qc, false, 0));
            lock_drawer();


        } else if (role.equals("Party Worker") && userType.equals("d2d")) {

            //  name_list.add(search);
            //name_list.add(conductSurvey);
            list.add(new MainMenuDetails(R.drawable.search2, search, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            // list.add(new MainMenuDetails(R.drawable.survey_data2, conductSurvey, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata, false, 0));
            list.add(new MainMenuDetails(R.drawable.election_history2, gotv, false, 0));

            lock_drawer();


        } else if (role.equals("Manager") && userType.equalsIgnoreCase("opcp")) {

            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            //    list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            // list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            //list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata));
            //name_list.add(enquirePeople);
            //  initiate_drawer();

        } else if (role.equals("Supervisor") && userType.equalsIgnoreCase("opcp")) {

            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            //list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            //  list.add(new MainMenuDetails(R.drawable.election_history2, qc, false, 0));
            // name_list.add(enquirePeople);
            lock_drawer();

        } else if (role.equals("Party Worker") && userType.equalsIgnoreCase("opcp")) {
           /* name_list.add(opinionPoll);
            image_ids = new int[]{R.drawable.election_history2, R.drawable.benificiary2};
            lock_drawer();*/
            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            lock_drawer();

          /*  startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, OpinionPollActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            OfflineConstituencyDashBoardActivity.this.finish();*/

        } else if (role.equals("Client") && userType.equalsIgnoreCase("kp")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            // list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));

            //   list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.map, liveTrack, false, 0));
            list.add(new MainMenuDetails(R.drawable.communication2, communication, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            hideAddVoter();
            initiate_drawer();
        } else if (role.equals("Manager") && userType.equalsIgnoreCase("kp")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            // list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            // list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            //   list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            //list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata));
            //name_list.add(enquirePeople);
            hideAddVoter();
            lock_drawer();
            callConstituencyStatsApi();
        } else if (role.equals("Supervisor") && userType.equalsIgnoreCase("kp")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            //list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            //   list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            //   list.add(new MainMenuDetails(R.drawable.election_history2, qc, false, 0));
            // name_list.add(enquirePeople);
            hideAddVoter();
            lock_drawer();

        } else if (role.equals("Party Worker") && userType.equalsIgnoreCase("kp")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            hideAddVoter();

            lock_drawer();
        } else if (role.equals("Client") && userType.equalsIgnoreCase("ep")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, exitPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            // list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));

            //   list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.map, liveTrack, false, 0));
            list.add(new MainMenuDetails(R.drawable.communication2, communication, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            hideAddVoter();
            initiate_drawer();
        } else if (role.equals("Manager") && userType.equalsIgnoreCase("ep")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, exitPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            // list.add(new MainMenuDetails(R.drawable.caste_equation2, summary, false, 0));
            // list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            //   list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            //list.add(new MainMenuDetails(R.drawable.get_survey2, getSurveydata));
            //name_list.add(enquirePeople);
            hideAddVoter();
            lock_drawer();
            callConstituencyStatsApi();
        } else if (role.equals("Supervisor") && userType.equalsIgnoreCase("ep")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, exitPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            //list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            //   list.add(new MainMenuDetails(R.drawable.summary, statistics, false, 0));

            //   list.add(new MainMenuDetails(R.drawable.election_history2, qc, false, 0));
            // name_list.add(enquirePeople);
            hideAddVoter();
            lock_drawer();

        } else if (role.equals("Party Worker") && userType.equalsIgnoreCase("ep")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, exitPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            list.add(new MainMenuDetails(R.drawable.sync_online2, syncOnline, false, 0));
            list.add(new MainMenuDetails(R.drawable.mic, teleCalling, false, 0));
            hideAddVoter();

            lock_drawer();
        } else if (role.equalsIgnoreCase("OPM") && userType.equalsIgnoreCase("opcp")) {

            list.add(new MainMenuDetails(R.drawable.election_history2, opinionPoll, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.listen_to_people, listenToPeople, false, 0));
            //  list.add(new MainMenuDetails(R.drawable.map, maps));
            list.add(new MainMenuDetails(R.drawable.summary, surveyStats, false, 0));
            initiate_drawer();
        } else if (userType.equals("TSP") && role.equals("Manager")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, films, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            lock_drawer();
        } else if (userType.equals("TSP") && role.equals("Supervisor")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, films, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            lock_drawer();
            initializeRemoteConfig();
        } else if (userType.equals("TSP") && role.equals("Party Worker")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, films, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            lock_drawer();

        } else if (userType.equals("TSP") && role.equals("Client")) {
            list.add(new MainMenuDetails(R.drawable.election_history2, films, false, 0));
            list.add(new MainMenuDetails(R.drawable.benificiary2, userManagement, false, 0));
            list.add(new MainMenuDetails(R.drawable.map, maps, false, 0));
            lock_drawer();
        }


        set_grid_layout_manager(recyclerView, this, 3);
        set_adapter();


    }


    private void hideAddVoter() {

        fabAddVoter.hide();
    }

    @BindView(R.id.fab_addvoter)
    FloatingActionButton fabAddVoter;

    @BindString(R.string.questionare)
    String questionare;

    //update menu
    private void updateMenu() {

        int count = getSurveyCount();
        if (count > 0) {
            updateMenuImageId(R.drawable.sync_onlinered, count, true);

        } else {
            updateMenuImageId(R.drawable.sync_online2, count, false);
        }


    }

    private int getSurveyCount() {
        int isHaveCount = 0;
        try {

            Cursor c = db.get_voter_atrributes_count();
            Log.w(TAG, "count  : " + c.getCount());

            if (c.getCount() > 0) {
                isHaveCount = c.getCount();
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return isHaveCount;
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


    /*initiate drawer*/
    private void initiate_drawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            private float scaleFactor = 6f;


            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainLayout.setTranslationX(slideOffset * drawerView.getWidth());
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();


                mainLayout.setScaleX(1 - (slideOffset / scaleFactor));
                mainLayout.setScaleY(1 - (slideOffset / scaleFactor));


                //below line used to remove shadow of drawer
                drawerLayout.setScrimColor(Color.TRANSPARENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawerLayout.setElevation(0F);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //  show_child_details_in_drawer();
            }
        };

        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_reorder_white_24dp, getApplicationContext().getTheme());
        mDrawerToggle.setHomeAsUpIndicator(drawable);

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Log.w(TAG, "Drawer close");
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    Log.w(TAG, "Drawer open");
                }
            }
        });

        callConstituencyStatsApi();

    }

    static JSONArray array = null;

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    private class get_survey_data extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected void onPostExecute(JSONArray aVoid) {
            super.onPostExecute(aVoid);

            //  insertSurveyData();

            if (array != null) {
                Log.w(TAG, "array length : " + array.length());
            } else {
                Log.w(TAG, "array length : null");
            }


        }

        @Override
        protected JSONArray doInBackground(Void... params) {

            Log.w(TAG, "Url :" + API.GET_SURVEY_DATA + sharedPreferenceManager.get_constituency_id());


            StringRequest request = new StringRequest(Request.Method.GET, API.GET_SURVEY_DATA + sharedPreferenceManager.get_constituency_id(), new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    try {
                        array = new JSONArray(response);

                        dialog.dismiss();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.w(TAG, "version less than lollipop");
                                int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

                                ThreadPoolExecutor executor = new ThreadPoolExecutor(
                                        NUMBER_OF_CORES * 2,
                                        NUMBER_OF_CORES * 2,
                                        60L,
                                        TimeUnit.SECONDS,
                                        new LinkedBlockingQueue<Runnable>()
                                );
                                executor.execute(new SurveyInsertData());


                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Log.w(TAG, "Response741 " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Toast.makeText(OfflineConstituencyDashBoardActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error " + error.toString());
                }
            });

            VolleySingleton.getInstance(OfflineConstituencyDashBoardActivity.this).addToRequestQueue(request);
            return array;
        }


    }

    public class SurveyInsertData implements Runnable {


        JSONArray array;

        public SurveyInsertData() {

        }

        @Override
        public void run() {

            insertSurveyData();

        }
    }

    private void insertSurveyData() {
        Log.w(TAG, "insert survey data called : " + array.length());
        if (array != null) {
            try {
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

            isUpdating = false;
        }
    }

    int jobId = 0;


    @BindString(R.string.notAvailable)
    String notAvailable;
    @BindString(R.string.notResponding)
    String notResponding;
    @BindString(R.string.outofStation)
    String outOfStation;
    @BindString(R.string.mobileCount)
    String mobileCount;
    @BindString(R.string.male)
    String male;
    @BindString(R.string.female)
    String female;
    @BindString(R.string.total)
    String total;
    @BindString(R.string.age)
    String age;
    @BindString(R.string.expiredVoters)
    String expiredVoters;
    @BindString(R.string.shiftedVoters)
    String shiftedVoters;
    @BindString(R.string.newVoter)
    String newVoter;

    @BindString(R.string.telecalling)
    String teleCalling;

 /*   @BindView(R.id.imgRefresh)
    ImageView imgRefresh;*/

    /* @BindView(R.id.statisticsLayout)
     CardView statisticsLayout;
 */
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private float lastPosX, lastPosY, rawY, lastRawY;
    float dx, dy;


    boolean isRefreshing = false;
    boolean isScrollDown = false;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //get position for occurate pointer
                dx = view.getX() - motionEvent.getRawX();
                dy = view.getY() - motionEvent.getRawY();

                //get the initial position of x any y
                lastPosX = view.getX();
                lastPosY = view.getY();

                rawY = motionEvent.getRawY();
                lastRawY = motionEvent.getY();
                return true;
            case MotionEvent.ACTION_UP:
                view.setX(lastPosX);
                view.setY(lastPosY);
                if (isScrollDown) {
                    if (!isRefreshing) {
                        if (checkInternet.isConnected()) {
                            playAudio();
                            progressBar.setVisibility(View.VISIBLE);
                            isRefreshing = true;
                            // statisticsLayout.setVisibility(View.INVISIBLE);
                            //getTheatreSurveyDetails();
                        }

                    }

                    isScrollDown = false;
                }

                return true;
            case MotionEvent.ACTION_MOVE:

                //  float finalX = motionEvent.getX();
                float finalY = motionEvent.getY();


                if (lastRawY < finalY) {
                    isScrollDown = true;
                    view.setX(motionEvent.getRawX() + dx);
                    view.setY(motionEvent.getRawY() + dy);
                    Log.w(TAG, "up to down working");
                    return true;
                } else {
                    return false;
                }

            default:
                return false;
        }
    }

    private MediaPlayer mp;

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
            mp = MediaPlayer.create(this, R.raw.refresh);
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

    private class get_constituencystats extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            get_constituency_stats();
            return null;
        }


    }

    private static final String CRYPT_IV = "l/q6QiOm/UyThxIV";
    private static final String CRYPT_KEY = "ek7vJUAA50OmcMsTm2d7XcPZ+1W46J7J";


    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : "0");
    }

    //get constituency stats
    private void get_constituency_stats() {

        Log.w(TAG, " api called ");

        StringRequest request = new StringRequest(Request.Method.GET, API.CONSTITUENCY_STATS + sharedPreferenceManager.get_constituency_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "Response " + response);
                try {
                    JSONArray array = new JSONArray(response);


                    if (array.length() > 0) {

                        JSONObject object = array.getJSONObject(0);
                        Iterator iterator = object.keys();

                        NavigationItems item;
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            switch (key) {
                                case "male":
                                    setMenuCounter(R.id.male, object.getInt(key));

                                    break;
                                case "female":
                                    setMenuCounter(R.id.female, object.getInt(key));

                                    break;
                                case "total":
                                    setMenuCounter(R.id.total, object.getInt(key));

                                    break;
                                case "firstbatch":
                                    setMenuCounter(R.id.age18, object.getInt(key));

                                    break;
                                case "secondbatch":
                                    setMenuCounter(R.id.age25, object.getInt(key));

                                    break;
                                case "thirdbatch":
                                    setMenuCounter(R.id.age36, object.getInt(key));

                                    break;
                                case "lastbatch":
                                    setMenuCounter(R.id.age55, object.getInt(key));

                                    break;

                                case "Expired":
                                    setMenuCounter(R.id.expiredVoters, object.getInt(key));

                                    break;
                                case "Shifted":
                                    setMenuCounter(R.id.shiftedVoters, object.getInt(key));

                                    break;
                                case "NewVoters":
                                    setMenuCounter(R.id.newVoter, object.getInt(key));

                                    break;
                                case "NotAvailble":
                                    setMenuCounter(R.id.notAvailable, object.getInt(key));

                                    break;
                                case "NotResponding":
                                    setMenuCounter(R.id.notResponding, object.getInt(key));

                                    break;
                                case "OutofStation":
                                    setMenuCounter(R.id.outOfStation, object.getInt(key));

                                    break;
                                case "mobilecount":
                                    setMenuCounter(R.id.mobileCount, object.getInt(key));
                                    break;

                                case "TotalSurvey":

                                    txtAllocateBooths.setText(noBoothAllocated);
                                    int surveyCount = object.getInt(key);

                                    if (sharedPreferenceManager.getUserType().equalsIgnoreCase("d2d")) {
                                        try {
                                            Cursor yetToComplete = votersDatabase.getCount();
                                            yetToComplete.moveToFirst();
                                            int count = yetToComplete.getInt(0) - surveyCount;
                                            String yetCompleteCount = numYetToComplete + count;
                                            txtNoOFSurveysToComplete.setText(yetCompleteCount);

                                            yetToComplete.close();
                                        } catch (CursorIndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                            String yetCompleteCount = numYetToComplete + " 0";
                                            txtNoOFSurveysToComplete.setText(yetCompleteCount);
                                        }
                                    }

                                    // txtNoOFSurveysToComplete.setText("Number of survey's yet to complete : 0");
                                    //number of survey's done
                                    String values = numOfDoneSurvey + surveyCount;
                                    txtNoOfSurveysDone.setText(values);
                                    break;
                                default:
                                    break;

                            }


                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //  txt_no_data.setVisibility(View.VISIBLE);
                Log.w(TAG, "Error " + error.toString());
            }
        });


        VolleySingleton.getInstance(this).addToRequestQueue(request);


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


    private void get_booth_details() {
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

                        Log.w(TAG, "Name :" + object.getString("name"));
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
        adapter = new MainMenuAdapter(this, list, OfflineConstituencyDashBoardActivity.this);
        recyclerView.setAdapter(adapter);
    }

    public void goto_next_activity(String position) {

        hideInfoLayout();
        if (position.equals(search)) {

            if (is_enabled_gps(OfflineConstituencyDashBoardActivity.this)) {
                // startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, FamilyDataActivity.class).putExtra("bskMode", false));

                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, VoterSearchActivity.class).putExtra("bskMode", false));
            } else {
                mEnableGps();
            }

        } else if (position.equals(conductSurvey)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, ConductSurveyActivity.class).putExtra("bskMode", false));

        } else if (position.equals(casteEquaion)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, CasteEquationActivity.class).putExtra("url", ""));
        } else if (position.equals(events)) {

            if (checkPermissionGranted(Constants.READ_CONTACTS, this)) {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, TimeTableActivity.class));
            } else {
                askPermission(new String[]{Constants.READ_CONTACTS});
            }


        } else if (position.equals(userManagement)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, UserManagementActivity.class));
        } else if (position.equals(opinionPoll)) {
            Log.w(TAG, "Wrong selection");
            if (isPermissionsGranted(OfflineConstituencyDashBoardActivity.this, new String[]{
                    Constants.LOCATION_PERMISSION})) {

                if (is_enabled_gps(OfflineConstituencyDashBoardActivity.this)) {
                    startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, OpinionPollActivity.class));
                } else {
                    mEnableGps();
                }
            } else {
                askPermission(new String[]{Constants.LOCATION_PERMISSION});
            }
        } else if (position.equals(opinionPoll)) {
            //  startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, OpinionPollActivity.class));
            if (isPermissionsGranted(OfflineConstituencyDashBoardActivity.this, new String[]{
                    Constants.LOCATION_PERMISSION})) {

                if (is_enabled_gps(OfflineConstituencyDashBoardActivity.this)) {
                    startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, OpinionPollActivity.class));
                } else {
                    mEnableGps();
                }
            } else {
                askPermission(new String[]{Constants.LOCATION_PERMISSION});
            }
        } else if (position.equals(listenToPeople)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, ListenToPeopleActivity.class));
        } else if (position.equals(maps)) {

            if (checkPermissionGranted(Constants.LOCATION_PERMISSION, this)) {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, TrackSurveyActivity.class));
            } else {
                askPermission(new String[]{Constants.LOCATION_PERMISSION});
            }
           /* } else {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, TrackSurveyActivity.class));
            }*/
        } else if (position.equals(enquirePeople)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, EnquirePeopleActivity.class));
        } else if (position.equals(surveyStats)) {
            if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, SurveyStatsActivity.class));
            } else {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, QCUsersActivity.class));
            }
        } else if (position.equals(getSurveydata)) {

            if (checkInternet.isConnected()) {
                if (!isUpdating) {
                    isUpdating = true;
                    new get_survey_data().execute();
                } else {
                    Toastmsg(OfflineConstituencyDashBoardActivity.this, updatingBackground);
                }
            } else {
                Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
            }

        } else if (position.equals(syncOnline)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                scheduler.cancelAll();
            }
            if (checkInternet.isConnected()) {
                surveyDetailsList.clear();
                upload_to_server(true);
            } else {
                Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
            }
        } else if (position.equals(gotv)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, GOTVActivity.class));

          /*  if (sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Client")) {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, GOTVStatistics.class));

            } else {
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, GOTVActivity.class));
            }*/
        } else if (position.equalsIgnoreCase(summary)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, ConstituencySummaryActivity.class));
        } else if (position.equalsIgnoreCase(questionare)) {
            if (checkPermissionGranted(Constants.LOCATION_PERMISSION, this)) {

                if (!is_enabled_gps(OfflineConstituencyDashBoardActivity.this)) {

                    mEnableGps();
                    // showSettingsAlert(ConductSurveyActivity.this);
                } else {
                    if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.RECORD_AUDIO, this)) {

                        startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, QuestionareActivity.class).putExtra("userType", "kp").putExtra("boothName", "NotAvailable"));

                    } else {
                        askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.RECORD_AUDIO});
                    }
                }
            } else {
                askPermission(new String[]{Constants.LOCATION_PERMISSION});
            }
        } else if (position.equalsIgnoreCase(qc)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, QCUsersActivity.class));
        } else if (position.equalsIgnoreCase(statistics)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, QCUsersActivity.class));
        } else if (position.equalsIgnoreCase(liveTrack)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, LiveTrackActivity.class));
        } else if (position.equalsIgnoreCase(communication)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, CommunicationActivity.class));
        } else if (position.equalsIgnoreCase(mgmtActivities)) {
            showAlertForManagementActivities();

        } else if (position.equalsIgnoreCase(teleCalling)) {
            startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, SurveyStatsActivity.class));

        } else if (position.equalsIgnoreCase(exitPoll)) {
            if (is_enabled_gps(OfflineConstituencyDashBoardActivity.this)) {

                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, DummyEVMActivity.class));
            } else {
                mEnableGps();
            }

        }

    }

    ExportDatabase ex_db;

    @BindString(R.string.noInternet)
    String noInternet;

    @BindString(R.string.mgmtActivities)
    String mgmtActivities;

    @BindString(R.string.updatingBackground)
    String updatingBackground;

    @BindString(R.string.noImagefound)
    String noImageFound;

    @BindString(R.string.successfullyUploaded)
    String successfullyUploaded;


    public void showAlertForManagementActivities() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alert_layout, null);


        TextView txtFund = alertLayout.findViewById(R.id.txtFundManagement);
        TextView txtBeneficiary = alertLayout.findViewById(R.id.txtBeneficiary);
        TextView txtGrievance = alertLayout.findViewById(R.id.txtGrievance);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //    alert.setTitle("Info");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);

        final AlertDialog dialog1 = alert.create();
        dialog1.show();

        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        txtFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog1.dismiss();
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, FundManagmentActivity.class).putExtra("title", fundManagement));


            }
        });


        txtBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, FundManagmentActivity.class).putExtra("title", beneficiaryManagement));
                dialog1.dismiss();
            }
        });

        txtGrievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog1.dismiss();
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, GrievanceActivity.class));

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem addvoter = menu.findItem(R.id.addVoter);
        MenuItem pastHistory = menu.findItem(R.id.pastElection);
        MenuItem evm = menu.findItem(R.id.more);
        MenuItem kannada = menu.findItem(R.id.kannada);

        if (!sharedPreferenceManager.getUserType().equalsIgnoreCase("d2d")) {
            addvoter.setVisible(false);
            pastHistory.setVisible(false);
        }

        if (!sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Client")) {
            evm.setVisible(false);
        }

        if (sharedPreferenceManager.get_project_id().equalsIgnoreCase("16")) {
            kannada.setVisible(false);
        }

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.liteUpload:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    scheduler.cancelAll();
                }
                if (checkInternet.isConnected()) {
                    surveyDetailsList.clear();
                    upload_to_server(false);
                } else {
                    Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
                }
                return true;

            case R.id.pastElection:
                startActivity(new Intent(this, GOTVStatistics.class).putExtra("isStatistics", false));
                return true;

            case R.id.logout:

                if (sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Party Worker")) {

                    if (checkInternet.isConnected()) {

                        dialog.show();
                        getLoginStatus(sharedPreferenceManager.get_username());

                    } else {
                        Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
                    }

                } else {
                    sharedPreferenceManager.set_keep_login(false);
                    sharedPreferenceManager.set_keep_login_role("");
                    db.delete_question();
                    logoutUser();
                }
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
            case R.id.upload:

                doHardUpload();
                return true;

            case R.id.re_sync:
                Toastmsg(OfflineConstituencyDashBoardActivity.this, processing);
                db.getResyncData();
                updateMenu();
                return true;
            case R.id.addVoter:
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, AddVoterActivity.class));
                return true;

            case R.id.evm:
                startActivity(new Intent(OfflineConstituencyDashBoardActivity.this, DummyEVMActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void doHardUpload() {
        try {

            if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {
                if (!hardUpload) {
                    if (checkInternet.isConnected()) {
                        Toastmsg(OfflineConstituencyDashBoardActivity.this, dataUploading);
                        String file_name;
                        file_name = sharedPreferenceManager.get_user_id() + "_" + sharedPreferenceManager.get_constituency_id() + "_" + get_current_date() + "_" + get_current_time() + ".db";
                        file_name = file_name.replaceAll(" ", "_");

                        ex_db = new ExportDatabase(this, file_name);

                        get_hard_upload_data(file_name);
                    } else {
                        Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
                        alert_for_no_internet();
                    }
                }
            } else {
                askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean hardUpload = false;

    //get upload data
    private void get_hard_upload_data(String file_name) {


        int count = 0;


        try {
            Cursor c = db.get_all_survey_data();

            count = c.getCount();

            if (c.moveToFirst()) {

                do {
                    boolean is_sync = Boolean.parseBoolean(c.getString(5));
                    VoterAttribute attribute = new VoterAttribute(c.getString(1), c.getString(2), c.getString(3), c.getString(4), is_sync, c.getString(6));
                    ex_db.insert_voter_attribute(attribute);

                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (count > 0) {

            hardUpload = true;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/" + file_name);
            if (file.exists()) {
                dialog.show();
                S3Utils.getInstance(OfflineConstituencyDashBoardActivity.this).upload_export_db(file, Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.SURVEY_PATH, dialog);
                // upload_export_db(file);
                hardUpload = false;
            } else {
                hardUpload = false;
                //dismiss_dialog();
                Toastmsg(OfflineConstituencyDashBoardActivity.this, errorUploading);


            }

        } else {
            hardUpload = false;
            Toastmsg(OfflineConstituencyDashBoardActivity.this, noRecordFound);
        }


    }

    @BindString(R.string.errorUploading)
    String errorUploading;

    /*refresh activity*/
    private void refreshActivity() {
        Intent i = new Intent(OfflineConstituencyDashBoardActivity.this, OfflineConstituencyDashBoardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("languageChanged", true);
        startActivity(i);
    }


    /*get booth details*/
    private void getBooths() {
        StringRequest request = new StringRequest(Request.Method.GET, API.BOOTH_INFO + "userId=" + sharedPreferenceManager.get_user_id() + "&constituencyId=" + sharedPreferenceManager.get_constituency_id() + "&projectId=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


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

                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private class getBoothDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getBooths();
            return null;


        }

    }


    @BindString(R.string.Error)
    String Error;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //check app update
    private void checkAppUpdate() {
        new CheckNewAppVersion(this).setOnTaskCompleteListener(new CheckNewAppVersion.ITaskComplete() {
            @Override
            public void onTaskComplete(CheckNewAppVersion.Result result) {

                //Checks if there is a new version available on Google Play Store.
                result.hasNewVersion();
                Log.w(TAG, "has new version : " + result.hasNewVersion());

                Log.w(TAG, "has new version : " + result.getNewVersionCode());

                //Get the new published version code of the app.
                // result.getNewVersionCode();

                //Get the app current version code.
                // result.getOldVersionCode();

                if (result.hasNewVersion()) {
                    result.openUpdateLink();
                }
                //Opens the Google Play Store on your app page to do the update.
                //
            }
        }).execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);

        updateMenu();
        //  checkAppUpdate();

        //  getLocation();
    }

    @BindString(R.string.gpsSettings)
    String gpsSettings;
    @BindString(R.string.gpsmsg)
    String gpsmsg;

    @Override
    protected void onDestroy() {
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }

    private JSONArray get_upload_data(boolean isSyncOnline) {
        JSONArray payload = new JSONArray();
        try {
            Cursor c = null;
            if (isSyncOnline) {
                if (isUploadAgain) {

                    c = db.get_unsync_data();
                } else {
                    c = db.get_voter_atrributes();
                }
            } else {
                c = db.get_unsync_data();
            }
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        try {
                            if (c.getString(4).equalsIgnoreCase("false")) {
                                JSONObject object = getJSONForBulkUpload(c.getString(1), c.getString(2), c.getString(3));
                                Log.w(TAG, "csa 590 " + object);
                                payload.put(object);

                            }
                            if (c.getString(4).equalsIgnoreCase("false") || c.getString(4).equalsIgnoreCase("intermediate")) {
                                addDataToSurveyList(c.getString(3));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w(TAG, "csa 601 " + payload);
        return payload;
    }

    private void addDataToSurveyList(String data) {

        Log.w(TAG, "data : " + data);
        try {

            JSONObject object = new JSONObject(data);
            String date = "";

            try {
                date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date(object.getString("surveyDate")));
                //Log.w(TAG, "date  :" + date);
            } catch (Exception e) {
                date = object.getString("surveyDate");
            }
            //   Log.w(TAG, "pwname : " + object.getString("pwname") + "audioFileName : " + object.getString("audioFileName"));
            surveyDetailsList.add(new SurveyDetails(object.getString("surveyid"),
                    object.getString("latitude"), object.getString("longitude"),
                    object.getString("audioFileName"), date,
                    object.getString("partyWorker"), object.getString("userType"),
                    object.getString("projectId"), object.getString("booth_name"), object.getString("surveyType"), object.getString("respondantname"), object.getString("mobile"), object.getString("pwname")));

        } catch (Exception e) {
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

            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    BroadcastReceiver myReceiver;

    private int file_count = 0;

    private void clearAll() {
        isUpoadingData = false;
        progressLayout.setVisibility(GONE);
        txtUploadData.setText("");
        txtUploadFinish.setText("");
        progressBar.setProgress(0);
        file_count = 0;
        file = null;
        S3_RICHASSETS_FOLDER = "richassets";

    }

    @BindView(R.id.txtUploadFinish)
    TextView txtUploadFinish;

    @BindString(R.string.uploading)
    String uploading;

    @BindString(R.string.gettingFiles)
    String gettingFiles;

    private TransferObserver transferObserver;


    private static final String S3_BUCKETNAME = "rn-assets";
    private static final String S3_CONSTITUENCIES_FOLDER = "constituencies";
    private String S3_RICHASSETS_FOLDER = "richassets";
    private static final String S3_CONSTITUENCY_DESTINATION_FOLDER = "offline/data/";
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;


    private void againCallUploadToServer(boolean isSyncOnline) {

        Log.w(TAG, "again call to upload working");
        if (checkInternet.isConnected()) {
            upload_to_server(isSyncOnline);
        } else {
            Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
        }
    }


    private void upload_to_server(boolean isSyncOnline) {


        //  surveyDetailsList.clear();
        JSONObject object = new JSONObject();
        final JSONArray array = get_upload_data(isSyncOnline);
        //array.get
        try {
            object.put("payload", array);
            object.put("username", sharedPreferenceManager.get_username());
            object.put("date", get_current_date());
            object.put("constituencyId", sharedPreferenceManager.get_constituency_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toastmsg(OfflineConstituencyDashBoardActivity.this,"CSA 489");
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
                                //      Toastmsg(OfflineConstituencyDashBoardActivity.this, " Successfully Uploaded .");


                                for (int i = 0; i <= array.length() - 1; i++) {
                                    JSONObject dataObj = array.getJSONObject(i);
                                    String data = dataObj.getString("propValue");
                                    JSONObject propValue = new JSONObject(data);
                                    db.update_attribute_status(propValue.getString("surveyid"));

                                }

                                try {
                                    int count = 0;
                                    try {
                                        Cursor c = db.get_unsync_data();
                                        if (c.getCount() > 0) {
                                            count = c.getCount();
                                        }
                                    } catch (CursorIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }

                                    if (count == 0) {
                                        isUploadAgain = false;
                                        progressBar.setProgress(30);
                                        progressBar.setProgress(50);
                                        txtUploadFinish.setText(getResources().getString(R.string.successfullyUploaded));
                                        txtUploadData.setText(gettingFiles);
                                        upload_image();
                                    } else {
                                        isUploadAgain = true;
                                        againCallUploadToServer(isSyncOnline);
                                    }

                                } catch (CursorIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                    progressBar.setProgress(30);
                                    progressBar.setProgress(50);
                                    txtUploadFinish.setText(getResources().getString(R.string.successfullyUploaded));
                                    txtUploadData.setText(gettingFiles);
                                    upload_image();
                                }

                            } else {
                                clearAll();
                                Toastmsg(OfflineConstituencyDashBoardActivity.this, response.getString("message"));
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
                        Toast.makeText(OfflineConstituencyDashBoardActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error " + error.toString());

                    }
                });

                VolleySingleton.getInstance(this).addToRequestQueue(request);
            } else {
                Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
            }
        } else {
            callToUploadImage();
            Toastmsg(OfflineConstituencyDashBoardActivity.this, noRecordFound);
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


    @BindString(R.string.noRecordFound)
    String noRecordFound;
    File[] file;


    public void upload_image() {

        try {
            File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/");
            file = parentDir.listFiles();

//        Log.w(TAG, "list file length " + file.length);
            if (file != null) {
                if (checkInternet.isConnected()) {
                    //   dialog.show();

                    transferUtility =
                            TransferUtility.builder()
                                    .context(OfflineConstituencyDashBoardActivity.this)
                                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                                    .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                                    .build();
                    // txtUploadData.setText("Uploading files (0/" + file.length + ")");

                    upload_image_onebyone(file[file_count]);
                    txtUploadData.setText(getResources().getString(R.string.uploadFiles, "0", file.length + ""));
                } else {
                    Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
                }
            } else {
                Toastmsg(OfflineConstituencyDashBoardActivity.this, getResources().getString(R.string.noImagefound));
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
        } catch (Exception e) {
            e.printStackTrace();
            Toastmsg(OfflineConstituencyDashBoardActivity.this, getResources().getString(R.string.noImagefound));
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

    TransferUtility transferUtility;

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


        TransferObserver uploadObserver =
                transferUtility.upload(S3_BUCKETNAME,
                        S3_RICHASSETS_FOLDER + "/" + filetoBeUploaded.getName(),
                        filetoBeUploaded);


        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    try {

                        Log.w(TAG, "completed");
                        filetoBeUploaded.delete();

                        file_count++;

                        if (file_count <= file.length - 1) {
                            if (checkInternet.isConnected()) {
                                //  dialog.show();
                                updateData();
                                upload_image_onebyone(file[file_count]);
                            } else {
                                Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
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
                } else if (TransferState.FAILED == state) {

                    Log.w(TAG, "failed");
                    try {
                        //   dialog.dismiss();
                        if (checkInternet.isConnected()) {
                            //  dialog.show();
                            updateData();
                            upload_image_onebyone(file[file_count]);
                        } else {
                            Toastmsg(OfflineConstituencyDashBoardActivity.this, noInternet);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (checkInternet.isConnected()) {

                            updateProgressbar();
                            clearAll();

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
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors

                Log.w(TAG, "error ;" + ex.getMessage());
            }

        });
    }

    private int surveyCount = 0;


    private void failed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                completeNotification(uploadfailed);
                clearAll();
                Toastmsg(OfflineConstituencyDashBoardActivity.this, uploadfailed);
            }
        });
    }

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
                    Toastmsg(OfflineConstituencyDashBoardActivity.this, successfullyUploaded);
                    surveyCount = 0;
                    surveyDetailsList.clear();
                    updateMenu();
                }

            }
        });
    }

    private void uploadSurveyOnebyOne(final SurveyDetails details) {


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
                e.printStackTrace();

                Log.w(TAG, "Error " + call.toString());
                dialog.dismiss();
                // failed();
                success(details.getSurveyId());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                final String myResponse = response.body().string();

                Log.w(TAG, "response :" + myResponse);
                // dialog.dismiss();
                success(details.getSurveyId());

            }
        });

      /*  StringRequest request = new StringRequest(Request.Method.GET, API.UPLOAD_SURVEY + "&projectID=" + details.getProjectId() +
                "&cid=" + sharedPreferenceManager.get_constituency_id() + "&userId=" + details.getUserId() + "&usertype=" + details.getUserType() +
                "&surveyid=" + details.getSurveyId() + "&surveytype=" + details.getSurveyType() + "&boothname=" + details.getBoothNmae() +
                "&lattitude=" + details.getLatitude() + "&longitude=" + details.getLongitude() + "&audiofilename=" + details.getAudioFileName() +
                "&surveydate=" + details.getSurveyDate() + "&respondantname=" + details.getName() + "&mobile=" + details.getMobile() + "&pwname=" + details.getPwName()
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
                    Toastmsg(OfflineConstituencyDashBoardActivity.this, successfullyUploaded);
                    surveyCount = 0;
                    surveyDetailsList.clear();
                    updateMenu();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                    Toastmsg(OfflineConstituencyDashBoardActivity.this, successfullyUploaded);
                    surveyCount = 0;
                    surveyDetailsList.clear();
                    updateMenu();
                }

                Log.w(TAG, "Error : " + error.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);*/
    }


    @BindString(R.string.uploadFailed)
    String uploadfailed;

    private void createNotification() {

        String channelId = "Uploading";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        build = (NotificationCompat.Builder) new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(upload)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)

                //  setChannelId(sharedPreferenceManager.get_user_id())
                .setContentText(uploadInProgress);

        build.setProgress(100, 0, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel;
            channel = new NotificationChannel(channelId,
                    "Upload",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyManager.createNotificationChannel(channel);
        }


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

    private void updateData() {

        try {

            txtUploadData.setText(getResources().getString(R.string.uploadFiles, (file_count + 1) + "", file.length + ""));
            //txtUploadData.setText("Uploading Files (" + (file_count + 1) + "/" + file.length + ")");

            // Log.w(TAG, "file count : " + file_count + "file length : " + file.length);

            int progresscount = ((file_count * 50) / file.length);


            Log.w(TAG, "Progress bar state : " + progresscount);
            progressBar.setProgress(progresscount + 50);
        } catch (Exception e) {
            txtUploadData.setText(uploading);
            e.printStackTrace();
        }
    }


    private void updateProgressbar() {
        progressBar.setProgress(100);
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

    @Override
    public void onBackPressed() {
        if (isUpoadingData) {
            Toastmsg(OfflineConstituencyDashBoardActivity.this, dataUploading);
        } else {
            super.onBackPressed();
        }
    }


    private void getLoginStatus(String username) {
        StringRequest request = new StringRequest(Request.Method.POST, API.LOGIN_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    Log.w(TAG, "response : " + response);
                    dialog.dismiss();
                    JSONArray array = new JSONArray(response);

                    JSONObject jsonObject = array.getJSONObject(0);

                    switch (jsonObject.getString("msg")) {


                        case "success":
                            MyApplication.getInstance().trackEvent(sharedPreferenceManager.get_project_id(),

                                    "Logout", sharedPreferenceManager.get_username() + "-" + getCurrentateAndTime() + "-" + getDeviceName(), 2);

                            sharedPreferenceManager.set_keep_login(false);
                            sharedPreferenceManager.set_keep_login_role("");
                            db.delete_question();
                            logoutUser();
                            break;

                        case "failure":

                            Toastmsg(OfflineConstituencyDashBoardActivity.this, "Failure try again later");
                            break;


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                Log.w(TAG, "error " + error.toString());
                Toastmsg(OfflineConstituencyDashBoardActivity.this, "Failure try again later");


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("type", "logout");
                return map;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);


    }
}
