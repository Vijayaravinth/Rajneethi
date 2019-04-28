package com.software.cb.rajneethi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.EnquirePeopleAdapter;
import com.software.cb.rajneethi.fragment.ShowPayLoadFragment;
import com.software.cb.rajneethi.models.EnquirePeopleDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.checkInternet;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 11/28/2017.
 */

public class EnquirePeopleActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.enquire_people)
    String title;

    @BindView(R.id.enquireRecyclerView)
    RecyclerView recyclerView;

    EnquirePeopleAdapter adapter;

    ArrayList<EnquirePeopleDetails> list = new ArrayList<>();

    @BindView(R.id.btnRetry)
    Button btnRetry;

    IOSDialog dialog;
    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquire_people);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, title);


        dialog = show_dialog(this, false);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        list.add(new EnquirePeopleDetails("Vijay", "8675505758"));
        list.add(new EnquirePeopleDetails("Vijay", "8675505758"));
        list.add(new EnquirePeopleDetails("Vijay", "8675505758"));
        list.add(new EnquirePeopleDetails("Vijay", "8675505758"));
        list.add(new EnquirePeopleDetails("Vijay", "8675505758"));
        list.add(new EnquirePeopleDetails("Vijay", "8675505758"));
        list.add(new EnquirePeopleDetails("Vijay", "8675505758"));


        set_Linearlayout_manager(recyclerView, this);

        setAdapter();

    }

    //show payload data
    public void showPayloadData(String payloadData) {

        //   ShowPayLoadFragment frag = ShowPayLoadFragment.newInstance(payloadData);
        /// frag.show(getSupportFragmentManager(), "Dialog");
    }

    //set adapter
    private void setAdapter() {
        adapter = new EnquirePeopleAdapter(this, list, EnquirePeopleActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("MissingPermission")
    public void call(String mobileNumber, String payloadData) {

        if (checkPermissionGranted(Constants.CALL, this)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber.trim()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            showPayloadData(payloadData);
        } else {
            askPermission(new String[]{Constants.CALL});
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
