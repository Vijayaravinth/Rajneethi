package com.software.cb.rajneethi.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.fragment.CasteFragment;
import com.software.cb.rajneethi.fragment.SMSAssemblywiseFragment;
import com.software.cb.rajneethi.fragment.SMSBoothwiseFragment;
import com.software.cb.rajneethi.fragment.SMSWardWiseFragment;
import com.software.cb.rajneethi.fragment.SummaryFragment;
import com.software.cb.rajneethi.utility.UtilActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

public class CommunicationActivity extends UtilActivity {


    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    SMSBoothwiseFragment boothwiseFragment;
    SMSWardWiseFragment wardWiseFragment;
    SMSAssemblywiseFragment assemblywiseFragment;
    CasteFragment casteFragment;

    @BindString(R.string.smsbooth)
    String boothWise;
    @BindString(R.string.smsassemblywise)
    String assemblyWise;
    @BindString(R.string.smsward)
    String wardwise;

    @BindString(R.string.communication)
    String communication;

    @BindView(R.id.sms_layout)
    public CardView smsLayout;

    @BindView(R.id.autocompltetextview)
    AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.fabsendSMS)
    FloatingActionButton sendSMS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        ButterKnife.bind(this);


        setup_toolbar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(communication);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.back));
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

         viewPager.setOffscreenPageLimit(3);


        setAdapterForTemplate();
    }

    @OnClick(R.id.close)
    public void close() {
        hideKeyboard(CommunicationActivity.this);
        autoCompleteTextView.getText().clear();
        smsLayout.setVisibility(GONE);
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        boothwiseFragment = new SMSBoothwiseFragment();
        wardWiseFragment = new SMSWardWiseFragment();
        assemblywiseFragment = new SMSAssemblywiseFragment();

        casteFragment = new CasteFragment();

        adapter.addFragment(boothwiseFragment, boothWise);
        adapter.addFragment(wardWiseFragment, wardwise);
        adapter.addFragment(assemblywiseFragment, assemblyWise);
        adapter.addFragment(casteFragment, "Caste");

        viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();





    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();


        ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }
    }

    ArrayList<String> list = new ArrayList<>();

    private void setAdapterForTemplate() {


        list.add("Template 1");
        list.add("Template 2");
        list.add("Template 3");
        list.add("Template 4");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        autoCompleteTextView.setAdapter(spinnerArrayAdapter);
        autoCompleteTextView.setThreshold(2);
    }

    @OnClick(R.id.fabsendSMS)
    public void sendSMS() {

        if (!autoCompleteTextView.getText().toString().trim().isEmpty()) {

            list.add(autoCompleteTextView.getText().toString().trim());
            hideKeyboard(CommunicationActivity.this);
            autoCompleteTextView.getText().clear();

            Toastmsg(CommunicationActivity.this, "Sending sms");
            smsLayout.setVisibility(GONE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.communication_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.broadcast:
                Toastmsg(CommunicationActivity.this, "Sending broadcast message");
                return true;

            case R.id.payment:
                startActivity(new Intent(CommunicationActivity.this, PaymentActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}


