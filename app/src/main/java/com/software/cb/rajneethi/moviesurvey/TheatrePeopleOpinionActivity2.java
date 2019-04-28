package com.software.cb.rajneethi.moviesurvey;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.services.RecordService;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DELL on 02-01-2018.
 */

public class TheatrePeopleOpinionActivity2 extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.peopleOpinion)
    String title;

    @BindArray(R.array.banner)
    String[] banner;
    @BindView(R.id.edtBanner)
    EditText edtBanner;
    @BindView(R.id.spinnerMonday)
    Spinner spinnerMonday;
    @BindView(R.id.spinnerTuesday)
    Spinner spinnerTuesday;
    @BindView(R.id.spinnerWednesday)
    Spinner spinnerWednesday;
    @BindView(R.id.spinnerThursday)
    Spinner spinnerThursday;
    @BindView(R.id.spinnerFriday)
    Spinner spinnerFriday;
    @BindView(R.id.spinnerSaturday)
    Spinner spinnerSaturday;
    @BindView(R.id.spinnerSunday)
    Spinner spinnerSunday;

    @BindArray(R.array.showTime)
    String[] showTime;

    String genreOfMoviesLike = "", influencePreference = "", productionBanner = "", generallyWatchMovie = "", smartPhone = "", pricePay = "", recommendTheatre = "", supportDubMovie = "", showConvenient = "", hearAboutMovies = "";

    String monday = "", tuesday = "", thursday = "", wednesday = "", friday = "", saturday = "", sunday = "";
    boolean isAnyShow = false, newMovieAdditional = false;

    String part1;

    JSONObject finalObj = new JSONObject();

    String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theatre_people_opinion2);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);

        if (getIntent().getExtras() != null) {
            part1 = getIntent().getExtras().getString("part1");
            mFileName = getIntent().getExtras().getString("fileName");
        }

        setRadioGroupListener();
        setAdapterForSpinner();
        Log.w(TAG, "is service running :" + isMyServiceRunning(RecordService.class));
    }

    private void setAdapterForSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, banner);
        spinnerBanner.setAdapter(spinnerArrayAdapter);

        spinnerBanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    productionBanner = spinnerBanner.getSelectedItem().toString();
                    edtBanner.setVisibility(View.GONE);
                } else {
                    productionBanner = "";
                    edtBanner.setVisibility(View.GONE);
                }

                if (spinnerBanner.getSelectedItem().toString().equals("User input")) {
                    edtBanner.setVisibility(View.VISIBLE);
                    edtBanner.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerMonday.setAdapter(adapter());
        spinnerThursday.setAdapter(adapter());
        spinnerTuesday.setAdapter(adapter());
        spinnerWednesday.setAdapter(adapter());
        spinnerFriday.setAdapter(adapter());
        spinnerSaturday.setAdapter(adapter());
        spinnerSunday.setAdapter(adapter());

        spinnerMonday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    monday = spinnerMonday.getSelectedItem().toString().trim();
                } else {
                    monday = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerTuesday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    tuesday = spinnerTuesday.getSelectedItem().toString().trim();
                } else {
                    tuesday = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerWednesday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    wednesday = spinnerWednesday.getSelectedItem().toString().trim();
                } else {
                    wednesday = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerThursday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    thursday = spinnerThursday.getSelectedItem().toString().trim();
                } else {
                    thursday = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerFriday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    friday = spinnerFriday.getSelectedItem().toString().trim();
                } else {
                    friday = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerSaturday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    saturday = spinnerSaturday.getSelectedItem().toString().trim();
                } else {
                    saturday = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSunday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    sunday = spinnerSunday.getSelectedItem().toString().trim();
                } else {
                    sunday = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private ArrayAdapter<String> adapter() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, showTime);
        return spinnerArrayAdapter;
    }

    private void setRadioGroupListener() {

        rgGenerallyWatchMovie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbPiracy:
                        generallyWatchMovie = "16a";
                        break;
                    case R.id.rbTheatre:
                        generallyWatchMovie = "16b";
                        break;
                    case R.id.rbDvd:
                        generallyWatchMovie = "16c";
                        break;
                    case R.id.rbPaidInternet:
                        generallyWatchMovie = "16d";
                        break;
                    case R.id.rbTv:
                        generallyWatchMovie = "16e";
                        break;
                    case R.id.rbOthers:
                        generallyWatchMovie = "Other";
                        break;
                }
            }
        });

        rgSmartPhone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbSmartPhone:
                        smartPhone = "17a";
                        break;
                    case R.id.rbSmartPhoneInternet:
                        smartPhone = "17b";
                        break;
                    case R.id.rbNoSmartPhone:
                        smartPhone = "17c";
                        break;
                }
            }
        });

        rgMorePrice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbPriceYes:
                        pricePay = "18a";
                        break;
                    case R.id.rbPriceNo:
                        pricePay = "18b";
                        break;

                }
            }
        });

        rgSupportKannadaMovie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbKannadaYes:
                        supportDubMovie = "20a";
                        break;
                    case R.id.rbKannadaNo:
                        supportDubMovie = "20b";
                        break;

                }
            }
        });

        rbAnyShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAnyShow = b;
            }
        });

        rgAboutNewMovie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbMoviePoster:
                        hearAboutMovies = "22a";
                        hideView(edtAboutNewMovie);
                        newMovieAdditional = false;
                        break;
                    case R.id.rbNewsPaper:
                        hearAboutMovies = "22b";
                        showView(edtAboutNewMovie);
                        edtAboutNewMovie.requestFocus();
                        newMovieAdditional = true;
                        break;
                    case R.id.rbMovieTv:
                        hearAboutMovies = "22c";
                        newMovieAdditional = true;
                        showView(edtAboutNewMovie);
                        edtAboutNewMovie.requestFocus();
                        break;
                    case R.id.rbSocialMedia:
                        hearAboutMovies = "22d";
                        hideView(edtAboutNewMovie);
                        newMovieAdditional = false;
                        break;
                    case R.id.rbRadio:
                        hearAboutMovies = "22e";
                        newMovieAdditional = true;
                        showView(edtAboutNewMovie);
                        edtAboutNewMovie.requestFocus();
                        break;
                }
            }
        });
    }

    @OnClick(R.id.txtNext)
    public void save() {


        finalObj = null;
        try {
            finalObj = new JSONObject(part1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String genre = getGenreOfMovies();
        if (genre.isEmpty()) {
            toast(13);
            return;
        }

        String influence = getInfluencePreference();
        if (influence.isEmpty()) {
            toast(14);
            return;
        }

        if (!productionBanner.isEmpty()) {
            if (productionBanner.equals("User input")) {
                if (!edtBanner.getText().toString().trim().isEmpty()) {
                    //  productionBanner = edtBanner.getText().toString().trim();
                    addValues("15", edtBanner.getText().toString().trim());
                } else {
                    toast(15);
                    return;
                }
            } else {
                addValues("15", productionBanner);
            }
        } else {
            toast(15);
            return;
        }

        //question 16
        if (!generallyWatchMovie.isEmpty()) {
            if (generallyWatchMovie.equals("Other")) {
                if (!edtGenerallyWatchMovie.getText().toString().trim().isEmpty()) {
                    // generallyWatchMovie = edtGenerallyWatchMovie.getText().toString().trim();
                    addValues("16f", edtGenerallyWatchMovie.getText().toString().trim());
                } else {
                    toast(16);
                    return;
                }
            } else {
                addValues(generallyWatchMovie, "yes");
            }
        } else {
            toast(16);
            return;
        }

        //question 17
        if (smartPhone.isEmpty()) {
            toast(17);
            return;
        } else {
            addValues(smartPhone, "yes");
        }
        //question 18
        if (pricePay.isEmpty()) {
            toast(18);
            return;
        } else {
            addValues(pricePay, "yes");
        }
        //question 19
        if (!edtRecommentTheatre.getText().toString().trim().isEmpty()) {
            // recommendTheatre = edtRecommentTheatre.getText().toString().trim();
            addValues("19", edtRecommentTheatre.getText().toString().trim());
        } else {
            toast(19);
            return;
        }

        //question 20
        if (supportDubMovie.isEmpty()) {
            toast(20);
            return;
        } else {
            addValues(supportDubMovie, "yes");
        }

        //question 21
        if (isAnyShow) {
            // showConvenient = "Any day, any Show";
            addValues("21f", "yes");
        } else {
            if (!(sunday.isEmpty() || monday.isEmpty() || tuesday.isEmpty() ||
                    wednesday.isEmpty() || thursday.isEmpty() || friday.isEmpty() ||
                    saturday.isEmpty())) {

                addValues("21a", monday);
                addValues("21b", tuesday);
                addValues("21c", wednesday);
                addValues("21d", thursday);
                addValues("21e", friday);
                addValues("21f", saturday);
                addValues("21g", sunday);

              /*  try {
                    JSONObject object = new JSONObject();
                    object.put("monday", monday);
                    object.put("tuesday", tuesday);
                    object.put("wednesday", wednesday);
                    object.put("thursday", thursday);
                    object.put("friday", friday);
                    object.put("saturday", saturday);
                    object.put("sunday", sunday);

                    showConvenient = object.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
*/
            } else {
                toast(21);
                return;
            }
        }

        //question 22
        if (!hearAboutMovies.isEmpty()) {
            if (newMovieAdditional) {
                if (!edtAboutNewMovie.getText().toString().trim().isEmpty()) {
                    //hearAboutMovies += ", Details :" + edtAboutNewMovie.getText().toString().trim();
                    addValues(hearAboutMovies, edtAboutNewMovie.getText().toString().trim());
                } else {
                    toast(22);
                    return;
                }
            } else {
                addValues(hearAboutMovies, "yes");
            }
        } else {
            toast(22);
            return;
        }
        Log.w(TAG, "value :" + finalObj.toString());

        startActivity(new Intent(TheatrePeopleOpinionActivity2.this, TheatrePeopleOpinionActivity3.class).putExtra("fileName", mFileName).putExtra("part2", finalObj.toString()));

        /*try {
            JSONObject object = new JSONObject(part1);
            //  object.put("genreOfMoviesLike", genreOfMoviesLike);
            //  object.put("influencePreference", influencePreference);
            // object.put("productionBanner", productionBanner);
            //object.put("generallywatchMovie", generallyWatchMovie);
            // object.put("smartPhone", smartPhone);
            // object.put("pricePay", pricePay);
          //  object.put("recommendTheatre", recommendTheatre);
          //  object.put("supportDubMovie", supportDubMovie);
            object.put("showConvenient", showConvenient);
            object.put("hearAboutMovies", hearAboutMovies);
} catch (JSONException e) {
            e.printStackTrace();
        }
*/
       /* genreOfMoviesLike="" , influencePreference="" , productionBanner="",generallyWatchMovie = "", smartPhone=""
                ,pricePay="", recommendTheatre="",supportDubMovie="", showConvenient="", hearAboutMovies=""*/
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.w("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.w(TAG, "on back pressed");
    }

    private String TAG = "Opinion 2";

    private String getInfluencePreference() {
        String influence = "";
        if (cbFriends.isChecked()) {
            addValues("14a", "yes");
            influence += "," + "Family / Friends / Children";
        } else {
            removeValues("14a");
        }

        if (cbFavouriteActor.isChecked()) {
            addValues("14b", "yes");
            influence += "," + "Favourite Actor";
        } else {
            removeValues("14b");
        }

        if (cbDirector.isChecked()) {
            addValues("14c", "yes");
            influence += "," + "Director";
        } else {
            removeValues("14c");
        }

        if (cbProducer.isChecked()) {
            addValues("14d", "yes");
            influence += "," + "Producer";
        } else {
            removeValues("14d");
        }

        if (cbReviews.isChecked()) {
            addValues("14e", "yes");
            influence += "," + "Reviews";
        } else {
            removeValues("14e");
        }

        if (cbFavouriteActress.isChecked()) {
            addValues("14f", "yes");
            influence += "," + "Favourite Actress";
        } else {
            removeValues("14f");
        }

        if (cbHype.isChecked()) {
            addValues("14g", "yes");
            influence += "," + "Hype Created";
        } else {
            removeValues("14g");
        }

        if (cbSocialMedia.isChecked()) {

            addValues("14h", "yes");
            influence += "," + "Social Media (Fb,Youtube)";
        } else {
            removeValues("14h");
        }

        if (cbPoster.isChecked()) {
            addValues("14i", "yes");
            influence += "," + "Poster";
        } else {
            removeValues("14i");
        }

        if (cbTrailer.isChecked()) {
            addValues("14j", "yes");
            influence += "," + "Trailer";
        } else {
            removeValues("14j");
        }

        if (cbMusic.isChecked()) {
            addValues("14k", "yes");
            influence += "," + "Music";
        } else {
            removeValues("14k");
        }


        return influence;
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

    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    private void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    private void toast(int qus) {
        Toastmsg(TheatrePeopleOpinionActivity2.this, "Please answer question :" + qus);
    }

    private String getGenreOfMovies() {
        String genre = "";
        if (cbFamily.isChecked()) {
            addValues("13a", "yes");
            genre += "," + "Family";
        } else {
            removeValues("13a");
        }

        if (cbAction.isChecked()) {
            genre += "," + "Action/Fight";
            addValues("13b", "yes");
        } else {
            removeValues("13b");
        }
        if (cbAdult.isChecked()) {
            genre += "," + "Adult";
            addValues("13c", "yes");
        } else {

            removeValues("13c");
        }
        if (cbDrama.isChecked()) {

            genre += "," + "Drama";
            addValues("13d", "yes");
        } else {
            removeValues("13d");
        }
        if (cbThriller.isChecked()) {

            genre += "," + "Thriller / Suspense";
            addValues("13e", "yes");
        } else {
            removeValues("13e");
        }

        if (cbAnimation.isChecked()) {

            genre += "," + "Animation";
            addValues("13f", "yes");
        } else {
            removeValues("13f");
        }

        if (cbHorror.isChecked()) {
            genre += "," + "Horror";
            addValues("13g", "yes");
        } else {
            removeValues("13g");
        }

        if (cbRomantic.isChecked()) {

            genre += "," + "Romantic / Love";
            addValues("13h", "yes");
        } else {
            removeValues("13h");
        }

        if (cbComedy.isChecked()) {
            addValues("13i", "yes");
            genre += "," + "Comedy";

        } else {
            removeValues("13i");
        }
        if (cbMythology.isChecked()) {
            addValues("13j", "yes");
            genre += "," + "Mythological";

        } else {
            removeValues("13j");
        }

        if (cbArt.isChecked()) {

            genre += "," + "Art Cinema";
            addValues("13k", "yes");
        } else {
            removeValues("13k");
        }
        if (cbAnyGenre.isChecked()) {

            genre += "," + "Any Genre";
            addValues("13l", "yes");
        } else {
            removeValues("13l");
        }

        return genre;

    }

    @BindView(R.id.edtAboutNewMovies)
    EditText edtAboutNewMovie;
    @BindView(R.id.rgAboutNewMovie)
    RadioGroup rgAboutNewMovie;

    @BindView(R.id.rbAnyShow)
    RadioButton rbAnyShow;
    @BindView(R.id.rgSupportKannadaMovie)
    RadioGroup rgSupportKannadaMovie;
    @BindView(R.id.edtRecommentTheatre)
    EditText edtRecommentTheatre;
    @BindView(R.id.rgMorePrice)
    RadioGroup rgMorePrice;
    @BindView(R.id.rgSmartPhone)
    RadioGroup rgSmartPhone;
    @BindView(R.id.rgGenerallyWatchMovie)
    RadioGroup rgGenerallyWatchMovie;
    @BindView(R.id.edtMovieGenerallyWatch)
    EditText edtGenerallyWatchMovie;
    @BindView(R.id.spinnerBanner)
    Spinner spinnerBanner;
    @BindView(R.id.cbFriends)
    CheckBox cbFriends;
    @BindView(R.id.cbFavouriteActor)
    CheckBox cbFavouriteActor;
    @BindView(R.id.cbDirector)
    CheckBox cbDirector;
    @BindView(R.id.cbProducer)
    CheckBox cbProducer;
    @BindView(R.id.cbreviews)
    CheckBox cbReviews;
    @BindView(R.id.cbActress)
    CheckBox cbFavouriteActress;
    @BindView(R.id.cbHype)
    CheckBox cbHype;
    @BindView(R.id.cbSocialMedia)
    CheckBox cbSocialMedia;
    @BindView(R.id.cbPoster)
    CheckBox cbPoster;
    @BindView(R.id.cbMusic)
    CheckBox cbMusic;
    @BindView(R.id.cbTrailer)
    CheckBox cbTrailer;
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
    @BindView(R.id.cbAnyGenre)
    CheckBox cbAnyGenre;
}
