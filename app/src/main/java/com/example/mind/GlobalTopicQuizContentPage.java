package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;

import java.util.ArrayList;
import java.util.List;

public class GlobalTopicQuizContentPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_topic_quiz_content_page);

        RecyclerView recyclerView = findViewById(R.id.quiz_content_id);
        TextView tv_title = findViewById(R.id.topic_title);

        String topicId = getIntent().getStringExtra("topicId");
        Topic topic = GlobalForum.allTopics.get(topicId);

        List<GlobalTopicQuizContentAdapter.QuizItem> quizItemList = new ArrayList<>();
        for (Quiz quiz : topic.quizzes.values())
            quizItemList.add(new GlobalTopicQuizContentAdapter.QuizItem(topic, quiz));

        tv_title.setText(topic.title);

        recyclerView.setAdapter(new GlobalTopicQuizContentAdapter(this, quizItemList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}