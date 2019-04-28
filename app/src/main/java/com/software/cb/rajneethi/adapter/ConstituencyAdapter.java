package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.ConstituencyActivity;
import com.software.cb.rajneethi.activity.DownloadActivity;
import com.software.cb.rajneethi.models.ConstituencyDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vijay on 19-03-2017.
 */

public class ConstituencyAdapter extends RecyclerView.Adapter<ConstituencyAdapter.ConstituencyViewHolder> {


    private Context context;
    private ArrayList<ConstituencyDetails> list;
    private ConstituencyActivity activity;

    public ConstituencyAdapter(Context context, ArrayList<ConstituencyDetails> list, ConstituencyActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public ConstituencyAdapter.ConstituencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_constituency, parent, false);
        return new ConstituencyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ConstituencyAdapter.ConstituencyViewHolder holder, int position) {

        final ConstituencyDetails details = list.get(position);
        holder.txt_name.setText(details.getName());
        holder.txt_ward_number.setText(details.getNumber());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DownloadActivity.class).putExtra("id", details.getId()).putExtra("project_id", details.getProject_id()).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ConstituencyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adapter_txt_constituency_name)
        TextView txt_name;

        @BindView(R.id.adapter_cardview_constituency)
        CardView cardview;

        @BindView(R.id.adapter_txt_ward_number)
        TextView txt_ward_number;

        public ConstituencyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
