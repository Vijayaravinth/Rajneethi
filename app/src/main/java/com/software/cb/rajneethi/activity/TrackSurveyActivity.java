package com.software.cb.rajneethi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.fragment.ShowPayLoadFragment;
import com.software.cb.rajneethi.models.AudioFileDetails;
import com.software.cb.rajneethi.models.SurveyStats;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by w7 on 11/25/2017.
 */

public class TrackSurveyActivity extends Util implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap map;


    @BindString(R.string.maps)
    String title;

    SupportMapFragment mapFragment;
    MyDatabase db;

    @BindView(R.id.booth_spinner)
    Spinner spinner;

    @BindView(R.id.spinnerLayout)
    LinearLayout spinnerLayout;

    @BindView(R.id.arrow)
    ImageView arrow;

    String boothName;

    private boolean isBoothWise = false;


    JSONArray theatreSurveyArray = new JSONArray();

    private ArrayList<String> booth_list = new ArrayList<>();

    private SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.user_spinner)
    Spinner userSpinner;
    private ArrayList<String> userList = new ArrayList<>();


    @BindString(R.string.Error)
    String Error;
    @BindString(R.string.selectBooth)
    String selectBooth;

    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    @BindView(R.id.menuLayout)
    FrameLayout menuLayout;

    @BindString(R.string.selectUsername)
    String selectUsername;

    IOSDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_track_survey);
        ButterKnife.bind(this);

        // setup_toolbar(toolbar);
        //  txtTitle.setText(title);

        if (getIntent().getExtras() != null) {
            isBoothWise = getIntent().getExtras().getBoolean("isBoothWise");
            boothName = getIntent().getExtras().getString("boothName");
        }

        sharedPreferenceManager = new SharedPreferenceManager(this);
        dialog = show_dialog(TrackSurveyActivity.this, false);
        db = new MyDatabase(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);


        if (sharedPreferenceManager.getUserType().equals("TSP")) {

            if (getIntent().getExtras() != null) {
                try {
                    theatreSurveyArray = new JSONArray(getIntent().getExtras().getString("mapValue"));
                    spinner.setVisibility(GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            if (!isBoothWise) {
                spinner.setVisibility(View.VISIBLE);

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
                        dialog.show();
                        get_booth_details();
                    }
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @BindString(R.string.noInternet)
    String noInternet;

    @OnClick(R.id.arrow)
    public void imgArrowClick() {
        hideOrShowSpinnerLayout();
    }

    //hide layout
    private void hideOrShowSpinnerLayout() {
        Animation animation;
        if (spinnerLayout.isShown()) {

            arrow.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_keyboard_arrow_right_black_24dp));
            animation = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
            spinnerLayout.clearAnimation();
            spinnerLayout.startAnimation(animation);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    hide();
                }
            }, 500);
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
            arrow.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_keyboard_arrow_left_white_24dp));

            spinnerLayout.clearAnimation();
            spinnerLayout.startAnimation(animation);
            spinnerLayout.setVisibility(View.VISIBLE);
        }

        // hideKeyboard(TrackSurveyActivity.this);
    }

    @BindString(R.string.noRecordFound)
    String noRecordFound;

    private void getCoordinates(String api) {
        StringRequest request = new StringRequest(Request.Method.GET, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                JSONArray mapData = new JSONArray();

                try {
                    JSONArray array = new JSONArray(response);
                    Log.w(TAG, "response " + response);
                    if (array.length() > 0) {
                        LatLng myLocation1 = null;
                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.getUserType().equalsIgnoreCase("Supervisor")) {
                                if (object.getString("parentid").equals(sharedPreferenceManager.get_user_id()) || object.getString("parentid").equals(sharedPreferenceManager.get_username())) {
                                    myLocation1 = new LatLng(object.getDouble("lattitude"), object.getDouble("longitude"));

                                    userList.add(object.getString("pwname"));
                                    JSONObject mapObject = new JSONObject();
                                    mapObject.put("lattitude", object.getDouble("lattitude"));
                                    mapObject.put("longitude", object.getDouble("longitude"));
                                    mapObject.put("surveyflag", object.getString("surveyflag"));
                                    mapObject.put("pwname", object.getString("pwname"));
                                    mapObject.put("boothname", object.getString("boothname"));
                                    mapObject.put("surveyid", object.getString("surveyid"));

                                    map.addMarker(new MarkerOptions().position(myLocation1).snippet(mapObject.toString())).hideInfoWindow();
                                    map.setOnMarkerClickListener(TrackSurveyActivity.this);
                                    theatreSurveyArray.put(mapObject);
                                }
                            } else {
                                userList.add(object.getString("pwname"));
                                myLocation1 = new LatLng(object.getDouble("lattitude"), object.getDouble("longitude"));
                                // map.addMarker(new MarkerOptions().position(myLocation1).title(object.getString("respondantname")));
                                JSONObject mapObject = new JSONObject();
                                mapObject.put("lattitude", object.getDouble("lattitude"));
                                mapObject.put("longitude", object.getDouble("longitude"));
                                mapObject.put("surveyflag", object.getString("surveyflag"));
                                mapObject.put("pwname", object.getString("pwname"));
                                mapObject.put("boothname", object.getString("boothname"));
                                mapObject.put("surveyid", object.getString("surveyid"));

                                theatreSurveyArray.put(mapObject);

                                map.addMarker(new MarkerOptions().position(myLocation1).snippet(mapObject.toString())).hideInfoWindow();
                                map.setOnMarkerClickListener(TrackSurveyActivity.this);
                            }
                            ///
                        }

                        if (myLocation1 != null) {
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                    myLocation1).zoom(17).build();
                            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                    } else {
                        Toastmsg(TrackSurveyActivity.this, noRecordFound);
                    }

                    Set<String> hs = new HashSet<>();
                    hs.addAll(userList);
                    userList.clear();
                    userList.addAll(hs);
                    userList.add(0, selectUser);
                    setAdapterForUser(userList);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
                dialog.dismiss();
                Toastmsg(TrackSurveyActivity.this, Error);
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void hide() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerLayout.setVisibility(GONE);
            }
        });

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

                Toastmsg(TrackSurveyActivity.this, Error);

            }
        });

        //  VolleySingleton.getInstance(this).addToRequestQueue(request);

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /*load data to spinner*/
    private void loadDataToSpinner() {
        if (booth_list.size() > 0) {
            set_adapter_for_spinner(booth_list);
        }
    }


    private void setAdapterForUser(ArrayList<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, R.id.txtSpinnerText, list);
        userSpinner.setAdapter(adapter);
        //     spinner.setThreshold(1);
        adapter.setDropDownViewResource(R.layout.spinner_textview);

     /*   spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMarkerBaseOnBooth(spinner.getText().toString());
            }
        });*/

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // hideOrShowSpinnerLayout();
                if (i != 0) {
                    setMarkerBaseOnUser(userSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setMarkerBaseOnUser(String username) {
        map.clear();
        LatLng myLocation1 = null;
        for (int i = 0; i <= theatreSurveyArray.length() - 1; i++) {
            try {
                JSONObject object = theatreSurveyArray.getJSONObject(i);

                if (object.getString("pwname").equals(username)) {
                    myLocation1 = new LatLng(object.getDouble("lattitude"), object.getDouble("longitude"));
                    map.addMarker(new MarkerOptions().position(myLocation1).snippet(object.toString())).hideInfoWindow();
                    map.setOnMarkerClickListener(this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (myLocation1 != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    myLocation1).zoom(17).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

       /* if (myLocation1 != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    myLocation1).zoom(12).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }*/

    }

    private void setMarkerBaseOnBooth(String boothName) {
        map.clear();
        LatLng myLocation1 = null;
        for (int i = 0; i <= theatreSurveyArray.length() - 1; i++) {
            try {
                JSONObject object = theatreSurveyArray.getJSONObject(i);

                if (object.getString("boothname").equals(boothName)) {
                    myLocation1 = new LatLng(object.getDouble("lattitude"), object.getDouble("longitude"));
                    map.addMarker(new MarkerOptions().position(myLocation1).snippet(object.toString())).hideInfoWindow();
                    map.setOnMarkerClickListener(this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (myLocation1 != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    myLocation1).zoom(17).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    private void set_adapter_for_spinner(ArrayList<String> options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, R.id.txtSpinnerText, options);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_textview);


        /*spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.w(TAG, "On item click working  " + parent.getSelectedItem().toString());
                setMarkerBaseOnBooth(spinner.getSelectedItem().toString());
                //  hideOrShowSpinnerLayout();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    public void call(String mobileNumber) {

        Log.w(TAG, "Mobile number");
        if (checkPermissionGranted(Constants.CALL, this)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber.trim()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } else {
            askPermission(new String[]{Constants.CALL});
        }
    }


    private void addMarkerIntoMap() {
        map.clear();
        LatLng myLocation1 = null;
        for (int i = 0; i <= theatreSurveyArray.length() - 1; i++) {
            try {
                JSONObject object = theatreSurveyArray.getJSONObject(i);

                myLocation1 = new LatLng(object.getDouble("lattitude"), object.getDouble("longitude"));
                map.addMarker(new MarkerOptions().position(myLocation1).snippet(object.toString())).hideInfoWindow();
                //  map.setOnMarkerClickListener(this);

                userList.add(object.getString("username"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (myLocation1 != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    myLocation1).zoom(10).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }


        if (userList.size() > 0) {
            Set<String> hs = new HashSet<>();
            hs.addAll(userList);
            userList.clear();
            userList.addAll(hs);
            userList.add(0, selectUser);
            setAdapterForUser(userList);
        }

    }

    @BindString(R.string.selectUser)
    String selectUser;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //  addMarkerIntoMap();


        if (checkInternet.isConnected()) {
            dialog.show();
            if (isBoothWise) {
                getCoordinates(API.GET_BOOTH_WISE_DATA + sharedPreferenceManager.get_project_id() + "&boothName=" + boothName);
            } else {
                getCoordinates(API.GET_AUDIO_FILES + sharedPreferenceManager.get_project_id());
            }
        } else {
            Toastmsg(TrackSurveyActivity.this, noInternet);
        }


    }


    private String TAG = "Maps Activity";


    @Override
    public boolean onMarkerClick(Marker marker) {


        try {
            JSONObject object = new JSONObject(marker.getSnippet());

            ShowPayLoadFragment frag = ShowPayLoadFragment.newInstance(new AudioFileDetails(object.getString("surveyid"), true, object.getString("surveyflag")));
            frag.show(getSupportFragmentManager(), "Dialog");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
