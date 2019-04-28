package com.software.cb.rajneethi.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

import static android.view.View.GONE;

/**
 * Created by Vijay on 11-03-2017.
 */

public class AddVoterActivity extends Util implements checkInternet.ConnectivityReceiverListener, OnMapReadyCallback, LocationListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

       @BindView(R.id.edt_state)
    EditText edt_state;

    @BindView(R.id.edt_assemble_constituency)
    EditText edt_assemble_constituency;

    @BindView(R.id.edt_name)
    EditText edt_name;

    @BindView(R.id.edt_surename)
    EditText edt_surname;

    @BindView(R.id.rg_gender)
    RadioGroup rg_gender;

    @BindView(R.id.edt_dob)
    EditText edt_dob;

    @BindView(R.id.edt_pob_state)
    EditText edt_pob_state;

    @BindView(R.id.district_spinner)
    Spinner spinner_district;

    @BindView(R.id.edt_village_town)
    EditText edt_village_town;

    @BindView(R.id.edt_place_of_birth)
    EditText edt_pob;

    @BindView(R.id.rg_relationship)
    RadioGroup rg_relationship;

    @BindView(R.id.edt_relationship_name)
    EditText edt_relation_name;

    @BindView(R.id.edt_realtionsurename)
    EditText edt_relationsurname;

    @BindView(R.id.edt_houseno)
    EditText edt_houseno;

    @BindView(R.id.edt_street)
    EditText edt_street;

    @BindView(R.id.edt_fa_village_town)
    EditText edt_fa_village_town;

    @BindView(R.id.edt_postoffice)
    EditText edt_post_office;

    @BindView(R.id.edt_pincode)
    EditText edt_pincode;

    @BindView(R.id.edt_taluk)
    EditText edt_taluk;

    @BindView(R.id.fa_district_spinner)
    Spinner fa_district_spinner;

    @BindView(R.id.edt_mobile)
    EditText edt_mobile;

    @BindView(R.id.edt_email)
    EditText edt_email;


    @BindView(R.id.image_photograph)
    ImageView image_photograph;

    @BindView(R.id.container)
    LinearLayout container;

    @BindView(R.id.image_address_proof)
    ImageView image_address_proof;

    @BindView(R.id.application_detailshow)
    LinearLayout applicationshow;
    @BindView(R.id.placebirthdetailshow)
    LinearLayout placebirthshow;
    @BindView(R.id.relatinshipdetailshow)
    LinearLayout relationshow;
    @BindView(R.id.fulladdressshow)
    LinearLayout fulladdressshow;
    @BindView(R.id.documentshow)
    LinearLayout documentshow;

    @BindView(R.id.cardview5)
    CardView cardview5;
    @BindView(R.id.cardview4)
    CardView cardview4;
    @BindView(R.id.cardview3)
    CardView cardview3;
    @BindView(R.id.cardview1)
    CardView cardview1;
    @BindView(R.id.cardview2)
    CardView cardview2;

    String dir;
    File newfile;
    String file;
    String userType = "";

    Uri outputFileUri;
    boolean photograph = false, id_proof = false, address_proof = false;
    String path, address_path, id_path, type;

    private SharedPreferenceManager sharedPreferenceManager;
    private static final int CAMERA_REQUEST = 1888;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.software.cb.rajneethi.fileprovider";
    private static final int PIC_CROP = 1;
    private ArrayList<Questions> list = new ArrayList<>();
    private HashMap<String, String> survey_result = new HashMap<>();
    private ArrayList<HashMap<String, String>> result_map = new ArrayList<>();
    private HashMap<String, EditText> edit_text_list = new HashMap<>();
    @BindView(R.id.image_id_proof)
    ImageView image_id_proof;

    String currentImageCapture;

    private static final String TAG = "AddVoter";

    String gender, relationship_type, pob_district, fa_district;


    private HashMap<String, EditText> single_choice_edittext = new HashMap<>();


    private MyDatabase db;
    private GPSTracker gpsTracker;

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 101;


    private ArrayList<String> booth_list = new ArrayList<>();

    @BindView(R.id.booth_list_spinner)
    Spinner booth_list_spinner;

    String booth_name = "";

    @BindView(R.id.btn_add)
    Button btn_add;


    @BindView(R.id.txt_is_form_required)
    TextView txt_form_required;

    @BindView(R.id.spiner_form6)
    Spinner spinner_form6;

    @BindView(R.id.form6_layout)
    LinearLayout form_6_layout;

    boolean is_form_6_required = false;

    private String mFileName = null;
    private MediaRecorder mRecorder = null;

    @BindString(R.string.survey)
    String toolbarTitle;

    @BindString(R.string.noInternet)
    String noInternet;

    IOSDialog dialog;

    File newDir;

    int qusCount = 0;

    @BindView(R.id.form6card)
    CardView form6cardview;

    @BindView(R.id.txtLatitude)
    TextView txtLatitude;
    @BindView(R.id.txtLongitude)
    TextView txtLongitude;
    @BindView(R.id.setLocationLayout)
    View setLocationLayout;
    @BindView(R.id.getLocationLayout)
    View getLocationLayout;
    SupportMapFragment mapFragment;
    private boolean isMapClicked = false;

    @BindView(R.id.voterCardLayout)
    CardView voterCardLayout;

    @BindView(R.id.voterCardCardView)
    CardView voterCardView;
    @BindView(R.id.spinner_voterId)
    Spinner spinnerVoterId;

    @BindView(R.id.edtVoterCard)
    EditText edtVoterCardNumber;

    @BindView(R.id.txtVoterCard)
    TextView txtVoterCard;

    private GoogleMap map;

    android.location.Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private String location_string;
    private static LocationManager locationManager;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 0 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 60000;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    LatLng dest;

    boolean isHaveVoterCard = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voter);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        ButterKnife.bind(this);
        setup_toolbar_with_back(toolbar,toolbarTitle);

        // Toastmsg(AddVoterActivity.this, "Booth After Activity = " + sharedPreferenceManager.get_survey_booth());;

        dialog = show_dialog(AddVoterActivity.this, false);
        if (getIntent().getExtras() != null) {
            userType = getIntent().getExtras().getString("userType");


        }
        gpsTracker = new GPSTracker(AddVoterActivity.this);

        Log.w(TAG, "Location " + " latitude :" + gpsTracker.getLatitude() + " longitude " + gpsTracker.getLongitude());
        db = new MyDatabase(this);
        db.delete_checkbox_values();


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


        hide_question_layout();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        setLocationLayout.setVisibility(GONE);
        mapFragment.getView().setVisibility(GONE);


        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radio_male:
                        gender = "male";
                        break;
                    case R.id.radio_female:
                        gender = "female";
                        break;
                    case R.id.radio_other:
                        gender = "other";
                        break;
                }
            }
        });

        rg_relationship.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_father:
                        relationship_type = "father";
                        break;
                    case R.id.radio_mother:
                        relationship_type = "mother";
                        break;
                    case R.id.radio_husband:
                        relationship_type = "other";
                        break;
                    case R.id.radio_relation_other:
                        relationship_type = "other";
                        break;
                }
            }
        });


        check_form_6_required();

        init_spinner_Values();


        try {
            Cursor c = db.getBooths();
            if (c.moveToFirst()) {
                do {
                    booth_list.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
            loadDataToSpinner();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            if (checkInternet.isConnected()) {
                dialog.show();
                get_booth_details();
            } else {

                Toastmsg(AddVoterActivity.this, noInternet);
                finish_activity();
            }
        }




    }




    public Location getLocation() {
        try {

            getLocationLayout.setVisibility(View.VISIBLE);
            Log.w(TAG, "inside get location called ");
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                Log.w(TAG, "GPS Enabled");
                canGetLocation = true;
                isGPSEnabled = true;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        //if (latitude == 0L && longitude != 0L) {

                        getLocationLayout.setVisibility(GONE);
                        Log.w(TAG, "is Map clicked : " + isMapClicked);
                        if (!isMapClicked && !booth_name.isEmpty()) {
                            setLocationLayout.setVisibility(View.VISIBLE);
                        }
                        txtLatitude.setText(latitude + "");
                        txtLongitude.setText(longitude + "");


                        dest = new LatLng(latitude, longitude);
                        LatLng myLocation = new LatLng(latitude, longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                myLocation).zoom(18).build();
                        map.clear();

                        map.addMarker(new MarkerOptions().position(myLocation)
                                .title("My Location"));
                        //   map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                        //   map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        Log.w(TAG, " Latitude " + latitude + " Longitude " + longitude);
                    }
                }

            } else {
                Toastmsg(AddVoterActivity.this, "please Enable gps");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        Log.w(TAG, "get location called before");
        getLocation();

    }

    //start or stop record
    private void onRecord(boolean start) {
        Log.w(TAG, "Inside on record");
        if (start) {

            startRecording();
        } else {
            stopRecording();
        }
    }

    //start recording
    private void startRecording() {
        Log.w(TAG, "Start recording");
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/";
        mFileName += "AUD-" + sharedPreferenceManager.get_username() + "_" + booth_name + "_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        try {
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //stop recording
    private void stopRecording() {

        Log.w(TAG, "Stop recording method");
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRecord(false);
    }

    /*hide form 6 layout*/
    private void hide_form6() {
        if (form_6_layout.isShown()) {
            form_6_layout.setVisibility(GONE);
        }
    }

    /*show  form 6 layout*/
    private void show_form6() {
        if (!form_6_layout.isShown()) {
            form_6_layout.setVisibility(View.VISIBLE);
        }

    }

    /*set is form 6 required*/
    private void set_form6_required(boolean is_required) {
        is_form_6_required = is_required;
    }

    /*check form 6 is required*/
    private void check_form_6_required() {

        String[] options = getResources().getStringArray(R.array.form6);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinnerVoterId.setAdapter(spinnerArrayAdapter);

        spinnerVoterId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                set_form6_required(false);

                if (i != 0) {

                    if (!btn_add.isShown()) {
                        btn_add.setVisibility(View.VISIBLE);
                    }

                    if (!container.isShown()) {
                        container.setVisibility(View.VISIBLE);
                    }



                    if (i == 1) {
                        isHaveVoterCard = true;
                        if (!voterCardLayout.isShown()) {
                            voterCardLayout.setVisibility(View.VISIBLE);
                        }

                    } else {
                        isHaveVoterCard = false;
                        voterCardLayout.setVisibility(View.GONE);
                    }
                } else {
                    voterCardLayout.setVisibility(View.GONE);
                    btn_add.setVisibility(View.GONE);
                    container.setVisibility(View.GONE);
                    isHaveVoterCard = false;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
    }


    @BindString(R.string.enterVoterCard)
    String enterVoterCard;
    //hide question layout
    private void hide_question_layout() {

        container.setVisibility(GONE);
        btn_add.setVisibility(GONE);
        txt_form_required.setVisibility(GONE);
        spinner_form6.setVisibility(GONE);

    }

    //show question layout
    private void show_question_layout() {

        if (qusCount > 0) {
            if (!container.isShown()) {
                container.setVisibility(View.VISIBLE);
            }

            if (!txt_form_required.isShown()) {
                txt_form_required.setVisibility(View.VISIBLE);
            }


            if (!spinner_form6.isShown()) {
                spinner_form6.setVisibility(View.VISIBLE);
            }
        } else {
            hide_question_layout();
            Toastmsg(AddVoterActivity.this, noQuestion);
        }
    }

    @BindString(R.string.selectBooth)
    String selectBooth;

    @OnClick(R.id.btnOk)
    public void startSurvey() {

        mapFragment.getView().setVisibility(GONE);
        show_question_layout();
        deleteAudioFile();
        onRecord(false);
        onRecord(true);

        isMapClicked = true;

        setLocationLayout.setVisibility(GONE);


        voterCardView.setVisibility(View.VISIBLE);
        form6cardview.setVisibility(View.GONE);
        spinnerVoterId.setVisibility(View.VISIBLE);
        txtVoterCard.setVisibility(View.VISIBLE);

    }


    private void showMap() {
        setLocationLayout.setVisibility(View.VISIBLE);
        mapFragment.getView().setVisibility(View.VISIBLE);
        getLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    //set adapter for booth
    private void set_adapter_for_booth_list() {

        booth_list.add(0, selectBooth);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, booth_list);
        booth_list_spinner.setAdapter(spinnerArrayAdapter);

        get_questions();
        renderDynamicLayout();

        booth_list_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    showMap();
                    booth_name = booth_list_spinner.getSelectedItem().toString();

                } else {
                    mapFragment.getView().setVisibility(GONE);
                    hide_question_layout();
                    booth_name = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*load data to spinner*/
    private void loadDataToSpinner() {
        if (booth_list.size() > 0) {
            setLocationLayout.setVisibility(GONE);
            set_adapter_for_booth_list();
        } else {
            hide_question_layout();
            Toastmsg(AddVoterActivity.this, noBoothAllocated);
            finish_activity();
        }
    }


    private LinearLayout createHeaderLayout(String title, int pos) {

        LinearLayout layout = new LinearLayout(AddVoterActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(params);

        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(AddVoterActivity.this);
        imageView.setId(pos);
        imageView.setImageDrawable(ContextCompat.getDrawable(AddVoterActivity.this, R.drawable.answered));

        imageView.setVisibility(GONE);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(70, 70);
        imageView.setLayoutParams(params1);
        layout.addView(imageView);

        TextView textView = new TextView(AddVoterActivity.this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(params2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);

        textView.setTextColor(ContextCompat.getColor(AddVoterActivity.this, R.color.icons));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackground(ContextCompat.getDrawable(AddVoterActivity.this, R.drawable.material_shadow1));
        } else {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(AddVoterActivity.this, R.drawable.material_shadow1));
        }
        // textView.setBackgroundColor(ContextCompat.getColor(AddVoterActivity.this, R.color.divider));
        textView.setText(title);
        textView.setTypeface(changeFont());

        layout.addView(textView);


        return layout;
        //  params.addRule(RelativeLayout.ALIGN_START, imageView.getId());
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

                Toastmsg(AddVoterActivity.this, Error);

            }
        });

        //  VolleySingleton.getInstance(this).addToRequestQueue(request);

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    @BindString(R.string.Error)
    String Error;

    //finishh the activity
    private void finish_activity() {
        AddVoterActivity.super.onBackPressed();
        AddVoterActivity.this.finish();
    }


    @BindString(R.string.noQuestion)
    String noQuestion;

    private void get_questions() {

        try {
            Cursor c = db.get_question();
            if (c.moveToFirst()) {
                do {

                    Log.w(TAG,"Question : "+ c.getString(0)+": "+ c.getString(1)+":"+ c.getString(2)+":"+ c.getString(3));
                    Questions questions = new Questions(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                    list.add(questions);
                    Log.w(TAG, "question size" + list.size());
                } while (c.moveToNext());
            }

            if (list.size() > 0) {
                Collections.sort(list, new QuestionOrderComparator());
            } else {
                Toastmsg(AddVoterActivity.this, noQuestion);
                finish_activity();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void renderDynamicLayout() {

        for (int i = 0; i <= list.size() - 1; i++) {
            Questions questions = list.get(i);
            try {
                JSONObject object = new JSONObject(questions.getData());

                String questionFlag = object.getString("questionFlag");
                if (questionFlag.equals("BOTH") || questionFlag.equals("NS")) {

                    qusCount++;

                    LinearLayout layout = get_header_layout();

                    Log.w(TAG, questions.getName() + " data " + questions.getData() + " order " + object.getString("order"));
                    String questionType = object.getString("questionType");
                    switch (questionType.toLowerCase()) {

                        case Constants.QUESTION_TYPE_SINGLECHOICE:
                            LinearLayout single_choice_layout = create_spinner_choice_layout(object,i);
                            container.addView(createCardView(single_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_PHONEINPUT:
                            LinearLayout phone_input_choice_layout = create_edittext_input_layout(object,i);
                            container.addView(createCardView(phone_input_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_MULTICHOICE:
                            LinearLayout multi_choice_layout = create_multi_choice_layout(object,i);
                            container.addView(createCardView(multi_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_MULTICHOICEWEB:
                            LinearLayout multi_choice_layoutweb = create_multi_choice_layout(object,i);
                            container.addView(createCardView(multi_choice_layoutweb));
                            break;
                        case Constants.QUESTION_TYPE_SINGLEINPUT:
                            LinearLayout single_input_layout = create_edittext_singleinput_layout(object,i);
                            container.addView(createCardView(single_input_layout));
                            break;
                        default:
                            break;
                    }

                    container.addView(layout);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    /*create cardview*/
    private CardView createCardView(View view) {
        CardView cardView = new CardView(this);
        cardView.setUseCompatPadding(true);
        //   cardView.setContentPadding(5, 5, 5, 5);
        cardView.setRadius(5.0F);
        cardView.addView(view);
        return cardView;
    }

    private LinearLayout create_multi_choice_layout(final JSONObject object, final int pos) {
        LinearLayout layout = get_header_layout();
        try {


            String title, questionText;
            JSONArray array;

            if (getLanguage().equals("en")) {

                title = object.getString("attributeName");
                questionText = object.getString("questionText");
                array = new JSONArray(object.getString("answerChoices"));
            } else {
                title = object.getString("attributeNameRegional");
                questionText = object.getString("questionTextRegional");
                array = new JSONArray(object.getString("answerChoicesRegional"));
            }


            final JSONArray arrayEnglish = new JSONArray(object.getString("answerChoices"));

            final String txtTitle = object.getString("attributeName");
            //  final String title1 = object.getString("attributeName");
            //  TextView textView1 = create_header_textview(title);
            //   final String title = object.getString("attributeName");
            //  TextView textView = create_header_textview(title);

            LinearLayout headerlayout = createHeaderLayout(title, pos);
            final ImageView imageView = (ImageView) headerlayout.findViewById(pos);
            layout.addView(headerlayout);
            TextView question_text = create_question_textview(questionText);
            layout.addView(question_text);


            for (int i = 0; i <= array.length() - 1; i++) {
                final CheckBox checkBox = new CheckBox(AddVoterActivity.this);
                checkBox.setText(array.getString(i));
                checkBox.setId(i);
                layout.addView(checkBox);
                final String value = (String) arrayEnglish.get(i);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((CheckBox) v).isChecked()) {

                            Log.w(TAG, "Check box is checked");
                            // String value = ((CheckBox) v).getText().toString();
                            Log.w(TAG, "checkbox value " + value);

                            imageView.setVisibility(View.VISIBLE);

                            db.insert_checkbox_value(txtTitle, value);

                        } else {
                            // String value = ((CheckBox) v).getText().toString();
                            db.delet_un_checkbox(txtTitle, value);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layout;

    }

    private LinearLayout create_edittext_input_layout(final JSONObject object, final int pos) {
        LinearLayout layout = get_header_layout();

        try {

            String title, questionText;


            if (getLanguage().equals("en")) {

                title = object.getString("attributeName");
                questionText = object.getString("questionText");
            } else {
                title = object.getString("attributeNameRegional");
                questionText = object.getString("questionTextRegional");
            }

            // final String title1 = object.getString("attributeName");
            // TextView textView1 = create_header_textview(title);

            LinearLayout headerlayout = createHeaderLayout(title, pos);
            layout.addView(headerlayout);
            ImageView imageView = (ImageView) headerlayout.findViewById(pos);


            TextView question_text = create_question_textview(questionText);
            layout.addView(question_text);

            Log.w(TAG, "order " + object.getString("order"));
            EditText editTextsingle = new EditText(AddVoterActivity.this);
            editTextsingle.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextsingle.setId(Integer.parseInt(object.getString("order")));
            edit_text_list.put(object.getString("attributeName"), editTextsingle);
            Log.w(TAG, "Edit text list size " + edit_text_list.size());
            layout.addView(editTextsingle);


            editTextsingle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if (editable.toString().length() > 0) {
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layout;
    }

    private LinearLayout create_edittext_singleinput_layout(final JSONObject object, final int pos) {
        LinearLayout layout = get_header_layout();

        try {

            String title, questionText;


            if (getLanguage().equals("en")) {

                title = object.getString("attributeName");
                questionText = object.getString("questionText");
            } else {
                title = object.getString("attributeNameRegional");
                questionText = object.getString("questionTextRegional");
            }
            //  final String title1 = object.getString("attributeName");
            // final String title = object.getString("attributeName");
            LinearLayout headerLayout = createHeaderLayout(title, pos);
            layout.addView(headerLayout);

            final ImageView imageView = (ImageView) headerLayout.findViewById(pos);

            TextView question_text = create_question_textview(questionText);
            layout.addView(question_text);

            Log.w(TAG, "order " + object.getString("order"));
            EditText editText = new EditText(AddVoterActivity.this);
            // editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setId(Integer.parseInt(object.getString("order")));
            edit_text_list.put(object.getString("attributeName"), editText);
            Log.w(TAG, "Edit text list size " + edit_text_list.size());
            layout.addView(editText);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if (editable.toString().length() > 0){
                        imageView.setVisibility(View.VISIBLE);
                    }else{
                        imageView.setVisibility(View.GONE);
                    }
                }
            });

            //  survey_result.put(title, editText.getText().toString().trim());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layout;
    }

    private String getLanguage() {

        return sharedPreferenceManager.getLanguage();
    }


    private LinearLayout create_spinner_choice_layout(final JSONObject object, int pos) {

        LinearLayout layout = get_header_layout();


        final Spinner spinner = new Spinner(AddVoterActivity.this);
        final EditText editText = new EditText(AddVoterActivity.this);

        String title, questionText;
        ArrayList<String> options;

        try {

            if (getLanguage().equals("en")) {

                title = object.getString("attributeName");
                questionText = object.getString("questionText");
                options = getChoices(new JSONArray(object.getString("answerChoices")));
            } else {
                title = object.getString("attributeNameRegional");
                questionText = object.getString("questionTextRegional");
                options = getChoices(new JSONArray(object.getString("answerChoicesRegional")));
            }


            final ArrayList<String> englishOptions = getChoices(new JSONArray(object.getString("answerChoices")));
            englishOptions.add(0, "Select one...");
            englishOptions.add("User Input");


            //  TextView textView = create_header_textview(title);
            //  layout.addView(textView);

            LinearLayout headerlayout = createHeaderLayout(title, pos);
            layout.addView(headerlayout);

            final ImageView tickImage = (ImageView) layout.findViewById(pos);


            TextView question_text = create_question_textview(questionText);
            layout.addView(question_text);


            //options = getChoices(new JSONArray(object.getString("answerChoices")));
            options.add(0, "Select one...");
            options.add("User Input");
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(AddVoterActivity.this, android.R.layout.simple_spinner_dropdown_item, options);
            spinner.setAdapter(spinnerArrayAdapter);

            editText.setHint("Please enter text");
            editText.setVisibility(GONE);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        if (position != 0) {

                            try {
                                String res = englishOptions.get(position);
                                editText.setVisibility(GONE);
                                survey_result.put(object.getString("attributeName"), res);
                                tickImage.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            tickImage.setVisibility(View.GONE);
                        }

                        if (spinner.getSelectedItem().toString().equals("User Input")) {
                            editText.setVisibility(View.VISIBLE);
                            tickImage.setVisibility(View.GONE);
                            single_choice_edittext.put(object.getString("attributeName"), editText);


                            editText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                    if (editable.toString().length() > 0) {
                                        tickImage.setVisibility(View.VISIBLE);
                                    } else {
                                        tickImage.setVisibility(View.GONE);
                                    }
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        layout.addView(spinner);
        layout.addView(editText);

        return layout;

    }

    private String check_contains_comma(String val) {
        String comma = "";
        comma = val.substring(val.length() - 1, val.length());
        Log.w(TAG, "comma " + comma);
        if (comma.contains(",")) {
            return val.substring(0, val.length() - 1);
        } else {
            return val;
        }
    }


    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    @OnClick(R.id.btn_add)
    public void add() {
       /* if (is_form_6_required) {
            if (!(edt_state.getText().toString().trim().isEmpty() || edt_assemble_constituency.getText().toString().trim().isEmpty() || booth_name.isEmpty() && edt_name.getText().toString().trim().isEmpty() || edt_surname.getText().toString().trim().isEmpty() ||
                    gender.isEmpty() || edt_dob.getText().toString().trim().isEmpty() || edt_village_town.getText().toString().trim().isEmpty() ||
                    edt_pob_state.getText().toString().trim().isEmpty() || edt_pob.getText().toString().trim().isEmpty() ||
                    edt_relation_name.getText().toString().trim().isEmpty() || edt_houseno.getText().toString().trim().isEmpty() ||
                    edt_street.getText().toString().trim().isEmpty() || edt_fa_village_town.getText().toString().trim().isEmpty() ||
                    edt_post_office.getText().toString().trim().isEmpty() || edt_pincode.getText().toString().trim().isEmpty() ||
                    edt_taluk.getText().toString().trim().isEmpty() || edt_mobile.getText().toString().trim().isEmpty() ||
                    edt_email.getText().toString().trim().isEmpty())) {


                Log.w(TAG, "Form 6 values : " + get_form6_values().toString());

                survey_result.put("form6Values", get_form6_values().toString());
                String mobile_number = getMobileNumber();
                String name = getName();
                Log.w(TAG, "Mobile number : " + mobile_number);
                String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
                Log.w(TAG, "survey id " + surveyId);

                if (!(name.equals("empty") || mobile_number.equals("empty"))) {

                    insert_data(mobile_number, getSurveyData(surveyId), surveyId, name);

                } else {
                    Toastmsg(AddVoterActivity.this, "Name and mobile number required");
                    return;
                }
            } else {

                Toastmsg(AddVoterActivity.this, allFieldsRequired);

            }
        } else {
*/
        String voterCardNumber = "newVoter";

        if (isHaveVoterCard) {
            if (edtVoterCardNumber.getText().toString().trim().isEmpty()) {
                Toastmsg(AddVoterActivity.this, enterVoterCard);
                return;
            } else {
                voterCardNumber = edtVoterCardNumber.getText().toString().trim();
            }
        }
        String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
        Log.w(TAG, "survey id " + surveyId);
        String mobile_number = getMobileNumber();
        String name = getName();
        Log.w(TAG, "Mobile number : " + mobile_number);


        if (!(name.isEmpty() || mobile_number.isEmpty())) {

            String latitude = gpsTracker.getLatitude()+"";
            String longitude = gpsTracker.getLongitude()+"";

            insert_data(mobile_number, getSurveyData(surveyId, latitude, longitude), surveyId, name, voterCardNumber, latitude,longitude);

        } else {
            Toastmsg(AddVoterActivity.this, nameMobileRequired);
            return;
        }

    }


    @BindString(R.string.nameMobileRequired)
    String nameMobileRequired;

    /*get json object for inserting data*/
    private JSONObject getSurveyData(String surveyId, String latitude, String longitude) {
  /*single choice edittext list*/
        if (single_choice_edittext.size() > 0) {
            for (HashMap.Entry<String, EditText> entry : single_choice_edittext.entrySet()) {
                EditText editText = (EditText) entry.getValue();
                String edit_value = editText.getText().toString().trim();
                Log.w(TAG, "edittext value " + edit_value);
                String edit_key = entry.getKey();
                if (!edit_value.isEmpty()) {
                    survey_result.put(edit_key, edit_value);
                }
            }
        }


        String result = "", title = "";

        try {
            Cursor cursor = db.get_title();
            if (cursor.moveToFirst()) {
                do {
                    title = cursor.getString(0);

                    Log.w(TAG, "title " + title);
                    Cursor c = db.get_check_box_value(title);
                    if (c.moveToFirst()) {
                        do {

                            if (c.moveToNext()) {
                                result += c.getString(1) + ",";
                            } else {
                                result += c.getString(1);
                            }

                        } while (c.moveToNext());
                    }

                    c.close();


                    survey_result.put(title, result);
                    result = "";

                } while (cursor.moveToNext());

                cursor.close();
            }


        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        survey_result.put("photo", path);
        survey_result.put("id_photo", id_path);
        survey_result.put("address_photo", address_path);

        survey_result.put("surveyType", "Field Survey");

        survey_result.put("userType", userType);
        //added for new 4 attribute
        survey_result.put("surveyDate", new Date().toString());
        survey_result.put("projectId", sharedPreferenceManager.get_project_id());
        survey_result.put("partyWorker", sharedPreferenceManager.get_user_id());
        //  survey_result.put("booth", sharedPreferenceManager.get_survey_booth());
        survey_result.put("audioFileName", mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));
        survey_result.put("pwname", sharedPreferenceManager.get_username());
        Log.w(TAG, "Location latest " + "latitude : " + location.getLatitude() + " longitude " + location.getLongitude());

        survey_result.put("latitude", latitude );
        survey_result.put("longitude", longitude);
        survey_result.put("booth_name", booth_name);

        survey_result.put("surveyid", surveyId);

        JSONObject object = new JSONObject();
        for (HashMap.Entry<String, String> entry : survey_result.entrySet()) {
            String key1 = entry.getKey();
            String value = entry.getValue();
            try {
                object.put(key1, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return object;
    }

    private String getName() {
        String name = "";
        for (HashMap.Entry<String, EditText> entry : edit_text_list.entrySet()) {
            EditText editText = (EditText) entry.getValue();
            String edit_value = editText.getText().toString().trim();
            String edit_key = entry.getKey();

            if (edit_key.equalsIgnoreCase("Name")) {
                name = edit_value;
            }


        }

        return name;
    }


    /*get mobile number from edittext*/
    private String getMobileNumber() {
        String mobile = "empty";
        for (HashMap.Entry<String, EditText> entry : edit_text_list.entrySet()) {
            EditText editText = (EditText) entry.getValue();
            String edit_value = editText.getText().toString().trim();
            String edit_key = entry.getKey();

            if (edit_key.equalsIgnoreCase("Mobile Number")) {
                mobile = edit_value;
            }

            if (!edit_value.isEmpty()) {
                survey_result.put(edit_key, edit_value);
            }
        }
        return mobile;
    }

    /*delete audio file*/
    private void deleteAudioFile() {
        try {
            if (mFileName != null) {
                File file = new File(newDir, mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));

                Log.w(TAG, "File path : " + file.getAbsolutePath());

                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {

            deleteAudioFile();
            if (path != null) {
                File photo = new File(newDir, path.substring(path.lastIndexOf("/") + 1, path.length()));
                if (photo.exists()) {
                    photo.delete();
                }
            }
            if (address_path != null) {
                File address = new File(newDir, address_path.substring(address_path.lastIndexOf("/") + 1, address_path.length()));
                if (address.exists()) {
                    address.delete();
                }
            }
            if (id_path != null) {
                File id = new File(newDir, id_path.substring(id_path.lastIndexOf("/") + 1, id_path.length()));
                if (id.exists()) {
                    id.delete();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @BindString(R.string.mobileNumberExist)
    String mobilNumberExist;
    @BindString(R.string.successfullyAdded)
    String successfullyAdded;

    @BindString(R.string.validMobile)
    String validMobile;

    //insert data into database
    private void insert_data(String mobile_number, JSONObject object, String surveyId, String name, String voterCardNumber, String latitude, String longitude) {

        if (validMobile.length() >= 10) {
            if (!db.check_mobile_number_exist(mobile_number)) {

                if (!db.check_location(latitude, longitude)) {
                try {
                    object.put("duplicate_survey", "no");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                }else{
                    try {
                        object.put("duplicate_survey", "yes");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                    onRecord(false);

                    try {
                        object.put("mobile", mobile_number);
                        object.put("respondantname", name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    VoterAttribute attribute = new VoterAttribute(voterCardNumber, "Survey", object.toString(), get_current_date(), false, surveyId);


                    BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                            booth_name, "Survey", userType);

                    db.insertBoothStats(stats);
                    //VotersDatabase votersDatabase = new VotersDatabase(AddVoterActivity.this);
                    db.insert_voter_attribute(attribute);
                    db.insert_mobile_number(mobile_number, latitude, longitude);
                    //  votersDatabase.update_survey(attribute.getVoterCardNumber());

                    Toastmsg(AddVoterActivity.this, successfullyAdded);
                    AddVoterActivity.super.onBackPressed();
                    AddVoterActivity.this.finish();


            } else {

                Toast.makeText(AddVoterActivity.this, mobilNumberExist, Toast.LENGTH_LONG).show();

            }

        } else {
            Toastmsg(AddVoterActivity.this, validMobile);
        }

    }

    private JSONObject get_form6_values() {


        JSONObject newVoter = new JSONObject();

        try {
            newVoter.put("state", edt_state.getText().toString().trim());
            newVoter.put("assembly_constituency", edt_assemble_constituency.getText().toString().trim());
            newVoter.put("state", edt_pob_state.getText().toString().trim());
            newVoter.put("name", edt_name.getText().toString().trim());
            newVoter.put("surname", edt_surname.getText().toString().trim());
            newVoter.put("gender", gender);
            newVoter.put("date_of_birth", edt_dob.getText().toString().trim());
            newVoter.put("place_of_birth_state", edt_pob_state.getText().toString().trim());
            newVoter.put("place_of_birth_district", pob_district);
            newVoter.put("place_of_birth_town", edt_village_town.getText().toString().trim());
            newVoter.put("place_of_birth", edt_pob.getText().toString().trim());
            newVoter.put("relationship_type", relationship_type);
            newVoter.put("relationship_name", edt_relation_name.getText().toString().trim());
            newVoter.put("relationship_surname", edt_relationsurname.getText().toString().trim());
            newVoter.put("house", edt_houseno.getText().toString().trim());
            newVoter.put("street", edt_street.getText().toString().trim());
            newVoter.put("town", edt_village_town.getText().toString().trim());
            newVoter.put("post_office", edt_post_office.getText().toString().trim());
            newVoter.put("pin_code", edt_pincode.getText().toString().trim());
            newVoter.put("taluka", edt_taluk.getText().toString().trim());
            newVoter.put("address_district", fa_district);
            newVoter.put("mobile_number", edt_mobile.getText().toString().trim());
            newVoter.put("email_id", edt_email.getText().toString().trim());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newVoter;

    }


    private void onclick_for_district() {
        spinner_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pob_district = spinner_district.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onclick_fa_district() {
        fa_district_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fa_district = fa_district_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void init_spinner_Values() {
        //For now hardcoded
        String[] options = {"Select one..", "B.B.M.P(Central)", "B.B.M.P(North)", "B.B.M.P(South)", "Bagalkot", "Bangalore Rural", "Bangalore Urban", "Belgaum", "Bellary", "Bidar", "Bijapur", "Chamarajnagar", "Chikkaballapur", "Chikmagalur", "Chitradurga", "Dakshina Kannada", "Davangere", "Dharwad", "Gadag", "Gulbarga", "Hassan", "Haveri", "Kodagu", "Kolar", "Koppal", "Mandya", "Mysore", "Raichur", "Ramanagaram", "Shimoga", "Tumkur", "Udupi", "Uttara Kannada"};
        set_adapter_for_spinner(options, spinner_district);

        set_adapter_for_spinner(options, fa_district_spinner);


        onclick_for_district();
        onclick_fa_district();


    }

    private void set_adapter_for_spinner(String[] options, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @OnClick(R.id.image_button_photograph)
    public void photographCapture() {

        if (checkPermissionGranted(Constants.CAMERA, this)) {
            photograph = true;
            id_proof = false;
            address_proof = false;
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            currentImageCapture = "photograph";
            file = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_" + edt_name.getText().toString() + "_" + currentImageCapture + ".jpg";
            path = file;
            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Log.w(TAG, "inside version 7");
                outputFileUri = FileProvider.getUriForFile(AddVoterActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            } else {
                Log.w(TAG, "inside version 6");
                outputFileUri = Uri.fromFile(newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            // outputFileUri = Uri.fromFile(newfile);
            // outputFileUri = FileProvider.getUriForFile(AddVoterActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            askPermission(new String[]{Constants.CAMERA});
        }

    }


    @OnClick(R.id.image_button_address_proof)
    public void addressProofCapture(View v) {
        if (checkPermissionGranted(Constants.CAMERA, this)) {
            photograph = false;
            id_proof = false;
            address_proof = true;
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            currentImageCapture = "address_proof";
            file = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_" + edt_name.getText().toString() + "_" + currentImageCapture + ".jpg";
            address_path = file;

            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }

            //outputFileUri = Uri.fromFile(newfile);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(AddVoterActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            } else {
                outputFileUri = Uri.fromFile(newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            askPermission(new String[]{Constants.CAMERA});
        }
    }

    @OnClick(R.id.image_button_id_proof)
    public void idProofCapture(View v) {

        if (checkPermissionGranted(Constants.CAMERA, this)) {
            photograph = false;
            id_proof = true;
            address_proof = false;
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            currentImageCapture = "id_proof";
            file = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_" + edt_name.getText().toString() + "_" + currentImageCapture + ".jpg";
            id_path = file;
            newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(AddVoterActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            } else {
                outputFileUri = Uri.fromFile(newfile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            //

            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            askPermission(new String[]{Constants.CAMERA});
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

                Log.w("Voter addition", " path " + path);
                type = "";
                type = "jpeg";


                if (photograph) {
                    File imgFile = new File(path);

                    if (imgFile.exists()) {
                        Log.w(TAG, "file exist");
                        new CompressImage().execute(imgFile.getAbsolutePath());
                    }


                    Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    selectedBitmap = getResizedBitmap(selectedBitmap, 400, 400);
                    image_photograph.setImageBitmap(selectedBitmap);
                    // Uri uri = Uri.parse(path);


                    //`  performCrop();
                    // startActivity(new Intent(VoterAddition.this,CropImage.class).putExtra("image", path));
                    // image_photograph.setImageBitmap(selectedBitmap);
                } else if (id_proof) {
                    File imgFile = new File(id_path);

                    if (imgFile.exists()) {
                        Log.w(TAG, "file exist");
                        new CompressImage().execute(imgFile.getAbsolutePath());
                    }


                    Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    image_id_proof.setImageBitmap(getResizedBitmap(selectedBitmap, 400, 400));
                } else if (address_proof) {
                    File imgFile = new File(address_path);

                    if (imgFile.exists()) {
                        Log.w(TAG, "file exist");
                        new CompressImage().execute(imgFile.getAbsolutePath());
                    }

                    Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    image_address_proof.setImageBitmap(getResizedBitmap(selectedBitmap, 400, 400));
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {


        getLocationLayout.setVisibility(GONE);

        if (!isMapClicked && !booth_name.isEmpty()) {
            setLocationLayout.setVisibility(View.VISIBLE);
        }
        txtLatitude.setText(location.getLatitude() + "");
        txtLongitude.setText(location.getLongitude() + "");


        //   dest = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                myLocation).zoom(18).build();
        map.clear();

        map.addMarker(new MarkerOptions().position(myLocation)
                .title("My Location"));
        //   map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        //   map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    //compress image
    private class CompressImage extends AsyncTask<String, Void, Void> {

        File file;

        @Override
        protected Void doInBackground(String... params) {
            file = new File(params[0]);
            try {
                File compressedImage = new Compressor(AddVoterActivity.this)
                        .setMaxWidth(500)
                        .setMaxHeight(500)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES) + "/compressimage")
                        .compressToFile(file);

                if (photograph) {
                    path = compressedImage.getAbsolutePath();
                    Log.w(TAG, "Image path :" + path);
                } else if (address_proof) {
                    address_path = compressedImage.getAbsolutePath();
                    Log.w(TAG, "Address path :" + address_path);
                } else {
                    address_path = compressedImage.getAbsolutePath();
                    Log.w(TAG, "Id path :" + address_path);
                }
                Log.w(TAG, "Compressed image size : " + compressedImage.length() / 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    private LinearLayout get_header_layout() {

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        //  layout.setPadding(5, 5, 5, 5);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.icons));

        return layout;
    }

    private TextView create_header_textview(String title) {
        TextView textView = new TextView(this);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(this, R.color.icons));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.material_shadow1));
        } else {
            textView.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.material_shadow1));
        }
        textView.setText(title);
        textView.setTypeface(changeFont());
        return textView;
    }

    private ArrayList<String> getChoices(JSONArray jsonArray) throws JSONException {

        ArrayList<String> choices = new ArrayList<String>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++)
            choices.add(jsonArray.getString(i));
        return choices;
    }

    private Typeface changeFont() {
        Typeface font = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            font = getResources().getFont(R.font.optima_bold);
        }else{
            font = ResourcesCompat.getFont(AddVoterActivity.this, R.font.optima_bold);
        }
        return font;
    }

    private TextView create_question_textview(String question) {
        TextView textView = new TextView(this);
        textView.setText(question);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextColor(ContextCompat.getColor(this, R.color.primary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTypeface(changeFont());
        return textView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Until you grant the permission, you cannot get a location", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
       /* if (checkInternet.isConnected()) {
            getLocation();
        }*/
    }

    private class QuestionOrderComparator implements Comparator<Questions> {

        @Override
        public int compare(Questions lhs, Questions rhs) {
            if (lhs == null || rhs == null)
                return 0;
            if (lhs.getData() == null || rhs.getData() == null)
                return 0;
            JSONObject lhsJSON = lhs.getJson();
            JSONObject rhsJSON = rhs.getJson();

            try {
                int lhsOrder = lhsJSON.getInt("order");
                int rhsOrder = rhsJSON.getInt("order");
                return (lhsOrder > rhsOrder) ? 1 : -1;
            } catch (JSONException e) {
                return 0;
            }
        }
    }

    private TextView create_textview(String question) {
        TextView textView = new TextView(AddVoterActivity.this);
        textView.setText(question);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return textView;
    }

    private EditText create_edittext(String value) {
        EditText editText = new EditText(AddVoterActivity.this);
        editText.setText(value);
        return editText;
    }

}
