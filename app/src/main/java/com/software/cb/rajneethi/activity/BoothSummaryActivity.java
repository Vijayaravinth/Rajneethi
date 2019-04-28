package com.software.cb.rajneethi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by w7 on 6/17/2017.
 */

public class BoothSummaryActivity extends Util {

    @BindView(R.id.edt_qus1)
    EditText edt_qus1;

    @BindView(R.id.edt_qus2)
    EditText edt_qus2;

    @BindView(R.id.edt_qus3)
    EditText edt_qus3;

    @BindView(R.id.edt_qus4)
    EditText edt_qus4;
    @BindView(R.id.edt_qus5)
    EditText edt_qus5;

    @BindView(R.id.edt_qus6)
    EditText edt_qus6;

    @BindView(R.id.edt_qus7)
    EditText edt_qus7;

    @BindView(R.id.edt_qus8)
    EditText edt_qus8;
    @BindView(R.id.edt_qus9)
    EditText edt_qus9;


    @BindView(R.id.toolbar)
    Toolbar toolbar;



    MyDatabase db;
    SharedPreferenceManager sharedPreferenceManager;
    GPSTracker gpsTracker;

    String userType = "", boothName = "";

    @BindString(R.string.boothSummary)
    String toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth_summary);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,toolbarTitle);

        if (getIntent().getExtras() != null) {
            userType = getIntent().getExtras().getString("userType");
            boothName = getIntent().getExtras().getString("booth_name");
        }

        db = new MyDatabase(BoothSummaryActivity.this);
        sharedPreferenceManager = new SharedPreferenceManager(BoothSummaryActivity.this);
        gpsTracker = new GPSTracker(BoothSummaryActivity.this);

    }

    @OnClick(R.id.btn_save)
    public void save() {

        if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {

            if (!(edt_qus1.getText().toString().trim().isEmpty() ||
                    edt_qus2.getText().toString().trim().isEmpty() || edt_qus3.getText().toString().trim().isEmpty()
                    || edt_qus4.getText().toString().trim().isEmpty() || edt_qus5.getText().toString().trim().isEmpty() ||
                    edt_qus6.getText().toString().trim().isEmpty() || edt_qus7.getText().toString().trim().isEmpty() ||
                    edt_qus8.getText().toString().trim().isEmpty() || edt_qus9.getText().toString().trim().isEmpty()
            )) {

                JSONObject object = new JSONObject();

                try {
                    object.put("Eleecticity Scarcity", edt_qus1.getText().toString().trim());
                    object.put("Water Issue", edt_qus2.getText().toString().trim());
                    object.put("Education", edt_qus3.getText().toString().trim());
                    object.put("Health", edt_qus4.getText().toString().trim());
                    object.put("Roads", edt_qus5.getText().toString().trim());
                    object.put("Crime / Security", edt_qus6.getText().toString().trim());
                    object.put("Basic Facilities", edt_qus7.getText().toString().trim());
                    object.put("Transport Service", edt_qus8.getText().toString().trim());
                    object.put("Other Issues", edt_qus9.getText().toString().trim());

                    String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();

                    //added for new 4 attribute
                    object.put("surveyType", "BoothSummary");
                    object.put("pwname", sharedPreferenceManager.get_username());
                    object.put("surveyDate", new Date().toString());
                    object.put("projectId", sharedPreferenceManager.get_project_id());
                    object.put("partyWorker", sharedPreferenceManager.get_username());
                    object.put("booth_name", boothName);
                    object.put("userType", userType);
                    object.put("latitude", gpsTracker.getLatitude() + "");
                    object.put("longitude", gpsTracker.getLongitude() + "");
                    object.put("surveyid", surveyId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                MyDatabase db = new MyDatabase(BoothSummaryActivity.this);

                BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                        boothName, "Issues", userType);

                db.insertBoothStats(stats);
                createTextFile(BoothSummaryActivity.this, "issues_" + boothName + "_" + sharedPreferenceManager.get_user_id() + "_" + get_current_date() + "_" + get_current_time() + ".txt", object.toString());
                Toastmsg(BoothSummaryActivity.this, success);
                finishActivity();


            } else {
                Toastmsg(BoothSummaryActivity.this, allFieldsRequired);
            }
        } else {
            askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE});
        }
    }

    private void finishActivity() {
        BoothSummaryActivity.super.onBackPressed();
        BoothSummaryActivity.this.finish();
    }


    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    @BindString(R.string.successfullyAdded)
    String success;

    private void clear_edittext() {
        edt_qus1.getText().clear();
        edt_qus1.getText().clear();
        edt_qus1.getText().clear();
        edt_qus1.getText().clear();
        edt_qus1.getText().clear();
    }

    private void send_email(String to, String subject, String message) {

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
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
}
