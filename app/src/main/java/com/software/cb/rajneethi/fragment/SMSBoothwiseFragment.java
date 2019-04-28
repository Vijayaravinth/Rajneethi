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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.CommunicationActivity;
import com.software.cb.rajneethi.activity.VoterSearchActivity;
import com.software.cb.rajneethi.adapter.CommunicationAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SMSBoothwiseFragment extends Fragment {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    ArrayList<BoothDetails> booth_list = new ArrayList<>();

    CommunicationAdapter adapter;
    MyDatabase db;
    SharedPreferenceManager sharedPreferenceManager;
    private String TAG = "BoothWiseSMS";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_communication, container, false);
        ButterKnife.bind(this, v);


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
        try {
            Cursor c = db.getBooths();
            Log.w(TAG, "Booth count :" + c.getCount());
            if (c.moveToFirst()) {
                do {
                    booth_list.add(new BoothDetails(c.getString(0), false));
                } while (c.moveToNext());
            }
            c.close();

            if (booth_list.size() > 0) {
                adapter.notifyDataSetChanged();
            }
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            if (checkInternet.isConnected()) {
                get_booth_details();
            } else {
                Toast.makeText(getActivity(), noInternet, Toast.LENGTH_SHORT).show();


            }
        }
    }

    @BindString(R.string.noInternet)
    String noInternet;

    /*get booth details*/
    private void get_booth_details() {
        StringRequest request = new StringRequest(Request.Method.GET, API.BOOTH_INFO + "userId=" + sharedPreferenceManager.get_user_id() + "&constituencyId=" + sharedPreferenceManager.get_constituency_id() + "&projectId=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.w(TAG, "Response " + response);
                try {
                    JSONArray array = new JSONArray(response);

                    if (array.length() > 0) {

                        db.deleteBooths();

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            //    int status = object.getInt("astatus");

                            db.insertBooths(object.getString("booth"));
                            booth_list.add(new BoothDetails(object.getString("booth"), false));


                        }

                        if (booth_list.size() > 0) {
                            adapter.notifyDataSetChanged();
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());


                Toast.makeText(getActivity(), Error, Toast.LENGTH_SHORT).show();

            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    @BindString(R.string.Error)
    String Error;

    private void setLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void setAdapter() {
        adapter = new CommunicationAdapter(getActivity(), booth_list, SMSBoothwiseFragment.this);
        recyclerView.setAdapter(adapter);
    }
}
