package com.software.cb.rajneethi.supervisormigratoon;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.supervisormigratoon.pojo.VoterListDetail;
import com.software.cb.rajneethi.utility.RequestFactory;
import com.software.cb.rajneethi.utility.ServerUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by admin on 4/8/2017.
 */

public class VoterListVDetails extends AppCompatActivity {
    private Context context;
    private ProgressBar spinner;
   // private String partyworkerid;
   // private String SupervisorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voterlist_details);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        //findViewById(R.id.get_survey_data).setOnClickListener(this);
        //init1();
        //RecyclerView re=((RecyclerView) findViewById(R.id.partyworkers));
        fetchPartyWorkerFromServer();
    }


    private void fetchPartyWorkerFromServer() {
        spinner.setVisibility(View.VISIBLE);
        try {
            final String   partyworkerid=getIntent().getStringExtra("PARTY_WORKER_ID");
            final String   SupervisorId=getIntent().getStringExtra("SUPRVISOR_ID");
            final String   Booth=getIntent().getStringExtra("Booth");
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                    API.GETROLEURL+"VOTERDETAIL&VoterId="+getIntent().getStringExtra("VOTERID"),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            ArrayList<VoterListDetail> voterdetaillist =new ArrayList();
                            Log.w("Constituency ","Response "+ response);
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    VoterListDetail voter = new VoterListDetail(response.getJSONObject(i));
                                        voter.partyworker=partyworkerid;
                                        voter.superVisor=SupervisorId;
                                    voterdetaillist.add(voter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //hidePDialog();

                            if (voterdetaillist.size() > 0) {

                                final VoterListDetailsAdapteroff adapter = new VoterListDetailsAdapteroff(VoterListVDetails.this,voterdetaillist,getApplicationContext(),Booth);
                                ((ListView)findViewById(R.id.Voterlistdetail)).setAdapter(adapter);

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
            RequestFactory.getInstance(this).addToRequestQueue(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*@Override*/
   /* public void onClick(View v) {
        // startActivity(new Intent( SupervisorView.class,));
    }*/
}
