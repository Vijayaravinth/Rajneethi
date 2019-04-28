package com.software.cb.rajneethi.qc;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.QCUserAdapter;
import com.software.cb.rajneethi.adapter.StatisticsAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.QCUsers;
import com.software.cb.rajneethi.models.UserStatistics;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by DELL on 03-04-2018.
 */

public class QCUsersActivity extends UtilActivity {


    @BindView(R.id.userRecyclerview)
    RecyclerView recyclerView;

    ArrayList<QCUsers> usersList = new ArrayList<>();

    QCUserAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.statistics)
    String qc;

    private String TAG = "QC User";

    @BindString(R.string.noInternet)
    String noInternet;

    @BindString(R.string.Error)
    String Error;

    @BindString(R.string.noRecordFound)
    String noRecordFound;

    IOSDialog dialog;

    @BindView(R.id.btnRetry)
    Button btnRetry;

    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.statisticsLayout)
    RelativeLayout statisticsLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btnStatisticsRetry)
    Button btnStatisticsRetry;
    @BindView(R.id.statisticsRecyclerview)
    RecyclerView statisticsRecyclerView;

    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qc_users);
        ButterKnife.bind(this);


        dialog = show_dialog(QCUsersActivity.this, false);
        setup_toolbar_with_back(toolbar, qc);

        set_Linearlayout_manager(recyclerView, this);
        set_Linearlayout_manager(statisticsRecyclerView, this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        setAdapter();
        callAPI();

    }

    @OnClick(R.id.btnRetry)
    public void retry() {
        callAPI();
    }


    private void callAPI() {
        if (checkInternet.isConnected()) {
            dialog.show();
            getStatistics();
        } else {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    ArrayList<String> userNameList = new ArrayList<>();
    ArrayList<UserStatistics> statisticsList = new ArrayList<>();


    StatisticsAdapter statisticsAdapter;

    private void setAdapterForUserStatistics() {

        statisticsAdapter = new StatisticsAdapter(statisticsList, this);
        statisticsRecyclerView.setAdapter(statisticsAdapter);
    }

    private void getUserStatistics(String name) {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_USER_STATISTICS + name, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "response :" + response);
                try {
                    progressBar.setVisibility(GONE);
                    statisticsList.clear();
                    JSONArray array = new JSONArray(response);
                    if (array.length() > 0) {

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            statisticsList.add(new UserStatistics(object.getString("boothname"), object.getString("verified"), object.getString("rejected"), object.getString("unverified"), object.getString("total")));
                            //  }else{

                                /*if (object.getString("parentid").equals(sharedPreferenceManager.get_user_id())) {
                                    statisticsList.add(new UserStatistics(object.getString("boothname"), object.getString("verified"), object.getString("rejected"), object.getString("unverified"), object.getString("total")));

                                }*/

                            // }
                        }

                        if (statisticsAdapter == null) {
                            setAdapterForUserStatistics();
                        } else {
                            statisticsAdapter.notifyDataSetChanged();
                        }

                        toolbar.setTitle(name);

                    } else {
                        Toastmsg(QCUsersActivity.this, "No data found for this user");
                        statisticsLayout.setVisibility(GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(GONE);
                btnStatisticsRetry.setVisibility(View.VISIBLE);
                if (statisticsAdapter == null) {
                    setAdapterForUserStatistics();
                } else {
                    statisticsAdapter.notifyDataSetChanged();
                }

                statisticsList.clear();
                Log.w(TAG, "Errror : " + error.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void getStatistics() {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_USER_BOOTHS + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "Response : " + response);


                int total = 0;
                dialog.dismiss();

                btnRetry.setVisibility(GONE);
                try {
                    JSONArray array = new JSONArray(response);

                    if (array.length() > 0) {

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Client") || sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Manager")) {

                                userNameList.add(object.getString("username"));
                            }else{
                                if (object.getString("parentid").equals(sharedPreferenceManager.get_user_id())) {
                                    userNameList.add(object.getString("username"));
                                }
                            }
                        }


                        if (userNameList.size() > 0) {
                            Set<String> hs = new HashSet<>();
                            hs.addAll(userNameList);
                            userNameList.clear();
                            userNameList.addAll(hs);


                            adapter.notifyDataSetChanged();
                        }else{
                            Toastmsg(QCUsersActivity.this,"no data found");
                        }

                    } else {
                        btnRetry.setText("No record found");
                    }

                    /*if (array.length() > 0) {

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            total += object.getInt("count");
                            usersList.add(new QCUsers(object.getString("boothname") + " - " + object.getString("count")));
                        }

                    } else {
                        Toastmsg(QCUsersActivity.this, noRecordFound);
                    }

                    if (usersList.size() > 0) {
                        usersList.add(new QCUsers("Total " + " - " + total));


                        adapter.notifyDataSetChanged();
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                btnRetry.setVisibility(View.VISIBLE);
                dialog.dismiss();
                Toastmsg(QCUsersActivity.this, Error);
                Log.w(TAG, "Error " + error.toString());
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void setAdapter() {

        adapter = new QCUserAdapter(this, userNameList, QCUsersActivity.this);
        recyclerView.setAdapter(adapter);


    }

    @OnClick(R.id.btnStatisticsRetry)
    public void statistisRetry() {
        if (!username.isEmpty()) {
            showStatistics(username);
        }
    }

    String username = "";

    public void showStatistics(String username) {


        if (checkInternet.isConnected()) {

            this.username = username;

            mainLayout.setVisibility(GONE);
            statisticsLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            getUserStatistics(username);

        } else {
            Toastmsg(QCUsersActivity.this, "No internet connection");
        }
    }


    private void goBack() {
        if (statisticsLayout.isShown()) {
            toolbar.setTitle("Statistics");
            statisticsLayout.setVisibility(GONE);
            mainLayout.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
