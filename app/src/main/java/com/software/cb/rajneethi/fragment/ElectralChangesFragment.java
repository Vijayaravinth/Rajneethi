package com.software.cb.rajneethi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.AddVoterActivity;
import com.software.cb.rajneethi.activity.VoterProfileActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.VoterAttribute;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by monika on 4/2/2017.
 */

public class ElectralChangesFragment extends Fragment {

    public ElectralChangesFragment() {
    }

    @BindView(R.id.spinner_type_of_change)
    Spinner spinner_type_of_change;

    @BindView(R.id.cardview_deletion)
    CardView deletion_layout;

    @BindView(R.id.rg_deletion)
    RadioGroup rg_deletion;

    private String[] type_list = {
            "Please Selct one", "Inclusion", "Deletion", "Modification", "Transposition(Movement within Constituency)"
    };

    String type = "";
    private static final String TAG = "Electral Changes";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_electral_changes, container, false);
        ButterKnife.bind(this, v);

        set_adapter_for_spinner();
        onclick_for_spinner();
        radio_group_click_listener();
        return v;
    }

    public void radio_group_click_listener() {
        rg_deletion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio_shifted:
                        type = "Shifted";
                        break;
                    case R.id.radio_repeated:
                        type = "Repeated";
                        break;
                    case R.id.radio_other:
                        type = "Others";
                        break;
                    case R.id.radio_disqualified:
                        type = "Disqualified";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @OnClick(R.id.btn_deletion)
    public void delete() {

        if (!type.isEmpty()) {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = df.format(c.getTime());

            Log.w(TAG,"date "+formattedDate);
            String voter_card_number = ((VoterProfileActivity) getActivity()).votercardnumber;
            VoterAttribute attribute = new VoterAttribute(voter_card_number, "electoralchange_deletion", type, formattedDate, false);
            MyDatabase db = new MyDatabase(getActivity());
            db.insert_voter_attribute(attribute);
        } else {
            Toast.makeText(getActivity(), "Please Select type", Toast.LENGTH_LONG).show();
        }
    }

    private void onclick_for_spinner() {
        spinner_type_of_change.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    switch (position) {
                        case 1:
                            if (deletion_layout.isShown()) {
                                deletion_layout.setVisibility(View.GONE);
                            }
                            startActivity(new Intent(getActivity(), AddVoterActivity.class));
                            break;
                        case 2:
                            deletion_layout.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void set_adapter_for_spinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, type_list);
        spinner_type_of_change.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}
