package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.TimeTableActivity;
import com.software.cb.rajneethi.models.EventsDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by monika on 5/10/2017.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {


    private Context context;
    private ArrayList<EventsDetails> list;
    private TimeTableActivity activity;


    public EventsAdapter(Context context, ArrayList<EventsDetails> list, TimeTableActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public EventsAdapter.EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_events, parent, false);
        return new EventsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventsAdapter.EventsViewHolder holder, int position) {


        final int pos = position;
        final EventsDetails details = list.get(position);
        holder.txt_title.setText(details.getEventTitle());
        holder.txt_date.setText(details.getEventDate());
        holder.txt_place.setText(details.getPlace());
        holder.txt_time.setText(details.getEventTime());


        holder.closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.menuLayout.setVisibility(View.GONE);
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.bottom_to_top);
                holder.contentLayout.setAnimation(anim);
                holder.contentLayout.setVisibility(View.VISIBLE);
            }
        });

        holder.contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.contentLayout.setVisibility(View.GONE);
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom);
                holder.menuLayout.setAnimation(anim);
                holder.menuLayout.setVisibility(View.VISIBLE);
            }
        });


        Log.w("Adapter", "Time : " + details.getEventTime());
        holder.sms_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.send_sms(details);
            }
        });

        holder.delate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.delete(pos,details.getId());
            }
        });

        holder.reschedule_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.reschedule_event(details);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adapter_txt_events_title)
        TextView txt_title;
        @BindView(R.id.adapter_txt_event_date)
        TextView txt_date;
        @BindView(R.id.adapter_txt_event_place)
        TextView txt_place;
        @BindView(R.id.adapter_txt_event_time)
        TextView txt_time;

        @BindView(R.id.sms_layout)
        LinearLayout sms_layout;
        @BindView(R.id.reschedule_layout)
        LinearLayout reschedule_layout;
        @BindView(R.id.delete_layout)
        LinearLayout delate_layout;

        @BindView(R.id.menuLayout)
        LinearLayout menuLayout;
        @BindView(R.id.contentLayout)
        RelativeLayout contentLayout;

        @BindView(R.id.closeLayout)
        LinearLayout closeLayout;

        public EventsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
