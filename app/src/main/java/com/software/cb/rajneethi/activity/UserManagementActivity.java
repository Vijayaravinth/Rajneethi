package com.software.cb.rajneethi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
//import com.blanche.carte.rajneethi.adapter.ArraySwipeAdapterSample;
//import com.blanche.carte.rajneethi.adapter.ListViewAdapter;
import com.software.cb.rajneethi.adapter.UserAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.Partyworkerstats;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.supervisormigratoon.PartyWorkers;
import com.software.cb.rajneethi.supervisormigratoon.pojo.SupervisorPartyWorker;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.ServerUtils;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserManagementActivity extends Util implements checkInternet.ConnectivityReceiverListener {


    private Context mContext = this;

    private SharedPreferenceManager sharedPreferenceManager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<SupervisorPartyWorker> partyWorkerslist = new ArrayList<>();


    @BindView(R.id.partyworkers1)
    RecyclerView recyclerView;
    UserAdapter adapter;

    public String TAG = "UserManagement";
    String Role;

    @BindString(R.string.userManagement)
    String toolbarTitle;

    private IOSDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        ButterKnife.bind(this);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        dialog = show_dialog(UserManagementActivity.this, false);

        setup_toolbar_with_back(toolbar,toolbarTitle);
        // txt_title.setText("User Management ");
        //Toastmsg(UserManagementActivity.this, "something");
        Role = sharedPreferenceManager.get_keep_login_role();

        Log.w(TAG, "Role : " + Role);
        partyWorkerslist.clear();
        set_Linearlayout_manager(recyclerView, this);
        set_adapter();

        //  call_api();
    }

    public String getRole(){
        return  sharedPreferenceManager.get_keep_login_role();
    }

    /*call api based on role*/
    private void call_api() {

        if (checkInternet.isConnected()) {
            dialog.show();
            fetch();
        } else {
            Toastmsg(UserManagementActivity.this, noInternet);
        }
      /*  if (Role.equals("Supervisor")) {

            Log.w(TAG, "Supervisor : ");
            dialog.show();
            fetchPartyWorkerFromServer();
        } else if (Role.equals("Manager")) {
            Log.w(TAG, "Manager : ");
            dialog.show();
            fetchUsersforManager();
        } else if (Role.equals("Client")) {
            Log.w(TAG, "Client : ");
            dialog.show();
            fetch();
        }*/
    }


    //delete user
    public void delete_user(final int pos, final String id) {
        Log.w(TAG, "user id : " + id);
        delete_alert_dialog(pos, id);
    }

    @BindString(R.string.delete)
    String delete;
    @BindString(R.string.confirmDelete)
    String confirmDelete;
    @BindString(R.string.noInternet)
    String noInternet;

    //alert dialog for delete
    private void delete_alert_dialog(final int pos, final String user_id) {
        new AlertDialog.Builder(this)
                .setTitle(delete)
                .setMessage(confirmDelete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (checkInternet.isConnected()) {


                            delete(user_id, pos);

                        } else {
                            Toastmsg(UserManagementActivity.this, noInternet);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
    }

    public void callToSendSMS(String message, String mobile) {

        if (checkInternet.isConnected()) {
            dialog.show();
            sendSms(message, mobile);
        } else {
            Toastmsg(this, noInternet);
        }
    }

    @BindString(R.string.yes)
    String yes;

    @BindString(R.string.no)
    String no;

    @BindString(R.string.message)
    String message;
    @BindString(R.string.enterMessage)
    String enterMessage;

    public void alertForSendMessage(final String mobile) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserManagementActivity.this);
        alertDialog.setTitle(message);
        alertDialog.setMessage(enterMessage);

        final EditText input = new EditText(UserManagementActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.rajneethilogo1);

        alertDialog.setPositiveButton(yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log.w(TAG, "Message : " + input.getText().toString().trim());
                        String message = input.getText().toString().trim();
                        if (message.length() > 0) {
                            callToSendSMS(message, mobile);
                        }
                    }
                });

        alertDialog.setNegativeButton(no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    public void sendSms(String message, String mobile) {


        StringRequest request = new StringRequest(Request.Method.GET, "http://103.16.101.52:8080/sendsms/bulksms?username=nxtr-vlgokul&password=123456&type=0&dlr=1&destination=" + mobile + "&source=NXTEDU&message=" + message, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Toastmsg(UserManagementActivity.this, "Success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(UserManagementActivity.this, Error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        queue.getCache().clear();
    }


    @BindString(R.string.Error)
    String Error;

    //delete
    private void delete(final String userid, final int pos) {

        dialog.show();
        Log.w(TAG, "delete url : " + API.DELETE_USER + userid + "&userId=" + userid);
        StringRequest request = new StringRequest(Request.Method.GET, API.DELETE_USER + userid + "&userId=" + userid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                Log.w(TAG, "Response : " + response);

                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject object = array.getJSONObject(0);
                    int status = object.getInt("deletestatus");
                    if (status == 0) {

                        partyWorkerslist.remove(pos);
                        adapter.notifyDataSetChanged();


                    } else {
                        Toastmsg(UserManagementActivity.this, Error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.w(TAG, "error : " + error.toString());
                Toastmsg(UserManagementActivity.this, Error);
            }
        });/*{


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Id", userid);
                    return map;
                }
        };*/

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    //set adapter\
    private void set_adapter() {
        adapter = new UserAdapter(partyWorkerslist, this, UserManagementActivity.this);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));
    }


    /*@OnClick(R.id.fab)
    public void add_events() {
        //startActivity(new Intent(UserManagementActivity.this, AddUserActivity.class));
    }*/


    /*fet all users*/
    public void fetch() {


        StringRequest request = new StringRequest(Request.Method.GET, API.GETROLEURL + "GETALLUSERSOFPROJECT&projectID=" + sharedPreferenceManager.get_project_id() + "&cid=" + sharedPreferenceManager.get_constituency_id()
                + "&userId=" + sharedPreferenceManager.get_user_id() + "&userRole=" + sharedPreferenceManager.get_keep_login_role() +
                "&userType=" + sharedPreferenceManager.getUserType(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                partyWorkerslist.clear();

                Log.w(TAG, "Response :" + response);

                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject json = array.getJSONObject(i);

                        if (sharedPreferenceManager.get_keep_login_role().equals("Client") || sharedPreferenceManager.get_keep_login_role().equals("Manager")) {
                            if (!sharedPreferenceManager.get_username().equalsIgnoreCase(json.getString("username"))) {
                                SupervisorPartyWorker worker = new SupervisorPartyWorker(json.getInt("id"), json.getString("username"), json.getString("parentid"), json.getString("role"), json.getString("phonenumber"), json.getInt("count") + "");
                                Log.w("Worker", "" + worker);
                                partyWorkerslist.add(worker);
                            }
                        } else {
                            if (json.getInt("parentid") == Integer.parseInt(sharedPreferenceManager.get_user_id())) {
                                SupervisorPartyWorker worker = new SupervisorPartyWorker(json.getInt("id"), json.getString("username"), json.getString("parentid"), json.getString("role"), json.getString("phonenumber"), json.getInt("count") + "");
                                Log.w("Worker", "" + worker);
                                partyWorkerslist.add(worker);
                            }
                        }

                    }
                } catch (JSONException e) {
                    //Toastmsg(UserManagementActivity.this, "third");
                    e.printStackTrace();
                }
                //hidePDialog();

                if (partyWorkerslist.size() > 0) {
                    adapter.notifyDataSetChanged();
                } else {
                    Toastmsg(UserManagementActivity.this, noRecorrdFound);
                }
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toastmsg(UserManagementActivity.this, Error);
            }
        });

        //VolleySingleton.getInstance(UserManagementActivity.this).addToRequestQueue(request);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        queue.getCache().clear();
    }

    public String getSupervioserName(String id){
        String name = "";
        for (int i = 0 ; i <= partyWorkerslist.size()-1 ;i++){

            SupervisorPartyWorker worker = partyWorkerslist.get(i);
            String userid = worker.getId()+"";
            if (userid.equalsIgnoreCase(id)){
                name = worker.getName();
                break;
            }

        }

        return name;
    }

    private class QuestionOrderComparator implements Comparator<SupervisorPartyWorker> {


        @Override
        public int compare(SupervisorPartyWorker supervisorPartyWorker, SupervisorPartyWorker t1) {
            if (supervisorPartyWorker.getSupervisorId().equalsIgnoreCase(t1.getSupervisorId())) {

                return 0;
            }else {
                return -1;
            }
        }
    }


    @BindString(R.string.noRecordFound)
    String noRecorrdFound;

    private void fetchAllUsers() {
        // Toastmsg(UserManagementActivity.this, "something");

        try {
            // Toastmsg(UserManagementActivity.this, "something11 " + sharedPreferenceManager.get_project_id() );

            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                    API.GETROLEURL + "GETALLUSERSOFPROJECT&projectID=" + sharedPreferenceManager.get_project_id(),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Toastmsg(UserManagementActivity.this, "something11212 " + sharedPreferenceManager.get_project_id());
                            sharedPreferenceManager.set_keep_login(true);
                            // sharedPreferenceManager.set_keep_login_role("Client");

                            // Log.w("value","",+sharedPreferen);
                            Log.w("Constituency240 ", "Response " + response);
                            //Toastmsg(UserManagementActivity.this, "Second");
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    SupervisorPartyWorker worker = new SupervisorPartyWorker(response.getJSONObject(i));
                                    Log.w("Worker", "" + worker);
                                    partyWorkerslist.add(worker);
                                } catch (JSONException e) {
                                    //Toastmsg(UserManagementActivity.this, "third");
                                    e.printStackTrace();
                                }
                            }
                            //hidePDialog();

                            if (partyWorkerslist.size() > 0) {

                                adapter.notifyDataSetChanged();


                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ServerUtils.getCustomHeaders();
                }
            };
            // RequestFactory.getInstance(this).addToRequestQueue(jsObjRequest);
            VolleySingleton.getInstance(UserManagementActivity.this).addToRequestQueue(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.add:
                startActivity(new Intent(UserManagementActivity.this, AddUserActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        call_api();
    }

    /*@Override*/
   /* public void onClick(View v) {
        // startActivity(new Intent( SupervisorView.class,));
    }*/
}







