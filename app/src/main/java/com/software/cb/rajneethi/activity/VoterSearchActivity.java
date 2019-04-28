package com.software.cb.rajneethi.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.software.cb.rajneethi.adapter.FamilyDetailsAdapter;
import com.software.cb.rajneethi.adapter.SearchAdapter;
import com.software.cb.rajneethi.api.API;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.VoterDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.Util;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;
import com.gmail.samehadar.iosdialog.IOSDialog;

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

/**
 * Created by Monica on 11-03-2017.
 */

public class VoterSearchActivity extends UtilActivity implements TextWatcher {

    private static final String TAG = "VoterSearchActivity";

    @BindView(R.id.edt_search)
    EditText edt_search;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.search_recyclerview)
    RecyclerView recyclerView;

    ArrayList<VoterDetails> list = new ArrayList<>();
    SearchAdapter adapter;
    private VotersDatabase myDatabase;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindString(R.string.searchVoters)
    String toolbarTitle;

    @BindView(R.id.booth_list_spinner)
    Spinner booth_list_spinner;


    IOSDialog dialog = null;

    private MyDatabase myDB;
    @BindString(R.string.noInternet)
    String noInternet;

    public boolean bskMode;

    private SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.img_voice_search)
    ImageView img_voice_search;

    public String voterCardnumber = "";

    private int lastPosition = -1;
    private String selectVoterCardnumber = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votersearch);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, toolbarTitle);

        dialog = show_dialog(VoterSearchActivity.this, false);

        if (getIntent().getExtras() != null) {
            bskMode = getIntent().getExtras().getBoolean("bskMode");
        }

        myDatabase = new VotersDatabase(this);
        myDB = new MyDatabase(this);
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


        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String VoterData = edt_search.getText().toString().trim();

                    if (VoterData.length() > 0) {

                        new getDataFromDb().execute(VoterData);
                        //searchData(VoterData);
                    }
                    return true;
                }
                return false;
            }
        });


        try {
            Cursor c = myDB.getBooths();
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
                Toastmsg(VoterSearchActivity.this, noInternet);
            }
        }
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

    //check gps is enabled
    public boolean checkGPSEnabled() {
        return is_enabled_gps(VoterSearchActivity.this);
    }

    //check permissions are granted
    public boolean checkPermission() {

        return checkPermissionGranted(Constants.RECORD_AUDIO, this) && checkPermissionGranted(Constants.LOCATION_PERMISSION, this) && checkPermissionGranted(Constants.READ_CONTACTS, this) && checkPermissionGranted(Constants.WRITE_EXTERNAL_STORAGE, this);
    }

    //ask permmission
    public void askPermission() {

        askPermission(new String[]{Constants.RECORD_AUDIO, Constants.WRITE_EXTERNAL_STORAGE, Constants.LOCATION_PERMISSION, Constants.READ_CONTACTS});
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

                        myDB.deleteBooths();

                        for (int i = 0; i <= array.length() - 1; i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {
                                int status = object.getInt("astatus");
                                if (status != 0) {
                                    myDB.insertBooths(object.getString("booth"));
                                    booth_list.add(object.getString("booth"));
                                }
                            } else {
                                myDB.insertBooths(object.getString("booth"));
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

                Toastmsg(VoterSearchActivity.this, Error);

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

    private ArrayList<String> booth_list = new ArrayList<>();

    @BindString(R.string.selectBooth)
    String selectBooth;

    @BindString(R.string.noBoothAllocated)
    String noBoothAllocated;

    /*load data to spinner*/
    private void loadDataToSpinner() {
        if (booth_list.size() > 0) {
            set_adapter_for_booth_list();
        } else {
            Toastmsg(VoterSearchActivity.this, noBoothAllocated);
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

        Log.w(TAG, "On text changed working : length : " + charSequence.length());

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
                adapter.notifyDataSetChanged();
                showBoothSpinner();

                if (booth_list.size() > 0) {
                    booth_list_spinner.setSelection(0);
                }
            } else {
                Toastmsg(VoterSearchActivity.this, noRecordFound);
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

        String boothNames = "";
        for (int i = 1; i <= booth_list.size() - 1; i++) {
            String name = booth_list.get(i);
            name = name.replaceAll("-", "_");
            boothNames += "'" + name + "',";
            //  boothNames.append(",");
        }
        Cursor c = null;

        if (sharedPreferenceManager.get_keep_login_role().equals("Party Worker")) {
            if (!boothNames.isEmpty()) {
                c = myDatabase.getVoterFromParticularBooth(boothNames.substring(0, boothNames.length() - 1), _VoterName);
            } else {
                Toastmsg(VoterSearchActivity.this, noBoothAllocated);
            }
        } else {
            c = myDatabase.getSearchVotersData(_VoterName);
        }
        if (c != null) {
            c.moveToFirst();
            Log.w(TAG, "Cursor count " + c.getCount());
            if (c.moveToFirst()) {
                try {
                    do {

                        Log.w(TAG, "mobile :" + c.getInt(23));

                        //   Log.w(TAG, "SOS value :" + c.getInt(22));
                        VoterDetails details = new VoterDetails(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(21) == null || c.getString(21).equals("false") ? "false" : c.getString(21), c.getInt(22), c.getString(23) + "", c.getInt(24) + "", c.getInt(25) + "");
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
        item.setVisible(false);


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
        adapter.notifyDataSetChanged();

        for (int i = 0; i <= list.size() - 1; i++) {
            VoterDetails details = list.get(i);
            if (boothName.equals(details.getBoothName())) {
                searchList.add(details);
            }

        }

        if (searchList.size() > 0) {
            set_adapter(searchList);
        } else {
            Toastmsg(VoterSearchActivity.this, noRecordFound);
        }
        Log.w(TAG, "List size " + list.size());
    }

    //sort by age
    private void sortVoterWithAge(int age) {

        searchList.clear();
        //recyclerView.setAdapter(null);
        adapter.notifyDataSetChanged();
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
            set_adapter(searchList);
        } else {
            Toastmsg(VoterSearchActivity.this, noRecordFound);
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
                    Toastmsg(VoterSearchActivity.this, noRecordFound);
                }
                return true;
            case R.id.age40:
                if (checkListSize() > 0) {
                    sortVoterWithAge(40);
                } else {
                    Toastmsg(VoterSearchActivity.this, noRecordFound);
                }
                return true;
            case R.id.age50:
                if (checkListSize() > 0) {
                    sortVoterWithAge(50);
                } else {
                    Toastmsg(VoterSearchActivity.this, noRecordFound);
                }
                return true;
            case R.id.agegreater50:
                if (checkListSize() > 0) {
                    sortVoterWithAge(51);
                } else {
                    Toastmsg(VoterSearchActivity.this, noRecordFound);
                }
                return true;
            case R.id.defaultData:
                if (checkListSize() > 0) {
                    set_adapter(list);
                } else {
                    Toastmsg(VoterSearchActivity.this, noRecordFound);
                }

                return true;
            case android.R.id.home:
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void clear_list() {

        list.clear();
        searchList.clear();
        adapter.notifyDataSetChanged();

        hideBoothSpinner();
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
        adapter = new SearchAdapter(this, list, VoterSearchActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));
    }

    @BindView(R.id.relation_details_recyclerview)
    RecyclerView relationRecylerview;

    @BindView(R.id.txt_no_relation_found)
    TextView txt_no_relation_found;

    @BindView(R.id.familyDetails)
    View familyDetailsLAyout;

    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    ArrayList<VoterDetails> relationList = new ArrayList<>();
    FamilyDetailsAdapter relationAdapter;

    private void setAdapterForRelationDetails() {

        set_Linearlayout_manager(relationRecylerview, this);

        relationAdapter = new FamilyDetailsAdapter(this, relationList, myDB, VoterSearchActivity.this);
        relationRecylerview.setAdapter(relationAdapter);
    }

    public void changeFamilyHead(int pos) {


        VoterDetails details = relationList.get(pos);

        selectVoterCardnumber = details.getVoterCardNumber();

        if (details.isFamilyHead()) {
            myDB.deleteFamilyHead(details.getVoterCardNumber());
            details.setFamilyHead(false);
            relationAdapter.notifyItemChanged(pos);
        } else {

            myDB.insertFamilyHead(details.getVoterCardNumber());
            details.setFamilyHead(true);
            relationAdapter.notifyItemChanged(pos);
        }
    }

    public void showFamilyDetails(VoterDetails details, int pos) {


        voterCardnumber = "";
        relationList.clear();

        toolbar.getMenu().clear();
        goneView(mainLayout);
        showView(familyDetailsLAyout);

        lastPosition = pos;

        voterCardnumber = details.getVoterCardNumber();

        myDB.insertFamilyDetails(voterCardnumber, voterCardnumber);

        getRealionValues(details.getHouseNo(), details.getAddressEnglish(), details.getVoterCardNumber());


        details.setFamilyHead(myDB.checkFamilyHead(details.getVoterCardNumber()));
        relationList.add(0, details);

        if (relationList.size() > 0) {


            goneView(txt_no_relation_found);
            if (relationAdapter == null) {
                setAdapterForRelationDetails();
            } else {
                relationAdapter.notifyDataSetChanged();
            }

        } else {

            showView(txt_no_relation_found);
        }

    }

    public void goBack() {
        if (familyDetailsLAyout.isShown()) {
            toolbar.inflateMenu(R.menu.voter_search_menu);

            MenuItem item = toolbar.getMenu().findItem(R.id.change);
            voterCardnumber = "";
            lastPosition = -1;
            item.setVisible(false);
            goneView(familyDetailsLAyout);
            showView(mainLayout);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    //get databse values
    public void getRealionValues(String houseno, String address, String votercardnumber) {
        VotersDatabase db = new VotersDatabase(this);
        Cursor c = db.get_relation_details(houseno, address, votercardnumber);
        c.moveToFirst();
        try {
            do {

                VoterDetails details = new VoterDetails(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(21) == null || c.getString(21).equals("false") ? "false" : c.getString(21), c.getInt(22), c.getString(23) + "", c.getInt(24) + "", c.getInt(25) + "");

                Log.w(TAG, "is family head :" + myDB.checkFamilyHead(details.getVoterCardNumber()));
                details.setFamilyHead(myDB.checkFamilyHead(details.getVoterCardNumber()));


                relationList.add(details);
            } while (c.moveToNext());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  private void setFilterAdapter() {
        adapter = new SearchAdapter(this, searchList, VoterSearchActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new dividerLineForRecyclerView(this));
    }
*/

}
