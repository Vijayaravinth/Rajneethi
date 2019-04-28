package com.software.cb.rajneethi.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.StatsHeaderAdapter;
import com.software.cb.rajneethi.adapter.SurveyStatsAdapter;
import com.software.cb.rajneethi.adapter.SurveyStatsClientAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.fragment.ShowPayloaddataForClient;
import com.software.cb.rajneethi.models.AudioFileDetails;
import com.software.cb.rajneethi.models.SurveyStats;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by w7 on 12/7/2017.
 */

public class SurveyStatsActivity extends Util implements checkInternet.ConnectivityReceiverListener, ShowPayloaddataForClient.updateData {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.surveyStats)
    String title;

    @BindView(R.id.btnRetry)
    Button btnRetry;
    String dir;
    File file;
    ArrayList<SurveyStats> mainList = new ArrayList<>();

    private MyDatabase db;

    String boothName;

    private boolean isBoothWise = false;

    private SharedPreferenceManager sharedPreferenceManager;
    @BindString(R.string.noRecordFound)
    String noRecordFound;

    @BindString(R.string.noInterest)
    String noInternet;

    @BindView(R.id.stats_recyclerview)
    RecyclerView recyclerView;

    SurveyStatsAdapter adapter;

    IOSDialog dialog;

    int oldPosition = -1;

    StatsHeaderAdapter clientAdapter;
    ArrayList<SurveyStats> pwNameList = new ArrayList<>();

    @BindView(R.id.headerRecyclerview)
    RecyclerView headerRecyclerView;

    @BindView(R.id.othersLayout)
    LinearLayout othersLayout;

    @BindView(R.id.contentRecyclerView)
    RecyclerView contentRecyclerview;

    private String TAG = "SurveyStatsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_stats);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, title);


        db = new MyDatabase(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        Log.w(TAG, "User id : " + sharedPreferenceManager.get_user_id());
        dialog = show_dialog(this, false);

       /* if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {
            try {
                Cursor c = db.getBoothStats(sharedPreferenceManager.get_project_id());
                if (c.getCount() > 0) {
                    if (c.moveToFirst()) {
                        do {
                            mainList.add(new SurveyStats(c.getString(1), c.getString(0), false));
                        } while (c.moveToNext());
                    }

                    if (mainList.size() > 0) {
                        set_Linearlayout_manager(recyclerView, this);
                        setAdapter();
                    }

                } else {
                    Toastmsg(this, noRecordFound);
                    SurveyStatsActivity.super.onBackPressed();
                    SurveyStatsActivity.this.finish();
                }

            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else {*/

        if (getIntent().getExtras() != null) {
            isBoothWise = getIntent().getExtras().getBoolean("isBoothWise");
            boothName = getIntent().getExtras().getString("boothName");
        }

        othersLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(GONE);
        set_Linearlayout_manager(headerRecyclerView, this);
        setAdapterForClient();

        callAPI();


    }


    private void setAdapterForClient() {
        clientAdapter = new StatsHeaderAdapter(this, pwNameList, SurveyStatsActivity.this);
        headerRecyclerView.setAdapter(clientAdapter);
    }

    private void showRetryButton() {
        if (!btnRetry.isShown()) {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }


    private void hideRetryButton() {
        if (btnRetry.isShown()) {
            btnRetry.setVisibility(GONE);
        }
    }

    @OnClick(R.id.btnRetry)
    public void retry() {
        callAPI();
    }

    //call api
    private void callAPI() {
        if (checkInternet.isConnected()) {
            dialog.show();
            if (isBoothWise) {
                getMusicFiles(API.GET_BOOTH_WISE_DATA + sharedPreferenceManager.get_project_id() + "&boothName=" + boothName);
            } else {
                getMusicFiles(API.GET_AUDIO_FILES + sharedPreferenceManager.get_project_id());
            }
        } else {

            Toastmsg(SurveyStatsActivity.this, noInternet);
            showRetryButton();
        }
    }

    ArrayList<SurveyStats> dataList = new ArrayList<>();

    boolean isUserwise = true;

    public void loadData(String pwName) {

        dataList.clear();

        Log.w(TAG, "Pw name :" + pwName);

        dialog.show();

        for (int j = 0; j <= mainList.size() - 1; j++) {
            SurveyStats stats = mainList.get(j);
            if (isUserwise) {
                if (stats.getBoothName().equalsIgnoreCase(pwName)) {
                    dataList.add(stats);
                }
            } else {
                if (stats.getBoothName().equalsIgnoreCase(pwName)) {
                    dataList.add(stats);
                }
            }
        }

        Log.w(TAG, "Data list size : " + dataList.size());

        if (dataList.size() > 0) {
            statsAdapter.notifyDataSetChanged();
        }

        dialog.dismiss();
    }


    public void dismiss() {
        dialog.dismiss();
    }


    public void showPayLoadData(SurveyStats details, int pos) {

        validatePosition = pos;
        ShowPayloaddataForClient frag = ShowPayloaddataForClient.newInstance(details);
        frag.show(getSupportFragmentManager(), "Dialog");
    }


    @SuppressLint("MissingPermission")
    public void call(String mobileNumber) {
        Log.w(TAG, "Mobile number");

        try {
            if (checkPermissionGranted(Constants.CALL, this)) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber.trim()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                askPermission(new String[]{Constants.CALL});
            }
        } catch (Exception e) {
            Toastmsg(SurveyStatsActivity.this, Error);
        }
    }

    ArrayList<String> userNameList = new ArrayList<>();
    ArrayList<String> boothNameList = new ArrayList<>();

    private void getMusicFiles(String api) {
        StringRequest request = new StringRequest(Request.Method.GET, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                hideRetryButton();
                try {
                    JSONArray array = new JSONArray(response);

                    Log.w(TAG, "response " + response);
                    if (array.length() > 0) {


                        ArrayList<SurveyStats> valueList = new ArrayList<>();

                        for (int i = 0; i <= array.length() - 1; i++) {

                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.get_keep_login_role().equals("Supervisor")) {

                                if (object.getString("parentid").equals(sharedPreferenceManager.get_user_id()) || object.getString("parentid").equals(sharedPreferenceManager.get_username())) {
                                    userNameList.add(object.getString("boothname"));
                                    boothNameList.add(object.getString("boothname"));
                                    mainList.add(new SurveyStats(object.getString("audiofilename"), object.getString("surveyid"),
                                            object.getString("surveyflag"), object.getString("parentid"), object.getString("respondantname"),
                                            object.getString("mobile"), object.getString("pwname"), 1, object.getString("boothname")));


                                }

                            } else {

                                userNameList.add(object.getString("boothname"));
                                boothNameList.add(object.getString("boothname"));
                                mainList.add(new SurveyStats(object.getString("audiofilename"), object.getString("surveyid"),
                                        object.getString("surveyflag"), object.getString("parentid"), object.getString("respondantname"),
                                        object.getString("mobile"), object.getString("pwname"), 1, object.getString("boothname")));

                            }
                        }


                        //  List<String> al = new ArrayList<>();
                        Set<String> hs = new HashSet<>();
                        hs.addAll(userNameList);
                        userNameList.clear();
                        userNameList.addAll(hs);
                        dialog.dismiss();

                        for (int i = 0; i <= userNameList.size() - 1; i++) {

                            if (db.checkBoothExist(userNameList.get(i))) {
                                Log.w(TAG, "Booth Exist");
                                SurveyStats stats = new SurveyStats("0", userNameList.get(i), false);
                                pwNameList.add(stats);
                            }
                        }

                        if (pwNameList.size() > 0) {
                            clientAdapter.notifyDataSetChanged();

                            set_Linearlayout_manager(contentRecyclerview, SurveyStatsActivity.this);
                            setAdapterForStats();

                        } else {
                            Toastmsg(SurveyStatsActivity.this, noRecordFound);
                        }

                       /* if (userNameList.size() > 0) {
                            new addDataIntoList(userNameList, valueList).execute();
                            Log.w(TAG, "User list size : " + userNameList.size() + " value list : " + valueList.size() + " main list size : " + mainList.size());

                            Log.w(TAG, "Main list size  : " + mainList.size());
                        } else {
                            Toastmsg(SurveyStatsActivity.this, noRecordFound);
                        }*/

                        //   clientAdapter.notifyDataSetChanged();


                    } else {
                        dialog.dismiss();
                        Toastmsg(SurveyStatsActivity.this, noRecordFound);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //  Log.w(TAG, "Response :" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
                dialog.dismiss();
                Toastmsg(SurveyStatsActivity.this, Error);
                showRetryButton();

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    SurveyStatsClientAdapter statsAdapter;

    private void setAdapterForStats() {
        statsAdapter = new SurveyStatsClientAdapter(this, dataList, SurveyStatsActivity.this);
        contentRecyclerview.setAdapter(statsAdapter);
    }

    @Override
    public void update(String flag) {
        if (validatePosition != -1) {
            SurveyStats details = dataList.get(validatePosition);
            details.setSurveyFlag(flag);
            Log.w(TAG, "details " + details.getSurveyFlag());
            //  update();

            statsAdapter.notifyItemChanged(validatePosition);

        }

        validatePosition = -1;

    }

    int validatePosition = -1;


    private class addDataIntoList extends AsyncTask<Void, Void, Void> {

        ArrayList<String> userNameList;
        ArrayList<SurveyStats> valueList;

        public addDataIntoList(ArrayList<String> userNameList, ArrayList<SurveyStats> valueList) {
            this.userNameList = userNameList;
            this.valueList = valueList;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i <= userNameList.size() - 1; i++) {

                mainList.add(new SurveyStats(userNameList.get(i), 0));
                //  Log.w(TAG, "Main list size  i: " + mainList.size());

                for (int j = 0; j <= valueList.size() - 1; j++) {
                    SurveyStats stats = valueList.get(j);
                    if (stats.getUserId().equals(userNameList.get(i))) {
                        mainList.add(stats);
                        Log.w(TAG, "Main list size  j: " + mainList.size());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            clientAdapter.notifyDataSetChanged();
        }
    }

    @BindString(R.string.Error)
    String Error;

    public void expandData(int pos) {

        if (oldPosition != -1) {
            SurveyStats oldStats = pwNameList.get(oldPosition);
            oldStats.setExpanded(false);
        }

        SurveyStats stats = pwNameList.get(pos);
        stats.setExpanded(true);
        oldPosition = pos;

        clientAdapter.notifyDataSetChanged();

    }

    public void expandForPartyWorkers(int pos) {
        if (oldPosition != -1) {
            SurveyStats oldStats = mainList.get(oldPosition);
            oldStats.setExpanded(false);
        }

        SurveyStats stats = mainList.get(pos);
        stats.setExpanded(true);
        oldPosition = pos;

        adapter.notifyDataSetChanged();

    }

    public void disableExpand(int pos) {
        SurveyStats stats = pwNameList.get(pos);
        stats.setExpanded(false);
        clientAdapter.notifyDataSetChanged();
        oldPosition = -1;
    }

    public void disableExpandForPartyWorkers(int pos) {
        SurveyStats stats = mainList.get(pos);
        stats.setExpanded(false);
        adapter.notifyDataSetChanged();
        oldPosition = -1;
    }


    public ArrayList<SurveyStats> getSurveyTypeStats(String boothName) {

        ArrayList<SurveyStats> list = new ArrayList<>();
        try {

            Cursor c = db.getBoothSurveyStats(boothName, sharedPreferenceManager.get_project_id());

            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        list.add(new SurveyStats(c.getString(0), c.getString(1)));
                    } while (c.moveToNext());
                }
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return list;
    }


    private void setAdapter() {
        adapter = new SurveyStatsAdapter(this, mainList, SurveyStatsActivity.this);
        recyclerView.setAdapter(adapter);

        //recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public void setPos(int pos, String mobile) {

        call(mobile);
        oldPosition = pos;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        if (oldPosition != -1) {
            statsAdapter.notifyItemChanged(oldPosition);
            oldPosition = -1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Party Worker")) {
            getMenuInflater().inflate(R.menu.stats_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.calendar:
                showCalendar();
                return true;
            case R.id.change:
                if (isUserwise) {
                    isUserwise = false;
                } else {
                    isUserwise = true;
                }
                pwNameList.clear();

                if (isUserwise) {
                    Set<String> hs = new HashSet<>();
                    hs.addAll(userNameList);
                    userNameList.clear();
                    userNameList.addAll(hs);


                    for (int i = 0; i <= userNameList.size() - 1; i++) {
                        SurveyStats stats = new SurveyStats("0", userNameList.get(i), false);
                        pwNameList.add(stats);
                    }
                } else {
                    Set<String> hs = new HashSet<>();
                    hs.addAll(boothNameList);
                    boothNameList.clear();
                    boothNameList.addAll(hs);


                    for (int i = 0; i <= boothNameList.size() - 1; i++) {
                        SurveyStats stats = new SurveyStats("0", boothNameList.get(i), false);
                        pwNameList.add(stats);
                    }
                }

                if (pwNameList.size() > 0) {


                    oldPosition = -1;

                    clientAdapter.notifyDataSetChanged();

                    dataList.clear();

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showCalendar() {
        Calendar newCalendar = Calendar.getInstance();

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.US);
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(SurveyStatsActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        fromDatePickerDialog.show();
    }


}
