package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.SurveyStatsActivity;
import com.software.cb.rajneethi.activity.TeleCallingActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.SurveyStats;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 19-01-2018.
 */

public class SurveyStatsClientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<SurveyStats> list;
    private SurveyStatsActivity activity;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    MyDatabase db;

    public SurveyStatsClientAdapter(Context context, ArrayList<SurveyStats> list, SurveyStatsActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        db = new MyDatabase(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_stats_item, parent, false);
        return new ContentViewHolder(v);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        final int pos = position;

        final SurveyStats stats = list.get(position);

        ((ContentViewHolder) holder).txtName.setText(stats.getRepondantName());
        ((ContentViewHolder) holder).txtMobile.setText(stats.getMobile());
            /*((ContentViewHolder) holder).circulrText.setSolidColor("#128c7e");
            ((ContentViewHolder) holder).circulrText.setStrokeWidth(1);
            ((ContentViewHolder) holder).circulrText.setStrokeColor("#ffffff");
            try {
                ((ContentViewHolder) holder).circulrText.setText(stats.getRepondantName().substring(0, 1));
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
                ((ContentViewHolder) holder).circulrText.setText("N/A");
            }*/

        final String status = db.getStatusTeleCalling(stats.getMobile());

        if (status.equalsIgnoreCase("success")) {

            ((ContentViewHolder) holder).layout.setBackgroundColor(ContextCompat.getColor(context, R.color.green1));
            ((ContentViewHolder) holder).txtName.setTextColor(ContextCompat.getColor(context, R.color.icons));
            ((ContentViewHolder) holder).txtMobile.setTextColor(ContextCompat.getColor(context, R.color.icons));
        } else if (status.equalsIgnoreCase("failed")) {
            ((ContentViewHolder) holder).layout.setBackgroundColor(ContextCompat.getColor(context, R.color.red1));
            ((ContentViewHolder) holder).txtName.setTextColor(ContextCompat.getColor(context, R.color.icons));
            ((ContentViewHolder) holder).txtMobile.setTextColor(ContextCompat.getColor(context, R.color.icons));
        } else {
            ((ContentViewHolder) holder).layout.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
            ((ContentViewHolder) holder).txtName.setTextColor(ContextCompat.getColor(context, R.color.option_background1));
            ((ContentViewHolder) holder).txtMobile.setTextColor(ContextCompat.getColor(context, R.color.option_background1));

        }


        ((ContentViewHolder) holder).imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.call(stats.getMobile());
            }
        });

        ((ContentViewHolder) holder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // activity.showPayLoadData(stats,pos);
                if (!status.equalsIgnoreCase("success")) {
                    activity.setPos(pos, stats.getMobile());
                    context.startActivity(new Intent(context, TeleCallingActivity.class).putExtra("boothName", stats.getBoothName()).putExtra("name", stats.getRepondantName()).putExtra("mobile", stats.getMobile()));
                }else{
                    Toast.makeText(context,"Already verified",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtPwName)
        TextView txtPwName;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.adapter_txt_name)
        TextView txtName;
        @BindView(R.id.adapter_txt_mobile)
        TextView txtMobile;

        @BindView(R.id.img_call)
        ImageView imgCall;

        @BindView(R.id.user_layout)
        RelativeLayout layout;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
