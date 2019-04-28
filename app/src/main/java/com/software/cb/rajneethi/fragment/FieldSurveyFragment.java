package com.software.cb.rajneethi.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.VoterProfileActivity;
import com.software.cb.rajneethi.activity.VoterSearchActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
 * Created by monika on 4/2/2017.
 */

public class FieldSurveyFragment extends Fragment {

    public FieldSurveyFragment() {
    }

    @BindView(R.id.container)
    LinearLayout container;

    @BindView(R.id.btn_save)
    Button btn_save;

    int qusCount = 0;


    String dir;
    String file;

    Uri outputFileUri;
    boolean photograph = false, id_proof = false, address_proof = false;
    String path = "empty", address_path = "empty", id_path = "empty", type;
    File newfile;
    private SharedPreferenceManager sharedPreferenceManager;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PIC_CROP = 1;

    String currentImageCapture;

    MyDatabase db;
    VotersDatabase votersDatabase;
    VoterAttribute voterAttribute;
    ArrayList<TextView> textViewArrayList = new ArrayList<>();
    ArrayList<EditText> editTextArrayList = new ArrayList<>();

    private static final String TAG = "Filed Survey";

    private ArrayList<Questions> list = new ArrayList<>();

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.blanche.carte.rajneethi.fileprovider";

    private HashMap<String, String> survey_result = new HashMap<>();
    private ArrayList<HashMap<String, String>> result_map = new ArrayList<>();
    private HashMap<String, EditText> edit_text_list = new HashMap<>();

    private HashMap<String, EditText> single_choice_edittext = new HashMap<>();

    String name;

    @BindString(R.string.successfullyAdded)
    String successfulyAdded;

