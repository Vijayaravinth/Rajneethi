package com.software.cb.rajneethi.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MVIJAYAR on 30-05-2017.
 */

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {


    private ArrayList<GraphDetails> fields;
    private Context context;
    private int[] colors;
    private Activity activity;

    public ChartAdapter(ArrayList<GraphDetails> fields, Context context, Activity activity) {
        this.fields = fields;
        this.context = context;
        this.activity = activity;

    }

    @Override
    public ChartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chart, parent, false);
        return new ChartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChartViewHolder holder, int position) {

        GraphDetails details = fields.get(position);
        holder.btn_color.setBackgroundColor(details.getColor());


        double percentage;

        int total = 0;

        if (activity instanceof  ConstituencySummaryActivity){
            total = ((ConstituencySummaryActivity)activity).total;
        }else if (activity instanceof GOTVActivity){
            total = ((GOTVActivity)activity).total;
        }else{
            total = ((GOTVStatistics)activity).total;
        }

        try {
            percentage = (Double.parseDouble(details.getValue()) * 100) / total;

        } catch (Exception e) {
            e.printStackTrace();
            percentage = Double.parseDouble(details.getValue());
        }

        // Math.floor(percentage);
        DecimalFormat df = new DecimalFormat("#.##");
        String value = details.getTitle() + " - " + df.format(percentage) + " %";
        holder.txt_caste.setText(value);

    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public class ChartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_color)
        Button btn_color;
        @BindView(R.id.txt_caste)
        TextView txt_caste;


        public ChartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
