package com.software.cb.rajneethi.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.EventsAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.EventsDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.GridSpacingItemDecoration;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;
import com.software.cb.rajneethi.widget.MyWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by monika on 5/10/2017.
 */

public class TimeTableActivity extends Util {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.fab_add_events)
    FloatingActionButton fab_add_events;

    @BindView(R.id.events_recyclerview)
    RecyclerView recyclerView;


    ArrayList<EventsDetails> list = new ArrayList<>();
    private MyDatabase db;

    IOSDialog dialog;
    @BindView(R.id.btnRetry)
    Button btnRetry;

    private EventsDetails details;

    @BindView(R.id.txt_no_events)
    TextView txt_no_events;

    EventsAdapter adapter;
    private String TAG = "Events";

    BroadcastReceiver receiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    @BindString(R.string.events)
    String toolbarTitle;

    int rowCount, spacing;
    boolean isTablet;

    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, toolbarTitle);

        dialog = show_dialog(this, false);
        db = new MyDatabase(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            rowCount = 3;
            spacing = 30;
        } else {
            rowCount = 2;
            spacing = 20;
        }
        Log.w(TAG, "Is boolean : " + isTablet);

        //   set_Linearlayout_manager(recyclerView, this);
        set_grid_layout_manager(recyclerView, this, rowCount);
        set_adapter();

        // register receiver
        // ---when the SMS has been delivered---
    /*    registerReceiver(receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), smsDeleivered,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), smsNotDelivered,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));*/



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.add:
                startActivity(new Intent(TimeTableActivity.this, AddEventsActivity.class).putExtra("type", "add"));
