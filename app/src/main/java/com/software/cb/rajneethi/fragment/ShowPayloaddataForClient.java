package com.software.cb.rajneethi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.SurveyStatsActivity;
import com.software.cb.rajneethi.adapter.PayloadDataAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.SurveyStats;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static android.view.View.GONE;

/**
 * Created by DELL on 21-01-2018.
 */

public class ShowPayloaddataForClient extends DialogFragment {

    String TAG = "ShowPayload";

    public static ShowPayloaddataForClient newInstance(SurveyStats details) {
        ShowPayloaddataForClient frag = new ShowPayloaddataForClient();
        Bundle args = new Bundle();
        // args.putString("data", payloadData);
        args.putSerializable("data", details);
        frag.setArguments(args);
        return frag;
    }

    public static interface updateData {

        public void update(String flag);
    }

    updateData listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener  = (updateData) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payload_data, container);

        ButterKnife.bind(this, view);
        return view;
    }

    @BindView(R.id.txtPayloadData)
    TextView txtPayloadData;

    @BindView(R.id.callLayout)
    LinearLayout callLayout;
    @BindView(R.id.validateLayout)
    LinearLayout validateLayout;
    @BindView(R.id.invalidateLayout)
    LinearLayout invalidateLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.payloadDataRecyclerView)
    RecyclerView recyclerView;

    @BindString(R.string.Error)
    String errorMsg;

    @BindString(R.string.noInternet)
    String noInternet;

    String payloadData = "";

    SurveyStats details;

    IOSDialog dialog;

    @BindString(R.string.alreadyUpdated)
    String alreadyUpdated;

    @BindString(R.string.successfullyAdded)
    String success;

    String mobileNumber = "";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().addFlags(Window.FEATURE_NO_TITLE);
        //   final String payloadData = getArguments().getString("data");

        details = (SurveyStats) getArguments().getSerializable("data");

        dialog = new IOSDialog.Builder(getActivity())
                .setTitle("Loading")
                .setTitleColorRes(R.color.icons)
                .setCancelable(false)
                .setSpinnerColor(ContextCompat.getColor(getActivity(), R.color.icons))
                .build();

        callLayout.setVisibility(GONE);
        validateLayout.setVisibility(GONE);
        txtPayloadData.setVisibility(GONE);
        invalidateLayout.setVisibility(GONE);


        if (checkInternet.isConnected()) {
            progressBar.setVisibility(View.VISIBLE);
            getPayloadData(details.getSurveyId());

        } else {
            Toast.makeText(getActivity(), noInternet, Toast.LENGTH_LONG).show();
        }


        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.w(TAG, "call clicked :"+ mobileNumber);

             //   if (getActivity() instanceof SurveyStatsActivity) {
                    if (!mobileNumber.isEmpty()) {
                        ((SurveyStatsActivity) getActivity()).call(details.getMobile());
                   }
                //}
               /* if (details.isShowBottomLayout()) {
                    ((ListenToPeopleActivity) getActivity()).pauseAudio();

                    if (!mobileNumber.isEmpty()) {

                        ((ListenToPeopleActivity) getActivity()).call(mobileNumber);

                    }
                } else {
                    if (!mobileNumber.isEmpty()) {

                        ((TrackSurveyActivity) getActivity()).call(mobileNumber);


                    }
                }*/
            }
        });

        validateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "validate clicked : " + details.getSurveyFlag());

                if (checkInternet.isConnected()){
                    dialog.show();
                    validateOrInvalidate("yes", details.getSurveyId());
                }

              /*  if (details.getSurveyFlag().equalsIgnoreCase("Y2V")) {
                    dialog.show();
                    validateOrInvalidate("yes", details.getSurveyId());
                } else {
                    if (details.getSurveyFlag().equalsIgnoreCase("yes") || details.getSurveyFlag().equalsIgnoreCase("no")) {
                        dialog.show();
                        validateOrInvalidate("yes", details.getSurveyId());
                    }


                }*/
            }

        });


        invalidateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "invalidate clicked : " + details.getSurveyFlag());

                if (checkInternet.isConnected()){
                    dialog.show();
                    validateOrInvalidate("no", details.getSurveyId());
                }

              /*  if (details.getSurveyFlag().equalsIgnoreCase("Y2V")) {
                    dialog.show();
                    validateOrInvalidate("no", details.getSurveyId());
                } else {
                    if (details.getSurveyFlag().equalsIgnoreCase("yes") || details.getSurveyFlag().equalsIgnoreCase("no")) {
                        dialog.show();
                        validateOrInvalidate("no", details.getSurveyId());
                    }
                }*/

            }
        });
    }

    //validate or invalidate
    private void validateOrInvalidate(final String msg, String surveyId) {

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(API.VALIDATE_SURVEY + "&surveyid=" + surveyId + "&statusFlag=" + msg).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                //
                e.printStackTrace();

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                final String myResponse = response.body().string();

                Log.w(TAG, "response :" + myResponse);
                // dialog.dismiss();

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                            if (!myResponse.equals("[]")) {

                                if (myResponse.contains("[{\"msg\":\"Success\"}]")) {
                                    Toast.makeText(getActivity(), success, Toast.LENGTH_LONG).show();

                                    listener.update(msg);
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });




       /* StringRequest request = new StringRequest(Request.Method.GET, API.VALIDATE_SURVEY + "&surveyid=" + surveyId + "&statusFlag=" + msg, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                details.setSurveyFlag(msg);

                if (!response.equals("[]")) {

                    if (response.contains("[{\"msg\":\"Success\"}]")) {
                        Toast.makeText(getActivity(), success, Toast.LENGTH_LONG).show();
                    }
                }

                Log.w(TAG, "Response :" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });


        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);*/
    }

    //get payload dat
    private void getPayloadData(String surveyId) {
        Log.w(TAG, "surveyId:" + surveyId);

        Log.w(TAG, "surveyId:" + surveyId);
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url(API.GET_PAYLOADDATA + "&surveyid=" + surveyId).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                //
                e.printStackTrace();

                Log.w(TAG, "Error " + call.toString());
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtPayloadData.setText(errorMsg);
                            recyclerView.setVisibility(GONE);
                            progressBar.setVisibility(GONE);
                            txtPayloadData.setVisibility(View.VISIBLE);
                        }
                    });
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                // failed();

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                final String myResponse = response.body().string();


                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(GONE);

                            if (!myResponse.equals("[]")) {

                                txtPayloadData.setVisibility(View.GONE);
                                //if (details.isShowBottomLayout()) {

                                callLayout.setVisibility(View.VISIBLE);
                                validateLayout.setVisibility(View.VISIBLE);
                                invalidateLayout.setVisibility(View.VISIBLE);


                                ArrayList<String> list = new ArrayList<>();
                                // }
                                try {

                                    JSONArray array = new JSONArray(myResponse);
                                    for (int i = 0; i <= array.length() - 1; i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        //  payloadData += object.getString("attribute") + " : " + object.getString("value");
                                        list.add(object.getString("attribute") + " : " + object.getString("value"));
                                        if (object.getString("attribute").equals("Mobile Number") || object.getString("attribute").equals("mobile") || object.getString("attribute").equals("ps-32d") || object.getString("attribute").equals("s6-mobile")) {

                                            mobileNumber = object.getString("value");
                                        }

                                    }

                                    if (list.size() > 0) {

                                        recyclerView.setVisibility(View.VISIBLE);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setAdapter(new PayloadDataAdapter(list, getActivity()));
                                        //  recyclerView.addItemDecoration(new dividerLineForRecyclerView(getActivity()));

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                txtPayloadData.setText(errorMsg);
                                txtPayloadData.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });




       /* StringRequest request = new StringRequest(Request.Method.GET, API.GET_PAYLOADDATA + "&surveyid=" + surveyId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(GONE);
                Log.w(TAG, "Response :" + response);

                if (!response.equals("[]")) {

                    txtPayloadData.setVisibility(View.GONE);
                    //if (details.isShowBottomLayout()) {

                    callLayout.setVisibility(View.VISIBLE);
                    validateLayout.setVisibility(View.VISIBLE);
                    invalidateLayout.setVisibility(View.VISIBLE);


                    ArrayList<String> list = new ArrayList<>();
                    // }
                    try {

                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            //  payloadData += object.getString("attribute") + " : " + object.getString("value");
                            list.add(object.getString("attribute") + " : " + object.getString("value"));
                            if (object.getString("attribute").equals("Mobile Number") || object.getString("attribute").equals("mobile")|| object.getString("attribute").equals("ps-32d") || object.getString("attribute").equals("s6-mobile") ) {
                                mobileNumber = object.getString("value");
                            }

                        }

                        if (list.size() > 0) {

                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(new PayloadDataAdapter(list, getActivity()));
//                            recyclerView.addItemDecoration(new dividerLineForRecyclerView(getActivity()));

                        }
                        //  txtPayloadData.setText(payloadData);
                        //txtPayloadData.setMovementMethod(new ScrollingMovementMethod());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    txtPayloadData.setText(errorMsg);
                    txtPayloadData.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtPayloadData.setText(errorMsg);
                recyclerView.setVisibility(GONE);
                progressBar.setVisibility(GONE);
                txtPayloadData.setVisibility(View.VISIBLE);
                Log.w(TAG, "Error :" + error.toString());
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
}
