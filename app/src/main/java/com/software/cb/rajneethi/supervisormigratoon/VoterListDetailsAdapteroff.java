package com.software.cb.rajneethi.supervisormigratoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoterListDetailsAdapteroff extends BaseAdapter {
    private Activity activity;
    private Context context;
    private String booth;
    private LayoutInflater inflater;
    private List<VoterListDetail> VoterDetaildata;

    public VoterListDetailsAdapteroff(Activity activity, ArrayList partylist,Context context,String booth) {
        this.activity = activity;
        this.VoterDetaildata = partylist;
        this.context=context;
        this.booth=booth;

    }
    @Override
    public int getCount() {
        return VoterDetaildata.size();
    }

    @Override
    public Object getItem(int i) {
        return VoterDetaildata.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.activity_voterlist_details_item, null);
            new ViewHolder(view);
        }

        /*SupervisorPartyWorker c = partyworkerlist.get(i);
        TextView line1 = (TextView) view.findViewById(R.id.partworkerName);
       // TextView line2 = (TextView) view.findViewById(R.id.line2);
        line1.setText(c.getConstituencyNumber() + " : " + c.getConstituencyName());
       // line2.setText(c.getProjectName());*/

        ViewHolder holder = (ViewHolder) view.getTag();
        final VoterListDetail cp = VoterDetaildata.get(i);

        holder.V_Name.setText(cp.getName());
        holder.mo_no.setText(cp.getMobileNo());
       // holder.voter_id.setText(String.valueOf(cp.getVo_id()));
        holder.cardNo.setText(cp.getVoterCardNumber());
        holder.gender.setText(cp.getGender());
        holder.age.setText(cp.getAge());
        holder.houseNo.setText(cp.getHouseNo());
        holder.serialNo.setText(cp.getSerialNo());
        holder.address.setText(cp.getAddressEnglish());
        holder.relation.setText(cp.getRelatedEnglish());
        holder.relationship.setText(cp.getRelationship());
        holder.PolingDetail.setText(cp.getIsvalid());
        holder.Valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.v("fggjdf", cp.toString());
        final String id1=String.valueOf(cp.getVo_id());
                try {
                    JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                            API.GETROLEURL+"UpdateVoterVerificationAttribute&VoterId="+id1+"&Isvalid=1",
                            null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {

                                    Intent intent = new Intent(activity, VoterListV.class);
                                    intent.putExtra("PARTY_WORKER_ID", String.valueOf(cp.getId()));
                                    intent.putExtra("SUPRVISOR_ID",  String.valueOf(cp.getSupervisorId()));
                                    intent.putExtra("BOOTHNAME", String.valueOf(booth));

                                    activity.startActivity(intent);
                                    activity.finish();

                                    //VoterListDetailsAdapteroff.finish();.this.finish();

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return ServerUtils.getCustomHeaders();
                        }
                    };
                    RequestFactory.getInstance(activity).addToRequestQueue(jsObjRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });
        holder.Invalid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String id=String.valueOf(cp.getVo_id());
                Log.v("fggjdf",API.GETROLEURL+"UpdateVoterVerificationAttribute&VoterId="+id+"&Isvalid=0");

                try {
                    JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                            API.GETROLEURL+"UpdateVoterVerificationAttribute&VoterId="+id+"&Isvalid=0",
                            null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Intent intent = new Intent(activity, VoterListV.class);
                                    intent.putExtra("PARTY_WORKER_ID", String.valueOf(cp.getId()));
                                    intent.putExtra("SUPRVISOR_ID",  String.valueOf(cp.getSupervisorId()));
                                    intent.putExtra("BOOTHNAME", String.valueOf(booth));

                                    activity.startActivity(intent);
                                    activity.finish();
                                    //activity.finish();
                                   /* Intent intent = new Intent(activity,VoterListVDetails.class);
                                    intent.putExtra("VOTERID", id);
                                    activity.startActivity(intent);*/
                                   /* activity.finish();
                                    activity.startActivity(activity.getIntent());*/
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return ServerUtils.getCustomHeaders();
                        }
                    };
                    RequestFactory.getInstance(activity).addToRequestQueue(jsObjRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.mo_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String id1 = String.valueOf(cp.getMobileNo());
                try {
                  Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+id1));
                    activity.startActivity(intent);
                } catch (Exception  e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    class ViewHolder{

        final TextView V_Name;
        final TextView mo_no;
      //  final TextView voter_id;
        final TextView cardNo;
        final TextView gender;
        final TextView age;
        final TextView houseNo;
        final TextView serialNo;
        final TextView address;
        final TextView relation;
        final TextView relationship;
        final TextView PolingDetail;
        final Button Valid;
        final Button Invalid;

        public ViewHolder(View v){
            V_Name = (TextView) v.findViewById(R.id.name);
            mo_no = (TextView) v.findViewById(R.id.mo_no);
           // voter_id = (TextView) v.findViewById(R.id.voterId);
            cardNo = (TextView) v.findViewById(R.id.cardNo);
            gender = (TextView) v.findViewById(R.id.gender);
            age = (TextView) v.findViewById(R.id.age);
            houseNo = (TextView) v.findViewById(R.id.houseNo);
            serialNo = (TextView) v.findViewById(R.id.serialNo);
            address = (TextView) v.findViewById(R.id.address);
            relation = (TextView) v.findViewById(R.id.relation);
            relationship = (TextView) v.findViewById(R.id.relationship);
            PolingDetail = (TextView) v.findViewById(R.id.hierarchy);
            Valid = (Button) v.findViewById(R.id.valid);
            Invalid = (Button) v.findViewById(R.id.Invalid);
            v.setTag(this);
        }
    }
}