package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.SurveyStatsActivity;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.models.SurveyStats;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 12/7/2017.
 */

public class SurveyStatsAdapter extends RecyclerView.Adapter<SurveyStatsAdapter.SurveyStatsViewHolder> {


    private Context context;
    private ArrayList<SurveyStats> list;
    private SurveyStatsActivity activity;

    public SurveyStatsAdapter(Context context, ArrayList<SurveyStats> list, SurveyStatsActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public SurveyStatsAdapter.SurveyStatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_survey_stats, parent, false);
        return new SurveyStatsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SurveyStatsAdapter.SurveyStatsViewHolder holder, int position) {


        final SurveyStats details = list.get(position);

        final int pos = position;

        holder.txtBoothName.setText(details.getName());
        holder.txtSurveyCount.setStrokeColor("#ffffff");
        holder.txtSurveyCount.setSolidColor("#9C27B0");
        holder.txtSurveyCount.setStrokeWidth(1);
        holder.txtSurveyCount.setText(details.getCount());

        if (details.isExpanded()) {
            //  holder.recyclerView.setAdapter(null);
            holder.imgExpand.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_expand_less_white_24dp));
            holder.cardview.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.VISIBLE);
            ArrayList<SurveyStats> list = activity.getSurveyTypeStats(details.getName());
            if (list.size() > 0) {
                holder.recyclerView.setAdapter(null);
                setLayoutManager(holder.recyclerView);
                setAdapter(holder.recyclerView, list);
            }

        } else {
            holder.imgExpand.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_expand_more_white_24dp));
            holder.cardview.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
            holder.recyclerView.setAdapter(null);
            // holder.recyclerView.setAdapter(null);
        }


        holder.imgExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (details.isExpanded()) {
                    activity.disableExpandForPartyWorkers(pos);
                } else {
                    activity.expandForPartyWorkers(pos);
                }
            }
        });

    }


    private void setLayoutManager(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setAdapter(RecyclerView recyclerView, ArrayList<SurveyStats> list) {
        SurveyTypeStatsAdapter adapter = new SurveyTypeStatsAdapter(list, context);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new dividerLineForRecyclerView(context));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SurveyStatsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtBoothName)
        TextView txtBoothName;

        @BindView(R.id.txt_survey_count)
        CircularTextView txtSurveyCount;

        @BindView(R.id.imgExpand)
        ImageView imgExpand;

        @BindView(R.id.cardview)
        CardView cardview;

        @BindView(R.id.surveyTypeRecyclerview)
        RecyclerView recyclerView;

        public SurveyStatsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
