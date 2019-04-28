package com.software.cb.rajneethi.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.ChartAdapter;
import com.software.cb.rajneethi.adapter.GOTVAdapter;
import com.software.cb.rajneethi.adapter.GotvStatisticsAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.GraphDetails;
import com.software.cb.rajneethi.models.VoterDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by w7 on 11/2/2017.
 */

public class GOTVActivity extends Util implements TextWatcher {


    private static final String TAG = "VoterSearchActivity";

    @BindView(R.id.piechart)
    public PieChart pieChart;


    @BindView(R.id.btn_retry)
    Button btnRetry;
    private ArrayList<GraphDetails> graphDetailsList = new ArrayList<>();

    @BindView(R.id.caste_recyclerview)
    RecyclerView graphRecyclerview;

    @BindView(R.id.autocompltetextview)
    Spinner boothSpinner;


    @BindView(R.id.edt_search)
    EditText edt_search;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.search_recyclerview)
    RecyclerView recyclerView;

    ArrayList<VoterDetails> list = new ArrayList<>();
    GOTVAdapter adapter;
    private VotersDatabase myDatabase;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ArrayList<String> booth_list = new ArrayList<>();

    @BindString(R.string.gotv)
    String toolbarTitle;

    @BindView(R.id.booth_list_spinner)
    Spinner booth_list_spinner;

    @BindView(R.id.img_voice_search)
    ImageView img_voice_search;

    IOSDialog dialog = null;

    private MyDatabase db;
    @BindString(R.string.noInternet)
    String noInternet;

    public boolean bskMode;

    String boothName = "default";

    boolean isStatistics = true;

    boolean isSearch = false;

    @BindView(R.id.statisticsLayout)
    View statisticsLayout;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    public int animPosition = -1;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votersearch);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, toolbarTitle);

        dialog = show_dialog(GOTVActivity.this, false);

        myDatabase = new VotersDatabase(this);
        db = new MyDatabase(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        set_Linearlayout_manager(recyclerView, this);

        set_adapter(list);

        edt_search.addTextChangedListener(this);

        //Search Voter Data When Click Search Image
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String VoterData = edt_search.getText().toString().trim();

                if (VoterData.length() > 0) {

                    new getDataFromDb().execute(VoterData);
                    //searchData(VoterData);
                }
            }
        });

        try {
            Cursor c = db.getBooths();
            Log.w(TAG, "Booth count :" + c.getCount());
            if (c.moveToFirst()) {
                do {
                    booth_list.add(c.getString(0));
                } while (c.moveToNext());
            }
            c.close();
            loadDataToSpinner();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            if (checkInternet.isConnected()) {
                dialog.show();
                get_booth_details();
            } else {
                Toastmsg(GOTVActivity.this, noInternet);
            }
        }
    }


    ArrayList<String> attributeNameList = new ArrayList<>();
    ArrayList<String> attributeValueList = new ArrayList<>();

    private void getGOTVStatistics(String boothName) {
        StringRequest request = new StringRequest(Request.Method.GET, API.GET_GOTV_STATISTICS + "constituency_id=" + sharedPreferenceManager.get_project_id() + "&gotv_search_attribute=" + boothName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                graphDetailsList.clear();
                attributeNameList.clear();
                attributeValueList.clear();
                pieChart.clearChart();
                Log.w(TAG, "response : " + response);
                dialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response);

                    JSONObject msgObject = object.getJSONObject("message");
                    //  fragment.txtView.setText("Position :" + position);

                    getChoices(new JSONArray(msgObject.getString("grapth_data")));


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> {

            dialog.dismiss();
            Toastmsg(GOTVActivity.this, "Error try again later");
            btnRetry.setVisibility(View.VISIBLE);
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }


    /*set adapter for recyclerview*/
    private void set_adapter_for_recyclerview(ArrayList<GraphDetails> list, RecyclerView recyclerView) {
        ChartAdapter adapter = new ChartAdapter(list, this, GOTVActivity.this);
        recyclerView.setAdapter(adapter);
    }


    public int total;

    private void getChoices(JSONArray jsonArray) throws JSONException {

        total = 0;

        for (int i = 0; i < jsonArray.length(); i++)
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                attributeNameList.add(object.getString("type"));
                attributeValueList.add(object.getString("cnt_aff"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        for (int i = 0; i <= attributeValueList.size() - 1; i++) {
            int color = randomColor();
            total += Integer.parseInt(attributeValueList.get(i));
            pieChart.addPieSlice(new PieModel(attributeNameList.get(i), Float.parseFloat(attributeValueList.get(i)), color));
            graphDetailsList.add(new GraphDetails(attributeNameList.get(i), attributeValueList.get(i), color));
        }

        if (graphDetailsList.size() > 0) {
            set_grid_layout_manager(graphRecyclerview, this, 2);
            set_adapter_for_recyclerview(graphDetailsList, graphRecyclerview);
            pieChart.startAnimation();
        }


    }

    @OnClick(R.id.btn_retry)
    public void retry() {
        callAPI();
    }

    private void callAPI() {

        if (checkInternet.isConnected()) {
            btnRetry.setVisibility(GONE);
            dialog.show();
            if (isStatistics) {
                getGOTVStatistics(boothName);
            }
        } else {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    private void set_adapterBooths() {

     //   booth_list.add(0, selectBooth);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, booth_list);
        boothSpinner.setAdapter(spinnerArrayAdapter);


        boothSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position != 0) {

                    boothName = boothSpinner.getSelectedItem().toString();
                    callAPI();
                } else {
                    boothName = "default";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    GotvStatisticsAdapter statisticsAdapter;

    private void setAdapterForGraph() {
        statisticsAdapter = new GotvStatisticsAdapter(graphDetailsList, this, GOTVActivity.this);
        graphRecyclerview.setAdapter(adapter);
    }

    /*get booth details*/
    private void get_booth_details() {
        StringRequest request = new StringRequest(Request.Method.GET, API.BOOTH_INFO + "userId=" + sharedPreferenceManager.get_user_id() + "&constituencyId=" + sharedPreferenceManager.get_constituency_id() + "&projectId=" + sharedPreferenceManager.get_project_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                Log.w(TAG, "Response " + response);
                try {
                    JSONArray array = new JSONArray(response);

                    if (array.length() > 0) {

                        db.deleteBooths();

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {
                                int status = object.getInt("astatus");
                                if (status != 0) {
                                    db.insertBooths(object.getString("booth"));
                                    booth_list.add(object.getString("booth"));
                                }
                            } else {
                                db.insertBooths(object.getString("booth"));
                                booth_list.add(object.getString("booth"));

                            }

                        }

                        loadDataToSpinner();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());

                dialog.dismiss();

                Toastmsg(GOTVActivity.this, Error);

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @BindString(R.string.Error)
    String Error;


    private void showBoothSpinner() {
        if (!booth_list_spinner.isShown()) {
            booth_list_spinner.setVisibility(View.VISIBLE);
        }
    }


    @BindString(R.string.selectBooth)
    String selectBooth;

    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    /*load data to spinner*/
    private void loadDataToSpinner() {
        if (booth_list.size() > 0) {
            set_adapter_for_booth_list();
            set_adapterBooths();
        } else {
            Toastmsg(GOTVActivity.this, noBoothAllocated);
        }
    }

    String booth_name = "";

    //set adapter for booth
    private void set_adapter_for_booth_list() {

        booth_list.add(0, selectBooth);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, booth_list);
        booth_list_spinner.setAdapter(spinnerArrayAdapter);

        booth_list_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    if (booth_list_spinner.getSelectedItem().toString().equals("DEFAULT")) {
                        //  makeAdapterNull();
                        isSearch = true;
                        set_adapter(list);
                    } else {
                        //   makeAdapterNull();
                        booth_name = booth_list_spinner.getSelectedItem().toString().replace("-", "_");
                        sortVoter(booth_list_spinner.getSelectedItem().toString().replace("-", "_"));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
            goneView(img_voice_search);
            showView(imgSearch);
        } else {
            showView(img_voice_search);
            goneView(imgSearch);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    private void showView(View view) {
        if (!view.isShown()) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void goneView(View v) {
        if (v.isShown()) {
            v.setVisibility(View.GONE);
        }
    }

    private class getDataFromDb extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            if (list.size() > 0) {
                isSearch = true;
                adapter.notifyDataSetChanged();
                showBoothSpinner();

                if (booth_list.size() > 0) {
                    booth_list_spinner.setSelection(0);
                }
            } else {
                Toastmsg(GOTVActivity.this, noRecordFound);
            }

            img_voice_search.setVisibility(View.VISIBLE);
            imgSearch.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.clear();
            booth_name = "";
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            edt_search.getText().clear();
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            searchData(strings[0]);
            return null;
        }
    }

    //Search data from local database
    private void searchData(String _VoterName) {

        Cursor c = myDatabase.getSearchVotersData(_VoterName);
        if (c != null) {
            c.moveToFirst();
            Log.w(TAG, "Cursor count " + c.getCount());
            if (c.moveToFirst()) {
                try {
                    do {

                        VoterDetails details = new VoterDetails(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(21) == null || c.getString(21).equals("false") ? "false" : c.getString(21), c.getInt(22), c.getString(23), c.getString(24), c.getInt(25) + "", false);
                        list.add(details);

                    } while (c.moveToNext());

                } catch (Exception e) {
                    //   Toastmsg(VoterSearchActivity.this, noRecordFound);
                    e.printStackTrace();
                }
            }
            c.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voter_search_menu, menu);
        MenuItem item = menu.findItem(R.id.change);
        if (sharedPreferenceManager.get_keep_login_role().equalsIgnoreCase("Party Worker")){
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    //generate random
    private int generateRandom() {
        Random random = new Random();
        return random.nextInt(5);
    }

    /*sort the list*/
    private void sortVoter(String boothName) {

        searchList.clear();

        for (int i = 0; i <= list.size() - 1; i++) {
            VoterDetails details = list.get(i);
            if (boothName.equals(details.getBoothName())) {
                searchList.add(details);
            }

        }

        if (searchList.size() > 0) {
            isSearch = false;
            set_adapter(searchList);
        } else {
            Toastmsg(GOTVActivity.this, noRecordFound);
        }
        Log.w(TAG, "List size " + list.size());
    }

    //sort by age
    private void sortVoterWithAge(int age) {

        searchList.clear();
        for (int i = 0; i <= list.size() - 1; i++) {
            VoterDetails details = list.get(i);
            if (age == 30) {

                if (Integer.parseInt(details.getAge()) <= age) {
                    searchList.add(details);
                }

            } else if (age == 40) {

                if (Integer.parseInt(details.getAge()) >= 30 && Integer.parseInt(details.getAge()) <= 40) {
                    searchList.add(details);

                }
            } else if (age == 50) {

                if (Integer.parseInt(details.getAge()) >= 40 && Integer.parseInt(details.getAge()) <= 50) {
                    searchList.add(details);

                }
            } else {
                if (Integer.parseInt(details.getAge()) >= age) {
                    searchList.add(details);
                }

            }
        }
        if (searchList.size() > 0) {
            isSearch = false;
            set_adapter(searchList);
        } else {
            Toastmsg(GOTVActivity.this, noRecordFound);
        }
    }

    private int checkListSize() {
        return list.size();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.age30:
                if (checkListSize() > 0) {
                    sortVoterWithAge(30);
                } else {
                    Toastmsg(GOTVActivity.this, noRecordFound);
                }
                return true;
            case R.id.age40:
                if (checkListSize() > 0) {
                    sortVoterWithAge(40);
                } else {
                    Toastmsg(GOTVActivity.this, noRecordFound);
                }
                return true;
            case R.id.age50:
                if (checkListSize() > 0) {
                    sortVoterWithAge(50);
                } else {
                    Toastmsg(GOTVActivity.this, noRecordFound);
                }
                return true;
            case R.id.agegreater50:
                if (checkListSize() > 0) {
                    sortVoterWithAge(51);
                } else {
                    Toastmsg(GOTVActivity.this, noRecordFound);
                }
                return true;
            case R.id.defaultData:
                if (checkListSize() > 0) {
                    set_adapter(list);
                } else {
                    Toastmsg(GOTVActivity.this, noRecordFound);
                }

                return true;

            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.change:
                if (mainLayout.isShown()) {
                    set_Linearlayout_manager(graphRecyclerview, this);
                  /*  if (statisticsAdapter == null) {
                        setAdapterForGraph();
                    }*/

                  try{
                      boothSpinner.setSelection(1);
                  }catch (Exception e){
                    e.printStackTrace();
                  }

                    callAPI();

                    statisticsLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                } else {
                    statisticsLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /*generate random color*/
    public int randomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }

    /*hide booth spinner*/
    private void hideBoothSpinner() {
        if (booth_list_spinner.isShown()) {
            booth_list_spinner.setVisibility(View.GONE);
        }
    }

    @BindString(R.string.noRecordFound)
    String noRecordFound;
    @BindString(R.string.noDBFound)
    String noDbFound;

    private ArrayList<VoterDetails> searchList = new ArrayList<>();

    private void set_adapter(ArrayList<VoterDetails> list) {
        adapter = new GOTVAdapter(this, list, GOTVActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));
    }

    @OnClick(R.id.img_voice_search)
    public void voiceSearch() {
        promptSearch();
    }

    //prompt search
    private void promptSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, Constants.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // txtSpeechInput.setText(result.get(0));
                    Log.w(TAG, "voice input : " + result.get(0));
                    String VoterData = result.get(0);
                    if (VoterData.length() > 0) {

                        new getDataFromDb().execute(VoterData);
                        //searchData(VoterData);
                    }
                }
                break;
            }

        }
    }


    @SuppressLint("MissingPermission")
    public void call(String mobileNumber) {
        Log.w(TAG, "Mobile number");

        try {
            if (checkPermissionGranted(Constants.CALL, this)) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber.trim()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            } else {
                askPermission(new String[]{Constants.CALL});
            }
        } catch (Exception e) {
            Toastmsg(GOTVActivity.this, Error);
        }
    }


    public void shareData(String shareContent, String mobile) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.putExtra(Intent.ACTION_SENDTO, "8675505758");
        intent.setType("text/plain");
        startActivity(intent);
    }

    public void sendMessage(String mobile) {

        if (mobile != null) {
            if (checkInternet.isConnected()) {
                dialog.show();
                sendSMS(mobile);
            }
        } else {
            Toastmsg(GOTVActivity.this, validMobile);
        }
    }

    @BindString(R.string.mobileNotAvailable)
    String validMobile;

    public void sendSMS(String mobile) {
        StringRequest request = new StringRequest(Request.Method.GET, "http://103.16.101.52:8080/sendsms/bulksms?username=nxtr-vlgokul&password=123456&type=0&dlr=1&destination=" + mobile + "&source=NXTEDU&message=hi%20test", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                Toastmsg(GOTVActivity.this, Error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        queue.getCache().clear();
    }

    public void updateVote(int position, String voterId, int vnv) {

        animPosition = position;

        if (isSearch) {
            myDatabase.updateVoted(voterId, vnv);
            VoterDetails details = list.get(position);
            details.setVNV(vnv + "");
        } else {
            myDatabase.updateVoted(voterId, vnv);
            VoterDetails details = searchList.get(position);
            details.setVNV(vnv + "");
        }

        adapter.notifyDataSetChanged();

       /* StringRequest request = new StringRequest(Request.Method.POST, API.UPDATE_VOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        queue.getCache().clear();
*/
    }


}
