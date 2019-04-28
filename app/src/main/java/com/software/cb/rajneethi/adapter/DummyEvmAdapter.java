package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.DummyEVMActivity;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.models.DummyEvmDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DummyEvmAdapter extends RecyclerView.Adapter<DummyEvmAdapter.DummyViewHolder> {

    ArrayList<DummyEvmDetails> list;
    private Context context;
    MediaPlayer mp = null;

    DummyEVMActivity activity;

    public DummyEvmAdapter(ArrayList<DummyEvmDetails> list, Context context, DummyEVMActivity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public DummyEvmAdapter.DummyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_dummy_evm, viewGroup, false);
        return new DummyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DummyEvmAdapter.DummyViewHolder holder, int i) {


        DummyEvmDetails details = list.get(i);

        String name = details.getSerialNo() + ". " + details.getCandidateName() + "(" + details.getPartyName() + ")";
        holder.txtName.setText(name);

        holder.candidateImage.setImageResource(details.getCandidateImage());
        holder.imgParty.setImageResource(details.getSymbol());


        holder.btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgArrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.arrowred));


                activity.save(details.getSerialNo(),details.getCandidateName());

                if (mp != null) {
                    mp.release();
                    mp = null;
                }
                try {
                    mp = MediaPlayer.create(context, R.raw.erro);
                    // mp.prepare();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (mp != null) {
                                mp.release();
                                mp = null;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mp.start();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.imgArrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.arrowblack));
                    }
                }, 2000);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DummyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txtName)
        TextView txtName;

        @BindView(R.id.imgPartyImage)
        ImageView imgParty;

      @BindView(R.id.imgArrow)
      ImageView imgArrow;

        @BindView(R.id.buttonVote)
        Button btnVote;

        @BindView(R.id.candidateImage)
        ImageView candidateImage;

        public DummyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
