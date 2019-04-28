package com.software.cb.rajneethi.activity;

import android.graphics.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.RadioGroup.*;

import butterknife.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.*;
import com.software.cb.rajneethi.models.*;
import com.software.cb.rajneethi.sharedpreference.*;
import com.software.cb.rajneethi.utility.*;
import com.gmail.samehadar.iosdialog.IOSDialog;

import java.util.*;

import org.eazegraph.lib.charts.*;
import org.eazegraph.lib.models.*;
import org.json.*;

import android.support.v7.widget.Toolbar;

/**
 * Created by MVIJAYAR on 30-05-2017.
 */

public class AnalyticsActivity extends Util {


    // @BindView(R.id.toolbar)
    Toolbar toolbar;
    //    @BindView(R.id.txt_toolbar_title)



    private String TAG = "analytics";

    //    @BindView(R.id.btn_retry)
    Button btn_retry;

    //  @BindView(R.id.autocompltetextview)
    Spinner spinner;

    //  @BindView(R.id.caste_recyclerview)
    RecyclerView recyclerView;

    ArrayList<String> autocomplte_list = new ArrayList<>();
    ArrayList<ChartDetails> chart_details_list = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    //  @BindView(R.id.piechart)
    org.eazegraph.lib.charts.PieChart mPieChart;

    IOSDialog dialog;

    @BindString(R.string.analytics)
    String toolbarTitle;
    // private int[] colors = {R.color.caste1, R.color.caste2, R.color.caste3, R.color.caste4, R.color.caste5, R.color.caste6};
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analytics);
        //  ButterKnife.bind(this);
        mPieChart = (PieChart) findViewById(R.id.piechart);
        recyclerView = (RecyclerView) findViewById(R.id.caste_recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner = (Spinner) findViewById(R.id.autocompltetextview);
        btn_retry = (Button) findViewById(R.id.btn_retry);
        setup_toolbar_with_back(toolbar,toolbarTitle);

        dialog = show_dialog(AnalyticsActivity.this, false);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgroup);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                //Toastmsg(AnalyticsActivity.this, "selected  "+checkedId);


            }
        });

        sharedPreferenceManager = new SharedPreferenceManager(this);

        get_details();
        set_Linearlayout_manager(recyclerView, this);
    }

    public void addRadioButtons(int number, List<String> list) {

        for (int row = 0; row < 1; row++) {
            RadioGroup ll = new RadioGroup(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            for (int i = 1; i <= number; i++) {
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId((row * 2) + i);
                rdbtn.setText(list.get(i - 1));
                ll.addView(rdbtn);
            }
            ((ViewGroup) findViewById(R.id.rgroup)).addView(ll);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    /*Initiate chart*/
    public void initiate_chart(String chart_value, int k) {
        Log.w(TAG, "chart value " + chart_value);
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> value_list = new ArrayList<>();
        String[] fields = chart_value.split("_");

        int[] col = new int[fields.length / 2];
        Log.w(TAG, "fields tokens " + fields.length);

        mPieChart.clearChart();
        int color = 0;
        for (int i = 0; i <= fields.length - 1; i += 2) {

            color = randomColor();


            col[i / 2] = color;
            mPieChart.addPieSlice(new PieModel(fields[i], Float.parseFloat(fields[i + 1]), color));
            list.add(fields[i]);
            value_list.add(fields[i + 1]);
        }

        if (list.size() > 0) {
            //set_adapter_for_recyclerview(list, value_list , col);

            addRadioButtons(list.size(), list);
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

 /*   *//*set adapter for recyclerview*//*
    private void set_adapter_for_recyclerview(ArrayList<String> list, ArrayList<String> value_list, int [] col) {

        ChartAdapter adapter = new ChartAdapter(list, this, col, value_list);
        recyclerView.setAdapter(adapter);
    }*/

    @BindString(R.string.Error)
    String Error;
    @BindString(R.string.selectBooth)
    String selectBooth;

    private void get_chart_details() {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_CASTE_EQUATION + sharedPreferenceManager.get_constituency_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                Log.w(TAG, "response169 " + response);
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
                            initiate_chart(object.getString("caste_equation_percentage").trim(), 0);
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
                Toastmsg(AnalyticsActivity.this, Error);
                btn_retry.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(AnalyticsActivity.this);
        queue.add(request);
    }

    /*autocomplete textview adapter*/
    private void set_adapter() {
        adapter = new ArrayAdapter<String>(AnalyticsActivity.this,
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
                    //initiate_chart(details.getValue(),0);
                } else {
                    Toastmsg(AnalyticsActivity.this, selectBooth);
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



}
