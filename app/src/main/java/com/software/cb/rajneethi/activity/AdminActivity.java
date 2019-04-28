package com.software.cb.rajneethi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.MainMenuAdapter;
import com.software.cb.rajneethi.adapter.UploadBoothsAdapter;
import com.software.cb.rajneethi.models.MainMenuDetails;
import com.software.cb.rajneethi.models.UploadBoothDetails;
import com.software.cb.rajneethi.utility.UtilActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminActivity extends UtilActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.adminrecyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.addLayout)
    LinearLayout addLayout;

    LayoutInflater factory;

    private ArrayList<MainMenuDetails> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        setup_toolbar(toolbar, "Admin");

        set_grid_layout_manager(recyclerView, this, 3);

        list.add(new MainMenuDetails(R.drawable.create_project, "Create Project", false, 0));
        list.add(new MainMenuDetails(R.drawable.create_user, "Create Client", false, 0));
        list.add(new MainMenuDetails(R.drawable.upload_booths, "Upload Booths", false, 0));
        list.add(new MainMenuDetails(R.drawable.questions, "Upload Questions", false, 0));
        list.add(new MainMenuDetails(R.drawable.report_survey, "Survey Reports", false, 0));

        set_adapter();
    }


    private void set_adapter() {
        MainMenuAdapter adapter = new MainMenuAdapter(this, list, AdminActivity.this);
        recyclerView.setAdapter(adapter);
    }

    public void showForm(String title) {
        switch (title) {
            case "Create Project":
                initializeFactory();
                showCreateProjectView();
                break;
            case "Create Client":
                initializeFactory();
                showCreateClient();
                break;
            case "Upload Booths":

                initializeFactory();
                showUploadBooths();
                break;
            case "Upload Questions":

                startActivity(new Intent(this, CreateQuestionsActivity.class));
                break;
            case "Survey Reports":
                break;
            default:
                break;

        }
    }

    public void initializeFactory() {
        if (factory == null) {
            factory = LayoutInflater.from(this);
        }

        recyclerView.setVisibility(View.GONE);
    }


    public void showCreateProjectView() {

        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_create_project, null);
        addLayout.addView(myView);
    }

    public void showCreateClient() {
        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.activity_add_user, null);
        addLayout.addView(myView);
    }

    public void showUploadBooths() {
        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_upload_booth, null);

        Spinner projectSpinner = myView.findViewById(R.id.projectSpinner);

        String[] projects = new String[]{"Project 1", " Project 2", "Project 3"};
        set_adapter_for_spinner(projects, projectSpinner);

        RecyclerView boothRecyclerview = myView.findViewById(R.id.boothrecyclerview);
        set_Linearlayout_manager(boothRecyclerview, this);

        setAdapterForUploadBooths(boothRecyclerview);

        ImageView imgAddBooth = myView.findViewById(R.id.addBooth);

        final EditText edtBoothNo = myView.findViewById(R.id.edtBoothNumber);
        final EditText edtboothName = myView.findViewById(R.id.edtBoothName);

        imgAddBooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(edtboothName.getText().toString().trim().isEmpty() || edtBoothNo.getText().toString().trim().isEmpty())) {

                    boothUploadList.add(new UploadBoothDetails(edtBoothNo.getText().toString().trim(), edtboothName.getText().toString().trim(), true, false));

                    if (uploadBoothsAdapter == null) {
                        setAdapterForUploadBooths(boothRecyclerview);
                    } else {
                        uploadBoothsAdapter.notifyDataSetChanged();
                    }

                    edtboothName.getText().clear();
                    edtBoothNo.getText().clear();
                }
            }
        });

        addLayout.addView(myView);
    }

    UploadBoothsAdapter uploadBoothsAdapter;
    ArrayList<UploadBoothDetails> boothUploadList = new ArrayList<>();

    private void setAdapterForUploadBooths(RecyclerView recyclerView) {
        uploadBoothsAdapter = new UploadBoothsAdapter(this, boothUploadList, AdminActivity.this);
        recyclerView.setAdapter(uploadBoothsAdapter);
    }

    private void set_adapter_for_spinner(String[] options, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    @Override
    public void onBackPressed() {
        if (addLayout.isShown()) {
            recyclerView.setVisibility(View.VISIBLE);
            addLayout.removeAllViews();
            addLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
