package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 12-01-2018.
 */

public class BoothInfoAdapter extends RecyclerView.Adapter<BoothInfoAdapter.BoothInfoViewHolder> {

    ArrayList<String> list;
    Context context;

    public BoothInfoAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public BoothInfoAdapter.BoothInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_allocated_booth, parent, false);
        return new BoothInfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BoothInfoAdapter.BoothInfoViewHolder holder, int position) {
       // holder.img_select.setVisibility(View.VISIBLE);
        holder.txt_booth_name.setText(list.get(position));
      //  holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
        holder.txt_booth_name.setTextSize(14);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BoothInfoViewHolder extends RecyclerView.ViewHolder {



        @BindView(R.id.txt_options)
        TextView txt_booth_name;
        @BindView(R.id.option_layout)
        RelativeLayout layout;


        public BoothInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
