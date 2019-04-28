package com.software.cb.rajneethi.activity;

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

public class InfluentialPeopleActivity extends Util {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.edt_name)
    EditText edt_name;
    @BindView(R.id.edt_age)
    EditText edt_age;
    @BindView(R.id.edt_sex)
    EditText edt_sex;
    @BindView(R.id.edt_phone)
    EditText edt_phone;
    @BindView(R.id.edt_affliation)
    EditText edt_affiliation;
    @BindView(R.id.edt_post)
    EditText edt_post;

    String title = "", booth_name = "", userType;

    @BindString(R.string.influentialPeople)
    String toolbarTitle;

    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    private GPSTracker gpsTracker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influential_people);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, toolbarTitle);
        gpsTracker = new GPSTracker(InfluentialPeopleActivity.this);

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

        if (checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.READ_EXTERNAL_STORAGE, this)) {
            if (!(edt_name.getText().toString().trim().isEmpty() ||
                    edt_age.getText().toString().trim().isEmpty() ||
                    edt_sex.getText().toString().trim().isEmpty() ||
                    edt_phone.getText().toString().trim().isEmpty() ||
                    edt_affiliation.getText().toString().trim().isEmpty() ||
                    edt_post.getText().toString().trim().isEmpty())) {

                JSONObject object = new JSONObject();
                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(InfluentialPeopleActivity.this);
                String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
                // survey_result.put("surveyid", surveyId);
                try {

                    object.put("surveyDate", new Date().toString());
                    object.put("projectId", sharedPreferenceManager.get_project_id());
                    object.put("partyWorker", sharedPreferenceManager.get_username());
                    object.put("pwname", sharedPreferenceManager.get_username());
                    // object.put("booth", sharedPreferenceManager.get_booth());
                    object.put("userType", userType);
                    object.put("booth_name", booth_name);
                    object.put("ip_name", edt_name.getText().toString().trim());
                    object.put("ip_age", edt_age.getText().toString().trim());
                    object.put("ip_sex", edt_sex.getText().toString().trim());
                    object.put("ip_phone", edt_phone.getText().toString().trim());
                    object.put("ip_party ngo name", edt_affiliation.getText().toString().trim());
                    object.put("ip_Occupation", edt_post.getText().toString().trim());
                    object.put("surveyType", title);
                    object.put("surveyid", surveyId);
                    object.put("latitude", gpsTracker.getLatitude() + "");
                    object.put("longitude", gpsTracker.getLongitude() + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", object.toString(), get_current_date(), true, surveyId);

                MyDatabase db = new MyDatabase(InfluentialPeopleActivity.this);

                BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                        booth_name, "Influential People", userType);

                db.insertBoothStats(stats);


                db.insert_voter_attribute(attribute);
                reWriteTextFile(InfluentialPeopleActivity.this, "influentialPeople_" + booth_name + "_" + sharedPreferenceManager.get_user_id() + ".txt", object.toString());
                Toastmsg(InfluentialPeopleActivity.this, success);
                InfluentialPeopleActivity.super.onBackPressed();
                InfluentialPeopleActivity.this.finish();

            } else {
                Toastmsg(InfluentialPeopleActivity.this, allFieldsRequired);
            }
        } else {
            askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.READ_EXTERNAL_STORAGE});
        }
    }


    @BindString(R.string.successfullyAdded)
    String success;

}
