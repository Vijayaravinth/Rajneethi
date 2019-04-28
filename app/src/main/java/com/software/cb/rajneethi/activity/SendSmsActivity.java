package com.software.cb.rajneethi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.VoterDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class SendSmsActivity extends UtilActivity implements TextWatcher {


    private String TAG = "send sms";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edtMessage)
    EditText edtMessage;
    @BindView(R.id.txtTextLength)
    TextView txtTextLength;
    @BindView(R.id.txtMsgLength)
    TextView txtMessageLength;

    private long textLength = 0;
    private int msgLength = 0;

    String MobileNumbers = "", message = "";

    ArrayList<String> selectedList;

    @BindView(R.id.fabSend)
    FloatingActionButton fabSend;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    SharedPreferenceManager sharedPreferenceManager;
    String caste = "";

    VotersDatabase db;

    ArrayList<String> numbersList = new ArrayList<>();
    int selectedItem = 0;
    List<String> currentSendingNumbers;

    String selectedac = "", selectedhubli = "", allacs = "",singleac="";

    long balance = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, "Send SMS");

        edtMessage.addTextChangedListener(this);

        db = new VotersDatabase(this);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        if (getIntent().getExtras() != null) {
            selectedList = getIntent().getExtras().getStringArrayList("list");

            allacs = getIntent().getExtras().getString("allac");
            selectedac = getIntent().getExtras().getString("selectedac");
            selectedhubli = getIntent().getExtras().getString("selectedhubli");
            balance = getIntent().getExtras().getLong("balance");
            singleac = getIntent().getExtras().getString("singleac");
            if(balance != -1) {
                toolbar.setTitle("Message Balance : " + balance);
            }
        }

//        String commaValue = String.join(",", selectedList);

        for (int i = 0; i <= selectedList.size() - 1; i++) {


            if (i == selectedList.size() - 1) {
                caste += "'" + selectedList.get(i) + "'";
            } else {
                caste += "'" + selectedList.get(i) + "',";
            }

        }


        //fabSend.hide();
        //  new getMobilenumbers().execute();


    }

    private class getMobilenumbers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Cursor c = db.getMobileNumbers(caste);
                if (c.moveToFirst()) {
                    do {


                        numbersList.add(c.getString(0));
                        //  Log.w(TAG,"mobile "+c.getString(0));

                    } while (c.moveToNext());
                }


                Log.w(TAG, "numbers list size :" + numbersList.size());
                c.close();
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fabSend.show();
                }
            });

            return null;
        }
    }

    private void getBalanceInfo() {
        StringRequest request = new StringRequest(Request.Method.POST, BuildConfig.BALANCEAPI+sharedPreferenceManager.getSenderDetails(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String bal = response.substring(response.lastIndexOf("-") + 1, response.length());
                Log.w(TAG, "response :" + response.substring(response.lastIndexOf("-") + 1, response.length()));


                try {
                    balance = Long.parseLong(bal);
                    toolbar.setTitle("Message Balance : " + balance);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @OnClick(R.id.fabSend)
    public void sendSmS() {


        if (checkInternet.isConnected()) {

            if (balance == -1) {
                if(!sharedPreferenceManager.getSenderDetails().equalsIgnoreCase("empty")) {
                    Toastmsg(SendSmsActivity.this, "Getting your balance . Please wait");
                    getBalanceInfo();
                }
            } else {

                if (!edtMessage.getText().toString().trim().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    fabSend.hide();
                    new sendSMS(fabSend, sharedPreferenceManager, progressBar, edtMessage.getText().toString().trim()).execute();
                } else {
                    edtMessage.setError("Please enter message");
                }
            }
        } else {
            Toastmsg(SendSmsActivity.this, "No internet connection");
        }
        //  sendMinItem();


    }

    private void sendMinItem() {
        if (selectedItem < numbersList.size()) {
            currentSendingNumbers = numbersList.subList(selectedItem, 5000);
            send(getCommaMobileNumbers());
        } else {
            fabSend.show();
        }
    }

    private String getCommaMobileNumbers() {
        StringBuilder mobile = new StringBuilder();

        Log.w(TAG, "list size : " + currentSendingNumbers.size());

        for (int i = 0; i <= currentSendingNumbers.size() - 1; i++) {


            if (currentSendingNumbers.get(i).length() == 10) {
                if (i == currentSendingNumbers.size() - 1) {
                    //  mobile = currentSendingNumbers.get(i);
                    mobile.append(currentSendingNumbers.get(i));
                } else {
                    mobile.append(currentSendingNumbers.get(i));
                    mobile.append(",");
                }
            }
        }
        Log.w(TAG, "mobile " + mobile);
        return mobile.toString();
    }


    public void send(String mobile) {
        if (mobile.length() > 0 && edtMessage.getText().toString().trim().length() > 0) {
            //  new sendSMS(fabSend, mobile, sharedPreferenceManager, progressBar, edtMessage.getText().toString().trim()).execute();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


        //if (textLength == 160) {

        if (s.length() > 0) {
            msgLength = s.length() / 160;
            msgLength += 1;
        } else {
            msgLength = 1;
        }
        // }
        textLength = s.length();

        String length = msgLength + "/" + textLength;
        //txtMessageLength.setText(String.valueOf(msgLength));
        txtTextLength.setText(length);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void success() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toastmsg(SendSmsActivity.this, "Messages sent successfully");
                selectedItem += 5000;
                sendMinItem();
            }
        });
    }

    private void failure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toastmsg(SendSmsActivity.this, "Messages sent successfully");
                selectedItem += 5000;
                sendMinItem();
            }
        });
    }

    private class sendSMS extends AsyncTask<Void, Void, Void> {


        SharedPreferenceManager sharedPreferenceManager;
        String msg;
        FloatingActionButton fabSend;
        Context context;

        sendSMS(FloatingActionButton fabSend, SharedPreferenceManager sharedPreferenceManager, ProgressBar progressBar, String msg) {

            this.msg = msg;
            this.sharedPreferenceManager = sharedPreferenceManager;
            this.fabSend = fabSend;

        }

        String mobile = "";

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            //fabSend.setVisibility(View.VISIBLE);
            fabSend.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {


            StringRequest request = new StringRequest(Request.Method.POST, API.SMS_API, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.w(TAG, "response " + response);
                    try {
                        JSONArray array = new JSONArray(response.trim());

                        JSONObject object = array.getJSONObject(0);

                        if (object.getString("message").equalsIgnoreCase("success")) {
                            Toastmsg(SendSmsActivity.this, object.getString("response"));
                        } else {

                            alertForInsufficientBalance(object.getString("response"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "Error " + error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    HashMap<String, String> map = new HashMap<>();
                    map.put("allac", allacs);
                    map.put("ac", selectedac);
                    map.put("hubli", selectedhubli);
                    map.put("caste", caste);
                    map.put("sender", sharedPreferenceManager.getSenderId());
                    map.put("balance", balance + "");
                    map.put("singleac",singleac);
                    return map;
                }
            };

            VolleySingleton.getInstance(SendSmsActivity.this).addToRequestQueue(request);
            return null;
        }
    }

    private void alertForInsufficientBalance(String messae) {
        new AlertDialog.Builder(SendSmsActivity.this, R.style.MyDialogTheme)
                .setTitle("Insufficient Balance")
                .setCancelable(false)
                .setMessage(messae)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })

                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
    }
}
