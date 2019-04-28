package com.software.cb.rajneethi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ARUVI on 7/4/2017.
 */

public class RespondentInfoActivity extends Util implements Spinner.OnItemSelectedListener {

    private static final String TAG = "RespondentInfoActivity";


    @BindView(R.id.edtxt_name)
    EditText _edTxt_name;
    @BindView(R.id.edtxt_age)
    EditText _edTxt_age;
    @BindView(R.id.spinner_gender)
    Spinner _spinner_gender;
    @BindView(R.id.edtxt_caste)
    EditText _edTxt_caste;
    @BindView(R.id.edtxt_phone)
    EditText _edTxt_phone;
    @BindView(R.id.edtxt_education)
    EditText _edTxt_education;
    @BindView(R.id.edtxt_occupation)
    EditText _edTxt_occupation;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String Gender = "", Survey = null;
    VoterAttribute voterAttribute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respondent_info);
        ButterKnife.bind(this);


        setup_toolbar_with_back(toolbar,getResources().getString(R.string.respondent_info));


        Intent intent = getIntent();


        _spinner_gender.setOnItemSelectedListener(this);

        if (intent != null) {
            voterAttribute = (VoterAttribute) intent.getSerializableExtra("survey");
            Log.w(TAG, "attribute " + voterAttribute.getAttributeValue());

            // getJsonValues(voterAttribute.getAttributeValue());
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

        String Name = _edTxt_name.getText().toString().trim();
        String Age = _edTxt_age.getText().toString().trim();
        String Caste = _edTxt_caste.getText().toString().trim();
        String Phone = _edTxt_phone.getText().toString().trim();
        String Education = _edTxt_education.getText().toString().trim();
        String Occupation = _edTxt_occupation.getText().toString().trim();


        Log.w(TAG, "values : " + Name + Age + Gender + Caste + Phone + Education + Occupation);

        if (!(Name.isEmpty() || Gender.isEmpty() || Age.isEmpty() || Caste.isEmpty() || Phone.isEmpty() || Education.isEmpty() || Occupation.isEmpty())) {
            JSONObject object = new JSONObject();
            try {
                object.put("name", Name);
                object.put("age", Age);
                object.put("caste", Caste);
                object.put("phone", Phone);
                object.put("education", Education);
                object.put("occupation", Occupation);

            } catch (JSONException e) {
                e.printStackTrace();
            }

/*
            JSONArray array = new JSONArray();
            array.put(voterAttribute.getAttributeValue());
            array.put(object);

            voterAttribute.setAttributeValue(array.toString());
*/

            voterAttribute.setAttributeValue_hardcode_qus(object.toString());
            startActivity(new Intent(RespondentInfoActivity.this, PreviewActivity.class).putExtra("survey", voterAttribute).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            RespondentInfoActivity.this.finish();

        } else {
            Toastmsg(RespondentInfoActivity.this, allFieldsRequired);
        }


    }

    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    private void getJsonValues(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            Log.w(TAG, "json obj length" + jsonObject.length());
            String key;
            for (int i = 0; i <= jsonObject.length() - 1; i++) {

                key = String.valueOf(jsonObject.keys().next());

                Log.w(TAG, "key " + key);

            }

            // Log.w(TAG, "jsonObj Loc " + jsonObject.getString("longitude"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Gender = _spinner_gender.getSelectedItem().toString();
        Log.w(TAG, "Gender " + Gender);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
