package com.mindapps.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mindapps.mind.data.ActiveQuiz;

import java.util.ArrayList;
import java.util.List;

public class QuizResultDetail_popup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_detail_popup);

        // Container of the RecycleView
        RecyclerView correctRecyclerView = findViewById(R.id.correct_items);
        RecyclerView incorrectRecyclerView = findViewById(R.id.incorrect_items);

        List<QuizResultAdapter.QuizItemInfo> correctItems = new ArrayList<>();
        List<QuizResultAdapter.QuizItemInfo> wrongItems = new ArrayList<>();

        for (QuizResultAdapter.QuizItemInfo item : ActiveQuiz.active.items)
            (item.answer.equals(item.response) ? correctItems : wrongItems).add(item);

        System.out.println("Correct: " + correctItems.size());
        System.out.println("Wrong: " + wrongItems.size());

        // RecycleView of Correct Items
        correctRecyclerView.setAdapter(new QuizResultAdapter(this, correctItems));
        correctRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // RecycleView of Incorrect Items
        incorrectRecyclerView.setAdapter(new QuizResultAdapter(this, wrongItems));
        incorrectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}