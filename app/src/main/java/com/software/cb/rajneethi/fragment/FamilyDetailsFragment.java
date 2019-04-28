package com.software.cb.rajneethi.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.VoterProfileActivity;
import com.software.cb.rajneethi.adapter.RelationDetailsAdapter;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.database.VotersDatabase;
import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.SurveyDetails;
import com.software.cb.rajneethi.models.VoterAttribute;
import com.software.cb.rajneethi.models.VoterDetails;
import com.software.cb.rajneethi.sharedpreference.SharedPreferenceManager;
import com.software.cb.rajneethi.utility.GPSTracker;
import com.software.cb.rajneethi.utility.dividerLineForRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by monika on 4/2/2017.
 */

public class FamilyDetailsFragment extends Fragment {


    @BindView(R.id.relation_details_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.txt_no_relation_found)
    TextView txt_no_relation_found;

    public boolean isUpdateRelationData = false;

    @BindString(R.string.pleaseSelectOne)
    String selectOne;
    @BindString(R.string.shifted)
    String shifted;
    @BindString(R.string.expired)
    String expired;
    @BindString(R.string.notAvailable)
    String notAvailable;
    @BindString(R.string.outofStation)
    String outOfStation;
    @BindString(R.string.notResponding)
    String notResponding;

    @BindString(R.string.familyHead)
    String familyHead;

    ArrayList<String> options = new ArrayList<>();

    public FamilyDetailsFragment() {
    }

    RelationDetailsAdapter adapter;
    private ArrayList<VoterDetails> list = new ArrayList<>();

    private String TAG = "Family Details";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_family_details, container, false);
        ButterKnife.bind(this, v);

      //  gpsTracker = new GPSTracker(getActivity());

        return v;
    }

    public void loadFamilyDetails() {
        list = ((VoterProfileActivity) getActivity()).list;
        if (list.size() > 0) {
            set_layoutmanager();
            set_adapter();
        } else {
            txt_no_relation_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void set_adapter() {

        options.clear();

        options.add(selectOne);
        options.add(familyHead);
        options.add(shifted);
        options.add(expired);
        options.add(notAvailable);
        options.add(outOfStation);
        options.add(notResponding);

        adapter = new RelationDetailsAdapter(getActivity(), list, FamilyDetailsFragment.this, options);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new dividerLineForRecyclerView(getActivity()));
    }

    public void updateRelationDetailsSurvey(int listPos, int selection, VoterDetails details) {

        String modification = "";
        switch (selection) {
            case 1:
                modification = "Shifted";
                break;
            case 2:
                modification = "Expired";
                break;
            case 3:
                modification = "Not Available";
                break;
            case 4:
                modification = "Out of Station";
                break;
            case 5:
                modification = "Not Responding";
                break;
            case 6:
                modification = "Family Head";
                break;
        }

        VoterDetails details1 = list.get(listPos);
        details1.setIs_updated("true");
        adapter.notifyDataSetChanged();

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        try {
            JSONObject survey_result = new JSONObject();
            //added for new 4 attribute
            String audioFilename = ((VoterProfileActivity) getActivity()).mFileName;
            survey_result.put("surveyDate", new Date().toString());
            survey_result.put("projectId", sharedPreferenceManager.get_project_id());
            survey_result.put("partyWorker", sharedPreferenceManager.get_user_id());
            survey_result.put("booth_name", details.getBoothName());
            survey_result.put("pwname", sharedPreferenceManager.get_username());
            survey_result.put("surveyType", "Field Survey");


            try {
                survey_result.put("audioFileName", audioFilename.substring(audioFilename.lastIndexOf("/") + 1, audioFilename.length()));
            } catch (Exception e) {
                e.printStackTrace();
                survey_result.put("audioFileName", "empty");

            }
            switch (modification) {
                case "Expired":
                    survey_result.put("Expired", "yes");
                    break;
                case "Shifted":
                    survey_result.put("Shifted", "yes");
                    break;
                case "Not Responding":
                    survey_result.put("Not Responding", "yes");
                    break;
                case "Out of Station":
                    survey_result.put("Out of Station", "yes");
                    break;
                case "Family Head":
                    survey_result.put("familyHead", modification);
                    break;
                default:
                    survey_result.put("Not Available", "yes");
                    break;
            }

            String latitude =((VoterProfileActivity)getActivity()).latitude == 0.0 ? "Not found " : ((VoterProfileActivity)getActivity()).latitude + "";
            String longitude =((VoterProfileActivity)getActivity()).longitude == 0.0 ? "Not found" : ((VoterProfileActivity)getActivity()).longitude + "";

            survey_result.put("latitude", latitude);
            survey_result.put("longitude", longitude);
            survey_result.put("userType", ((VoterProfileActivity) getActivity()).userType);
            survey_result.put("respondantname", details.getNameEnglish());
            survey_result.put("mobile", details.getMobile() == null ? "Not Available" : details.getMobile());
            String surveyId = DateFormat.format("yyyyMMdd_hhmmss", new Date()).toString() + "_" + sharedPreferenceManager.get_user_id();
            Log.w(TAG, "survey id " + surveyId);
            survey_result.put("surveyid", surveyId);

            Log.w(TAG, "data : " + survey_result.toString());

            VoterAttribute attribute = new VoterAttribute(details.getVoterCardNumber(), "Survey", survey_result.toString(), ((VoterProfileActivity) getActivity()).date, false, surveyId);

            VotersDatabase votersDatabase = new VotersDatabase(getActivity());
            BoothStats stats = new BoothStats(sharedPreferenceManager.get_project_id(),
                    details.getBoothName(), "Survey", ((VoterProfileActivity) getActivity()).userType);


            MyDatabase db = new MyDatabase(getActivity());
            db.insert_voter_attribute(attribute);
            db.insertBoothStats(stats);
            votersDatabase.update_survey(details.getVoterCardNumber());
            Toast.makeText(getActivity(), "Successfully update ", Toast.LENGTH_LONG).show();
            //  getActivity().finish();
            isUpdateRelationData = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.w(TAG, "modification : " + modification);

    }

    public void change_voter_details(VoterDetails details) {


        ((VoterProfileActivity) getActivity()).dialog.show();
        list.clear();
        adapter.notifyDataSetChanged();
        ((VoterProfileActivity) getActivity()).change_details(details);
        // ((VoterProfileActivity) getActivity()).get_db_values();
        list = ((VoterProfileActivity) getActivity()).list;
        adapter.notifyDataSetChanged();

        if (list.size() == 0) {
            txt_no_relation_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            if (txt_no_relation_found.isShown()) {
                txt_no_relation_found.setVisibility(View.GONE);
            }
            if (!recyclerView.isShown()) {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }


    }

    private void set_layoutmanager() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
