package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.BoothSelectActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;


/**
 * Created by MVIJAYAR on 07-07-2017.
 */

public class BoothSelectAdapter extends RecyclerView.Adapter<BoothSelectAdapter.BoothViewHolder> {

    private Context context;
    private ArrayList<String> title;
 //   private BoothSelectActivity activity;


    public BoothSelectAdapter(Context context, ArrayList<String> title) {
        this.context = context;
        this.title = title;

    }



    @Override
    public BoothSelectAdapter.BoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_opinion_poll, parent, false);
        return new BoothViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BoothSelectAdapter.BoothViewHolder holder,  int position) {


        final int pos = position;
       /* holder.button.setText(title.get(position));

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // activity.btn_click_from_menu(pos,title.get(pos));
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class BoothViewHolder extends RecyclerView.ViewHolder {

     /*   @BindView(R.id.btn_opinion_menu)
        Button button;
*/
        public BoothViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
