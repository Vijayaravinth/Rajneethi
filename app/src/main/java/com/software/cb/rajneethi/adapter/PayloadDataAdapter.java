package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.cb.rajneethi.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 07-01-2018.
 */

public class PayloadDataAdapter extends RecyclerView.Adapter<PayloadDataAdapter.PayloadViewHolder> {

    ArrayList<String> list;
    Context context;


    public PayloadDataAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public PayloadDataAdapter.PayloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_payload, parent, false);
        return new PayloadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PayloadDataAdapter.PayloadViewHolder holder, int position) {

        holder.txtPayload.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PayloadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtPayload)
        TextView txtPayload;

        public PayloadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
