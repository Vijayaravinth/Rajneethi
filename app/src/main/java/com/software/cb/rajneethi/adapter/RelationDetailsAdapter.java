package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.fragment.FamilyDetailsFragment;
import com.software.cb.rajneethi.models.VoterDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by monika on 4/8/2017.
 */

public class RelationDetailsAdapter extends RecyclerView.Adapter<RelationDetailsAdapter.RelationViewHolder> {

    private Context context;
    private ArrayList<VoterDetails> list;
    private FamilyDetailsFragment fragment;
    private ArrayList<String> options;


    public RelationDetailsAdapter(Context context, ArrayList<VoterDetails> list, FamilyDetailsFragment fragment, ArrayList<String> options) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;
        this.options = options;
    }

    @Override
    public RelationDetailsAdapter.RelationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_relation_details, parent, false);
        return new RelationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RelationDetailsAdapter.RelationViewHolder holder, final int position) {

        final VoterDetails details = list.get(position);

        holder.txt_address.setText(details.getAddressEnglish());
        holder.txt_age.setText(details.getAge());
        holder.txt_relation_name.setText(details.getRelatedEnglish());
        holder.txt_name.setText(details.getNameEnglish());
        holder.txt_voter_card_number.setText(details.getVoterCardNumber());
        holder.txt_serial_number.setText(details.getSerialNo());
        holder.txtBoothName.setText(details.getBoothName());
        if (details.getGender().equals("F")) {
            holder.genderLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green1));
            holder.img_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.female));
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.green1));
        } else {
            holder.genderLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red1));
            holder.img_gender.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.male));
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.red1));

        }


        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (details.is_updated() == null) {
                    fragment.change_voter_details(details);
                } else {
                    if (details.is_updated().equals("true")) {
                        Toast.makeText(context, context.getResources().getString(R.string.surveyTaken), Toast.LENGTH_SHORT).show();
                    } else {
                        fragment.change_voter_details(details);
                    }
                }
            }
        });

        Log.w("Adapter", "data " + details.is_updated());

        if (details.is_updated() == null) {
            holder.spinner.setVisibility(View.VISIBLE);
            setAdapterForSpinner(holder.spinner);

        } else {
            holder.spinner.setVisibility(View.GONE);
        }

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    fragment.updateRelationDetailsSurvey(position,i,details);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setAdapterForSpinner(Spinner spinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RelationViewHolder extends RecyclerView.ViewHolder {


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

        @BindView(R.id.adapter_txt_booth_name)
        TextView txtBoothName;

        @BindView(R.id.spinner_options)
        Spinner spinner;

        @BindView(R.id.genderLayout)
        LinearLayout genderLayout;

        @BindView(R.id.view)
        View view;

        public RelationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
