package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.AdminActivity;
import com.software.cb.rajneethi.models.UploadBoothDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadBoothsAdapter extends RecyclerView.Adapter<UploadBoothsAdapter.UploadBoothviewHolder> {

    private Context context;
    private ArrayList<UploadBoothDetails> list;
    private AdminActivity activity;

    public UploadBoothsAdapter(Context context, ArrayList<UploadBoothDetails> list, AdminActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UploadBoothsAdapter.UploadBoothviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_upload_booth,parent, false);
        return new UploadBoothviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadBoothsAdapter.UploadBoothviewHolder holder, int i) {


        UploadBoothDetails details = list.get(i);

        String name = details.getBoothNumber()+" - "+ details.getBoothName();
        holder.txtName.setText(name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UploadBoothviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.delete)
        ImageView imgDelete;
        @BindView(R.id.txtName)
        TextView txtName;

        public UploadBoothviewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
