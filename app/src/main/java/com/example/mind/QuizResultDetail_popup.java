package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class QuizResultDetail_popup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_detail_popup);

        // Container of the Recycleview
        RecyclerView correctRecyclerView = findViewById(R.id.correct_items);
        RecyclerView incorrectRecyclerView = findViewById(R.id.incorrect_items);

        /*
        LibraryContentAdapter contentAdapter = new LibraryContentAdapter(this, new ArrayList<>(User.current.topics.values()));

        // RecycleView of Correct Items
        correctRecyclerView.setAdapter(contentAdapter);
        correctRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // RecycleView of Incorrect Items
        incorrectRecyclerView.setAdapter(contentAdapter);
        incorrectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        */

    }
}