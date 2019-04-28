package com.software.cb.rajneethi.activity;

import android.content.Intent;
import android.os.Bundle;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.utility.Util;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Util {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.contituency_map_layout)
    public void go_to_constituency_map_activity() {

        go_to_activity(MyConstituencyActivity.class);
    }

    public void go_to_activity(Class classname) {
        startActivity(new Intent(MainActivity.this, classname));

    }
}
