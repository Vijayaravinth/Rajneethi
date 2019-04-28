package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.models.UserStatistics;

import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {

    ArrayList<UserStatistics> list;
    private Context context;

    public StatisticsAdapter(ArrayList<UserStatistics> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public StatisticsAdapter.StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_statistics, viewGroup, false);
        return new StatisticsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.StatisticsViewHolder holder, int i) {

        UserStatistics details = list.get(i);
        holder.txtRejected.setText(details.getRejected());
        holder.txtVerified.setText(details.getVerified());
        holder.txtUnverified.setText(details.getUnverified());

        try {
            int total = Integer.parseInt(details.getRejected()) + Integer.parseInt(details.getUnverified()) + Integer.parseInt(details.getVerified());
            String totalcount = total + "";
            holder.txtTotal.setText(totalcount);
        } catch (NumberFormatException e) {
            holder.txtTotal.setText(details.getTotal());
        }
        holder.txtBoothName.setText(details.getBoothName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class StatisticsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtRejected)
        TextView txtRejected;
        @BindView(R.id.txtUnVerifiedCount)
        TextView txtUnverified;
        @BindView(R.id.txtVerifiedCount)
        TextView txtVerified;
        @BindView(R.id.txtTotalCount)
        TextView txtTotal;

        @BindView(R.id.txtBoothName)
        TextView txtBoothName;

        public StatisticsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
