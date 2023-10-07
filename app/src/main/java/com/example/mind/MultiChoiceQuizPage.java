package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.mind.models.Question;
import com.example.mind.models.User;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;

import org.w3c.dom.Text;

import java.util.List;

public class MultiChoiceQuizPage extends AppCompatActivity {

    Button choices;
    TextView questions;

    private Topic topic;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        // Get topic from intent from library sheet
        String quizId = getIntent().getStringExtra("quizId");
        String topicId = getIntent().getStringExtra("topicId");

        topic = User.current.topics.get(topicId);
        quiz = topic.quizzes.get(quizId);


    }
}