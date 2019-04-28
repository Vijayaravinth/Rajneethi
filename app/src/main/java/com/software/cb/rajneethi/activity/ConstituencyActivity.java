package com.software.cb.rajneethi.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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
import com.software.cb.rajneethi.adapter.ConstituencyAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.ConstituencyDetails;
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
 * Created by Monica on 19-03-2017.
 */

public class ConstituencyActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    SharedPreferenceManager sharedPreferenceManager;
    private String TAG = "Constituency";

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    private ArrayList<ConstituencyDetails> list = new ArrayList<>();
    String project_id;

    @BindView(R.id.constituency_recyclerview)
    RecyclerView recyclerView;

    ConstituencyAdapter adapter;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_WRITE_PERMISSION = 124;
    final private int REQUEST_CODE_READ_PERMISSION = 125;



    private IOSDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituency);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,"Constituency");

        dialog = show_dialog(ConstituencyActivity.this,false);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        set_Linearlayout_manager(recyclerView, this);
        set_adapter();

        if (getIntent().getExtras() != null) {
            project_id = getIntent().getExtras().getString("id");
            Log.w(TAG, "project id " + project_id);
        }

        if (checkInternet.isConnected()) {
            dialog.show();
            fetch_constituency_from_server();
        } else {
            alert_for_no_internet();
        }
    }


    private void set_adapter() {
        adapter = new ConstituencyAdapter(this, list , ConstituencyActivity.this);
        recyclerView.setAdapter(adapter);
    }

    public void finish_activity(){
        ConstituencyActivity.this.finish();
    }


    private void fetch_constituency_from_server() {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_CONSTITUENCY+"/project/"+project_id+"/"+sharedPreferenceManager.get_user_id() , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               dialog.dismiss();
                Log.w(TAG, "respose112 " + response);

                if (!response.equals("[]")) {

                    try {
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            sharedPreferenceManager.set_project_id(object.getInt("projectId") + "");
                            sharedPreferenceManager.set_Constituency_id(object.getInt("id") + "");
                            ConstituencyDetails details = new ConstituencyDetails(object.getInt("id") + "", object.getInt("projectId") + "", object.getString("name"), object.getString("number"));
                            list.add(details);
                        }

                        if (list.size() > 0) {
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toastmsg(ConstituencyActivity.this, "No projects available");
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "Error " + error.toString());
                Toastmsg(ConstituencyActivity.this, error.toString());
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
