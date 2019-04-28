package com.software.cb.rajneethi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.software.cb.rajneethi.R;

/**
 * Created by admin on 5/30/2017.
 */

public class ToastMessageActivity extends AppCompatActivity {
    Toolbar toolbar;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toastmeaasge);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.messagetoast)).setText(getIntent().getStringExtra("MESSAGE"));
        // ((TextView) findViewById(R.id.messagetoastok)).setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent;
                if (getIntent().getStringExtra("CLASSNAME").equals("OfflineConstituencyDashBoardActivity.class")) {
                    mainIntent = new Intent(ToastMessageActivity.this, OfflineConstituencyDashBoardActivity.class);
                } else {
                    mainIntent = new Intent(ToastMessageActivity.this, ConductSurveyActivity.class);
                }
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ToastMessageActivity.this.startActivity(mainIntent);
                ToastMessageActivity.this.finish();
            }
        }, 1000);


    }


}
