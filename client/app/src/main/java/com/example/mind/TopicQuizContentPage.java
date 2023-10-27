package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mind.data.SocketIO;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.ArrayList;
import java.util.List;

public class TopicQuizContentPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_quiz_content_page);

        // Get topic from intent from Quiz Content
        String topicId = getIntent().getStringExtra("topicId");
        Topic topic = User.current.topics.get(topicId);

        List<TopicQuizContentAdapter.QuizItem> quizItems = new ArrayList<>();
        for (Quiz quiz : topic.quizzes.values())
            quizItems.add(new TopicQuizContentAdapter.QuizItem(topic, quiz));

        // Get the title of the topic
        TextView title = findViewById(R.id.topic_title);
        title.setText(topic.title);

        // Container of the Recycleview
        RecyclerView recyclerView = findViewById(R.id.quiz_content_id);

        TopicQuizContentAdapter contentAdapter = new TopicQuizContentAdapter(this, quizItems);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.currentActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.currentActivity = this;
    }
}