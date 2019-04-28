package com.software.cb.rajneethi.moviesurvey;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.OpinionPollActivity;
import com.software.cb.rajneethi.activity.QuestionareActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.services.RecordService;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DELL on 02-01-2018.
 */

public class TheatrePeopleOpinionActivity3 extends Util {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.peopleOpinion)
    String title;

    @BindArray(R.array.hindiActor)
    String[] hindi;
    @BindArray(R.array.tamilActor)
    String[] tamil;
    @BindArray(R.array.teluguActor)
    String[] telugu;
    @BindArray(R.array.malayalamActor)
    String[] malayalam;
    @BindArray(R.array.englishActor)
    String[] english;

    @BindView(R.id.edtHindiActor)
    EditText edtHindiActorName;
    @BindView(R.id.edtTamilActor)
    EditText edtTamilActorName;
    @BindView(R.id.edtTeluguActor)
    EditText edtTeluguActorName;
    @BindView(R.id.edtMalayalamActor)
    EditText edtMalayalamActorName;
    @BindView(R.id.edtEnglishActor)
    EditText edtEnglishActorName;


    String part2;

    JSONObject finalObj = new JSONObject();

    String mFileName;

    private GPSTracker gpsTracker;
    private SharedPreferenceManager sharedPreferenceManager;
    private MyDatabase db;

    String whomWithWatchMovie = "", movieElements = "", languageMovieLike = "", hindiActorName = "", tamilActorName = "",
            malayalamActorName = "", teluguActorName = "", englishActorName = "", watchMultiplex = "",
            name = "", age = "", gender, education = "", occupation = "", mobile = "", motherTongue = "", economicStatus = "";
    boolean isHindiSelected, isTamilSelected, isTeluguSelected, isMalayalamSelected, isEnglishSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theatre_people_opinion3);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);

        gpsTracker = new GPSTracker(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        db = new MyDatabase(this);

        if (getIntent().getExtras() != null) {
            part2 = getIntent().getExtras().getString("part2");
            mFileName = getIntent().getExtras().getString("fileName");
        }

        setRadioGroupListener();
        setSpinnerAdapter();

        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10){
                    edtMobile.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setSpinnerAdapter() {
        ArrayAdapter<String> hindiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, hindi);
        spinnerHindiActor.setAdapter(hindiAdapter);

        spinnerHindiActor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    hindiActorName = spinnerHindiActor.getSelectedItem().toString();
                } else {
                    hindiActorName = "";
                }

                if (spinnerHindiActor.getSelectedItem().toString().equals("User input")) {
                    edtHindiActorName.setVisibility(View.VISIBLE);
                    edtHindiActorName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> tamilAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tamil);
        spinnerTamilActor.setAdapter(tamilAdapter);

        spinnerTamilActor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    tamilActorName = spinnerTamilActor.getSelectedItem().toString();
                    edtTamilActorName.setVisibility(View.GONE);
                } else {
                    tamilActorName = "";
                    edtTamilActorName.setVisibility(View.GONE);
                }
                if (spinnerTamilActor.getSelectedItem().toString().equals("User input")) {
                    edtTeluguActorName.setVisibility(View.VISIBLE);
                    edtTeluguActorName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> teluguAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, telugu);
        spinnerTeluguActor.setAdapter(teluguAdapter);

        spinnerTeluguActor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    teluguActorName = spinnerTeluguActor.getSelectedItem().toString();
                    edtTeluguActorName.setVisibility(View.GONE);

                } else {
                    teluguActorName = "";
                    edtTeluguActorName.setVisibility(View.GONE);
                }
                if (spinnerTeluguActor.getSelectedItem().toString().equals("User input")) {
                    edtTeluguActorName.setVisibility(View.VISIBLE);
                    edtTeluguActorName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> malayalamAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, malayalam);
        spinnerMalayalamActor.setAdapter(malayalamAdapter);

        spinnerMalayalamActor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    malayalamActorName = spinnerMalayalamActor.getSelectedItem().toString();
                    edtMalayalamActorName.setVisibility(View.GONE);
                } else {
                    malayalamActorName = "";
                    edtMalayalamActorName.setVisibility(View.GONE);
                }
                if (spinnerMalayalamActor.getSelectedItem().toString().equals("User input")) {
                    edtMalayalamActorName.setVisibility(View.VISIBLE);
                    edtMalayalamActorName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> englishAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, english);
        spinnerEnglishActor.setAdapter(englishAdapter);

        spinnerEnglishActor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    englishActorName = spinnerEnglishActor.getSelectedItem().toString().trim();
                    edtEnglishActorName.setVisibility(View.GONE);
                } else {
                    englishActorName = "";
                    edtEnglishActorName.setVisibility(View.GONE);
                }
                if (spinnerEnglishActor.getSelectedItem().toString().equals("User input")) {
                    edtEnglishActorName.setVisibility(View.VISIBLE);
                    edtEnglishActorName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRadioGroupListener() {
        rgMovieWithWhom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbWithAlone:
                        whomWithWatchMovie = "23a";
                        break;
                    case R.id.rbWithCouple:
                        whomWithWatchMovie = "23b";
                        break;
                    case R.id.rbWithFamily:
                        whomWithWatchMovie = "23c";
                        break;
                    case R.id.rbWithFriends:
                        whomWithWatchMovie = "23d";
                        break;
                    case R.id.rbWithColleagues:
                        whomWithWatchMovie = "23e";
                        break;
                }
            }
        });

        rgMovieElements.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbActing:
                        movieElements = "24a";
                        break;
                    case R.id.rbDialogue:
                        movieElements = "24b";
                        break;
                    case R.id.rbFavouriteActor:
                        movieElements = "24c";
                        break;
                    case R.id.rbFavouriteDirector:
                        movieElements = "24d";
                        break;
                    case R.id.rbFavouriteActress:
                        movieElements = "24e";
                        break;
                    case R.id.rbCamera:
                        movieElements = "24f";
                        break;
                    case R.id.rbMusic:
                        movieElements = "24g";
                        break;
                    case R.id.rbScreenplay:
                        movieElements = "24h";
                        break;
                    case R.id.rbSets:
                        movieElements = "24i";
                        break;
                    case R.id.rbSpecialEffects:
                        movieElements = "24j";
                        break;
                    case R.id.rbStory:
                        movieElements = "24k";
                        break;
                    case R.id.rbFight:
                        movieElements = "24l";
                        break;
                    case R.id.rbDance:
                        movieElements = "24m";
                        break;

                }
            }
        });

        cbHindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isHindiSelected = true;
                    showView(txtHindiActor, spinnerHindiActor);
                } else {
                    isHindiSelected = false;
                    hideView(txtHindiActor, spinnerHindiActor);
                }
            }
        });
        cbTamil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isTamilSelected = true;
                    showView(txtTamilActor, spinnerTamilActor);
                } else {
                    isTamilSelected = false;
                    hideView(txtTamilActor, spinnerTamilActor);
                }
            }
        });
        cbTelugu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isTeluguSelected = true;
                    showView(txtTeluguActor, spinnerTeluguActor);
                } else {
                    isTeluguSelected = false;
                    hideView(txtTeluguActor, spinnerTeluguActor);
                }
            }
        });

        cbMalayalam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isMalayalamSelected = true;
                    showView(txtMalayalamActor, spinnerMalayalamActor);
                } else {
                    isMalayalamSelected = false;
                    hideView(txtMalayalamActor, spinnerMalayalamActor);
                }
            }
        });
        cbEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isEnglishSelected = true;
                    showView(txtEnglishActor, spinnerEnglishActor);
                } else {
                    isEnglishSelected = false;
                    hideView(txtEnglishActor, spinnerEnglishActor);
                }
            }
        });

        rgWatchMultiplex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbMultiplexYes:
                        watchMultiplex = "31a";
                        break;
                    case R.id.rbMultiplexNo:
                        watchMultiplex = "31b";
                        break;
                }
            }
        });
        rgAge.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbAge1:
                        age = "32b1";
                        break;
                    case R.id.rbAge2:
                        age = "32b2";
                        break;
                    case R.id.rbAge3:
                        age = "32b3";
                        break;
                    case R.id.rbAge4:
                        age = "32b4";
                        break;
                    case R.id.rbAge5:
                        age = "32b5";
                        break;
                }
            }
        });

        rgEducation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbIlliterate:
                        education = "32e1";
                        break;
                    case R.id.rbHighSchool:
                        education = "32e2";
                        break;
                    case R.id.rbUnderGraduate:
                        education = "32e3";
                        break;
                    case R.id.rbGraduate:
                        education = "32e4";
                        break;
                    case R.id.rbPostGraduate:
                        education = "32e5";
                        break;
                    case R.id.rbDoctorate:
                        education = "32e6";
                        break;
                }
            }
        });

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbMale:
                        gender = "32c1";
                        break;
                    case R.id.rbFemale:
                        gender = "32c2";
                        break;
                    case R.id.rbOther:
                        gender = "32c3";
                        break;

                }
            }
        });

        rgOccupation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbPartTime:
                        occupation = "32f1";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbFullTime:
                        occupation = "32f2";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbStudent:
                        occupation = "32f3";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbBusiness:
                        occupation = "32f4";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbFormer:
                        occupation = "32f5";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbHouseWife:
                        occupation = "32f6";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbDailyWage:
                        occupation = "32f7";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbUnemployed:
                        occupation = "32f8";
                        hideView(edtoccupationOthers);
                        break;
                    case R.id.rbOthersOccupation:
                        occupation = "Other";
                        showView(edtoccupationOthers);
                        break;
                }
            }
        });

        rgMotherTongue.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbMotherKannada:
                        motherTongue = "32g1";
                        hideView(edtMotherTongue);
                        break;
                    case R.id.rbMotherHindi:
                        motherTongue = "32g2";
                        hideView(edtMotherTongue);
                        break;
                    case R.id.rbMotherTamil:
                        motherTongue = "32g3";
                        hideView(edtMotherTongue);
                        break;
                    case R.id.rbMotherTelugu:
                        motherTongue = "32g4";
                        hideView(edtMotherTongue);
                        break;
                    case R.id.rbMotherMalayalam:
                        motherTongue = "32g5";
                        hideView(edtMotherTongue);
                        break;
                    case R.id.rbmotherMarati:
                        motherTongue = "32g6";
                        hideView(edtMotherTongue);
                        break;
                    case R.id.rbMotherTulu:
                        motherTongue = "32g7";
                        hideView(edtMotherTongue);
                        break;
                    case R.id.rbOthersMotherTongue:
                        motherTongue = "Other";
                        showView(edtMotherTongue);
                        break;
                }
            }
        });

        rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbRich:
                        economicStatus = "32h1";
                        break;
                    case R.id.rbMiddle:
                        economicStatus = "32h2";
                        break;
                    case R.id.rbPoor:
                        economicStatus = "32h3";
                        break;
                }
            }
        });
    }

    private void addValues(String key, String value) {

        try {
            finalObj.put("ps-" + key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeValues(String key) {
        finalObj.remove(key);
    }

    /*delete audio file*/
    private void delete_audio_file() {
        try {
            File newDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + "/compressimage");
            File file = new File(newDir, mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnSave)
    public void save() {


        try {
            finalObj = null;
            finalObj = new JSONObject(part2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (whomWithWatchMovie.isEmpty()) {
            toast(23);
            return;
        } else {
            addValues(whomWithWatchMovie, "yes");
        }

        if (movieElements.isEmpty()) {
            toast(24);
            return;
        } else {
            addValues(movieElements, "yes");
        }

        String languageLike = getLanguageLikes();
        if (!languageLike.isEmpty()) {
            languageMovieLike = languageLike;
        } else {
            toast(25);
            return;
        }

        if (isHindiSelected) {
            if (!hindiActorName.isEmpty()) {
                if (hindiActorName.equals("User input")) {
                    if (!edtHindiActorName.getText().toString().trim().isEmpty()) {
                        // hindiActorName = edtHindiActorName.getText().toString().trim();
                        addValues("26", edtHindiActorName.getText().toString().trim());
                    } else {
                        toast(26);
                        return;
                    }
                } else {
                    addValues("26", hindiActorName);
                }
            } else {
                toast(26);
                return;
            }
        }

        if (isTamilSelected) {
            if (!tamilActorName.isEmpty()) {
                if (tamilActorName.equals("User input")) {
                    if (!edtTamilActorName.getText().toString().trim().isEmpty()) {
                        //  tamilActorName = edtTamilActorName.getText().toString().trim();
                        addValues("27", edtTamilActorName.getText().toString().trim());
                    } else {
                        toast(27);
                        return;
                    }
                } else {
                    addValues("27", tamilActorName);
                }
            } else {
                toast(27);
                return;
            }
        }

        if (isTeluguSelected) {
            if (!teluguActorName.isEmpty()) {
                if (teluguActorName.equals("User input")) {
                    if (!edtTeluguActorName.getText().toString().trim().isEmpty()) {
                        //teluguActorName = edtTeluguActorName.getText().toString().trim();
                        addValues("28", edtTeluguActorName.getText().toString().trim());
                    } else {
                        toast(28);
                        return;
                    }
                } else {
                    addValues("28", teluguActorName);
                }
            } else {
                toast(28);
                return;
            }
        }

        if (isMalayalamSelected) {
            if (!malayalamActorName.isEmpty()) {

                if (malayalamActorName.equals("User input")) {
                    if (!edtMalayalamActorName.getText().toString().trim().isEmpty()) {
                        // malayalamActorName = edtMalayalamActorName.getText().toString().trim();
                        addValues("29", edtMalayalamActorName.getText().toString().trim());
                    } else {
                        toast(29);
                        return;
                    }
                } else {
                    addValues("29", malayalamActorName);
                }
            } else {
                toast(29);
                return;
            }
        }

        if (isEnglishSelected) {
            if (!englishActorName.isEmpty()) {

                if (englishActorName.equals("User input")) {
                    if (!edtEnglishActorName.getText().toString().trim().isEmpty()) {
                        //    englishActorName = edtEnglishActorName.getText().toString().trim();
                        addValues("30", edtEnglishActorName.getText().toString().trim());
                    } else {
                        toast(30);
                        return;
                    }
                } else {
                    addValues("30", englishActorName);
                }
            } else {
                toast(30);
                return;
            }
        }

        if (watchMultiplex.isEmpty()) {
            toast(31);
            return;
        } else {
            addValues(watchMultiplex, "yes");
        }

        if (!edtName.getText().toString().trim().isEmpty()) {
            //name = edtName.getText().toString().trim();
            addValues("32a", edtName.getText().toString().trim());
        } else {
            toast(32);
            return;
        }

        if (age.isEmpty()) {
            toast(32);
            return;
        } else {
            addValues(age, "yes");
        }

        if (gender.isEmpty()) {
            toast(32);
            return;
        } else {
            addValues(gender, "yes");
        }

        if (!edtMobile.getText().toString().trim().isEmpty()) {
            if (edtMobile.getText().toString().trim().length() == 10) {
                mobile = edtMobile.getText().toString().trim();
                addValues("32d", edtMobile.getText().toString().trim());
            }else{
                toast(32);
                return;
            }
        } else {
            toast(32);
            return;
        }

        if (education.isEmpty()) {
            toast(32);
            return;
        } else {
            addValues(education, "yes");
        }
        if (!occupation.isEmpty()) {

            if (occupation.equals("Other")) {
                if (!edtoccupationOthers.getText().toString().trim().isEmpty()) {
                    //occupation = edtoccupationOthers.getText().toString().trim();
                    addValues("32f9", edtoccupationOthers.getText().toString().trim());
                } else {
                    toast(32);
                    return;
                }
            } else {
                addValues(occupation, "yes");
            }
        } else {
            toast(32);
            return;
        }

        if (!motherTongue.isEmpty()) {
            if (motherTongue.equals("Other")) {
                if (!edtMotherTongue.getText().toString().trim().isEmpty()) {
                    motherTongue = edtMotherTongue.getText().toString();
                    addValues("32g8", edtMotherTongue.getText().toString());
                } else {
                    toast(32);
                    return;
                }
            } else {
                addValues(motherTongue, "yes");
            }
        } else {
            toast(32);
            return;
        }

        if (economicStatus.isEmpty()) {
            toast(32);
            return;
        } else {
            addValues(economicStatus, "yes");
        }

        try {/*
            JSONObject object = new JSONObject(part2);
            object.put("whomWithWatchMovie", whomWithWatchMovie);
            object.put("movieElements", movieElements);
            object.put("languageMovieLike", languageMovieLike);
            if (isHindiSelected) {
                object.put("hindiActorName", hindiActorName);
            }
            if (isTamilSelected) {
                object.put("tamilActorName", tamilActorName);
            }
            if (isTeluguSelected) {
                object.put("teluguActorName", teluguActorName);
            }

            if (isMalayalamSelected) {
                object.put("malayalamActorName", malayalamActorName);
            }
            if (isEnglishSelected) {
                object.put("englishActorName", englishActorName);
            }
            object.put("watchMultiplex", watchMultiplex);

            //respondent info
            JSONObject resObj = new JSONObject();
            resObj.put("name", name);
            resObj.put("age", age);
            resObj.put("gender", gender);
            resObj.put("education", education);
            resObj.put("occupation", occupation);
            resObj.put("mobile", mobile);


            resObj.put("motherTongue", motherTongue);
            resObj.put("economicStatus", economicStatus);
            object.put("respondentInfo", resObj.toString());*/
            String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();

            //standard parameter
            finalObj.put("surveyType", "People opinion");
            finalObj.put("userType", "TSP");
            finalObj.put("surveyDate", new Date().toString());
            finalObj.put("projectId", sharedPreferenceManager.get_project_id());
            finalObj.put("partyWorker", sharedPreferenceManager.get_user_id());

            String latitude = gpsTracker.getLatitude() + "";
            String longitude = gpsTracker.getLongitude() + "";

            finalObj.put("latitude", latitude);
            finalObj.put("longitude", longitude);
            // object.put("booth_name", booth_name);
            finalObj.put("audioFileName", mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));

            finalObj.put("surveyid", surveyId);

            if (!db.check_location(latitude, longitude)) {
                try {
                    finalObj.put("duplicate_survey", "no");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                try {
                    finalObj.put("duplicate_survey", "yes");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }


            Log.w(TAG, "Value :" + finalObj.toString());

            if (!db.check_mobile_number_exist(mobile)) {
                VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", finalObj.toString(), get_current_date(), false, surveyId);
                db.insert_voter_attribute(attribute);

                Intent i = new Intent(this, RecordService.class);
                stopService(i);
                //  votersDatabase.update_survey(attribute.getVoterCardNumber());
                db.insert_mobile_number(mobile, latitude, longitude);
                Toastmsg(TheatrePeopleOpinionActivity3.this, successfullyAdded);
                startActivity(new Intent(TheatrePeopleOpinionActivity3.this, OpinionPollActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                TheatrePeopleOpinionActivity3.this.finish();

            } else {
                Toastmsg(TheatrePeopleOpinionActivity3.this, mobilNumberExist);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String TAG = "opinion3";
    @BindString(R.string.mobileNumberExist)
    String mobilNumberExist;
    @BindString(R.string.successfullyAdded)
    String successfullyAdded;
    @BindString(R.string.validMobile)
    String validMobile;

    private String getLanguageLikes() {
        String languageLikes = "";

        if (cbKannada.isChecked()) {
            addValues("25a", "yes");
            languageLikes += "," + "Kannada";
        } else {
            removeValues("25a");
        }

        if (cbTamil.isChecked()) {
            addValues("25b", "yes");
            languageLikes += "," + "Tamil";
        } else {
            removeValues("25b");
        }
        if (cbTelugu.isChecked()) {
            addValues("25c", "yes");
            languageLikes += "," + "Telugu";
        } else {
            removeValues("25c");
        }
        if (cbMalayalam.isChecked()) {
            addValues("25d", "yes");
            languageLikes += "," + "Malayalam";
        } else {
            removeValues("25d");
        }
        if (cbEnglish.isChecked()) {
            addValues("25e", "yes");
            languageLikes += "," + "English";
        } else {
            removeValues("25e");
        }

        return languageLikes;
    }

    private void toast(int qus) {
        Toastmsg(TheatrePeopleOpinionActivity3.this, "Please answer question :" + qus);
    }

    private void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    private void showView(View v1, View v2) {
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.VISIBLE);
    }

    private void hideView(View v1, View v2) {
        v1.setVisibility(View.GONE);
        v2.setVisibility(View.GONE);
    }

    @BindView(R.id.rgAge)
    RadioGroup rgAge;
    @BindView(R.id.rgStatus)
    RadioGroup rgStatus;
    @BindView(R.id.rgMotherTongue)
    RadioGroup rgMotherTongue;
    @BindView(R.id.edtMotherTongue)
    EditText edtMotherTongue;
    @BindView(R.id.rgOccupation)
    RadioGroup rgOccupation;
    @BindView(R.id.edtOccupationOthers)
    EditText edtoccupationOthers;
    @BindView(R.id.rgEducation)
    RadioGroup rgEducation;
    @BindView(R.id.rgGender)
    RadioGroup rgGender;
    @BindView(R.id.edtMobile)
    EditText edtMobile;
    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.rgWatchInMultiplex)
    RadioGroup rgWatchMultiplex;
    @BindView(R.id.txtTamilActor)
    TextView txtTamilActor;
    @BindView(R.id.spinnerTamilActor)
    Spinner spinnerTamilActor;
    @BindView(R.id.txtTeluguActor)
    TextView txtTeluguActor;
    @BindView(R.id.spinnerTeluguActor)
    Spinner spinnerTeluguActor;
    @BindView(R.id.txtMalayalamActor)
    TextView txtMalayalamActor;
    @BindView(R.id.spinnerMalayalamActor)
    Spinner spinnerMalayalamActor;
    @BindView(R.id.txtEnglishActor)
    TextView txtEnglishActor;
    @BindView(R.id.spinnerEnglishActor)
    Spinner spinnerEnglishActor;
    @BindView(R.id.txthindiActor)
    TextView txtHindiActor;
    @BindView(R.id.spinnerHindiActor)
    Spinner spinnerHindiActor;
    @BindView(R.id.cbKannada)
    CheckBox cbKannada;
    @BindView(R.id.cbHindi)
    CheckBox cbHindi;
    @BindView(R.id.cbTamil)
    CheckBox cbTamil;
    @BindView(R.id.cbTelugu)
    CheckBox cbTelugu;
    @BindView(R.id.cbMalayalam)
    CheckBox cbMalayalam;
    @BindView(R.id.cbEnglish)
    CheckBox cbEnglish;
    @BindView(R.id.rgMovieElements)
    RadioGroup rgMovieElements;
    @BindView(R.id.rgMovieWithWhom)
    RadioGroup rgMovieWithWhom;
}
