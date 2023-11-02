package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mind.data.SocketIO;

public class GlobalQuizContentPage extends AppCompatActivity {

    TextView notificationBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_quiz_content_page);

        notificationBar = findViewById(R.id.notification);
        SocketIO.setNotificationBar(notificationBar);

        EditText et_content = findViewById(R.id.edit_content_field);
        Button btn_quizzes = findViewById(R.id.check_content_btn);

        String topicId = getIntent().getStringExtra("topicId");

        et_content.setText(GlobalForum.allTopics.get(topicId).content);
        btn_quizzes.setOnClickListener(v -> {
            Intent intent = new Intent(this, GlobalTopicQuizContentPage.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar);
    }
}