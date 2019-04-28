package com.software.cb.rajneethi.activity;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.ChartAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.fragment.SummaryFragment;
import com.software.cb.rajneethi.models.GraphDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 10/28/2017.
 */

public class ConstituencySummaryActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.booth_spinner)
    Spinner spinner;

    SharedPreferenceManager sharedPreferenceManager;

    private String TAG = "ConstSummary";
    MyDatabase db;
    public ArrayList<GraphDetails> list = new ArrayList<>();

    ArrayList<Fragment> fragmentList = new ArrayList<>();

    private boolean isFirstTime = true;


    private IOSDialog dialog;

    public int total = 0;

    @BindString(R.string.noInternet)
    String noInternet;

    boolean isRequestRunning = false;

    String boothName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituency_summary);
        ButterKnife.bind(this);

        db = new MyDatabase(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        dialog = show_dialog(this, false);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    if (isFirstTime) {
                        try {

                            callAPI(position);
                           /* SummaryFragment fragment = (SummaryFragment) fragmentList.get(position);
                            fragment.txtView.setText("Position :" + position);
                           */
                            isFirstTime = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                //  Log.w(TAG, "onPageSelected" + position);
                try {
                    callAPI(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });


        try {
            Cursor c = db.getBooths();
            if (c.moveToFirst()) {
                do {
                    booth_list.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();

        }


        setAdapterForSpinner();

    }

    private void callAPI(int position) {

        SummaryFragment fragment = (SummaryFragment) fragmentList.get(position);
        Log.w(TAG, "size : " + fragment.pieChart.getData().size());

        if (fragment.pieChart.getData().size() == 0) {

            if (!isRequestRunning) {
                if (checkInternet.isConnected()) {

                    //if(!tabLayout.getTabAt(position).getText().toString().equalsIgnoreCase("Caste")) {

                    dialog.show();

                    isRequestRunning = true;
                    //    Log.w(TAG, "title: " + tabLayout.getTabAt(position).getText().toString());

                    getSummaryDetails(position, tabLayout.getTabAt(position).getText().toString(), boothName);
                /*}else{

                    SummaryFragment fragment1 = (SummaryFragment) fragmentList.get(position);
                    fragment1.pieChart.clearChart();
                    total = 5000;
                    graphDetailsList.clear();

                    int color = randomColor();
                    fragment1.pieChart.addPieSlice(new PieModel("Lingayats", 1000.0F, color));
                    graphDetailsList.add(new GraphDetails("Lingayats", "1000", color));

                    color = randomColor();
                    fragment1.pieChart.addPieSlice(new PieModel("Vokkaligas", 1000.0F, color));
                    graphDetailsList.add(new GraphDetails("Vokkaligas", "1000", color));
                    color = randomColor();
                    fragment1.pieChart.addPieSlice(new PieModel("SC", 1000.0F, color));
                    graphDetailsList.add(new GraphDetails("SC", "1000", color));

                    color = randomColor();
                    fragment1.pieChart.addPieSlice(new PieModel("ST", 1000.0F, color));
                    graphDetailsList.add(new GraphDetails("ST", "1000", color));  color = randomColor();

                    color = randomColor();
                    fragment1.pieChart.addPieSlice(new PieModel("Muslims", 1000.0F, color));
                    graphDetailsList.add(new GraphDetails("Muslims", "1000", color));  color = randomColor();

                    if (graphDetailsList.size() > 0) {
                        set_grid_layout_manager(fragment1.chartRecyclerview, ConstituencySummaryActivity.this,2);
                        set_adapter_for_recyclerview(graphDetailsList, fragment1.chartRecyclerview);
                        fragment1.pieChart.startAnimation();
                    }
                }*/
                } else {
                    Toastmsg(ConstituencySummaryActivity.this, noInternet);
                }

            }
        }
    }


    private ArrayList<String> booth_list = new ArrayList<>();
    private ArrayList<GraphDetails> graphDetailsList = new ArrayList<>();

    private ArrayList<String> getChoices(JSONArray jsonArray) throws JSONException {

        ArrayList<String> choices = new ArrayList<String>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++)
            choices.add(jsonArray.getString(i));
        return choices;
    }


    private void getSummaryDetails(final int position, String title, String boothName) {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_CONSTITUENCY_SUMMARY + "constituency_id=" + sharedPreferenceManager.get_constituency_id() + "&project_id=" +
                sharedPreferenceManager.get_project_id() + "&search_parameter=" + title + "&booth_name=" + boothName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                Log.w(TAG, "response : " + response);
                SummaryFragment fragment = (SummaryFragment) fragmentList.get(position);
                fragment.pieChart.clearChart();
                isRequestRunning = false;
                graphDetailsList.clear();
                try {
                    JSONObject mainObject = new JSONObject(response);
                    JSONObject msgObjet = mainObject.getJSONObject("message");

                    ArrayList<String> attributeName = getChoices(new JSONArray(msgObjet.getString("attribute_array")));
                    ArrayList<String> attributeValue = getChoices(new JSONArray(msgObjet.getString("value_array")));
                    //  fragment.txtView.setText("Position :" + position);

                    total = 0;

                    for (int i = 0; i <= attributeName.size() - 1; i++) {

                        int color = randomColor();


                        //   JSONObject object = array.getJSONObject(i);

                        total += Integer.parseInt(attributeValue.get(i));

                        fragment.pieChart.addPieSlice(new PieModel(attributeName.get(i), Float.parseFloat(attributeValue.get(i)), color));
                        graphDetailsList.add(new GraphDetails(attributeName.get(i), attributeValue.get(i), color));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (graphDetailsList.size() > 0) {
                    set_grid_layout_manager(fragment.chartRecyclerview, ConstituencySummaryActivity.this, 2);
                    set_adapter_for_recyclerview(graphDetailsList, fragment.chartRecyclerview);
                    fragment.pieChart.startAnimation();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isRequestRunning = false;
                dialog.dismiss();
                Log.w(TAG, "Error :" + error.toString());
            }
        });


        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    /*set adapter for recyclerview*/
    private void set_adapter_for_recyclerview(ArrayList<GraphDetails> list, RecyclerView recyclerView) {
        ChartAdapter adapter = new ChartAdapter(list, this, ConstituencySummaryActivity.this);
        recyclerView.setAdapter(adapter);
    }


    /*generate random color*/
    public int randomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }


    /*adapter for spinner*/
    private void setAdapterForSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ConstituencySummaryActivity.this,
                android.R.layout.simple_list_item_1, booth_list);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!isRequestRunning) {
                    if (checkInternet.isConnected()) {


                        try {
                            isRequestRunning = true;
                            //if(!tabLayout.getTabAt(position).getText().toString().equalsIgnoreCase("Caste")) {

                            Log.w(TAG, "Spinner Item seleted");
                            boothName = spinner.getSelectedItem().toString();
                            if (boothName.equalsIgnoreCase("Default")) {
                                boothName = "";
                            }
                            dialog.show();

                            //    Log.w(TAG, "title: " + tabLayout.getTabAt(position).getText().toString());

                            getSummaryDetails(tabLayout.getSelectedTabPosition(), tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString(), boothName);
                        } catch (Exception e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            Toastmsg(ConstituencySummaryActivity.this,"No Questions Found");
                            ConstituencySummaryActivity.super.onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //  spinner.setSelection(1);
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        try {
            Cursor c = db.get_question();
            if (c.moveToFirst()) {
                do {
                    try {
                        JSONObject object = new JSONObject(c.getString(3));
                        String questionFlag = object.getString("questionFlag");
                        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("d2d")) {

                            if (questionFlag.equalsIgnoreCase("BOTH") || questionFlag.equalsIgnoreCase("NS") || questionFlag.equalsIgnoreCase("SS")) {
                                if (!(c.getString(2).equalsIgnoreCase("Name") || c.getString(2).equalsIgnoreCase("Mobile Number") || c.getString(2).equalsIgnoreCase("Area Name"))) {

                                    SummaryFragment fragment = new SummaryFragment();
                                    adapter.addFragment(fragment, c.getString(2));
                                    fragmentList.add(fragment);
                                }
                            }
                        } else if (sharedPreferenceManager.getUserType().equalsIgnoreCase("opcp") || sharedPreferenceManager.getUserType().equalsIgnoreCase("kp")) {
                            if (questionFlag.equalsIgnoreCase("BOTH") || questionFlag.equalsIgnoreCase("opcp") || questionFlag.equalsIgnoreCase("kp")) {
                                if (!(c.getString(2).equalsIgnoreCase("Name") || c.getString(2).equalsIgnoreCase("Education") || c.getString(2).equalsIgnoreCase("Area Name") || c.getString(2).equalsIgnoreCase("Occupation") || c.getString(2).equalsIgnoreCase("Mobile Number"))) {
                                    SummaryFragment fragment = new SummaryFragment();
                                    adapter.addFragment(fragment, c.getString(2));
                                    fragmentList.add(fragment);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } while (c.moveToNext());
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();


    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();


        ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
