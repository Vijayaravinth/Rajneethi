/**
 * Created by admin on 4/8/2017.
 */

package com.software.cb.rajneethi.supervisormigratoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.software.cb.rajneethi.R;

import java.util.ArrayList;
import java.util.List;

public class BoothListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<String> Boothlist;
    private String partyId;
    private String superviId;

    public BoothListAdapter(Activity activity, ArrayList partylist,String Partyworker,String Supervisor) {
        this.activity = activity;
        this.Boothlist = partylist;
        this.partyId=Partyworker;
        this.superviId=Supervisor;

    }

    @Override
    public int getCount() {
        return Boothlist.size();
    }

    @Override
    public Object getItem(int i) {
        return Boothlist.get(i);
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
            view = inflater.inflate(R.layout.activity_partyworkerbooth, null);
            new ViewHolder(view);
        }

        /*SupervisorPartyWorker c = partyworkerlist.get(i);
        TextView line1 = (TextView) view.findViewById(R.id.partworkerName);
       // TextView line2 = (TextView) view.findViewById(R.id.line2);
        line1.setText(c.getConstituencyNumber() + " : " + c.getConstituencyName());
       // line2.setText(c.getProjectName());*/

        ViewHolder holder = (ViewHolder) view.getTag();
        final String cp = Boothlist.get(i);

        holder.V_Name.setText(cp);
        /*holder.mo_no.setText(cp.getMobileNo());
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
        }*/
        holder.V_Name.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(final View view) {
                //Log.v("fggjdf",cp.toString());
                //activity.startActivity(Intent.parseUri(url,Intent.FLAG_ACTIVITY_NEW_TASK));
               // activity.startActivity(new Intent(activity,VoterListVDetails.class));
                Intent intent = new Intent(activity,VoterListV.class);
                intent.putExtra("PARTY_WORKER_ID", String.valueOf(partyId));
                intent.putExtra("SUPRVISOR_ID",  String.valueOf(superviId));
                //intent.putExtra("VOTERID", String.valueOf(cp.getVo_id()));
                intent.putExtra("BOOTHNAME", String.valueOf(((AppCompatTextView) view).getText()));


                activity.startActivity(intent);
                activity.finish();


            }


        });
        return view;
    }


    class ViewHolder{

        final TextView V_Name;
       /* final TextView mo_no;
       final ImageView is_valid;
        final LinearLayout layoutid;*/

        public ViewHolder(View v){
            V_Name = (TextView) v.findViewById(R.id.BoothName);
          /*  mo_no = (TextView) v.findViewById(R.id.mobileNo);
            is_valid = (ImageView) v.findViewById(R.id.taskImage);
            layoutid = (LinearLayout) v.findViewById(R.id.listitemVoter);*/

            v.setTag(this);
        }
    }
}