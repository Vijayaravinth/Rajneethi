package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.SurveyStatsActivity;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.models.SurveyStats;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 21-01-2018.
 */

public class SurveyStatsDetailsClientAdapter extends RecyclerView.Adapter<SurveyStatsDetailsClientAdapter.ClientViewHolder> {

    private Context context;
    private ArrayList<SurveyStats> list;
    private SurveyStatsActivity activity;

    public SurveyStatsDetailsClientAdapter(Context context, ArrayList<SurveyStats> list, SurveyStatsActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public SurveyStatsDetailsClientAdapter.ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_stats_item,parent,false);
        return new ClientViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SurveyStatsDetailsClientAdapter.ClientViewHolder holder, int position) {

        final int pos = position;
        final SurveyStats stats = list.get(position);
        holder.txtName.setText(stats.getRepondantName());
        holder.txtMobile.setText(stats.getMobile());
        holder.circulrText.setSolidColor("#128c7e");
        holder.circulrText.setStrokeWidth(1);
        holder.circulrText.setStrokeColor("#ffffff");
        holder.circulrText.setText(stats.getRepondantName().substring(0, 1));

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.call(stats.getMobile());
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showPayLoadData(stats, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ClientViewHolder extends RecyclerView.ViewHolder{
        
        @BindView(R.id.circular_text)
        CircularTextView circulrText;
        @BindView(R.id.adapter_txt_name)
        TextView txtName;
        @BindView(R.id.adapter_txt_mobile)
        TextView txtMobile;

        @BindView(R.id.img_call)
        ImageView imgCall;

        @BindView(R.id.user_layout)
        RelativeLayout layout;
        public ClientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
