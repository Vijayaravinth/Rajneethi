package com.software.cb.rajneethi.supervisormigratoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.StatsActivity;
import com.software.cb.rajneethi.supervisormigratoon.pojo.SupervisorPartyWorker;

import java.util.ArrayList;
import java.util.List;

public class PartyworkersAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<SupervisorPartyWorker> partyworkerlist;

    public PartyworkersAdapter(Activity activity, ArrayList partylist) {
        this.activity = activity;
        this.partyworkerlist = partylist;
    }

    @Override
    public int getCount() {
        return partyworkerlist.size();
    }

    @Override
    public Object getItem(int i) {
        return partyworkerlist.get(i);
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
            view = inflater.inflate(R.layout.activity_party_workers_item, null);
            new ViewHolder(view);
        }

        /*SupervisorPartyWorker c = partyworkerlist.get(i);
        TextView line1 = (TextView) view.findViewById(R.id.partworkerName);
       // TextView line2 = (TextView) view.findViewById(R.id.line2);
        line1.setText(c.getConstituencyNumber() + " : " + c.getConstituencyName());
       // line2.setText(c.getProjectName());*/

        ViewHolder holder = (ViewHolder) view.getTag();
        final SupervisorPartyWorker cp = partyworkerlist.get(i);

        holder.Name.setText(cp.getName());
        holder.Role.setText(cp.getRole());

        holder.Name.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(final View view) {
                Log.v("fggjdf",cp.toString());
                //activity.startActivity(Intent.parseUri(url,Intent.FLAG_ACTIVITY_NEW_TASK));
               // activity.startActivity(new Intent(activity,VoterListV.class));
                Intent intent = new Intent(activity, StatsActivity.class);
                intent.putExtra("PARTY_WORKER_ID", String.valueOf(cp.getId()));
                intent.putExtra("SUPRVISOR_ID",  String.valueOf(cp.getSupervisorId()));

                activity.startActivity(intent);
                activity.finish();

            }


        });
        return view;
    }
    class ViewHolder{
        final TextView Name;
        final TextView Role;
        public ViewHolder(View v){
            Name = (TextView) v.findViewById(R.id.partworkerName);

            Role = (TextView) v.findViewById(R.id.partworkerRole);
            v.setTag(this);
        }
    }
}