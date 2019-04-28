package com.software.cb.rajneethi.moviesurvey;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.OpinionPollActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.services.RecordService;
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
import id.zelory.compressor.Compressor;

/**
 * Created by DELL on 31-12-2017.
 */

public class RespondentInfoActivity extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.respondentInfo)
    String title;

    String file, fileName = "";
    File newDir;
    File dir;
    File newfile;
    Bitmap selectedBitmap;
    private static final int CAMERA_REQUEST = 1888;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.software.cb.rajneethi.fileprovider";
    String theatreName;
    SharedPreferenceManager sharedPreferenceManager;
    private MyDatabase db;
    private GPSTracker gpsTracker;


    JSONObject finalObj = new JSONObject();

    String mFileName;
    String administration, ticketCounter, physicalSecurity, houseKeeping, technicalAspects;

    String TAG = "RespondentInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respondent);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        db = new MyDatabase(this);
        gpsTracker = new GPSTracker(this);

        if (getIntent().getExtras() != null) {
            theatreName = getIntent().getExtras().getString("name");
            administration = getIntent().getExtras().getString("all");
            mFileName = getIntent().getExtras().getString("fileName");
            // ticketCounter = getIntent().getExtras().getString("ticketCounter");
            // physicalSecurity = getIntent().getExtras().getString("technicalAspects");
            // houseKeeping = getIntent().getExtras().getString("physicalSecurity");
            // technicalAspects = getIntent().getExtras().getString("houseKeeping");
        }


        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/Folder/");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");


        edtMobile.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10) {
                    edtMobile.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void addValues(String key, String value) {

        try {
            finalObj.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeValues(String key) {
        finalObj.remove(key);
    }


    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    @OnClick(R.id.btnSave)
    public void save() {

        if (!(edtName.getText().toString().trim().isEmpty() || edtAge.getText().toString().trim().isEmpty() ||
                edtMobile.getText().toString().trim().isEmpty() || edtEducation.getText().toString().trim().isEmpty() ||
                edtOccupation.getText().toString().trim().isEmpty() || edtGender.getText().toString().trim().isEmpty() || edtMaritalStatus.getText().toString().trim().isEmpty())) {

            if (selectedBitmap != null) {
                if (edtMobile.getText().toString().trim().length() == 10) {
                    try {


                        finalObj = null;
                        finalObj = new JSONObject(administration);
                    /*
                   // finalObj.put("administration", administration);
                    object.put("ticketCounter", ticketCounter);
                    object.put("technicalAspects", technicalAspects);
                    object.put("physicalSecurity", physicalSecurity);
                    object.put("houseKeeping", houseKeeping);
*/
                        // JSONObject resObj = new JSONObject();
                        addValues("s6-name", edtName.getText().toString().trim());
                        addValues("s6-age", edtAge.getText().toString().trim());
                        addValues("s6-mobile", edtMobile.getText().toString().trim());
                        addValues("s6-education", edtEducation.getText().toString().trim());
                        addValues("s6-occupation", edtOccupation.getText().toString().trim());
                        addValues("s6-gender", edtGender.getText().toString().trim());
                        addValues("s6-maritalStatus", edtMaritalStatus.getText().toString().trim());
                        addValues("s6-theatrePhoto", fileName);

                        // object.put("respondentInfo", resObj.toString());

                        String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();

                        //standard parameter
                        addValues("surveyType", "Theatre");
                        addValues("userType", "TSP");
                        addValues("surveyDate", new Date().toString());
                        addValues("projectId", sharedPreferenceManager.get_project_id());
                        addValues("partyWorker", sharedPreferenceManager.get_user_id());
                        addValues("latitude", gpsTracker.getLatitude() + "");
                        addValues("longitude", gpsTracker.getLongitude() + "");
                        // object.put("booth_name", booth_name);
                        addValues("audioFileName", mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));
                        addValues("surveyid", surveyId);

                        Log.w(TAG, "Respondent info :" + finalObj.toString());
                        if (!db.check_mobile_number_exist(edtMobile.getText().toString().trim())) {
                            VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", finalObj.toString(), get_current_date(), false, surveyId);
                            db.insert_voter_attribute(attribute);
                            Intent i = new Intent(this, RecordService.class);
                            stopService(i);
                            //  votersDatabase.update_survey(attribute.getVoterCardNumber());
                            Toastmsg(RespondentInfoActivity.this, successfullyAdded);
                            startActivity(new Intent(RespondentInfoActivity.this, OpinionPollActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            RespondentInfoActivity.this.finish();
                        } else {
                            Toastmsg(RespondentInfoActivity.this, mobilNumberExist);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toastmsg(RespondentInfoActivity.this, validMobile);
                }
            } else {
                Toastmsg(RespondentInfoActivity.this, "Please take a photo");
                return;
            }

        } else {
            Toastmsg(RespondentInfoActivity.this, allFieldsRequired);
        }

    }


    @BindString(R.string.successfullyAdded)
    String successfullyAdded;
    @BindString(R.string.mobileNumberExist)
    String mobilNumberExist;
    @BindString(R.string.validMobile)
    String validMobile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (data != null) {
                Log.w(TAG, "data is not null");
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.w(TAG, "data is null");
            }

            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                File imgFile = new File(file);

                new CompressImage().execute(imgFile.getAbsolutePath());
                selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                selectedBitmap = getResizedBitmap(selectedBitmap, 400, 400);
                imgPhoto.setVisibility(View.VISIBLE);
                imgPhoto.setImageBitmap(selectedBitmap);
            }
        } else {
            try {
                if (newfile.exists()) {
                    newfile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //compress image
    private class CompressImage extends AsyncTask<String, Void, Void> {

        File file;

        @Override
        protected Void doInBackground(String... params) {
            file = new File(params[0]);
            try {
                File compressedImage = new Compressor(RespondentInfoActivity.this)
                        .setMaxWidth(500)
                        .setMaxHeight(500)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES) + "/compressimage")
                        .compressToFile(file);


                Log.w(TAG, "Compressed image size : " + compressedImage.length() / 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.w(TAG, "Delete File path : " + file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    public void onBackPressed() {
        deleteFile();
        super.onBackPressed();

    }


    @OnClick(R.id.txtAddPhoto)
    public void takePhoto() {
        if (checkPermissionGranted(Constants.CAMERA, this) && checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {
            deleteFile();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            file = newDir + "/" + sharedPreferenceManager.get_username() + "_" + theatreName + "_" + "theatre_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";

            fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            Uri outputFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(RespondentInfoActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            } else {
                outputFileUri = Uri.fromFile(newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        } else {
            askPermission(new String[]{Constants.CAMERA, Constants.WRITE_EXTERNAL_STORAGE});
        }
    }

    private void deleteFile() {
        if (!(fileName.isEmpty())) {
            File delete = new File(dir, fileName);
            if (delete.exists()) {
                delete.delete();
            }

        }
    }

    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtAge)
    EditText edtAge;
    @BindView(R.id.edtGender)
    EditText edtGender;
    @BindView(R.id.edtMobile)
    EditText edtMobile;
    @BindView(R.id.edtEdcation)
    EditText edtEducation;
    @BindView(R.id.edtOccupation)
    EditText edtOccupation;
    @BindView(R.id.edtMaritalStatus)
    EditText edtMaritalStatus;
    @BindView(R.id.txtAddPhoto)
    TextView txtAddPhoto;
    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;

}
