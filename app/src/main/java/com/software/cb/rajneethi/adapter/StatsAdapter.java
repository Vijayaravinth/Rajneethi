package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.models.Partyworkerstats;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.BoothStatsViewHolder> {


    private Context context;
    private ArrayList<Partyworkerstats> partyworkerstatses;

    public StatsAdapter(Context context, ArrayList<Partyworkerstats> partyworkerstatses) {
        this.context = context;
        this.partyworkerstatses = partyworkerstatses;
    }

    @Override
    public StatsAdapter.BoothStatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_booth_stats, parent, false);
        return new BoothStatsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatsAdapter.BoothStatsViewHolder holder, int position) {

        Partyworkerstats object = partyworkerstatses.get(position);

        holder.txt_booth_name.setText(object.getBoothname());
        holder.txt_survey_count.setText(object.getStatscount());
        holder.txt_survey_count.setStrokeColor("#ffffff");
        holder.txt_survey_count.setSolidColor("#9C27B0");
        holder.txt_survey_count.setStrokeWidth(1);

    }

    @Override
    public int getItemCount() {
        return partyworkerstatses.size();
    }

    public class BoothStatsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adapter_txt_booth_name)
        TextView txt_booth_name;

        @BindView(R.id.survey_count)
        CircularTextView txt_survey_count;

        public BoothStatsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}