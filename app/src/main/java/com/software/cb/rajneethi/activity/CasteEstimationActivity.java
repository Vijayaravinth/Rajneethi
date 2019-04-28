package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.VoterAttribute;
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

public class CasteEstimationActivity extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.edt_caste_name)
    EditText edt_caste_name;

    @BindView(R.id.edt_total)
    EditText edt_total;
    @BindView(R.id.edt_percentage)
    EditText edt_percentage;

    String title = "", booth_name = "", userType = "";

    GPSTracker gpsTracker;

    @BindString(R.string.casteEstimate)
    String casteEstimate;
    @BindString(R.string.casteEstimation)
    String casteEstimation;

    private String TAG = "Caste Estimation";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caste_estimate);
        ButterKnife.bind(this);


        gpsTracker = new GPSTracker(CasteEstimationActivity.this);
        if (getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString("title");
            booth_name = getIntent().getExtras().getString("booth_name");
            userType = getIntent().getExtras().getString("userType");

        }

        if (title.equalsIgnoreCase("Caste Estimate")) {
            setup_toolbar_with_back(toolbar, casteEstimate);
        } else {
            setup_toolbar_with_back(toolbar, casteEstimation);
        }
    }

    @OnClick(R.id.btn_save)
    public void save() {

        if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.READ_EXTERNAL_STORAGE, this)) {
            if (!(edt_caste_name.getText().toString().trim().isEmpty() || edt_total.getText().toString().trim().isEmpty() || edt_percentage.getText().toString().trim().isEmpty())) {

                JSONObject object = new JSONObject();
                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(CasteEstimationActivity.this);
                String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
                Log.w(TAG, "survey id " + surveyId);
                //  survey_result.put("surveyid", surveyId);

                //added for new 4 attribute
                try {
                    object.put("surveyDate", new Date().toString());
                    object.put("projectId", sharedPreferenceManager.get_project_id());
                    object.put("partyWorker", sharedPreferenceManager.get_username());
                    object.put("pwname", sharedPreferenceManager.get_username());

                    object.put("booth", sharedPreferenceManager.get_booth());
                    object.put("userType", userType);
                    object.put("surveyid", surveyId);
                    object.put("booth_name", booth_name);
                    if (title.equals("Caste Estimate")) {
                        object.put("ce1_caste_name", edt_caste_name.getText().toString().trim());
                        object.put("ce1_total", edt_total.getText().toString().trim());
                        object.put("ce1_percentage", edt_percentage.getText().toString().trim());
                    } else {
                        object.put("ce2_caste_name", edt_caste_name.getText().toString().trim());
                        object.put("ce2_total", edt_total.getText().toString().trim());
                        object.put("ce2_percentage", edt_percentage.getText().toString().trim());
                    }
                    object.put("surveyType", title);
                    object.put("latitude", gpsTracker.getLatitude() + "");
                    object.put("longitude", gpsTracker.getLongitude() + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", object.toString(), get_current_date(), true, surveyId);
                MyDatabase db = new MyDatabase(CasteEstimationActivity.this);

                BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                        booth_name, title, userType);

                db.insertBoothStats(stats);

                db.insert_voter_attribute(attribute);

                Toastmsg(CasteEstimationActivity.this, success);
                if (title.equals("Caste Estimate")) {
                    reWriteTextFile(CasteEstimationActivity.this, "casteEstimate_" + booth_name + "_" + sharedPreferenceManager.get_user_id() + ".txt", object.toString());

                } else {
                    reWriteTextFile(CasteEstimationActivity.this, "casteEstimation_" + booth_name + "_" + sharedPreferenceManager.get_user_id() + ".txt", object.toString());
                }

                CasteEstimationActivity.super.onBackPressed();
                CasteEstimationActivity.this.finish();


            } else {
                Toastmsg(CasteEstimationActivity.this, allFieldsRequired);
            }
        } else {
            askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.READ_EXTERNAL_STORAGE});
        }
    }

    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    @BindString(R.string.successfullyAdded)
    String success;


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
}
