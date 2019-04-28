package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.GrievanceActivity;
import com.software.cb.rajneethi.models.GrievanceDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GrievanceAdapater extends RecyclerView.Adapter<GrievanceAdapater.GrievanceViewHolder> {

    private Context context;
    private ArrayList<GrievanceDetails> list;
    private GrievanceActivity activity;

    public GrievanceAdapater(Context context, ArrayList<GrievanceDetails> list,GrievanceActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public GrievanceAdapater.GrievanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fund_greiavance, parent, false);
        return new GrievanceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GrievanceAdapater.GrievanceViewHolder holder, int i) {


        final int position = i;
        GrievanceDetails details = list.get(i);
        holder.txtTitle.setText(details.getTitle());
        holder.txtDate.setText(details.getDate());
        holder.txtCost.setText(details.getName());

        if (details.isIisSelected()){
            holder.imgDelete.setVisibility(View.GONE);
            holder.imgEdit.setVisibility(View.GONE);
        }else{
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgEdit.setVisibility(View.VISIBLE);
        }


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showGrievanceDetails(details,position,false);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.delete(position,details.getId());
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showGrievanceDetails(details,position,true);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GrievanceViewHolder  extends  RecyclerView.ViewHolder{


        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.txtDate)
        TextView txtDate;
        @BindView(R.id.txtCost)
        TextView txtCost;

        @BindView(R.id.viewLayout)
        LinearLayout viewLayout;

        @BindView(R.id.imgDelete)
        ImageView imgDelete;
        @BindView(R.id.imgEdit)
        ImageView imgEdit;

        @BindView(R.id.layout)
        RelativeLayout layout;

        public GrievanceViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
