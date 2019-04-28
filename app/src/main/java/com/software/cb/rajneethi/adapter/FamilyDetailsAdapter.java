package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.VoterSearchActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.VoterDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyDetailsAdapter extends RecyclerView.Adapter<FamilyDetailsAdapter.FamilyViewholder> {


    private Context context;
    private ArrayList<VoterDetails> list;
    private MyDatabase db;
    private VoterSearchActivity activity;

    public FamilyDetailsAdapter(Context context, ArrayList<VoterDetails> list, MyDatabase db, VoterSearchActivity activity) {
        this.context = context;
        this.list = list;
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FamilyDetailsAdapter.FamilyViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_family_details,viewGroup,false);
        return new FamilyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyDetailsAdapter.FamilyViewholder holder, int position) {
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

        if (details.isFamilyHead()){
            holder.imgFamily.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);

        }else{
            holder.imgFamily.setColorFilter(ContextCompat.getColor(context, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN);

        }

        holder.cbFamily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    db.insertFamilyDetails(details.getVoterCardNumber(),activity.voterCardnumber);

                }else{
                //    db.deleteFamilySegregation(details.getVoterCardNumber());
                    db.deleteFamilySegregation(details.getVoterCardNumber());

                }
            }
        });


        holder.imgFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFamilyHead(pos);
            }
        });

        holder.cbFamily.setChecked(db.checkFamilyMemberExist(details.getVoterCardNumber()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FamilyViewholder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.cbFamily)
        CheckBox cbFamily;
        @BindView(R.id.genderLayout)
        LinearLayout genderLayout;

        @BindView(R.id.imgFamily)
        ImageView imgFamily;

        @BindView(R.id.view)
        View view;


        public FamilyViewholder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
