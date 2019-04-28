package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.utility.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mvijayar on 06-07-2017.
 */

public class PreviewActivity extends Util {


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.btn_save)
    Button btn_save;

    @BindView(R.id.linear_layout)
    LinearLayout layout;

    @BindString(R.string.preview)
    String preview;

    private String TAG = "Preview";
    private MyDatabase db;

    VoterAttribute voterAttribute;
    private ArrayList<Questions> list = new ArrayList<>();
    ArrayList<TextView> textViewArrayList = new ArrayList<>();
    ArrayList<EditText> editTextArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        db = new MyDatabase(this);
        setup_toolbar_with_back(toolbar,preview);

       // Log.w(TAG, "Survey here" + voterAttribute.getAttributeValue());

        if (getIntent().getExtras() != null) {

            voterAttribute = (VoterAttribute) getIntent().getSerializableExtra("survey");
            Log.w(TAG, "Survey76 " + voterAttribute.getAttributeValue());
        }

        JSONObject obj = null;
        try {

            /*Cursor c = db.get_question();
            if (c.moveToFirst()) {
                do {
                    Questions questions = new Questions(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
                    list.add(questions);
                    Log.w(TAG, "question size" + list.size());
                } while (c.moveToNext());
            }

            for (int i = 0; i <= list.size() - 1; i++) {
                Questions questions = list.get(i);
                String value1 = obj.getString(object.getString("title"));
                JSONObject object = new JSONObject(questions.getData());
                TextView textView = create_textview(object.getString("title"));
                textViewArrayList.add(textView);
                layout.addView(textView);
            }*/


//            obj = new JSONObject(voterAttribute.getAttributeValue_hardcode_qus());
            obj = new JSONObject(voterAttribute.getAttributeValue());
            Log.w(TAG, "Survey 103 " + voterAttribute.getAttributeValue());
            Iterator<String> iter = obj.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = obj.getString(key);
               /* if (!(key.equals("projectId") || key.equals("surveyDate") || key.equals("longitude") || key.equals("BOOTH") || key.equals("latitude"))) {
                    {*/
                    TextView textView = create_textview(key);
                    EditText editText = create_edittext(value);
                    textViewArrayList.add(textView);
                    editTextArrayList.add(editText);

                    layout.addView(textView);
                    layout.addView(editText);

                    // }

                } catch (JSONException e) {
                    // Something went wrong!
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @BindString(R.string.successfullyAdded)
    String successfullyAdded;
    @OnClick(R.id.btn_save)
    public void save() {

        try {
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            for (int i = 0; i <= textViewArrayList.size() - 1; i++) {

                TextView txtview = textViewArrayList.get(i);
                EditText editText = editTextArrayList.get(i);
                object.put(txtview.getText().toString().trim(), editText.getText().toString().trim());

            }

            //array.put(voterAttribute.getAttributeValue());
            array.put(object);
            Log.w(TAG, "sandeep149" +object);
            voterAttribute.setAttributeValue(array.toString());

            Log.w(TAG, "Voter attribute value while saving" + voterAttribute.getAttributeValue());

            MyDatabase db = new MyDatabase(PreviewActivity.this);
            VotersDatabase votersDatabase = new VotersDatabase(PreviewActivity.this);
            db.insert_voter_attribute(voterAttribute);
            votersDatabase.update_survey(voterAttribute.getVoterCardNumber());

            Toastmsg(PreviewActivity.this, successfullyAdded);
            PreviewActivity.super.onBackPressed();
        } catch (JSONException e) {
            e.printStackTrace();
            Toastmsg(PreviewActivity.this, "Json exception");

        }
    }


    private TextView create_textview(String question) {
        TextView textView = new TextView(PreviewActivity.this);
        textView.setText(question);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        return textView;
    }

    private EditText create_edittext(String value) {
        EditText editText = new EditText(PreviewActivity.this);
        editText.setText(value);
        return editText;
    }
}
