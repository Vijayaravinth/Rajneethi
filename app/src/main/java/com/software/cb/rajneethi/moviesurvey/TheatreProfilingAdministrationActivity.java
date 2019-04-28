package com.software.cb.rajneethi.moviesurvey;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.services.RecordService;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DELL on 31-12-2017.
 */

public class TheatreProfilingAdministrationActivity extends Util {


    private String TAG = "Administration";
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.theatreProfiling)
    String title;

    @BindView(R.id.qus3Layout)
    LinearLayout qus3Layout;

    String theatreName = "", address = "", outsideTheatre = "", theatreOld = "", renovationDetails = "", rentalDetails = "", multiplexPrcentage = "", provision = "", whoRunsTheatre = "",
            leaseDetails = "", theatreLandDetails = "", distance = "", amenities = "", nearestTheatre = "", sharepercentage = "";

    String isRenovationDone = "";

    File newDir;
    public String mFileName = null;

    LayoutInflater factory;
    HashMap<View, EditText> edtTheatreNameList = new HashMap<>();
    HashMap<View, EditText> edtTheatreVillageList = new HashMap<>();
    HashMap<View, EditText> edtTheatreContactList = new HashMap<>();

    JSONObject finalObj = new JSONObject();
    Intent i;

    @BindString(R.string.validMobile)
    String validMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiling_administration);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);

        factory = LayoutInflater.from(this);

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/";

        mFileName += "AUD-" + sharedPreferenceManager.get_username() + "_" + "theatreSurvey" + "_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";


        i = new Intent(this, RecordService.class);
        i.putExtra("fileName", mFileName);
        startService(i);
        addView();
        radioGroupListener();


        addTextWatcher();

    }

    private void addTextWatcher() {
        edtTheatrePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10 && charSequence.length() != 0) {
                    edtTheatrePhone.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOwnerPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10) {
                    edtOwnerPhone.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtAlterPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10) {
                    edtAlterPhoneNo.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtLeasePhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10) {
                    edtLeasePhoneNo.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtAlterNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10) {
                    edtAlterNo.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(i);
        delete_audio_file();
    }

    /*delete audio file*/
    private void delete_audio_file() {
        try {
            File file = new File(newDir, mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addView() {

        final View myView = factory.inflate(R.layout.outside_taluk_theatre_details, null);
        qus3Layout.addView(myView);

        final EditText edtName = (EditText) myView.findViewById(R.id.edtOutsideTheatreName);
        edtTheatreNameList.put(myView, edtName);

        final EditText edtVillage = (EditText) myView.findViewById(R.id.edtOutsideTheatreVillage);
        edtTheatreVillageList.put(myView, edtVillage);

        final EditText edtContactNo = (EditText) myView.findViewById(R.id.edtOutsideTheatreContactNo);
        edtTheatreContactList.put(myView, edtContactNo);

        edtContactNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 10) {
                    edtContactNo.setError(validMobile);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // edtName.requestFocus();

        ImageView btn_add = (ImageView) myView.findViewById(R.id.img_add);
        final ImageView btn_remove = (ImageView) myView.findViewById(R.id.img_minus);

        if (qus3Layout.getChildCount() == 1) {
            btn_remove.setVisibility(View.INVISIBLE);
        }
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(edtName.getText().toString().trim().isEmpty() || edtContactNo.getText().toString().trim().isEmpty() || edtVillage.getText().toString().trim().isEmpty())) {
                    addView();
                } else {
                    Toastmsg(TheatreProfilingAdministrationActivity.this, "Please enter all the values");
                }
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtTheatreNameList.remove(myView);
                edtTheatreVillageList.remove(myView);
                edtTheatreContactList.remove(myView);
                qus3Layout.removeView(myView);
                if (qus3Layout.getChildCount() == 1) {
                    btn_remove.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void radioGroupListener() {
        rgRenovation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbRenovationNo:
                        isRenovationDone = "no";
                        renovationYesLayout.setVisibility(View.GONE);
                        break;
                    case R.id.rbRenovationYes:
                        isRenovationDone = "yes";
                        renovationYesLayout.setVisibility(View.VISIBLE);
                        renovationDetails = "";
                        break;
                }
            }
        });

        rgRentalDetails.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbRental7Days:
                        rentalDetails = "6a";
                        break;
                    case R.id.rbRentalPerShow:
                        rentalDetails = "6b";
                        break;
                }

                edtRentalDetails.requestFocus();
            }
        });

        rgPercentage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbPercentageNo:
                        provision = "8b";
                        //  addValues("8a","yes");
                        //removeValues("8b");
                        break;
                    case R.id.rbPercentageYes:
                        provision = "8a";
                        //addValues("8b","yes");
                        // removeValues("8a");
                }
            }
        });

        rgOwner.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbOwner:
                        whoRunsTheatre = "9a";
                        ownerDetailsLayout.setVisibility(View.VISIBLE);
                        leaseDetailsLayout.setVisibility(View.GONE);
                        leaseLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rbOwnerNo:
                        whoRunsTheatre = "9b";
                        leaseLayout.setVisibility(View.GONE);
                        leaseDetailsLayout.setVisibility(View.VISIBLE);
                        ownerDetailsLayout.setVisibility(View.GONE);
                        ownerDetailsLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });

        rgLease.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbLeaseYes:
                        leaseDetails = "9b1";
                        break;
                    case R.id.rbLeaseNo:
                        //  leaseDetailsLayout.setVisibility(View.GONE);
                        leaseDetails = "9b2";
                }
            }
        });
    }

    private void clearAll() {
        theatreName = "";
        address = "";
        theatreOld = "";
        //  renovationDetails = "";
        //  rentalDetails = "";
        multiplexPrcentage = "";
        //provision = "";
        //  whoRunsTheatre = "";
        // leaseDetails = "";
        theatreLandDetails = "";
        distance = "";
        amenities = "";
        nearestTheatre = "";
        sharepercentage = "";

    }

    private void addValues(String key, String value) {

        try {
            finalObj.put("s1-" + key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeValues(String key) {
        finalObj.remove("s1-" + key);
    }

    private void toast(int qus) {
        Toastmsg(TheatreProfilingAdministrationActivity.this, "Please answer question :" + qus);
    }

    @OnClick(R.id.txtNext)
    public void save() {

        finalObj = new JSONObject();

        //clearAll();
        if (!edtTheatreName.getText().toString().trim().isEmpty()) {
            theatreName = edtTheatreName.getText().toString().trim();
        } else {
            toast(1);
            return;
        }
        //theatre address
        if (!(edtTheatreAddress.getText().toString().trim().isEmpty() || edtTheatreCity.getText().toString().trim().isEmpty() || edtTheatreTaluk.getText().toString().trim().isEmpty() ||
                edtTheatreDistrict.getText().toString().trim().isEmpty() || edtTheatrePincode.getText().toString().trim().isEmpty() || edtTheatrePhone.getText().toString().trim().isEmpty())) {

            if (edtTheatrePhone.getText().toString().trim().length() == 10) {
                address = edtTheatreAddress.getText().toString().trim() + "," + edtTheatreCity.getText().toString().trim() + "," + edtTheatreTaluk.getText().toString().trim()
                        + "," + edtTheatreDistrict.getText().toString().trim() + "," + edtTheatrePincode.getText().toString().trim() + "," + edtTheatrePhone.getText().toString().trim();
            } else {
                toast(validMobile);
                return;
            }
           /* try {
                JSONObject object = new JSONObject();


                object.put("address", edtTheatreAddress.getText().toString().trim());
                object.put("city", edtTheatreCity.getText().toString().trim());
                object.put("tau", edtTheatreTaluk.getText().toString().trim());
                object.put("district", edtTheatreDistrict.getText().toString().trim());
                object.put("pincode", edtTheatrePincode.getText().toString().trim());
                object.put("phone", edtTheatrePhone.getText().toString().trim());
                address = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {

            Toastmsg(TheatreProfilingAdministrationActivity.this, "Please enter theatre Address");
            Log.w(TAG, "Data empty");
            return;
        }

        JSONObject outsideTheatreObj = new JSONObject();
        //third question;
        for (Map.Entry<View, EditText> entry : edtTheatreNameList.entrySet()) {

            EditText edtName = entry.getValue();
            EditText edtVillage = edtTheatreVillageList.get(entry.getKey());
            EditText edtContactNo = edtTheatreContactList.get(entry.getKey());

            if (outsideTheatre.isEmpty()) {
                outsideTheatre = edtName.getText().toString().trim() + "," + edtVillage.getText().toString().trim() + "," + edtContactNo.getText().toString().trim();
            } else {
                outsideTheatre += "," + edtName.getText().toString().trim() + "," + edtVillage.getText().toString().trim() + "," + edtContactNo.getText().toString().trim();

            }
          /*  try {
                outsideTheatreObj.put("theatreName", edtName.getText().toString().trim());
                outsideTheatreObj.put("theatreVillage", edtVillage.getText().toString().trim());
                outsideTheatreObj.put("theatreContactNo", edtContactNo.getText().toString().trim());

                Log.w(TAG, "OutSide Theater Obj :" + outsideTheatreObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

        }

        //  outsideTheatre = outsideTheatreObj.toString();

        if (outsideTheatre.isEmpty() || outsideTheatre == null) {
            toast(3);
            return;
        }


        //theatre old
        if (!edtTheatreOld.getText().toString().trim().isEmpty()) {
            theatreOld = edtTheatreOld.getText().toString().trim();
        } else {
            toast(4);
            return;
        }

        //renovation details
        if (!isRenovationDone.isEmpty()) {
            if (isRenovationDone.equals("yes")) {
                addValues("5a", "yes");
                if (!edtTheatreRenovationWhen.getText().toString().trim().isEmpty()) {
                    renovationDetails = "when :" + edtTheatreRenovationWhen.getText().toString() + ", what : ";
                    addValues("5a1", edtTheatreRenovationWhen.getText().toString().trim());
                    String whatRenovated = getRenovationDetails();

                    if (!whatRenovated.isEmpty()) {
                        renovationDetails += whatRenovated;
                        Log.w(TAG, "renovation details :" + renovationDetails);
                    } else {
                        toast("Enter renovation details");
                        return;
                    }
                } else {
                    toast("Enter renovation year");
                    return;
                }
            } else {
                renovationDetails = "no";
                addValues("5b", "yes");
            }
        } else {
            toast("Enter renovation details");
            return;
        }

        //rental details
        if (!rentalDetails.isEmpty()) {
            if (!edtRentalDetails.getText().toString().trim().isEmpty()) {
                addValues(rentalDetails, edtRentalDetails.getText().toString().trim());
                // rentalDetails += "," + edtRentalDetails.getText().toString().trim();
            } else {
                toast("Enter rental details");
                return;
            }
        } else {
            toast("Enter rental details");
            return;
        }

        //percentage
        if (!edtTheatrePercentage.getText().toString().trim().isEmpty()) {
            multiplexPrcentage = edtTheatrePercentage.getText().toString().trim();
        } else {
            toast("Answer Question 7");
            return;
        }

        //provision details
        if (!provision.isEmpty()) {

            addValues(provision, "yes");
        } else {
            toast("Answer question 8");
            return;
        }

        //who runs theatre
        if (!whoRunsTheatre.isEmpty()) {
            if (whoRunsTheatre.equals("9a")) {
                if (!(edtOwnerName.getText().toString().trim().isEmpty() || edtOwnerPhone.getText().toString().trim().isEmpty() || edtAlterName.getText().toString().trim().isEmpty() ||
                        edtAlterPhoneNo.getText().toString().trim().isEmpty() || edtOwnerEmail.getText().toString().trim().isEmpty())) {

                    if (edtOwnerPhone.getText().toString().trim().length() == 10 && edtAlterPhoneNo.getText().toString().trim().length() == 10) {
                        String value = edtOwnerName.getText().toString().trim() + "," + edtOwnerPhone.getText().toString().trim() + "," +
                                edtAlterName.getText().toString().trim() + "," + edtAlterPhoneNo.getText().toString().trim() + "," + edtOwnerEmail.getText().toString().trim();
                        addValues(whoRunsTheatre, value);
                    } else {
                        toast(9);
                        toast(validMobile);

                        return;
                    }
                } else {
                    toast("Enter owner details");
                    return;
                }
            } else {
                if (!(edtLeaseName.getText().toString().trim().isEmpty() || edtLeasePhoneNo.getText().toString().trim().isEmpty() || edtLeaseAlterName.getText().toString().trim().isEmpty() ||
                        edtAlterNo.getText().toString().trim().isEmpty() || edtLeaseEmail.getText().toString().trim().isEmpty() ||
                        edtStartDate.getText().toString().trim().isEmpty() || edtEndDate.getText().toString().trim().isEmpty())) {

                    if (edtLeasePhoneNo.getText().toString().trim().length() == 10 && edtAlterNo.getText().toString().trim().length() == 10) {
                        String value = edtLeaseName.getText().toString().trim() + "," + edtLeasePhoneNo.getText().toString().trim() + "," +
                                edtLeaseAlterName.getText().toString().trim() + "," + edtAlterNo.getText().toString().trim() + "," +
                                edtLeaseEmail.getText().toString().trim() + "," + edtStartDate.getText().toString().trim() + "," +
                                edtEndDate.getText().toString().trim();
                        addValues(whoRunsTheatre, value);
                    } else {
                        toast(9);
                        toast(validMobile);
                        return;
                    }
                } else {
                    toast("Enter lease details 1");
                    return;
                }

            }
        } else {
            toast(9);
            return;
        }

        //lease details
        if (whoRunsTheatre.equals("9a")) {
            if (!leaseDetails.isEmpty()) {

                addValues(leaseDetails, "yes");
            } else {
                toast("Enter lease details 2");
                return;
            }
        }

        //land area
        if (!(edtTheatreLandArea.getText().toString().trim().isEmpty() || edtTheatreBuildingArea.getText().toString().trim().isEmpty())) {

            theatreLandDetails = "10a :" + edtTheatreLandArea.getText().toString().trim() + " , 10b : " + edtTheatreBuildingArea.getText().toString().trim();
        } else {
            toast("Answer Question 10");
            return;
        }

        //far theatre from
        if (!(edtBusStop.getText().toString().trim().isEmpty() || edtRailwayStation.getText().toString().trim().isEmpty() ||
                edtCabStand.getText().toString().trim().isEmpty() || edtAutoStand.getText().toString().trim().isEmpty() || edtHospital.getText().toString().trim().isEmpty() ||
                edtMarriageHall.getText().toString().trim().isEmpty() || edtCollege.getText().toString().trim().isEmpty())) {

            distance = edtBusStop.getText().toString().trim() + "," + edtRailwayStation.getText().toString().trim() + "," + edtCabStand.getText().toString().trim() + "," +
                    edtAutoStand.getText().toString().trim() + "," + edtHospital.getText().toString().trim() + "," + edtMarriageHall.getText().toString().trim()
                    + "," + edtCollege.getText().toString().trim();

            addValues("11a", edtBusStop.getText().toString().trim());
            addValues("11b", edtRailwayStation.getText().toString().trim());
            addValues("11c", edtCabStand.getText().toString().trim());
            addValues("11d", edtAutoStand.getText().toString().trim());
            addValues("11e", edtHospital.getText().toString().trim());
            addValues("11f", edtMarriageHall.getText().toString().trim());
            addValues("11g", edtCollege.getText().toString().trim());

            /*try {
                JSONObject object = new JSONObject();
                object.put("BusStop", edtBusStop.getText().toString().trim());
                object.put("RailwayStation", edtRailwayStation.getText().toString().trim());
                object.put("CabStand", edtCabStand.getText().toString().trim());
                object.put("AutoStand", edtAutoStand.getText().toString().trim());
                object.put("Hospital", edtHospital.getText().toString().trim());
                object.put("mariageHall", edtMarriageHall.getText().toString().trim());
                object.put("College", edtCollege.getText().toString().trim());

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        } else {
            toast("Answer Question 11");
            return;
        }

        //amenities
        if (cbCanteen.isChecked()) {
            addValues("12a", "yes");
            if (amenities.isEmpty()) {
                amenities = "Canteen";
            } else {
                amenities += "," + "Canteen";
            }
        } else {
            removeValues("12a");
        }
        if (cbRestroom.isChecked()) {
            addValues("12b", "yes");
            if (amenities.isEmpty()) {
                amenities = "Restrooms";
            } else {
                amenities += "," + "Restrooms";
            }
        } else {
            removeValues("12b");
        }
        if (cbLounge.isChecked()) {
            addValues("12c", "yes");
            if (amenities.isEmpty()) {
                amenities = "Lounge/Seating";
            } else {
                amenities += "," + "Lounge/Seating";
            }
        } else {
            removeValues("12c");
        }

        if (amenities.isEmpty()) {
            return;
        }

        //distance another theatre

        if (!(edtOtherTheatreName.getText().toString().trim().isEmpty() || edtOtherTheatreDistance.getText().toString().trim().isEmpty())) {
            nearestTheatre = "Name :" + edtOtherTheatreName.getText().toString().trim() + ",Distance : " + edtOtherTheatreDistance.getText().toString().trim();
        } else {
            toast("answer Question 13");
            return;
        }

        //sharing
        if (!(edtTicketPercentage.getText().toString().trim().isEmpty() || edtcanteenPercentage.getText().toString().trim().isEmpty() ||
                edtAdvertisingPercentage.getText().toString().trim().isEmpty() || edtParkingPercentage.getText().toString().trim().isEmpty())) {

            addValues("14a", edtTicketPercentage.getText().toString().trim());
            addValues("14b", edtcanteenPercentage.getText().toString().trim());
            addValues("14c", edtAdvertisingPercentage.getText().toString().trim());
            addValues("14d", edtParkingPercentage.getText().toString().trim());
        } else {
            toast("Answer Question 14");
            return;
        }


        // JSONObject object = new JSONObject();
        addValues("theatreName", theatreName);
        addValues("address", address);
        addValues("outsideTheatreDetails", outsideTheatre);
        addValues("BuildingYear", theatreOld);
        //  finalObj.put("renovationDetails", renovationDetails);
        //  finalObj.put("rentalDetails", rentalDetails);
        addValues("multiplexPercentage", multiplexPrcentage);
        // finalObj.put("provision", provision);
        // finalObj.put("whoRunTheatre", whoRunsTheatre);
        //  finalObj.put("leaseDetails", leaseDetails);
        addValues("theatrePremise", theatreLandDetails);
        //finalObj.put("s1-Distance", distance);
        //  finalObj.put("amenities", amenities);
        addValues("nearestTheatre", nearestTheatre);
        //    finalObj.put("revenuePercentage", sharepercentage);
        Log.w(TAG, "Administration details : " + finalObj.toString());

        startActivity(new Intent(TheatreProfilingAdministrationActivity.this, ProfilingTicketCounterActivity.class).putExtra("name", theatreName).putExtra("fileName", mFileName).putExtra("administration", finalObj.toString()));


    }

    private String getRenovationDetails() {

        String whatRenovated = "";
        if (cbScreen.isChecked()) {

            whatRenovated += "," + "Screen";
            addValues("5b1", "yes");

        } else {
            removeValues("5b1");
        }
        if (cbSeats.isChecked()) {

            whatRenovated += "," + "Seats";

            addValues("5b2", "yes");
        } else {
            removeValues("5b2");
        }
        if (cbSounds.isChecked()) {

            whatRenovated += "," + "Sounds";


            addValues("5b3", "yes");
        } else {
            removeValues("5b3");
        }
        if (cbProjectionSystem.isChecked()) {

            whatRenovated += "," + "Projection System";

            addValues("5b4", "yes");
        } else {
            removeValues("5b4");
        }

        return whatRenovated;
    }


    @Override
    protected void onResume() {
        super.onResume();
        clearAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void toast(String msg) {
        Toastmsg(TheatreProfilingAdministrationActivity.this, msg);
    }

    @BindView(R.id.edtTicketPercentage)
    EditText edtTicketPercentage;
    @BindView(R.id.edtCanteenPercentage)
    EditText edtcanteenPercentage;
    @BindView(R.id.edtAdvertisingPercentage)
    EditText edtAdvertisingPercentage;
    @BindView(R.id.edtParkingPercentage)
    EditText edtParkingPercentage;
    @BindView(R.id.edtOtherTheatreDistance)
    EditText edtOtherTheatreDistance;
    @BindView(R.id.edtOtherTheatreName)
    EditText edtOtherTheatreName;
    @BindView(R.id.cbLounge)
    CheckBox cbLounge;
    @BindView(R.id.cbRestroom)
    CheckBox cbRestroom;
    @BindView(R.id.cbCanteen)
    CheckBox cbCanteen;
    @BindView(R.id.edtCollege)
    EditText edtCollege;
    @BindView(R.id.edtMarriageHall)
    EditText edtMarriageHall;
    @BindView(R.id.edtHospital)
    EditText edtHospital;
    @BindView(R.id.edtCabStand)
    EditText edtCabStand;
    @BindView(R.id.edtAutoStand)
    EditText edtAutoStand;
    @BindView(R.id.edtRailwayStation)
    EditText edtRailwayStation;
    @BindView(R.id.edtBusStop)
    EditText edtBusStop;
    @BindView(R.id.edtTheatreLandArea)
    EditText edtTheatreLandArea;
    @BindView(R.id.edtTheatreBuildingArea)
    EditText edtTheatreBuildingArea;
    @BindView(R.id.edtEndDate)
    EditText edtEndDate;
    @BindView(R.id.edtStartDate)
    EditText edtStartDate;
    @BindView(R.id.edtleaseEmail)
    EditText edtLeaseEmail;
    @BindView(R.id.edtLeaseAlterNo)
    EditText edtAlterNo;
    @BindView(R.id.edtLeaseAlterName)
    EditText edtLeaseAlterName;
    @BindView(R.id.edtLeasePhone)
    EditText edtLeasePhoneNo;
    @BindView(R.id.edtLeaseName)
    EditText edtLeaseName;
    @BindView(R.id.leaseDetailsLayout)
    TableLayout leaseDetailsLayout;
    @BindView(R.id.rbLeaseNo)
    RadioButton rbLeaseNo;
    @BindView(R.id.rbLeaseYes)
    RadioButton rbLeaseYes;
    @BindView(R.id.rgLease)
    RadioGroup rgLease;
    @BindView(R.id.leaseLayout)
    LinearLayout leaseLayout;
    @BindView(R.id.edtOwnerEmail)
    EditText edtOwnerEmail;
    @BindView(R.id.edtOwnerAlterNo)
    EditText edtAlterPhoneNo;
    @BindView(R.id.edtalterName)
    EditText edtAlterName;
    @BindView(R.id.edtOwnerPhone)
    EditText edtOwnerPhone;
    @BindView(R.id.edtOwnerName)
    EditText edtOwnerName;
    @BindView(R.id.ownerDetailsLayout)
    TableLayout ownerDetailsLayout;
    @BindView(R.id.rbOwnerNo)
    RadioButton rbOwnerNo;
    @BindView(R.id.rbOwner)
    RadioButton rbOwner;
    @BindView(R.id.rgOwner)
    RadioGroup rgOwner;
    @BindView(R.id.rbPercentageYes)
    RadioButton rgPercentageYes;
    @BindView(R.id.rbPercentageNo)
    RadioButton rgPercentageNo;
    @BindView(R.id.rgPercentage)
    RadioGroup rgPercentage;
    @BindView(R.id.edtTheatrePercentage)
    EditText edtTheatrePercentage;
    /*  @BindView(R.id.edtRentalPerShow)
      EditText edtRentalPerShow;
      @BindView(R.id.edtRental7days)
      EditText edtRental7Days;
      @BindView(R.id.rbRentalPerShow)
      RadioButton rbRentalPerShow;
      @BindView(R.id.rbRental7Days)
      RadioButton rbRental7Days;*/
    @BindView(R.id.edtRentalDetails)
    EditText edtRentalDetails;
    @BindView(R.id.rgRentalDetails)
    RadioGroup rgRentalDetails;
    @BindView(R.id.cbProjectionSystem)
    CheckBox cbProjectionSystem;
    @BindView(R.id.cbSounds)
    CheckBox cbSounds;
    @BindView(R.id.cbSeats)
    CheckBox cbSeats;
    @BindView(R.id.cbScreen)
    CheckBox cbScreen;
    @BindView(R.id.edtTheatreRenovationWhen)
    EditText edtTheatreRenovationWhen;
    @BindView(R.id.renovationYesLayout)
    TableLayout renovationYesLayout;
    @BindView(R.id.rgRenovation)
    RadioGroup rgRenovation;
    @BindView(R.id.edtTheatreOld)
    EditText edtTheatreOld;
    @BindView(R.id.edtTheatrePhone)
    EditText edtTheatrePhone;
    @BindView(R.id.edtTheatrePincode)
    EditText edtTheatrePincode;
    @BindView(R.id.edtTheatreDistrict)
    EditText edtTheatreDistrict;
    @BindView(R.id.edtTheatreCity)
    EditText edtTheatreCity;
    @BindView(R.id.edtTheatreTaluk)
    EditText edtTheatreTaluk;
    @BindView(R.id.edtTheatreAddress)
    EditText edtTheatreAddress;
    @BindView(R.id.edtTheatreName)
    EditText edtTheatreName;


}
