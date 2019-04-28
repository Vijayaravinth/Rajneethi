package com.software.cb.rajneethi.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.software.cb.rajneethi.R;


import butterknife.ButterKnife;

/**
 * Created by Monica on 06-03-2017.
 */

public class CastEquationFragment extends Fragment {




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_case_equation, container, false);
        ButterKnife.bind(this, v);

        return v;
    }


}
