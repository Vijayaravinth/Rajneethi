package com.software.cb.rajneethi.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.GrievanceAdapater;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.models.GrievanceDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

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

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GrievanceActivity extends UtilActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar progressbar;

    @BindView(R.id.fundRecyclerview)
    RecyclerView recyclerView;

    @BindString(R.string.grievanceManagement)
    public String grievanceManagement;

    @BindView(R.id.btnRetry)
    Button btnRetry;

    ArrayList<GrievanceDetails> list = new ArrayList<>();
    GrievanceAdapater adapater;

    @BindView(R.id.addLayout)
    LinearLayout addLayout;

    LayoutInflater factory;
    IOSDialog dialog;

    @BindString(R.string.noInternet)
    String noInternet;

    private String TAG = "Grievance";

    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, grievanceManagement);

        dialog = show_dialog(this, false);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        set_Linearlayout_manager(recyclerView, this);
        setAdapater();

        callAPI();
    }

    private void callAPI() {
        if (checkInternet.isConnected()) {

            dialog.show();
            getGrievance();
        } else {
            Toastmsg(GrievanceActivity.this, noInternet);
            btnRetry.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnRetry)
    public void retry() {
        callAPI();
    }

    private void setAdapater() {
        adapater = new GrievanceAdapater(this, list, GrievanceActivity.this);
        recyclerView.setAdapter(adapater);
    }

    public void initializeFactory() {
        if (factory == null) {
            factory = LayoutInflater.from(this);
        }
    }


    public void delete(int pos, String id) {


        delete_alert_dialog(pos, API.DELETE_GRIEVANCE + "griev_id=" + id, dialog);


    }


    @BindString(R.string.delete)
    String delete;
    @BindString(R.string.confirmDelete)
    String confirmDelete;

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
                            Toastmsg(GrievanceActivity.this, noInternet);
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
                    adapater.notifyDataSetChanged();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(GrievanceActivity.this, "Error try again later");
            }
        });

        VolleySingleton.getInstance(GrievanceActivity.this).addToRequestQueue(request);

    }

    @BindArray(R.array.status)
    String[] statusList;

    @BindArray(R.array.priority)
    String[] priorityList;
    @BindArray(R.array.typeArray)
    String[] typeList;
    @BindArray(R.array.subTypeArray)
    String[] subTypeList;

    @BindString(R.string.all_fileds_required)
    String allFieldsRequired;


    String status = "", priority = "", type = "", subType = "";

    private void getGrievance() {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_GRIEVANCE_DETAILS + "constituency_id=" + sharedPreferenceManager.get_constituency_id() + "&project_id=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                btnRetry.setVisibility(View.GONE);
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = new JSONArray(object.getString("message"));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());
                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject details = array.getJSONObject(i);

                        Date date = format.parse(details.getString("Date"));
                        list.add(new GrievanceDetails(details.getString("grivienceId"), details.getString("Title"), details.getString("Name"), details.getString("VoterID"),
                                format.format(date), details.getString("Type"), details.getString("SubType"),
                                details.getString("Remarks"), details.getString("Priority"), details.getString("SLATime"), details.getString("Status"), details.getString("Rewards"), false));

                    }


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

                if (list.size() > 0) {
                    adapater.notifyDataSetChanged();
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

    DatePickerDialog fromDatePickerDialog;
    TimePickerDialog timePickerDialog;

    private void showAddGrievanceLayout() {


        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_add_grievance, null);

        final EditText edtTitle = myView.findViewById(R.id.edtTitle);
        final EditText edtName = myView.findViewById(R.id.edtName);
        final EditText edtVoterName = myView.findViewById(R.id.edtVoter);
        final EditText edtDate = myView.findViewById(R.id.edtDate);
        final Button btnSubmit = myView.findViewById(R.id.btnSubmit);

        final Spinner typeSpinner = myView.findViewById(R.id.typeSpinner);
        final Spinner subTypeSpinner = myView.findViewById(R.id.subTypeSpinner);
        final Spinner prioritySpinner = myView.findViewById(R.id.prioritySpinner);
        final Spinner statusSpinner = myView.findViewById(R.id.statusSpinner);
        final EditText edtRemarks = myView.findViewById(R.id.edtRemark);
        final EditText edtTime = myView.findViewById(R.id.edtTime);
        final EditText edtRewards = myView.findViewById(R.id.edtRewards);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(edtTitle.getText().toString().trim().isEmpty() || edtName.getText().toString().isEmpty() || edtVoterName.getText().toString().trim().isEmpty() ||
                        edtDate.getText().toString().trim().isEmpty() || edtRemarks.getText().toString().trim().isEmpty() || edtTime.getText().toString().trim().isEmpty() ||
                        edtRewards.getText().toString().trim().isEmpty() || type.isEmpty() || subType.isEmpty() || priority.isEmpty() || status.isEmpty())) {


                    if (checkInternet.isConnected()) {
                        GrievanceDetails details = new GrievanceDetails("temp", edtTitle.getText().toString().trim(), edtName.getText().toString(), edtVoterName.getText().toString(),
                                edtDate.getText().toString(), type, subType,
                                edtRemarks.getText().toString(), priority, edtTime.getText().toString(), status, edtRewards.getText().toString(), true);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("project_id", sharedPreferenceManager.get_project_id());
                        map.put("griev_title", details.getTitle());
                        map.put("griev_name", details.getName());
                        map.put("VoterID", details.getVoter());
                        map.put("griev_date", details.getDate());
                        map.put("griev_type", details.getType());
                        map.put("griev_sub_type", details.getSubType());
                        map.put("griev_remarks", details.getRemark());
                        map.put("griev_priority", details.getProiroty());
                        map.put("griev_SLATime", details.getSlaTime());
                        map.put("griev_status", details.getStatus());
                        map.put("griev_rewards", details.getRewards());
                        dialog.show();
                        addGrievanceData(API.ADD_NEW_GRIEVANCE_DETAILS, details, 0, false, map);

                    } else {
                        Toastmsg(GrievanceActivity.this, noInternet);
                    }


                } else {
                    Toastmsg(GrievanceActivity.this, allFieldsRequired);
                }


            }
        });

        set_adapter_for_spinner(typeList, typeSpinner);
        set_adapter_for_spinner(subTypeList, subTypeSpinner);
        set_adapter_for_spinner(priorityList, prioritySpinner);
        set_adapter_for_spinner(statusList, statusSpinner);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    type = typeSpinner.getSelectedItem().toString();
                } else {
                    type = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        subTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    subType = subTypeSpinner.getSelectedItem().toString();
                } else {
                    subType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    priority = prioritySpinner.getSelectedItem().toString();
                } else {
                    priority = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        Calendar newCalendar = Calendar.getInstance();

        int mHour = newCalendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = newCalendar.get(Calendar.MINUTE);

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.US);
        fromDatePickerDialog = new DatePickerDialog(GrievanceActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });

        timePickerDialog = new TimePickerDialog(GrievanceActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                edtTime.setText(i + ":" + i1);
            }
        }, mHour, mMinute, false);


        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });


        addLayout.addView(myView);

    }


    private void addGrievanceData(String url, GrievanceDetails details, int pos, boolean isEditable, HashMap<String, String> map) {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);

                    Log.w(TAG, "response :" + response);
                    if (isEditable) {

                        if (object.getString("message").equalsIgnoreCase("Grievance Data Is Updated Successfully!")) {
                            list.remove(pos);
                            list.add(pos, details);
                            adapater.notifyDataSetChanged();
                            hide();
                            Toastmsg(GrievanceActivity.this, "Successfully updated");
                        }
                    } else {


                        if (object.getString("message").equalsIgnoreCase("Grievance Data Is saved Successfully!")) {
                            list.add(0, details);
                            adapater.notifyDataSetChanged();
                            hide();
                            Toastmsg(GrievanceActivity.this, "Successfully Added");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Log.w(TAG, "Error :" + error.toString());
                Toastmsg(GrievanceActivity.this, "Error try again later");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };


        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void set_adapter_for_spinner(String[] options, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void showGrievanceDetails(GrievanceDetails details, int pos, boolean isEditable) {
        initializeFactory();
        recyclerView.setVisibility(View.GONE);
        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_add_grievance, null);


        final EditText edtTitle = myView.findViewById(R.id.edtTitle);
        final EditText edtName = myView.findViewById(R.id.edtName);
        final EditText edtVoterName = myView.findViewById(R.id.edtVoter);
        final EditText edtDate = myView.findViewById(R.id.edtDate);
        final Spinner typeSpinner = myView.findViewById(R.id.typeSpinner);
        final Spinner subTypeSpinner = myView.findViewById(R.id.subTypeSpinner);
        final Spinner prioritySpinner = myView.findViewById(R.id.prioritySpinner);
        final Spinner statusSpinner = myView.findViewById(R.id.statusSpinner);
        final Button btnSubmit = myView.findViewById(R.id.btnSubmit);


        final EditText edtRemarks = myView.findViewById(R.id.edtRemark);
        final EditText edtTime = myView.findViewById(R.id.edtTime);
        final EditText edtRewards = myView.findViewById(R.id.edtRewards);

        TextView txtStatus = myView.findViewById(R.id.txtStatus);
        TextView txtType = myView.findViewById(R.id.txtType);
        TextView txtSubType = myView.findViewById(R.id.txtSubType);
        TextView txtPriority = myView.findViewById(R.id.txtPriority);

        edtTitle.setText(details.getTitle());
        edtName.setText(details.getName());
        edtVoterName.setText(details.getVoter());
        edtDate.setText(details.getDate());
        edtRemarks.setText(details.getRemark());
        edtTime.setText(details.getSlaTime());
        edtRewards.setText(details.getRewards());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(details.getTitle().equalsIgnoreCase(edtTitle.getText().toString().trim()) && details.getName().equalsIgnoreCase(edtName.getText().toString().trim()) &&
                        edtVoterName.getText().toString().trim().equalsIgnoreCase(details.getVoter()) && edtDate.getText().toString().trim().equalsIgnoreCase(details.getDate()) &&
                        edtRemarks.getText().toString().equalsIgnoreCase(details.getRemark()) && edtTime.getText().toString().equalsIgnoreCase(details.getSlaTime()) &&
                        edtRewards.getText().toString().trim().equalsIgnoreCase(details.getRewards()) && type.equalsIgnoreCase(details.getType()) &&
                        details.getSubType().equalsIgnoreCase(subType) && status.equalsIgnoreCase(details.getStatus()) && priority.equalsIgnoreCase(details.getProiroty()))) {


                    if (!(edtTitle.getText().toString().trim().isEmpty() || edtName.getText().toString().isEmpty() || edtVoterName.getText().toString().trim().isEmpty() ||
                            edtDate.getText().toString().trim().isEmpty() || edtRemarks.getText().toString().trim().isEmpty() || edtTime.getText().toString().trim().isEmpty() ||
                            edtRewards.getText().toString().trim().isEmpty() || type.isEmpty() || subType.isEmpty() || priority.isEmpty() || status.isEmpty())) {


                        if (checkInternet.isConnected()) {
                            GrievanceDetails details1 = new GrievanceDetails(details.getId(), edtTitle.getText().toString().trim(), edtName.getText().toString(), edtVoterName.getText().toString(),
                                    edtDate.getText().toString(), type, subType,
                                    edtRemarks.getText().toString(), priority, edtTime.getText().toString(), status, edtRewards.getText().toString(), false);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("grivienceId", details.getId());
                            Log.w(TAG, "g id :" + details.getId());
                            map.put("project_id", sharedPreferenceManager.get_project_id());
                            map.put("griev_title", details1.getTitle());
                            map.put("griev_name", details1.getName());
                            map.put("VoterID", details1.getVoter());
                            map.put("griev_date", details1.getDate());
                            map.put("griev_type", details1.getType());
                            map.put("griev_sub_type", details1.getSubType());
                            map.put("griev_remarks", details1.getRemark());
                            map.put("griev_priority", details1.getProiroty());
                            map.put("griev_SLATime", details1.getSlaTime());
                            map.put("griev_status", details1.getStatus());
                            map.put("griev_rewards", details1.getRewards());
                            dialog.show();
                            addGrievanceData(API.UPDATE_GRIEVANCE_DETAILS, details1, pos, true, map);

                        } else {
                            Toastmsg(GrievanceActivity.this, noInternet);
                        }


                    } else {
                        Toastmsg(GrievanceActivity.this, allFieldsRequired);
                    }


                } else {
                    Toastmsg(GrievanceActivity.this, "No Changes Detected");
                }


            }
        });

        if (isEditable) {
            txtStatus.setVisibility(View.GONE);
            txtType.setVisibility(View.GONE);
            txtSubType.setVisibility(View.GONE);
            txtPriority.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            set_adapter_for_spinner(typeList, typeSpinner);
            set_adapter_for_spinner(subTypeList, subTypeSpinner);
            set_adapter_for_spinner(priorityList, prioritySpinner);
            set_adapter_for_spinner(statusList, statusSpinner);

            typeSpinner.setSelection(getSpinnerPos(typeList, details.getType()));
            subTypeSpinner.setSelection(getSpinnerPos(subTypeList, details.getSubType()));
            prioritySpinner.setSelection(getSpinnerPos(priorityList, details.getProiroty()));
            statusSpinner.setSelection(getSpinnerPos(statusList, details.getStatus()));

            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != 0) {
                        type = typeSpinner.getSelectedItem().toString();
                    } else {
                        type = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            subTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != 0) {
                        subType = subTypeSpinner.getSelectedItem().toString();
                    } else {
                        subType = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i != 0) {
                        priority = prioritySpinner.getSelectedItem().toString();
                    } else {
                        priority = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

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

            Calendar newCalendar = Calendar.getInstance();

            int mHour = newCalendar.get(Calendar.HOUR_OF_DAY);
            int mMinute = newCalendar.get(Calendar.MINUTE);

            final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.US);
            fromDatePickerDialog = new DatePickerDialog(GrievanceActivity.this, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    edtDate.setText(dateFormatter.format(newDate.getTime()));
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            edtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fromDatePickerDialog.show();
                }
            });

            timePickerDialog = new TimePickerDialog(GrievanceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {

                    edtTime.setText(i + ":" + i1);
                }
            }, mHour, mMinute, false);


            edtTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timePickerDialog.show();
                }
            });

        } else {
            txtStatus.setVisibility(View.VISIBLE);
            txtType.setVisibility(View.VISIBLE);
            txtSubType.setVisibility(View.VISIBLE);
            txtPriority.setVisibility(View.VISIBLE);
            txtStatus.setText(details.getStatus());
            txtType.setText(details.getType());
            txtSubType.setText(details.getSubType());
            txtPriority.setText(details.getProiroty());
            subTypeSpinner.setVisibility(View.GONE);
            prioritySpinner.setVisibility(View.GONE);
            typeSpinner.setVisibility(View.GONE);
            statusSpinner.setVisibility(View.GONE);

            disableEditText(edtTime);
            disableEditText(edtTitle);
            disableEditText(edtName);
            disableEditText(edtVoterName);
            disableEditText(edtDate);
            disableEditText(edtRemarks);
            disableEditText(edtRewards);
            btnSubmit.setVisibility(View.GONE);
        }


        addLayout.addView(myView);
    }

    private int getSpinnerPos(String[] list, String title) {
        int pos = 0;
        for (int i = 0; i <= list.length - 1; i++) {
            if (title.equalsIgnoreCase(list[i])) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fund_grievance_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                addLayout.setVisibility(View.VISIBLE);
                initializeFactory();
                showAddGrievanceLayout();
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
