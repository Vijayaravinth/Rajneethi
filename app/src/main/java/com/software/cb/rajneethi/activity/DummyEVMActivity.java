package com.software.cb.rajneethi.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.DummyEvmAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.DummyEvmDetails;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class DummyEVMActivity extends UtilActivity {


    @BindView(R.id.evmRecyclerview)
    RecyclerView recyclerView;


    @BindView(R.id.datalayout)
    LinearLayout dataLayout;

    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtNumber)
    EditText edtMobile;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.casteSpinner)
    Spinner spinner;

    @BindView(R.id.ageSpinner)
    Spinner ageSpinner;
    @BindView(R.id.genderSpinner)
    Spinner genderSpinner;

    @BindView(R.id.edtcaste)
    EditText edtcaste;

    @BindView(R.id.booth_spinner)
    Spinner boothSinner;

    @BindView(R.id.edtAge)
    EditText edtAge;
    @BindView(R.id.edtGender)
    EditText edtGender;

    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;
    @BindString(R.string.successfullyAdded)
    String successfullyAdded;

    private IOSDialog dialog;

    String[] casteArray = new String[]{"Select one", "VOKKALIGA", "LINGAYATH", "MUSLIM", "CHRISTIAN", "GOLLA", "KURUBA", "SC (MALA,MADIGA,BOVI,CHALAVADI, ADI KARNATAKA, ODDA)", "BALIJA", "ST (NAYAKA, SOLIGARU, NAIKA)", "DEVANGA/NEKARA", "IDIGA", "BRAHMANA", "BALAJIGA", "BHANDARI", "VISHWAKARMA/BADIGA", "UPPARA", "ARYA VYSYA", "MARATA", "User Input"};


    String[] casteKannada = new String[]{"Select one", "ಒಕ್ಕಲಿಗ", "ಲಿಂಗಾಯತ", "ಮುಸ್ಲಿಂ", "ಕ್ರಿಸ್ಚಿಯನ್", "ಗೊಲ್ಲ", "ಕುರುಬ", "ಎಸ್ ಸೀ (ಮಾಲ, ಮಾದಿಗ, ಬೋವಿ, ಚಲವಾದಿ, ಆದಿ ಕರ್ನಾಟಕ, ಒಡ್ಡ)", "ಬಲಿಜ", "ಎಸ್ ಟೀ (ನಾಯಕ, ಸೋಲಿಗರು, ನಾಯ್ಕ)", "ದೇವಾಂಗ", "ಈಡಿಗ", "ಬ್ರಾಹ್ಮಣ", "ಬಲಜಿಗ", "ಭಂಡಾರಿ", "ವಿಶ್ವಕರ್ಮ/ ಬಡಿಗ", "ಉಪ್ಪಾರ", "ಆರ್ಯ ವೈಶ್ಯ", "ಮರಾಠ", "User Input"};


    String[] ageArray = new String[]{"Select one", "18-23-NEW VOTER", "24-35-YOUNG VOTER", "36-55-MATURED VOTER", "56 Above-EXPERIENCED VOTER", "User Input"};
    String[] ageKannada = new String[]{"Select one", "18-23-ನವ ಮತದಾರ", "24-35-ಯುವ ಮತದಾರ", "36-55-ಪ್ರೌಢ ಮತದಾರ", "56 ಅನುಭವಿ ಮತದಾರ", "User Input"};

    String[] genderArray = new String[]{"Select one", "Male", "Female", "User Input"};
    String[] genderKanada = new String[]{"Select one", "ಪುರುಷ", "ಸ್ತ್ರೀ", "User Input"};

    String add = "NA";


    ArrayList<DummyEvmDetails> list = new ArrayList<>();

    double latitude; // latitude
    double longitude; // longitude
    private String location_string;
    private static LocationManager locationManager;
    android.location.Location location; // location
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 0 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 60000;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;

    public static final int LOCATION_PERMISSION_CODE = 23;

    LatLng dest;

    private String mFileName = null;
    private MediaRecorder mRecorder = null;

    private MyDatabase db;
    private GPSTracker gpsTracker;

    Date startTime;
    Date endTime;

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 101;
    private SharedPreferenceManager sharedPreferenceManager;

    String file;
    String userType = "";

    String booth_name;
    Date startedTime, endedTime;

    JSONObject dataObj = new JSONObject();

    ArrayList<String> booth_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_evm);
        ButterKnife.bind(this);


        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());

        File newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        setup_toolbar_with_back(toolbar, "EVM");

        dialog = show_dialog(DummyEVMActivity.this, false);


        //  if (getIntent().getExtras() != null) {
        userType = sharedPreferenceManager.getUserType();

        //  booth_name = getIntent().getExtras().getString("boothName");
        //  }

        gpsTracker = new GPSTracker(DummyEVMActivity.this);

        db = new MyDatabase(this);
        db.delete_checkbox_values();
        loadBoothDetails();
        if (sharedPreferenceManager.getLanguage().equalsIgnoreCase("en")) {
            setAdapterForSpinner(casteArray);
            setAdapterForAge(ageArray);
            setAdapterForGender(genderArray);
        } else {
            setAdapterForSpinner(casteKannada);
            setAdapterForAge(ageKannada);
            setAdapterForGender(genderKanada);
        }


        getLocation();


        set_Linearlayout_manager(recyclerView, this);

    /*    list.add(new DummyEvmDetails("1", "ನಿಖಿಲ್ ಕುಮಾರಸ್ವಾಮಿ", R.drawable.i1, "ಜೆ ಡಿ ಎಸ್", R.drawable.s1));
        list.add(new DummyEvmDetails("2", "ನಂಜುಂಡ ಸ್ವಾಮಿ", R.drawable.i2, "ಬಿ ಎಸ್ ಪಿ", R.drawable.s2));
        list.add(new DummyEvmDetails("3", "ಗುರುಲಿಂಗಯ್ಯ", R.drawable.i3, "ಇಂಡಿಯನ್ ನಿವ್ ಕಾಂಗ್ರೆಸ್ ಪಾರ್ಟಿ", R.drawable.s3));
        list.add(new DummyEvmDetails("4", "ಡಿ. ಸಿ. ಜಯಶಂಕರ", R.drawable.i4, "ಎ ಎನ್ ಪಿ", R.drawable.s4));
        list.add(new DummyEvmDetails("5", "ದಿವಾಕರ್. ಸಿ. ಪಿ. ಗೌಡ", R.drawable.i5, "ಉತ್ತಮ ಪ್ರಜಾಕೀಯ ಪಕ್ಷ", R.drawable.s5));
        list.add(new DummyEvmDetails("6", "ಸಂತೋಷ್ ಮಂಡ್ಯ ಗೌಡ", R.drawable.i6, "ಇಂಜಿನಿಯರ್ಸ್ ಪಾರ್ಟಿ", R.drawable.s6));
        list.add(new DummyEvmDetails("7", "ಅರವಿಂದ್ ಪ್ರೇಮಾನಂದ್", R.drawable.i7, "ಪಕ್ಷೇತರ", R.drawable.s7));
        list.add(new DummyEvmDetails("8", "ಕೌಡ್ಲೇ ಚೆನ್ನಪ್ಪ", R.drawable.i8, "ಪಕ್ಷೇತರ", R.drawable.s8));
        list.add(new DummyEvmDetails("9", "ಟಿ.ಕೆ. ದಸರ್", R.drawable.i9, "ಪಕ್ಷೇತರ", R.drawable.s9));
        list.add(new DummyEvmDetails("10", "ಹೆಚ್. ನಾರಯಣ", R.drawable.i10, "ಪಕ್ಷೇತರ", R.drawable.s10));
        list.add(new DummyEvmDetails("11", "ಪುಟ್ಟೇಗೌಡ ಎನ್. ಸಿ.", R.drawable.i11, "ಪಕ್ಷೇತರ", R.drawable.s11));
        list.add(new DummyEvmDetails("12", "ಪ್ರೇಮ್ ಕುಮಾರ್. ವಿ.ವಿ.", R.drawable.i12, "ಪಕ್ಷೇತರ", R.drawable.s12));
        list.add(new DummyEvmDetails("13", "ಮಂಜುನಾಥ್. ಬಿ.", R.drawable.i13, "ಪಕ್ಷೇತರ", R.drawable.s13));
        list.add(new DummyEvmDetails("14", "ಜಿ. ಮಂಜುನಾಥ್", R.drawable.i14, "ಪಕ್ಷೇತರ", R.drawable.s14));
        list.add(new DummyEvmDetails("15", "ಲಿಂಗೇಗೌಡ . ಎಸ್. ಹೆಚ್.", R.drawable.i15, "ಪಕ್ಷೇತರ", R.drawable.s15));
        list.add(new DummyEvmDetails("16", "ಸಿ. ಲಿಂಗೇಗೌಡ", R.drawable.i16, "ಪಕ್ಷೇತರ", R.drawable.s16));
        list.add(new DummyEvmDetails("17", "ಎಮ್. ಎಲ್. ಶಶಿಕುಮಾರ್", R.drawable.i17, "ಪಕ್ಷೇತರ", R.drawable.s17));
        list.add(new DummyEvmDetails("18", "ಸತೀಶ್ ಕುಮಾರ್. ಟಿ. ಎನ್.", R.drawable.i18, "ಪಕ್ಷೇತರ", R.drawable.s18));
        list.add(new DummyEvmDetails("19", "ಸುಮಲತಾ", R.drawable.i19, "ಪಕ್ಷೇತರ", R.drawable.s19));
        list.add(new DummyEvmDetails("20", "ಸುಮಲತಾ ಅಂಬರೀಷ್", R.drawable.i20, "ಪಕ್ಷೇತರ", R.drawable.s20));
        list.add(new DummyEvmDetails("21", "ಎಮ್. ಸುಮಲತಾ", R.drawable.i21, "ಪಕ್ಷೇತರ", R.drawable.s21));
        list.add(new DummyEvmDetails("22", "ಸುಮಲತಾ", R.drawable.i22, "ಪಕ್ಷೇತರ", R.drawable.s22));
*/

