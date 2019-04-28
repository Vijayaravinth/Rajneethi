package com.software.cb.rajneethi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.regions.Regions;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.qc.QCUsersActivity;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.MyApplication;
import com.software.cb.rajneethi.utility.ServerUtils;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

/**
 * Created by Monica on 09-03-2017.
 */

public class LoginActivity extends ActivityManagePermission implements checkInternet.ConnectivityReceiverListener {


    @BindView(R.id.edt_username)
    EditText edt_username;

    @BindView(R.id.edt_password)
    EditText edt_password;

    @BindView(R.id.linearlayout)
    LinearLayout layout;

    @BindView(R.id.spinner)
    Spinner spinner;

    private String TAG = "Login";

    private IOSDialog dialog;

    private SharedPreferenceManager sharedPreferenceManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceManager = new SharedPreferenceManager(this);


        if (!sharedPreferenceManager.getLanguage().isEmpty()) {
            changeLanguage(sharedPreferenceManager.getLanguage());
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }


        new MyDatabase(this).deleteBooths();


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        dialog = new IOSDialog.Builder(this)
                .setTitle("Loading")
                .setTitleColorRes(R.color.icons)
                .setCancelable(false)
                .setSpinnerColor(ContextCompat.getColor(this, R.color.icons))

                .build();


