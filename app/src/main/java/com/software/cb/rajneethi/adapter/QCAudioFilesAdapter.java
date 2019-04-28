package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.models.QCAudioDetails;
import com.software.cb.rajneethi.qc.QCUserAudioActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 04-04-2018.
 */

public class QCAudioFilesAdapter extends RecyclerView.Adapter<QCAudioFilesAdapter.QCAudioViewHolder> {

    private Context context;
    private ArrayList<QCAudioDetails> list;
    private QCUserAudioActivity activity;

    public QCAudioFilesAdapter(Context context, ArrayList<QCAudioDetails> list, QCUserAudioActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public QCAudioFilesAdapter.QCAudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_qc_audio_files, parent, false);
        return new QCAudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(QCAudioFilesAdapter.QCAudioViewHolder holder, int position) {


        final int pos = position;
        QCAudioDetails details = list.get(pos);
        holder.txtAudioFileName.setText(details.getAudioFileName());

        if (details.isDownloading()) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(details.getProgress());
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }



        if (details.isPlaying()) {
            holder.txtAudioFileName.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            holder.txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.icons));
        } else {

            if (details.getIsVerified().equalsIgnoreCase("yes")){
                holder.txtAudioFileName.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                holder.txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.icons));
            }else {
                holder.txtAudioFileName.setBackgroundColor(ContextCompat.getColor(context, R.color.icons));
                holder.txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
            }
        }



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.playAudioFromAdapter(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refresh(int pos, int progress) {

        QCAudioDetails details = list.get(pos);
        details.setProgress(progress);
        notifyItemChanged(pos);
    }

    public class QCAudioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtAudioFileName)
        TextView txtAudioFileName;

        @BindView(R.id.cardview)
        CardView cardView;

        @BindView(R.id.progressbar)
        ProgressBar progressBar;

        @BindView(R.id.layout)
        LinearLayout layout;

        public QCAudioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
