package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.checkInternet;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.eazegraph.lib.charts.PieChart;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MVIJAYAR on 06-06-2017.
 */

public class ElectionHistoryActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.piechart)
    PieChart pieChart;

    @BindString(R.string.swotAnalysis)
    String toolbarTitle;
    private String TAG = "Election History";

    private IOSDialog dialog;
    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_history);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, toolbarTitle);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        dialog = show_dialog(ElectionHistoryActivity.this, false);

        if (checkInternet.isConnected()) {

            dialog.show();
            // get_election_history();
            get_optionon_poll_and_benifit_details();

            //get_history_booth_details();
        }

    }

    /*This api for picharts of Loksubha and vidhanasabba*/

    private void get_election_history() {

        Log.w(TAG, "url " + API.GET_ELECTION_HISTORY + sharedPreferenceManager.get_username() + "&ProID=" + sharedPreferenceManager.get_project_id());
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_ELECTION_HISTORY + sharedPreferenceManager.get_username() + "&ProID=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "response78 " + response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "error " + error.toString());

            }
        });

        RequestQueue queue = Volley.newRequestQueue(ElectionHistoryActivity.this);
        queue.add(request);
    }

    // http://ec2-52-66-83-57.ap-south-1.compute.amazonaws.com/api/v1/shared?PRCID=GETQUESTION&constituencyId=" + $scope.consId


    private void get_optionon_poll_and_benifit_details() {

        StringRequest request = new StringRequest(Request.Method.GET, "http://ec2-52-66-83-57.ap-south-1.compute.amazonaws.com/api/v1/shared?PRCID=GETQUESTION&constituencyId=" + "1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "response104 " + response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "error " + error.toString());

            }
        });

        RequestQueue queue = Volley.newRequestQueue(ElectionHistoryActivity.this);
        queue.add(request);
    }

    //http://ec2-52-66-83-57.ap-south-1.compute.amazonaws.com/api/v1/constituencymap/' + projectId
    private void get_history_booth_details() {

        StringRequest request = new StringRequest(Request.Method.GET, "http://ec2-52-66-83-57.ap-south-1.compute.amazonaws.com/api/v1/constituencymap/" + "1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "response128 " + response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "error " + error.toString());

            }
        });

        RequestQueue queue = Volley.newRequestQueue(ElectionHistoryActivity.this);
        queue.add(request);
    }


    @Override
    protected void onPause() {
        super.onPause();
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
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    /*get survey report*/
    private void get_survey_report() {

        Log.w(TAG, "url " + API.GET_ELECTION_HISTORY + sharedPreferenceManager.get_username() + "&ProID=" + sharedPreferenceManager.get_project_id());
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_ELECTION_HISTORY + sharedPreferenceManager.get_username() + "&ProID=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "response175 " + response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "error " + error.toString());

            }
        });

        RequestQueue queue = Volley.newRequestQueue(ElectionHistoryActivity.this);
        queue.add(request);
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
