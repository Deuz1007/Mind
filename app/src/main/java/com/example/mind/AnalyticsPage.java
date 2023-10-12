package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.text.DecimalFormat;

public class AnalyticsPage extends AppCompatActivity {

    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    int totalTopics;
    int totalQuizzes;
    int totalRetries;
    double average;
    double accuracy;

    final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_page);

        // BGM
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.bgm1);
        backgroundMusicPlayer.start();

        TextView tv_username = findViewById(R.id.display_username);
        TextView tv_email = findViewById(R.id.display_email);
        TextView tv_topics = findViewById(R.id.total_generated_topics);
        TextView tv_quizzes = findViewById(R.id.total_generated_quiz);
        TextView tv_retries = findViewById(R.id.total_retry);
        TextView tv_average = findViewById(R.id.total_average_score);
        TextView tv_accuracy = findViewById(R.id.answers_accuracy);

        calculateAnalytics();

        tv_username.setText(User.current.username);
        tv_email.setText(User.current.email);
        tv_topics.setText(totalTopics + "");
        tv_quizzes.setText(totalQuizzes + "");
        tv_retries.setText(totalRetries + "");
        tv_average.setText(decimalFormat.format(average));
        tv_accuracy.setText(decimalFormat.format(accuracy) + "%");

        // Go back to Home Screen
        Button goBackToHomeScreen = findViewById(R.id.go_back_btn);
        goBackToHomeScreen.setOnClickListener(view -> onBackPressed());
    }

    private void calculateAnalytics() {
        // Get total topics by topics size
        totalTopics = User.current.topics.size();

        // Initiate temporary values
        totalQuizzes = 0;
        totalRetries = 0;
        average = 0;
        double totalAccuracy = 0;

        // Traverse to each topic
        for (Topic topic : User.current.topics.values()) {
            // Increment the total quizzes by the quizzes size
            totalQuizzes += topic.quizzes.size();

            // Traverse to each quiz
            for (Quiz quiz : topic.quizzes.values()) {
                // Increment the values respectively
                totalRetries += quiz.retries;
                average += quiz.average;
                // Accuracy = average / no. of items
                totalAccuracy += quiz.average / quiz.questions.size();
            }
        }

        // The temporary value of average is the sum of each quiz's average
        // To get the average, divide the total average by the total quiz size
        average /= totalQuizzes;
        // To get the accuracy, divide the total accuracy by the total quiz size
        // You'll get the decimal value. Multiply it by 100 to get the percentage
        accuracy = totalAccuracy / totalQuizzes * 100;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}