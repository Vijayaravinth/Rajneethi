package com.software.cb.rajneethi.activity;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.gmail.samehadar.iosdialog.IOSDialog;

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

import static android.view.View.GONE;

/**
 * Created by w7 on 11/1/2017.
 */

public class BSKActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;



    @BindString(R.string.bsk)
    String title;

    @BindView(R.id.container)
    LinearLayout container;

    @BindView(R.id.btn_save)
    Button btn_save;
    MyDatabase db;

    private HashMap<String, String> survey_result = new HashMap<>();
    private ArrayList<HashMap<String, String>> result_map = new ArrayList<>();
    private HashMap<String, EditText> edit_text_list = new HashMap<>();
    private ArrayList<Questions> list = new ArrayList<>();

    private HashMap<String, EditText> single_choice_edittext = new HashMap<>();
    private GPSTracker gpsTracker;

    String name;

    @BindString(R.string.successfullyAdded)
    String successfulyAdded;

    private SharedPreferenceManager sharedPreferenceManager;
    private ArrayList<String> booth_list = new ArrayList<>();

    @BindView(R.id.booth_list_spinner)
    Spinner booth_list_spinner;

    String booth_name = "";

    String userType = "";
    @BindString(R.string.noInternet)
    String noInternet;

    IOSDialog dialog;
    private String TAG = "BSK Activity";

    private String mFileName = null;
    private MediaRecorder mRecorder = null;

    int bskQusCount = 0;
    File newDir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsk);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,title);

        if (getIntent().getExtras() != null) {
            userType = getIntent().getExtras().getString("userType");
        }

        dialog = show_dialog(BSKActivity.this, false);

        hide_question_layout();

        db = new MyDatabase(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
         /*delete table value*/
        db.delete_checkbox_values();

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }


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

                Toastmsg(BSKActivity.this, noInternet);
                finish_activity();
            }
        }

        gpsTracker = new GPSTracker(this);

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
        mFileName += "AUD-" + sharedPreferenceManager.get_user_id() + "-" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    @BindString(R.string.Error)
    String Error;

    @BindString(R.string.selectBooth)
    String selectBooth;

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

                Toastmsg(BSKActivity.this, Error);

            }
        });

        //  VolleySingleton.getInstance(this).addToRequestQueue(request);

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    //finishh the activity
    private void finish_activity() {
        BSKActivity.super.onBackPressed();
        BSKActivity.this.finish();
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
                    booth_name = booth_list_spinner.getSelectedItem().toString();
                    show_question_layout();
                    deleteAudioFile();
                    onRecord(false);
                    onRecord(true);
                } else {
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
            set_adapter_for_booth_list();
        } else {
            hide_question_layout();
            Toastmsg(BSKActivity.this, noBoothAllocated);
            finish_activity();
        }
    }

    //hide question layout
    private void hide_question_layout() {

        container.setVisibility(GONE);
        btn_save.setVisibility(GONE);


    }

    //show question layout
    private void show_question_layout() {
        if (bskQusCount > 0) {
            if (!container.isShown()) {
                container.setVisibility(View.VISIBLE);
            }

            if (!btn_save.isShown()) {
                btn_save.setVisibility(View.VISIBLE);
            }
        } else {
            Toastmsg(BSKActivity.this, noQuestion);
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

    private ArrayList<String> getChoices(JSONArray jsonArray) throws JSONException {

        ArrayList<String> choices = new ArrayList<String>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++)
            choices.add(jsonArray.getString(i));
        return choices;
    }

    private TextView create_header_textview(String title) {
        // TextView textView = new TextView(this, null, R.style.FieldSurvey_Header_Text);
        TextView textView = new TextView(this);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(this, R.color.icons));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.side_nav_bar));
        } else {
            textView.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.side_nav_bar));
        }
        // textView.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
        textView.setText(title);
        return textView;
    }

    private TextView create_question_textview(String question) {
        TextView textView = new TextView(this);
        textView.setText(question);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextColor(ContextCompat.getColor(this, R.color.primary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return textView;
    }

    private LinearLayout create_spinner_choice_layout(final JSONObject object) {

        LinearLayout layout = get_header_layout();
        final Spinner spinner = new Spinner(this);
        final EditText editText = new EditText(this);


        try {
            TextView textView = create_header_textview(object.getString("title"));
            layout.addView(textView);

            TextView question_text = create_question_textview(object.getString("questionText"));
            layout.addView(question_text);


            ArrayList<String> options = getChoices(new JSONArray(object.getString("answerChoices")));
            options.add(0, "Select one...");
            options.add("User Input");
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
            spinner.setAdapter(spinnerArrayAdapter);

            editText.setHint("Please enter text");
            editText.setVisibility(View.GONE);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        if (position != 0) {
                            editText.setVisibility(View.GONE);
                            survey_result.put(object.getString("attributeName"), spinner.getSelectedItem().toString());
                        }

                        if (spinner.getSelectedItem().toString().equals("User Input")) {
                            editText.setVisibility(View.VISIBLE);
                            single_choice_edittext.put(object.getString("attributeName"), editText);

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

    /*create cardview*/
    private CardView createCardView(View view) {
        CardView cardView = new CardView(this);
        cardView.setUseCompatPadding(true);
        //   cardView.setContentPadding(5, 5, 5, 5);
        cardView.setRadius(5.0F);
        cardView.addView(view);
        return cardView;
    }

    private void get_questions() {

        try {
            Cursor c = db.get_question();
            if (c.moveToFirst()) {
                do {
                    Questions questions = new Questions(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                    list.add(questions);
                    Log.w(TAG, "question size" + list.size());
                } while (c.moveToNext());
                c.close();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        if (list.size() > 0) {
            Collections.sort(list, new QuestionOrderComparator());
        } else {

            btn_save.setVisibility(View.GONE);
            Toast.makeText(BSKActivity.this, noQuestion, Toast.LENGTH_LONG).show();

        }
    }

    private void renderDynamicLayout() {


        for (int i = 0; i <= list.size() - 1; i++) {
            Questions questions = list.get(i);
            try {
                JSONObject object = new JSONObject(questions.getData());

                String questionFlag = object.getString("questionFlag");
                if (questionFlag.equals("BSKNS") || questionFlag.equals("BSKBOTH")) {

                    bskQusCount++;
                    LinearLayout layout = get_header_layout();
                    Log.w(TAG, questions.getName() + " data " + questions.getData() + " order " + object.getString("order"));

                    String questionType = object.getString("questionType");
                    switch (questionType.toLowerCase()) {

                        case Constants.QUESTION_TYPE_SINGLECHOICE:
                            LinearLayout single_choice_layout = create_spinner_choice_layout(object);
                            container.addView(createCardView(single_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_PHONEINPUT:
                            LinearLayout phone_input_choice_layout = create_edittext_input_layout(object);
                            container.addView(createCardView(phone_input_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_MULTICHOICE:
                            LinearLayout multi_choice_layout = create_multi_choice_layout(object);
                            container.addView(createCardView(multi_choice_layout));
                            break;
                        case Constants.QUESTION_TYPE_MULTICHOICEWEB:
                            LinearLayout multi_choice_layoutweb = create_multi_choice_layout(object);
                            container.addView(createCardView(multi_choice_layoutweb));
                            break;
                        case Constants.QUESTION_TYPE_SINGLEINPUT:
                            LinearLayout single_input_layout = create_edittext_singleinput_layout(object);
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

    private LinearLayout create_edittext_singleinput_layout(final JSONObject object) {
        LinearLayout layout = get_header_layout();

        try {
            final String title1 = object.getString("title");
            TextView textView1 = create_header_textview(title1);
            final String title = object.getString("attributeName");
            TextView textView = create_header_textview(title);
            layout.addView(textView1);
            TextView question_text = create_question_textview(object.getString("questionText"));
            layout.addView(question_text);

            Log.w(TAG, "order " + object.getString("order"));
            EditText editText = new EditText(this);
            // editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setId(Integer.parseInt(object.getString("order")));
            edit_text_list.put(title, editText);
            Log.w(TAG, "Edit text list size " + edit_text_list.size());
            layout.addView(editText);

            //  survey_result.put(title, editText.getText().toString().trim());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layout;
    }

    private LinearLayout create_multi_choice_layout(final JSONObject object) {
        LinearLayout layout = get_header_layout();
        try {

            final String title1 = object.getString("title");
            TextView textView1 = create_header_textview(title1);
            final String title = object.getString("attributeName");
            TextView textView = create_header_textview(title);
            layout.addView(textView1);
            TextView question_text = create_question_textview(object.getString("questionText"));
            layout.addView(question_text);
            final JSONArray array = new JSONArray(object.getString("answerChoices"));

            for (int i = 0; i <= array.length() - 1; i++) {
                final CheckBox checkBox = new CheckBox(this);
                checkBox.setText(array.getString(i));
                checkBox.setId(i);
                layout.addView(checkBox);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((CheckBox) v).isChecked()) {

                            Log.w(TAG, "Check box is checked");
                            String value = ((CheckBox) v).getText().toString();
                            Log.w(TAG, "checkbox value " + value);


                            db.insert_checkbox_value(title, value);

                        } else {
                            String value = ((CheckBox) v).getText().toString();
                            db.delet_un_checkbox(title, value);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layout;

    }

    private LinearLayout create_edittext_input_layout(final JSONObject object) {
        LinearLayout layout = get_header_layout();

        try {
            final String title1 = object.getString("title");
            TextView textView1 = create_header_textview(title1);
            final String title = object.getString("attributeName");
            TextView textView = create_header_textview(title);
            layout.addView(textView1);
            TextView question_text = create_question_textview(object.getString("questionText"));
            layout.addView(question_text);

            Log.w(TAG, "order " + object.getString("order"));
            EditText editTextsingle = new EditText(this);
            editTextsingle.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextsingle.setId(Integer.parseInt(object.getString("order")));
            edit_text_list.put(title, editTextsingle);
            Log.w(TAG, "Edit text list size " + edit_text_list.size());
            layout.addView(editTextsingle);

            //  survey_result.put(title, editText.getText().toString().trim());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return layout;
    }

    @OnClick(R.id.btn_save)
    public void save() {


        String mobile_number = "";

        for (HashMap.Entry<String, EditText> entry : edit_text_list.entrySet()) {
            EditText editText = (EditText) entry.getValue();
            String edit_value = editText.getText().toString().trim();
            String edit_key = entry.getKey();

            if (edit_key.equals("mobilenumber")) {
                mobile_number = edit_value;
            }
            Log.w(TAG, "edittext key " + edit_key);
            if (!edit_value.isEmpty()) {
                survey_result.put(edit_key, edit_value);
            }
        }


        Log.w(TAG, "single choice edit text " + single_choice_edittext.size());
        /*single choice edittext list*/
        if (single_choice_edittext.size() > 0) {
            for (HashMap.Entry<String, EditText> entry : single_choice_edittext.entrySet()) {
                EditText editText = (EditText) entry.getValue();
                String edit_value = editText.getText().toString().trim();

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

                    Log.w(TAG, "result " + result);

                    survey_result.put(title, result);
                    result = "";
                } while (cursor.moveToNext());

                cursor.close();
            }


        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        String audioFilename = mFileName;
        survey_result.put("surveyType", "BSK");
        //added for new 4 attribute
        survey_result.put("surveyDate", new Date().toString());
        survey_result.put("projectId", sharedPreferenceManager.get_project_id());
        survey_result.put("partyWorker", sharedPreferenceManager.get_username());
        survey_result.put("boothName", booth_name);
        // survey_result.put("wardName", ((VoterProfileActivity) this).wardName);
        survey_result.put("audioFilename", audioFilename.substring(audioFilename.lastIndexOf("/") + 1, audioFilename.length()));


        String latitude = gpsTracker.getLatitude() == 0.0 ? "Not found " : gpsTracker.getLatitude() + "";
        String longitude = gpsTracker.getLongitude() == 0.0 ? "Not found" : gpsTracker.getLatitude() + "";

        survey_result.put("latitude", latitude);
        survey_result.put("longitude", longitude);
        survey_result.put("userType", userType);

        if (!db.check_location(latitude, longitude)) {
            survey_result.put("duplicate_survey", "no");
        } else {
            survey_result.put("duplicate_survey", "yes");
        }


        boolean is_all_filled = true;

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


        mobile_number = getMobileNumber();

        if (!db.check_mobile_number_exist(mobile_number)) {
            VoterAttribute attribute = new VoterAttribute("BSK", "BSK Survey", object.toString(), get_current_date(), false, object.toString());
            JSONArray array = new JSONArray();


            onRecord(false);
            VotersDatabase votersDatabase = new VotersDatabase(this);
            db.insert_voter_attribute(attribute);
            db.insert_mobile_number(mobile_number, latitude, longitude);
            votersDatabase.update_survey(attribute.getVoterCardNumber());
            Toast.makeText(this, successfulyAdded,
                    Toast.LENGTH_LONG).show();
            //Toastmsg(FieldSurveyFragment.this, "Successfully inserted");
            // FieldSurveyFragment.super.onBackPressed();

            //startActivity(new Intent(this, RespondentInfoActivity.class).putExtra("survey", attribute).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish_activity();

        } else {
            Toast.makeText(this, mobilNumberExist, Toast.LENGTH_LONG).show();
        }


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

        Log.w(TAG, "onBack pressed");
        super.onBackPressed();
        deleteAudioFile();
    }

    /*get mobile number from edittext*/
    private String getMobileNumber() {
        String mobile = "";
        for (HashMap.Entry<String, EditText> entry : edit_text_list.entrySet()) {
            EditText editText = (EditText) entry.getValue();
            String edit_value = editText.getText().toString().trim();
            String edit_key = entry.getKey();

            if (edit_key.equals("mobilenumber")) {
                mobile = edit_value;
            }

            if (!edit_value.isEmpty()) {
                survey_result.put(edit_key, edit_value);
            }
        }
        return mobile;
    }

    @BindString(R.string.mobileNumberExist)
    String mobilNumberExist;

    @BindString(R.string.noQuestion)
    String noQuestion;

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRecord(false);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

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
}