    @BindView(R.id.txtNoQus)
    TextView txtNoQus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_field_survey, container, false);
       // setHasOptionsMenu(true);
        sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        ButterKnife.bind(this, v);

        db = new MyDatabase(getActivity());

        /*delete table value*/
        db.delete_checkbox_values();

        try {
            Cursor c = db.get_title();
            c.moveToFirst();
            Log.w(TAG, "cursor count " + c.getCount());
        } catch (CursorIndexOutOfBoundsException r) {

        }

        name = ((VoterProfileActivity) getActivity()).name;

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/";
        File newdir = new File(dir);
        if (!newdir.exists()) {
            newdir.mkdirs();
        }

        //get_questions();
        //  renderDynamicLayout();



        return v;
    }

    public void get_questions() {

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

            btn_save.setVisibility(GONE);
            Toast.makeText(getActivity(), noQuestion, Toast.LENGTH_LONG).show();

        }
    }


    @BindString(R.string.noQuestion)
    String noQuestion;

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

    @BindString(R.string.familyHead)
    String familyHead;

  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.field_survey_menu, menu);
        MenuItem item = menu.findItem(R.id.familyHead);
        SpannableString s = new SpannableString(familyHead);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        item.setTitle(s);
    }*/

    @BindString(R.string.surveyTaken)
    String surveyTaken;

    @OnClick(R.id.btn_save)
    public void save() {

        if (((VoterProfileActivity) getActivity()).is_survey_taken) {
            Toast.makeText(getActivity(), surveyTaken, Toast.LENGTH_SHORT).show();
        } else {
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

                                result += c.getString(1) + ",";

                            } while (c.moveToNext());
                        }

                        c.close();

                        Log.w(TAG, "result " + result);

                        survey_result.put(title, check_contains_comma(result));
                        result = "";
                    } while (cursor.moveToNext());

                    cursor.close();
                }


            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }


         /*   survey_result.put("photo", path);
            survey_result.put("id_photo", id_path);
            survey_result.put("address_photo", address_path);
*/
            String audioFilename = ((VoterProfileActivity) getActivity()).mFileName;


            String userType = ((VoterProfileActivity) getActivity()).userType;
            String boothName = ((VoterProfileActivity) getActivity()).boothName;

            //added for new 4 attribute
            survey_result.put("surveyDate", new Date().toString());
            survey_result.put("surveyType", "Field Survey");
            survey_result.put("projectId", sharedPreferenceManager.get_project_id());
            survey_result.put("partyWorker", sharedPreferenceManager.get_user_id());
            survey_result.put("pwname", sharedPreferenceManager.get_username());
            survey_result.put("booth_name", boothName);
            survey_result.put("familyHead", ((VoterProfileActivity) getActivity()).isFamilyHead);

            //    survey_result.put("wardName", ((VoterProfileActivity) getActivity()).wardName);
            survey_result.put("audioFileName", audioFilename.substring(audioFilename.lastIndexOf("/") + 1, audioFilename.length()));


            String latitude =((VoterProfileActivity)getActivity()).latitude == 0.0 ? "Not found " : ((VoterProfileActivity)getActivity()).latitude + "";
            String longitude =((VoterProfileActivity)getActivity()).longitude == 0.0 ? "Not found" : ((VoterProfileActivity)getActivity()).longitude + "";

            survey_result.put("latitude", latitude);
            survey_result.put("longitude", longitude);
            survey_result.put("userType", userType);

            if (!db.check_location(latitude, longitude)) {
                survey_result.put("duplicate_survey", "no");
            } else {
                survey_result.put("duplicate_survey", "yes");
            }

            String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
            Log.w(TAG, "survey id " + surveyId);
            survey_result.put("surveyid", surveyId);

            //boolean is_all_filled = true;

            JSONObject object = new JSONObject();
            for (HashMap.Entry<String, String> entry : survey_result.entrySet()) {
                String key1 = entry.getKey();
                String value = entry.getValue();
                try {
/*
                    Log.w(TAG, "Value check null :" + (survey_result.get(key1) == null));
                    if (survey_result.get(key1) == null) {
                        is_all_filled = false;
                    }*/
                    object.put(key1, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            mobile_number = getMobileNumber();

            String name = ((VoterProfileActivity) getActivity()).name;

            if (!(mobile_number.equals("empty") || name.equals("empty"))) {
                try {
                    object.put("mobile", mobile_number);
                    object.put("respondantname", name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Name and mobile number empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.w(TAG, "Mobile number : " + mobile_number + " name  : " + name);
            if (mobile_number.length() == 10) {
                if (!db.check_mobile_number_exist(mobile_number)) {
                    VoterAttribute attribute = new VoterAttribute(((VoterProfileActivity) getActivity()).votercardnumber, "Survey", object.toString(), ((VoterProfileActivity) getActivity()).date, false, surveyId);


                    BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                            boothName, "Survey", userType);

                    db.insertBoothStats(stats);

                    ((VoterProfileActivity) getActivity()).onRecord(false);
                    VotersDatabase votersDatabase = new VotersDatabase(getActivity());
                    db.insert_voter_attribute(attribute);
                    db.insert_mobile_number(mobile_number, latitude, longitude);
                    new VotersDatabase(getActivity()).updateMobileNumber(((VoterProfileActivity) getActivity()).votercardnumber, mobile_number);
                    votersDatabase.update_survey(attribute.getVoterCardNumber());
                    Toast.makeText(getActivity(), successfulyAdded,
                            Toast.LENGTH_LONG).show();

                    //Toastmsg(FieldSurveyFragment.this, "Successfully inserted");
                    //FieldSurveyFragment.super.onBackPressed();

                    // ((VoterProfileActivity) getActivity()).onBackPressed();
                    //startActivity(new Intent(getActivity(), RespondentInfoActivity.class).putExtra("survey", attribute).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    getActivity().startActivity(new Intent(getActivity(), VoterSearchActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();

                } else {

                    Toast.makeText(getActivity(), mobileNumberExist, Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(getActivity(), validMobile, Toast.LENGTH_LONG).show();
                return;
            }


        }
    }


    @BindString(R.string.validMobile)
    String validMobile;


    /*get mobile number from editText*/
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

    private String getName() {
        String name = "empty";
        for (HashMap.Entry<String, EditText> entry : edit_text_list.entrySet()) {
            EditText editText = (EditText) entry.getValue();
            String edit_value = editText.getText().toString().trim();
            String edit_key = entry.getKey();

            if (edit_key.contains("Person Name") || edit_key.contains("influential person")) {
                name = edit_value;
            }


        }

        return name;
    }


    @BindString(R.string.mobileNumberExist)
    String mobileNumberExist;

    public void renderDynamicLayout() {

        for (int i = 0; i <= list.size() - 1; i++) {
            Questions questions = list.get(i);

            String flag1 = "", flag2 = "";

            if (((VoterProfileActivity) getActivity()).bskMode) {

                flag1 = "BSKSS";
                flag2 = "BSKBOTH";
            } else {
                flag1 = "BOTH";
                flag2 = "SS";
            }


            try {
                JSONObject object = new JSONObject(questions.getData());

                String questionFlag = object.getString("questionFlag");
                if (questionFlag.equals(flag1) || questionFlag.equals(flag2)) {

                    qusCount++;
                    LinearLayout layout = get_header_layout();
                    Log.w(TAG, questions.getName() + " data " + questions.getData() + " order " + object.getString("order"));

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

        if (qusCount == 0) {
            container.setVisibility(GONE);
            btn_save.setVisibility(GONE);
            txtNoQus.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), noQuestion, Toast.LENGTH_LONG).show();
        }

    }

    private String getLanguage() {

        return sharedPreferenceManager.getLanguage();
    }

    /*create cardview*/
    private CardView createCardView(View view) {
        CardView cardView = new CardView(getActivity());
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
                final CheckBox checkBox = new CheckBox(getActivity());
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
            EditText editTextsingle = new EditText(getActivity());
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
            EditText editText = new EditText(getActivity());
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


    //public void Toastmsg(FieldSurveyFragment context, String msg) {
    //  Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    //}

    private LinearLayout create_spinner_choice_layout(final JSONObject object, int pos) {

        LinearLayout layout = get_header_layout();


        final Spinner spinner = new Spinner(getActivity());
        final EditText editText = new EditText(getActivity());

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
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, options);
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


    private ArrayList<String> getChoices(JSONArray jsonArray) throws JSONException {

        ArrayList<String> choices = new ArrayList<String>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++)
            choices.add(jsonArray.getString(i));
        return choices;
    }

    private LinearLayout createHeaderLayout(String title, int pos) {

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(params);

        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getActivity());
        imageView.setId(pos);
        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.answered));

        imageView.setVisibility(GONE);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(70, 70);
        imageView.setLayoutParams(params1);
        layout.addView(imageView);

        TextView textView = new TextView(getActivity());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(params2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);

        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.icons));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow1));
        } else {
            layout.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow1));
        }
        // textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider));
        textView.setText(title);
        textView.setTypeface(changeFont());

        layout.addView(textView);


        return layout;
        //  params.addRule(RelativeLayout.ALIGN_START, imageView.getId());
    }

    private TextView create_header_textview(String title) {
        // TextView textView = new TextView(getActivity(), null, R.style.FieldSurvey_Header_Text);
        TextView textView = new TextView(getActivity());

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.icons));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow1));
        } else {
            textView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow1));
        }
        // textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider));
        textView.setText(title);
        textView.setTypeface(changeFont());
        return textView;
    }

    private Typeface changeFont() {
        Typeface font = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            font = getActivity().getResources().getFont(R.font.optima_bold);
        } else {
            font = ResourcesCompat.getFont(getActivity(), R.font.optima_bold);
        }
        return font;
    }

    private TextView create_question_textview(String question) {
        TextView textView = new TextView(getActivity());
        textView.setText(question);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTypeface(changeFont());
        return textView;
    }

    private LinearLayout get_header_layout() {

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        //  layout.setPadding(5, 5, 5, 5);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);
        layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.icons));


        return layout;
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
