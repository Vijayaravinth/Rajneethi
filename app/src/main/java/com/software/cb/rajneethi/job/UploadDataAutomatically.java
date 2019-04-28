package com.software.cb.rajneethi.job;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.OfflineConstituencyDashBoardActivity;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.SurveyDetails;
import com.software.cb.rajneethi.models.VoterDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DELL on 24-01-2018.
 */

public class UploadDataAutomatically extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String url;

    private IOSDialog dialog;
    private String TAG = "UploadDataAutomatically";

    private MyDatabase db;

    int year, month, day;

    private TransferObserver transferObserver;



    private static final String S3_BUCKETNAME = "rn-assets";
    private static final String S3_CONSTITUENCIES_FOLDER = "constituencies";
    private String S3_RICHASSETS_FOLDER = "richassets";
    private static final String S3_CONSTITUENCY_DESTINATION_FOLDER = "offline/data/";
    private BasicAWSCredentials s3Credentials;
    private AmazonS3Client client;
    private TransferUtility transferUtility;

    boolean isUploadAgain = false;

    private ArrayList<SurveyDetails> surveyDetailsList = new ArrayList<>();


    private SharedPreferenceManager sharedPreferenceManager;


    UploadDataAutomatically(Context context) {
        this.context = context;
        sharedPreferenceManager = new SharedPreferenceManager(context);
        db = new MyDatabase(context);
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


    private JSONArray get_upload_data() {
        JSONArray payload = new JSONArray();
        try {
            Cursor c = null;
            if (isUploadAgain) {
                c = db.get_unsync_data();
            } else {
                c = db.get_voter_atrributes();
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


    public String get_current_date() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder string = new StringBuilder()
                // Month is 0 based so add 1
                .append(year).append("-")
                .append(month + 1).append("-")
                .append(day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return string.toString();

    }

    private void againCallUploadToServer() {

        Log.w(TAG, "again call to upload working");
        if (checkInternet.isConnected()) {
            upload_to_server();
        }
    }


    private void upload_to_server() {


        //  surveyDetailsList.clear();
        JSONObject object = new JSONObject();
        final JSONArray array = get_upload_data();
        //array.get
        try {
            object.put("payload", array);
            object.put("username", sharedPreferenceManager.get_username());
            object.put("date", get_current_date());
            object.put("constituencyId", sharedPreferenceManager.get_constituency_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (array.length() > 0) {
            if (checkInternet.isConnected()) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API.UPLOAD_DATA, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.w(TAG, response.getString("message"));

                            if (response.getString("message").equals("Inserted Successfully")) {
                                //      Toastmsg(OfflineConstituencyDashBoardActivity.this, " Successfully Uploaded .");


                             /*   for (int i = 0; i <= array.length() - 1; i++) {
                                    JSONObject dataObj = array.getJSONObject(i);
                                    String data = dataObj.getString("propValue");
                                    JSONObject propValue = new JSONObject(data);
                                    db.update_attribute_status(propValue.getString("surveyid"));

                                }
*/
                                try {
                                    int count = 0;
                                   /* try {
                                        Cursor c = db.get_unsync_data();
                                        if (c.getCount() > 0) {
                                            count = c.getCount();
                                        }
                                    } catch (CursorIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }*/

                                    if (count == 0) {
                                        isUploadAgain = false;

                                        upload_image();
                                    } else {
                                        isUploadAgain = true;
                                        againCallUploadToServer();
                                    }

                                } catch (CursorIndexOutOfBoundsException e) {

                                    upload_image();
                                }

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

                        Log.w(TAG, "Error " + error.toString());

                    }
                });

                VolleySingleton.getInstance(context).addToRequestQueue(request);
            }
        } else {
            // callToUploadImage();

        }
    }

    File[] file;

    public void upload_image() {

        try {
            File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/");
            file = parentDir.listFiles();

//        Log.w(TAG, "list file length " + file.length);
            if (file != null) {
                if (checkInternet.isConnected()) {
                    //   dialog.show();


                    // txtUploadData.setText("Uploading files (0/" + file.length + ")");

                    upload_image_onebyone(file[file_count]);
                    // txtUploadData.setText(getResources().getString(R.string.uploadFiles, "0", file.length + ""));
                }
            } else {


                if (checkInternet.isConnected()) {
                    if (surveyDetailsList.size() > 0) {
                        //  dialog.show();
                        // createNotification();
                        //isUpoadingData = true;
                        //updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                        // uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
           /* Toastmsg(OfflineConstituencyDashBoardActivity.this, getResources().getString(R.string.noImagefound));
            clearAll();
            updateProgressbar();
            clearAll();*/

            if (checkInternet.isConnected()) {
                if (surveyDetailsList.size() > 0) {
                    uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                    // dialog.show();
                   /* createNotification();
                    isUpoadingData = true;
                    updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                    */
                }
            }
        }
    }

    //get file type
    public String getFileType(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }


    public String get_current_time() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        return sdf.format(d);
    }


    private int file_count = 0;

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

                        Log.w(TAG, "Completed");
                        try {
                            //filetoBeUploaded.delete();

                            file_count++;

                            if (file_count <= file.length - 1) {
                                if (checkInternet.isConnected()) {
                                    //  dialog.show();
                                    //  updateData();
                                    upload_image_onebyone(file[file_count]);
                                }
                            }

                            if (file_count == file.length) {
                                // updateProgressbar();
                                //  clearAll();

                                Log.w(TAG, "Finished image uploading");
                               /* if (checkInternet.isConnected()) {
                                    if (surveyDetailsList.size() > 0) {
                                        dialog.show();
                                        createNotification();
                                        isUpoadingData = true;
                                        updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());

                                    }
                                }*/

                                file_count = 0;
                                file = null;
                                if (checkInternet.isConnected()) {
                                    if (surveyDetailsList.size() > 0) {

                                        uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case FAILED:

                        Log.w(TAG, "Failed");
                        try {
                            //   dialog.dismiss();
                            if (checkInternet.isConnected()) {
                                //  dialog.show();
                                // updateData();
                                upload_image_onebyone(file[file_count]);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (checkInternet.isConnected()) {

                                /*updateProgressbar();
                                clearAll();

                                if (surveyDetailsList.size() > 0) {
                                    dialog.show();
                                    createNotification();
                                    isUpoadingData = true;
                                    updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());

                                }*/

                                file_count = 0;
                                file = null;
                                if (checkInternet.isConnected()) {
                                    if (surveyDetailsList.size() > 0) {

                                        uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                                    }
                                }
                            }
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

    private int surveyCount = 0;

    private void uploadSurveyOnebyOne(final SurveyDetails details) {

        StringRequest request = new StringRequest(Request.Method.GET, API.UPLOAD_SURVEY + "&projectID=" + details.getProjectId() +
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
                    //updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                    uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                }

                if (surveyCount == surveyDetailsList.size()) {
                    //   completeNotification(uploadComplete);
                    //dialog.dismiss();
                    //  isUpoadingData = false;
                    //Toastmsg(OfflineConstituencyDashBoardActivity.this, successfullyUploaded);
                    surveyCount = 0;
                    surveyDetailsList.clear();
                    sendLocalBroadCast();
                    //updateMenu();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                surveyCount++;
                db.update_attribute(details.getSurveyId());

                if (surveyCount <= surveyDetailsList.size() - 1) {
                    //   updateNotification((int) ((surveyCount + 1) * 100) / surveyDetailsList.size());
                    uploadSurveyOnebyOne(surveyDetailsList.get(surveyCount));
                }

                if (surveyCount == surveyDetailsList.size()) {

                    surveyCount = 0;
                    surveyDetailsList.clear();
                    sendLocalBroadCast();
                    // updateMenu();
                }

              /*  dialog.dismiss();
                completeNotification(uploadfailed);
                clearAll();
                Toastmsg(OfflineConstituencyDashBoardActivity.this, uploadfailed);*/
                Log.w(TAG, "Error : " + error.toString());
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
    public static final String INTENT_FILTER = "INTENT_FILTER";

    private void sendLocalBroadCast() {
        Intent i = new Intent(INTENT_FILTER);
        i.putExtra("job","done");
        context.sendBroadcast(i);
    }


    @Override
    protected Void doInBackground(Void... voids) {

        upload_to_server();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.w("Upload Automat", "Done process");
    }
}
