package com.software.cb.rajneethi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.common.collect.ContiguousSet;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.SelectedContactAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.EventsDetails;
import com.software.cb.rajneethi.models.SelectedContactDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.software.cb.rajneethi.widget.MyWidgetProvider;
import com.software.cb.rajneethi.widget.MywidgetListviewService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by monika on 5/10/2017.
 */

public class AddEventsActivity extends Util implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.edt_event_title)
    EditText edt_title;

    @BindView(R.id.edt_event_date)
    EditText edt_date;

    @BindString(R.string.sms_delivered)
    String sms_delivered;

    @BindString(R.string.sms_not_delivered)
    String sms_not_deliverd;

    @BindView(R.id.edt_event_time)
    EditText edt_time;
    @BindView(R.id.autocomplete_event_contacts)
    AutoCompleteTextView autoCompleteTextView_contacts;

    @BindView(R.id.edtBooths)
    AutoCompleteTextView edtBooths;
    @BindView(R.id.edt_event_place)
    EditText edt_place;

    MyDatabase db;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.btn_add_event)
    Button btn_add_event;

    @BindView(R.id.selected_contact_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.imgAdd)
    ImageView imgAdd;

    String contacts = "";

    private String TAG = "Add Event";

    private static int ACTIVITY_CHOOSE_FILE1 = 101;


    private ArrayList<String> contacts_list = new ArrayList<>();

    private ArrayList<SelectedContactDetails> contact_details_list = new ArrayList<>();

    // ArrayAdapter<?> adapter;
    private ArrayAdapter<String> adapter;

    private SelectedContactAdapter contact_adapter;

    String contactWithName = "";
    String type, id;

    @BindView(R.id.boothRecyclerview)
    RecyclerView boothRecyclerview;

    BroadcastReceiver receiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY

            };

    @BindString(R.string.add_events)
    String title;

    IOSDialog dialog;

    @BindView(R.id.txtFileName)
    TextView txtFileName;

    private SharedPreferenceManager sharedPreferenceManager;

    String eventTitle, eventDate, eventTime, booth, place, numbers;

    ArrayList<SelectedContactDetails> boothSelectedList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);
        ButterKnife.bind(this);
        db = new MyDatabase(this);

        setup_toolbar_with_back(toolbar, title);

        dialog = show_dialog(AddEventsActivity.this, false);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        set_horizontal_layout_manager(recyclerView, this);
        set_horizontal_layout_manager(boothRecyclerview, this);
        set_adapter_for_recyclerview();
        setAdapterForBooths();

        setDatePicker();
        getBooths();


        edt_date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.performClick();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    return true;
                }
                return false;
            }
        });

     //   getSupportLoaderManager().initLoader(0, null, AddEventsActivity.this);
        // setadapter();
        //AutoComplete Item Click Listener
        autoCompleteTextView_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String SelectedValue = (String) parent.getItemAtPosition(position);

                Log.w(TAG, "selected value : " + SelectedValue);

                SelectedContactDetails details = new SelectedContactDetails(SelectedValue);
                contact_details_list.add(0, details);

                contact_adapter.notifyDataSetChanged();

                if (contact_details_list.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                autoCompleteTextView_contacts.getText().clear();
            }
        });


        // register receiver
        // ---when the SMS has been delivered---
        registerReceiver(receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), sms_delivered,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), sms_not_deliverd,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));


        if (getIntent().getExtras() != null) {

            Bundle args = getIntent().getExtras();

            type = args.getString("type");

            assert type != null;
            if (type.equals("reschedule")) {
                id = args.getString("id");
                eventTitle = args.getString("eventTitle");
                eventDate = args.getString("date");
                eventTime = args.getString("time");
                place = args.getString("location");
                booth = args.getString("booth");
                numbers = args.getString("number");


                edt_title.setText(eventTitle);
                edt_date.setText(eventDate);
                edt_time.setText(eventTime);
                // edtBooths.setText(c.getString(5));
                edt_place.setText(place);
                btn_add_event.setText(update_event);

                try {

                    String[] oldBoothList = booth.split(",");
                    String[] oldNumbers = numbers.split(",");

                    for (int i = 0; i <= oldBoothList.length - 1; i++) {
                        boothSelectedList.add(new SelectedContactDetails(oldBoothList[i], "booth"));
                    }

                    if (boothSelectedList.size() > 0) {
                        boothRecyclerview.setVisibility(View.VISIBLE);
                        boothAdapter.notifyDataSetChanged();
                    }

                    for (int i = 0; i <= oldNumbers.length - 1; i++) {
                        String value = "Unknown\n" + oldNumbers[i];
                        contact_details_list.add(new SelectedContactDetails(value));
                    }

                    if (contact_details_list.size() > 0){
                        recyclerView.setVisibility(View.VISIBLE);
                        contact_adapter.notifyDataSetChanged();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
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

    ArrayList<String> booth_list = new ArrayList<>();

    private void getBooths() {
        try {
            Cursor c = db.getBooths();
            if (c.moveToFirst()) {
                do {
                    booth_list.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, booth_list);
        edtBooths.setAdapter(spinnerArrayAdapter);
        edtBooths.setThreshold(1);

        edtBooths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boothSelectedList.add(new SelectedContactDetails(edtBooths.getText().toString().trim(), "booth"));
                boothAdapter.notifyDataSetChanged();

                if (!boothRecyclerview.isShown()) {
                    boothRecyclerview.setVisibility(View.VISIBLE);
                }
                edtBooths.getText().clear();
            }
        });

    }

    DatePickerDialog fromDatePickerDialog;
    TimePickerDialog timePickerDialog;

    private void setDatePicker() {
        Calendar newCalendar = Calendar.getInstance();

        int mHour = newCalendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = newCalendar.get(Calendar.MINUTE);

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.US);
        fromDatePickerDialog = new DatePickerDialog(AddEventsActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edt_date.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });

        timePickerDialog = new TimePickerDialog(AddEventsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                edt_time.setText(i + ":" + i1);
            }
        }, mHour, mMinute, false);


        edt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });


    }

    @OnClick(R.id.btnChooseFile)
    public void chooseFile() {
        if (checkPermissionGranted(Constants.READ_EXTERNAL_STORAGE, this) && checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this)) {

            selectCSVFile();
        } else {
            askPermission(new String[]{Constants.WRITE_EXTERNAL_STORAGE, Constants.READ_EXTERNAL_STORAGE});
        }

    }

    @BindString(R.string.choosecsv)
    String chooseCSV;

    //select csv file
    private void selectCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), ACTIVITY_CHOOSE_FILE1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {

            if (requestCode == ACTIVITY_CHOOSE_FILE1) {

                //  Log.w(TAG, "Path : " + data.getData().getPath());
                // File file = new File(data.getData().getPath());
                String path = data.getData().getPath();
                String format = path.substring(path.lastIndexOf(".") + 1, path.length());

                if (format.equalsIgnoreCase("csv")) {

                    txtFileName.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                    new ReadContactsFromCSV().execute(path);
                } else {
                    Toastmsg(AddEventsActivity.this, chooseCSV);
                }
            } else {
                Log.w(TAG, "Result not ok");
            }
        } else {
            Log.w(TAG, "Result cancelled");
        }

    }

    private class ReadContactsFromCSV extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.w(TAG, "On post execute");
            dialog.dismiss();
            if (contact_details_list.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                contact_adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            String path = strings[0];
            Log.w(TAG, "Do in background");
            Log.w(TAG, "Path : " + path);

            try {
                File csvFile = new File(path);
                InputStream is = new FileInputStream(csvFile);
                BufferedReader reader = null;
                reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    Log.w(TAG, "Line : " + line);
                    String[] fields = line.split(",");
                    Log.w(TAG, "Fields " + "name : " + fields[0] + " mobile : " + fields[1]);

                    if (checkIsANumber(fields[1]) && fields[1].length() == 10) {
                        SelectedContactDetails details = new SelectedContactDetails((fields[0].isEmpty() ? "Unknown" : fields[0]) + "\n" + fields[1]);
                        contact_details_list.add(0, details);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toastmsg(AddEventsActivity.this, error);
            }


            return null;
        }
    }


    @BindString(R.string.error)
    String error;

    @OnClick(R.id.imgAdd)
    public void addContact() {
        String SelectedValue = autoCompleteTextView_contacts.getText().toString().trim();

        if (checkIsANumber(SelectedValue)) {
            if (SelectedValue.length() == 10) {

                SelectedValue = "Unknown \n" + SelectedValue;
                Log.w(TAG, "selected value : " + SelectedValue);
                String Number = SelectedValue.substring(SelectedValue.indexOf("\n") + 1, SelectedValue.length());

                SelectedContactDetails details = new SelectedContactDetails(SelectedValue);
                contact_details_list.add(0, details);

                contact_adapter.notifyDataSetChanged();

                if (contact_details_list.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                autoCompleteTextView_contacts.getText().clear();
            } else {
                Toastmsg(AddEventsActivity.this, validMobile);
            }

        } else {
            Toastmsg(AddEventsActivity.this, validMobile);
        }


    }

    @BindString(R.string.validMobile)
    String validMobile;

    @BindString(R.string.update_event)
    String update_event;

    public void remove_contacts(SelectedContactDetails details) {

        if (details.getType().equalsIgnoreCase("contact")) {
            contact_details_list.remove(details);
            contact_adapter.notifyDataSetChanged();
        } else {
            boothSelectedList.remove(details);
            boothAdapter.notifyDataSetChanged();
        }
    }

    private String check_contains_semicolon(String val) {
        String semicolon = "";
        try {
            semicolon = val.substring(val.length() - 1, val.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (semicolon.contains(";")) {
            return val.substring(0, val.length() - 1);
        } else {
            return val;
        }
    }


    public void back() {
        super.onBackPressed();
    }

    @BindString(R.string.noInternet)
    String noInternet;


    @OnClick(R.id.btn_add_event)
    public void add_event() {

        if (contact_details_list.size() > 0) {
        } else {
            Toastmsg(this, selectContact);
            return;
        }

        if (boothSelectedList.size() > 0) {
        } else {
            Toastmsg(this, "Please select booth");
            return;
        }
        for (int i = 0; i <= contact_details_list.size() - 1; i++) {
            SelectedContactDetails details = contact_details_list.get(i);

            String SelectedValue = details.getContactDetails();
            contactWithName += SelectedValue + ";";
            String Number = SelectedValue.substring(SelectedValue.indexOf("\n") + 1, SelectedValue.length());
            contacts += Number + ";";
        }

        Log.w(TAG, "Contacts293 " + contacts);
        if (!(edt_date.getText().toString().trim().isEmpty() || edt_time.getText().toString().trim().isEmpty() || edt_title.getText().toString().trim().isEmpty() || edt_place.getText().toString().trim().isEmpty())) {
            EventsDetails details = new EventsDetails(edt_title.getText().toString().trim(), edt_date.getText().toString().trim(), edt_time.getText().toString().trim(), contacts, edtBooths.getText().toString().trim().isEmpty() ? "" : edtBooths.getText().toString().trim(), contactWithName, edt_place.getText().toString().trim());

            String message = "Title : " + details.getEventTitle() + " Date : " + details.getEventDate() + " Time : " + details.getEventTime() + " Place :" + details.getPlace();
            if (type.equals("add")) {

                if (checkInternet.isConnected()) {
                    List<String> list = get_recipents(contacts);

                    HashMap<String, String> map = new HashMap<>();
                    for (int i = 0; i <= list.size() - 1; i++) {
                        map.put("participants[" + i + "]", list.get(i));
                    }

                    for (int i = 0; i <= boothSelectedList.size() - 1; i++) {
                        map.put("booth[" + i + "]", boothSelectedList.get(i).getContactDetails());
                    }

                    map.put("project_id", sharedPreferenceManager.get_project_id());
                    map.put("title", edt_title.getText().toString().trim());
                    map.put("date", edt_date.getText().toString().trim());
                    map.put("time", edt_title.getText().toString().trim());
                    map.put("location", edt_place.getText().toString().trim());


                    dialog.show();
                    addEvents(API.ADD_NEW_EVENTS, map);

                } else {
                    Toastmsg(AddEventsActivity.this, noInternet);
                }

                /*db.insert_events(details);

                List<String> list = get_recipents(contacts);
                Log.w(TAG, "size of recepent " + list.size());
                Toastmsg(AddEventsActivity.this, sending_sms);
                for (int i = 0; i <= list.size() - 1; i++) {
                    sendSms(list.get(i), message);
                }


                updateEventInWidget();*/
            } else if (type.equals("reschedule")) {

                if (checkInternet.isConnected()) {
                    List<String> list = get_recipents(contacts);

                    HashMap<String, String> map = new HashMap<>();
                    for (int i = 0; i <= list.size() - 1; i++) {
                        map.put("participants[" + i + "]", list.get(i));
                    }

                    for (int i = 0; i <= boothSelectedList.size() - 1; i++) {
                        map.put("booth[" + i + "]", boothSelectedList.get(i).getContactDetails());
                    }
                    map.put("event_id",id);
                    map.put("project_id", sharedPreferenceManager.get_project_id());
                    map.put("title", edt_title.getText().toString().trim());
                    map.put("date", edt_date.getText().toString().trim());
                    map.put("time", edt_title.getText().toString().trim());
                    map.put("location", edt_place.getText().toString().trim());


                    dialog.show();
                    addEvents(API.UPDATE_EVENTS, map);

                } else {
                    Toastmsg(AddEventsActivity.this, noInternet);
                }



            }
            // super.onBackPressed();
        } else {
            Toastmsg(this, all_fields_required);
        }

    }


    private void addEvents(String url, HashMap<String, String> map) {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("message").equalsIgnoreCase("Event Data is saved Successfully!")) {
                        updateEventInWidget();

                        boothSelectedList.clear();
                        contact_details_list.clear();
                        boothAdapter.notifyDataSetChanged();
                        contact_adapter.notifyDataSetChanged();
                        edt_time.getText().clear();
                        edt_title.getText().clear();
                        edt_date.getText().clear();
                        edt_place.getText().clear();
                        Toastmsg(AddEventsActivity.this, "Successfully added");
                    }else if(object.getString("message").equalsIgnoreCase("Event Data is Updated Successfully!")){
                        updateEventInWidget();

                        boothSelectedList.clear();
                        contact_details_list.clear();
                        boothAdapter.notifyDataSetChanged();
                        contact_adapter.notifyDataSetChanged();
                        edt_time.getText().clear();
                        edt_title.getText().clear();
                        edt_date.getText().clear();
                        edt_place.getText().clear();
                        Toastmsg(AddEventsActivity.this, "Successfully updated");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.w(TAG, "response :" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(AddEventsActivity.this, "Error try again later");
                Log.w(TAG, "error :" + error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    //update event in widget
    private void updateEventInWidget() {
        Intent intent = new Intent(this, MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EX  TRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);

       /* for (int appWidgetId : ids) {
            Log.w(TAG, "Appwidget id : " + appWidgetId);
            updateWidget(this, appWidgetManager, appWidgetId);
        }*/
    }


    @BindString(R.string.all_fileds_required)
    String all_fields_required;
    @BindString(R.string.sending_sms)
    String sending_sms;
    @BindString(R.string.whatsapp_select_contact)
    String selectContact;

    private List<String> get_recipents(String contacts) {
        contacts = check_contains_semicolon(contacts);
        List<String> list = Arrays.asList(contacts.split(";"));
        return list;
    }

    private void send(String recipent, String message) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.putExtra("address", recipent);
        i.putExtra("sms_body", message);
        i.setType("vnd.android-dir/mms-sms");
        startActivity(i);
    }


    private void set_adapter_for_recyclerview() {
        contact_adapter = new SelectedContactAdapter(this, contact_details_list, AddEventsActivity.this);
        recyclerView.setAdapter(contact_adapter);
    }

    SelectedContactAdapter boothAdapter;

    private void setAdapterForBooths() {
        boothAdapter = new SelectedContactAdapter(this, boothSelectedList, AddEventsActivity.this);
        boothRecyclerview.setAdapter(boothAdapter);
    }


    private void setadapter() {
        adapter = new ArrayAdapter<>(AddEventsActivity.this, android.R.layout.simple_list_item_1, contacts_list);
        autoCompleteTextView_contacts.setThreshold(2);//start searching from 3 character
        autoCompleteTextView_contacts.setAdapter(adapter);   //set the adapter for displaying profile id
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {

            unregisterReceiver(receiver);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null);
    }

   /* @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
*/


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            do {
                contacts_list.add(data.getString(1) + "\n" + data.getString(0));
            } while (data.moveToNext());
        }

        setadapter();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            ((AddEventsActivity) getActivity()).edt_time.setText(hourOfDay + ":" + minute);
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            String mon, date;
            if ((month + 1) < 10) {
                mon = "0" + (month + 1);
            } else {
                mon = (month + 1) + "";
            }
            if (day < 10) {
                date = "0" + day;
            } else {
                date = day + "";
            }
            ((AddEventsActivity) getActivity()).edt_date.setText(year + "-" + mon + "-" + date);
        }
    }


    private void sendSms(final String phoneNumber, final String message) {

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);


    }


}
