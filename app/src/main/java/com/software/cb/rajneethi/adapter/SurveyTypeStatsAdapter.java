package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.models.SurveyStats;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 12/7/2017.
 */

public class SurveyTypeStatsAdapter extends RecyclerView.Adapter<SurveyTypeStatsAdapter.SurveyViewHolder> {

    private ArrayList<SurveyStats> list;
    private Context context;

    public SurveyTypeStatsAdapter(ArrayList<SurveyStats> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public SurveyTypeStatsAdapter.SurveyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_booth_stats, parent, false);

        return new SurveyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SurveyTypeStatsAdapter.SurveyViewHolder holder, int position) {

        SurveyStats details = list.get(position);

        holder.txt_booth_name.setTextColor(ContextCompat.getColor(context, R.color.icons));
        holder.txt_survey_count.setStrokeColor("#60847f");
        holder.txt_survey_count.setSolidColor("#880E4F");
        holder.txt_survey_count.setStrokeWidth(1);
        holder.txt_survey_count.setText(details.getCount());
        holder.txt_booth_name.setText(details.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.adapter_txt_booth_name)
        TextView txt_booth_name;

        @BindView(R.id.survey_count)
        CircularTextView txt_survey_count;

        @BindView(R.id.layout)
        RelativeLayout layout;

        public SurveyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
