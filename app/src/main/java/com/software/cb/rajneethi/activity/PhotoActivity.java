package com.software.cb.rajneethi.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import id.zelory.compressor.Compressor;

/**
 * Created by w7 on 11/28/2017.
 */

public class PhotoActivity extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.photo)
    String title;

    String file, fileName = "";
    File newDir;
    File dir;
    private static final int CAMERA_REQUEST = 1888;
    private String TAG = "photo";
    String boothName;
    SharedPreferenceManager sharedPreferenceManager;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.software.cb.rajneethi.fileprovider";
    private GPSTracker gpsTracker;

    @BindView(R.id.edtDescription)
    EditText edtDescription;

    @BindString(R.string.all_fileds_required)
    String allFiledsRequired;

    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, title);

        gpsTracker = new GPSTracker(this);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        if (getIntent().getExtras() != null) {
            boothName = getIntent().getExtras().getString("booth_name");
            userType = getIntent().getExtras().getString("userType");
        }

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/Folder/");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
    }

    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;
    File newfile;

    private void deleteFile() {
        if (!(fileName.isEmpty())) {

            File delete = new File(dir, fileName);
            Log.w(TAG, "File Name : " + fileName);

            if (delete.exists()) {
                Log.w(TAG, "File exist");
                delete.delete();
            }

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

    @BindString(R.string.successfullyAdded)
    String successfullyAdded;

    @OnClick(R.id.btnAdd)
    public void add() {

        if (!edtDescription.getText().toString().trim().isEmpty() && selectedBitmap != null) {

            try {

                JSONObject object = new JSONObject();

                String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();

                object.put("description", edtDescription.getText().toString().trim());
                object.put("imageFileName", fileName);

                object.put("surveyType", "Photo");

                object.put("userType", userType);
                //added for new 4 attribute
                object.put("surveyDate", new Date().toString());
                object.put("projectId", sharedPreferenceManager.get_project_id());
                object.put("partyWorker", sharedPreferenceManager.get_user_id());
                object.put("pwname", sharedPreferenceManager.get_username());
                // survey_result.put("booth", sharedPreferenceManager.get_survey_booth());


                object.put("latitude", gpsTracker.getLatitude() + "");
                object.put("longitude", gpsTracker.getLongitude() + "");
                object.put("booth_name", boothName);
                object.put("surveyid", surveyId);

                VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", object.toString(), get_current_date(), true, surveyId);

                MyDatabase db = new MyDatabase(this);


                BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                        boothName, "Photo", userType);
                db.insertBoothStats(stats);
                //VotersDatabase votersDatabase = new VotersDatabase(AddVoterActivity.this);
                db.insert_voter_attribute(attribute);
                //  votersDatabase.update_survey(attribute.getVoterCardNumber());

                Toastmsg(PhotoActivity.this, successfullyAdded);
                PhotoActivity.super.onBackPressed();
                PhotoActivity.this.finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            Toastmsg(this, allFiledsRequired);
        }
    }

    @OnClick(R.id.imgPhoto)
    public void takePhoto() {

        if (checkPermissionGranted(Constants.CAMERA, this) && checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {
            deleteFile();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            file = newDir + "/" + sharedPreferenceManager.get_username() + "_" + boothName + "_" + "Questionaire_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";

            fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            Uri outputFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Log.w(TAG, "inside version 7");
                outputFileUri = FileProvider.getUriForFile(PhotoActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            } else {
                Log.w(TAG, "inside version 6");
                outputFileUri = Uri.fromFile(newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        } else {
            askPermission(new String[]{Constants.CAMERA, Constants.WRITE_EXTERNAL_STORAGE});
        }
    }

    Bitmap selectedBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                File imgFile = new File(file);

                new CompressImage().execute(imgFile);
                selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                selectedBitmap = getResizedBitmap(selectedBitmap, 400, 400);
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
    private class CompressImage extends AsyncTask<File, Void, Void> {

        File file;

        @Override
        protected Void doInBackground(File... params) {
            file = params[0];
            try {
                File compressedImage = new Compressor(PhotoActivity.this)
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
}
