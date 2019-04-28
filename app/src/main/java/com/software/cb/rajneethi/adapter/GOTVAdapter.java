package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.GOTVActivity;
import com.software.cb.rajneethi.activity.VoterProfileActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.VoterDetails;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 11/2/2017.
 */

public class GOTVAdapter extends RecyclerView.Adapter<GOTVAdapter.GOTVViewHolder> {

    private Context context;
    private ArrayList<VoterDetails> list;
    private GOTVActivity activity;

    public GOTVAdapter(Context context, ArrayList<VoterDetails> list, GOTVActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public GOTVAdapter.GOTVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gotv, parent, false);
        return new GOTVViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GOTVAdapter.GOTVViewHolder holder, int position) {

        final int pos = position;
        final VoterDetails details = list.get(position);

        if (details.getVNV().equalsIgnoreCase("1")) {
            if (activity.animPosition != -1) {
                if (pos == activity.animPosition) {
                    Animation anim = null;
                    anim = AnimationUtils.loadAnimation(context, R.anim.menu_anim);
                    holder.imgVote.clearAnimation();
                    holder.imgVote.startAnimation(anim);
                    activity.animPosition = -1;
                }
            }

            holder.imgVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_done));
        } else {
            holder.imgVote.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.square));
        }

        holder.txt_address.setText(details.getAddressEnglish());
        holder.txt_age.setText(details.getAge());
        holder.txt_relation_name.setText(details.getRelatedEnglish());
        holder.txt_name.setText(details.getNameEnglish());
        holder.txt_voter_card_number.setText(details.getVoterCardNumber());
        holder.txt_serial_number.setText(details.getSerialNo());
        holder.txtBoothName.setText(details.getBoothName());

        Log.w("Adapter", "Gender : " + details.getGender());
        if (details.getGender().equals("F")) {
            holder.img_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.female));
        } else {
            holder.img_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.male));
        }

        switch (details.getSOS()) {
            case 0:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 1:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 2:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 3:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.dark_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 4:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.background_color), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                break;

        }

        holder.imgVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (details.getVNV().equalsIgnoreCase("1")) {
                    activity.updateVote(pos, details.getVoterCardNumber(), 0);
                } else {
                    activity.updateVote(pos, details.getVoterCardNumber(), 1);

                }
            }
        });

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.call(details.getMobile());
            }
        });
        holder.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.sendMessage(details.getMobile());
            }
        });
        holder.imgVoiceMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareContent = "Name : " + details.getNameEnglish() + "\nAge :" + details.getAge() + "\nVoter Card Number : " + details.getVoterCardNumber() + "\nSerial No :" + details.getSerialNo() + "\nBooth Number:" + details.getBoothName() + "\nWard Name :" + details.getWardName();
                activity.shareData(shareContent, details.getMobile());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GOTVViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.adapter_gender_image)
        ImageView img_gender;
        @BindView(R.id.adapter_txt_age)
        TextView txt_age;
        @BindView(R.id.adapter_txt_name)
        TextView txt_name;
        @BindView(R.id.adapter_txt_voter_card_number)
        TextView txt_voter_card_number;
        @BindView(R.id.adapter_txt_serial_number)
        TextView txt_serial_number;
        @BindView(R.id.adapter_txt_address)
        TextView txt_address;
        @BindView(R.id.adapter_txt_relation_name)
        TextView txt_relation_name;

        @BindView(R.id.imgCall)
        ImageView imgCall;
        @BindView(R.id.imgMessage)
        ImageView imgMessage;
        @BindView(R.id.imgVoiceMessage)
        ImageView imgVoiceMessage;
        @BindView(R.id.imgShare)
        ImageView imgShare;

        @BindView(R.id.adapter_card_search)
        CardView cardview;

        @BindView(R.id.details_layout)
        LinearLayout details_layout;


        @BindView(R.id.imgVote)
        ImageView imgVote;

        @BindView(R.id.adapter_txt_booth_name)
        TextView txtBoothName;


        @BindString(R.string.submitVote)
        String submitVote;
        @BindString(R.string.voted)
        String voted;

        public GOTVViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
