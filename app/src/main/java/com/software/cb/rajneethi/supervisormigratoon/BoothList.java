package com.software.cb.rajneethi.supervisormigratoon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.software.cb.rajneethi.activity.UserManagementActivity;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
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

public class BoothList extends Util implements View.OnClickListener {
    private Context context;
    private ProgressBar spinner;
    private SharedPreferenceManager sharedPreferenceManager;
    String s;
    Toolbar toolbar;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);

        Toastmsg(BoothList.this, "third");
        setContentView(R.layout.activity_voterlist);
        toolbar= (Toolbar) findViewById(R.id.toolbar);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        setup_toolbar(toolbar);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
     //  s = getIntent().getStringExtra("PARTY_WORKER_ID");
        //findViewById(R.id.get_survey_data).setOnClickListener(this);
        image.setImageResource(R.drawable.backimage);
       //image.setImageDrawable(drawable.backimage.png);
        //init1();
        //RecyclerView re=((RecyclerView) findViewById(R.id.partyworkers));
        fetchPartyWorkerFromServer();
    }


    private void fetchPartyWorkerFromServer() {
        spinner.setVisibility(View.VISIBLE);
        try {
           // Toastmsg(BoothList.this, "third");
            //Log.v("fggjdf",API.GETROLEURL+"VOTERS&SupervisorId="+getIntent().getStringExtra("SUPRVISOR_ID")+"&PartyWorkerId="+getIntent().getStringExtra("PARTY_WORKER_ID"));
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                    API.GETROLEURL+"GETPARTYWORKERBOOTHS&partyWorker="+getIntent().getStringExtra("PARTY_WORKER_ID"),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray  response) {
                            ArrayList<String> Booth =new ArrayList();
                            Log.w("Constituency ","Response "+ response);
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                   // Voterlist voter = new Voterlist(response.getJSONObject(i));
                                    //Booth.add(response.getString("BoothName");
                                    Booth.add(response.getJSONObject(i).getString("BoothName"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //hidePDialog();

                            if (Booth.size() > 0) {

                                final BoothListAdapter adapter = new BoothListAdapter(BoothList.this,Booth,getIntent().getStringExtra("PARTY_WORKER_ID"),getIntent().getStringExtra("SUPRVISOR_ID"));
                                ((ListView)findViewById(R.id.Voterlist)).setAdapter(adapter);

                            }
                            spinner.setVisibility(View.GONE);

                           // RequestFactory.getInstance().getRequestQueue().add(jsObjRequest);

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
          //  RequestFactory.getInstance(this).addToRequestQueue(jsObjRequest);
            RequestFactory.getInstance(this).getRequestQueue().add(jsObjRequest);
            RequestFactory.getInstance(this).getRequestQueue().getCache().clear();// cancelAll(jsObjRequest);
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
                startActivity(new Intent(BoothList.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                BoothList.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(BoothList.this, UserManagementActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        //public void go_back() {
            //back();
        //}
    }
   /* public void back() {
        super.onBackPressed();
    }*/

    /*@Override*/
   /* public void onClick(View v) {
        // startActivity(new Intent( SupervisorView.class,));
    }*/
}
