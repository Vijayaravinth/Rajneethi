package com.software.cb.rajneethi.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.ConstituencySummaryActivity;
import com.software.cb.rajneethi.activity.GOTVActivity;
import com.software.cb.rajneethi.activity.GOTVStatistics;
import com.software.cb.rajneethi.models.GraphDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 13-04-2018.
 */

public class GotvStatisticsAdapter extends RecyclerView.Adapter<GotvStatisticsAdapter.GotvViewHolder> {
    private ArrayList<GraphDetails> fields;
    private Context context;
    private Activity activity;

    public GotvStatisticsAdapter(ArrayList<GraphDetails> fields, Context context, Activity activity) {
        this.fields = fields;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public GotvStatisticsAdapter.GotvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chart, parent, false);

        return new GotvViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GotvStatisticsAdapter.GotvViewHolder holder, int position) {
        GraphDetails details = fields.get(position);
        holder.btn_color.setBackgroundColor(details.getColor());
        String value = details.getTitle() + " - " + details.getValue();
        holder.txt_caste.setText(value);
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public class GotvViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_color)
        Button btn_color;
        @BindView(R.id.txt_caste)
        TextView txt_caste;


        public GotvViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
