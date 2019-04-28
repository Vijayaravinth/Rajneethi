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
import com.software.cb.rajneethi.activity.BoothSelectionActivity;
import com.software.cb.rajneethi.models.BoothDetails;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 10/8/2017.
 */

public class BoothAdapter extends RecyclerView.Adapter<BoothAdapter.BoothViewHolder> {


    private Context context;
    private ArrayList<BoothDetails> list;
    private BoothSelectionActivity activity;


    public BoothAdapter(Context context, ArrayList<BoothDetails> list, BoothSelectionActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public BoothAdapter.BoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_booth_selection, parent, false);
        return new BoothViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BoothAdapter.BoothViewHolder holder, int position) {

        final int pos = position;
        final BoothDetails obj = list.get(position);

        if (obj.is_selected()) {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.green1));
            holder.img_select.setVisibility(View.VISIBLE);
            holder.txt_booth_name.setTextColor(ContextCompat.getColor(context,R.color.icons));
        } else {
            holder.img_select.setVisibility(View.INVISIBLE);

            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
            holder.txt_booth_name.setTextColor(ContextCompat.getColor(context,R.color.primary));
        }

        holder.txt_booth_name.setText(obj.getBooth_name());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!obj.is_selected()) {
                    //activity.select_booth(pos);
                    activity.allocate_alert_dialog(pos, obj.getBooth_name(),context.getResources().getString(R.string.allocate),true, context.getResources().getString(R.string.allocateBooth, context.getResources().getString(R.string.allocate)));
                } else {
                    activity.allocate_alert_dialog(pos, obj.getBooth_name(),context.getResources().getString(R.string.deallocate), false,context.getResources().getString(R.string.deallocateBooth, context.getResources().getString(R.string.deallocate)));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BoothViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_select)
        ImageView img_select;

        @BindView(R.id.txt_options)
        TextView txt_booth_name;

        @BindView(R.id.option_layout)
        RelativeLayout layout;

        @BindString(R.string.allocateBooth)
        String allocateBooth;
        @BindString(R.string.deallocateBooth)
        String deallocateBooth;


        public BoothViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
