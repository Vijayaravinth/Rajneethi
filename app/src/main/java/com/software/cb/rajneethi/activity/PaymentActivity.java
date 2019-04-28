package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.PaymentAdapter;
import com.software.cb.rajneethi.models.PaymentDetails;
import com.software.cb.rajneethi.utility.UtilActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentActivity extends UtilActivity {


    @BindView(R.id.menuRecyclerview)
    RecyclerView recyclerView;



    ArrayList<PaymentDetails> list = new ArrayList<>();
    PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        ButterKnife.bind(this);


        set_grid_layout_manager(recyclerView, this, 2);

        loadData();

    }

    private void loadData() {

        list.add(new PaymentDetails("SMS",R.drawable.sms,"10000","Rs.3000"));
        list.add(new PaymentDetails("Voice SMS",R.drawable.voicelatest,"10000","Rs.3000"));
        list.add(new PaymentDetails("Whatsapp SMS",R.drawable.whatsapplatest,"10000","Rs.3000"));
        setAdapter();
    }

    private void setAdapter(){
        adapter = new PaymentAdapter(this,list,PaymentActivity.this);
        recyclerView.setAdapter(adapter);
    }

}
