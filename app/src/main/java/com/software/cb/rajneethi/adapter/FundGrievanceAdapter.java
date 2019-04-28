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
import com.software.cb.rajneethi.activity.FundManagmentActivity;
import com.software.cb.rajneethi.models.FundDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FundGrievanceAdapter extends RecyclerView.Adapter<FundGrievanceAdapter.FundViewHolder> {

    ArrayList<FundDetails> list;
    private Context context;
    private FundManagmentActivity activity;

    public FundGrievanceAdapter(ArrayList<FundDetails> list, Context context, FundManagmentActivity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FundGrievanceAdapter.FundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fund_greiavance, parent, false);
        return new FundViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FundGrievanceAdapter.FundViewHolder holder, int i) {

        final int position = i;
        FundDetails details = list.get(i);

        if (activity.title.equalsIgnoreCase(activity.fundManagement)) {

            holder.txtTitle.setText(details.getTitle());
            holder.txtDate.setText(details.getStartDate());
            holder.txtCost.setText("Rs." + details.getCost());


        } else if (activity.title.equals(activity.beneficiaryManagment)){

            holder.txtTitle.setText(details.getBeneficiaryTitle());
            holder.txtDate.setText(details.getBeneficiaryName());
            holder.txtCost.setText(details.getBeneficiaryDate());
        }

        if (details.isSelected()){
            holder.imgDelete.setVisibility(View.GONE);
            holder.imgEdit.setVisibility(View.GONE);
        }else{
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgEdit.setVisibility(View.VISIBLE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.title.equalsIgnoreCase(activity.fundManagement)) {
                    activity.showFundData(details,false, position);
                }else if(activity.title.equalsIgnoreCase(activity.beneficiaryManagment)){
                    activity.showBeneficiaryData(details, false,position);
                }
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
                if (activity.title.equalsIgnoreCase(activity.fundManagement)) {
                    activity.showFundData(details,true,position);
                }else if(activity.title.equalsIgnoreCase(activity.beneficiaryManagment)){
                    activity.showBeneficiaryData(details,false, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FundViewHolder extends RecyclerView.ViewHolder {


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

        public FundViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
