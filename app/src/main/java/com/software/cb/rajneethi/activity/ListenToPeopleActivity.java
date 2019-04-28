package com.software.cb.rajneethi.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.AudioFilesAdapter;
import com.software.cb.rajneethi.adapter.StatsHeaderAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.fragment.ShowPayLoadFragment;
import com.software.cb.rajneethi.models.AudioFileDetails;
import com.software.cb.rajneethi.models.SurveyStats;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.S3Utils;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by w7 on 11/12/2017.
 */

public class ListenToPeopleActivity extends Util implements checkInternet.ConnectivityReceiverListener, ShowPayLoadFragment.updateData {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.listenToPeople)
    String title;

    private SharedPreferenceManager sharedPreferenceManager;
    private String TAG = "ListenPeople";

    @BindView(R.id.btnRetry)
    Button btnRetry;

    IOSDialog dialog;
    @BindString(R.string.noInternet)
    String noInternet;

    @BindString(R.string.noRecordFound)
    String noRecordFound;
    @BindString(R.string.Error)
    String Error;

    @BindView(R.id.musicRecyclerview)
    RecyclerView recyclerView;

    StatsHeaderAdapter adapter;

    private ArrayList<AudioFileDetails> list = new ArrayList<>();


    private TransferObserver transferObserver;


    private static final String S3_BUCKETNAME = "rn-assets";
    private static final String S3_CONSTITUENCIES_FOLDER = "constituencies";
    private static final String S3_RICHASSETS_FOLDER = "richassets/audio";
    private static final String S3_CONSTITUENCY_DESTINATION_FOLDER = "offline/data/";
    private static volatile S3Utils instance;
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;
    private TransferUtility transferUtility;

    String dir;
    File file;

    @BindString(R.string.fileMissing)
    String fileNotFound;

    @BindView(R.id.audioLayout)
    CardView audioLayout;

    @BindView(R.id.txtAudioFilename)
    TextView txtAudioFileName;

    @BindView(R.id.imgPlay)
    ImageView play;

    @BindView(R.id.imgPause)
    ImageView pause;

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    private int currentPosition = -1, seekbarPosition;
    Handler mHandler = new Handler();
    Runnable updateRunnable;

    int pos;


    int oldPosition = -1;

    String boothName;

    private boolean isBoothWise = false;

    ArrayList<SurveyStats> pwNameList = new ArrayList<>();


    AudioFilesAdapter audioAdapter;

    BroadcastReceiver receiver;

    @BindView(R.id.contentRecyclerView)
    RecyclerView contentRecylerview;

    private ArrayList<String> boothNameList = new ArrayList<>();

    String accessKey = "";
    String secretKey = "";


    private NotificationManager mNotifyManager;
    private android.support.v4.app.NotificationCompat.Builder build;
    int id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_to_people);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, title);
        sharedPreferenceManager = new SharedPreferenceManager(this);

