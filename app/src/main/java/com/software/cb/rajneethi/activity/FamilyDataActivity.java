package com.software.cb.rajneethi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.software.cb.rajneethi.BuildConfig;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.FamilyDataAdapter;
import com.software.cb.rajneethi.adapter.FamilyDataExpandAdapter;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.FamilyData;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.UtilActivity;
import com.software.cb.rajneethi.utility.VolleySingleton;
import com.software.cb.rajneethi.utility.checkInternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FamilyDataActivity extends UtilActivity {


    private String TAG = "Family Data";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private VotersDatabase myDatabase;

    String lastClickedItem = "", currentClickedItem = "";

    @BindView(R.id.txtData)
    TextView txtData;
    @BindView(R.id.dataRecyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.txtTotalVoters)
    TextView txtTotalVoters;

    @BindView(R.id.expandableList)
    ExpandableListView expandableListView;

    @BindView(R.id.txtTotalPhoneNumbers)
    TextView txtTotalPhone;
    @BindView(R.id.txtCasteWisePhoneNumber)
    TextView txtCasteWisePhone;
    @BindView(R.id.txtCasteWiseHouse)
    TextView txtCasteWiseHouse;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    @BindView(R.id.navigation_view)
    NavigationView navigationView;


    private ActionBarDrawerToggle mDrawerToggle;


    ArrayList<FamilyData> list = new ArrayList<>();

    long totalCount = -1;

    HashMap<String, Long> casteWisePhone = new HashMap<>();
    HashMap<String, Long> casteWiseHouse = new HashMap<>();
    HashMap<String, Long> hubliWiseHouse = new HashMap<>();
    HashMap<String, Long> percentageWiseVoter = new HashMap<>();

    FamilyDataAdapter adapter;
    FamilyDataExpandAdapter expandableAdapter;
    ArrayList<String> expandableListTitle = new ArrayList<>();
    HashMap<String, List<String>> expandableListDetail = new HashMap<>();

    public boolean ISINACTIONMODE = false;

    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_data);
        ButterKnife.bind(this);
        setup_toolbar(toolbar, "Family Data");

        myDatabase = new VotersDatabase(this);

        lastClickedItem = "totalVoters";
        currentClickedItem = lastClickedItem;

        initializeRemoteConfig();


        sharedPreferenceManager = new SharedPreferenceManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            txtTotalVoters.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_green));
        } else {

            txtTotalVoters.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button_green));
        }
        showCount();

        initializeDrawer();

        set_Linearlayout_manager(recyclerView, this);
        setAdapter();

        loadDataForExpandableList();


    }


    FirebaseRemoteConfig mFirebaseRemoteConfig;

    private void initializeRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()

                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);


        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.w(TAG, "Task failure :");
                        }
                        initialize();
                    }
                });
    }

    private void initialize() {

        String value = mFirebaseRemoteConfig.getString("p_" + sharedPreferenceManager.get_project_id());

        String[] list = value.split(",");
        if (list.length > 0) {
            String senderId = list[0];
            String userinfo = list[1];
            sharedPreferenceManager.setSenderId(senderId);
            sharedPreferenceManager.setSenderDetails(userinfo);
        }

    }

    public void inflateMenu() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.send_sms_menu);
    }

    private void clearMenu() {
        toolbar.getMenu().clear();
        showMenu();
    }

    private void showMenu() {
        toolbar.inflateMenu(R.menu.family_data_menu);
    }

    public ArrayList<String> selectedCasteList = new ArrayList<>();

    //select item
    public void selectCaste(int pos) {

        FamilyData data = list.get(pos);
        data.setSelected(true);
        selectedCasteList.add(data.getCasteName());

        adapter.notifyItemChanged(pos);
    }

    //remove item
    public void removeItem(int pos) {
        FamilyData data = list.get(pos);
        data.setSelected(false);
        selectedCasteList.remove(data.getCasteName());
        adapter.notifyItemChanged(pos);

        if (selectedCasteList.size() == 0) {
            ISINACTIONMODE = false;
            clearMenu();
        }
    }

    private void initializeDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //  show_child_details_in_drawer();
            }
        };

        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_reorder_white_24dp, getApplicationContext().getTheme());
        mDrawerToggle.setHomeAsUpIndicator(drawable);

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Log.w(TAG, "Drawer close");
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                    Log.w(TAG, "Drawer open");
                }
            }
        });
    }

    private void loadDataForExpandableList() {

        try {
            Cursor c = myDatabase.getAcName();


            if (c.moveToFirst()) {


                do {
                    expandableListTitle.add(c.getString(0));
                    Cursor data = myDatabase.getHubliName(c.getString(0));

                    ArrayList<String> itemList = new ArrayList<>();
                    if (data.moveToFirst()) {

                        do {

                            itemList.add(data.getString(0));
                        } while (data.moveToNext());

                    }
                    data.close();
                    expandableListDetail.put(c.getString(0), itemList);

                } while (c.moveToNext());

            }

            c.close();
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        if (expandableListTitle.size() > 0) {
            setAdapterForExpandableList();
        }
    }

    private void setAdapterForExpandableList() {

        expandableAdapter = new FamilyDataExpandAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableAdapter);

    }


    private String SELECTED_AC = "", SELECTED_HUBLI = "", SINGLEAC = "";

    private void setAdapter() {
        adapter = new FamilyDataAdapter(this, list, FamilyDataActivity.this);
        recyclerView.setAdapter(adapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                SINGLEAC = expandableListTitle.get(groupPosition);
                SELECTED_AC = expandableListTitle.get(groupPosition);

                ALLAC = "";
                SELECTED_HUBLI = "";


                switch (lastClickedItem) {
                    case "castePercentage":
                        String query2 = "select casteWiseHouse from unitTable where ac_name='" + SINGLEAC + "'";
                        showHubliWiseData(percentageWiseVoter, query2);
                        break;
                    case "casteWiseHouse":
                        String query = "select casteWiseHouse from unitTable where ac_name='" + SINGLEAC + "'";
                        showHubliWiseData(hubliWiseHouse, query);
                        break;
                    case "casteWisePhone":

                        Log.w(TAG, "caste wise phone woriking");
                        String query1 = "select casteWisePhone from unitTable where ac_name='" + SINGLEAC + "'";
                        showHubliWiseData(casteWisePhone, query1);
                        break;
                }

                Log.w(TAG, "SelectedAc " + SELECTED_AC);
            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                SELECTED_HUBLI = expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition);
                Log.w(TAG, "Selected hubli " + SELECTED_HUBLI);
                drawerLayout.closeDrawer(GravityCompat.START);
                if (!(SELECTED_HUBLI.isEmpty() && SELECTED_AC.isEmpty())) {

                    ALLAC = "";
                    SINGLEAC = "";

                    switch (lastClickedItem) {
                        case "castePercentage":
                            String query2 = "select casteWiseHouse from unitTable where ac_name='" + SELECTED_AC + "' and hubli_name='" + SELECTED_HUBLI + "'";
                            showHubliWiseData(percentageWiseVoter, query2);
                            break;
                        case "casteWiseHouse":
                            String query = "select casteWiseHouse from unitTable where ac_name='" + SELECTED_AC + "' and hubli_name='" + SELECTED_HUBLI + "'";
                            showHubliWiseData(hubliWiseHouse, query);
                            break;
                        case "casteWisePhone":
                            String query1 = "select casteWisePhone from unitTable where ac_name='" + SELECTED_AC + "' and hubli_name='" + SELECTED_HUBLI + "'";
                            showHubliWiseData(casteWisePhone, query1);
                            break;
                    }

                }

                return false;
            }
        });
    }

    public void showHubliWiseData(HashMap<String, Long> map, String query) {


        currentClickedItem = "";
        //changeColor(lastClickedItem);

        txtData.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        try {

            Cursor c = myDatabase.getHubliWiseCasteData(query);
            if (c.moveToFirst()) {

                map.clear();
                do {

                    JSONArray array = new JSONArray(c.getString(0));
                    addHouseData(array, map);
                } while (c.moveToNext());
            }


            if (lastClickedItem.equalsIgnoreCase("castePercentage")) {
                printValueForPercentage(map);
            } else {
                printValue(map);
            }

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.txtTotalVoters)
    public void totalVoters() {

        currentClickedItem = "totalVoters";

        changeColor(lastClickedItem);

        lastClickedItem = currentClickedItem;

        showCount();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            txtTotalVoters.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_green));
        } else {

            txtTotalVoters.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button_green));
        }
    }

    private void showCount() {

        txtData.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        View header = navigationView.getHeaderView(0);
        TextView txtPcName;
        if (totalCount == -1) {

            try {

                Cursor c = myDatabase.getCountForPC();

                if (c.moveToFirst()) {

                    txtPcName = (TextView) findViewById(R.id.txtPcName);
                    txtPcName.setText(c.getString(0));
                    totalCount = c.getLong(1);

                }

            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            String count = "Total Number of voters : " + totalCount;
            txtTotalVoters.setText(count);
        }
    }

    private void clearString() {
        ALLAC = "";
        SELECTED_AC = "";
        SINGLEAC = "";
        SELECTED_HUBLI = "";
    }

    @OnClick(R.id.txtTotalPhoneNumbers)
    public void totalPhoneNumbers() {
        currentClickedItem = "castePercentage";
        changeColor(lastClickedItem);
        // showCount();
        lastClickedItem = currentClickedItem;

        clearString();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            txtTotalPhone.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_green));
        } else {

            txtTotalPhone.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button_green));
        }


        percentageWiseVoter.clear();
        progressBar.setVisibility(View.VISIBLE);

        try {

            Cursor c = myDatabase.getCasteWiseHouseData();
            if (c.moveToFirst()) {
                do {

                    JSONArray array = new JSONArray(c.getString(0));
                    addHouseData(array, percentageWiseVoter);
                } while (c.moveToNext());
            }


            printValueForPercentage(percentageWiseVoter);

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @OnClick(R.id.txtCasteWiseHouse)
    public void casteWiseHouse() {
        currentClickedItem = "casteWiseHouse";
        changeColor(lastClickedItem);
        lastClickedItem = currentClickedItem;

        clearString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            txtCasteWiseHouse.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_green));
        } else {

            txtCasteWiseHouse.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button_green));
        }

        txtData.setVisibility(View.GONE);
        casteWiseHouse.clear();
        progressBar.setVisibility(View.VISIBLE);

        try {

            Cursor c = myDatabase.getCasteWiseHouseData();
            if (c.moveToFirst()) {
                do {

                    JSONArray array = new JSONArray(c.getString(0));
                    addHouseData(array, casteWiseHouse);
                } while (c.moveToNext());
            }


            printValue(casteWiseHouse);

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isPercentage = false;

    private void printValueForPercentage(HashMap<String, Long> map) {
        list.clear();
        ISINACTIONMODE = false;
        selectedCasteList.clear();
        clearMenu();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        isPercentage = true;


        Iterator myVeryOwnIterator = map.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            long value = map.get(key);

            //Log.w(TAG,"val "+ value);
            float val = 0;
            try {
                val = ((float) value * 100) / totalCount;
                //value = (value * 100) / totalCount;
            } catch (ArithmeticException e) {
                e.printStackTrace();
            }

            value = (long) val;

            list.add(new FamilyData(key, value, false, df.format(val) + "%", val));

        }

        Collections.sort(list, new PercentageComparator());

        if (adapter == null) {
            txtData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            setAdapter();
        } else {
            txtData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }

        progressBar.setVisibility(View.GONE);
    }

    private void printValue(HashMap<String, Long> map) {
        list.clear();
        ISINACTIONMODE = false;
        selectedCasteList.clear();
        clearMenu();


        Iterator myVeryOwnIterator = map.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            long value = map.get(key);

            list.add(new FamilyData(key, value, false));

        }


        Collections.sort(list, new QuestionOrderComparator());

        if (adapter == null) {
            txtData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            setAdapter();
        } else {
            txtData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }

        progressBar.setVisibility(View.GONE);
    }

    long totalPeople = 0;

    private void addHouseData(JSONArray array, HashMap<String, Long> map) {

        totalPeople = 0;
        Log.w(TAG, "Array length " + array.length());
        for (int i = 0; i <= array.length() - 1; i++) {

            try {
                JSONObject object = array.getJSONObject(i);


                if (map.get(object.getString("caste_name")) == null) {

                    map.put(object.getString("caste_name"), object.getLong("count"));
                } else {

                    long count = map.get(object.getString("caste_name"));
                    count += object.getLong("count");
                    map.put(object.getString("caste_name"), count);
                }

                totalPeople = map.get(object.getString("caste_name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.txtCasteWisePhoneNumber)
    public void casteWisePhone() {
        currentClickedItem = "casteWisePhone";

        changeColor(lastClickedItem);

        lastClickedItem = currentClickedItem;

        clearString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            txtCasteWisePhone.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_green));
        } else {

            txtCasteWisePhone.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button_green));
        }

        casteWisePhone.clear();
        try {

            Cursor c = myDatabase.getCasteWisePhoneData();
            if (c.moveToFirst()) {
                do {

                    JSONArray array = new JSONArray(c.getString(0));
                    addHouseData(array, casteWisePhone);
                } while (c.moveToNext());
            }


            printValue(casteWisePhone);

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void enableDisableSelectedItem(boolean val) {
        for (FamilyData family : list) {

            if (val) {
                if (!family.isSelected()) {
                    family.setSelected(val);
                    selectedCasteList.add(family.getCasteName());
                }
            } else {
                family.setSelected(val);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void changeColor(String clickedItem) {

        Log.w(TAG, "clicked item : " + clickedItem + " ast clicked " + lastClickedItem);

        if (!currentClickedItem.equalsIgnoreCase(lastClickedItem)) {
            switch (lastClickedItem) {
                case "totalVoters":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        txtTotalVoters.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
                    } else {

                        txtTotalVoters.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button));
                    }
                    break;
                case "castePercentage":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        txtTotalPhone.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
                    } else {

                        txtTotalPhone.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button));
                    }
                    isPercentage = false;
                    break;
                case "casteWiseHouse":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        txtCasteWiseHouse.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
                    } else {

                        txtCasteWiseHouse.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button));
                    }

                    break;
                case "casteWisePhone":

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        txtCasteWisePhone.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
                    } else {

                        txtCasteWisePhone.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_button));
                    }
                    break;
            }
        }

    }

    String[] items;

    String ALLAC = "";

    public void showAlert() {
        //    final CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};
        // arraylist to keep the selected items
        final ArrayList seletedItems = new ArrayList();


        items = new String[expandableListTitle.size()];


        for (int i = 0; i <= expandableListTitle.size() - 1; i++) {
            items[i] = expandableListTitle.get(i);
        }

        Log.w(TAG, "size :" + items.length);

        AlertDialog.Builder builder = new AlertDialog.Builder(FamilyDataActivity.this)
                .setTitle("Select AC")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (seletedItems.size() < 2) {

                            if (isChecked) {
                                seletedItems.add(items[which]);

                            } else {
                                seletedItems.remove(items[which]);
                            }
                        } else {
                            Toastmsg(FamilyDataActivity.this, "You can select only 2 ac at a time");
                            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                            seletedItems.remove(items[which]);
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ALLAC = "";
                        if (seletedItems.size() > 0) {

                            for (int i = 0; i <= seletedItems.size() - 1; i++) {

                                if (i == seletedItems.size() - 1) {
                                    ALLAC += "'" + seletedItems.get(i) + "'";
                                } else {
                                    ALLAC += "'" + seletedItems.get(i) + "',";
                                }
                            }

                        }
                        selectedCasteList.clear();
                        SELECTED_AC = "";
                        SELECTED_HUBLI = "";
                        SINGLEAC = "";
                        startActivity(new Intent(FamilyDataActivity.this, SendSmsActivity.class).putExtra("singleac",SINGLEAC).putExtra("balance", balance).putExtra("allac", ALLAC).putExtra("selectedac", SELECTED_AC).putExtra("selectedhubli", SELECTED_HUBLI).putStringArrayListExtra("list", selectedCasteList));


                        Log.w(TAG, "all ac " + ALLAC);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.family_data_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.send_sms:

                if (selectedCasteList.size() > 0) {
                    clearMenu();
                    ALLAC = "";
                    startActivity(new Intent(FamilyDataActivity.this, SendSmsActivity.class).putExtra("singleac",SINGLEAC).putExtra("balance", balance).putExtra("allac", ALLAC).putExtra("selectedac", SELECTED_AC).putExtra("selectedhubli", SELECTED_HUBLI).putStringArrayListExtra("list", selectedCasteList));
                    enableDisableSelectedItem(false);
                    ISINACTIONMODE = false;
                }
                return true;

            case R.id.logout:
                sharedPreferenceManager.set_keep_login(false);
                sharedPreferenceManager.set_keep_login_role("");
                startActivity(new Intent(FamilyDataActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                FamilyDataActivity.this.finish();
                return true;

            case R.id.download:

                startActivity(new Intent(FamilyDataActivity.this, DownloadActivity.class).putExtra("userId", sharedPreferenceManager.get_user_id())
                        .putExtra("userType", sharedPreferenceManager.getUserType())
                        .putExtra("role", sharedPreferenceManager.get_keep_login_role())
                        .putExtra("cid", sharedPreferenceManager.get_constituency_id()).putExtra("project_id", sharedPreferenceManager.get_project_id())
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                FamilyDataActivity.this.finish();

                return true;

            case R.id.selectAll:
                enableDisableSelectedItem(true);
                return true;

            case R.id.unSelectAll:
                selectedCasteList.clear();
                enableDisableSelectedItem(false);
                return true;

            case R.id.acwise:

                showAlert();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkInternet.isConnected()) {
            if (!sharedPreferenceManager.getSenderDetails().equalsIgnoreCase("empty")) {
                getBalanceInfo();

            }
        } else {
            Toastmsg(FamilyDataActivity.this, "can't get your balance");
        }

    }

    private long balance = -1;

    private void getBalanceInfo() {
        StringRequest request = new StringRequest(Request.Method.POST, BuildConfig.BALANCEAPI + sharedPreferenceManager.getSenderDetails(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String bal = response.substring(response.lastIndexOf("-") + 1, response.length());
                Log.w(TAG, "response :" + response.substring(response.lastIndexOf("-") + 1, response.length()));
                toolbar.setTitle("Message Balance : " + bal);


                try {
                    balance = Long.parseLong(bal);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error :" + error.toString());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private class QuestionOrderComparator implements Comparator<FamilyData> {

        @Override
        public int compare(FamilyData lhs, FamilyData rhs) {
            if (lhs == null || rhs == null)
                return 0;
            if (lhs.getCount() == 0 || rhs.getCount() == 0)
                return 0;

            try {
                Long lhsCount = lhs.getCount();
                Long rhsCount = rhs.getCount();
                return (lhsCount < rhsCount) ? 1 : -1;
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private class PercentageComparator implements Comparator<FamilyData> {
        @Override
        public int compare(FamilyData lhs, FamilyData rhs) {
            if (lhs == null || rhs == null)
                return 0;
            if (lhs.getCalPer() == 0 || rhs.getCalPer() == 0)
                return 0;

            try {
                float lhsCount = lhs.getCalPer();
                float rhsCount = rhs.getCalPer();
                return (lhsCount < rhsCount) ? 1 : -1;
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
