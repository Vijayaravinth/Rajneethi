package com.software.cb.rajneethi.activity;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.fragment.ShowPayLoadFragment;
import com.software.cb.rajneethi.models.AudioFileDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.DataParser;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class LiveTrackActivity extends UtilActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.live_track)
    String title;


    @BindString(R.string.quota_exceed)
    String quotaExceed;

    @BindView(R.id.calendarView)
    CalendarView calendarView;

    GoogleMap mMap;
    SupportMapFragment mapFragment;

    private String TAG = "LiveTrack";

    MyDatabase db;

    String userid;

    IOSDialog dialog;

    int currentCount = 0;
    SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.user_spinner)
    Spinner userSpinner;
    private ArrayList<String> userList = new ArrayList<>();
    JSONArray theatreSurveyArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_track);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar,title);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        if (getIntent().getExtras() != null) {
            userid = getIntent().getExtras().getString("id");
        }

        dialog = show_dialog(this, false);

        db = new MyDatabase(this);

        setDateClickListenrForCalendar();
        calendarView.setMaxDate(System.currentTimeMillis() - 1);
        calendarView.setVisibility(GONE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);





        //  publishToAMQP();


        try {
            Cursor c = db.getLiveTrackCount(userid);

            Log.w(TAG, "cursor count :" + c.getCount());
            if (c.getCount() > 0) {

                c.moveToFirst();
                Log.w(TAG, "User values : id " + c.getString(1) + " last date :" + c.getString(2) + " current date :" + c.getString(3) + " count :" + c.getInt(4));

                if (c.getString(3).equalsIgnoreCase(get_current_date())) {

                    if (c.getInt(4) <= 25) {
                        currentCount = c.getInt(4);
                        // receiveMessage();
                    } else {
                        Toastmsg(LiveTrackActivity.this, quotaExceed);
                    }
                } else {

                    db.updateDateLiveTrack(get_current_date(), userid);
                    // receiveMessage();
                }


            } else {
                db.insertDataForLiveTrackCount(userid, get_current_date());

                // receiveMessage();
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


    }

    private void getCoordinates(String api) {
        StringRequest request = new StringRequest(Request.Method.GET, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray mapData = new JSONArray();

                dialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    Log.w(TAG, "response " + response);
                    if (array.length() > 0) {
                        LatLng myLocation1 = null;
                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.getUserType().equalsIgnoreCase("Supervisor")) {
                                if (object.getString("parentid").equals(sharedPreferenceManager.get_user_id()) || object.getString("parentid").equals(sharedPreferenceManager.get_username())) {
                                    myLocation1 = new LatLng(object.getDouble("lattitude"), object.getDouble("longitude"));

                                    userList.add(object.getString("pwname"));
                                    JSONObject mapObject = new JSONObject();
                                    mapObject.put("lattitude", object.getDouble("lattitude"));
                                    mapObject.put("longitude", object.getDouble("longitude"));
                                    mapObject.put("surveyflag", object.getString("surveyflag"));
                                    mapObject.put("pwname", object.getString("pwname"));
                                    mapObject.put("boothname", object.getString("boothname"));
                                    mapObject.put("surveyid", object.getString("surveyid"));

                                    mMap.addMarker(new MarkerOptions().position(myLocation1).snippet(mapObject.toString())).hideInfoWindow();
                                    mMap.setOnMarkerClickListener(LiveTrackActivity.this);
                                    theatreSurveyArray.put(mapObject);
                                }
                            } else {
                                userList.add(object.getString("pwname"));
                                myLocation1 = new LatLng(object.getDouble("lattitude"), object.getDouble("longitude"));
                                // map.addMarker(new MarkerOptions().position(myLocation1).title(object.getString("respondantname")));
                                JSONObject mapObject = new JSONObject();
                                mapObject.put("lattitude", object.getDouble("lattitude"));
                                mapObject.put("longitude", object.getDouble("longitude"));
                                mapObject.put("surveyflag", object.getString("surveyflag"));
                                mapObject.put("pwname", object.getString("pwname"));
                                mapObject.put("boothname", object.getString("boothname"));
                                mapObject.put("surveyid", object.getString("surveyid"));

                                theatreSurveyArray.put(mapObject);

                                mMap.addMarker(new MarkerOptions().position(myLocation1).snippet(mapObject.toString())).hideInfoWindow();
                                mMap.setOnMarkerClickListener(LiveTrackActivity.this);
                            }
                            ///
                        }

                        if (myLocation1 != null) {
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                    myLocation1).zoom(17).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                    } else {
                        Toastmsg(LiveTrackActivity.this, "No Record found");
                    }

                    Set<String> hs = new HashSet<>();
                    hs.addAll(userList);
                    userList.clear();
                    userList.addAll(hs);
                    userList.add(0, selectUser);
                    setAdapterForUser(userList);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
                dialog.dismiss();
                Toastmsg(LiveTrackActivity.this, Error);
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    @BindString(R.string.selectUser)
    String selectUser;

    @BindString(R.string.error)
    String Error;

    private void setAdapterForUser(ArrayList<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, R.id.txtSpinnerText, list);
        userSpinner.setAdapter(adapter);
        //     spinner.setThreshold(1);
        adapter.setDropDownViewResource(R.layout.spinner_textview);

     /*   spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMarkerBaseOnBooth(spinner.getText().toString());
            }
        });*/

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // hideOrShowSpinnerLayout();
                if (i != 0) {
                    //  setMarkerBaseOnUser(userSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setDateClickListenrForCalendar() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Log.w(TAG, "Date : " + i + "-" + (i1 + 1) + "-" + i2);
                ShowLastTodayLocation(i + "-" + (i1 + 1) + "-" + i2);
                calendarView.setVisibility(GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Party Worker")) {
            getMenuInflater().inflate(R.menu.stats_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.calendar:

                Log.w(TAG, "calendar is shown : " + calendarView.isShown());
                if (calendarView.getVisibility() == GONE) {
                    calendarView.setVisibility(View.VISIBLE);
                    //  calendarView.setVisibility(View.VISIBLE);
                } else {
                    calendarView.setVisibility(GONE);
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void ShowLastTodayLocation(String date) {
        try {
            Cursor c = db.getLastLocation(date, userid);

            Log.w(TAG, "cursor count :" + c.getCount());
            if (c.getCount() > 0) {

                if (c.moveToFirst()) {
                    do {

                        StringTokenizer tokenizer = new StringTokenizer(c.getString(0), "$");

                        ArrayList<String> tempList = new ArrayList<>();
                        while (tokenizer.hasMoreTokens()) {

                            // add tokens to AL
                            tempList.add(tokenizer.nextToken());
                        }

                        Log.w(TAG, "Url : " + c.getString(1));

                        if (!c.getString(1).isEmpty() || c.getString(1) != null) {
                            drawLineFromLocal(tempList, c.getString(1));
                        }

                    } while (c.moveToNext());
                }
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    private void drawLineFromLocal(ArrayList<String> tempList, String result) {

        StringTokenizer tokens = new StringTokenizer(tempList.get(0), ",");
        String lat = tokens.nextToken();
        String lng = tokens.nextToken();
        LatLng origin1 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        StringTokenizer tokens1 = new StringTokenizer(tempList.get(tempList.size() - 1), ",");
        String lat1 = tokens1.nextToken();
        String lng1 = tokens1.nextToken();
        double lati1 = Double.parseDouble(lat1);
        double lngi1 = Double.parseDouble(lng1);
        LatLng dest1 = new LatLng(lati1, lngi1);

        Log.w(TAG, "Start pos " + startPos + " current pos :" + currentPos);
        for (int i = 0; i <= tempList.size() - 1; i++) {

            Log.w(TAG, "i th position : " + i);
            String message = tempList.get(i);
            Double latitude = Double.parseDouble(message.substring(0, message.indexOf(",")));
            Double longitude = Double.parseDouble(message.substring(message.indexOf(",") + 1, message.length()));

            LatLng myLocation = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    myLocation).zoom(18).build();

            mMap.addMarker(new MarkerOptions().position(myLocation)
                    .title("My Location"));


            // WAY += message.trim() + "|";

        }


        ParserTask parserTask = new ParserTask();
        parserTask.execute(result);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        //add the marker locations that you'd like to display on the map
        boundsBuilder.include(origin1);
        boundsBuilder.include(dest1);
        final LatLngBounds bounds = boundsBuilder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
        mMap.animateCamera(cameraUpdate);


    }


    private boolean isUpdatedCount = false, isFirstTimeDraw = false;

    private int startPos = 0;
    private int currentPos = 7;
    private long lastInsertId;

    private void receiveMessage() {
        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                try {
                    JSONObject object = new JSONObject(msg.getData().getString("msg"));

                    String message = object.getString("location");

                    mapList.add(message);

                    if (!isFirstTimeDraw) {
                        if (mapList.size() == 7) {

                            try {
                                Cursor c = db.getLastLocation(get_current_date(), userid);
                                if (c.getCount() > 0) {
                                    c.moveToFirst();

                                    String tokenizer = c.getString(0);

                                    tokenizer = tokenizer.substring(tokenizer.lastIndexOf("$") + 1, tokenizer.length());
                                    mapList.add(startPos, tokenizer);
                                }
                            } catch (CursorIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            if (!isUpdatedCount) {
                                db.updateLiveTrackCount(currentCount + 1, userid);
                                isUpdatedCount = true;
                            }


                            String locations = "";
                            for (int i = startPos; i <= currentPos - 1; i++) {
                                if (i == currentPos - 1) {
                                    locations += mapList.get(i);
                                } else {
                                    locations += mapList.get(i) + "$";
                                }
                            }

                            Log.w(TAG, "Locations" + locations);
                            db.insertUrl(sharedPreferenceManager.get_project_id(), userid, get_current_date(), locations);
                            lastInsertId = db.last_insert_id;
                            if (checkInternet.isConnected()) {
                                Drawlines();
                            }
                            isFirstTimeDraw = true;
                        }
                    } else {

                        if (mapList.size() >= (currentPos + 7)) {

                            startPos = currentPos;
                            currentPos += 7;

                            try {
                                Cursor c = db.getLastLocation(get_current_date(), userid);
                                if (c.getCount() > 0) {
                                    c.moveToFirst();

                                    String tokenizer = c.getString(0);

                                    tokenizer = tokenizer.substring(tokenizer.lastIndexOf("$") + 1, tokenizer.length());
                                    mapList.add(startPos, tokenizer);
                                }
                            } catch (CursorIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }

                            String locations = "";
                            for (int i = startPos; i <= currentPos - 1; i++) {
                                if (i == currentPos - 1) {
                                    locations += mapList.get(i);
                                } else {
                                    locations += mapList.get(i) + "$";
                                }
                            }

                            Log.w(TAG, "Locations" + locations);
                            db.insertUrl(sharedPreferenceManager.get_project_id(), userid, get_current_date(), locations);
                            lastInsertId = db.last_insert_id;

                            if (checkInternet.isConnected()) {
                                Drawlines();
                            }

                        } else {

                        }


                    }
                    Log.w(TAG, "Incoming message :" + message + " list size :" + mapList.size());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

              /*  TextView tv = (TextView) findViewById(R.id.textView);
                Date now = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                tv.append(ft.format(now) + ' ' + message + '\n');*/
              /*  Double latitude = Double.parseDouble(message.substring(0, message.indexOf(",")));
                Double longitude = Double.parseDouble(message.substring(message.indexOf(",") + 1, message.length()));

                LatLng myLocation = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        myLocation).zoom(18).build();

                mMap.addMarker(new MarkerOptions().position(myLocation)
                        .title("My Location"));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                // String message = msg.getData().getString("msg");

              /*  Log.w(TAG, "message" + message);
                Double latitude = Double.parseDouble(message.substring(0, message.indexOf(",")));
                Double longitude = Double.parseDouble(message.substring(message.indexOf(",") + 1, message.length()));

                LatLng myLocation = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        myLocation).zoom(18).build();

                map.addMarker(new MarkerOptions().position(myLocation)
                        .title("My Location"));
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

            }
        };

        subscribe(incomingMessageHandler);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ShowLastTodayLocation(get_current_date());

        if (checkInternet.isConnected()) {
            dialog.show();
            getCoordinates(API.GET_AUDIO_FILES + sharedPreferenceManager.get_project_id());
        } else

        {
            Toastmsg(LiveTrackActivity.this, noInternet);
        }
    }

    @BindString(R.string.noInterest)
    String noInternet;

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    LatLng way;

    String Starting_Point;
    String Ending_Point;
    String WAY = "";

    LatLng origin, dest;


    ArrayList<String> mapList = new ArrayList<>();

    public void Drawlines() {
        StringTokenizer tokens = new StringTokenizer(mapList.get(startPos), ",");
        String lat = tokens.nextToken();
        String lng = tokens.nextToken();
        origin = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        StringTokenizer tokens1 = new StringTokenizer(mapList.get(currentPos - 1), ",");
        String lat1 = tokens1.nextToken();
        String lng1 = tokens1.nextToken();
        double lati1 = Double.parseDouble(lat1);
        double lngi1 = Double.parseDouble(lng1);
        dest = new LatLng(lati1, lngi1);

        Log.w(TAG, "Start pos " + startPos + " current pos :" + currentPos);
        for (int i = startPos; i <= currentPos - 1; i++) {

            Log.w(TAG, "i th position : " + i);
            String message = mapList.get(i);
            Double latitude = Double.parseDouble(message.substring(0, message.indexOf(",")));
            Double longitude = Double.parseDouble(message.substring(message.indexOf(",") + 1, message.length()));

            LatLng myLocation = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    myLocation).zoom(18).build();

            mMap.addMarker(new MarkerOptions().position(myLocation)
                    .title("My Location"));


            WAY += message.trim() + "|";

        }


        try {
            // Getting URL to the Google Directions API
            String url = getUrl(origin, dest, WAY);
            Log.w(TAG, "onMapClick " + url);
            FetchUrl FetchUrl = new FetchUrl();
            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            //add the marker locations that you'd like to display on the map
            boundsBuilder.include(origin);
            boundsBuilder.include(dest);
            final LatLngBounds bounds = boundsBuilder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            mMap.animateCamera(cameraUpdate);

            WAY = "";
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //get url from goole
    private String getUrl(LatLng origin, LatLng dest, String Waypoints) {
        Log.w(TAG, " getUrl LatLng " + origin + " " + dest);

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String waypoints = "waypoints=" + Waypoints;

        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        // return preferenceManager.get_goole_map_api() + output + "?" + parameters;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            JSONObject object = new JSONObject(marker.getSnippet());

            ShowPayLoadFragment frag = ShowPayLoadFragment.newInstance(new AudioFileDetails(object.getString("surveyid"), true, object.getString("surveyflag")));
            frag.show(getSupportFragmentManager(), "Dialog");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);

                Log.w("Background Task data", data);
            } catch (Exception e) {
                Log.w("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

         /*   if (!is_route_url_exist) {
                DBHelper db = new DBHelper(MapsActivity.this);
                db.insert_url(result + "", preferenceManager.getChildRouteNumber(), get_location_and_name_json_array().toString());
            }*/
            //myDataBase.insert_drawline(sharedPreferenceManager.getSchoolId(), Route_name, result);

            db.updateUrl(lastInsertId + "", result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.w("ParserTask jsonData ", jsonData[0]);
                DataParser parser = new DataParser();
                Log.w("ParserTask Print", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.w("ParserTask", "Executing routes");
                Log.w("ParserTask routes ", routes.toString());

            } catch (Exception e) {
                Log.w("ParserTask exception", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            // showDialog(getApplicationContext());
            //mCustomProgressDialog.dismiss("");
            // dismiss_dialog();

            try {
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;

                Log.w(TAG, " Drawlines " + result);
                // Toast.makeText(MapsActivity.this, result.toString(), Toast.LENGTH_LONG).show();

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        //jhh
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    Log.w(TAG, "Points " + points);

                    // Adding all the points in the route to LineOptions

                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.BLUE);

                    Log.w(TAG, "onPostExecute onPostExecute lineoptions decoded");

                }

                Log.w(TAG, " line Points" + lineOptions);

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    mMap.addPolyline(lineOptions);
                    Log.w(TAG, "onPostExecute with Polylines drawn " + lineOptions.toString());
                } else {
                    Log.w(TAG, "onPostExecute without Polylines drawn");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            //  Log.w(TAG, "Start Count " + start_count);
            //  Log.w(TAG, "End Count " + total_count);

            /*if (!is_route_url_exist) {
                try {
                    if (start_count < total_count) {
                        temp_drawMapViews.clear();
                        for (int i = start_count; i <= start_count + 7; i++) {
                            if (i >= total_count) {
                                break;
                            } else {
                                DrawMapView mapobj = drawMapViews.get(i);
                                Log.w(TAG, "Temp list Stop Name " + mapobj.getStopName());
                                temp_drawMapViews.add(mapobj);
                            }
                        }
                        start_count += 7;

                        if (isConnectingToInternet()) {
                            //mCustomProgressDialog.show("");
                            show_dialog(MapsActivity.this);
                            Drawlines();
                        } else {
                            noInternetConnection();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastMsg("Error");
                }
            }*/
        }
    }


}
