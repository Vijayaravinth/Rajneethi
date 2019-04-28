package com.software.cb.rajneethi.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.AudioFileDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.S3Utils;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.checkInternet;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vijay on 19-03-2017.
 */

public class DownloadActivity extends Util implements checkInternet.ConnectivityReceiverListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    private String id, project_id;

    @BindView(R.id.download_layout)
    RelativeLayout download_layout;

    String[] descriptionData = {"Details", "Status", "Photo", "Confirm"};

    private SharedPreferenceManager sharedPreferenceManager;

    private String zipfilename, costituencyFilename;

    private S3Utils s3Utils;
    private static final String TAG = "Download Activity";
    String file_path = "/offline/data";
    String filename = "/offline/data";

    private TransferObserver transferObserver;


    private static final String S3_BUCKETNAME = "rn-assets";
    private static final String S3_CONSTITUENCIES_FOLDER = "constituencies";
    private static final String S3_RICHASSETS_FOLDER = "richassets";
    private static final String S3_CONSTITUENCY_DESTINATION_FOLDER = "offline/data/";
    private static volatile S3Utils instance;
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;
    private String appFilePath;
    private Context context;
    private TransferUtility transferUtility;
    String dir;
    File file;
    String zipfilename1;
    File path;

    MyDatabase db;

    @BindView(R.id.your_state_progress_bar_id)
    StateProgressBar stateProgressBar;

    @BindView(R.id.txt_status)
    TextView txt_status;

    @BindString(R.string.downloadData)
    String toolbarTittle;

    @BindString(R.string.downloading)
    String downloading;

    String userId, userType, role;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        db = new MyDatabase(this);
        setup_toolbar_with_back(toolbar,toolbarTittle);
        if (getIntent().getExtras() != null) {
            Bundle args = getIntent().getExtras();

            id = args.getString("cid");
            project_id = args.getString("project_id");
            userId = args.getString("userId");
            role = args.getString("role");
            userType = args.getString("userType");
        }
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        zipfilename = "ac-" + project_id + ".db.gz";
        zipfilename1 = "ac-" + project_id + ".db.gz";
        // costituencyFilename = "ac-" + project_id + ".voters.db";

        costituencyFilename = "voters.db";

    /*    s3Credentials = new BasicAWSCredentials(sharedPreferenceManager.getS3ACCESSKEY(), sharedPreferenceManager.getS3SECRETKEY());
        client = new AmazonS3Client(s3Credentials);
        client.setRegion(Region.getRegion(Regions.AP_SOUTH_1));

        transferUtility = new TransferUtility(client, DownloadActivity.this);*/
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/" + zipfilename1;
        file = new File(dir);


        txt_status.setText(downloading);
        if (checkInternet.isConnected()) {
            Log.w(TAG, "call before download file");
            new download_csv_file().execute();
        } else {
            Toastmsg(DownloadActivity.this, noInternet);
            DownloadActivity.super.onBackPressed();
        }
    }

    @BindString(R.string.noInternet)
    String noInternet;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @BindString(R.string.extractingFile)
    String extractingFile;
    int total = 0;
    int percent = 0;
    float fraction = 0.0F;

    private class download_csv_file extends AsyncTask<String, Void, String> {

        File nweFile = null;

        @Override
        protected String doInBackground(String... params) {


            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(new AmazonS3Client(AWSMobileClient.getInstance())).build();


            TransferObserver downloadObserver =
                    transferUtility.download(
                            S3_BUCKETNAME, S3_CONSTITUENCIES_FOLDER + "/" + zipfilename, file);

            // Attach a listener to the observer to get state update and progress notifications
            downloadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int i, TransferState state) {

                    Log.w(TAG, "download complete");
                    if (TransferState.COMPLETED == downloadObserver.getState()) {
                        // Handle a completed upload.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt_status.setText(extracting);
                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                            }
                        });

                        update_task("download");
                        File file = new File(path, "/Folder/" + "ac-" + project_id + ".db.gz");
                        nweFile = new File(path, "/Folder/" + costituencyFilename);

                        Log.w(TAG, "file path " + file.getAbsolutePath() + " target " + nweFile.getAbsolutePath());

                        try {
                            if (file.exists()) {
                                GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(file));
                                FileOutputStream fileOutputStream = new FileOutputStream(nweFile);

                                byte[] buffer = new byte[4096];
                                int bytes_read;
                                while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
                                    fileOutputStream.write(buffer, 0, bytes_read);
                                }
                                gZIPInputStream.close();
                                fileOutputStream.close();
                            }

                            update_task("task2");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else if (TransferState.FAILED == downloadObserver.getState()){

                        Log.w(TAG,"Download failed");
                        sharedPreferenceManager.set_keep_login(true);
                        sharedPreferenceManager.set_keep_login_role(role);
                        sharedPreferenceManager.set_user_type(userType);
                        sharedPreferenceManager.set_project_id(project_id);
                        sharedPreferenceManager.set_Constituency_id(id);
                        sharedPreferenceManager.set_user_id(userId);

                        if(userType.equalsIgnoreCase("d2d")) {
                            startActivity(new Intent(DownloadActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        }else{
                            startActivity(new Intent(DownloadActivity.this, FamilyDataActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        }

                        DownloadActivity.this.finish();

                    }else if(TransferState.IN_PROGRESS == downloadObserver.getState()){
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
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
                       Log.w(TAG,"failed called" +ex.getMessage());
                    //
                    //  Log.w(TAG, "error " + ex.getMessage());
                }

            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           /* if (nweFile.exists()) {
                // new Task2().execute();
                read_data();
            }*/
        }
    }

    private void calculatePercentage(final int total, final long byteTransfered) {
        Log.w(TAG, "inside calculate percentage " + " total :" + total + " bytes transfered : " + byteTransfered);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (total > 0 && byteTransfered > 0) {
                    fraction = byteTransfered / total;
                    Log.w(TAG, "calculation : " + (byteTransfered / total));
                    Log.w(TAG, "fraction not cal " + fraction);
                    Log.w(TAG, "fraction  " + (fraction * 100));
                    percent = (int) Math.round(fraction * 100);
                    Log.w(TAG, "percent " + percent);
                }
            }
        });

    }

    @BindString(R.string.completed)
    String completed;

    @BindString(R.string.extractingFile)
    String extracting;

    private void update_task(String task) {


        switch (task) {

            case "download":

                Log.w(TAG, "Extracting called");
                txt_status.setText(extracting);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt_status.setText(extracting);
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);

                    }
                });
                break;

            case "task2":
                //  stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                txt_status.setText(completed);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        try {
                            File file = new File(path, "/Folder/" + "ac-" + project_id + ".db.gz");
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sharedPreferenceManager.set_keep_login(true);
                        sharedPreferenceManager.set_keep_login_role(role);
                        sharedPreferenceManager.set_user_type(userType);
                        sharedPreferenceManager.set_project_id(project_id);
                        sharedPreferenceManager.set_Constituency_id(id);
                        sharedPreferenceManager.set_user_id(userId);


                        if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {

                            MyApplication.getInstance().trackEvent(sharedPreferenceManager.get_project_id(),

                                    "Login", sharedPreferenceManager.get_username() + ":" +getCurrentateAndTime()+":"+getDeviceName(),1);

                            startActivity(new Intent(DownloadActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            DownloadActivity.this.finish();
                        } else {
                            startActivity(new Intent(DownloadActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            DownloadActivity.this.finish();
                        }

                        //  new Task2().execute();
                    }
                }, 1000);
                break;
            case "failed":
                txt_status.setText(downloading);


                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (checkInternet.isConnected()) {
                            new download_csv_file().execute();
                        }
                    }
                }, 1000);
                break;
            case "success":

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt_status.setText(completed);
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);

                    }
                });
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // File csvFile = new File(path, "/Folder/" + costituencyFilename);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sharedPreferenceManager.set_keep_login(true);
                        startActivity(new Intent(DownloadActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        DownloadActivity.this.finish();
                    }
                }, 1000);
            default:
                break;
        }
    }


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private String getCurrentateAndTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.US);
        Date date = new Date();
        return formatter.format(date);
    }

    //Read csv file
    private class Task2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            File csvFile = new File(path, "/Folder/" + costituencyFilename);
            try {
                InputStream is = new FileInputStream(csvFile);
                BufferedReader reader = null;
                reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String line = "";
                StringTokenizer st = null;
                try {
                    boolean is_heading = true;

                    int i = 0;
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.split("\\|");
                        String cid = "" + fields[0];
                        String hid = "" + fields[1];
                        String hhash = "" + fields[2];
                        String votercard = "" + fields[3];
                        String name = "" + fields[4];
                        String regional = "" + fields[5];
                        String gender = "" + fields[6];
                        String age = "" + fields[7];
                        String relatedenglish = "" + fields[8];
                        String relatedRegional = "" + fields[9];
                        String relationship = "" + fields[10];
                        String address = "" + fields[11];
                        String address_regional = "" + fields[12];
                        String houseno = "" + fields[13];
                        String serialno = "" + fields[14];
                        Log.v("id", fields[4]);
                        if (!is_heading) {
                            String sql = "INSERT INTO votersData(constituencyId,hierarchyId,hierarchyHash,voterCardNumber,nameEnglish,nameRegional,gender,age,relatedEnglish,relatedRegional,relationship,addressEnglish,addressRegional,houseNo,serialNo,isUpdated,idPath,photoPath,addressPath) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                            db.getWritableDatabase().beginTransaction();

                            SQLiteStatement stmt = db.getWritableDatabase().compileStatement(sql);
                            stmt.bindString(1, cid);
                            stmt.bindString(2, hid);
                            stmt.bindString(3, hhash);
                            stmt.bindString(4, votercard);
                            stmt.bindString(5, name);
                            stmt.bindString(6, regional);
                            stmt.bindString(7, gender);
                            stmt.bindString(8, age);
                            stmt.bindString(9, relatedenglish);
                            stmt.bindString(10, relatedRegional);
                            stmt.bindString(11, relationship);
                            stmt.bindString(12, address);
                            stmt.bindString(13, address_regional);
                            stmt.bindString(14, houseno);
                            stmt.bindString(15, serialno);
                            stmt.bindString(16, "false");
                            stmt.bindString(17, "empty");
                            stmt.bindString(18, "empty");
                            stmt.bindString(19, "empty");

                            stmt.execute();
                            stmt.clearBindings();
                            db.getWritableDatabase().setTransactionSuccessful();
                            db.getWritableDatabase().endTransaction();
                            //  VoterDetails details = new VoterDetails(cid, hid, hhash, votercard, name,
                            // regional, gender, age, relatedenglish, relatedRegional,
                            // relationship, address, address_regional, houseno, serialno,
                            // false, "empty", "empty", "empty");
                            // db.insert_voter_details(details);
                        }
                        is_heading = false;
                    }

                    update_task("success");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
