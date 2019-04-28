package com.software.cb.rajneethi.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.ChartAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.ChartDetails;
import com.software.cb.rajneethi.models.GraphDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.checkInternet;
import com.gmail.samehadar.iosdialog.IOSDialog;

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

/**
 * Created by MVIJAYAR on 30-05-2017.
 */

public class CasteEquationActivity extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String TAG = "caste equation";

    @BindView(R.id.btn_retry)
    Button btn_retry;

    @BindView(R.id.autocompltetextview)
    Spinner spinner;

    @BindView(R.id.caste_recyclerview)
    RecyclerView recyclerView;

    ArrayList<String> autocomplte_list = new ArrayList<>();
    ArrayList<ChartDetails> chart_details_list = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    @BindView(R.id.piechart)
    org.eazegraph.lib.charts.PieChart mPieChart;

    @BindString(R.string.casteEquation)
    String toolbarTitle;
    IOSDialog dialog;
    // private int[] colors = {R.color.caste1, R.color.caste2, R.color.caste3, R.color.caste4, R.color.caste5, R.color.caste6};
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caste_equation);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, toolbarTitle);

        dialog = show_dialog(CasteEquationActivity.this, false);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        get_details();
        set_Linearlayout_manager(recyclerView, this);
    }


    /*Initiate chart*/
    public void initiate_chart(String chart_value) {
        Log.w(TAG, "chart value " + chart_value);
        ArrayList<GraphDetails> list = new ArrayList<>();

        String[] fields = chart_value.split("_");


        Log.w(TAG, "fields tokens " + fields.length);

        mPieChart.clearChart();
        for (int i = 0; i <= fields.length - 1; i += 2) {

            int color = randomColor();
            mPieChart.addPieSlice(new PieModel(fields[i], Float.parseFloat(fields[i + 1]), color));
            list.add(new GraphDetails(fields[i], fields[i + 1], color));
        }

        if (list.size() > 0) {
            set_adapter_for_recyclerview(list);
            mPieChart.startAnimation();

        }
    }

    /*generate random color*/
    public int randomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }

    /*set adapter for recyclerview*/
    private void set_adapter_for_recyclerview(ArrayList<GraphDetails> list) {

       /* ChartAdapter adapter = new ChartAdapter(list, this);
        recyclerView.setAdapter(adapter);*/
    }

    @BindString(R.string.selectBooth)
    String selectBooth;


    private void get_chart_details() {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_CASTE_EQUATION + "1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                Log.w(TAG, "response144 " + response);
                try {
                    JSONArray array = new JSONArray(response);

                    if (array.length() > 0) {
                        autocomplte_list.add(selectBooth);
                    }
                    for (int i = 0; i <= array.length() - 1; i++) {

                        JSONObject object = array.getJSONObject(i);
                        String booth_name = object.getString("booth_panchayat_name");

                        ChartDetails details = new ChartDetails(object.getInt("id"), object.getString("caste_equation_percentage").trim());
                        chart_details_list.add(details);

                        autocomplte_list.add(booth_name);
                        if (booth_name.equals("Default")) {
                            initiate_chart(object.getString("caste_equation_percentage").trim());
                        }
                    }

                    if (autocomplte_list.size() > 0) {
                        set_adapter();
                        click_listener_for_autocomplete_textview();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(CasteEquationActivity.this, Error);
                btn_retry.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(CasteEquationActivity.this);
        queue.add(request);
    }

    @BindString(R.string.Error)
    String Error;

    /*autocomplete textview adapter*/
    private void set_adapter() {
        adapter = new ArrayAdapter<String>(CasteEquationActivity.this,
                android.R.layout.simple_list_item_1, autocomplte_list);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);


    }

    /*click listener for autocomplete textview*/
    private void click_listener_for_autocomplete_textview() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ChartDetails details = chart_details_list.get(position - 1);
                    initiate_chart(details.getValue());
                } else {
                    Toastmsg(CasteEquationActivity.this, selectBooth);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /*call to get caste equation details*/
    private void get_details() {
        if (checkInternet.isConnected()) {
            if (btn_retry.isShown()) {
                btn_retry.setVisibility(View.GONE);
            }
            dialog.show();
            get_chart_details();
        } else {
            alert_for_no_internet();
            btn_retry.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.btn_retry)
    public void retry() {
        get_details();
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
