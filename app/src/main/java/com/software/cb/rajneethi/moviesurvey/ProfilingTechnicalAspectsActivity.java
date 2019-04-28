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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
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

public class ProfilingTechnicalAspectsActivity extends Util {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.theatreProfiling)
    String title;

    String technology = "", screenPremise = "", entrances = "", parkingFacility = "", cleanCondition = "", toiletCondition = "", toiletsCleaned = "", hallCleaned = "", controlSystem = "";
    String sounds = "", digitalSystem = "", digitalScreen = "";
    boolean isMultiplex;

    boolean isTakingToiletPhoto;
    String entrancePhoto = "", toiletPhoto = "";
    String file, fileName = "";
    File newDir;
    File dir;
    File newfile;
    Bitmap selectedBitmap;
    private static final int CAMERA_REQUEST = 1888;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.software.cb.rajneethi.fileprovider";
    Bitmap entranceBitmap, toiletBitmap;
    String theatreName;
    SharedPreferenceManager sharedPreferenceManager;
    String TAG = "TechnicalAspects";

    String administration, ticketCounter;

    JSONObject finalObj = new JSONObject();

    String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profling_technical_aspects);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);

        if (getIntent().getExtras() != null) {
            isMultiplex = getIntent().getExtras().getBoolean("isMultiplex");
            theatreName = getIntent().getExtras().getString("name");
            mFileName = getIntent().getExtras().getString("fileName");
            //  administration = getIntent().getExtras().getString("administration");
            ticketCounter = getIntent().getExtras().getString("ticketCounter");
        }

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/Folder/");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");


        sharedPreferenceManager = new SharedPreferenceManager(this);
        if (isMultiplex) {
            txtQus2.setVisibility(View.VISIBLE);
            edtScreenPremise.setVisibility(View.VISIBLE);

        } else {
            txtPhyQus1.setVisibility(View.VISIBLE);
            phyQus1Layout.setVisibility(View.VISIBLE);
        }

        setRadioGroupListener();
    }

    @OnClick(R.id.txtAddPhoto)
    public void addPhoto() {
        isTakingToiletPhoto = false;
        if (checkPermissionGranted(Constants.CAMERA, this) && checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {
            deleteEntranceFile();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            file = newDir + "/" + sharedPreferenceManager.get_username() + "_" + theatreName + "_" + "entrance_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";

            entrancePhoto = file.substring(file.lastIndexOf("/") + 1, file.length());
            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            Uri outputFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(ProfilingTechnicalAspectsActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
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

    @OnClick(R.id.txtAddToiletPhoto)
    public void toiletPhoto() {
        isTakingToiletPhoto = true;
        if (checkPermissionGranted(Constants.CAMERA, this) && checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {
            deleteToiletPhoto();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            file = newDir + "/" + sharedPreferenceManager.get_username() + "_" + theatreName + "_" + "toilet_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";

            toiletPhoto = file.substring(file.lastIndexOf("/") + 1, file.length());
            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            Uri outputFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(ProfilingTechnicalAspectsActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
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
                if (isTakingToiletPhoto) {
                    toiletBitmap = selectedBitmap;
                    imgToiletPhoto.setVisibility(View.VISIBLE);
                    imgToiletPhoto.setImageBitmap(selectedBitmap);
                } else {
                    entranceBitmap = selectedBitmap;
                    imgPhoto.setVisibility(View.VISIBLE);
                    imgPhoto.setImageBitmap(selectedBitmap);
                }
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
                File compressedImage = new Compressor(ProfilingTechnicalAspectsActivity.this)
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

    private void deleteToiletPhoto() {
        if (!(toiletPhoto.isEmpty())) {
            File delete = new File(dir, toiletPhoto);
            if (delete.exists()) {
                delete.delete();
            }

        }
    }

    private void deleteEntranceFile() {
        if (!(entrancePhoto.isEmpty())) {
            File delete = new File(dir, entrancePhoto);
            if (delete.exists()) {
                delete.delete();
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteEntranceFile();
        deleteToiletPhoto();
    }



    private void setRadioGroupListener() {
        rgSounds.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.rbSoundDolby:
                        sounds = "1a1";
                        edtSoundOthers.setVisibility(View.GONE);
                        break;
                    case R.id.rbSoundDTS:
                        sounds = "1a2";
                        edtSoundOthers.setVisibility(View.GONE);
                        break;
                    case R.id.rbSoundImax:
                        sounds = "1a3";
                        edtSoundOthers.setVisibility(View.GONE);
                        break;
                    case R.id.rbSoundSurround:
                        sounds = "1a4";
                        edtSoundOthers.setVisibility(View.GONE);
                        break;
                    case R.id.rbSoundOthers:
                        sounds = "others";
                        edtSoundOthers.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        rgDigital.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb2D:
                        digitalSystem = "1b1";
                        hideView(edtDigitalOthers);
                        break;
                    case R.id.rb3D:
                        digitalSystem = "1b2";
                        hideView(edtDigitalOthers);
                        break;
                    case R.id.rb4D:
                        digitalSystem = "1b3";
                        hideView(edtDigitalOthers);
                        break;
                    case R.id.rb7D:
                        digitalSystem = "1b4";
                        hideView(edtDigitalOthers);
                        break;
                    case R.id.rbDigitalOthers:
                        digitalSystem = "others";
                        showView(edtDigitalOthers);
                }
            }
        });

        rgDigitalScreen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbUFO:
                        digitalScreen = "1c1";
                        hideView(edtDigitalScreen);
                        break;
                    case R.id.rbQUBE:
                        digitalScreen = "1c2";
                        hideView(edtDigitalScreen);
                        break;
                    case R.id.rbScrabble:
                        digitalScreen = "1c3";
                        hideView(edtDigitalScreen);
                        break;
                    case R.id.rb4K:
                        digitalScreen = "1c4";
                        hideView(edtDigitalScreen);
                        break;
                    case R.id.rbDigitalOthers:
                        digitalScreen = "others";
                        showView(edtDigitalScreen);
                }
            }

        });

        rgCleanCondition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbClean:
                        cleanCondition = "s5-1a";
                        break;
                    case R.id.rbveryClean:
                        cleanCondition = "s5-1b";
                        break;
                    case R.id.rbAverage:
                        cleanCondition = "s5-1c";
                        break;
                    case R.id.rbDirty:
                        cleanCondition = "s5-1d";
                        break;
                    case R.id.rbVeryDirty:
                        cleanCondition = "s5-1e";
                        break;
                }
            }
        });

        rgToiletCondition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbToiletClean:
                        toiletCondition = "s5-2a";
                        break;
                    case R.id.rbToiletveryClean:
                        toiletCondition = "s5-2b";
                        break;
                    case R.id.rbToiletAverage:
                        toiletCondition = "s5-2c";
                        break;
                    case R.id.rbToiletDirty:
                        toiletCondition = "s5-2d";
                        break;
                    case R.id.rbToiletVeryDirty:
                        toiletCondition = "s5-2e";
                        break;
                }
            }
        });

        rgToiletCleaned.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbOnce:
                        toiletsCleaned = "s5-3a";
                        break;
                    case R.id.rbTwice:
                        toiletsCleaned = "s5-3b";
                        break;
                    case R.id.rbThrice:
                        toiletsCleaned = "s5-3c";
                        break;
                    case R.id.rbOnce2Days:
                        toiletsCleaned = "s5-3d";
                        break;
                    case R.id.rbOnceWeekly:
                        toiletsCleaned = "s5-3e";
                        break;
                }
            }
        });

        rgHallCleaned.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbEachShow:
                        hallCleaned = "s5-4a";
                        break;
                    case R.id.rbHallOnce:
                        hallCleaned = "s5-4b";
                        break;
                    case R.id.rb2days:
                        hallCleaned = "s5-4c";
                        break;
                    case R.id.rbOnce3Days:
                        hallCleaned = "s5-4d";
                        break;
                    case R.id.rbhallWeek:
                        hallCleaned = "s5-4e";
                        break;
                }
            }
        });

        rgFumigation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbFugimationYes:
                        controlSystem = "s5-5a";
                        break;
                    case R.id.rbFumigationNo:
                        controlSystem = "s5-5b";
                        break;
                }
            }
        });
    }

    JSONObject technicalAspects, physicalSecurity, houseKeeping;

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


    @OnClick(R.id.txtNext)
    public void save() {


        finalObj = null;
        try {
            finalObj = new JSONObject(ticketCounter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!(sounds.isEmpty() || digitalScreen.isEmpty() || digitalSystem.isEmpty())) {

            if (sounds.equals("others")) {
                if (!edtSoundOthers.getText().toString().trim().isEmpty()) {
                    // sounds = edtSoundOthers.getText().toString().trim();
                    addValues("s3-1a5", edtSoundOthers.getText().toString().trim());
                } else {
                    toast(1);
                    return;
                }
            } else {
                addValues("s3-" + sounds, "yes");
            }
            if (digitalSystem.equals("others")) {
                if (!edtDigitalOthers.getText().toString().trim().isEmpty()) {
                    // digitalSystem = edtDigitalOthers.getText().toString().trim();
                    addValues("s3-1b5", edtDigitalOthers.getText().toString().trim());
                } else {
                    toast(1);
                    return;
                }
            } else {
                addValues("s3-" + digitalScreen, "yes");
            }
            if (digitalScreen.equals("others")) {
                if (!edtDigitalScreen.getText().toString().trim().isEmpty()) {
                    //digitalSystem = edtDigitalScreen.getText().toString().trim();
                    addValues("s3-1c5", edtDigitalScreen.getText().toString().trim());
                } else {
                    toast(1);
                    return;
                }
            } else {
                addValues("s3-" + digitalScreen, "yes");
            }

          /*  try {
                JSONObject object = new JSONObject();
                object.put("sounds", sounds);
                object.put("digitalSystem", digitalSystem);
                object.put("digitalScreen", digitalScreen);
                technology = object.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            toast(1);
            return;
        }

        if (isMultiplex) {
            if (!edtScreenPremise.getText().toString().trim().isEmpty()) {
                //screenPremise = edtScreenPremise.getText().toString().trim();
                addValues("s3-2", edtScreenPremise.getText().toString().trim());
            } else {
                toast(2);
                return;
            }
        }

        if (!isMultiplex) {

            if (!(edt2Wheeler.getText().toString().trim().isEmpty() || edt2Rate.getText().toString().trim().isEmpty() ||
                    edt3Wheeler.getText().toString().trim().isEmpty() || edt3Rate.getText().toString().isEmpty() ||
                    edt4Wheeler.getText().toString().trim().isEmpty() || edt4Rate.getText().toString().trim().isEmpty())) {

                addValues("s4-1a", edt2Wheeler.getText().toString().trim());
                addValues("s4-1a1", edt2Rate.getText().toString().trim());
                addValues("s4-1b", edt3Wheeler.getText().toString().trim());
                addValues("s4-1b1", edt3Rate.getText().toString().trim());
                addValues("s4-1c", edt4Wheeler.getText().toString().trim());
                addValues("s4-1c1", edt4Rate.getText().toString().trim());
               /* try {
                    JSONObject object = new JSONObject();
                    object.put("2wheeler", edt2Wheeler.getText().toString().trim() + ", Rate :" + edt2Rate.getText().toString().trim().isEmpty());
                    object.put("3wheeler", edt3Wheeler.getText().toString().trim() + ", Rate :" + edt3Rate.getText().toString().trim().isEmpty());
                    object.put("4wheeler", edt4Wheeler.getText().toString().trim() + ", Rate :" + edt4Rate.getText().toString().trim().isEmpty());
                    parkingFacility = object.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            } else {
                toast(1);
                return;
            }
        }

        if (!edtEntrance.getText().toString().trim().isEmpty()) {
            // entrances = edtEntrance.getText().toString().trim();
            addValues("s4-2", edtEntrance.getText().toString().trim());
        } else {
            toast(2);
            return;
        }

        if (entranceBitmap == null) {
            Toastmsg(ProfilingTechnicalAspectsActivity.this, "Please take photo for question 2");
            return;
        } else {
            addValues("s4-2entrancePhoto", entrancePhoto);
        }

       /* //physical security
        physicalSecurity = new JSONObject();
        try {
            if (!isMultiplex) {
                physicalSecurity.put("parkingFacility", parkingFacility);
            }
            physicalSecurity.put("entrances", entrances);
            physicalSecurity.put("entrancePhoto", entrancePhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        //house keeping
        if (cleanCondition.isEmpty()) {
            toast(1);
            return;
        } else {
            addValues(cleanCondition, "yes");
        }
        if (toiletCondition.isEmpty()) {
            toast(2);
            return;
        } else {
            addValues(toiletCondition, "yes");
        }
        if (toiletsCleaned.isEmpty()) {
            toast(3);
            return;
        } else {
            addValues(toiletsCleaned, "yes");
        }
        if (hallCleaned.isEmpty()) {
            toast(4);
            return;
        } else {
            addValues(hallCleaned, "yes");
        }
        if (controlSystem.isEmpty()) {
            toast(5);
            return;
        } else {
            addValues(controlSystem, "yes");
        }

        if (toiletBitmap == null) {
            Toastmsg(ProfilingTechnicalAspectsActivity.this, "Please take toilet photo for question 2");
            return;
        } else {
            addValues("s5-toiletPhoto", toiletPhoto);
        }
        Log.w(TAG, "House keeping :" + finalObj.toString());

        startActivity(new Intent(ProfilingTechnicalAspectsActivity.this, RespondentInfoActivity.class)
                .putExtra("all", finalObj.toString()).putExtra("fileName", mFileName)
                .putExtra("name", theatreName));
    }

    private void toast(int qus) {
        Toastmsg(ProfilingTechnicalAspectsActivity.this, "Please Answer Question :" + qus);
    }

    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View v) {
        v.setVisibility(View.GONE);
    }

    @BindView(R.id.rgFumigation)
    RadioGroup rgFumigation;
    @BindView(R.id.rgHallCleaned)
    RadioGroup rgHallCleaned;
    @BindView(R.id.txtAddToiletPhoto)
    TextView txtToiletPhoto;
    @BindView(R.id.imgToiletPhoto)
    ImageView imgToiletPhoto;
    @BindView(R.id.rgToiletCleaned)
    RadioGroup rgToiletCleaned;
    @BindView(R.id.rgToiletCondition)
    RadioGroup rgToiletCondition;
    @BindView(R.id.rgCleanCondition)
    RadioGroup rgCleanCondition;
    @BindView(R.id.txtAddPhoto)
    TextView txtAddPhoto;
    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;
    @BindView(R.id.edtEntrance)
    EditText edtEntrance;
    @BindView(R.id.txtPhyQus1)
    TextView txtPhyQus1;
    @BindView(R.id.phyQus1)
    TableLayout phyQus1Layout;
    @BindView(R.id.edt2wheeler)
    EditText edt2Wheeler;
    @BindView(R.id.edt2rate)
    EditText edt2Rate;
    @BindView(R.id.edt3Wheeler)
    EditText edt3Wheeler;
    @BindView(R.id.edt3rate)
    EditText edt3Rate;
    @BindView(R.id.edt4Wheeler)
    EditText edt4Wheeler;
    @BindView(R.id.edt4rate)
    EditText edt4Rate;
    @BindView(R.id.txtQus2)
    TextView txtQus2;
    @BindView(R.id.edtScreenPremise)
    EditText edtScreenPremise;
    @BindView(R.id.edtDigitalScreenOthers)
    EditText edtDigitalScreen;
    @BindView(R.id.rgDigitalScreen)
    RadioGroup rgDigitalScreen;
    @BindView(R.id.rgDigital)
    RadioGroup rgDigital;
    @BindView(R.id.edtDigitalOthers)
    EditText edtDigitalOthers;
    @BindView(R.id.rgSounds)
    RadioGroup rgSounds;
    @BindView(R.id.edtSoundOthers)
    EditText edtSoundOthers;
}