/*
        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                Log.w(TAG, "AWSMobileClient initialized. User State is " + result.getUserState());

            }

            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Initialization error.", e);
            }
        });*/


        dialog = show_dialog(ListenToPeopleActivity.this, false);


        if (getIntent().getExtras() != null) {
            isBoothWise = getIntent().getExtras().getBoolean("isBoothWise");
            boothName = getIntent().getExtras().getString("boothName");
        }

        set_Linearlayout_manager(recyclerView, this);
        setAdapter();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        callAPI();

        forwardMusic();


    }


    int validatePosition = -1;


    //show payload data
    public void showPayloadData(AudioFileDetails details, int pos) {

        validatePosition = pos;
        ShowPayLoadFragment frag = ShowPayLoadFragment.newInstance(details);
        frag.show(getSupportFragmentManager(), "Dialog");
    }


    private void createNotification(String title) {

        String channelId = "Audio";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        build = (NotificationCompat.Builder) new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
        ;
        // .setOngoing(true)


        //  setChannelId(sharedPreferenceManager.get_user_id())

        build.setProgress(100, 0, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel;
            channel = new NotificationChannel(channelId,
                    "Audio",
                    NotificationManager.IMPORTANCE_DEFAULT);

            try {
                channel.setSound(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mNotifyManager.createNotificationChannel(channel);
        }


        mNotifyManager.notify(id, build.build());

    }

    private void updateNotification(int percentage) {

        Log.w(TAG, " notification percentage : " + percentage);

        build.setProgress(100, percentage, false);
        mNotifyManager.notify(id, build.build());
    }

    private void completeNotification() {
        mNotifyManager.cancel(id);
    }


    private void setLayoutManagerForMusicFiles() {
        contentRecylerview.setHasFixedSize(true);
        contentRecylerview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setAdapterForMusicFiles() {
        audioAdapter = new AudioFilesAdapter(this, dataList, ListenToPeopleActivity.this);
        contentRecylerview.setAdapter(audioAdapter);

        //  recyclerView.addItemDecoration(new dividerLineForRecyclerView(context));
    }

    @SuppressLint("MissingPermission")
    public void call(String mobileNumber) {

        Log.w(TAG, "Mobile number");
        if (checkPermissionGranted(Constants.CALL, this)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber.trim()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } else {
            askPermission(new String[]{Constants.CALL});
        }
    }

    //validate or invalidate survey
    public void validateOrInvalidateSurvey(String msg) {

        AudioFileDetails details = list.get(pos);
        details.setSurveyFlag(msg);
        adapter.notifyDataSetChanged();

    }

    MediaPlayer player;

    public void hideAudioLayout() {
        if (audioLayout.isShown()) {
            audioLayout.setVisibility(View.GONE);
        }
        releasePlayer();
    }

    //play audio
    public void playAudio(String fileName, int position) {

        if (currentPosition != -1) {
            AudioFileDetails details = dataList.get(currentPosition);
            details.setPlaying(false);
            details.setPaused(false);
        }

     /*   if (!audioLayout.isShown()) {
            audioLayout.setVisibility(View.VISIBLE);
        }*/

        txtAudioFileName.setText(fileName);
        currentPosition = position;
        AudioFileDetails details = dataList.get(currentPosition);
        details.setPlaying(true);
        audioAdapter.notifyDataSetChanged();
        // seekbarPosition = 0;
        //seekBar.setProgress(seekbarPosition);


        mNotifyManager.cancel(id);

        playSong(getApplicationContext().getCacheDir() + "/" + fileName);
    }

    public void expandData(int pos) {

        try {
            if (oldPosition != -1) {
                SurveyStats oldStats = pwNameList.get(oldPosition);
                oldStats.setExpanded(false);
            }

            if (currentPosition != -1) {
                AudioFileDetails details = dataList.get(currentPosition);
                details.setPlaying(false);
                details.setPaused(false);
            }


            SurveyStats stats = pwNameList.get(pos);
            stats.setExpanded(true);
            oldPosition = pos;

            completeNotification();
            releasePlayer();

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            completeNotification();
            releasePlayer();
        }

    }

    public void disableExpand(int pos) {
        SurveyStats stats = pwNameList.get(pos);
        stats.setExpanded(false);
        adapter.notifyDataSetChanged();
        oldPosition = -1;
    }

    ArrayList<AudioFileDetails> dataList = new ArrayList<>();

    public void loadData(String pwName) {

        dataList.clear();

        Log.w(TAG, "Pw name :" + pwName);

        dialog.show();

        for (int j = 0; j <= list.size() - 1; j++) {
            AudioFileDetails stats = list.get(j);
            if (isUserwise) {
                if (stats.getUserId().equalsIgnoreCase(pwName)) {

                    file = new File(getApplicationContext().getCacheDir(), stats.getFileName());
                    if (file.exists()) {
                        Log.w(TAG, "Audio file exist : ");
                        stats.setDownloaded(true);
                    }
                    dataList.add(stats);
                }
            } else {
                if (stats.getBoothname().equalsIgnoreCase(pwName)) {

                    file = new File(getApplicationContext().getCacheDir(), stats.getFileName());
                    if (file.exists()) {
                        Log.w(TAG, "Audio file exist : ");
                        stats.setDownloaded(true);
                    }
                    dataList.add(stats);
                }
            }
        }

        Log.w(TAG, "Data list size : " + dataList.size());

        if (dataList.size() > 0) {
            audioAdapter.notifyDataSetChanged();
        }

        dialog.dismiss();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void playSong(String songPath) {

        Log.w(TAG, "Song path : " + songPath);
        try {

            releasePlayer();
            player = new MediaPlayer();


            player.setDataSource(songPath);
            player.prepare();
            player.start();
            int duration = player.getDuration();
            Log.w("Duartion ", duration + "");
            duration = duration / 1000;
            seekBar.setMax(duration);

            hidePlayButton();

            if (!audioLayout.isShown()) {

                audioLayout.setVisibility(View.VISIBLE);
            }

            //  completeNotification();

            // createNotification(songPath.substring(songPath.lastIndexOf("/") + 1, songPath.length()));

            this.runOnUiThread(updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (player != null) {
                        seekbarPosition = player.getCurrentPosition() / 1000;
                        seekBar.setProgress(seekbarPosition);

                        int percentage = (100 * player.getCurrentPosition()) / player.getDuration();

                        Log.w(TAG, "percentage:" + percentage);

                        // updateNotification(percentage);


                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

            player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.w(TAG, "buffering start");
                            //   show_dialog(SuccessStoriesAudioActivity.this);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.w(TAG, "buffering end");
                            //  dismiss_dialog();
                            break;
                    }
                    return false;
                }
            });
            Log.w("Duartion ", duration + "");

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    hidePauseButton();
                    AudioFileDetails details = dataList.get(currentPosition);
                    details.setPlaying(false);
                    details.setPaused(false);


                    if (mHandler != null) {
                        mHandler.removeCallbacks(updateRunnable);
                    }
                    //   completeNotification();

                    audioAdapter.notifyDataSetChanged();

                    if (mHandler != null) {
                        mHandler.removeCallbacks(updateRunnable);
                    }

                    if (audioLayout.isShown()) {
                        audioLayout.setVisibility(GONE);
                    }
                }
            });


        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void forwardMusic() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (player != null) {

                    if (b) {
                        player.seekTo(i * 1000);

                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void hidePlayButton() {
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
    }

    private void hidePauseButton() {
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.imgPlay)
    public void play_audio() {

        hidePlayButton();

        if (player != null) {
            AudioFileDetails details = list.get(currentPosition);
            details.setPlaying(true);
            details.setPaused(false);
            audioAdapter.notifyDataSetChanged();

            player.start();
        }
    }

    public void pauseAudio() {
        Log.w(TAG, "pause audio called");
        if (player != null) {
            player.pause();

            //  hidePauseButton();
        }
    }


    @OnClick(R.id.imgPause)
    public void pause() {
        pauseAudio();
        hidePauseButton();
        AudioFileDetails details = list.get(currentPosition);
        details.setPlaying(false);
        details.setPaused(true);
        audioAdapter.notifyDataSetChanged();


    }

    private void releasePlayer() {
        if (player != null) {
            player.pause();
            player.reset();
            player.release();
            player = null;
        }
    }

    @Override
    public void onBackPressed() {
        releasePlayer();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(updateRunnable);
        }

        // completeNotification();

        releasePlayer();
    }

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.software.cb.rajneethi.fileprovider";

    public void shareAudio(String fileName) {

        try {
            pauseAudio();
            String sharePath = getApplicationContext().getCacheDir() + "/" + fileName;

            Log.w(TAG, "Path : " + sharePath);
            File f = new File(sharePath);
            Uri uri;//= Uri.parse("file://" + file.getAbsolutePath());

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                uri = FileProvider.getUriForFile(ListenToPeopleActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, f);
                share.setDataAndType(uri, getContentResolver().getType(uri));
            } else {
                uri = Uri.parse("file://" + file.getAbsolutePath());
                share.setDataAndType(uri,
                        getContentResolver().getType(uri));
            }
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share Sound File"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        adapter = new StatsHeaderAdapter(this, pwNameList, ListenToPeopleActivity.this);
        recyclerView.setAdapter(adapter);
        //  recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));
    }


    public void download(String filename, int position) {


        Log.w(TAG, "File name : " + filename);
        if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {

            if (checkInternet.isConnected()) {

                AudioFileDetails details = dataList.get(position);
                details.setDownloading(true);
                audioAdapter.notifyDataSetChanged();

                Log.w(TAG, "Downloading called");
                dialog.show();
                /*downloadAudioFile(filename, position);*/
                downloadWithTransferUtility(filename, position);

            } else {
                Toastmsg(ListenToPeopleActivity.this, noInternet);
            }
        } else {
            askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE});
        }
    }

    @OnClick(R.id.btnRetry)
    public void retry() {
        callAPI();
    }

    //call api
    private void callAPI() {
        if (checkInternet.isConnected()) {
            dialog.show();
            if (isBoothWise) {
                getMusicFiles(API.GET_BOOTH_WISE_DATA + sharedPreferenceManager.get_project_id() + "&boothName=" + boothName);
            } else {
                getMusicFiles(API.GET_AUDIO_FILES + sharedPreferenceManager.get_project_id());
            }
        } else {

            Toastmsg(ListenToPeopleActivity.this, noInternet);
            showRetryButton();
        }
    }

    private void showRetryButton() {
        if (!btnRetry.isShown()) {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    private void hideRetryButton() {
        if (btnRetry.isShown()) {
            btnRetry.setVisibility(GONE);
        }
    }

    ArrayList<String> userNameList = new ArrayList<>();

    private void getMusicFiles(String api) {
        StringRequest request = new StringRequest(Request.Method.GET, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                hideRetryButton();
                try {
                    JSONArray array = new JSONArray(response);
                    if (array.length() > 0) {
                        ArrayList<AudioFileDetails> valueList = new ArrayList<>();
                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.get_keep_login_role().equals("Supervisor")) {

                                if (object.getString("parentid").equals(sharedPreferenceManager.get_user_id())) {

                                    userNameList.add(object.getString("pwname"));
                                    boothNameList.add(object.getString("boothname"));
                                    AudioFileDetails details = new AudioFileDetails(object.getString("audiofilename"), false, false, false, false,
                                            object.getString("surveyid"), object.getString("surveyflag"), object.getString("parentid"), true, object.getString("pwname"), 1, object.getString("boothname"));

                                    /*file = new File(getApplicationContext().getCacheDir(), object.getString("audiofilename"));
                                    if (file.exists()) {
                                        Log.w(TAG, "Audio file exist : ");
                                        details.setDownloaded(true);
                                    }*/
                                    //  valueList.add(details);
                                    list.add(details);
                                }
                            } else {


                                userNameList.add(object.getString("pwname"));
                                boothNameList.add(object.getString("boothname"));
                                AudioFileDetails details = new AudioFileDetails(object.getString("audiofilename"), false, false, false, false,
                                        object.getString("surveyid"), object.getString("surveyflag"), object.getString("parentid"), true, object.getString("pwname"), 1, object.getString("boothname"));

                               /* file = new File(getApplicationContext().getCacheDir(), object.getString("audiofilename"));
                                if (file.exists()) {
                                    Log.w(TAG, "Audio file exist : ");
                                    details.setDownloaded(true);
                                }*/
//                                valueList.add(details);
                                list.add(details);
                            }

                        }

                        //  list.clear();

                        //  List<String> al = new ArrayList<>();
                        Set<String> hs = new HashSet<>();
                        hs.addAll(userNameList);
                        userNameList.clear();
                        userNameList.addAll(hs);


                        for (int i = 0; i <= userNameList.size() - 1; i++) {
                            SurveyStats stats = new SurveyStats("0", userNameList.get(i), false);
                            pwNameList.add(stats);
                        }

                        if (pwNameList.size() > 0) {

                            adapter.notifyDataSetChanged();

                            setLayoutManagerForMusicFiles();
                            setAdapterForMusicFiles();
                        } else {
                            Toastmsg(ListenToPeopleActivity.this, noRecordFound);
                        }
                    } else {
                        Toastmsg(ListenToPeopleActivity.this, noRecordFound);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.w(TAG, "Response :" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
                dialog.dismiss();
                Toastmsg(ListenToPeopleActivity.this, Error);
                showRetryButton();

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }




    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }


    private boolean isUserwise = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.calendar:
                showCalendar();
                return true;

            case R.id.change:

                if (isUserwise) {
                    isUserwise = false;
                } else {
                    isUserwise = true;
                }
                pwNameList.clear();

                if (isUserwise) {
                    Set<String> hs = new HashSet<>();
                    hs.addAll(userNameList);
                    userNameList.clear();
                    userNameList.addAll(hs);


                    for (int i = 0; i <= userNameList.size() - 1; i++) {
                        SurveyStats stats = new SurveyStats("0", userNameList.get(i), false);
                        pwNameList.add(stats);
                    }
                } else {
                    Set<String> hs = new HashSet<>();
                    hs.addAll(boothNameList);
                    boothNameList.clear();
                    boothNameList.addAll(hs);


                    for (int i = 0; i <= boothNameList.size() - 1; i++) {
                        SurveyStats stats = new SurveyStats("0", boothNameList.get(i), false);
                        pwNameList.add(stats);
                    }
                }

                if (pwNameList.size() > 0) {


                    oldPosition = -1;
                    currentPosition = -1;

                    adapter.notifyDataSetChanged();

                    dataList.clear();
                    audioAdapter.notifyDataSetChanged();

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showCalendar() {
        Calendar newCalendar = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.US);
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(ListenToPeopleActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        fromDatePickerDialog.show();
    }


    @Override
    public void update(String flag) {

        Log.w(TAG, "On update working : " + flag + " pos : " + validatePosition);

        if (validatePosition != -1) {
            AudioFileDetails details = dataList.get(validatePosition);
            details.setSurveyFlag(flag);
            Log.w(TAG, "details " + details.getSurveyFlag());
            //  update();

            audioAdapter.notifyItemChanged(validatePosition);

        }

    }


    private void downloadWithTransferUtility(String filename, int position) {


        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance())).build();

        dir = getCacheDir() + "/" + filename;

        file = new File(getApplicationContext().getCacheDir(), filename);


        Log.w(TAG, "bucket details :" + S3_BUCKETNAME + "/" + Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.AUDIO_PATH + "/" + filename);

        TransferObserver downloadObserver =
                transferUtility.download(
                        S3_BUCKETNAME, Constants.PATH + sharedPreferenceManager.get_constituency_id() + "/" + Constants.AUDIO_PATH + "/" + filename,
                        file);

        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == downloadObserver.getState()) {
                    // Handle a completed upload.
                    Log.w(TAG, " task completed");


                    AudioFileDetails details = dataList.get(position);
                    details.setDownloaded(true);
                    details.setDownloading(false);
                    audioAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    Log.w(TAG, "download complete");
                }else if (TransferState.FAILED == downloadObserver.getState()){
                    Toastmsg(ListenToPeopleActivity.this, fileNotFound);
                    Log.w(TAG, "task failed");
                    dialog.dismiss();
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("Your Activity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
             //   Log.w(TAG,"failed called" +ex.getMessage());
              //
              //  Log.w(TAG, "error " + ex.getMessage());
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.


        Log.d("Your Activity", "Bytes Transferred: " + downloadObserver.getBytesTransferred());
        Log.d("Your Activity", "Bytes Total: " + downloadObserver.getBytesTotal());
    }


}
