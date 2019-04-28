package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.custmo_widgets.CircularTextView;
import com.software.cb.rajneethi.models.ConstitunecyAdapterDetails;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 10/8/2017.
 */

public class ConstituencyDetailsAdapter extends RecyclerView.Adapter<ConstituencyDetailsAdapter.ConstituencyViewHolder> {


    private Context context;
    private ArrayList<ConstitunecyAdapterDetails> list;

    public ConstituencyDetailsAdapter(Context context, ArrayList<ConstitunecyAdapterDetails> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ConstituencyDetailsAdapter.ConstituencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_constituency_details, parent, false);
        return new ConstituencyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ConstituencyDetailsAdapter.ConstituencyViewHolder holder, int position) {

        final ConstitunecyAdapterDetails obj = list.get(position);

        holder.txt_count.setText(obj.getCount());
        holder.txt_type.setText(obj.getName());

        int color = randomColor();

        Log.w("Adapter ", "Color : " + color);

        holder.txt_constituency_title.setStrokeWidth(3);
        holder.txt_constituency_title.setStrokeColor("#ffffff");
        // holder.txt_constituency_title.setSolidColor("#9C27B0");
        //String strColor = String.format("#%06X", 0xFFFFFF & color);
        holder.txt_constituency_title.setSolidColor(String.format("#%06X", 0xFFFFFF & color));
        switch (obj.getName()) {
            case "male":
                holder.txt_constituency_title.setText("M");
                break;
            case "female":
                holder.txt_constituency_title.setText("F");
                break;
            case "total":
                holder.txt_constituency_title.setText("T");
                break;
            case "firstbatch":
                holder.txt_constituency_title.setText("18-23");
                break;
            case "secondbatch":
                holder.txt_constituency_title.setText("24-35");
                break;
            case "thirdbatch":
                holder.txt_constituency_title.setText("36-55");
                break;
            case "lastbatch":
                holder.txt_constituency_title.setText("55+");
                break;
            case "mobilecount":
                holder.txt_constituency_title.setText("MOB");
                break;
            case "Expired":
                holder.txt_constituency_title.setText("E");
                break;
            case "Shifted":
                holder.txt_constituency_title.setText("S");
                break;
            case "NewVoters":
                holder.txt_constituency_title.setText("NV");
                break;
            default:
                break;

        }


        //  int is_exist = context.getResources().getIdentifier(obj.getName(), "drawable", context.getPackageName());

      /*  if (is_exist != 0) {

            Log.w("adapter ","Image  :"+ obj.getImage());
            int resource = context.getResources().getIdentifier(obj.getName(), "drawable",context.getPackageName());
           // Drawable drawable = getResources().getDrawable(id);
            holder.image.setImageResource(resource);
        } else {*/
        // holder.image.setImageResource(R.drawable.male_user);
        //  }


        holder.layout.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ConstituencyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.details_layout)
        LinearLayout layout;

        /*  @BindView(R.id.adapter_icon)
          CircleImageView image;*/
        @BindView(R.id.adapter_txt_count)
        TextView txt_count;

        @BindView(R.id.txt_type)
        TextView txt_type;

        @BindView(R.id.txt_constituency_title)
        CircularTextView txt_constituency_title;

        public ConstituencyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /*generate random color*/
    public int randomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }
}
