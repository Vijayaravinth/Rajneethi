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
import com.software.cb.rajneethi.activity.EnquirePeopleActivity;
import com.software.cb.rajneethi.models.EnquirePeopleDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 11/28/2017.
 */

public class EnquirePeopleAdapter extends RecyclerView.Adapter<EnquirePeopleAdapter.EnquireViewHolder> {


    private Context context;
    private ArrayList<EnquirePeopleDetails> list;
    private EnquirePeopleActivity activity;

    public EnquirePeopleAdapter(Context context, ArrayList<EnquirePeopleDetails> list, EnquirePeopleActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public EnquirePeopleAdapter.EnquireViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_enquire_people, parent, false);
        return new EnquireViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EnquirePeopleAdapter.EnquireViewHolder holder, int position) {

        final EnquirePeopleDetails details = list.get(position);
        holder.txtName.setText(details.getName());
        holder.txtMobile.setText(details.getMobile());

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.call(details.getMobile() ,"\"{\\\\\\\"PARTY PREFERENCE\\\\\\\":\\\\\\\"INC\\\\\\\",\\\\\\\"surveyType\\\\\\\":\\\\\\\"Questionaire\\\\\\\",\\\\\\\"projectId\\\\\\\":\\\\\\\"1\\\\\\\",\\\\\\\"SATISFACTION ON MLA\\\\\\\":\\\\\\\"Yes\\\\\\\",\\\\\\\"surveyDate\\\\\\\":\\\\\\\"Sat Nov 25 19:09:07 GMT+05:30 2017\\\\\\\",\\\\\\\"audioFileName\\\\\\\":\\\\\\\"AUD-197-2017-11-25_070835.mp3\\\\\\\",\\\\\\\"TIME OF VOTE PREFERENCE\\\\\\\":\\\\\\\"One or two days prior to the day of polling\\\\\\\",\\\\\\\"VOTING INFLUENCE\\\\\\\":\\\\\\\"Husband  Wife  Family Members\\\\\\\",\\\\\\\"SOCIAL MEDIA INFLUENCE\\\\\\\":\\\\\\\"Yes\\\\\\\",\\\\\\\"Mobile Number\\\\\\\":\\\\\\\"868466465\\\\\\\",\\\\\\\"VOTE HIM-HER AS INDEPENDENT\\\\\\\":\\\\\\\"Yes\\\\\\\",\\\\\\\"booth_name\\\\\\\":\\\\\\\"Booth2\\\\\\\",\\\\\\\"CANDIDATE PREFERENCE\\\\\\\":\\\\\\\"Sri Seetharam M R  (Congress)\\\\\\\",\\\\\\\"Major Issue\\\\\\\":\\\\\\\"Electricity\\\\\\\",\\\\\\\"Name\\\\\\\":\\\\\\\"fbfbdb\\\\\\\",\\\\\\\"longitude\\\\\\\":\\\\\\\"77.50271\\\\\\\",\\\\\\\"PAST CHOICE\\\\\\\":\\\\\\\"Loksatta ( Smt Meenakshi Bharath)\\\\\\\",\\\\\\\"partyWorker\\\\\\\":\\\\\\\"197\\\\\\\",\\\\\\\"SATISFACTION ON GOVT\\\\\\\":\\\\\\\"No\\\\\\\",\\\\\\\"Caste\\\\\\\":\\\\\\\"xhxb\\\\\\\",\\\\\\\"Education\\\\\\\":\\\\\\\"cj j\\\\\\\",\\\\\\\"PARTY PREFERENCE REASON\\\\\\\":\\\\\\\"Impressed by MLA Candidates\\\\\\\",\\\\\\\"Gender\\\\\\\":\\\\\\\"cjcjc\\\\\\\",\\\\\\\"userTpe\\\\\\\":\\\\\\\"opcp\\\\\\\",\\\\\\\"latitude\\\\\\\":\\\\\\\"13.048273333333334\\\\\\\",\\\\\\\"Occupation\\\\\\\":\\\\\\\"cjcj\\\\\\\",\\\\\\\"Age\\\\\\\":\\\\\\\"dhdhd\\\\\\\"}\\\"}],\\n\" +\n" +
                        "                        \"\\\"username\\\":\\\"ac157partyworker\\\"\"");


            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EnquireViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardview)
        CardView cardview;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtMobile)
        TextView txtMobile;
        @BindView(R.id.imgCall)
        ImageView imgCall;

        public EnquireViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
