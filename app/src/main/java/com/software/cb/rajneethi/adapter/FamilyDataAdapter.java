package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.FamilyDataActivity;
import com.software.cb.rajneethi.models.FamilyData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyDataAdapter extends RecyclerView.Adapter<FamilyDataAdapter.FamilyViewHolder> {


    private Context context;
    private ArrayList<FamilyData> list;
    private FamilyDataActivity activity;

    public FamilyDataAdapter(Context context, ArrayList<FamilyData> list, FamilyDataActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FamilyDataAdapter.FamilyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_family_data, viewGroup, false);
        return new FamilyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyDataAdapter.FamilyViewHolder holder, int i) {

        final int pos = i;
        final FamilyData details = list.get(i);

        if (activity.isPercentage) {
            String count = details.getPercentage();
            holder.txtCount.setText(count);
        }else{
            String count = String.valueOf(details.getCount());
            holder.txtCount.setText(count);
        }

        holder.txtCasteName.setText(details.getCasteName());

        if(details.isSelected()){
            holder.cardview.setCardBackgroundColor(ContextCompat.getColor(context,R.color.background));
        }else{
            holder.cardview.setCardBackgroundColor(ContextCompat.getColor(context,R.color.icons));
        }


        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.ISINACTIONMODE) {
                    if (details.isSelected()) {
                        activity.removeItem(pos);
                    } else {
                        activity.selectCaste(pos);
                    }
                }
            }
        });


        holder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!activity.ISINACTIONMODE){
                    activity.selectedCasteList.clear();
                    activity.ISINACTIONMODE = true;
                    activity.selectCaste(pos);
                    activity.inflateMenu();
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FamilyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtCount)
        TextView txtCount;
        @BindView(R.id.txtCasteName)
        TextView txtCasteName;

        @BindView(R.id.cardview)
        CardView cardview;

        public FamilyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
