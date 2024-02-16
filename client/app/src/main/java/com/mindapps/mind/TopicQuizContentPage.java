package com.mindapps.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.mindapps.mind.data.SocketIO;
import com.mindapps.mind.dialogs.ErrorDialog;
import com.mindapps.mind.models.Quiz;
import com.mindapps.mind.models.Topic;
import com.mindapps.mind.models.User;

import java.util.ArrayList;
import java.util.List;

public class TopicQuizContentPage extends AppCompatActivity {

    TextView notificationBar;
    ErrorDialog errorDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_quiz_content_page);

        notificationBar = findViewById(R.id.notification);
        errorDialog = new ErrorDialog(this);
        SocketIO.setNotificationBar(notificationBar, errorDialog);

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
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }
}