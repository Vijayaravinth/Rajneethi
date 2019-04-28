package com.software.cb.rajneethi.supervisormigratoon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.LoginActivity;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.supervisormigratoon.pojo.SupervisorPartyWorker;
import com.software.cb.rajneethi.utility.RequestFactory;
import com.software.cb.rajneethi.utility.ServerUtils;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by admin on 4/8/2017.
 */



public class PartyWorkers extends Util  {
    private Context context;
    private ProgressBar spinner;
    private SharedPreferenceManager sharedPreferenceManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_party_workers);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setup_toolbar(toolbar);

        sharedPreferenceManager = new SharedPreferenceManager(this);
      // int id= CredentialUtils.user.getId();
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        //findViewById(R.id.get_survey_data).setOnClickListener(this);
        //init1();
        //RecyclerView re=((RecyclerView) findViewById(R.id.partyworkers));
        fetchPartyWorkerFromServer();
    }


    private void fetchPartyWorkerFromServer() {
        spinner.setVisibility(View.VISIBLE);
        try {

            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                   API.GETROLEURL+"GETPARTYWORKER&SupervisorId="+sharedPreferenceManager.get_user_id(),
                 //   API.GET_BASEPATHSUPERVISOR+"GETPARTYWORKER&SupervisorId="+sharedPreferenceManager.get_userid(),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            sharedPreferenceManager.set_keep_login(true);
                            sharedPreferenceManager.set_keep_login_role("Supervisor");
                            ArrayList<SupervisorPartyWorker> partyWorkerslist =new ArrayList();
                            Log.w("Constituency84 ","Response "+ response);
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    SupervisorPartyWorker worker = new SupervisorPartyWorker(response.getJSONObject(i));
                                    partyWorkerslist.add(worker);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //hidePDialog();

                            if (partyWorkerslist.size() > 0) {

                                final PartyworkersAdapter adapter = new  PartyworkersAdapter(PartyWorkers.this,partyWorkerslist);
                                ((ListView)findViewById(R.id.partyworkers)).setAdapter(adapter);

                            }
                            spinner.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //hidePDialog();
                            //if (error instanceof TimeoutError)
                                //makeToast("Unable to connect to server");
                            //else if (error instanceof AuthFailureError) {
                              //  CredentialUtils.invalidateCredentials();
                                //launchActivity(LoginActivity.class);
                            //} else
                              //  makeToast("Unable to make contact with server");
                            spinner.setVisibility(View.GONE);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ServerUtils.getCustomHeaders();
                }
            };
           // RequestFactory.getInstance(this).addToRequestQueue(jsObjRequest);
            RequestFactory.getInstance(this).getRequestQueue().add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sharedPreferenceManager.set_keep_login(false);
                sharedPreferenceManager.set_keep_login_role("");
               // new delete().execute();
                startActivity(new Intent(PartyWorkers.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                PartyWorkers.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override*/
   /* public void onClick(View v) {
        // startActivity(new Intent( SupervisorView.class,));
    }*/
}
