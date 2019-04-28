package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.RequestFactory;
import com.software.cb.rajneethi.utility.ServerUtils;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.utility.VolleySingleton;

/**
 * Created by monika on 5/10/2017.
 */

public class AddUserActivity extends Util {


    MyDatabase db;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindString(R.string.sms_delivered)
    String smsdeliverd;

    @BindString(R.string.sms_not_delivered)
    String smsNotDeliverd;

    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.role)
    TextView role;
    @BindView(R.id.btn_add_event)
    Button btn_add_event;

    @BindView(R.id.partyworker_recyclerview)
    RecyclerView recyclerView;

    String contacts = "";
    String booth_name = "";
    SharedPreferenceManager sharedPreferenceManager;
    private GPSTracker gpsTracker;
    ArrayList<String> list = new ArrayList<>();


    private String TAG = "Add User";


    private ArrayList<String> contacts_list = new ArrayList<>();

    // ArrayAdapter<?> adapter;
    private ArrayAdapter<String> adapter;


    String contactWithName = "";
    String type, id;
    @BindString(R.string.addUser)
    String toolbarTitle;

    @BindString(R.string.selectRole)
    String selectRole;

    @BindView(R.id.edtUsername)
    EditText edtUserName;

    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtMobileNumber)
    EditText edtMobile;
    private IOSDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        ButterKnife.bind(this);
        db = new MyDatabase(this);
        setup_toolbar_with_back(toolbar,toolbarTitle);
       // txt_title.setText(toolbarTitle);

        dialog = show_dialog(AddUserActivity.this, false);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        gpsTracker = new GPSTracker(AddUserActivity.this);


        String Role = sharedPreferenceManager.get_keep_login_role();
        // Toastmsg(AddUserActivity.this, "Role = " + Role);;
        list.add(0, selectRole);
        if (Role.equals("Supervisor")) {
            list.add(1, partyWorker);
            set_adapter_for_spinner(list);

        } else if (Role.equals("Client") || Role.equalsIgnoreCase("OPM")) {
            list.add(1, manager);
            list.add(2, supervisor);
            list.add(3, partyWorker);
            set_adapter_for_spinner(list);

        } else if (Role.equals("Manager")) {
            list.add(1, supervisor);
            list.add(2, partyWorker);
            set_adapter_for_spinner(list);

        }


    }



    public void back() {
        super.onBackPressed();
    }


    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;



    @BindString(R.string.validMobile)
    String validMobile;

    @OnClick(R.id.btn_add_event)
    public void add_event() {

        if (!(edtMobile.getText().toString().trim().isEmpty() || edtUserName.getText().toString().trim().isEmpty() || edtPassword.getText().toString().trim().isEmpty() || booth_name.isEmpty())) {

            if (edtMobile.getText().toString().trim().length() == 10) {
                dialog.show();
                registration();
            }else{
                Toastmsg(AddUserActivity.this, validMobile);
            }
        } else {
            Toastmsg(AddUserActivity.this, allFieldsRequired);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    private void set_adapter_for_spinner(ArrayList<String> options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                  //  booth_name = spinner.getSelectedItem().toString();
                    if (spinner.getSelectedItem().toString().equalsIgnoreCase(partyWorker)){
                        booth_name = "Party Worker";
                    }else if (spinner.getSelectedItem().toString().equalsIgnoreCase(supervisor)){
                        booth_name = "Supervisor";
                    }else{
                        booth_name = "Manager";
                    }
                    //   sharedPreferenceManager.set_booth(booth_name);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    booth_name = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });
    }


    @BindString(R.string.partyWorker)
    String partyWorker;
    @BindString(R.string.Manager)
    String manager;
    @BindString(R.string.supervisor)
    String supervisor;


    private void get_role() {

        try {

            Log.v("fggjdf601", API.GETROLEURL + sharedPreferenceManager.get_user_id());
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                    API.GETROLEURL + "GETUSERROLE&Id=" + sharedPreferenceManager.get_user_id(),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            dialog.dismiss();
                            //ArrayList<Voterlist> voterlists =new ArrayList();
                            try {
                                //Log.v("fggjdf", API.GETROLEURL + sharedPreferenceManager.get_user_id());
                                //String Role = response.getJSONObject(0).getString("role");
                                String Role = sharedPreferenceManager.get_keep_login_role();
                                // Toastmsg(AddUserActivity.this, "Role = " + Role);;
                                list.add(0, "Select Role");
                                if (Role.equals("Supervisor")) {
                                    list.add(1, partyWorker);
                                    set_adapter_for_spinner(list);

                                } else if (Role.equals("Client")) {
                                    list.add(1, manager);
                                    list.add(2, supervisor);
                                    list.add(3, partyWorker);
                                    set_adapter_for_spinner(list);

                                } else if (Role.equals("Manager")) {
                                    list.add(1, supervisor);
                                    list.add(2, partyWorker);
                                    set_adapter_for_spinner(list);

                                }

                                // set_adapter_for_spinner(list);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.w("Constituency636 ", "Response " + response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                            Toastmsg(AddUserActivity.this, Error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ServerUtils.getCustomHeaders();
                }
            };
            //  RequestFactory.getInstance(this).addToRequestQueue(jsObjRequest);
            RequestFactory.getInstance(getApplicationContext()).getRequestQueue().add(jsObjRequest);
            RequestFactory.getInstance(getApplicationContext()).getRequestQueue().getCache().clear();// cancelAll(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @BindString(R.string.successfullyAdded)
    String succss;
    @BindString(R.string.userExist)
    String userExist;

    @BindString(R.string.Error)
    String Error;

    //create User
    private void registration() {
        StringRequest request = new StringRequest(Request.Method.POST, API.ADDUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.w(TAG, "Response669 " + response);


                dialog.dismiss();
                if (!response.equals("{}")) {

                    Toastmsg(AddUserActivity.this, succss);
                    edtMobile.getText().clear();
                    edtPassword.getText().clear();
                    edtUserName.getText().clear();
                    booth_name = "";
                    spinner.setSelection(0);
                    allocateproject(response);
                    // allocate project();
                } else {
                    Toastmsg(AddUserActivity.this, userExist);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // dismiss_dialog();
                dialog.dismiss();
                Log.w(TAG, "Error " + error.toString());
                Toastmsg(AddUserActivity.this, Error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", edtUserName.getText().toString().trim());
                map.put("password", edtPassword.getText().toString().trim());
                map.put("phonenumber", edtMobile.getText().toString().trim());
                map.put("usertype", sharedPreferenceManager.getUserType());
                map.put("role", booth_name);
                return map;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void allocateproject(String response) {
        Log.w(TAG, "Response705 " + response);
        String[] userType = response.split(",");
        String[] subtemp = userType[0].split(":");
        String createdID = subtemp[1];
        Log.w("id", "" + createdID);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                API.ALLOCATEPROJECT + createdID + "&projectId=" + sharedPreferenceManager.get_project_id() + "&constId=" + sharedPreferenceManager.get_constituency_id()+"&usertype="+sharedPreferenceManager.getUserType()
                        +"&parentID="+sharedPreferenceManager.get_user_id(),
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response1) {
                sharedPreferenceManager.set_keep_login(true);
                //sharedPreferenceManager.set_keep_login_role("Client");
                Log.w("Add user717", "response" + sharedPreferenceManager.get_project_id());
                Log.w("Add user718 ", "Response " + response1);
                if (!response1.equals("{}")) {

                    edtMobile.getText().clear();
                    edtPassword.getText().clear();
                    edtUserName.getText().clear();
                    booth_name = "";
                    spinner.setSelection(0);
                    Toastmsg(AddUserActivity.this, succss);
                    // allocate project();
                } else {
                    Toastmsg(AddUserActivity.this, userExist);
                }
                // ArrayList<SupervisorPartyWorker> partyWorkerslist =new ArrayList();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

}











