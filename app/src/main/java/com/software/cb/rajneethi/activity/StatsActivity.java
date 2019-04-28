package com.software.cb.rajneethi.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.StatsAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.Partyworkerstats;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.ServerUtils;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.checkInternet;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 4/8/2017.
 */

public class StatsActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    private Context context;

    private SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.booth_stats_recyclerview)
    RecyclerView recyclerView;

    private String TAG = "Booth Stats";
    ArrayList<Partyworkerstats> partyworkerstatses = new ArrayList<>();
    StatsAdapter adapter;

    @BindString(R.string.boothStats)
    String toolbarTitle;
    private IOSDialog dialog;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ButterKnife.bind(this);


        dialog = show_dialog(StatsActivity.this, false);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        if (getIntent().getExtras() != null) {
            userName = getIntent().getExtras().getString("username");
        }


        partyworkerstatses.add(new Partyworkerstats("2-Govt Higher Primary School", "10"));
        partyworkerstatses.add(new Partyworkerstats("3-Govt Higher Primary School(Left Wing)", "5"));
        partyworkerstatses.add(new Partyworkerstats("9-Govt Higher Primary School(Right Wing)", "8"));
        partyworkerstatses.add(new Partyworkerstats("111-B.E.O office", "10"));
        partyworkerstatses.add(new Partyworkerstats("172-Govt Higher Primary School", "6"));
        partyworkerstatses.add(new Partyworkerstats("174-Govt Lower Primary School", "9"));

        String name = userName + "      -  48";
        setup_toolbar_with_back(toolbar,name);



        set_Linearlayout_manager(recyclerView, this);

        set_adapter();

       /* if (checkInternet.isConnected()) {
            fetchPartyWorkerFromServer();
        } else {
            alert_for_no_internet();
        }*/
    }

    //set adapter
    private void set_adapter() {

        adapter = new StatsAdapter(this, partyworkerstatses);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));

    }

    @BindString(R.string.noRecordFound)
    String noRecordFound;


    private void fetchPartyWorkerFromServer() {
        dialog.show();
        try {
            Log.w("Constituency76 ", "Response " + sharedPreferenceManager);
            Log.w(TAG, "Booth stats url :" + API.GETROLEURL + "GETPARTYWORKETSTATS&partyWorker=" + sharedPreferenceManager.get_user_id());
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                    API.GETROLEURL + "GETPARTYWORKETSTATS&partyWorker=" + userName,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            if (response.length() > 0) {
                                Log.w("Constituency 84", "Response " + response);
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        // response.getJSONObject(0).get("BoothName");
                                        Partyworkerstats statsbooth = new Partyworkerstats(response.getJSONObject(i));
                                        partyworkerstatses.add(statsbooth);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                Toastmsg(StatsActivity.this, noRecordFound);
                            }
                            //hidePDialog();

                            if (partyworkerstatses.size() > 0) {
                                adapter.notifyDataSetChanged();
                            }
                            dialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.w(TAG, "Error :" + error.toString());
                            Toastmsg(StatsActivity.this, Error);
                            dialog.dismiss();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ServerUtils.getCustomHeaders();
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsObjRequest);
            queue.getCache().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @BindString(R.string.Error)
    String Error;


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
}
