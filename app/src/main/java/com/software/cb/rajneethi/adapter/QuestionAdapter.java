package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.CreateQuestionsActivity;
import com.software.cb.rajneethi.models.Questions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private Context context;
    private ArrayList<Questions> list;
    private CreateQuestionsActivity activity;


    public QuestionAdapter(Context context, ArrayList<Questions> list, CreateQuestionsActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public QuestionAdapter.QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_questions, viewGroup, false);
        return new QuestionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.QuestionViewHolder holder, int i) {

        Questions details = list.get(i);

        holder.txtTitle.setText(details.getName());
        holder.txtData.setText(details.getData());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtQuestionTitle)
        TextView txtTitle;
        @BindView(R.id.txtQuestionData)
        TextView txtData;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
