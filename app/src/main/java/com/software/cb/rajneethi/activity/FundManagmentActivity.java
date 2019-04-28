package com.software.cb.rajneethi.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.FundGrievanceAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.FundDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FundManagmentActivity extends UtilActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar progressbar;

    @BindView(R.id.fundRecyclerview)
    RecyclerView recyclerView;

    public String title = "";

    @BindView(R.id.btnRetry)
    Button btnRetry;


    @BindString(R.string.fundManagement)
    public String fundManagement;
    @BindString(R.string.grievanceManagement)
    public String grievanceManagement;
    @BindString(R.string.beneficiaryManagement)
    public String beneficiaryManagment;

    private FundGrievanceAdapter adapter;
    LayoutInflater factory;

    @BindView(R.id.piechart)
    PieChart piechart;


    SharedPreferenceManager sharedPreferenceManager;


    public boolean isFundManagement;
    ArrayList<FundDetails> list = new ArrayList<>();

    @BindView(R.id.addLayout)
    LinearLayout addLayout;

    private String TAG = "Beneficiary";
    IOSDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_managment);
        ButterKnife.bind(this);


        set_Linearlayout_manager(recyclerView, this);
        setAdapter();

        sharedPreferenceManager = new SharedPreferenceManager(this);

        dialog = show_dialog(this, false);

        if (getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString("title");

        }

        setup_toolbar_with_back(toolbar, title);


        callAPI();


    }

    private void callAPI() {

        if (checkInternet.isConnected()) {
            if (title.equalsIgnoreCase(fundManagement)) {
                // addFundData();
                dialog.show();
                getData(API.GET_FUND_DETAILS + "constituency_id=" + sharedPreferenceManager.get_constituency_id() + "&project_id=" + sharedPreferenceManager.get_project_id());

            } else if (title.equalsIgnoreCase(beneficiaryManagment)) {

                dialog.show();
                getData(API.GET_BENEFICIARY + "constituency_id=" + sharedPreferenceManager.get_constituency_id() + "&project_id=" + sharedPreferenceManager.get_project_id());

            }

        } else {
            btnRetry.setVisibility(View.VISIBLE);
        }

    }


    double total = 0;
    double expanse = 0;

    private void getData(String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                list.clear();
                dialog.dismiss();
                btnRetry.setVisibility(View.GONE);
                try {

                    JSONObject object = new JSONObject(response);
                    JSONArray array = new JSONArray(object.getString("message"));


                    Log.w(TAG, "response : " + response);
                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject details = array.getJSONObject(i);


                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());


                        if (title.equalsIgnoreCase(beneficiaryManagment)) {
                            Date date = format.parse(details.getString("Date"));
                            list.add(new FundDetails(details.getString("id"), details.getString("Title"), details.getString("Type"),
                                    details.getString("Name"), format.format(date), details.getString("vot_name"), false));
                        } else {
                            Date date = format.parse(details.getString("StartDate"));
                            Date endDate = format.parse(details.getString("EndDate"));


                            try {
                                total += details.getDouble("EstimatedCost");
                                expanse += details.getDouble("Expenditure");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            list.add(new FundDetails(details.getString("id"), details.getString("ProjectTitle"),
                                    format.format(date), format.format(endDate),
                                    details.getString("EstimatedCost"), details.getString("Expenditure"), details.getString("Status"),
                                    details.getString("Agency"), false));


                        }

                    }


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }


                if (list.size() > 0) {
                    adapter.notifyDataSetChanged();
                    if (title.equalsIgnoreCase(fundManagement)) {
                        piechart.setVisibility(View.VISIBLE);
                        piechart.addPieSlice(new PieModel("Total", (float) total, randomColor()));
                        piechart.addPieSlice(new PieModel("Expanse", (float) expanse, randomColor()));

                        piechart.startAnimation();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                btnRetry.setVisibility(View.VISIBLE);

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    /*generate random color*/
    public int randomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }

    @OnClick(R.id.btnRetry)
    public void retry() {
        callAPI();
    }

    public void delete(int pos, String id) {

        if (title.equalsIgnoreCase(fundManagement)) {

            delete_alert_dialog(pos, API.DELETE_FUND_DEAILS + "fund_id=" + id, dialog);
        } else {
            delete_alert_dialog(pos, API.DELETE_BENEFICIARY_DETAILS + "benef_id=" + id, dialog);
        }

    }

    @BindString(R.string.delete)
    String delete;
    @BindString(R.string.confirmDelete)
    String confirmDelete;
    @BindString(R.string.noInternet)
    String noInternet;

    //alert dialog for delete
    private void delete_alert_dialog(final int pos, String url, IOSDialog pDialog) {
        new AlertDialog.Builder(this)
                .setTitle(delete)
                .setMessage(confirmDelete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (checkInternet.isConnected()) {


                            pDialog.show();
                            deleteItem(pos, url);
                            //   delete(user_id, pos);

                        } else {
                            Toastmsg(FundManagmentActivity.this, noInternet);
                        }
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

    public void deleteItem(final int pos, String url) {

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                try {
                    list.remove(pos);
                    adapter.notifyDataSetChanged();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(FundManagmentActivity.this, "Error try again later");
            }
        });

        VolleySingleton.getInstance(FundManagmentActivity.this).addToRequestQueue(request);

    }

    private void setAdapter() {
        adapter = new FundGrievanceAdapter(list, this, FundManagmentActivity.this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fund_grievance_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                if (title.equalsIgnoreCase(fundManagement)) {
                    addLayout.setVisibility(View.VISIBLE);
                    initializeFactory();
                    recyclerView.setVisibility(View.GONE);
                    showaddFundLayout();
                } else if (title.equalsIgnoreCase(beneficiaryManagment)) {
                    addLayout.setVisibility(View.VISIBLE);
                    initializeFactory();
                    recyclerView.setVisibility(View.GONE);
                    showBeneficiaryAddlayout();
                }
                return true;
            case android.R.id.home:
                if (addLayout.isShown()) {
                    hide();
                } else {
                    super.onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;


    String status = "", agency = "";

    public void showaddFundLayout() {

        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_add_fund, null);

        EditText edtTitle = myView.findViewById(R.id.edtTitle);
        EditText edtStartDate = myView.findViewById(R.id.edtStartDate);
        EditText edtToDate = myView.findViewById(R.id.edtEndDate);
        EditText edtCost = myView.findViewById(R.id.edtCost);
        EditText edtExpentidure = myView.findViewById(R.id.edtExpenditure);
        Spinner statusSpinner = myView.findViewById(R.id.statusSpinner);
        Spinner agencySpinner = myView.findViewById(R.id.agencySpinner);
        Button btnSubmit = myView.findViewById(R.id.btnSubmit);

        statusSpinner.setVisibility(View.VISIBLE);
        agencySpinner.setVisibility(View.VISIBLE);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    status = statusSpinner.getSelectedItem().toString();
                } else {
                    status = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        agencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    agency = agencySpinner.getSelectedItem().toString();
                } else {
                    agency = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(edtTitle.getText().toString().trim().isEmpty() || edtStartDate.getText().toString().trim().isEmpty() ||
                        edtToDate.getText().toString().trim().isEmpty() || edtCost.getText().toString().trim().isEmpty() || edtExpentidure.getText().toString().trim().isEmpty() ||
                        status.isEmpty() || agency.isEmpty())) {

                    if (CheckDates(edtStartDate.getText().toString().trim(), edtToDate.getText().toString().trim())) {

                        if (checkInternet.isConnected()) {
                            FundDetails details = new FundDetails("temp", edtTitle.getText().toString().trim(), edtStartDate.getText().toString().trim(),
                                    edtToDate.getText().toString().trim(), edtCost.getText().toString().trim(), edtExpentidure.getText().toString().trim(), status, agency, true);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("project_id", sharedPreferenceManager.get_project_id());
                            map.put("ProjectTitle", edtTitle.getText().toString().trim());
                            map.put("ProjectType", sharedPreferenceManager.getUserType());
                            map.put("ProjectSubType", sharedPreferenceManager.getUserType());
                            map.put("StartDate", edtStartDate.getText().toString().trim());
                            map.put("EndDate", edtToDate.getText().toString().trim());
                            map.put("EstimatedCost", edtCost.getText().toString().trim());
                            map.put("Expenditure", edtExpentidure.getText().toString().trim());
                            map.put("Status", status);
                            map.put("Agency", agency);

                            dialog.show();
                            addNewData(API.ADD_FUND_DETAILS, details, map, false, 0);

                            agency = "";
                            status = "";
                        } else {
                            Toastmsg(FundManagmentActivity.this, noInternet);
                        }


                    } else {

                        Toastmsg(FundManagmentActivity.this, "Start date is greater than end date");
                    }


                } else {
                    Toastmsg(FundManagmentActivity.this, allFieldsRequired);
                }
            }
        });


        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        Calendar newCalendar = Calendar.getInstance();

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.US);
        fromDatePickerDialog = new DatePickerDialog(FundManagmentActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtStartDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        toDatePickerDialog = new DatePickerDialog(FundManagmentActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtToDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });
        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDatePickerDialog.show();
            }
        });


        ArrayList<String> statusList = new ArrayList<>();
        statusList.add("Select status");
        statusList.add("Not Yet Started");
        statusList.add("On Hold");
        statusList.add("Complete");

        setAdapterForSpinner(statusSpinner, statusList);

        ArrayList<String> agencyList = new ArrayList<>();
        agencyList.add("Select Agency");
        agencyList.add("PWD");
        agencyList.add("PRED");
        agencyList.add("BDA");
        setAdapterForSpinner(agencySpinner, agencyList);


        addLayout.addView(myView);
    }

    SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public boolean CheckDates(String startDate, String endDate) {
        boolean b = false;
        try {
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                b = true;//If start date is before end date
            } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                b = true;//If two dates are equal
            } else {
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    String beneType = "";

    public void showBeneficiaryAddlayout() {
        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_add_beneficiary, null);

        final EditText edtTitle = myView.findViewById(R.id.edtBeneficiaryTitle);

        final EditText edtName = myView.findViewById(R.id.edtBeneficiaryName);

        Button btnSubmit = myView.findViewById(R.id.btnSubmit);
        final Spinner spinner = myView.findViewById(R.id.beneficiaryTypeSpinner);
        ArrayList<String> list = new ArrayList<>();
        list.add("Select type");
        list.add("Health Camp");
        list.add("DWP (DESTITUTE WIDOW PENSION)");
        list.add("Ambedkar Loans");

        setAdapterForSpinner(spinner, list);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    beneType = spinner.getSelectedItem().toString();
                } else {
                    beneType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(edtTitle.getText().toString().trim().isEmpty() || edtName.getText().toString().trim().isEmpty() || beneType.isEmpty())) {

                    String name = edtName.getText().toString().trim();
                    FundDetails details = new FundDetails("temp_id", edtTitle.getText().toString().trim(), beneType, name, get_current_date(), name, true);

                    if (checkInternet.isConnected()) {
                        dialog.show();

                        HashMap<String, String> map = new HashMap<>();
                        map.put("project_id", sharedPreferenceManager.get_project_id());
                        map.put("benef_title", details.getBeneficiaryTitle());
                        map.put("benef_type", details.getType());
                        map.put("benef_name", details.getBeneficiaryName());
                        map.put("VoterID", details.getVoter());
                        map.put("benef_date", details.getBeneficiaryDate());


                        Log.w(TAG, "values :" + details.getBeneficiaryTitle() + " 2." + details.getType() + " 3." + details.getBeneficiaryName() + " 4." + details.getVoter() + " 5." + details.getBeneficiaryDate()
                                + " 6." + sharedPreferenceManager.get_project_id());

                        addNewData(API.ADD_NEW_BENEFICIARY, details, map, false, 0);
                    }
                } else {
                    Toastmsg(FundManagmentActivity.this, allFieldsRequired);
                }
            }
        });

        addLayout.addView(myView);
    }

    private void setAdapterForSpinner(Spinner spinner, ArrayList<String> list) {
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(FundManagmentActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(spinnerArrayAdapter);
    }


    private void addNewData(String url, FundDetails details, HashMap<String, String> map, boolean isUpdate, int pos) {

        Log.w(TAG, "url : " + url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response);

                    if (isUpdate) {
                        if (title.equalsIgnoreCase(beneficiaryManagment)) {

                            if (object.getString("message").equalsIgnoreCase("Beneficiary Data Is Updated Successfully!")) {
                                list.remove(pos);
                                list.add(pos, details);
                                adapter.notifyDataSetChanged();
                                hide();
                                Toastmsg(FundManagmentActivity.this, "Updated successfully");
                            }
                        } else {
                            if (object.getString("message").equalsIgnoreCase("Fund Data is Updated Successfully!")) {
                                list.remove(pos);
                                list.add(pos, details);
                                adapter.notifyDataSetChanged();
                                hide();
                                Toastmsg(FundManagmentActivity.this, "Updated successfully");
                            }
                        }
                    } else {
                        if (title.equalsIgnoreCase(beneficiaryManagment)) {

                            if (object.getString("message").equalsIgnoreCase("Beneficiary Data Is Saved Successfully!")) {
                                list.add(0, details);
                                adapter.notifyDataSetChanged();
                                hide();

                                beneType = "";
                                Toastmsg(FundManagmentActivity.this, "Added successfully");

                            }

                        } else {

                            if (object.getString("message").equalsIgnoreCase("Fund Data is saved Successfully!")) {
                                list.add(0, details);
                                adapter.notifyDataSetChanged();
                                hide();
                                Toastmsg(FundManagmentActivity.this, "Added successfully");

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.w(TAG, "response" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "error :" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                return map;
            }
        };

        VolleySingleton.getInstance(FundManagmentActivity.this).addToRequestQueue(request);
    }

    public void initializeFactory() {
        if (factory == null) {
            factory = LayoutInflater.from(this);
        }
    }


    public void showBeneficiaryData(FundDetails details, boolean isEditable, int pos) {
        recyclerView.setVisibility(View.GONE);
        initializeFactory();
        showBeneficiaryDetails(details, isEditable, pos);

    }

    public void showBeneficiaryDetails(FundDetails details, boolean isEditable, int pos) {
        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_add_beneficiary, null);

        EditText edtBeneficiaryTitle = (EditText) myView.findViewById(R.id.edtBeneficiaryTitle);
        TextView txtType = (TextView) myView.findViewById(R.id.txtType);
        EditText edtBeneficiaryName = (EditText) myView.findViewById(R.id.edtBeneficiaryName);
        //    EditText edtVoter = (EditText) myView.findViewById(R.id.edtVoter);
        EditText edtDate = (EditText) myView.findViewById(R.id.edtBeneficiaryDate);
        Button btnSubmit = (Button) myView.findViewById(R.id.btnSubmit);


        edtBeneficiaryTitle.setText(details.getBeneficiaryTitle());
        txtType.setText(details.getType());
        edtBeneficiaryName.setText(details.getBeneficiaryName());
        //  edtVoter.setText(details.getVoter());
        edtDate.setText(details.getBeneficiaryDate());


        if (isEditable) {
            disableEditText(edtBeneficiaryName);
            disableEditText(edtBeneficiaryTitle);
            // disableEditText(edtVoter);
            disableEditText(edtDate);
            btnSubmit.setVisibility(View.GONE);

        } else {
            btnSubmit.setVisibility(View.VISIBLE);
            final Spinner spinner = myView.findViewById(R.id.beneficiaryTypeSpinner);
            ArrayList<String> list = new ArrayList<>();
            list.add("Select type");
            list.add("Health Camp");
            list.add("DWP (DESTITUTE WIDOW PENSION)");
            list.add("Ambedkar Loans");

            spinner.setVisibility(View.VISIBLE);
            setAdapterForSpinner(spinner, list);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != 0) {
                        beneType = spinner.getSelectedItem().toString();
                    } else {
                        beneType = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            spinner.setSelection(getSpinnerPos(list, details.getType()));

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(edtBeneficiaryTitle.getText().toString().trim().equalsIgnoreCase(details.getBeneficiaryTitle()) &&
                            edtBeneficiaryName.getText().toString().trim().equalsIgnoreCase(details.getBeneficiaryName()) && beneType.equalsIgnoreCase(details.getType()))) {

                        if (!(edtBeneficiaryTitle.getText().toString().trim().isEmpty() || edtBeneficiaryName.getText().toString().trim().isEmpty() || beneType.isEmpty())) {

                            String name = edtBeneficiaryName.getText().toString().trim();
                            FundDetails details1 = new FundDetails(details.getId(), edtBeneficiaryTitle.getText().toString().trim(), beneType, name, get_current_date(), name, false);

                            if (checkInternet.isConnected()) {
                                dialog.show();

                                HashMap<String, String> map = new HashMap<>();
                                map.put("benef_id", details.getId());
                                map.put("project_id", sharedPreferenceManager.get_project_id());
                                map.put("benef_title", details1.getBeneficiaryTitle());
                                map.put("benef_type", details1.getType());
                                map.put("benef_name", details1.getBeneficiaryName());
                                map.put("VoterID", details1.getVoter());
                                map.put("benef_date", details1.getBeneficiaryDate());


                                addNewData(API.UPDATE_BENEFICIARY_DETAILS, details1, map, true, pos);
                            }
                        } else {
                            Toastmsg(FundManagmentActivity.this, allFieldsRequired);
                        }


                    } else {
                        Toastmsg(FundManagmentActivity.this, "No changes detected");
                    }
                }
            });

        }

        addLayout.addView(myView);
    }

    public void showFundData(FundDetails details, boolean isEditable, int position) {
        recyclerView.setVisibility(View.GONE);
        initializeFactory();
        showFundDetails(details, isEditable, position);
    }

    public void showFundDetails(FundDetails details, boolean isEditable, int position) {
        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_add_fund, null);

        final EditText edtTitle = (EditText) myView.findViewById(R.id.edtTitle);
        final EditText edtStartDate = (EditText) myView.findViewById(R.id.edtStartDate);
        final EditText edtToDate = (EditText) myView.findViewById(R.id.edtEndDate);
        final EditText edtCost = (EditText) myView.findViewById(R.id.edtCost);
        final EditText edtExpenditure = (EditText) myView.findViewById(R.id.edtExpenditure);
        final TextView txtStatus = (TextView) myView.findViewById(R.id.txtStatus);
        final TextView txtAgency = (TextView) myView.findViewById(R.id.txtAgency);
        Button btnSubmit = (Button) myView.findViewById(R.id.btnSubmit);

        Spinner statusSpinner = myView.findViewById(R.id.statusSpinner);
        Spinner agencySpinner = myView.findViewById(R.id.agencySpinner);


        if (!isEditable) {
            disableEditText(edtTitle);
            disableEditText(edtStartDate);
            disableEditText(edtToDate);
            disableEditText(edtCost);
            disableEditText(edtExpenditure);
            btnSubmit.setVisibility(View.GONE);
            txtStatus.setText(details.getStatus());
            txtAgency.setText(details.getAgency());
            txtStatus.setVisibility(View.VISIBLE);
            txtAgency.setVisibility(View.VISIBLE);
        } else {
            Calendar newCalendar = Calendar.getInstance();

            final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.US);
            fromDatePickerDialog = new DatePickerDialog(FundManagmentActivity.this, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    edtStartDate.setText(dateFormatter.format(newDate.getTime()));
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            toDatePickerDialog = new DatePickerDialog(FundManagmentActivity.this, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    edtToDate.setText(dateFormatter.format(newDate.getTime()));
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

            toDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            edtStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fromDatePickerDialog.show();
                }
            });
            edtToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toDatePickerDialog.show();
                }
            });

            statusSpinner.setVisibility(View.VISIBLE);
            agencySpinner.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);

            ArrayList<String> statusList = new ArrayList<>();
            statusList.add("Select status");
            statusList.add("Not Yet Started");
            statusList.add("On Hold");
            statusList.add("Complete");

            setAdapterForSpinner(statusSpinner, statusList);

            ArrayList<String> agencyList = new ArrayList<>();
            agencyList.add("Select Agency");
            agencyList.add("PWD");
            agencyList.add("PRED");
            agencyList.add("BDA");
            setAdapterForSpinner(agencySpinner, agencyList);

            agencySpinner.setSelection(getSpinnerPos(agencyList, details.getAgency()));
            statusSpinner.setSelection(getSpinnerPos(statusList, details.getStatus()));

            statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != 0) {
                        status = statusSpinner.getSelectedItem().toString();
                    } else {
                        status = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            agencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != 0) {
                        agency = agencySpinner.getSelectedItem().toString();
                    } else {
                        agency = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!(edtTitle.getText().toString().trim().equalsIgnoreCase(details.getTitle()) && edtStartDate.getText().toString().trim().equalsIgnoreCase(details.getStartDate())
                            && edtToDate.getText().toString().trim().equalsIgnoreCase(details.getEndDate()) && edtCost.getText().toString().trim().equalsIgnoreCase(details.getCost()) &&
                            edtExpenditure.getText().toString().trim().equalsIgnoreCase(details.getExpenditure()) && status.equalsIgnoreCase(details.getStatus()) && agency.equalsIgnoreCase(details.getAgency()))) {

                        Log.w(TAG, "changes detected");

                        if (!(edtTitle.getText().toString().trim().isEmpty() || edtStartDate.getText().toString().trim().isEmpty() ||
                                edtToDate.getText().toString().trim().isEmpty() || edtCost.getText().toString().trim().isEmpty() || edtExpenditure.getText().toString().trim().isEmpty() ||
                                status.isEmpty() || agency.isEmpty())) {

                            if (CheckDates(edtStartDate.getText().toString().trim(), edtToDate.getText().toString().trim())) {

                                if (checkInternet.isConnected()) {


                                    FundDetails details1 = new FundDetails(details.getId(), edtTitle.getText().toString().trim(), edtStartDate.getText().toString().trim(),
                                            edtToDate.getText().toString().trim(), edtCost.getText().toString().trim(), edtExpenditure.getText().toString().trim(), status, agency, false);


                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("fund_id", details.getId());
                                    map.put("project_id", sharedPreferenceManager.get_project_id());
                                    map.put("ProjectTitle", edtTitle.getText().toString().trim());
                                    map.put("ProjectType", sharedPreferenceManager.getUserType());
                                    map.put("ProjectSubType", sharedPreferenceManager.getUserType());
                                    map.put("StartDate", edtStartDate.getText().toString().trim());
                                    map.put("EndDate", edtToDate.getText().toString().trim());
                                    map.put("EstimatedCost", edtCost.getText().toString().trim());
                                    map.put("Expenditure", edtExpenditure.getText().toString().trim());
                                    map.put("Status", status);
                                    map.put("Agency", agency);

                                    dialog.show();
                                    addNewData(API.UPDATE_FUND_DETAILS, details1, map, true, position);

                                    agency = "";
                                    status = "";
                                } else {
                                    Toastmsg(FundManagmentActivity.this, noInternet);
                                }


                            } else {

                                Toastmsg(FundManagmentActivity.this, "Start date is greater than end date");
                            }


                        } else {
                            Toastmsg(FundManagmentActivity.this, allFieldsRequired);
                        }


                    } else {

                        Toastmsg(FundManagmentActivity.this, "No chnages detected");
                    }

                }
            });
        }


        edtTitle.setText(details.getTitle());
        edtStartDate.setText(details.getStartDate());
        edtToDate.setText(details.getEndDate());
        edtCost.setText(details.getCost());
        edtExpenditure.setText(details.getExpenditure());


        addLayout.addView(myView);
    }


    private int getSpinnerPos(ArrayList<String> list, String title) {
        int pos = 0;
        for (int i = 0; i <= list.size() - 1; i++) {
            if (title.equalsIgnoreCase(list.get(i))) {
                pos = i;
                break;
            }
        }

        return pos;

    }

    private void disableEditText(EditText editText) {
        editText.setClickable(false);
        editText.setEnabled(false);

    }

    private void hide() {
        recyclerView.setVisibility(View.VISIBLE);
        addLayout.removeAllViews();
        addLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (addLayout.isShown()) {
            hide();
        } else {
            super.onBackPressed();
        }
    }
}
