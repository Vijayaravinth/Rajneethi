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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.utility.ViewsInfo;
import com.google.common.io.LineReader;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.PhotoActivity;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

/**
 * Created by DELL on 31-12-2017.
 */

public class ProfilingTicketCounterActivity extends Util implements TextWatcher {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.theatreProfiling)
    String title;
    String file, fileName = "";
    File newDir;
    File dir;
    File newfile;
    Bitmap selectedBitmap;
    private static final int CAMERA_REQUEST = 1888;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.software.cb.rajneethi.fileprovider";
    //qus 7  textview have to hide based on condition

    private boolean isMultiplex;
    @BindView(R.id.txtNext)
    TextView txtNext;
    private String seatCapacity = "", hallContain = "", recentMovie = "", maximumRate = "", famousActorName = "", eBookingFacility = "", teleBooking = "", hallSeat = "", crowdAttract = "", peopleShowCategory = "", languagePreference = "", kannadaActorName = "", theatreHighDemand = "";
    private boolean isKannadaNotSelected;

    String comfortable = "", upholstery = "", reclining = "";

    SharedPreferenceManager sharedPreferenceManager;

    String theatreName = "";
    String administration = "";

    String mFileName;

    @BindArray(R.array.actorName)
    String[] actorNames;
    String TAG = "TicketCounter";

    JSONObject finalObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiling_ticket_counter);
        ButterKnife.bind(this);
        setup_toolbar(toolbar);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        if (getIntent().getExtras() != null) {

            theatreName = getIntent().getExtras().getString("name");
            administration = getIntent().getExtras().getString("administration");
            mFileName = getIntent().getExtras().getString("fileName");
        }

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/Folder/");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");

        qusLayout.setVisibility(View.GONE);
        txtNext.setVisibility(View.GONE);
        setRadioGroupListener();

        setAdapter(edtFamousActor1);
        setAdapter(edtFamousActor2);
        setAdapter(edtFamousActor3);
        setAdapter(edtFamousActor4);
        setAdapter(edtFamousActor5);
        setAdapter(edtFamousActor6);
        setAdapter(edtFamousActor7);

        edtBalcony.addTextChangedListener(this);
        edtFirstClass.addTextChangedListener(this);
        edtSecondClass.addTextChangedListener(this);
        edtgandhiClass.addTextChangedListener(this);

        edtNormal.addTextChangedListener(this);
        edtSilver.addTextChangedListener(this);
        edtGold.addTextChangedListener(this);
        edtImax.addTextChangedListener(this);
        edtplatinum.addTextChangedListener(this);
        edtPlatinumPrime.addTextChangedListener(this);
    }

    private void setAdapter(AutoCompleteTextView actors) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, actorNames);
        actors.setAdapter(spinnerArrayAdapter);
        actors.setThreshold(2);

    }

    @OnClick(R.id.txtAddPhoto)
    public void takePhoto() {
        if (checkPermissionGranted(Constants.CAMERA, this) && checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {
            deleteFile();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            file = newDir + "/" + sharedPreferenceManager.get_username() + "_" + theatreName + "_" + "cinemaHall_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";

            fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            Uri outputFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(ProfilingTicketCounterActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
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

    //compress image
    private class CompressImage extends AsyncTask<String, Void, Void> {

        File file;

        @Override
        protected Void doInBackground(String... params) {
            file = new File(params[0]);
            try {
                File compressedImage = new Compressor(ProfilingTicketCounterActivity.this)
                        .setMaxWidth(500)
                        .setMaxHeight(500)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
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
                imgSeatPhoto.setVisibility(View.VISIBLE);
                imgSeatPhoto.setImageBitmap(selectedBitmap);
            }
            // }
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.w(TAG, "On text changed");

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.w(TAG, "On after text changed");

        try {

            if (isMultiplex) {
                int value = edtNormal.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtNormal.getText().toString().trim());
                value += edtSilver.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtSilver.getText().toString().trim());
                value += edtGold.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtGold.getText().toString().trim());
                value += edtImax.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtImax.getText().toString().trim());
                value += edtplatinum.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtplatinum.getText().toString().trim());
                value += edtPlatinumPrime.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtPlatinumPrime.getText().toString().trim());

                edtMultiplexTotal.setText(value + "");
                if (edtNormal.getText().toString().trim().isEmpty() && edtSilver.getText().toString().trim().isEmpty() &&
                        edtGold.getText().toString().trim().isEmpty() && edtImax.getText().toString().trim().isEmpty() &&
                        edtplatinum.getText().toString().trim().isEmpty() && edtPlatinumPrime.getText().toString().trim().isEmpty()) {
                    edtMultiplexTotal.setText("0");


                }
            } else {

                int value = edtBalcony.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtBalcony.getText().toString().trim());
                value += edtFirstClass.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtFirstClass.getText().toString().trim());
                value += edtSecondClass.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtSecondClass.getText().toString().trim());
                value += edtgandhiClass.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(edtgandhiClass.getText().toString().trim());

                //  Log.w(TAG, "total : " + total);
                edtSingleTotal.setText(value + "");

                if (edtBalcony.getText().toString().trim().isEmpty() && edtFirstClass.getText().toString().trim().isEmpty() &&
                        edtSecondClass.getText().toString().trim().isEmpty() && edtgandhiClass.getText().toString().trim().isEmpty()) {
                    edtSingleTotal.setText("0");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        deleteFile();
        super.onBackPressed();

    }

    private void setRadioGroupListener() {

        rgScreen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbSingle:
                        isMultiplex = false;
                        seatSingleScreen.setVisibility(View.VISIBLE);
                        seatMultiplex.setVisibility(View.GONE);
                        qusLayout.setVisibility(View.VISIBLE);
                        txtNext.setVisibility(View.VISIBLE);
                        moviePreferenceLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rbMultiplex:
                        isMultiplex = true;
                        seatSingleScreen.setVisibility(View.GONE);
                        seatMultiplex.setVisibility(View.VISIBLE);
                        qusLayout.setVisibility(View.VISIBLE);
                        txtNext.setVisibility(View.VISIBLE);
                        moviePreferenceLayout.setVisibility(View.GONE);
                }
            }
        });

        cbKannada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (b) {
                    txtQus7.setVisibility(View.GONE);
                    edtActorName1.setVisibility(View.GONE);
                    edtActorName2.setVisibility(View.GONE);
                    edtActorName3.setVisibility(View.GONE);
                    isKannadaNotSelected = false;
                } else {

                    txtQus7.setVisibility(View.VISIBLE);
                    edtActorName1.setVisibility(View.VISIBLE);
                    edtActorName2.setVisibility(View.VISIBLE);
                    edtActorName3.setVisibility(View.VISIBLE);
                    isKannadaNotSelected = true;
                }
            }
        });

        rgComfortable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbComfortableNo:
                        comfortable = "9a1";
                        break;
                    case R.id.rbComfortableYes:
                        comfortable = "9a2";
                        break;
                }
            }
        });

        rgUpholestry.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbGood:
                        upholstery = "9b1";
                        break;
                    case R.id.rbBad:
                        upholstery = "9b2";
                        break;
                    case R.id.rbAverage:
                        upholstery = "9b3";
                        break;
                }
            }
        });

        rgreClining.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbRecliningNo:
                        reclining = "9c2";
                        break;
                    case R.id.rbRecliningYes:
                        reclining = "9c1";
                        break;
                }
            }
        });

        rgHallCondition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbFan:
                        hallContain = "10a";
                        break;
                    case R.id.rbAC:
                        hallContain = "10b";
                        break;
                    case R.id.rbBoth:
                        hallContain = "10c";
                        break;
                    case R.id.rbNone:
                        hallContain = "10d";
                        break;
                }
            }
        });
    }


    private void addValues(String key, String value) {

        try {
            finalObj.put("s2-" + key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeValues(String key) {
        finalObj.remove("s2-" + key);
    }

    @OnClick(R.id.txtNext)
    public void save() {

        finalObj = null;
        try {
            finalObj = new JSONObject(administration);
            Log.w(TAG, "final object :" + finalObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isMultiplex) {
            if (!(edtNormal.getText().toString().trim().isEmpty() || edtSilver.getText().toString().trim().isEmpty() || edtGold.getText().toString().trim().isEmpty() || edtImax.getText().toString().trim().isEmpty()
                    || edtPlatinumPrime.getText().toString().trim().isEmpty() || edtplatinum.getText().toString().trim().isEmpty())) {

                if (!(edtNormalRate.getText().toString().trim().isEmpty() || edtSilverRate.getText().toString().trim().isEmpty() || edtGoldRate.getText().toString().trim().isEmpty() ||
                        edtImaxRate.getText().toString().trim().isEmpty() || edtPlatinumPrimeRate.getText().toString().trim().isEmpty() || edtPlatinumRate.getText().toString().trim().isEmpty())) {

                    addValues("1a", edtNormal.getText().toString().trim());
                    addValues("1a1", edtNormalRate.getText().toString().trim());
                    addValues("1b", edtSilver.getText().toString().trim());
                    addValues("1b1", edtSilverRate.getText().toString().trim());
                    addValues("1c", edtImax.getText().toString().trim());
                    addValues("1c1", edtImaxRate.getText().toString().trim());
                    addValues("1d", edtGold.getText().toString().trim());
                    addValues("1d1", edtGoldRate.getText().toString().trim());
                    addValues("1e", edtplatinum.getText().toString().trim());
                    addValues("1e1", edtPlatinumRate.getText().toString().trim());
                    addValues("1f", edtPlatinumPrime.getText().toString().trim());
                    addValues("1f1", edtPlatinumPrimeRate.getText().toString().trim());
                    addValues("1g", edtMultiplexTotal.getText().toString().trim());

                 /*   try {
                        JSONObject object = new JSONObject();
                        object.put("normal", edtNormal.getText().toString().trim() + " Rate :" + edtNormalRate.getText().toString().trim());
                        object.put("silver", edtSilver.getText().toString().trim() + " Rate :" + edtSilverRate.getText().toString().trim());
                        object.put("imax", edtImax.getText().toString().trim() + " Rate :" + edtImaxRate.getText().toString().trim());
                        object.put("gold", edtGold.getText().toString().trim() + " Rate :" + edtGoldRate.getText().toString().trim());
                        object.put("platinum", edtplatinum.getText().toString().trim() + " Rate :" + edtPlatinumRate.getText().toString().trim());
                        object.put("platinumPrime", edtPlatinumPrime.getText().toString().trim() + " Rate :" + edtPlatinumPrimeRate.getText().toString().trim());
                        object.put("total", edtMultiplexTotal.getText().toString().trim());
                        seatCapacity = object.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    toast(1);
                    return;
                }
            } else {
                toast(1);
                return;
            }

        } else {

            if (!(edtBalcony.getText().toString().trim().isEmpty() || edtFirstClass.getText().toString().trim().isEmpty() || edtSecondClass.getText().toString().trim().isEmpty() ||
                    edtgandhiClass.getText().toString().trim().isEmpty())) {

                if (!(edtBalconyRate.getText().toString().trim().isEmpty() || edtFirstClassRate.getText().toString().trim().isEmpty() || edtSecondClassRate.getText().toString().trim().isEmpty()
                        || edtgandhiClassRate.getText().toString().trim().isEmpty())) {
                    addValues("1a", edtBalcony.getText().toString().trim());
                    addValues("1a1", edtBalconyRate.getText().toString().trim());
                    addValues("1b", edtFirstClass.getText().toString().trim());
                    addValues("1b1", edtFirstClassRate.getText().toString().trim());
                    addValues("1c", edtSecondClass.getText().toString().trim());
                    addValues("1c1", edtSecondClassRate.getText().toString().trim());
                    // addValues("1d", edtGold.getText().toString().trim());
                    addValues("1d1", edtgandhiClassRate.getText().toString().trim());
                    addValues("1e", edtSingleTotal.getText().toString().trim());
//                    try {
//                        JSONObject object = new JSONObject();
//                        object.put("balcony", edtBalcony.getText().toString().trim() + " Rate :" + edtBalconyRate.getText().toString().trim());
//                        object.put("firstClass", edtFirstClass.getText().toString().trim() + " Rate :" + edtFirstClassRate.getText().toString().trim());
//                        object.put("secondclass", edtSecondClass.getText().toString().trim() + " Rate :" + edtSecondClassRate.getText().toString().trim());
//                        object.put("gandhiClass", edtGold.getText().toString().trim() + " Rate :" + edtgandhiClassRate.getText().toString().trim());
//                        object.put("total", edtSingleTotal.getText().toString().trim());
//                        seatCapacity = object.toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    toast(1);
                    return;
                }
            } else {
                toast(1);
                return;
            }
        }

        //website
        if (!edtEbookingWebsite.getText().toString().isEmpty()) {
            eBookingFacility = edtEbookingWebsite.getText().toString().trim();
        } else {
            toast(2);
            return;
        }

        //tele booking
        if (!edtTicketBookingNo.getText().toString().trim().isEmpty()) {

            teleBooking = edtTicketBookingNo.getText().toString().trim();
        } else {
            toast(3);
            return;
        }

        //crowd attract
        String crowd = crowedAttract();
        if (!crowd.isEmpty()) {
            crowdAttract = crowd;
        } else {
            toast(4);
            return;
        }

        //prople attract which show
        if (MorningShow().isEmpty()) {
            toast(5);
            return;
        }
        if (MatineeShow().isEmpty()) {
            toast(5);
            return;
        }
        if (NightShow().isEmpty()) {
            toast(5);
            return;
        }

        if (!isMultiplex) {
            //language movie generally used
            String lang = languagePreference();
            if (!lang.isEmpty()) {
                languagePreference = lang;
            } else {
                toast(6);
                return;
            }

            if (isKannadaNotSelected) {
                if (!(edtActorName1.getText().toString().trim().isEmpty() || edtActorName2.getText().toString().trim().isEmpty() || edtActorName3.getText().toString().trim().isEmpty())) {
                    // kannadaActorName = edtActorName1.getText().toString().trim()+""
                    addValues("7a", edtActorName1.getText().toString().trim());
                    addValues("7b", edtActorName2.getText().toString().trim());
                    addValues("7c", edtActorName3.getText().toString().trim());
                 /*   try {
                        JSONObject object = new JSONObject();
                        object.put("Actor1", edtActorName1.getText().toString().trim());
                        object.put("Actor2", edtActorName2.getText().toString().trim());
                        object.put("Actor3", edtActorName3.getText().toString().trim());
                        kannadaActorName = object.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    toast(7);
                    return;
                }
            }
        }

        //theatre high demand
        String demand = getTheatreHighDemand();
        if (!demand.isEmpty()) {
            theatreHighDemand = demand;
        } else {
            toast(8);
            return;
        }

        //cinema hall
        if (!(comfortable.isEmpty() || upholstery.isEmpty() || reclining.isEmpty())) {

            addValues(comfortable, "yes");
            addValues(upholstery, "yes");
            addValues(reclining, "yes");

        } else {
            toast(9);
            return;
        }

        //hall contain
        if (!hallContain.isEmpty()) {
            addValues(hallContain, "yes");
        } else {
            toast(10);
            return;
        }

        //top 7 actor
        //  String name = get7Actorname();
        if (!(edtFamousActor1.getText().toString().trim().isEmpty() || edtFamousActor2.getText().toString().trim().isEmpty() || edtFamousActor3.getText().toString().trim().isEmpty() ||
                edtFamousActor4.getText().toString().trim().isEmpty() || edtFamousActor5.getText().toString().trim().isEmpty() || edtFamousActor6.getText().toString().trim().isEmpty() ||
                edtFamousActor7.getText().toString().trim().isEmpty())) {
            // famousActorName = name;
            addValues("11a", edtFamousActor1.getText().toString().trim());
            addValues("11b", edtFamousActor2.getText().toString().trim());
            addValues("11c", edtFamousActor3.getText().toString().trim());
            addValues("11d", edtFamousActor4.getText().toString().trim());
            addValues("11e", edtFamousActor5.getText().toString().trim());
            addValues("11f", edtFamousActor6.getText().toString().trim());
            addValues("11g", edtFamousActor7.getText().toString().trim());

        } else {
            toast(11);
            return;
        }

        //recent movies run in 25 days
        if (!(edtKannadaMovie.getText().toString().isEmpty() || edtNonKannadaMovie.getText().toString().trim().isEmpty())) {

            addValues("12a", edtKannadaMovie.getText().toString().trim());
            addValues("12b", edtNonKannadaMovie.getText().toString().trim());
            //recentMovie = "Kannada : " + edtKannadaMovie.getText().toString() + ", Non Kannada :" + edtNonKannadaMovie.getText().toString().trim();
        } else {
            toast(12);
            return;
        }

        //maximum rate for movie
        if (!(edtMaxKannadaAdmission.getText().toString().trim().isEmpty() || edtKannadaRate.getText().toString().trim().isEmpty() || edtNonKannadaMaxAdmission.getText().toString().trim().isEmpty() || edtNonKannadaRate.getText().toString().trim().isEmpty())) {

            addValues("13a", edtMaxKannadaAdmission.getText().toString().trim());
            addValues("13a1", edtKannadaRate.getText().toString().trim());
            addValues("13b", edtNonKannadaMaxAdmission.getText().toString().trim());
            addValues("13b1", edtNonKannadaRate.getText().toString().trim());

            /*try {
                JSONObject object = new JSONObject();
                object.put("kanndaMovie", edtMaxKannadaAdmission.getText().toString().trim() + ", Rate " + edtKannadaRate.getText().toString().trim());
                object.put("nonKanndaMovie", edtNonKannadaMaxAdmission.getText().toString().trim() + ", Rate " + edtNonKannadaRate.getText().toString().trim());
                maximumRate = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            toast(13);
            return;
        }


        if (selectedBitmap == null) {
            Toastmsg(ProfilingTicketCounterActivity.this, "Please take photo for question 9");
            return;
        }

        try {

            // ticketObj.put("seatCapacity", seatCapacity);
            finalObj.put("s2-eBookingFacility", eBookingFacility);
            finalObj.put("s2-teleBooking", teleBooking);
            //  finalObj.put("crowdAttract", crowdAttract);
            // finalObj.put("categoryPeople", peopleShowCategory);
/*            if (!isMultiplex) {
              //  finalObj.put("languagePreference", languagePreference);
                if (isKannadaNotSelected) {
                    finalObj.put("kanndaActorName", kannadaActorName);
                }
            }*/

            //  finalObj.put("highDemand", theatreHighDemand);
            // finalObj.put("cinemaHall", hallSeat);
            //finalObj.put("hallContain", hallContain);
            // finalObj.put("top7Actor", famousActorName);
            // finalObj.put("recentMovie", recentMovie);
            //finalObj.put("maximumRateAdmission", maximumRate);
            finalObj.put("s2-cinemaHallPhoto", fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length()));

            Log.w(TAG, "Ticket counter : " + finalObj.toString());
            startActivity(new Intent(ProfilingTicketCounterActivity.this, ProfilingTechnicalAspectsActivity.class).putExtra("fileName", mFileName).putExtra("ticketCounter", finalObj.toString()).putExtra("name", theatreName).putExtra("isMultiplex", isMultiplex));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String crowedAttract() {
        String crowdAttract = "";
        if (cbFamily.isChecked()) {
            addValues("4a", "yes");
            crowdAttract += "," + "Family";
        } else {
            removeValues("4a");
        }
        if (cbYouth.isChecked()) {
            addValues("4b", "yes");
            crowdAttract += "," + "Youths/Students";

        } else {
            removeValues("4a");
        }
        if (cbMass.isChecked()) {
            addValues("4c", "yes");
            crowdAttract += "," + "Mass/Adults";

        } else {
            removeValues("4c");
        }
        if (cbCouple.isChecked()) {

            crowdAttract += "," + "Couple";
            addValues("4d", "yes");
        } else {
            removeValues("4d");
        }
        return crowdAttract;
    }

  /*  private String get7Actorname() {
        String name = "";

        *//*try {
            JSONObject object = new JSONObject();
            object.put("Actor1", edtFamousActor1.getText().toString().trim());
            object.put("Actor2", edtFamousActor2.getText().toString().trim());
            object.put("Actor3", edtFamousActor3.getText().toString().trim());
            object.put("Actor4", edtFamousActor4.getText().toString().trim());
            object.put("Actor5", edtFamousActor5.getText().toString().trim());
            object.put("Actor6", edtFamousActor6.getText().toString().trim());
            object.put("Actor7", edtFamousActor7.getText().toString().trim());

            name = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
*//*
        return name;
    }*/

    private String getTheatreHighDemand() {
        String highDemand = "";

        if (cbWeekends.isChecked()) {
            addValues("8a", "yes");
            highDemand += "," + "Weekends";

        } else {
            removeValues("8a");
        }
        if (cbMorningShow.isChecked()) {

            highDemand += "," + "Morning Show";
            addValues("8b", "yes");
        } else {
            removeValues("8b");
        }
        if (cbEveningShow.isChecked()) {

            highDemand += "," + "Evening Show";
            addValues("8c", "yes");
        } else {
            removeValues("8c");
        }


        if (cbMatniee.isChecked()) {

            highDemand += "," + "Matinee";
            addValues("8d", "yes");
        } else {
            removeValues("8d");
        }
        if (cbPublicHolidays.isChecked()) {
            addValues("8e", "yes");
            highDemand += "," + "Public Holidays";

        } else {
            removeValues("8e");
        }

        if (cbFestivals.isChecked()) {

            highDemand += "," + "Festivals";
            addValues("8f", "yes");
        } else {
            removeValues("8f");
        }

        if (cbSummerVocation.isChecked()) {

            highDemand += "," + "Summer Vocation";
            addValues("8g", "yes");
        } else {
            removeValues("8g");
        }

        if (cbHitFilms.isChecked()) {
            addValues("8h", "yes");
            highDemand += "," + "Hit Films";

        } else {
            removeValues("8h");
        }

        return highDemand;
    }

    private String languagePreference() {
        String languages = "";
        if (cbKannada.isChecked()) {
            languages += "," + "Kannada";
            addValues("6a", "yes");
        } else {
            removeValues("6a");
        }
        if (cbHindi.isChecked()) {
            addValues("6b", "yes");
            languages += "," + "Hindi";

        } else {
            removeValues("6b");
        }
        if (cbTelugu.isChecked()) {

            languages += "," + "Telugu";
            addValues("6c", "yes");
        } else {
            removeValues("6c");
        }
        if (cbTamil.isChecked()) {
            addValues("6d", "yes");
            languages += "," + "Tamil";

        } else {
            removeValues("6d");
        }
        if (cbMalayalam.isChecked()) {
            addValues("6e", "yes");
            languages += "," + "Malayalam";

        } else {
            removeValues("6e");
        }
        if (cbEnglish.isChecked()) {

            languages += "," + "English";
            addValues("6f", "yes");
        } else {
            removeValues("6f");
        }
        if (cbMarati.isChecked()) {

            languages += "," + "Marati";
            addValues("6g", "yes");
        } else {
            removeValues("6g");
        }
        if (cbTulu.isChecked()) {

            languages += "," + "Tulu";
            addValues("6h", "yes");
        } else {
            removeValues("6h");
        }
        return languages;
    }

    private String MorningShow() {
        String morningShow = "";
        if (cbMorningCollege.isChecked()) {

            addValues("5a1", "yes");
            morningShow += "," + "College Geors";

        } else {
            removeValues("5a1");
        }
        if (cbMorningFamilies.isChecked()) {
            addValues("5a2", "yes");
            morningShow += "," + "Families";

        } else {
            removeValues("5a2");
        }
        if (cbMorningCouples.isChecked()) {
            addValues("5a3", "yes");
            morningShow += "," + "Couple and Mass/Adults";

        } else {
            removeValues("5a3");
        }

        return morningShow;
    }

    private String MatineeShow() {
        String morningShow = "";
        if (cbMatnieeCollege.isChecked()) {

            morningShow += "," + "College Geors";
            addValues("5b1", "yes");
        } else {
            removeValues("5b1");
        }
        if (cbMatnieeFamilies.isChecked()) {

            morningShow += "," + "Families";
            addValues("5b2", "yes");
        } else {
            removeValues("5b2");
        }
        if (cbMatnieeCouples.isChecked()) {
            addValues("5b3", "yes");
            morningShow += "," + "Couple and Mass/Adults";

        } else {
            removeValues("5b3");
        }

        return morningShow;
    }

    private String NightShow() {
        String morningShow = "";
        if (cbNightCollege.isChecked()) {
            addValues("5c1", "yes");
            morningShow += "," + "College Geors";

        } else {
            removeValues("5c1");
        }
        if (cbNightFamilies.isChecked()) {
            addValues("5c2", "yes");
            morningShow += "," + "Families";

        } else {
            removeValues("5c2");
        }
        if (cbNightCouples.isChecked()) {

            morningShow += "," + "Couple and Mass/Adults";
            addValues("5c3", "yes");
        } else {
            removeValues("5c3");
        }

        return morningShow;
    }

    private void toast(int qus) {
        Toastmsg(ProfilingTicketCounterActivity.this, "Answer Question " + qus);
    }

    @BindView(R.id.edtKannadaMaxAdmission)
    EditText edtMaxKannadaAdmission;
    @BindView(R.id.edtKannadaRate)
    EditText edtKannadaRate;
    @BindView(R.id.edtNonKannadaMaxAdmission)
    EditText edtNonKannadaMaxAdmission;
    @BindView(R.id.edtNonKannadaRate)
    EditText edtNonKannadaRate;
    @BindView(R.id.edtKanndaMovie)
    EditText edtKannadaMovie;
    @BindView(R.id.edtNonKannadaMovie)
    EditText edtNonKannadaMovie;
    @BindView(R.id.edtFamousActor1)
    AutoCompleteTextView edtFamousActor1;
    @BindView(R.id.edtFamousActor2)
    AutoCompleteTextView edtFamousActor2;
    @BindView(R.id.edtFamousActor3)
    AutoCompleteTextView edtFamousActor3;
    @BindView(R.id.edtFamousActor4)
    AutoCompleteTextView edtFamousActor4;
    @BindView(R.id.edtFamousActor5)
    AutoCompleteTextView edtFamousActor5;
    @BindView(R.id.edtFamousActor6)
    AutoCompleteTextView edtFamousActor6;
    @BindView(R.id.edtFamousActor7)
    AutoCompleteTextView edtFamousActor7;
    @BindView(R.id.rgHallCondition)
    RadioGroup rgHallCondition;//fan and ac
    @BindView(R.id.imgSeatPhoto)
    ImageView imgSeatPhoto;
    @BindView(R.id.rgComfortable)
    RadioGroup rgComfortable;
    @BindView(R.id.rgUpholstery)
    RadioGroup rgUpholestry;
    @BindView(R.id.rgReclining)
    RadioGroup rgreClining;
    @BindView(R.id.cbEveningShow)
    CheckBox cbEveningShow;
    @BindView(R.id.cbMatniee)
    CheckBox cbMatniee;
    @BindView(R.id.cbWeekends)
    CheckBox cbWeekends;
    @BindView(R.id.cbPublicHolidays)
    CheckBox cbPublicHolidays;
    @BindView(R.id.cbFestival)
    CheckBox cbFestivals;
    @BindView(R.id.cbSummerVocation)
    CheckBox cbSummerVocation;
    @BindView(R.id.cbHitFilms)
    CheckBox cbHitFilms;
    @BindView(R.id.cbMorningShow)
    CheckBox cbMorningShow;
    @BindView(R.id.edtActorName1)
    EditText edtActorName1;
    @BindView(R.id.edtActorName2)
    EditText edtActorName2;
    @BindView(R.id.edtActorName3)
    EditText edtActorName3;
    @BindView(R.id.ticketQus7)
    TextView txtQus7;
    @BindView(R.id.cbKannada)
    CheckBox cbKannada;
    @BindView(R.id.cbTelugu)
    CheckBox cbTelugu;
    @BindView(R.id.cbTamil)
    CheckBox cbTamil;
    @BindView(R.id.cbEnglish)
    CheckBox cbEnglish;
    @BindView(R.id.cbMarati)
    CheckBox cbMarati;
    @BindView(R.id.cbTulu)
    CheckBox cbTulu;
    @BindView(R.id.cbMalayalam)
    CheckBox cbMalayalam;
    @BindView(R.id.cbHindi)
    CheckBox cbHindi;
    @BindView(R.id.moviePreferenceLayout)
    LinearLayout moviePreferenceLayout;
    @BindView(R.id.cbNightCollege)
    CheckBox cbNightCollege;
    @BindView(R.id.cbNightFamilies)
    CheckBox cbNightFamilies;
    @BindView(R.id.cbNightCouples)
    CheckBox cbNightCouples;
    @BindView(R.id.cbMatnieeCollege)
    CheckBox cbMatnieeCollege;
    @BindView(R.id.cbMatnieeFamilies)
    CheckBox cbMatnieeFamilies;
    @BindView(R.id.cbMatnieeCouples)
    CheckBox cbMatnieeCouples;
    @BindView(R.id.cbMorningCollege)
    CheckBox cbMorningCollege;
    @BindView(R.id.cbMorningFamilies)
    CheckBox cbMorningFamilies;
    @BindView(R.id.cbMorningCouples)
    CheckBox cbMorningCouples;
    @BindView(R.id.cbMass)
    CheckBox cbMass;
    @BindView(R.id.cbCouple)
    CheckBox cbCouple;
    @BindView(R.id.cbYouths)
    CheckBox cbYouth;
    @BindView(R.id.cbfamily)
    CheckBox cbFamily;
    @BindView(R.id.edtTicketbookingNo)
    EditText edtTicketBookingNo;
    @BindView(R.id.edtEbookingWebsite)
    EditText edtEbookingWebsite;
    @BindView(R.id.edtMultiplexTotal)
    EditText edtMultiplexTotal;
    @BindView(R.id.edtPlatinumPrime)
    EditText edtPlatinumPrime;
    @BindView(R.id.edtPlatinumPrimeRate)
    EditText edtPlatinumPrimeRate;
    @BindView(R.id.edtPlatniumRate)
    EditText edtPlatinumRate;
    @BindView(R.id.edtPlatinum)
    EditText edtplatinum;
    @BindView(R.id.edtImax)
    EditText edtImax;
    @BindView(R.id.edtImaxRate)
    EditText edtImaxRate;
    @BindView(R.id.edtGold)
    EditText edtGold;
    @BindView(R.id.edtGoldRate)
    EditText edtGoldRate;
    @BindView(R.id.edtSilverRate)
    EditText edtSilverRate;
    @BindView(R.id.edtSilver)
    EditText edtSilver;
    @BindView(R.id.edtNormalRate)
    EditText edtNormalRate;
    @BindView(R.id.edtNormal)
    EditText edtNormal;
    @BindView(R.id.edtSingleTotal)
    EditText edtSingleTotal;
    @BindView(R.id.edtGandhiClassRate)
    EditText edtgandhiClassRate;
    @BindView(R.id.edtGandhiClass)
    EditText edtgandhiClass;
    @BindView(R.id.edtSecondClassRate)
    EditText edtSecondClassRate;
    @BindView(R.id.edtSecondClass)
    EditText edtSecondClass;
    @BindView(R.id.edtFirstClassRate)
    EditText edtFirstClassRate;
    @BindView(R.id.edtFirstClass)
    EditText edtFirstClass;
    @BindView(R.id.edtBalconyRate)
    EditText edtBalconyRate;
    @BindView(R.id.edtBalcony)
    EditText edtBalcony;
    @BindView(R.id.seatSingleScreen)
    TableLayout seatSingleScreen;
    @BindView(R.id.seatMultiplex)
    TableLayout seatMultiplex;
    @BindView(R.id.qusLayout)
    LinearLayout qusLayout;
    @BindView(R.id.rgScreen)
    RadioGroup rgScreen;
}
