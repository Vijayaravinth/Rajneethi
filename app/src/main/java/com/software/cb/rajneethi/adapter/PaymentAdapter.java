package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.PaymentActivity;
import com.software.cb.rajneethi.models.MainMenuDetails;
import com.software.cb.rajneethi.models.PaymentDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    Context context;
    ArrayList<PaymentDetails> list;
    PaymentActivity activity;

    public PaymentAdapter(Context context, ArrayList<PaymentDetails> list, PaymentActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PaymentAdapter.PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_adapter,parent,false);
        return new PaymentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.PaymentViewHolder holder, int i) {

        PaymentDetails details = list.get(i);
        holder.txtMenuName.setText(details.getTitle());
        holder.imgMenu.setImageResource(details.getId());

        holder.txtUnit.setText(details.getUnit());
        holder.txtPrice.setText(details.getPrice());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txtMenuName)
        TextView txtMenuName;
        @BindView(R.id.imgMenu)
        ImageView imgMenu;

        @BindView(R.id.txtUnit)
        TextView txtUnit;
        @BindView(R.id.txtPrice)
        TextView txtPrice;

        @BindView(R.id.cardview)
        CardView cardview;


        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
