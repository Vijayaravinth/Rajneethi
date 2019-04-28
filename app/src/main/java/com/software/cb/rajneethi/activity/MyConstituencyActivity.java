package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.software.cb.rajneethi.fragment.AgeFragment;
import com.software.cb.rajneethi.fragment.CastEquationFragment;
import com.software.cb.rajneethi.fragment.ElectionResultFragment;
import com.software.cb.rajneethi.fragment.MaleFemaleFragment;
import com.software.cb.rajneethi.fragment.SeniorCitizenFragment;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyConstituencyActivity extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    public FragmentManager fragmentManager;


    Fragment cast_equation_fragment, election_result_fragment, male_female_fragment, age_fragment, senoir_citizen_fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_constituency_map);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);


        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


    }

    public void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Assign like this
        cast_equation_fragment = new CastEquationFragment();
        election_result_fragment = new ElectionResultFragment();
        male_female_fragment = new MaleFemaleFragment();
        age_fragment = new AgeFragment();
        senoir_citizen_fragment = new SeniorCitizenFragment();

        adapter.addFragment(cast_equation_fragment, "Cast Equation");
        adapter.addFragment(election_result_fragment, "Election Result");
        adapter.addFragment(male_female_fragment, "Male/Female");
        adapter.addFragment(age_fragment, "AGe Fragment");
        adapter.addFragment(senoir_citizen_fragment, "Senior Citizen");


        viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();


        public ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
