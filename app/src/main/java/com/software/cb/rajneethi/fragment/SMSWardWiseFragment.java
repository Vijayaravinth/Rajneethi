package com.software.cb.rajneethi.fragment;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.CommunicationActivity;
import com.software.cb.rajneethi.adapter.CommunicationAdapter;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.checkInternet;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SMSWardWiseFragment extends Fragment{

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    ArrayList<BoothDetails> booth_list = new ArrayList<>();

    CommunicationAdapter adapter;
    MyDatabase db;
    SharedPreferenceManager sharedPreferenceManager;
    private String TAG = "WardWiseSMS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_communication,container,false);

        ButterKnife.bind(this,v);
        db = new MyDatabase(getActivity());
        sharedPreferenceManager = new SharedPreferenceManager(getActivity());

        setLayoutManager();

        setAdapter();

        addBooths();


        return v;
    }

    public void showSMSLayout(){


        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_top);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!((CommunicationActivity) Objects.requireNonNull(getActivity())).smsLayout.isShown()){
                ((CommunicationActivity) Objects.requireNonNull(getActivity())).smsLayout.setAnimation(anim);

                ((CommunicationActivity) Objects.requireNonNull(getActivity())).smsLayout.setVisibility(View.VISIBLE);
            }
        }else{
            if (!((CommunicationActivity)getActivity()).smsLayout.isShown()){
                ((CommunicationActivity)getActivity()).smsLayout.setAnimation(anim);
                ((CommunicationActivity)getActivity()).smsLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addBooths() {

        booth_list.add(new BoothDetails("Ward 1", false));
        booth_list.add(new BoothDetails("Ward 2", false));
        booth_list.add(new BoothDetails("Ward 3", false));
        booth_list.add(new BoothDetails("Ward 4", false));
        booth_list.add(new BoothDetails("Ward 5", false));
        booth_list.add(new BoothDetails("Ward 6", false));
        booth_list.add(new BoothDetails("Ward 7", false));
        booth_list.add(new BoothDetails("Ward 8", false));

        adapter.notifyDataSetChanged();
    }


    private void setLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void setAdapter() {
        adapter = new CommunicationAdapter(getActivity(), booth_list, SMSWardWiseFragment.this);
        recyclerView.setAdapter(adapter);
    }

}
