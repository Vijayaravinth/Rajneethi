package com.software.cb.rajneethi.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.adapter.QuestionAdapter;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.utility.UtilActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateQuestionsActivity extends UtilActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar progressbar;

    @BindView(R.id.fundRecyclerview)
    RecyclerView recyclerView;


    @BindView(R.id.addLayout)
    LinearLayout addLayout;

    LayoutInflater factory;

    QuestionAdapter adapter;


    ArrayList<Questions> qList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_managment);
        ButterKnife.bind(this);

        setup_toolbar_with_back(toolbar, "Questions");

        set_Linearlayout_manager(recyclerView, this);

        setAdapter();

        addData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fund_grievance_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initializeFactory() {
        if (factory == null) {
            factory = LayoutInflater.from(this);
        }
    }

    private void showCreateQuestions() {


        addLayout.removeAllViews();
        addLayout.setVisibility(View.VISIBLE);
        final View myView = factory.inflate(R.layout.layout_create_question, null);


        addLayout.addView(myView);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                initializeFactory();
                showCreateQuestions();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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

    private void setAdapter() {
        adapter = new QuestionAdapter(this, qList, CreateQuestionsActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private void addData() {
        qList.add(new Questions("3", "Name", "Data"));
        qList.add(new Questions("3", "Age", "Data"));
        qList.add(new Questions("3", "Gender", "Data"));
        qList.add(new Questions("3", "Affiliation", "Data"));
        qList.add(new Questions("3", "Occupation", "Data"));
        qList.add(new Questions("3", "Education", "Data"));

        adapter.notifyDataSetChanged();
    }
}
