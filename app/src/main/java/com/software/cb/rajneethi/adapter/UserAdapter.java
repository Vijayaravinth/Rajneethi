package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.BoothSelectionActivity;
import com.software.cb.rajneethi.activity.StatsActivity;
import com.software.cb.rajneethi.activity.UserManagementActivity;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.supervisormigratoon.pojo.SupervisorPartyWorker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 10/7/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<SupervisorPartyWorker> partyWorkerslist;
    private Context context;
    private UserManagementActivity activity;

    public UserAdapter(ArrayList<SupervisorPartyWorker> partyWorkerslist, Context context, UserManagementActivity activity) {
        this.partyWorkerslist = partyWorkerslist;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserAdapter.UserViewHolder holder, int position) {


        final int pos = position;
        final SupervisorPartyWorker obj = partyWorkerslist.get(position);

        //String nameWithCount = obj.getName() + " - " + obj.getCount();


        if (obj.getRole().equalsIgnoreCase("Party Worker")) {

            if (activity.getRole().equalsIgnoreCase("Client") || activity.getRole().equalsIgnoreCase("Manager")) {
                String username = "Created by -" + activity.getSupervioserName(obj.getSupervisorId() + "");
                holder.txt_role.setText(username);
            } else {
                holder.txt_role.setText(obj.getRole());
            }
            holder.txt_username.setText(obj.getName());
        } else {
            holder.txt_username.setText(obj.getName());
            holder.txt_role.setText(obj.getRole());
        }

        holder.circularTextView.setStrokeWidth(1);
        holder.circularTextView.setStrokeColor("#ffffff");

        holder.txtSurveyCount.setStrokeWidth(1);
        holder.txtSurveyCount.setStrokeColor("#ffffff");

        String role = obj.getRole().substring(0, 1);
        if (obj.getRole().trim().contains(" ")) {
            Log.w("Adapter", obj.getRole().substring(obj.getRole().indexOf(" ") + 1, obj.getRole().indexOf(" ") + 2));
            role += obj.getRole().substring(obj.getRole().indexOf(" ") + 1, obj.getRole().indexOf(" ") + 2);
        }

        if (obj.getRole().equals("Manager")) {
            holder.circularTextView.setSolidColor("#d6060c");
            holder.txtSurveyCount.setSolidColor("#d6060c");
            holder.txt_username.setTextColor(Color.parseColor("#d6060c"));
            holder.txt_role.setTextColor(Color.parseColor("#d6060c"));
        } else if (obj.getRole().equals("Supervisor")) {
            holder.circularTextView.setSolidColor("#128c7e");
            holder.txtSurveyCount.setSolidColor("#128c7e");
            holder.txt_username.setTextColor(Color.parseColor("#128c7e"));
            holder.txt_role.setTextColor(Color.parseColor("#128c7e"));
        } else if (obj.getRole().equals("Party Worker")) {
            holder.txtSurveyCount.setSolidColor("#9C27B0");
            holder.circularTextView.setSolidColor("#9C27B0");
            holder.txt_username.setTextColor(Color.parseColor("#9C27B0"));
            holder.txt_role.setTextColor(Color.parseColor("#9C27B0"));
        } else {
            holder.txtSurveyCount.setSolidColor("#9C27B0");
            holder.circularTextView.setSolidColor("#9C27B0");
            holder.txt_username.setTextColor(Color.parseColor("#9C27B0"));
            holder.txt_role.setTextColor(Color.parseColor("#9C27B0"));
        }

        holder.circularTextView.setText(role);
        holder.txtSurveyCount.setText(obj.getCount());

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, BoothSelectionActivity.class).putExtra("userid", obj.getId() + ""));
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  activity.alertForSendMessage(obj.getMobile());
                Log.w("Adapter", "Mobile number : " + obj.getMobile());
               /* Intent intent = new Intent(context, StatsActivity.class);
                intent.putExtra("PARTY_WORKER_ID", String.valueOf(obj.getId()));
                intent.putExtra("SUPRVISOR_ID", String.valueOf(obj.getSupervisorId()));
                intent.putExtra("username",obj.getName());
                activity.startActivity(intent);*/

            }
        });

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.w("Adapter ", "id: adapter " + obj.getId());
                activity.delete_user(pos, obj.getId() + "");

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return partyWorkerslist.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adapter_username)
        TextView txt_username;
        @BindView(R.id.adapter_user_role)
        TextView txt_role;
        @BindView(R.id.img_user_delete)
        ImageView img_delete;

        @BindView(R.id.user_layout)
        RelativeLayout layout;

        @BindView(R.id.txt_survey_count)
        CircularTextView txtSurveyCount;

        @BindView(R.id.circular_text)
        CircularTextView circularTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
