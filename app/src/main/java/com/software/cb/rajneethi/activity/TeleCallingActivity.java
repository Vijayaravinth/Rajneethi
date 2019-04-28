package com.software.cb.rajneethi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.Constants;
import com.software.cb.rajneethi.utility.UtilActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeleCallingActivity extends UtilActivity {

    @BindString(R.string.questionare)
    String toolbarTitle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String boothName, name, mobile;


    @BindView(R.id.s1)
    Spinner s1;
    @BindView(R.id.s2)
    Spinner s2;
    @BindView(R.id.s3)
    Spinner s3;
    @BindView(R.id.s4a)
    Spinner s4a;
    @BindView(R.id.s4b)
    Spinner s4b;
    @BindView(R.id.s5)
    Spinner s5;
    @BindView(R.id.s6)
    Spinner s6;

    @BindArray(R.array.topt1)
    String[] opt1;
    @BindArray(R.array.topt2)
    String[] opt2;
    @BindArray(R.array.topt3)
    String[] opt3;
    @BindArray(R.array.topt6)
    String[] opt6;

    private String mFileName = null;
    private MediaRecorder mRecorder = null;
    boolean isRecording = false;

    String qus1[] = new String[]{"NCP + INC", "BJP", "SHS", "AIMIM", "Independent"};
    String qus2[] = new String[]{"Smt. Chayatai Shantaram Gore (BJP)", "Sau. Nilam Santosh Gore (BJP)", "Sau. Sunita Machindra Shinde (BJP)", "Sau. Anita Shinde (BJP)", "Sau. Shubhangi Manhor Pote (NCP + INC)", "Sau. Archana Raju Gore", "Sau. Supriya Gorakh Aalekar"};
    String qus3[] = new String[]{"Yes", "No", "Can't Say"};
    String qus6[] = new String[]{"NCP + INC", "BJP"};

    private String TAG = "telecalling";
    SharedPreferenceManager sharedPreferenceManager;

    String ans1 = "", ans2 = "", ans3 = "", ans4 = "", ans5 = "", ans6 = "", ans7 = "";

    JSONObject object = new JSONObject();

    @BindString(R.string.successfullyAdded)
    String successfullyAdded;

    @BindView(R.id.rgResponse)
    RadioGroup rgResponse;

    @BindView(R.id.qusLayout)
    LinearLayout qusLayout;

    //make dir
    public void makeDir() {
        File newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telecalling);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, toolbarTitle);

        sharedPreferenceManager = new SharedPreferenceManager(this);

        if (getIntent().getExtras() != null) {
            Bundle args = getIntent().getExtras();
            boothName = args.getString("boothName");
            name = args.getString("name");
            mobile = args.getString("mobile");
        }

        if (!mobile.isEmpty() && !mobile.equalsIgnoreCase("Empty")) {

            call();

        }

        try {
            object.put("respondantname", name);
            object.put("mobile", mobile);
            object.put("party support", ans1);
            object.put("president", ans2);
            object.put("presidental candidate", ans3);
            object.put("choose party", ans4);
            object.put("candidate support", ans5);
            object.put("joins another party", ans6);
            object.put("party name", ans7);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        makeDir();
        setAdapterForSpinner(s1, opt1);
        setAdapterForSpinner(s2, opt2);
        setAdapterForSpinner(s3, opt3);
        setAdapterForSpinner(s4a, opt1);
        setAdapterForSpinner(s4b, opt2);
        setAdapterForSpinner(s5, opt3);
        setAdapterForSpinner(s6, opt6);

        spinnerLitener();

        rgResponse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbResponding:
                        onRecord(false);
                        deleteAudioFile();
                        onRecord(true);
                        qusLayout.setVisibility(View.VISIBLE);
                        rgResponse.setVisibility(View.GONE);
                        break;
                    case R.id.rbNotinterested:
                        MyDatabase db = new MyDatabase(TeleCallingActivity.this);
                        db.insertTeleCalling(mobile, "failed");
                        goBack();
                        break;
                }
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void call() {
        if (checkPermissionGranted(Constants.CALL, this)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile.trim()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } else {
            askPermission(new String[]{Constants.CALL});
        }
    }


    @OnClick(R.id.btnSubmit)
    public void submit() {
        if (!(ans1.isEmpty() && ans2.isEmpty() && ans3.isEmpty() && ans4.isEmpty() && ans5.isEmpty() && ans6.isEmpty() && ans7.isEmpty())) {

            try {
                object.put("party support", ans1);
                object.put("president", ans2);
                object.put("presidental candidate", ans3);
                object.put("choose party", ans4);
                object.put("candidate support", ans5);
                object.put("joins another party", ans6);
                object.put("party name", ans7);

                String surveyId = DateFormat.format("yyyyMMdd_hhmmss a", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
                object.put("surveyType", "TeleCalling");
                object.put("userType", sharedPreferenceManager.getUserType());
                //added for new 4 attribute
                object.put("surveyDate", new Date().toString());
                object.put("projectId", sharedPreferenceManager.get_project_id());
                object.put("partyWorker", sharedPreferenceManager.get_user_id());
                object.put("pwname", sharedPreferenceManager.get_username());

                // object.put("booth", sharedPreferenceManager.get_survey_booth());
                object.put("audioFileName", mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));
                object.put("latitude", "Not Available");
                object.put("longitude", "Not Available");
                object.put("booth_name", boothName);
                object.put("surveyid", surveyId);

                MyDatabase db = new MyDatabase(TeleCallingActivity.this);

                Log.w(TAG, "object " + object.toString());

                VoterAttribute attribute = new VoterAttribute("newVoter", "Survey", object.toString(), get_current_date(), false, surveyId);
                BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                        boothName, "Questionnaire", sharedPreferenceManager.getUserType());

                db.insertBoothStats(stats);
                //VotersDatabase votersDatabase = new VotersDatabase(AddVoterActivity.this);
                db.insert_voter_attribute(attribute);
                db.insertTeleCalling(mobile, "success");
                //  votersDatabase.update_survey(attribute.getVoterCardNumber());

                Toastmsg(TeleCallingActivity.this, successfullyAdded);
                TeleCallingActivity.super.onBackPressed();
                TeleCallingActivity.this.finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toastmsg(TeleCallingActivity.this, "All fields are required");
        }
    }

    private void spinnerLitener() {


        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ans1 = qus1[position - 1];
                } else {
                    ans1 = "";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ans2 = qus2[position - 1];
                } else {
                    ans2 = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ans3 = qus3[position - 1];
                } else {
                    ans3 = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s4a.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ans4 = qus1[position - 1];
                } else {
                    ans4 = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s4b.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ans5 = qus2[position - 1];
                } else {
                    ans5 = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ans6 = qus3[position - 1];
                } else {
                    ans6 = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ans7 = qus6[position - 1];
                } else {
                    ans7 = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    //start or stop record
    private void onRecord(boolean start) {
        Log.w(TAG, "Inside on record");
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    //start recording
    private void startRecording() {

        Log.w(TAG, "Start recording");
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/";
        mFileName += "AUD-" + sharedPreferenceManager.get_username() + "_" + boothName + "_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".mp3";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //  start_timer();

        try {
            mRecorder.prepare();
            //  t.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        try {
            isRecording = true;
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

            isRecording = false;


        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
        }

    }


    private void setAdapterForSpinner(Spinner spinner, String[] options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_theatre, R.id.txtSpinnerText, options);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_text_theatre);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void goBack() {
        deleteAudioFile();
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            onRecord(false);
        }
    }

    /*delete audio file*/
    private void deleteAudioFile() {
        try {

            File newDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + "/compressimage");
            if (mFileName != null) {
                File file = new File(newDir, mFileName.substring(mFileName.lastIndexOf("/") + 1, mFileName.length()));

                Log.w(TAG, "File path : " + file.getAbsolutePath());

                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
