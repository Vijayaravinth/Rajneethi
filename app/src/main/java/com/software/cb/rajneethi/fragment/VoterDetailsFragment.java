package com.software.cb.rajneethi.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.AddEventsActivity;
import com.software.cb.rajneethi.activity.VoterProfileActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.SelectedContactDetails;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.models.VoterDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by monika on 4/2/2017.
 */

public class VoterDetailsFragment extends Fragment {

    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.txt_age)
    TextView txt_age;
    @BindView(R.id.txt_gender)
    TextView txt_gender;
    @BindView(R.id.txt_houseno)
    TextView txt_house_no;

    @BindView(R.id.txt_votercardnumber)
    TextView txt_votercardnumber;
    @BindView(R.id.txt_serialno)
    TextView txt_serial_no;
    @BindView(R.id.txt_address)
    TextView txt_address;

    @BindView(R.id.txt_mobileNumber)
    TextView txtMobileNumber;

    @BindView(R.id.txt_ward_details)
    TextView txtWardDetails;

    @BindView(R.id.txt_poll_details)
    TextView txt_polling_details;


    @BindView(R.id.txtFamilyHead)
    TextView txtFamilyHead;

    @BindView(R.id.rgFamilyHead)
    RadioGroup rgFamilyHead;
    @BindView(R.id.rbYes)
    RadioButton rbYes;
    @BindView(R.id.rbNo)
    RadioButton rbNo;

    public VoterDetailsFragment() {
    }

    @BindView(R.id.txt_relation)
    TextView txt_relation;

    @BindView(R.id.btn_update)
    Button btn_update;

    @BindView(R.id.radio_group)
    RadioGroup rg_group;

    @BindView(R.id.cardview_other_details)
    public CardView card_other_details;


    private String TAG = "Details fragment";


    public String modification_details = "";
    public String name, age, gender, hno, votercard, serialno, address, relationshp, wardName, boothName, mobile;
    public String booth_details = "";

    @BindView(R.id.edtRespondentName)
    EditText edtRespondentName;

    @BindView(R.id.edtRespondentMobile)
    EditText edtRespondentMobile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_voter_details, container, false);
        ButterKnife.bind(this, v);

        name = ((VoterProfileActivity) getActivity()).name;
        age = ((VoterProfileActivity) getActivity()).age;
        gender = ((VoterProfileActivity) getActivity()).gender;
        hno = ((VoterProfileActivity) getActivity()).houseno;
        votercard = ((VoterProfileActivity) getActivity()).votercardnumber;
        serialno = ((VoterProfileActivity) getActivity()).serailno;
        address = ((VoterProfileActivity) getActivity()).address;
        relationshp = ((VoterProfileActivity) getActivity()).relation;
        wardName = ((VoterProfileActivity) getActivity()).wardName;
        boothName = ((VoterProfileActivity) getActivity()).boothName;
        mobile = ((VoterProfileActivity) getActivity()).mobile;

        update_textview();



        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                show_update_button();

                switch (checkedId) {
                    case R.id.rb_expired:
                        Log.w(TAG, "Expired");
                        modification_details = "Expired";
                        break;
                    case R.id.rb_shifted:
                        Log.w(TAG, "shifted");
                        modification_details = "Shifted";
                        break;
                    case R.id.rb_not_responding:
                        modification_details = "Not Responding";
                        break;
                    case R.id.rb_out_of_station:
                        modification_details = "Out of Station";
                        break;
                    case R.id.rb_not_availble:
                        modification_details = "Not Available";
                        break;
                    default:
                        break;
                }
            }
        });

        return v;
    }

    //show other details
    public void show_other_details() {
        if (!card_other_details.isShown()) {
            card_other_details.setVisibility(View.VISIBLE);
        }
    }

    //hide other details
    public void hide_other_details() {

        Log.w(TAG, "Is survey taken : " + ((VoterProfileActivity) getActivity()).is_survey_taken);

        if (((VoterProfileActivity) getActivity()).is_survey_taken) {
            if (card_other_details.isShown()) {
                Log.w(TAG, "card is shown");
                card_other_details.setVisibility(GONE);
            } else {
                Log.w(TAG, "card is not shown");
            }
        } else {
            Log.w(TAG, "else paret working");
            show_other_details();
        }

    }

    private void show_update_button() {
        if (!btn_update.isShown()) {
            btn_update.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_update)
    public void update() {
        if (!modification_details.isEmpty()) {


            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());

            try {
                JSONObject survey_result = new JSONObject();
                //added for new 4 attribute
                String audioFilename = ((VoterProfileActivity) getActivity()).mFileName;
                survey_result.put("surveyDate", new Date().toString());
                survey_result.put("projectId", sharedPreferenceManager.get_project_id());
                survey_result.put("partyWorker", sharedPreferenceManager.get_user_id());
                survey_result.put("booth_name", boothName);
                survey_result.put("pwname", sharedPreferenceManager.get_username());
                survey_result.put("surveyType", "Field Survey");
                survey_result.put("familyHead",((VoterProfileActivity)getActivity()).isFamilyHead);

                try {
                    survey_result.put("audioFileName", audioFilename.substring(audioFilename.lastIndexOf("/") + 1, audioFilename.length()));
                } catch (Exception e) {
                    e.printStackTrace();
                    survey_result.put("audioFileName", "empty");

                }
                switch (modification_details) {
                    case "Expired":
                        survey_result.put("Expired", "yes");
                        break;
                    case "Shifted":
                        survey_result.put("Shifted", "yes");
                        break;
                    case "Not Responding":
                        survey_result.put("Not Responding", "yes");
                        break;
                    case "Out of Station":
                        survey_result.put("Out of Station", "yes");
                        break;
                    default:
                        survey_result.put("Not Available", "yes");
                        break;
                }


                String latitude =((VoterProfileActivity)getActivity()).latitude == 0.0 ? "Not found " : ((VoterProfileActivity)getActivity()).latitude + "";
                String longitude =((VoterProfileActivity)getActivity()).longitude == 0.0 ? "Not found" : ((VoterProfileActivity)getActivity()).longitude + "";


                Log.w(TAG,"latitude :"+ latitude +" longitude:"+ longitude);

                survey_result.put("latitude", latitude);
                survey_result.put("longitude", longitude);
                survey_result.put("userType", ((VoterProfileActivity) getActivity()).userType);
                survey_result.put("respondantname", edtRespondentName.getText().toString().trim().isEmpty() ? "Not Available" : edtRespondentName.getText().toString().trim());
                survey_result.put("mobile", edtRespondentMobile.getText().toString().trim().isEmpty() ? "Not Available" : edtRespondentMobile.getText().toString().trim());
                String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
                Log.w(TAG, "survey id " + surveyId);
                survey_result.put("surveyid", surveyId);

                Log.w(TAG, "data : " + survey_result.toString());

                VoterAttribute attribute = new VoterAttribute(((VoterProfileActivity) getActivity()).votercardnumber, "Survey", survey_result.toString(), ((VoterProfileActivity) getActivity()).date, false, surveyId);

                VotersDatabase votersDatabase = new VotersDatabase(getActivity());
                BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                        boothName, "Survey", ((VoterProfileActivity) getActivity()).userType);


                MyDatabase db = new MyDatabase(getActivity());
                db.insert_voter_attribute(attribute);
                db.insertBoothStats(stats);
                votersDatabase.update_survey(attribute.getVoterCardNumber());
                Toast.makeText(getActivity(), "Successfully update ", Toast.LENGTH_LONG).show();
                getActivity().finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), allFieldsRequired, Toast.LENGTH_LONG).show();
        }

    }

    @BindString(R.string.pleaseSelectOne)
    String pleaseSelectOne;
    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;

    public void change_voter_details(VoterDetails details) {
        name = details.getNameEnglish();
        age = details.getAge();
        gender = details.getGender();
        hno = details.getHouseNo();
        votercard = details.getVoterCardNumber();
        serialno = details.getSerialNo();
        address = details.getAddressEnglish();
        relationshp = details.getRelatedEnglish();
        boothName = details.getBoothName();
        wardName = details.getWardName() == null || details.getWardName().isEmpty() ? "N/A" : details.getWardName();
        mobile = details.getMobile() == null || details.getMobile().isEmpty() || details.getMobile() == null || details.getMobile().isEmpty() ? "N/A" : details.getMobile();

        ((VoterProfileActivity) getActivity()).name = details.getNameEnglish();
        ((VoterProfileActivity) getActivity()).votercardnumber = details.getVoterCardNumber();

        ((VoterProfileActivity)getActivity()).isFamilyHead = "no";

        update_textview();

        ((VoterProfileActivity) getActivity()).dialog.dismiss();
    }

    @BindString(R.string.mobile)
    String mobileNumber;
    @BindString(R.string.addMobile)
    String addMobile;
    @BindString(R.string.add)
    String add;
    @BindString(R.string.cancel)
    String cancel;

    @BindString(R.string.validMobile)
    String validMobile;

    //alert dialog for update mobile number
    private void alertDialog() {
        final AutoCompleteTextView taskEditText = new AutoCompleteTextView(getActivity());
        setAdapter(taskEditText);

        taskEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String SelectedValue = (String) parent.getItemAtPosition(position);

                taskEditText.setText(SelectedValue.substring(SelectedValue.lastIndexOf("\n") + 1, SelectedValue.length()).trim());
                Log.w(TAG, "selected value : " + SelectedValue);
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(mobileNumber)
                .setMessage(addMobile)
                .setView(taskEditText)
                .setPositiveButton(add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mobile1 = taskEditText.getText().toString().trim();

                        if (checkIsANumber(mobile1)) {

                            if (mobile1.substring(0, 3).equals("+91")) {

                                mobile1 = mobile1.substring(3, mobile1.length());
                                Log.w(TAG, "mobile ; " + mobile1);

                                if (mobile1.length() == 10) {
                                    Log.w(TAG, "mobile contain +91 and length 13");
                                    new VotersDatabase(getActivity()).updateMobileNumber(votercard, mobile1);
                                    mobile = mobile1;
                                    ((VoterProfileActivity) getActivity()).mobile = mobile1;
                                    String number = "******" + mobile1.substring(6, mobile1.length());
                                    txtMobileNumber.setText(number);
                                } else {
                                    Toast.makeText(getActivity(), validMobile, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (mobile1.length() == 10) {
                                    Log.w(TAG, "mobile contain 10 digit only");
                                    new VotersDatabase(getActivity()).updateMobileNumber(votercard, mobile1);
                                    mobile = mobile1;
                                    String number = "******" + mobile1.substring(6, mobile1.length());

                                    txtMobileNumber.setText(number);
                                    ((VoterProfileActivity) getActivity()).mobile = mobile1;
                                } else {
                                    Toast.makeText(getActivity(), validMobile, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), validMobile, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(cancel, null)
                .create();
        dialog.show();
    }

    private boolean checkIsANumber(String number) {

        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setAdapter(AutoCompleteTextView autoCompleteTextView_contacts) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ((VoterProfileActivity) getActivity()).contact_list);
        autoCompleteTextView_contacts.setThreshold(2);//start searching from 3 character
        autoCompleteTextView_contacts.setAdapter(adapter);   //set the adapter for displaying profile id
    }

    /*  @OnClick(R.id.txt_mobileNumber)
      public void addMobile() {
          alertDialog();
      }
  */
    private void update_textview() {
        txt_name.setText(name);
        txt_age.setText(age);
        txt_gender.setText(gender.equals("M") ? "Male" : "Female");
        String familyHead = "Is "+ name +" as a family head?";
        txtFamilyHead.setText(familyHead);

        txt_house_no.setText(hno);
        txt_votercardnumber.setText(votercard);
        txt_serial_no.setText(serialno);
        txt_address.setText(address);
        txt_relation.setText(relationshp);
        txt_polling_details.setText(boothName);
        txtWardDetails.setText(wardName);

        if (checkIsANumber(mobile)) {

            String number = "******" + mobile.substring(mobile.length() - 4, mobile.length());
            txtMobileNumber.setText(number);

        } else {
            txtMobileNumber.setText(mobile);
        }

        rbYes.setChecked(false);
        rbNo.setChecked(false);
        rgFamilyListener();
        hide_other_details();
    }

    private void rgFamilyListener(){

        MyDatabase db = new MyDatabase(getActivity());
        rgFamilyHead.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rbYes:
                        ((VoterProfileActivity)getActivity()).isFamilyHead = "yes";
                        db.insertFamilyHead(txt_votercardnumber.getText().toString().trim());
                        break;
                    case R.id.rbNo:
                        ((VoterProfileActivity)getActivity()).isFamilyHead = "no";
                        db.deleteFamilyHead(txt_votercardnumber.getText().toString().trim());
                        break;
                }
            }
        });
    }
}
