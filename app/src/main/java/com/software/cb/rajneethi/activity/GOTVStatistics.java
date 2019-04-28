package com.software.cb.rajneethi.activity;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.ChartAdapter;
import com.software.cb.rajneethi.adapter.GotvStatisticsAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.GraphDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by DELL on 13-04-2018.
 */

public class GOTVStatistics extends UtilActivity {


    private String TAG = "GOTV";

    @BindView(R.id.piechart)
    public PieChart pieChart;


    @BindView(R.id.btn_retry)
    Button btnRetry;
    private ArrayList<GraphDetails> graphDetailsList = new ArrayList<>();

    @BindView(R.id.caste_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.statistics)
    String statistics;
    @BindString(R.string.gotv)
    String gotv;

    @BindView(R.id.autocompltetextview)
    Spinner booth_list_spinner;

    private ArrayList<String> booth_list = new ArrayList<>();

    SharedPreferenceManager sharedPreferenceManager;
    MyDatabase db;

    public int total = 0;
    private IOSDialog dialog;

    String boothName = "default";

    boolean isStatistics = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caste_equation);
        ButterKnife.bind(this);

        int color = randomColor();
        int color1 = randomColor();
        int color2 = randomColor();
        int color3 = randomColor();

        db = new MyDatabase(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        dialog = show_dialog(this, false);
        String title = gotv + " " + statistics;
        setup_toolbar_with_back(toolbar, title);

        if (getIntent().getExtras() != null){

            isStatistics = getIntent().getExtras().getBoolean("isStatistics");

            getSupportActionBar().setTitle("Past Election History");
        }



        set_Linearlayout_manager(recyclerView, GOTVStatistics.this);


        setAdapter();


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

        }


        callAPI();


    }

    private String [] getValues(JSONArray jsonArray) throws JSONException {

        String [] choices = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
            choices[i]=(jsonArray.getString(i));
        return choices;
    }

    public void getPastElectionHistory(String boothName){

        StringRequest request = new StringRequest(Request.Method.GET, API.PAST_ELECTION_HISTORY+"constituency_id="+sharedPreferenceManager.get_constituency_id()+"&project_id="+sharedPreferenceManager.get_project_id()+"&search_past_result=Assembly election"+"&booth="+boothName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                graphDetailsList.clear();
                attributeNameList.clear();
                attributeValueList.clear();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pieChart.clearChart();
                    }
                });

                Log.w(TAG, "response : " + response);

                try{
                    JSONObject object = new JSONObject(response);
                    JSONObject msgObject = object.getJSONObject("message");

                    total = 0;

                   String[] parties = msgObject.getString("parties").replaceAll("\"","").replaceAll("\\[","").replaceAll("]","").split(",");//getValues(new JSONArray(msgObject.getString("parties")));
                    String[] candidates = msgObject.getString("candidates").replaceAll("\"","").replaceAll("\\[","").replaceAll("]","").split(",");// getValues(new JSONArray(msgObject.getString("candidates")));
                    String[] secure_votes_percentage = msgObject.getString("secure_votes_percentage").replaceAll("\"","").replaceAll("\\[","").replaceAll("]","").split(",");// getValues(new JSONArray(msgObject.getString("secure_votes_percentage")));
                    String[] secure_vote_numbers =  msgObject.getString("secure_vote_numbers").replaceAll("\"","").replaceAll("\\[","").replaceAll("]","").split(",");//getValues(new JSONArray(msgObject.getString("secure_vote_numbers")));




                    for (int i = 0 ; i <= parties.length -1 ; i++){
                        int color = randomColor();

                        Log.w(TAG,"vote number "+ secure_vote_numbers[i]);
                        total += Integer.parseInt(secure_vote_numbers[i]);
                        pieChart.addPieSlice(new PieModel(parties[i], Float.parseFloat(secure_vote_numbers[i]), color));
                        graphDetailsList.add(new GraphDetails(candidates[i], secure_vote_numbers[i], color));
                    }


                    if (graphDetailsList.size() > 0) {
                        set_grid_layout_manager(recyclerView, GOTVStatistics.this, 2);
                        set_adapter_for_recyclerview(graphDetailsList, recyclerView);
                        pieChart.startAnimation();
                    }

                }catch (Exception e){
                    e.printStackTrace();


                    graphDetailsList.clear();recyclerView.setAdapter(null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(GOTVStatistics.this, "Error try again later");
                btnRetry.setVisibility(View.VISIBLE);
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }


    @OnClick(R.id.btn_retry)
    public void retry() {
        callAPI();
    }

    private void callAPI() {

        if (checkInternet.isConnected()) {
            btnRetry.setVisibility(GONE);
            dialog.show();
            if (isStatistics) {
                getGOTVStatistics(boothName);
            }else{
                getPastElectionHistory(boothName);
            }
        } else {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    ArrayList<String> attributeNameList = new ArrayList<>();
    ArrayList<String> attributeValueList = new ArrayList<>();

    private void getGOTVStatistics(String boothName) {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_GOTV_STATISTICS + "constituency_id=" + sharedPreferenceManager.get_project_id() + "&gotv_search_attribute=" + boothName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                graphDetailsList.clear();
                attributeNameList.clear();
                attributeValueList.clear();
                pieChart.clearChart();
                Log.w(TAG, "response : " + response);
                dialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response);

                    JSONObject msgObject = object.getJSONObject("message");
                    //  fragment.txtView.setText("Position :" + position);

                    getChoices(new JSONArray(msgObject.getString("grapth_data")));


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> {

            dialog.dismiss();
            Toastmsg(GOTVStatistics.this, "Error try again later");
            btnRetry.setVisibility(View.VISIBLE);
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void getChoices(JSONArray jsonArray) throws JSONException {

        total = 0;

        for (int i = 0; i < jsonArray.length(); i++)
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                attributeNameList.add(object.getString("type"));
                attributeValueList.add(object.getString("cnt_aff"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        for (int i = 0; i <= attributeValueList.size() - 1; i++) {
            int color = randomColor();
            total += Integer.parseInt(attributeValueList.get(i));
            pieChart.addPieSlice(new PieModel(attributeNameList.get(i), Float.parseFloat(attributeValueList.get(i)), color));
            graphDetailsList.add(new GraphDetails(attributeNameList.get(i), attributeValueList.get(i), color));
        }

        if (graphDetailsList.size() > 0) {
            set_grid_layout_manager(recyclerView, this, 2);
            set_adapter_for_recyclerview(graphDetailsList, recyclerView);
            pieChart.startAnimation();
        }


    }

    /*set adapter for recyclerview*/
    private void set_adapter_for_recyclerview(ArrayList<GraphDetails> list, RecyclerView recyclerView) {
        ChartAdapter adapter = new ChartAdapter(list, this, GOTVStatistics.this);
        recyclerView.setAdapter(adapter);
    }


    /*load data to spinner*/
    private void loadDataToSpinner() {
        if (booth_list.size() > 0) {
            set_adapter_for_booth_list();
        } else {
            booth_list_spinner.setVisibility(GONE);
            //
            // Toastmsg(GOTVStatistics.this, noBoothAllocated);
        }
    }


    @BindString(R.string.selectBooth)
    String selectBooth;

    private void set_adapter_for_booth_list() {

        booth_list.add(0, selectBooth);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, booth_list);
        booth_list_spinner.setAdapter(spinnerArrayAdapter);


        booth_list_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position != 0) {

                    boothName = booth_list_spinner.getSelectedItem().toString();
                    callAPI();
                } else {
                    boothName = "default";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    GotvStatisticsAdapter adapter;

    private void setAdapter() {
        adapter = new GotvStatisticsAdapter(graphDetailsList, this, GOTVStatistics.this);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
