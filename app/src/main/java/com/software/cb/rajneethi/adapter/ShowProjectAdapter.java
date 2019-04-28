package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.ConstituencyActivity;
import com.software.cb.rajneethi.activity.ProjectActivity;
import com.software.cb.rajneethi.models.ProjectDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monica on 09-03-2017.
 */

public class ShowProjectAdapter extends RecyclerView.Adapter<ShowProjectAdapter.ProjectAdapterViewHolder> {

    private Context context;
    private ArrayList<ProjectDetails> list;
    private ProjectActivity activity;

    public ShowProjectAdapter(Context context, ArrayList<ProjectDetails> list, ProjectActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }


    @Override
    public ShowProjectAdapter.ProjectAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_project, parent, false);
        return new ProjectAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ShowProjectAdapter.ProjectAdapterViewHolder holder, int position) {

        final ProjectDetails details = list.get(position);

        holder.txt_assemble_name.setText(details.getName());
        holder.txt_xonstituency_name.setText(details.getDescription());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ConstituencyActivity.class).putExtra("id", details.getId()).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                activity.finish_activity();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ProjectAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adapter_txt_assembly_name)
        TextView txt_assemble_name;

        @BindView(R.id.adapter_cardview_project)
        CardView cardView;

        @BindView(R.id.adapter_txtr_constituency_name)
        TextView txt_xonstituency_name;

        public ProjectAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
