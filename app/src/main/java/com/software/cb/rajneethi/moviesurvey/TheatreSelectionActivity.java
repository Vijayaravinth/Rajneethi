package com.software.cb.rajneethi.moviesurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.utility.Util;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 31-12-2017.
 */

public class TheatreSelectionActivity extends Util {

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    @BindString(R.string.theatreSelection)
    String title;

    @BindView(R.id.districtSpinner)
    Spinner districtSpinner;
    @BindView(R.id.citySpinner)
    Spinner citySpinner;

    @BindView(R.id.theatreSpinner)
    Spinner theatreSpinner;

    boolean isTheatreSurvey;

    String district, city, theatre;

    ArrayList<String> districtList = new ArrayList<>();
    ArrayList<String> cityList = new ArrayList<>();
    ArrayList<String> theatreList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theatre_selection);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);


        if (getIntent().getExtras() != null) {
            isTheatreSurvey = getIntent().getExtras().getBoolean("isTheatreSurvey");
        }

        addValuesIntoList();
        setadapterForDistrict();


    }

    //add vaues into list
    private void addValuesIntoList() {
        districtList.add("Select District");
        districtList.add("district 1");
        districtList.add("district 2");
        districtList.add("district 3");

    }

    private void setadapterForDistrict() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, districtList);
        districtSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_text_theatre);

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {

                    district = districtSpinner.getSelectedItem().toString();
                    cityList.clear();
                    setAdapterForCity();

                } else {
                    district = "";
                    city = "";
                    theatre = "";
                    cityList.clear();
                    theatreList.clear();
                    citySpinner.setVisibility(View.GONE);
                    theatreSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setAdapterForCity() {

        cityList.add("Select city");
        cityList.add("City 1");
        cityList.add("city 2");
        cityList.add("city 3");
        citySpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, cityList);
        citySpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_text_theatre);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    city = citySpinner.getSelectedItem().toString();
                    theatre = "";
                    theatreList.clear();
                    setAdapterForTheatre();
                } else {
                    city = "";
                    theatre = "";
                    theatreList.clear();
                    theatreSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setAdapterForTheatre() {
        theatreList.add("select theatre");
        theatreList.add("theatre 1");
        theatreList.add("theatre 2");
        theatreList.add("theatre 3");

        theatreSpinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, theatreList);
        theatreSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_text_theatre);

        theatreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {

                    theatre = theatreSpinner.getSelectedItem().toString();

                    if(!(theatre.isEmpty() || district.isEmpty() || city.isEmpty())){

                        Intent intent = null;
                        if(isTheatreSurvey){
                            intent = new Intent(TheatreSelectionActivity.this,TheatreProfilingAdministrationActivity.class);
                        }else{
                            intent = new Intent(TheatreSelectionActivity.this,TheatrePopleOpinionActivity1.class);
                        }

                        intent.putExtra("district",district);
                        intent.putExtra("city",city);
                        intent.putExtra("theatre",theatre);
                        startActivity(intent);
                    }

                } else {
                    theatre = "";
                    Toastmsg(TheatreSelectionActivity.this, "Please select theatre");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
