package com.software.cb.rajneethi.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.LocationCalculator;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.checkInternet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;


/**
 * Created by w7 on 11/22/2017.
 */

public class QuestionareActivity extends Util implements checkInternet.ConnectivityReceiverListener, OnMapReadyCallback, LocationListener {


    private GoogleMap map;


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
    private ArrayList<Questions> list = new ArrayList<>();
    private HashMap<String, EditText> single_choice_edittext = new HashMap<>();
    private HashMap<String, String> survey_result = new HashMap<>();
    private ArrayList<HashMap<String, String>> result_map = new ArrayList<>();
    private HashMap<String, EditText> edit_text_list = new HashMap<>();

    int qusCount = 0;
    String dir;
    String file;
    String userType = "";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.txtLatitude)
    TextView txtLatitude;
    @BindView(R.id.txtLongitude)
    TextView txtLongitude;
    @BindView(R.id.setLocationLayout)
    View setLocationLayout;
    @BindView(R.id.getLocationLayout)
    View getLocationLayout;
    @BindView(R.id.btn_add)
    Button btn_add;


    SupportMapFragment mapFragment;
    private boolean isMapClicked = false;


    @BindString(R.string.questionare)
    String toolbarTitle;

    String booth_name;
    private String TAG = "Questionare";

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    Date startedTime, endedTime;

    JSONObject dataObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionare);
        ButterKnife.bind(this);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());

        File newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        setup_toolbar_with_back(toolbar, toolbarTitle);

        if (getIntent().getExtras() != null) {
            userType = getIntent().getExtras().getString("userType");
            booth_name = getIntent().getExtras().getString("boothName");
        }

        gpsTracker = new GPSTracker(QuestionareActivity.this);

        Log.w(TAG, "Location " + " latitude :" + gpsTracker.getLatitude() + " longitude " + gpsTracker.getLongitude());
        db = new MyDatabase(this);
        db.delete_checkbox_values();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        getLocationLayout.setVisibility(View.VISIBLE);
        setLocationLayout.setVisibility(GONE);


        Log.w(TAG, "locatio " + sharedPreferenceManager.getLastLat() + " " + sharedPreferenceManager.getLastLng());


        if (sharedPreferenceManager.get_project_id().equalsIgnoreCase("14")) {
            try {

                Cursor c = db.get_particular_question("Candidate Support_" + booth_name);
                if (c.moveToFirst()) {
                    Questions questions = new Questions(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                    list.add(questions);
                }

            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        get_questions();
        renderDynamicLayout();
        // mapFragment.getView().setVisibility(GONE);

    }

    @BindString(R.string.noQuestion)
    String noQuestion;

    private void get_questions() {
        try {
            Cursor c = db.get_question();
            if (c.moveToFirst()) {
                do {

                    Log.w(TAG, "Id : " + c.getString(0) + " name : " + c.getString(2) + " data : " + c.getString(3));
                    if (sharedPreferenceManager.get_project_id().equalsIgnoreCase("14")) {
                        if (c.getString(2).contains("Candidate Support_")) {
                            continue;
                        } else {
                            Questions questions = new Questions(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                            list.add(questions);
                        }
                    } else {
                        Questions questions = new Questions(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                        list.add(questions);
                    }
                    Log.w(TAG, "question size" + list.size());
                } while (c.moveToNext());
            }

            if (list.size() > 0) {
                Collections.sort(list, new QuestionOrderComparator());
            } else {
                Toastmsg(QuestionareActivity.this, noQuestion);
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
                if (questionFlag.equalsIgnoreCase("OPCP") || questionFlag.equalsIgnoreCase("kp")) {

                    qusCount++;

                    LinearLayout layout = get_header_layout();

                    Log.w(TAG, questions.getName() + " order " + object.getString("order"));
                    String questionType = object.getString("questionType");
                    switch (questionType.toLowerCase()) {

                        case Constants.QUESTION_TYPE_SINGLECHOICE:
                            LinearLayout single_choice_layout = create_spinner_choice_layout(object, i);
                            container.addView(createCardView(single_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_PHONEINPUT:
                            LinearLayout phone_input_choice_layout = create_edittext_input_layout(object, i);
                            container.addView(createCardView(phone_input_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_MULTICHOICE:
                            LinearLayout multi_choice_layout = create_multi_choice_layout(object, i);
                            container.addView(createCardView(multi_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_MULTICHOICEWEB:
                            LinearLayout multi_choice_layoutweb = create_multi_choice_layout(object, i);
                            container.addView(createCardView(multi_choice_layoutweb));
                            break;
                        case Constants.QUESTION_TYPE_SINGLEINPUT:
                            LinearLayout single_input_layout = create_edittext_singleinput_layout(object, i);
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

    private LinearLayout createHeaderLayout(String title, int pos) {

        LinearLayout layout = new LinearLayout(QuestionareActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(params);

        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(QuestionareActivity.this);
        imageView.setId(pos);
        imageView.setImageDrawable(ContextCompat.getDrawable(QuestionareActivity.this, R.drawable.answered));

        imageView.setVisibility(GONE);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(70, 70);
        imageView.setLayoutParams(params1);
        layout.addView(imageView);

        TextView textView = new TextView(QuestionareActivity.this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(params2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);

        textView.setTextColor(ContextCompat.getColor(QuestionareActivity.this, R.color.icons));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackground(ContextCompat.getDrawable(QuestionareActivity.this, R.drawable.material_shadow1));
        } else {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(QuestionareActivity.this, R.drawable.material_shadow1));
        }
        // textView.setBackgroundColor(ContextCompat.getColor(QuestionareActivity.this, R.color.divider));
        textView.setText(title);
        textView.setTypeface(changeFont());

        layout.addView(textView);


        return layout;
        //  params.addRule(RelativeLayout.ALIGN_START, imageView.getId());
    }


    private Typeface changeFont() {
        Typeface font = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            font = getResources().getFont(R.font.optima_bold);
        } else {
            font = ResourcesCompat.getFont(this, R.font.optima_bold);
        }
        return font;
    }


    //hide question layout
    private void hide_question_layout() {

        container.setVisibility(GONE);
        btn_add.setVisibility(GONE);
        scrollView.setVisibility(GONE);

    }

    //show question layout
    private void show_question_layout() {

        if (qusCount > 0) {
            if (!container.isShown()) {
                scrollView.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                btn_add.setVisibility(View.VISIBLE);
            }


        } else {
            hide_question_layout();
            Toastmsg(QuestionareActivity.this, noQuestion);
            finish_activity();
        }
    }


    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;
    @BindString(R.string.successfullyAdded)
    String successfullyAdded;

    @OnClick(R.id.btn_add)
    public void add() {

        try {
            isEditTextEmpty = false;
            // isSingleChoiceNotAnswered = false;
            String mobile_number = getMobileNumber();


            String surveyId = DateFormat.format("yyyyMMdd_hhmmss a", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();

            JSONObject object = getSurveyData(surveyId);

            Log.w(TAG, "Data obj " + object.toString());

            boolean isObjectEmpty = false;

            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                if (object.get(key).toString().isEmpty()) {
                    isObjectEmpty = true;
                    break;
                }
            }


            Log.w(TAG, "is obj empty : " + isObjectEmpty);

            if (!isEditTextEmpty) {
                if (!isObjectEmpty) {
                    insert_data(mobile_number, object, surveyId);
                    onRecord(false);

                } else {
                    Toastmsg(QuestionareActivity.this, "All fields are required");
                }
            } else {

                Toastmsg(QuestionareActivity.this, "All fields are required");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toastmsg(QuestionareActivity.this, "Error try again later");
        }

    }


    private String getName() {
        String name = "empty";
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
            } else {
                editText.setError("please fill");
                isEditTextEmpty = true;
            }
        }
        return mobile;
    }


    private String getBooth() {
        String booth = "empty";
        for (HashMap.Entry<String, EditText> entry : edit_text_list.entrySet()) {
            EditText editText = (EditText) entry.getValue();
            String edit_value = editText.getText().toString().trim();
            String edit_key = entry.getKey();

            if (edit_key.equalsIgnoreCase("Booth Number")) {
                booth = edit_value;
            }

            if (!edit_value.isEmpty()) {
                survey_result.put(edit_key, edit_value);
            }
        }
        return booth;

    }

    @BindString(R.string.mobileNumberExist)
    String mobilNumberExist;

    @BindString(R.string.validMobile)
    String validMobile;


    private class sendNotification extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... voids) {

            final String surveyid = voids[0];

            Log.w(TAG, "Survey id :" + surveyid);


            StringRequest request = new StringRequest(Request.Method.POST, "http://bloodambulance.com/bloodapp/php/sent_notification.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.w(TAG, "Response :" + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", sharedPreferenceManager.get_username());
                    map.put("id", sharedPreferenceManager.get_user_id());
                    map.put("surveyid", surveyid);
                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(QuestionareActivity.this);
            queue.add(request);
            queue.getCache().clear();

            return null;
        }


    }

    long diffInSeconds = 0;


    private void insert_data(String mobile_number, JSONObject object, String surveyId) {


        if (mobile_number.length() == 10) {

            Log.w(TAG, "Latitude : " + txtLatitude.getText().toString().trim() + " longitude :" + txtLongitude.getText().toString().trim());
            if (!db.check_location(txtLatitude.getText().toString().trim(), txtLongitude.getText().toString().trim())) {
                try {
                    //   Log.w(TAG, "Not duplicate survey");
                    object.put("duplicate_survey", "no");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    object.put("duplicate_survey", "yes");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!db.check_mobile_number_exist(mobile_number)) {

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


                Log.w(TAG, "Seconds : " + diffInSeconds);
                if (diffInSeconds >= 90) {


                    VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", object.toString(), get_current_date(), false, surveyId);


                    BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                            booth_name, "Questionnaire", userType);

                    db.insertBoothStats(stats);
                    //VotersDatabase votersDatabase = new VotersDatabase(AddVoterActivity.this);
                    db.insert_voter_attribute(attribute);
                    //  votersDatabase.update_survey(attribute.getVoterCardNumber());


                    // latitude : 13.0085614 longitude :77.583277

                    db.insert_mobile_number(mobile_number, txtLatitude.getText().toString().trim(), txtLongitude.getText().toString().trim());
                    Toastmsg(QuestionareActivity.this, successfullyAdded);
                    QuestionareActivity.super.onBackPressed();
                    QuestionareActivity.this.finish();

                } else {

                    alertForNotValidSurvey();
                  /*  Log.w(TAG, "not a valid survey");
                    Toastmsg(QuestionareActivity.this, "Not a validd survey");*/
                }

            } else {
                Toast.makeText(QuestionareActivity.this, mobilNumberExist, Toast.LENGTH_LONG).show();
            }
        } else {
            Toastmsg(QuestionareActivity.this, validMobile);
        }
    }


    private boolean isEditTextEmpty = false;

    /*get json object for inserting data*/
    private JSONObject getSurveyData(String surveyId) {
        /*single choice edittext list*/

        Log.w(TAG, "single choice : " + edit_text_list.size() + " " + single_choice_edittext.size());
        if (single_choice_edittext.size() > 0) {
            for (HashMap.Entry<String, EditText> entry : single_choice_edittext.entrySet()) {
                EditText editText = (EditText) entry.getValue();
                String edit_value = editText.getText().toString().trim();
                Log.w(TAG, "edittext value " + edit_value);
                String edit_key = entry.getKey();
                if (!edit_value.isEmpty()) {
                    survey_result.put(edit_key, edit_value);
                } else {
                    isEditTextEmpty = true;
                    editText.setError("please fill");
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

                    int count = 1;
                    if (c.moveToFirst()) {
                        do {

                            if (c.getCount() == count) {
                                result += c.getString(1);
                            } else {
                                result += c.getString(1) + " $ ";
                            }
                            count++;

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
            survey_result.put("latitude", txtLatitude.getText().toString().trim());
            survey_result.put("longitude", txtLongitude.getText().toString().trim());
        } catch (Exception e) {
            survey_result.put("latitude", "Not Available");
            survey_result.put("longitude", "Not Available");
            e.printStackTrace();
        }
        survey_result.put("booth_name", booth_name);
        survey_result.put("surveyid", surveyId);
        survey_result.put("address", add);

        survey_result.put("respondantname", getName());
        survey_result.put("mobile", getMobileNumber());


        LocationCalculator cal = new LocationCalculator();
        try {
            if (sharedPreferenceManager.getLastLat().length() > 0 && sharedPreferenceManager.getLastLng().length() > 0) {

                double lat = Double.parseDouble(sharedPreferenceManager.getLastLat());
                double lng = Double.parseDouble(sharedPreferenceManager.getLastLng());

                double distance = cal.calculateDistance(lat, lng, latitude, longitude);

                Log.w(TAG, "last lat " + lat + " last lng " + lng);

                Log.w(TAG, "new lat : " + txtLatitude.getText().toString().trim() + " long " + txtLongitude.getText().toString().trim());


                sharedPreferenceManager.setLastLat(txtLatitude.getText().toString().trim());
                sharedPreferenceManager.setLastLng(txtLongitude.getText().toString().trim());
                survey_result.put("dis", distance + "");
                Log.w(TAG, "distance " + distance);

            } else {
                sharedPreferenceManager.setLastLat(txtLatitude.getText().toString().trim());
                sharedPreferenceManager.setLastLng(txtLongitude.getText().toString().trim());
                Log.w(TAG, "value empty");
                survey_result.put("dis", "NA");
            }
        } catch (Exception e) {
            survey_result.put("dis", "NA");
            e.printStackTrace();
        }

        JSONObject object = new JSONObject();
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

    @OnClick(R.id.btnOk)
    public void startSurvey() {

        mapFragment.getView().setVisibility(GONE);
        show_question_layout();
        deleteAudioFile();

        Log.w(TAG, "Latitude :" + txtLatitude.getText().toString() + " longitude :" + txtLongitude.getText().toString());
        try {
            startedTime = new Date();
        } catch (Exception e) {
            e.printStackTrace();
        }

        onRecord(false);
        onRecord(true);
        isMapClicked = true;
        setLocationLayout.setVisibility(GONE);

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
            mp = MediaPlayer.create(this, R.raw.erro);
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

    public void alertForNotValidSurvey() {


        playAudio();

        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle("Not a Valid Survey")
                .setCancelable(false)
                .setMessage("Survey seems to be FAKE.\n" +
                        "Time taken to complete the survey is very less than the average time required for a proper survey...")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        deleteAudioFile();
                        QuestionareActivity.super.onBackPressed();
                    }
                })

                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
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
                        getAddress(QuestionareActivity.this, location.getLatitude(), location.getLongitude());
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
                Toastmsg(QuestionareActivity.this, "please Enable gps");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
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
            EditText editText = new EditText(QuestionareActivity.this);
            // editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setId(Integer.parseInt(object.getString("order")));
            edit_text_list.put(object.getString("attributeName"), editText);
            Log.w(TAG, "Edit text list size " + edit_text_list.size());
            layout.addView(editText);
            dataObj.put(object.getString("attributeName"), "");
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
                        imageView.setVisibility(View.VISIBLE);
                    } else {
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

            dataObj.put(object.getString("attributeName"), "");
            for (int i = 0; i <= array.length() - 1; i++) {
                final CheckBox checkBox = new CheckBox(QuestionareActivity.this);
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
            EditText editTextsingle = new EditText(QuestionareActivity.this);
            editTextsingle.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextsingle.setId(Integer.parseInt(object.getString("order")));
            edit_text_list.put(object.getString("attributeName"), editTextsingle);
            Log.w(TAG, "Edit text list size " + edit_text_list.size());
            layout.addView(editTextsingle);

            dataObj.put(object.getString("attributeName"), "");

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


    /*create cardview*/
    private CardView createCardView(View view) {
        CardView cardView = new CardView(this);
        cardView.setUseCompatPadding(true);
        //   cardView.setContentPadding(5, 5, 5, 5);
        cardView.setRadius(10.0F);
        cardView.addView(view);
        return cardView;
    }


    private String getLanguage() {

        return sharedPreferenceManager.getLanguage();
    }

    private ArrayList<String> getChoices(JSONArray jsonArray) throws JSONException {

        ArrayList<String> choices = new ArrayList<String>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++)
            choices.add(jsonArray.getString(i));
        return choices;
    }


    private LinearLayout create_spinner_choice_layout(final JSONObject object, int pos) {

        LinearLayout layout = get_header_layout();


        final Spinner spinner = new Spinner(QuestionareActivity.this);
        final EditText editText = new EditText(QuestionareActivity.this);

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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, options);
            spinner.setAdapter(adapter);
            adapter.setDropDownViewResource(R.layout.spinner_text_theatre);

            dataObj.put(object.getString("attributeName"), "");
            //  survey_result.put(object.getString("attributeName"), "");

            /*final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(QuestionareActivity.this, android.R.layout.simple_spinner_dropdown_item, options);
            spinner.setAdapter(spinnerArrayAdapter);*/

            editText.setHint("Please enter text");
            editText.setVisibility(GONE);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    try {
                        if (position != 0) {

                            try {

                                hideKeyboard(QuestionareActivity.this);
                                String res = englishOptions.get(position);
                                editText.setVisibility(GONE);
                                survey_result.put(object.getString("attributeName"), res);
                                tickImage.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            tickImage.setVisibility(View.GONE);

                            survey_result.put(object.getString("attributeName"), "");
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

                        } else {
                            single_choice_edittext.remove(object.getString("attributeName"));
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

    private TextView create_question_textview(String question) {
        TextView textView = new TextView(this);
        textView.setText(question);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return textView;
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
        return textView;
    }


    //finishh the activity
    private void finish_activity() {
        QuestionareActivity.super.onBackPressed();
        QuestionareActivity.this.finish();
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
    public void onLocationChanged(Location location) {
        getLocationLayout.setVisibility(GONE);

        if (!isMapClicked && !booth_name.isEmpty()) {
            setLocationLayout.setVisibility(View.VISIBLE);
        }


        getAddress(QuestionareActivity.this, location.getLatitude(), location.getLongitude());

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        getLocation();
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

    String add = "NA";

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
