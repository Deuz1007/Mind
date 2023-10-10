package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

public class AnalyticsPage extends AppCompatActivity {

    int totalTopics;
    int totalQuizzes;
    int totalRetries;
    double average;
    double accuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_page);

        // Get total topics by topics size
        totalTopics = User.current.topics.size();

        // Initiate temporary values
        totalQuizzes = 0;
        totalRetries = 0;
        average = 0;

        // Traverse to each topic
        for (Topic topic : User.current.topics.values()) {
            // Increment the total quizzes by the quizzes size
            totalQuizzes += topic.quizzes.size();

            // Traverse to each quiz
            for (Quiz quiz : topic.quizzes.values()) {
                // Increment the values respectively
                totalRetries += quiz.retries;
                average += quiz.average;
            }
        }

        // The temporary value of average is the sum of each quiz's average
        // To get the average, divide the total average by the total quiz size
        average /= totalQuizzes;
        // To get the accuracy, divide the average by the total quiz size
        // You'll get the decimal value. Multiply it by 100 to get the percentage
        accuracy = average / totalQuizzes * 100;

        // Go back to Home Screen
        Button goBackToHomeScreen = findViewById(R.id.go_back_btn);
        goBackToHomeScreen.setOnClickListener(view -> onBackPressed());
    }
}