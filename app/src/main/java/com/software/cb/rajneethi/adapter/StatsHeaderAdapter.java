package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.ListenToPeopleActivity;
import com.software.cb.rajneethi.activity.SurveyStatsActivity;
import com.software.cb.rajneethi.models.AudioFileDetails;
import com.software.cb.rajneethi.models.SurveyStats;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by DELL on 29-03-2018.
 */

public class StatsHeaderAdapter extends RecyclerView.Adapter<StatsHeaderAdapter.StatsHeaderViewHolder> {


    public SurveyStatsClientAdapter statsAdapter;

    Context context;
    ArrayList<SurveyStats> list;
    AppCompatActivity activity;

    public StatsHeaderAdapter(Context context, ArrayList<SurveyStats> list, AppCompatActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public StatsHeaderAdapter.StatsHeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_stats_header, parent, false);
        return new StatsHeaderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatsHeaderAdapter.StatsHeaderViewHolder holder, int position) {


        final int pos = position;
        final SurveyStats details = list.get(pos);

        holder.txtPwname.setText(details.getName());

        if (details.isExpanded()) {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.option_background1));
            holder.txtPwname.setTextColor(ContextCompat.getColor(context, R.color.icons));
        } else {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
            holder.txtPwname.setTextColor(ContextCompat.getColor(context, R.color.option_background1));
        }




       /* if (details.isExpanded()) {

            Log.w("Adapter", "Is expanded : " + details.isExpanded());
            holder.imgExpand.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_expand_less_white_24dp));
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.option_background1));
            if (activity instanceof ListenToPeopleActivity) {
                ArrayList<AudioFileDetails> audioList = ((ListenToPeopleActivity) activity).loadData(details.getName());
                if (audioList.size() > 0) {
                    setLayoutManager(holder.recyclerView);
                    setAdapter(holder.recyclerView, audioList, ((ListenToPeopleActivity) activity));
                }

            } else if(activity instanceof SurveyStatsActivity) {

                ArrayList<SurveyStats> statsList = ((SurveyStatsActivity) activity).loadData(details.getName());
                if (statsList.size() > 0) {
                    setLayoutManager(holder.recyclerView);
                    setAdapterForStats(holder.recyclerView, statsList, ((SurveyStatsActivity) activity));
                    // setAdapter(holder.recyclerView, audioList, ((ListenToPeopleActivity) activity));
                }
            }
        } else {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
            holder.imgExpand.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_expand_more_white_24dp));
            holder.recyclerView.setVisibility(View.GONE);
            holder.recyclerView.setAdapter(null);
        }*/


        holder.txtPwname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity instanceof ListenToPeopleActivity) {
                    if (details.isExpanded()) {
                        ((ListenToPeopleActivity) activity).hideAudioLayout();
                        ((ListenToPeopleActivity) activity).disableExpand(pos);
                    } else {
                        ((ListenToPeopleActivity) activity).hideAudioLayout();
                        ((ListenToPeopleActivity) activity).expandData(pos);
                        ((ListenToPeopleActivity) activity).loadData(details.getName());
                    }
                } else if (activity instanceof SurveyStatsActivity) {
                    if (details.isExpanded()) {
                        ((SurveyStatsActivity) activity).disableExpand(pos);
                    } else {
                        ((SurveyStatsActivity) activity).expandData(pos);
                        ((SurveyStatsActivity) activity).loadData(details.getName());
                    }
                }
            }
        });

    }


    private void setAdapterForStats(RecyclerView recyclerView, ArrayList<SurveyStats> list, SurveyStatsActivity activity) {
        statsAdapter = new SurveyStatsClientAdapter(context, list, activity);
        recyclerView.setAdapter(statsAdapter);
        activity.dismiss();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class StatsHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtPwName)
        TextView txtPwname;


        @BindView(R.id.adapterLayout)
        RelativeLayout layout;


        public StatsHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
