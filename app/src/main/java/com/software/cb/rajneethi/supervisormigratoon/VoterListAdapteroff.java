/**
 * Created by admin on 4/8/2017.
 */

package com.software.cb.rajneethi.supervisormigratoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.supervisormigratoon.pojo.Voterlist;

import java.util.ArrayList;
import java.util.List;

public class VoterListAdapteroff extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Voterlist> Voterlistdata;
    private String Booth;

    public VoterListAdapteroff(Activity activity, ArrayList partylist,String Booth) {
        this.activity = activity;
        this.Voterlistdata = partylist;
        this.Booth=Booth;
    }

    @Override
    public int getCount() {
        return Voterlistdata.size();
    }

    @Override
    public Object getItem(int i) {
        return Voterlistdata.get(i);
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
            view = inflater.inflate(R.layout.activity_voterlist_item, null);
            new ViewHolder(view);
        }

        /*SupervisorPartyWorker c = partyworkerlist.get(i);
        TextView line1 = (TextView) view.findViewById(R.id.partworkerName);
       // TextView line2 = (TextView) view.findViewById(R.id.line2);
        line1.setText(c.getConstituencyNumber() + " : " + c.getConstituencyName());
       // line2.setText(c.getProjectName());*/

        ViewHolder holder = (ViewHolder) view.getTag();
        final Voterlist cp = Voterlistdata.get(i);

        holder.V_Name.setText(cp.getName());
        holder.mo_no.setText(cp.getMobileNo());
        if(cp.getIsvalid().equals("0"))
        {
            view.findViewById(R.id.taskImage).setVisibility(View.GONE);
            view.findViewById(R.id.taskImage2).setVisibility(View.VISIBLE);
            view.findViewById(R.id.taskImage1).setVisibility(View.GONE);
        }
        if(cp.getIsvalid().equals("1"))
        {
            view.findViewById(R.id.taskImage).setVisibility(View.GONE);
            view.findViewById(R.id.taskImage1).setVisibility(View.VISIBLE);
            view.findViewById(R.id.taskImage2).setVisibility(View.GONE);
        }
        holder.layoutid.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(final View view) {
                Log.v("fggjdf",cp.toString());
                //activity.startActivity(Intent.parseUri(url,Intent.FLAG_ACTIVITY_NEW_TASK));
               // activity.startActivity(new Intent(activity,VoterListVDetails.class));
                Intent intent = new Intent(activity,VoterListVDetails.class);
                intent.putExtra("PARTY_WORKER_ID", String.valueOf(cp.getId()));
                intent.putExtra("SUPRVISOR_ID",  String.valueOf(cp.getSupervisorId()));
                intent.putExtra("VOTERID", String.valueOf(cp.getVo_id()));
                intent.putExtra("Booth", String.valueOf(Booth));


                activity.startActivity(intent);
                activity.finish();


            }


        });
        return view;
    }


    class ViewHolder{

        final TextView V_Name;
        final TextView mo_no;
       final ImageView is_valid;
        final LinearLayout layoutid;

        public ViewHolder(View v){
            V_Name = (TextView) v.findViewById(R.id.partworkerName);
            mo_no = (TextView) v.findViewById(R.id.mobileNo);
            is_valid = (ImageView) v.findViewById(R.id.taskImage);
            layoutid = (LinearLayout) v.findViewById(R.id.listitemVoter);

            v.setTag(this);
        }
    }
}