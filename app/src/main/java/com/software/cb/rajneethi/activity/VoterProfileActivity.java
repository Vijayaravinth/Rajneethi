package com.software.cb.rajneethi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.fragment.ElectralChangesFragment;
import com.software.cb.rajneethi.fragment.FamilyDetailsFragment;
import com.software.cb.rajneethi.fragment.FieldSurveyFragment;
import com.software.cb.rajneethi.fragment.VoterDetailsFragment;
import com.software.cb.rajneethi.models.SelectedContactDetails;
import com.software.cb.rajneethi.models.VoterDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

/**
 * Created by monika on 4/2/2017.
 */

public class VoterProfileActivity extends Util implements LocationListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    public String mFileName = null;
    private MediaRecorder mRecorder = null;

    private boolean isRecording = false;


    public IOSDialog dialog;


    private final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 123;

    private static LocationManager locationManager;
    android.location.Location location; // location
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 0 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 60000;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;

    ElectralChangesFragment electralChangesFragment;
    FamilyDetailsFragment familyDetailsFragment;
    FieldSurveyFragment fieldSurveyFragment;
    VoterDetailsFragment voterDetailsFragment;

    public double latitude, longitude;

    public String name, gender, age, houseno, votercardnumber, mobile, serailno, address, relation, addressregional, hierarchy_hash, boothName, wardName;
    private static final String TAG = "VoterProfile";

    public ArrayList<VoterDetails> list = new ArrayList<>();

    public String date, userType ,isFamilyHead = "no";
    public boolean is_survey_taken, bskMode, isQusLoaded = false, isFamilyDetailsLoaded = false;

    @BindString(R.string.voterProfile)
    String toolbarTitle;

    File newDir;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY

            };
    public ArrayList<String> contact_list = new ArrayList<>();

    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_profile);
        ButterKnife.bind(this);

        setup_toolbar(toolbar);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        getLocation();
        //  getSupportLoaderManager().initLoader(0, null, this);

        date = get_current_date();
        if (getIntent().getExtras() != null) {
            name = getIntent().getExtras().getString("name");
            gender = getIntent().getExtras().getString("gender");
            age = getIntent().getExtras().getString("age");
            houseno = getIntent().getExtras().getString("houseNumber");
            votercardnumber = getIntent().getExtras().getString("voterCardNumber");
            serailno = getIntent().getExtras().getString("serialNumber");
            address = getIntent().getExtras().getString("address");
            relation = getIntent().getExtras().getString("relationship");
            addressregional = getIntent().getExtras().getString("addressRegional");
            is_survey_taken = getIntent().getExtras().getBoolean("is_survey_taken");
            userType = getIntent().getExtras().getString("userType");
            boothName = getIntent().getExtras().getString("boothName");
            mobile = getIntent().getExtras().getString("mobile").equals("0") || getIntent().getExtras().getString("mobile") == null || getIntent().getExtras().getString("mobile").isEmpty() || getIntent().getExtras().getString("mobile").isEmpty() ? "N/A" : getIntent().getExtras().getString("mobile");
            Log.w(TAG, "mobile number : " + mobile);
            wardName = getIntent().getExtras().getString("wardName") == null || getIntent().getExtras().getString("wardName").isEmpty() ? "N/A" : getIntent().getExtras().getString("wardName");
            Log.w(TAG, "is survey taken :" + is_survey_taken);
            bskMode = getIntent().getExtras().getBoolean("bskMode");
        }

        newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        get_db_values();

        toolbar.setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 1) {

                    if (!isQusLoaded) {
                        fieldSurveyFragment.get_questions();
                        fieldSurveyFragment.renderDynamicLayout();
                        isQusLoaded = true;
                    }

                } else if (position == 2) {

                    if (!isFamilyDetailsLoaded) {
                        familyDetailsFragment.loadFamilyDetails();
                        isFamilyDetailsLoaded = true;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dialog = show_dialog(this, false);

        onRecord(false);
        onRecord(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!familyDetailsFragment.isUpdateRelationData) {
            Log.w(TAG, "Family details not updated");
            delete_audio_file();
        }
    }

    //start or stop record
    public void onRecord(boolean start) {
        Log.w(TAG, "Inside on record");
        if (start) {
            startRecording();
            isRecording = true;
        } else {
            stopRecording();
            isRecording = false;
        }
    }

    //start recording
    private void startRecording() {
        Log.w(TAG, "Start recording");
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/";
        mFileName += "AUD-" + sharedPreferenceManager.get_username() + "_" + boothName + "_" + votercardnumber + "_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        try {
            mRecorder.start();
        } catch (Exception e) {
           e.printStackTrace();
        }


    }

    //stop recording
    private void stopRecording() {

        Log.w(TAG, "Stop recording method");
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
        }
    }


    public void go_back() {
        super.onBackPressed();
        VoterProfileActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRecord(false);
    }

    //get databse values
    public void get_db_values() {
        VotersDatabase db = new VotersDatabase(this);
        Cursor c = db.get_relation_details(houseno, address, votercardnumber);
        c.moveToFirst();
        try {
            do {
                Log.w(TAG, "SOS value :" + c.getInt(22));
                VoterDetails details = new VoterDetails(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(21), c.getInt(22), c.getString(23) + "", c.getInt(24) + "", c.getInt(25) + "");
                list.add(details);
            } while (c.moveToNext());


        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.w(TAG, "Address " + address + " house no " + houseno + " address regioanl " + addressregional);
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Assign like this
        voterDetailsFragment = new VoterDetailsFragment();
        familyDetailsFragment = new FamilyDetailsFragment();
        fieldSurveyFragment = new FieldSurveyFragment();
        //electralChangesFragment = new ElectralChangesFragment();

        adapter.addFragment(voterDetailsFragment, voterDetails);
        adapter.addFragment(fieldSurveyFragment, filedSurvey);
        adapter.addFragment(familyDetailsFragment, familyDetails);

        //adapter.addFragment(electralChangesFragment, electoralChanges);

        viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @BindString(R.string.electoralhanges)
    String electoralChanges;
    @BindString(R.string.voterDetails)
    String voterDetails;
    @BindString(R.string.fieldSurvey)
    String filedSurvey;
    @BindString(R.string.familyDetails)
    String familyDetails;

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();


        ViewPagerAdapter(android.support.v4.app.FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fieldSurveyFragment.onActivityResult(requestCode, resultCode, data);
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

    public void change_details(VoterDetails details) {
        onRecord(false);
        delete_audio_file();
        onRecord(true);

        votercardnumber = details.getVoterCardNumber();
        name = details.getNameEnglish();
        list.clear();
        is_survey_taken = false;
        voterDetailsFragment.change_voter_details(details);
        viewPager.setCurrentItem(0);

        get_db_values();
    }

    /*get booth details*/
    public String get_booth_details() {
        return voterDetailsFragment.booth_details.isEmpty() ? "Not found" : voterDetailsFragment.booth_details;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Until you grant the permission, you cannot get a location", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voter_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                if (!voterDetailsFragment.mobile.equals("N/A") || voterDetailsFragment.mobile != null) {
                    String shareContent = "Name : " + name + "\nAge :" + age + "\nVoter Card Number : " + votercardnumber + "\nSerial No :" + serailno + "\nBooth Number:" + boothName + "\nWard Name :" + wardName;
                    shareData(shareContent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void shareData(String shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.putExtra(Intent.ACTION_SENDTO, mobile);
        intent.setType("text/plain");
        startActivity(intent);
    }


    public void getLocation() {
        try {

         //   getLocationLayout.setVisibility(View.VISIBLE);
            Log.w(TAG, "inside get location called ");
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                Log.w(TAG, "GPS Enabled");
                canGetLocation = true;
                isGPSEnabled = true;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return ;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.w(TAG, " Latitude " + latitude + " Longitude " + longitude);
                    }
                }

            } else {
                Toastmsg(VoterProfileActivity.this, "please Enable gps");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
