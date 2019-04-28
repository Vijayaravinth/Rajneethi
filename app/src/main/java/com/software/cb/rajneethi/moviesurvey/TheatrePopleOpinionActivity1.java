package com.software.cb.rajneethi.moviesurvey;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.services.RecordService;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
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
 * Created by DELL on 31-12-2017.
 */

public class TheatrePopleOpinionActivity1 extends Util {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.peopleOpinion)
    String title;

    @BindArray(R.array.actorName)
    String[] actorName;

    @BindArray(R.array.actressName)
    String[] actress;

    @BindArray(R.array.comedian)
    String[] comedian;
    @BindArray(R.array.director)
    String[] director;
    @BindView(R.id.edtActorName)
    EditText edtActorName;
    @BindView(R.id.edtActressName)
    EditText edtActressName;
    @BindView(R.id.edtComedianName)
    EditText edtComedianName;
    @BindView(R.id.edtDirectorName)
    EditText edtDirectorName;

    String watchMovies = "", screen = "", preferClass = "", affortablePrice = "", maximumSpendMoney = "",
            favouritePresentActor = "", genreOfMovies = "", fan = "", actressName = "", comedianName = "", directorName = "",
            impressRole = "";
    String reason = "", singleScreen = "", multiplex = "", singleScreePrice = "",
            multiScreenPrice = "", maxSingleScreen = "", maxMultiplex = "";

    JSONObject finalObj = new JSONObject();

    File newDir;
    public String mFileName = null;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theatre_people_opinion1);
        ButterKnife.bind(this);


        setup_toolbar(toolbar);

        hideQusLayout();
        setRadioGroupListener();
        setAdapterForSpinner();

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/";

        mFileName += "AUD-" + sharedPreferenceManager.get_username() + "_" + "peopleSurvey" + "_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";


        i = new Intent(this, RecordService.class);
        i.putExtra("fileName", mFileName);
        startService(i);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* stopService(i);
        delete_audio_file();*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(i);
        delete_audio_file();
    }

    /*delete audio file*/
    private void delete_audio_file() {
        try {
            File file = new File(newDir, mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapterForSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, actorName);
        spinnerActor.setAdapter(spinnerArrayAdapter);
        spinnerActor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    edtActorName.setVisibility(View.GONE);
                    favouritePresentActor = spinnerActor.getSelectedItem().toString();
                } else {
                    edtActorName.setVisibility(View.GONE);
                    favouritePresentActor = "";
                }

                if (spinnerActor.getSelectedItem().toString().equals("User input")) {
                    edtActorName.setVisibility(View.VISIBLE);
                    edtActorName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<String> actressAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, actress);
        spinnerActress.setAdapter(actressAdapter);
        spinnerActress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    actressName = spinnerActress.getSelectedItem().toString();
                    edtActressName.setVisibility(View.GONE);
                } else {
                    actressName = "";
                    edtActressName.setVisibility(View.GONE);
                }
                if (spinnerActress.getSelectedItem().toString().equals("User input")) {
                    edtActressName.setVisibility(View.VISIBLE);
                    edtActressName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> comedianAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, comedian);
        spinnerComedian.setAdapter(comedianAdapter);
        spinnerComedian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    comedianName = spinnerComedian.getSelectedItem().toString();
                    edtComedianName.setVisibility(View.GONE);
                } else {
                    comedianName = "";
                    edtComedianName.setVisibility(View.GONE);
                }

                if (spinnerComedian.getSelectedItem().toString().equals("User input")) {
                    edtComedianName.setVisibility(View.VISIBLE);
                    edtComedianName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<String> directorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, director);
        spinnerDirector.setAdapter(directorAdapter);
        spinnerDirector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    directorName = spinnerDirector.getSelectedItem().toString();
                    edtDirectorName.setVisibility(View.GONE);
                } else {
                    directorName = "";
                    edtDirectorName.setVisibility(View.GONE);
                }

                if (spinnerDirector.getSelectedItem().toString().equals("User input")) {
                    edtDirectorName.setVisibility(View.VISIBLE);
                    edtDirectorName.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRadioGroupListener() {

        rgWatchMovie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbNever:
                        watchMovies = "1a";
                        Toastmsg(TheatrePopleOpinionActivity1.this, "Please tell us the reason");
                        showView(rgNever);
                        hideQusLayout();
                        break;
                    case R.id.rbOccasionally:
                        watchMovies = "1b";
                        hideView(rgNever);
                        showQuslayout();
                        break;
                    case R.id.rbOnceEvery3Month:
                        watchMovies = "1c";
                        hideView(rgNever);
                        showQuslayout();
                        break;
                    case R.id.rbOnceEvery2Month:
                        watchMovies = "1d";
                        hideView(rgNever);
                        showQuslayout();
                        break;
                    case R.id.rbOnceaMonth:
                        watchMovies = "1e";
                        hideView(rgNever);
                        showQuslayout();
                        break;
                    case R.id.rbOnceEvery2Week:
                        watchMovies = "1f";
                        hideView(rgNever);
                        showQuslayout();
                        break;
                    case R.id.rbOnceaWeek:
                        watchMovies = "1g";
                        hideView(rgNever);
                        showQuslayout();
                        break;
                }
            }
        });

        rgNever.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbAffordability:
                        reason = "Never";
                        goBack();
                        break;
                    case R.id.rbNoInterest:
                        reason = "Occasionally";
                        goBack();
                        break;
                    case R.id.rbAwareness:
                        reason = "Once every 3 months";
                        goBack();
                        break;
                    case R.id.rbLackOfTime:
                        reason = "Once every 2 months";
                        goBack();
                        break;
                    case R.id.rbQualityMovie:
                        reason = "Once a month";
                        goBack();
                        break;
                    case R.id.rbQualityTheatre:
                        reason = "Once every 2 weeks";
                        goBack();
                        break;
                }
            }
        });

        rgScreen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.rbSingleScreen:
                        screen = "2a";
                        hideView(edtQus2);
                        break;
                    case R.id.rbMultiplex:
                        screen = "2b";
                        hideView(edtQus2);
                        break;
                    case R.id.rbImax:
                        screen = "2c";
                        hideView(edtQus2);
                        break;
                    case R.id.rbOthers:
                        screen = "Other";
                        showView(edtQus2);
                        break;
                }
            }
        });

        rgSingleScreenTheatre.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbBalcony:
                        singleScreen = "3a1";
                        break;
                    case R.id.rbFirstClass:
                        singleScreen = "3a2";
                        break;
                    case R.id.rbSecondClass:
                        singleScreen = "3a3";
                        break;

                }
            }
        });

        rgMultiplex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbNormal:
                        multiplex = "3ba";
                        break;
                    case R.id.rbSilver:
                        multiplex = "3b2";
                        break;
                    case R.id.rbGold:
                        multiplex = "3b3";
                        break;
                    case R.id.rbPlatinum:
                        multiplex = "3b4";
                        break;
                }
            }
        });

        rgSingleScreenPrice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb100:
                        maxSingleScreen = "5a1";
                        break;
                    case R.id.rb160:
                        maxSingleScreen = "5a2";
                        break;
                    case R.id.rb210:
                        maxSingleScreen = "5a3";
                        break;
                    case R.id.rb260:
                        maxSingleScreen = "5a4";
                        break;
                    case R.id.rb300:
                        maxSingleScreen = "5a5";
                        break;
                }
            }
        });

        rgMultiplexPrice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb200:
                        maxMultiplex = "5b1";
                        break;
                    case R.id.rb310:
                        maxMultiplex = "5b2";
                        break;
                    case R.id.rb410:
                        maxMultiplex = "5b3";
                        break;
                    case R.id.rb500:
                        maxMultiplex = "5b4";
                        break;

                }
            }
        });

        rgFan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbHardCore:
                        fan = "Hardcore Fan";
                        break;
                    case R.id.rbFan:
                        fan = "Fan";
                        break;
                    case R.id.rbFollower:
                        fan = "Follower";
                        break;
                    case R.id.rbJustFan:
                        fan = "Just like to watch his movies";
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


    @OnClick(R.id.txtNext)
    public void save() {

        finalObj = new JSONObject();

        if (watchMovies.isEmpty()) {
            toast(1);
            return;
        } else {
            addValues(watchMovies, "yes");
        }

        if (!screen.isEmpty()) {

            if (screen.equals("Other")) {
                if (!edtQus2.getText().toString().trim().isEmpty()) {
                    // screen = edtQus2.getText().toString().trim();
                    addValues("2d", edtQus2.getText().toString().trim());
                } else {
                    toast(2);
                    return;
                }
            } else {
                addValues(screen, "yes");
            }
        } else {
            toast(2);
            return;
        }

        if (singleScreen.isEmpty()) {
            toast(3);
            return;
        }
        if (multiplex.isEmpty()) {
            toast(3);
            return;
        }

        addValues(singleScreen, "yes");
        addValues(multiplex, "yes");

        //  preferClass = "Single Screen :" + singleScreen + ", Multiplex :" + multiplex;

        //question4


        if (!(edtBalcony.getText().toString().trim().isEmpty() || edtFirstClass.getText().toString().trim().isEmpty() || edtSecondClass.getText().toString().trim().isEmpty())) {

            addValues("4a1", edtBalcony.getText().toString().trim());
            addValues("4a2", edtFirstClass.getText().toString().trim());
            addValues("4a3", edtSecondClass.getText().toString().trim());
           /* JSONObject object = new JSONObject();
            try {
                object.put("balconyPrice", edtBalcony.getText().toString().trim());
                object.put("firstClassPrice", edtFirstClass.getText().toString().trim());
                object.put("secondClassPrice", edtSecondClass.getText().toString().trim());
                singleScreePrice = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            toast(4);
            return;
        }

        if (!(edtNormal.getText().toString().trim().isEmpty() || edtSilver.getText().toString().trim().isEmpty() ||
                edtGold.getText().toString().trim().isEmpty() || edtPlatinum.getText().toString().trim().isEmpty())) {

            addValues("4b1", edtNormal.getText().toString().trim());
            addValues("4b2", edtSilver.getText().toString().trim());
            addValues("4b3", edtGold.getText().toString().trim());
            addValues("4b4", edtPlatinum.getText().toString().trim());

          /*  JSONObject object = new JSONObject();
            try {
                object.put("normal", edtNormal.getText().toString().trim());
                object.put("silver", edtSilver.getText().toString().trim());
                object.put("gold", edtGold.getText().toString().trim());
                object.put("platinum", edtPlatinum.getText().toString().trim());
                multiScreenPrice = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            toast(4);
            return;
        }

        //  affortablePrice = singleScreePrice + "," + multiScreenPrice;

        //question5
        if (maxSingleScreen.isEmpty()) {
            toast(5);
            return;
        }
        if (maxMultiplex.isEmpty()) {
            toast(5);
            return;
        }

        addValues(maxSingleScreen, "yes");
        addValues(maxMultiplex, "yes");

        //  maximumSpendMoney = "Single Screen :" + maxSingleScreen + ", Multiplex :" + maxMultiplex;

        //question 6
        if (!favouritePresentActor.isEmpty()) {
            if (favouritePresentActor.equals("User input")) {
                if (!edtActorName.getText().toString().trim().isEmpty()) {
                    //favouritePresentActor = edtActorName.getText().toString().trim();
                    addValues("6", edtActorName.getText().toString().trim());
                } else {
                    toast(6);
                    return;
                }
            } else {
                addValues("6", favouritePresentActor);
            }
        } else {
            toast(6);
            return;
        }

        //question 7
        if (fan.isEmpty()) {
            toast(7);
            return;
        } else {
            addValues("7", fan);
        }

        //question8
        String genre = getGenreOfMovies();
        if (!genre.isEmpty()) {
            genreOfMovies = genre;
        } else {
            toast(8);
            return;
        }


        //question 9
        if (!actressName.isEmpty()) {
            if (actressName.equals("User input")) {
                if (!edtActressName.getText().toString().trim().isEmpty()) {
                    //actressName = edtActressName.getText().toString();
                    addValues("9", edtActressName.getText().toString());
                } else {
                    toast(9);
                    return;
                }
            } else {
                addValues("9", actressName);
            }
        } else {
            toast(9);
            return;
        }

        //question 10
        if (!comedianName.isEmpty()) {
            if (comedianName.equals("User input")) {
                if (!edtComedianName.getText().toString().trim().isEmpty()) {
                    //comedianName = edtComedianName.getText().toString();
                    addValues("10", edtComedianName.getText().toString());
                } else {
                    toast(10);
                    return;
                }
            } else {
                addValues("10", comedianName);
            }
        } else {
            toast(10);
            return;
        }
        //question 11
        if (!directorName.isEmpty()) {
            if (directorName.equals("User input")) {
                if (!edtDirectorName.getText().toString().trim().isEmpty()) {
                    //  directorName = edtDirectorName.getText().toString().trim();
                    addValues("11", edtDirectorName.getText().toString().trim());
                } else {
                    toast(11);
                    return;
                }
            } else {
                addValues("11", directorName);
            }
        } else {
            toast(11);
            return;
        }

        //question 12
        String role = getImpressRole();
        if (!role.isEmpty()) {
            impressRole = role;
        } else {
            toast(12);
            return;
        }

        Log.w(TAG, "Values :" + finalObj.toString());
        startActivity(new Intent(TheatrePopleOpinionActivity1.this, TheatrePeopleOpinionActivity2.class).putExtra("fileName", mFileName).putExtra("part1", finalObj.toString()));

    }


    private String TAG = "Opinion 1";

    private String getImpressRole() {
        String role = "";
        if (cbHero.isChecked()) {

            addValues("12a", "yes");
            role += "," + "Hero";

        } else {
            removeValues("12a");
        }

        if (cbHeroin.isChecked()) {

            role += "," + "Heroin";
            addValues("12b", "yes");
        } else {
            removeValues("12b");
        }
        if (cbComedian.isChecked()) {

            role += "," + "Comedian";
            addValues("12c", "yes");
        } else {
            removeValues("12c");
        }

        if (cbSupport.isChecked()) {

            role += "," + "Supporting character";
            addValues("12d", "yes");
        } else {
            removeValues("12d");
        }

        if (cbVillian.isChecked()) {

            role += "," + "Villain";
            addValues("12e", "yes");
        } else {
            removeValues("12e");
        }

        return role;
    }

    private String getGenreOfMovies() {
        String genre = "";
        if (cbFamily.isChecked()) {

            genre += "," + "Family";
            addValues("8a", "yes");
        } else {
            removeValues("8a");
        }

        if (cbAction.isChecked()) {

            genre += "," + "Action/Fight";
            addValues("8b", "yes");
        } else {
            removeValues("8b");
        }
        if (cbAdult.isChecked()) {
            addValues("8c", "yes");
            genre += "," + "Adult";

        } else {
            removeValues("8c");
        }
        if (cbDrama.isChecked()) {

            genre += "," + "Drama";
            addValues("8d", "yes");
        } else {
            removeValues("8d");
        }
        if (cbThriller.isChecked()) {

            genre += "," + "Thriller / Suspense";
            addValues("8e", "yes");
        } else {
            removeValues("8e");
        }

        if (cbAnimation.isChecked()) {
            genre += "," + "Animation";
            addValues("8f", "yes");
        } else {
            removeValues("8f");
        }

        if (cbHorror.isChecked()) {
            genre += "," + "Horror";
            addValues("8g", "yes");
        } else {
            removeValues("8g");
        }

        if (cbRomantic.isChecked()) {

            genre += "," + "Romantic / Love";
            addValues("8h", "yes");
        } else {
            removeValues("8h");
        }

        if (cbComedy.isChecked()) {
            genre += "," + "Comedy";
            addValues("8i", "yes");
        } else {
            removeValues("7i");
        }
        if (cbMythology.isChecked()) {

            genre += "," + "Mythological";
            addValues("8j", "yes");
        } else {
            removeValues("8j");
        }

        if (cbArt.isChecked()) {
            genre += "," + "Art Cinema";
            addValues("8k", "yes");
        } else {
            removeValues("8k");
        }

        return genre;

    }

    private void goBack() {

        Toastmsg(TheatrePopleOpinionActivity1.this, "Survey Done! . Thank you");
        TheatrePopleOpinionActivity1.super.onBackPressed();
        TheatrePopleOpinionActivity1.this.finish();
    }

    private void hideQusLayout() {
        hideView(qusLayout);
        hideView(txtNext);
    }

    private void showQuslayout() {
        showView(qusLayout);
        showView(txtNext);
    }

    private void toast(int qus) {
        Toastmsg(TheatrePopleOpinionActivity1.this, "Please answer question :" + qus);
    }

    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    private void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }


    @BindView(R.id.cbHero)
    CheckBox cbHero;
    @BindView(R.id.cbHeroin)
    CheckBox cbHeroin;
    @BindView(R.id.cbComedian)
    CheckBox cbComedian;
    @BindView(R.id.cbSupportCharacter)
    CheckBox cbSupport;
    @BindView(R.id.cbVillain)
    CheckBox cbVillian;
    @BindView(R.id.txtNext)
    TextView txtNext;
    @BindView(R.id.spinnerDirector)
    Spinner spinnerDirector;
    @BindView(R.id.spinnerComedian)
    Spinner spinnerComedian;
    @BindView(R.id.spinnerActress)
    Spinner spinnerActress;
    @BindView(R.id.rgFan)
    RadioGroup rgFan;
    @BindView(R.id.cbFamily)
    CheckBox cbFamily;
    @BindView(R.id.cbAction)
    CheckBox cbAction;
    @BindView(R.id.cbAdult)
    CheckBox cbAdult;
    @BindView(R.id.cbDrama)
    CheckBox cbDrama;
    @BindView(R.id.cbThriller)
    CheckBox cbThriller;
    @BindView(R.id.cbAnimation)
    CheckBox cbAnimation;
    @BindView(R.id.cbHorror)
    CheckBox cbHorror;
    @BindView(R.id.cbRomantic)
    CheckBox cbRomantic;
    @BindView(R.id.cbComedy)
    CheckBox cbComedy;
    @BindView(R.id.cbMythology)
    CheckBox cbMythology;
    @BindView(R.id.cbArt)
    CheckBox cbArt;
    @BindView(R.id.spinnerActor)
    Spinner spinnerActor;
    @BindView(R.id.rgSingleScreenPrice)
    RadioGroup rgSingleScreenPrice;
    @BindView(R.id.rgMultiplexPrice)
    RadioGroup rgMultiplexPrice;
    @BindView(R.id.edtNormal)
    EditText edtNormal;
    @BindView(R.id.edtSilver)
    EditText edtSilver;
    @BindView(R.id.edtGold)
    EditText edtGold;
    @BindView(R.id.edtPlatinum)
    EditText edtPlatinum;
    @BindView(R.id.edtBalcony)
    EditText edtBalcony;
    @BindView(R.id.edtFirstClass)
    EditText edtFirstClass;
    @BindView(R.id.edtSecondClass)
    EditText edtSecondClass;
    @BindView(R.id.rgSingleScreenTheatre)
    RadioGroup rgSingleScreenTheatre;
    @BindView(R.id.rgMultipex)
    RadioGroup rgMultiplex;
    @BindView(R.id.rgScreen)
    RadioGroup rgScreen;
    @BindView(R.id.edtQus2)
    EditText edtQus2;
    @BindView(R.id.qusLayout)
    LinearLayout qusLayout;
    @BindView(R.id.rgWatchMovie)
    RadioGroup rgWatchMovie;
    @BindView(R.id.rgNever)
    RadioGroup rgNever;
}