/*
               if (checkPermissionGranted(Constants.READ_CONTACTS, this) && checkPermissionGranted(Constants.SEND_SMS, this)) {

                } else {
                    askPermission(new String[]{Constants.READ_CONTACTS, Constants.SEND_SMS});
                }*/
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    public void delete(int pos, String id){

        if (checkInternet.isConnected()){
            dialog.show();
            deleteEvents(id,pos);
        }else{
            Toastmsg(TimeTableActivity.this, "No internet connection");
        }

    }

    private void deleteEvents(String id, int pos){

        StringRequest request = new StringRequest(Request.Method.GET, API.DELETE_EVENTS+"event_id="+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.w(TAG,"response : "+ response);
                dialog.dismiss();
                try{
                    list.remove(pos);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(TimeTableActivity.this,"Error try again later");
            }
        });


        VolleySingleton.getInstance(this).addToRequestQueue(request);



    }


    private void notidyAdapter(){
        if (list.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txt_no_events.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(View.GONE);
            txt_no_events.setVisibility(View.VISIBLE);
        }

    }
    public void getEvents() {

        StringRequest request = new StringRequest(Request.Method.GET, API.GET_EVENTS + "project_id=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                btnRetry.setVisibility(View.GONE);

                dialog.dismiss();
                list.clear();

                Log.w(TAG,"response :"+ response);

                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = new JSONArray(object.getString("message"));
                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject eventObj = array.getJSONObject(i);

                        list.add(new EventsDetails(eventObj.getString("id"),eventObj.getString("title"),
                                eventObj.getString("start_date"),eventObj.getString("time"),eventObj.getString("appointment_type"),
                              "",eventObj.getString("location"),false,eventObj.getString("booth")));
                    }


                      notidyAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                    notidyAdapter();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                btnRetry.setVisibility(View.VISIBLE);
            }
        });

        VolleySingleton.getInstance(TimeTableActivity.this).addToRequestQueue(request);

    }

    @BindString(R.string.sending_sms)
    String sendingSms;


    @BindString(R.string.sms_delivered)
    String smsDeleivered;
    @BindString(R.string.sms_not_delivered)
    String smsNotDelivered;

    public void send_sms(EventsDetails details) {
     /*   String message = "Title : " + details.getEventTitle() + " Date : " + details.getEventDate() + " Time : " + details.getEventTime() + " Place :" + details.getPlace();
        // send(details.getContacts(), details.getRemarks());

        List<String> list = get_recipents(details.getContacts());
        Toastmsg(TimeTableActivity.this, sendingSms);
        Log.w(TAG, "size of recepent " + list.size());
        for (int i = 0; i <= list.size() - 1; i++) {
            sendSms(list.get(i), message);
        }*/

    }

    private void sendSms(final String phoneNumber, final String message) {

     /*   PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
*/
    }

    private List<String> get_recipents(String contacts) {
        contacts = check_contains_semicolon(contacts);
        List<String> list = Arrays.asList(contacts.split(";"));
        return list;
    }

    private String check_contains_semicolon(String val) {
        String semicolon = "";
        semicolon = val.substring(val.length() - 1, val.length());

        if (semicolon.contains(";")) {
            return val.substring(0, val.length() - 1);
        } else {
            return val;
        }
    }


    public void cancel_event(EventsDetails details) {
        list.remove(details);
        adapter.notifyDataSetChanged();
        Log.w(TAG, " id " + details.getId());
        db.delete_event(details.getId());
        String message = "Title : " + details.getEventTitle() + " event has been cancelled ";

        Toastmsg(TimeTableActivity.this, sendingSms);
        updateEventInWidget();

        List<String> list = get_recipents(details.getContacts());
        Log.w(TAG, "size of recepent " + list.size());
        for (int i = 0; i <= list.size() - 1; i++) {
            sendSms(list.get(i), message);
        }

    }


    //update event in widget
    private void updateEventInWidget() {
        Intent intent = new Intent(this, MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getApplication(), MyWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    private void set_adapter() {
        adapter = new EventsAdapter(this, list, TimeTableActivity.this);
        recyclerView.setAdapter(adapter);
        if (!isTablet) {
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(rowCount, spacing, true));
        }
    }

    private void get_data() {
        try {
            Cursor c = db.get_events();
            if (c.moveToFirst()) {
                do {
                    List<String> contactList = Arrays.asList(c.getString(4).split(";"));
                    try {
                        String eventTime = c.getString(3);
                        Time tme = new Time(Integer.parseInt(eventTime.substring(0, eventTime.indexOf(":"))), Integer.parseInt(eventTime.substring(eventTime.lastIndexOf(":") + 1, eventTime.length())), 0);//seconds by default set to zero
                        Format formatter;
                        formatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
                        //   Date date = format.parse(c.getString(3));
                        String time = formatter.format(tme);
                        Log.w(TAG, "Time : " + time);
                        details = new EventsDetails(c.getString(0), c.getString(1), c.getString(2), time, contactList.size() + "", c.getString(5), c.getString(7), false);
                        list.add(details);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            recyclerView.setVisibility(View.GONE);
            txt_no_events.setVisibility(View.VISIBLE);
        }


        Log.w(TAG, "list size " + list.size());
        if (list.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txt_no_events.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(View.GONE);
            txt_no_events.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.fab_add_events)
    public void add_events() {
        if (checkPermissionGranted(Constants.READ_CONTACTS, this) && checkPermissionGranted(Constants.SEND_SMS, this)) {
            startActivity(new Intent(TimeTableActivity.this, AddEventsActivity.class).putExtra("type", "add"));
        } else {
            askPermission(new String[]{Constants.READ_CONTACTS, Constants.SEND_SMS});
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    public void back() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void callApI(){
        if (checkInternet.isConnected()){
            dialog.show();
            getEvents();
        }else{
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btnRetry)
    public void retry(){
        callApI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        callApI();

    }

    public void reschedule_event(EventsDetails details) {
        if (checkPermissionGranted(Constants.READ_CONTACTS, this)) {
            startActivity(new Intent(TimeTableActivity.this, AddEventsActivity.class).putExtra("type", "reschedule").putExtra("id", details.getId())
            .putExtra("eventTitle",details.getEventTitle()).putExtra("date",details.getEventDate()).putExtra("time",details.getEventTime())
                    .putExtra("location",details.getPlace()).putExtra("number",details.getContacts()).putExtra("booth",details.getBooths())
            );
        } else {
            askPermission(new String[]{Constants.READ_CONTACTS});
        }
    }

 /*   private void send(String recipent, String message) {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.putExtra("address", recipent);
        i.putExtra("sms_body", message);
        i.setType("vnd.android-dir/mms-sms");
        startActivity(i);
    }
*/
    @Override
    protected void onDestroy() {

        if(receiver != null) {
            unregisterReceiver(receiver);
        }

        super.onDestroy();
    }
}
