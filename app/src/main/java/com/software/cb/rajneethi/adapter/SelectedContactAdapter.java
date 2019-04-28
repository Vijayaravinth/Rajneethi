package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.AddEventsActivity;
import com.software.cb.rajneethi.models.SelectedContactDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by monika on 5/12/2017.
 */

public class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.SelectedContactViewHolder> {


    private Context context;
    private ArrayList<SelectedContactDetails> list;
    private AddEventsActivity activity;

    public SelectedContactAdapter(Context context, ArrayList<SelectedContactDetails> list, AddEventsActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public SelectedContactAdapter.SelectedContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_selected_contact, parent, false);
        return new SelectedContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SelectedContactAdapter.SelectedContactViewHolder holder, int position) {

        final SelectedContactDetails details = list.get(position);

        if (details.getType().equalsIgnoreCase("contact")) {
            String val = details.getContactDetails();
            String name = val.substring(0, val.indexOf("\n"));
            String number = val.substring(val.indexOf("\n") + 1, val.length());

            holder.txt_contact_details.setText(name);
            holder.txtNumber.setText(number);
        }else{
            holder.txt_contact_details.setText(details.getContactDetails());
            holder.txtNumber.setVisibility(View.GONE);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.remove_contacts(details);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SelectedContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adapter_txt_contact_details)
        TextView txt_contact_details;
        @BindView(R.id.adapter_image_delete)
        ImageView img_delete;

        @BindView(R.id.adapter_txt_contact_number)
        TextView txtNumber;

        @BindView(R.id.adapter_cardview)
        CardView cardView;

        public SelectedContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
