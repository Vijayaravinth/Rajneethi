
package com.software.cb.rajneethi.supervisormigratoon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.software.cb.rajneethi.R;

import butterknife.BindView;

public class SupervisorView extends AppCompatActivity implements View.OnClickListener {
private Context context;
    @BindView(R.id.get_survey_data)
    LinearLayout layout_get_survey_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supervisorview);
        findViewById(R.id.get_survey_data).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        startActivity(new Intent(SupervisorView.this,PartyWorkers.class));
    }
}
