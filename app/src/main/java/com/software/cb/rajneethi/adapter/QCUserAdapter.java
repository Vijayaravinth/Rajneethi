package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.ListenToPeopleActivity;
import com.software.cb.rajneethi.activity.SurveyStatsActivity;
import com.software.cb.rajneethi.activity.TrackSurveyActivity;
import com.software.cb.rajneethi.models.QCUsers;
import com.software.cb.rajneethi.qc.QCUsersActivity;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 03-04-2018.
 */

public class QCUserAdapter extends RecyclerView.Adapter<QCUserAdapter.QCUserViewHolder> {

    /*generate random color*/
    public String randomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        return String.format("#%02x%02x%02x", r, g, b);
        //  return Color.rgb(r, g, b);
    }

    private Context context;
    private ArrayList<String> list;
    private QCUsersActivity activity;

    public QCUserAdapter(Context context, ArrayList<String> list, QCUsersActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }


    @Override
    public QCUserAdapter.QCUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.qc_user_adapter, parent, false);
        return new QCUserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(QCUserAdapter.QCUserViewHolder holder, int position) {

        String users = list.get(position);
        holder.txtUserName.setText(users);

        String color = randomColor();


        holder.layout.setBackgroundColor(Color.parseColor(color));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showStatistics(users);
            }
        });

       /* holder.circularTextView.setStrokeWidth(1);
        holder.circularTextView.setStrokeColor("#ffffff");
        holder.circularTextView.setSolidColor(color);*/


       /* holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, QCUserAudioActivity.class).putExtra("name", users.getUserName()));
            }
        });*/

        holder.imgAudioFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // context.startActivity(new Intent(context, ListenToPeopleActivity.class).putExtra("isBoothWise",true).putExtra("boothName",users.getUserName()));
            }
        });

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  context.startActivity(new Intent(context, SurveyStatsActivity.class).putExtra("isBoothWise",true).putExtra("boothName",users.getUserName()));
            }
        });

        holder.imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  context.startActivity(new Intent(context, TrackSurveyActivity.class).putExtra("isBoothWise",true).putExtra("boothName",users.getUserName()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class QCUserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adapter_username)
        TextView txtUserName;
        @BindView(R.id.user_layout)
        RelativeLayout layout;


        @BindView(R.id.imgAudioFiles)
        ImageView imgAudioFiles;

        @BindView(R.id.imgCall)
        ImageView imgCall;
        @BindView(R.id.imgMap)
        ImageView imgMap;

        public QCUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
