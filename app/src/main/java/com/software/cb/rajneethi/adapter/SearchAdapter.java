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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.VoterProfileActivity;
import com.software.cb.rajneethi.activity.VoterSearchActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.VoterDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by monika on 3/25/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private ArrayList<VoterDetails> list;
    private VoterSearchActivity activity;
    private MyDatabase db;


    public SearchAdapter(Context context, ArrayList<VoterDetails> list, VoterSearchActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        db = new MyDatabase(context);
    }


    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {

        final int pos = position;
        final VoterDetails details = list.get(position);

        Log.w("Adapter ", "is updated " + details.is_updated());

        if (details.is_updated().equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.cardview.setBackground(ContextCompat.getDrawable(context, R.drawable.custom_border));
            } else {
                holder.cardview.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.custom_border));
                //   holder.cardview.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            }
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_blink);
            holder.img_survey_done.setVisibility(View.VISIBLE);
            holder.img_survey_done.clearAnimation();
            holder.img_survey_done.startAnimation(animation);


            //  holder.details_layout.setBackgroundColor(ContextCompat.getColor(context,R.color.option_background2));
        } else {
            holder.cardview.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
            holder.img_survey_done.setVisibility(View.GONE);
            //  holder.details_layout.setBackgroundColor(ContextCompat.getColor(context,R.color.icons));
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
            holder.genderLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green1));
            holder.img_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.female));
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.green1));
        } else {
            holder.genderLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red1));
            holder.img_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.male));
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.red1));

        }

       /* switch (details.getSOS()) {
            case 1:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.option_background1), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 2:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 3:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 4:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            case 5:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.background_color), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.background_color), android.graphics.PorterDuff.Mode.SRC_IN);
                break;

        }*/

       /*if (db.checkFamilyHead(details.getVoterCardNumber())){
           holder.imgFamily.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);

       }else{
           holder.imgFamily.setColorFilter(ContextCompat.getColor(context, R.color.primaryDark), android.graphics.PorterDuff.Mode.SRC_IN);

       }*/

        Log.w("Adapter ", "Relation details related eng " + details.getRelatedEnglish() + " related regional " + details.getRelatedRegional() + " relationship " + details.getRelationship());

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (activity.checkPermission()) {

                    if (activity.checkGPSEnabled()) {

                        if (!details.is_updated().equals("true")) {
                            context.startActivity(new Intent(context, VoterProfileActivity.class).putExtra("bskMode", activity.bskMode).putExtra("name", details.getNameEnglish()).putExtra("gender", details.getGender()).putExtra("houseNumber", details.getHouseNo()).putExtra("age", details.getAge()).putExtra("voterCardNumber", details.getVoterCardNumber()).putExtra("address", details.getAddressEnglish()).putExtra("relationship", details.getRelatedEnglish()).putExtra("serialNumber", details.getSerialNo()).putExtra("addressRegional", details.getAddressRegional()).putExtra("is_survey_taken", false).putExtra("userType", "d2d").putExtra("boothName", details.getBoothName()).putExtra("wardName", details.getWardName()).putExtra("mobile", details.getMobile()));
                          //  activity.clear_list();
                        } else {
                            context.startActivity(new Intent(context, VoterProfileActivity.class).putExtra("bskMode", activity.bskMode).putExtra("name", details.getNameEnglish()).putExtra("gender", details.getGender()).putExtra("houseNumber", details.getHouseNo()).putExtra("age", details.getAge()).putExtra("voterCardNumber", details.getVoterCardNumber()).putExtra("address", details.getAddressEnglish()).putExtra("relationship", details.getRelatedEnglish()).putExtra("serialNumber", details.getSerialNo()).putExtra("addressRegional", details.getAddressRegional()).putExtra("is_survey_taken", true).putExtra("userType", "d2d").putExtra("boothName", details.getBoothName()).putExtra("wardName", details.getWardName()).putExtra("mobile", details.getMobile()));
                            Toast.makeText(context, "Already survey has been taken", Toast.LENGTH_LONG).show();
                          //  activity.clear_list();
                        }
                    } else {
                        activity.mEnableGps();
                    }

                } else {
                    activity.askPermission();
                }
            }
        });

        holder.imgSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sending sms", Toast.LENGTH_SHORT).show();
            }
        });


        holder.imgFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.showFamilyDetails(details , pos);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.adapter_card_search)
        CardView cardview;

        @BindView(R.id.details_layout)
        LinearLayout details_layout;

        @BindView(R.id.img_survey_done)
        ImageView img_survey_done;

        @BindView(R.id.adapter_txt_booth_name)
        TextView txtBoothName;

        @BindView(R.id.imgSms)
        ImageView imgSms;

        @BindView(R.id.imgFamily)
        ImageView imgFamily;

        @BindView(R.id.genderLayout)
        LinearLayout genderLayout;

        @BindView(R.id.view)
        View view;

        public SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