        AWSMobileClient.getInstance().initialize(this, new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                Log.w(TAG, "AWSMobileClient initialized. User State is " + result.getUserState());

            }

            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Initialization error.", e);
            }
        });



    /*    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.w(TAG, "Refreshed token: " + refreshedToken);
        if (refreshedToken != null) {
            sharedPreferenceManager.setFCMKey(refreshedToken);
        }*/


    }

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    private void initializeRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()

                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);


        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.w(TAG, "Task failure :");
                        }
                        initialize();
                    }
                });
    }

    private void initialize() {

    }

    private String getCurrentateAndTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        Date date = new Date();
        return formatter.format(date);
    }

    //change language
    private void changeLanguage(String languageToLoad) {

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        sharedPreferenceManager.set_Language(languageToLoad);
    }


    //check permissions
    private void checkPermission() {
        askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_READ_PHONE_STATE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_READ_CONTACTS, PermissionUtils.Manifest_RECORD_AUDIO}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                //permission granted
                //replace with your action
            }

            @Override
            public void permissionDenied() {
                //permission denied
                //replace with your action
            }

            @Override
            public void permissionForeverDenied() {
                // user has check 'never ask again'
                // you need to open setting manually
                //  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                //  Uri uri = Uri.fromParts("package", getPackageName(), null);
                //   intent.setData(uri);
                //  startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            }
        });
    }


    @BindString(R.string.makeSureInternet)
    String makeSureInternet;

    public void alert_for_no_internet() {
        new AlertDialog.Builder(this)
                .setTitle(noInternet)
                .setMessage(makeSureInternet)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

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

   /* //set toolbar text style
    public void toolbar_text_style() {
        s = new SpannableString(getResources().getString(R.string.gallery_activity));
        Typeface type = Typeface.createFromAsset(getAssets(), "GeosansLight.ttf");
        s.setSpan(new CustomTypefaceSpan(type), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }*/

    public void Toastmsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void Snackbar_msg(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show();
    }


    @OnClick(R.id.btn_login)
    public void login() {

        if (!(edt_password.getText().toString().trim().isEmpty() || edt_username.getText().toString().trim().isEmpty())) {
            if (checkInternet.isConnected()) {
                if (sharedPreferenceManager.get_keep_login()) {
                    if (sharedPreferenceManager.get_username().equals(edt_username.getText().toString().trim())) {
                        sharedPreferenceManager.set_keep_login(true);
                        startActivity(new Intent(LoginActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        LoginActivity.this.finish();

                    }
                } else {

                    Log.w(TAG, "login called");
                    dialog.show();
                    send_login_request();
                }

            } else {
                alert_for_no_internet();
            }

        } else {
            Toastmsg(LoginActivity.this, allFieldsRequired);
        }

    }

    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    /*set adapter for spinner*/
    private void set_adapter_for_spinner(ArrayList<String> options) {
        if (options.size() == 0) {

            options.add(0, "Please select Booth");
            layout.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
            spinner.setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position != 0) {
                        // launchActivity(ProjectActivity.class);
                        if (checkInternet.isConnected()) {
                            sharedPreferenceManager.set_booth(spinner.getSelectedItem().toString());
                            dialog.show();
                            download_project();
                        } else {
                            //Toastmsg(LoginActivity.this, "No internet connection try again");
                            alert_for_no_internet();
                        }
                    } else {
                        Toastmsg(LoginActivity.this, "please select booth");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            if (checkInternet.isConnected()) {
                // sharedPreferenceManager.set_booth(spinner.getSelectedItem().toString());
                dialog.show();
                download_project();
            } else {
                //Toastmsg(LoginActivity.this, "No internet connection try again");
                alert_for_no_internet();
            }
        }
    }

    String userid = "";

    @BindString(R.string.noInternet)
    String noInternet;
    @BindString(R.string.incorrect)
    String usernamepasswordIncorrect;

    private void send_login_request() {
        StringRequest request = new StringRequest(Request.Method.POST, API.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                dialog.dismiss();
                Log.w(TAG, "Response268 " + response);
                if (!response.equals("[]")) {
                    try {
                        JSONObject object = new JSONObject(response);

                        userid = object.getString("id");
                        Log.w("Login", "user id " + userid);
                        sharedPreferenceManager.set_username(edt_username.getText().toString().trim());
                        //  sharedPreferenceManager.set_user_id(userid);
                        //  sharedPreferenceManager.setPassword(edt_password.getText().toString().trim());

                        if (checkInternet.isConnected()) {
                            getRole();
                        } else {
                            Toastmsg(LoginActivity.this, noInternet);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toastmsg(LoginActivity.this, usernamepasswordIncorrect);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

                Log.w(TAG, "Error " + error.toString());
                Toastmsg(LoginActivity.this, Error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", edt_username.getText().toString().trim());
                map.put("password", edt_password.getText().toString().trim());
                //   map.put("fcmkey",sharedPreferenceManager.getFCMKey());
                return map;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    @BindString(R.string.Error)
    String Error;

    @BindString(R.string.noRecordFound)
    String noRecordFound;

    public void getRole() {
        try {
            Log.v("fggjdf309", API.GETROLEURL + userid);
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET,
                    API.GETROLEURL + "GETUSERROLE&Id=" + userid,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //ArrayList<Voterlist> voterlists =new ArrayList();
                            if (response.length() > 0) {

                                try {
                                    String Role = response.getJSONObject(0).getString("userrole");
                                    String project_id = response.getJSONObject(0).getInt("projectId") + "";
                                    String constituencyId = response.getJSONObject(0).getInt("constituencyId") + "";

                                    Log.w(TAG, "user role :" + Role);
                                    if (!(constituencyId.isEmpty() && project_id.isEmpty())) {

                                        if (response.getJSONObject(0).getString("userType").equalsIgnoreCase("opcp") || response.getJSONObject(0).getString("userType").equalsIgnoreCase("kp") || response.getJSONObject(0).getString("userType").equalsIgnoreCase("ep")) {

                                            sharedPreferenceManager.set_keep_login_role(Role);
                                            sharedPreferenceManager.set_user_type(response.getJSONObject(0).getString("userType"));
                                            sharedPreferenceManager.set_project_id(project_id);
                                            sharedPreferenceManager.set_Constituency_id(constituencyId);
                                            sharedPreferenceManager.set_user_id(userid);


                                            if (sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("party Worker")) {


                                                if (checkInternet.isConnected()) {
                                                    dialog.show();
                                                    getLoginStatus(sharedPreferenceManager.get_username(), sharedPreferenceManager.getUserType(), true, response);
                                                } else {

                                                    Toastmsg(LoginActivity.this, noInternet);
                                                }


                                            } else {
                                                sharedPreferenceManager.set_keep_login(true);
                                                startActivity(new Intent(LoginActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                LoginActivity.this.finish();
                                            }


                                        } else {
                                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/voters.db");

                                            Log.w(TAG, "user id :" + userid + " " + project_id + " stored values :" + sharedPreferenceManager.get_user_id() + " " + sharedPreferenceManager.get_project_id() + " file exist :" + file.exists());

                                            sharedPreferenceManager.set_keep_login_role("");

                                            if (userid.equals(sharedPreferenceManager.get_user_id()) && project_id.equals(sharedPreferenceManager.get_project_id()) && file.exists()) {

                                                sharedPreferenceManager.set_keep_login_role(Role);
                                                sharedPreferenceManager.set_user_type(response.getJSONObject(0).getString("userType"));
                                                sharedPreferenceManager.set_project_id(project_id);
                                                sharedPreferenceManager.set_Constituency_id(constituencyId);
                                                sharedPreferenceManager.set_user_id(userid);


                                                if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {

                                                    if (checkInternet.isConnected()) {
                                                        dialog.show();
                                                        getLoginStatus(sharedPreferenceManager.get_username(), sharedPreferenceManager.getUserType(), true, response);
                                                    } else {

                                                        Toastmsg(LoginActivity.this, noInternet);
                                                    }


                                                    // startActivity(new Intent(LoginActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                } else {
                                                    sharedPreferenceManager.set_keep_login(true);
                                                    startActivity(new Intent(LoginActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            } else {

                                                if (Role.equals("Party Worker")) {

                                                    if (checkInternet.isConnected()) {

                                                        sharedPreferenceManager.set_keep_login_role(Role);
                                                        sharedPreferenceManager.set_user_type(response.getJSONObject(0).getString("userType"));
                                                        sharedPreferenceManager.set_project_id(project_id);
                                                        sharedPreferenceManager.set_Constituency_id(constituencyId);
                                                        sharedPreferenceManager.set_user_id(userid);

                                                        dialog.show();
                                                        getLoginStatus(sharedPreferenceManager.get_username(), sharedPreferenceManager.getUserType(), true, response);
                                                    } else {

                                                        Toastmsg(LoginActivity.this, noInternet);
                                                    }

                                                } else {
                                                    startActivity(new Intent(LoginActivity.this, DownloadActivity.class).putExtra("userId", userid)
                                                            .putExtra("userType", response.getJSONObject(0).getString("userType"))
                                                            .putExtra("role", Role)
                                                            .putExtra("cid", constituencyId).putExtra("project_id", project_id)
                                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                    LoginActivity.this.finish();
                                                }
                                            }
                                            //
                                        }
                                    } else {
                                        Toastmsg(LoginActivity.this, noRecordFound);
                                    }


                                    //  }
                                                                   /* startActivity(new Intent(LoginActivity.this, ProjectActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                                    LoginActivity.this.finish();*/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.w("Constituency330", "Response " + response);

                            } else {
                                Toastmsg(LoginActivity.this, noRecordFound);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toastmsg(LoginActivity.this, Error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ServerUtils.getCustomHeaders();
                }
            };
            //  RequestFactory.getInstance(this).addToRequestQueue(jsObjRequest);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsObjRequest);
            queue.getCache().clear();
            // cancelAll(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private void download_project() {
        final StringRequest request = new StringRequest(Request.Method.GET, API.GET_PROJECT + sharedPreferenceManager.get_user_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();

                if (!response.equals("[]")) {


                    String id = "";
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject obj = array.getJSONObject(i);
                            id = "" + obj.getInt("id");
                        }

                        if (!id.isEmpty()) {
                            if (checkInternet.isConnected()) {
                                dialog.show();
                                fetch_constituency_from_server(id);
                            } else {
                                Toastmsg(LoginActivity.this, noInternet);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toastmsg(LoginActivity.this, "There is no project yet");
                }
                Log.w(TAG, "Response439 " + response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.w(TAG, "error " + error.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void fetch_constituency_from_server(String project_id) {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_CONSTITUENCY + "/project/" + project_id + "/" + sharedPreferenceManager.get_user_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                Log.w(TAG, "response468 " + response);

                if (!response.equals("[]")) {

                    try {
                        JSONArray array = new JSONArray(response);
                        String id = "", project_id = "";
                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            sharedPreferenceManager.set_project_id(object.getInt("projectId") + "");
                            sharedPreferenceManager.set_Constituency_id(object.getInt("id") + "");
                            project_id = object.getInt("projectId") + "";
                            id = object.getInt("id") + "";
                        }

                        if (!(id.isEmpty() && project_id.isEmpty())) {

                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/voters.db");

                            Log.w(TAG, "user id :" + userid + " " + project_id + " stored values :" + sharedPreferenceManager.get_user_id() + " " + sharedPreferenceManager.get_project_id() + " file exist :" + file.exists());


                            if (userid.equals(sharedPreferenceManager.get_user_id()) && project_id.equals(sharedPreferenceManager.get_project_id()) && file.exists()) {

                                sharedPreferenceManager.set_keep_login(true);
                                if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {
                                    startActivity(new Intent(LoginActivity.this, ConductSurveyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                }
                            } else {
                                startActivity(new Intent(LoginActivity.this, DownloadActivity.class).putExtra("id", id).putExtra("project_id", project_id).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }
                            LoginActivity.this.finish();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toastmsg(LoginActivity.this, "No projects available");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "Error " + error.toString());
                Toastmsg(LoginActivity.this, error.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void getLoginStatus(String username, String projecttype, boolean isFileFound, JSONArray val) {
        StringRequest request = new StringRequest(Request.Method.POST, API.LOGIN_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    Log.w(TAG, "response : " + response);
                    dialog.dismiss();
                    JSONArray array = new JSONArray(response);

                    JSONObject jsonObject = array.getJSONObject(0);

                    switch (jsonObject.getString("msg")) {


                        case "success":
                            MyApplication.getInstance().trackEvent(sharedPreferenceManager.get_project_id(),

                                    "Login", sharedPreferenceManager.get_username() + ":" + getCurrentateAndTime() + ":" + getDeviceName(), 1);

                            sharedPreferenceManager.set_keep_login(true);
                            if (sharedPreferenceManager.getUserType().equalsIgnoreCase("kp") || sharedPreferenceManager.getUserType().equalsIgnoreCase("opcp") || sharedPreferenceManager.getUserType().equalsIgnoreCase("ep")) {
                                startActivity(new Intent(LoginActivity.this, OfflineConstituencyDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                LoginActivity.this.finish();
                            } else {

                                startActivity(new Intent(LoginActivity.this, DownloadActivity.class).putExtra("userId", userid)
                                        .putExtra("userType", val.getJSONObject(0).getString("userType"))
                                        .putExtra("role", val.getJSONObject(0).getString("userrole"))
                                        .putExtra("cid", val.getJSONObject(0).getString("constituencyId")).putExtra("project_id", val.getJSONObject(0).getString("projectId"))
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                LoginActivity.this.finish();
                            }
                            break;

                        case "failure":

                            Toastmsg(LoginActivity.this, "Failure try again later");
                            break;

                        case "multiple":
                            alertForMultipleLogin();
                            break;


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                Log.w(TAG, "error " + error.toString());
                Toastmsg(LoginActivity.this, "Failure try again later");


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("type", "login");
                return map;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);


    }


    public void alertForMultipleLogin() {


        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle("Multiple Login")
                .setCancelable(true)
                .setMessage("This user already logged in another device, please logout from that device and try again")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })

                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
