package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.fragment.SMSAssemblywiseFragment;
import com.software.cb.rajneethi.fragment.SMSBoothwiseFragment;
import com.software.cb.rajneethi.fragment.SMSWardWiseFragment;
import com.software.cb.rajneethi.models.BoothDetails;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.CommunicationViewHolder> {

    private Context context;
    private ArrayList<BoothDetails> list;
    private Fragment fragment;

    public CommunicationAdapter(Context context, ArrayList<BoothDetails> list, Fragment fragment) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CommunicationAdapter.CommunicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_booth_selection, parent, false);
        return new CommunicationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunicationAdapter.CommunicationViewHolder holder, int position) {

        final int pos = position;
        final BoothDetails obj = list.get(position);

        if (obj.is_selected()) {
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.green1));
            holder.img_select.setVisibility(View.VISIBLE);
        } else {
            holder.img_select.setVisibility(View.INVISIBLE);
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
          //  holder.txt_booth_name.setTextColor(ContextCompat.getColor(context,R.color.primary));
        }

        holder.txt_booth_name.setText(obj.getBooth_name());


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment instanceof SMSWardWiseFragment) {
                    ((SMSWardWiseFragment) fragment).showSMSLayout();
                } else if (fragment instanceof SMSBoothwiseFragment) {
                    ((SMSBoothwiseFragment) fragment).showSMSLayout();
                } else if (fragment instanceof SMSAssemblywiseFragment) {
                    ((SMSAssemblywiseFragment) fragment).showSMSLayout();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommunicationViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.img_select)
        ImageView img_select;

        @BindView(R.id.txt_options)
        TextView txt_booth_name;

        @BindView(R.id.option_layout)
        RelativeLayout layout;


        public CommunicationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
