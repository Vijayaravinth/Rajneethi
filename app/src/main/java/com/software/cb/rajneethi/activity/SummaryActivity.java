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
 * Created by w7 on 7/7/2017.
 */

public class SummaryActivity extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

      String title = "", booth_name = "", userType = "";

    @BindView(R.id.edt_summary1)
    EditText edt_summary1;
    @BindView(R.id.edt_summary2)
    EditText edt_summary2;
    @BindView(R.id.edt_summary3)
    EditText edt_summary3;
    @BindView(R.id.edt_summary4)
    EditText edt_summary4;
    @BindView(R.id.edt_summary5)
    EditText edt_summary5;
    @BindView(R.id.edt_summary6)
    EditText edt_summary6;
    @BindView(R.id.edt_summary7)
    EditText edt_summary7;
    @BindView(R.id.edt_summary8)
    EditText edt_summary8;

    @BindString(R.string.summary)
    String toolbarTitle;

    GPSTracker gpsTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,toolbarTitle);


        gpsTracker = new GPSTracker(this);

        if (getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString("title");
            booth_name = getIntent().getExtras().getString("booth_name");
            userType = getIntent().getExtras().getString("userType");
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

    @OnClick(R.id.btn_save)
    public void save() {

        if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {
            if (!(edt_summary1.getText().toString().trim().isEmpty() || edt_summary2.getText().toString().trim().isEmpty()
                    || edt_summary3.getText().toString().trim().isEmpty()
                    || edt_summary4.getText().toString().trim().isEmpty() ||
                    edt_summary5.getText().toString().trim().isEmpty() ||
                    edt_summary6.getText().toString().trim().isEmpty() || edt_summary7.getText().toString().trim().isEmpty() ||
                    edt_summary8.getText().toString().trim().isEmpty())) {

                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(SummaryActivity.this);
                JSONObject object = new JSONObject();
                try {

                    String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();

                    object.put("surveyDate", new Date().toString());
                    object.put("projectId", sharedPreferenceManager.get_project_id());
                    object.put("partyWorker", sharedPreferenceManager.get_username());
                    object.put("booth_name", booth_name);
                    object.put("userType", userType);
                    object.put("pwname", sharedPreferenceManager.get_username());
                    object.put("surveyType", title);
                    object.put("latitude",gpsTracker.getLatitude());
                    object.put("longitude",gpsTracker.getLongitude());
                    object.put("surveyid", surveyId);

                    object.put("Important parties and supporting castes them", edt_summary1.getText().toString().trim());
                    object.put("which party is front leading ? Reason", edt_summary2.getText().toString().trim());
                    object.put("which candidate is front leading ? Reason", edt_summary3.getText().toString().trim());
                    object.put("politically related problems , development or caste related problems", edt_summary4.getText().toString().trim());
                    object.put("people\'s expectations from elected candidate", edt_summary5.getText().toString().trim());
                    object.put("people\'s expectations from elected candidate of the constituency", edt_summary6.getText().toString().trim());
                    object.put("people\'s expectations from upcoming government in karnataka", edt_summary7.getText().toString().trim());
                    object.put("Important expected changes expected by large people in polling booth", edt_summary8.getText().toString().trim());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MyDatabase db = new MyDatabase(SummaryActivity.this);

                BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                        booth_name, "Summary", userType);
                db.insertBoothStats(stats);
                createTextFile(SummaryActivity.this, "summary_" + booth_name + "_" + sharedPreferenceManager.get_user_id()+"_"+get_current_date()+"_"+get_current_time()  + ".txt", object.toString());

                Toastmsg(SummaryActivity.this, success);

                finishActivity();

            } else {
                Toastmsg(SummaryActivity.this, allFieldsRequired);
            }
        } else {
            askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE});
        }
    }


    @BindString(R.string.successfullyAdded)
    String success;

    private void send_email(String to, String subject, String message) {

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        //need this to prompts email client only
        email.setType("message/rfc822");

        //  startActivity(Intent.createChooser(email, "Choose an Email client :"));
        startActivity(email);

    }

    private void finishActivity() {
        SummaryActivity.super.onBackPressed();
        SummaryActivity.this.finish();
    }


    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;
}
