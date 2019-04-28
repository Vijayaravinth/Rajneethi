package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.ShowProjectAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.ProjectDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.checkInternet;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monica on 09-03-2017.
 */

public class ProjectActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    private String TAG = "Download activity";

    private SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.project_recyclerview)
    RecyclerView recyclerView;

    ArrayList<ProjectDetails> list = new ArrayList<>();
    private ShowProjectAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    final private int REQUEST_CODE_WRITE_PERMISSION = 124;
    final private int REQUEST_CODE_READ_PERMISSION = 125;

    private IOSDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,"Project");

        dialog = show_dialog(ProjectActivity.this,false);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        set_Linearlayout_manager(recyclerView, this);
        set_adapter();

        if (checkInternet.isConnected()) {
            dialog.show();
            download_project();
            // fetch_constituency_from_server();
        } else {
            alert_for_no_internet();
        }
    }

    private void set_adapter() {
        adapter = new ShowProjectAdapter(this, list,ProjectActivity.this);
        recyclerView.setAdapter(adapter);
    }

    public void finish_activity(){
        ProjectActivity.this.finish();
    }

    private void download_project() {

        final StringRequest request = new StringRequest(Request.Method.GET, API.GET_PROJECT + sharedPreferenceManager.get_user_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();

                if (!response.equals("[]")) {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject obj = array.getJSONObject(i);
                            ProjectDetails details = new ProjectDetails(obj.getInt("id") + "", obj.getString("name"), obj.getString("description"));
                            list.add(details);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toastmsg(ProjectActivity.this, "There is no project yet");
                }
                Log.w(TAG, "Response122 " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.w(TAG, "error " + error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void fetch_constituency_from_server() {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_CONSTITUENCY + sharedPreferenceManager.get_user_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               dialog.dismiss();
                Log.w(TAG, "response214 " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "Error " + error.toString());
                Toastmsg(ProjectActivity.this, error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

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
}