/*
        list.add(new DummyEvmDetails("BJP", "https://5.imimg.com/data5/DY/TU/MY-30984272/bjp-party-flag-500x500.jpg"));
        list.add(new DummyEvmDetails("Congress", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Flag_of_the_Indian_National_Congress.svg/1200px-Flag_of_the_Indian_National_Congress.svg.png"));
        list.add(new DummyEvmDetails("ADMK", "http://www.newspatrolling.com/wp-content/uploads/ADMK-Symbol-e1489896135128.png"));
        list.add(new DummyEvmDetails("DMK", "https://pbs.twimg.com/profile_images/1010406303011762181/KZ7zVc60_400x400.jpg"));
        list.add(new DummyEvmDetails("CPI", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/CPI-banner.svg/1200px-CPI-banner.svg.png"));
        list.add(new DummyEvmDetails("CPM", "https://img.etimg.com/thumb/msid-63120512,width-300,imgsize-26272,resizemode-4/cpim-twitter.jpg"));
        list.add(new DummyEvmDetails("AAP", "https://images.indianexpress.com/2018/08/aam-aadmi-party-aap-logo-759.jpg"));
        list.add(new DummyEvmDetails("DMDK", "https://upload.wikimedia.org/wikipedia/en/7/7b/DMDK_flag.PNG"));
        list.add(new DummyEvmDetails("PMK", "https://upload.wikimedia.org/wikipedia/commons/6/67/Pmk_flag.jpg"));
        list.add(new DummyEvmDetails("AITC", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c0/All_India_Trinamool_Congress_flag.svg/1200px-All_India_Trinamool_Congress_flag.svg.png"));
        list.add(new DummyEvmDetails("RJD", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/RJD_Flag.svg/1200px-RJD_Flag.svg.png"));*/
        setAdapter();
    }


    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    @BindString(R.string.Error)
    String Error;

    @BindString(R.string.selectBooth)
    String selectBooth;

    @BindString(R.string.noInternet)
    String noInternet;


    @BindString(R.string.mobileNumberExist)
    String mobilNumberExist;

    @BindString(R.string.validMobile)
    String validMobile;

    String age = "", gender = "";


    private void loadBoothDetails() {

        if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {

            if (checkInternet.isConnected()) {
                dialog.show();
                get_booth_details();
            } else {

                try {
                    Cursor c = db.getBooths();
                    Log.w(TAG, "count : " + c.getCount());
                    if (c.getCount() > 0) {
                        if (c.moveToFirst()) {
                            do {
                                booth_list.add(c.getString(0));
                            } while (c.moveToNext());
                        }
                        c.close();
                        loadDataToSpinner();
                    } else {
                        loadDefaultBooth();
                    }
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    loadDefaultBooth();
                }
                Toastmsg(DummyEVMActivity.this, noInternet);
                //  finish_activity();
                // loadDefaultBooth();
            }

        } else {
            try {
                Cursor c = db.getBooths();
                Log.w(TAG, "count : " + c.getCount());
                if (c.getCount() > 0) {
                    if (c.moveToFirst()) {
                        do {
                            booth_list.add(c.getString(0));
                        } while (c.moveToNext());
                    }
                    c.close();
                    loadDataToSpinner();
                } else {
                    if (checkInternet.isConnected()) {
                        dialog.show();
                        get_booth_details();
                    } else {
                        loadDefaultBooth();
                        Toastmsg(DummyEVMActivity.this, noInternet);
                        // finish_activity();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (checkInternet.isConnected()) {
                    dialog.show();
                    get_booth_details();
                } else {
                    loadDefaultBooth();
                    Toastmsg(DummyEVMActivity.this, noInternet);
                    //finish_activity();
                }
            }

        }


    }

    private void set_adapter_for_spinner(ArrayList<String> options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        boothSinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        boothSinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {


                    if (!recyclerView.isShown()) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    booth_name = boothSinner.getSelectedItem().toString();

                    dataLayout.setVisibility(GONE);
                    startSurvey();
                    //  sharedPreferenceManager.set_booth(booth_name);


                } else {
                    recyclerView.setVisibility(GONE);
                    dataLayout.setVisibility(GONE);
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
            boothSinner.setVisibility(View.VISIBLE);
            booth_list.add(0, selectBooth);
            set_adapter_for_spinner(booth_list);
        } else {
            //   hide_question_layout();
            boothSinner.setVisibility(GONE);
            Toastmsg(DummyEVMActivity.this, noBoothAllocated);
            // finish_activity();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @BindString(R.string.all_fileds_required)
    String allFields;


    @OnClick(R.id.btnSubmit)
    public void showEvm() {


        if (!(edtName.getText().toString().trim().isEmpty() || edtMobile.getText().toString().trim().isEmpty())) {


            Log.w(TAG, (isUserInput + "" + isAgeUserInput + "" + isGenderUserInput) + "");

            if (isUserInput) {

                if (edtcaste.getText().toString().trim().isEmpty()) {
                    Toastmsg(DummyEVMActivity.this, allFields);
                    edtcaste.setError("Enter caste");
                    return;
                } else {
                    caste = edtcaste.getText().toString().trim();
                }

            }

            if (isAgeUserInput) {

                if (edtAge.getText().toString().trim().isEmpty()) {
                    Toastmsg(DummyEVMActivity.this, allFields);
                    edtAge.setError("Enter age");
                    return;
                } else {
                    age = edtAge.getText().toString().trim();
                }

            }

            if (isGenderUserInput) {

                if (edtGender.getText().toString().trim().isEmpty()) {
                    Toastmsg(DummyEVMActivity.this, allFields);
                    edtGender.setError("Enter gender");
                    return;
                } else {
                    gender = edtGender.getText().toString().trim();
                }

            }


            Log.w(TAG, "caste " + caste + " age " + age + " gender " + gender);

            if (!(age.isEmpty() || gender.isEmpty() || caste.isEmpty())) {

                if (edtMobile.getText().toString().trim().length() == 10) {
                    if (!db.check_mobile_number_exist(edtMobile.getText().toString().trim())) {

                        String surveyId = DateFormat.format("yyyyMMdd_hhmmss a", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();

                        JSONObject object = getSurveyData(surveyId);

                        insert_data(edtMobile.getText().toString().trim(), object, surveyId, seraiLNo, candidateName);
                        onRecord(false);

                    } else {
                        Toastmsg(this, mobilNumberExist);
                    }
                } else {
                    Toastmsg(this, validMobile);
                }

            } else {
                Toastmsg(DummyEVMActivity.this, allFields + "2");
            }


        } else {


            Toastmsg(DummyEVMActivity.this, allFields + "1");
        }


    }


    String seraiLNo = "", candidateName = "";

    private void setAdapter() {
        DummyEvmAdapter adapter = new DummyEvmAdapter(list, this, DummyEVMActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));
    }

    boolean isUserInput = false, isAgeUserInput = false, isGenderUserInput = false;


    private void setAdapterForAge(String[] ageList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, ageList);
        ageSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_text_theatre);

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {

                    age = ageArray[position];

                    Log.w(TAG, "age " + age);
                    isAgeUserInput = false;
                }

                if (ageSpinner.getSelectedItem().toString().equalsIgnoreCase("User Input")) {

                    edtAge.setVisibility(View.VISIBLE);
                    isAgeUserInput = true;


                } else {
                    edtAge.setVisibility(View.GONE);
                    isAgeUserInput = false;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setAdapterForGender(String[] genderList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, genderList);
        genderSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_text_theatre);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    gender = genderArray[position];
                    isGenderUserInput = false;
                }

                if (genderSpinner.getSelectedItem().toString().equalsIgnoreCase("User Input")) {

                    edtGender.setVisibility(View.VISIBLE);
                    isGenderUserInput = true;


                } else {
                    edtGender.setVisibility(View.GONE);
                    isGenderUserInput = false;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setAdapterForSpinner(String[] casteArray1) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, casteArray1);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_text_theatre);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position != 0) {

                    caste = casteArray[position];
                    isUserInput = false;
                }

                if (spinner.getSelectedItem().toString().equalsIgnoreCase("User Input")) {

                    edtcaste.setVisibility(View.VISIBLE);
                    isUserInput = true;


                } else {
                    edtcaste.setVisibility(View.GONE);
                    isUserInput = false;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private HashMap<String, String> survey_result = new HashMap<>();


    public void save(String serialNumber, String name) {

        this.seraiLNo = serialNumber;
        this.candidateName = name;
        recyclerView.setVisibility(GONE);
        dataLayout.setVisibility(View.VISIBLE);

    }

    long diffInSeconds = 0;
    String caste = "";


    private void insert_data(String mobile_number, JSONObject object, String surveyId, String serialNumber, String name) {

        try {
            object.put("duplicate_survey", "no");
            object.put("serialNo", serialNumber);
            object.put("candidate_name", name);
            object.put("name", edtName.getText().toString().trim());
            //object.put("mobile", edtMobile.getText().toString().trim());
            object.put("gender", gender);
            object.put("age", age);
            object.put("caste", caste);
            object.put("address",add);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            endedTime = new Date();


            diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(endedTime.getTime() - startedTime.getTime());
            long hour = ((diffInSeconds / 60) / 60);
            long min = (diffInSeconds / 60);
            long sec = (diffInSeconds % 60);
            String timeTaken = hour + ":" + min + ":" + sec;
            object.put("time_taken", timeTaken);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.w(TAG, "values :" + object.toString());

        VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", object.toString(), get_current_date(), false, surveyId);


        BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                booth_name, "Questionnaire", userType);

        db.insertBoothStats(stats);
        //VotersDatabase votersDatabase = new VotersDatabase(AddVoterActivity.this);
        db.insert_voter_attribute(attribute);
        //  votersDatabase.update_survey(attribute.getVoterCardNumber());
        if (mobile_number.length() == 10) {

            db.insert_mobile_number(mobile_number, latitude + "", longitude + "");
        }

        // latitude : 13.0085614 longitude :77.583277

        //  db.insert_mobile_number(mobile_number, txtLatitude.getText().toString().trim(), txtLongitude.getText().toString().trim());
        Toastmsg(DummyEVMActivity.this, successfullyAdded);
        DummyEVMActivity.super.onBackPressed();
        DummyEVMActivity.this.finish();


    }


    /*get json object for inserting data*/
    private JSONObject getSurveyData(String surveyId) {

        survey_result.put("surveyType", "Questionaire");

        survey_result.put("userType", userType);
        //added for new 4 attribute
        survey_result.put("surveyDate", new Date().toString());
        survey_result.put("projectId", sharedPreferenceManager.get_project_id());
        survey_result.put("partyWorker", sharedPreferenceManager.get_user_id());
        survey_result.put("pwname", sharedPreferenceManager.get_username());

        // survey_result.put("booth", sharedPreferenceManager.get_survey_booth());
        survey_result.put("audioFileName", mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));

        // Log.w(TAG, "Location latest " + "latitude : " + location.getLatitude() + " longitude " + location.getLongitude());

        try {
            survey_result.put("latitude", latitude + "");
            survey_result.put("longitude", longitude + "");
        } catch (Exception e) {
            survey_result.put("latitude", "Not Available");
            survey_result.put("longitude", "Not Available");
            e.printStackTrace();
        }
        survey_result.put("booth_name", booth_name);
        survey_result.put("surveyid", surveyId);

        survey_result.put("respondantname", edtName.getText().toString().trim());
        survey_result.put("mobile", edtMobile.getText().toString().trim());


        for (HashMap.Entry<String, String> entry : survey_result.entrySet()) {
            String key1 = entry.getKey();
            String value = entry.getValue();
            try {
                dataObj.put(key1, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return dataObj;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void startSurvey() {


        deleteAudioFile();

        try {
            startedTime = new Date();
        } catch (Exception e) {
            e.printStackTrace();
        }

        onRecord(false);
        onRecord(true);


    }

    private String TAG = "EVM Activity";

    @Override
    public void onBackPressed() {

        goBack();
    }

    public void goBack() {
        super.onBackPressed();
        try {

            deleteAudioFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

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
        Log.w(TAG, "Ondestroy working");
        if (mRecorder != null) {
            onRecord(false);
        }
    }


    /*delete audio file*/
    private void deleteAudioFile() {
        try {

            File newDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + "/compressimage");
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


    public void getLocation() {
        try {

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

                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        getAddress(DummyEVMActivity.this, location.getLatitude(), location.getLongitude());
                        //if (latitude == 0L && longitude != 0L) {

                    }
                }

            } else {
                Toastmsg(DummyEVMActivity.this, "please Enable gps");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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

                    } else {
                        loadDefaultBooth();
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
                loadDefaultBooth();

                Toastmsg(DummyEVMActivity.this, Error);

            }
        });

        //  VolleySingleton.getInstance(this).addToRequestQueue(request);

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void loadDefaultBooth() {
        //  booth_list.add(selectBooth);
        booth_list.add("Default");
        loadDataToSpinner();
    }

    public void getAddress(Context context, double LATITUDE, double LONGITUDE) {

        //Set Address

        if (add.equalsIgnoreCase("NA")) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null && addresses.size() > 0) {


                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
             /*   String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
*/

                    add = address;
                    Log.w("question", "getAddress:  address" + address);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
