package com.software.cb.rajneethi.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.cb.rajneethi.R;

import org.eazegraph.lib.charts.PieChart;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by w7 on 10/28/2017.
 */

public class SummaryFragment extends Fragment {



    @BindView(R.id.piechart)
    public PieChart pieChart;

    @BindView(R.id.chart_recyclerview)
    public RecyclerView chartRecyclerview;

    public SummaryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, v);
        return v;
    }
}
