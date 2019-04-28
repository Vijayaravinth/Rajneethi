package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.ListenToPeopleActivity;
import com.software.cb.rajneethi.models.AudioFileDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 11/12/2017.
 */

public class AudioFilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private ArrayList<AudioFileDetails> list;
    private ListenToPeopleActivity activity;

    public AudioFilesAdapter(Context context, ArrayList<AudioFileDetails> list, ListenToPeopleActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_audio_file, parent, false);
        return new AudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        final int pos = position;
        final AudioFileDetails details = list.get(position);



        if (details.getSurveyFlag().equalsIgnoreCase("yes")){
           ((AudioViewHolder) holder).txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.icons));
            ((AudioViewHolder) holder).audioLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.green1));

        }else if(details.getSurveyFlag().equalsIgnoreCase("no")) {
            ((AudioViewHolder) holder).txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.icons));
            ((AudioViewHolder) holder).audioLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.red1));
        }

        else{
            ((AudioViewHolder) holder).txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.primary));
            ((AudioViewHolder) holder).audioLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.icons));

        }


        if (!details.isDownloaded()) {
            ((AudioViewHolder) holder).imgView.setVisibility(View.GONE);
            ((AudioViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_cloud_download_white_24dp));
        } else {
            if (details.isPlaying()) {
                ((AudioViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_circle_outline_white_24dp));
            } else {
                ((AudioViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_play_circle_outline_white_24dp));
            }
            ((AudioViewHolder) holder).imgView.setVisibility(View.VISIBLE);
        }


        if (details.isPlaying()) {
           // ((AudioViewHolder) holder).txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.accent));
            // ((AudioViewHolder) holder).txtAudioFileName.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            ((AudioViewHolder) holder).imgDownload.setColorFilter(ContextCompat.getColor(context, R.color.accent), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
          //  ((AudioViewHolder) holder).txtAudioFileName.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
            // ((AudioViewHolder) holder).txtAudioFileName.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            ((AudioViewHolder) holder).imgDownload.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);

        }
    /*    if (details.isDownloading()) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_blink);
            holder.imgDownload.clearAnimation();
            holder.imgDownload.startAnimation(anim);
        } else {
            holder.imgDownload.clearAnimation();
        }*/

        ((AudioViewHolder) holder).txtAudioFileName.setText(details.getFileName());


        ((AudioViewHolder) holder).audioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!details.isDownloaded()) {

                    if (!details.isDownloading()) {
                        activity.download(details.getFileName(), pos);
                    }
                } else {
                    if (details.isDownloaded() && !details.isDownloading()) {
                        if (!details.isPlaying() && !details.isPaused()) {
                            activity.playAudio(details.getFileName(), pos);
                            activity.showPayloadData(details, pos);
                        } else {
                            if (details.isPlaying()) {
                                activity.showPayloadData(details, pos);
                             //   activity.pause();
                            } /*else {
                                activity.play_audio();
                            }*/
                        }
                    }
                }
            }
        });

        ((AudioViewHolder) holder).imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // activity.showPayloadData(details, pos);

                activity.shareAudio(details.getFileName());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class AudioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtAudioFileName)
        TextView txtAudioFileName;

        @BindView(R.id.imgDownload)
        ImageView imgDownload;

        @BindView(R.id.audioLayout)
        RelativeLayout audioLayout;

        @BindView(R.id.imgView)
        ImageView imgView;

        public AudioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtPwName)
        TextView txtPwName;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
