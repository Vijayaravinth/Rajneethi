package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.OpinionPollActivity;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.models.MainMenuDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MVIJAYAR on 07-07-2017.
 */

public class OpinionPollAdapter extends RecyclerView.Adapter<OpinionPollAdapter.OpinionViewHolder> {

    private Context context;
    private OpinionPollActivity activity;

    private ArrayList<MainMenuDetails> list;

    public OpinionPollAdapter(Context context, ArrayList<MainMenuDetails> list, OpinionPollActivity activity) {
        this.context = context;
        this.activity = activity;
        this.list = list;
    }


    @Override
    public OpinionPollAdapter.OpinionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_offline_menu, parent, false);
        return new OpinionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OpinionPollAdapter.OpinionViewHolder holder, int position) {


        final int pos = position;
        final MainMenuDetails details = list.get(position);

        holder.menu_image.setImageResource(details.getId());
        holder.txt_menu_name.setText(details.getTitle());

        if (details.isDataToUpload()) {
            holder.txt_surveyCount.setVisibility(View.VISIBLE);
            holder.txt_surveyCount.setStrokeColor("#ffffff");
            holder.txt_surveyCount.setSolidColor("#d6060c");
            holder.txt_surveyCount.setStrokeWidth(1);
            Log.w("Adapter", "count :" + details.getCount());
            holder.txt_surveyCount.setText(details.getCount() + "");
            // holder.txt_surveyCount.setText("10000");

        } else {
            holder.txt_surveyCount.setVisibility(View.GONE);
        }

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.btn_click_from_menu(pos, details.getTitle());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OpinionViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.menu_image)
        ImageView menu_image;

        @BindView(R.id.txt_menu_name)
        TextView txt_menu_name;

        @BindView(R.id.cardview_main_menu)
        RelativeLayout cardview;

        @BindView(R.id.survey_count)
        CircularTextView txt_surveyCount;

        public OpinionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
