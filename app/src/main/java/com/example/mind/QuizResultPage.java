package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

public class QuizResultPage extends AppCompatActivity {

    String topicId;
    String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_page);

        TextView tv_correctScore = findViewById(R.id.txt_correct_answers);
        TextView tv_wrongScore = findViewById(R.id.txt_wrong_answers);

        Button btn_mainMenu = findViewById(R.id.main_menu_btn);
        Button btn_quizAgain = findViewById(R.id.again_btn);

        // Get intent extras
        topicId = getIntent().getStringExtra("topicId");
        quizId = getIntent().getStringExtra("quizId");
        String scoreStr = getIntent().getStringExtra("score");

        // Initialize objects
        Topic topic = User.current.topics.get(topicId);
        Quiz quiz = topic.quizzes.get(quizId);
        int score = Integer.parseInt(scoreStr);

        // Compute wrong score
        int wrongScore = quiz.itemsPerLevel * 3 - score;

        // Set text values
        tv_correctScore.setText(scoreStr);
        tv_wrongScore.setText(wrongScore + "");

        // Save score
        Quiz.saveScore(quiz, score, topic, new PostProcess() {
            @Override
            public void Success(Object... o) {

                // Set onclick listeners
                btn_mainMenu.setOnClickListener(v -> mainMenu());
                btn_quizAgain.setOnClickListener(v -> quizAgain());
            }

            @Override
            public void Failed(Exception e) {
                Toast.makeText(QuizResultPage.this, "Network failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Go to main menu
        mainMenu();
    }

    private void quizAgain() {
        Intent intent = new Intent(this, BooleanQuizPage.class);
        intent.putExtra("topicId", topicId);
        intent.putExtra("quizId", quizId);
        startActivity(intent);
        finish();
    }

    private void mainMenu() {
        Intent intent = new Intent(QuizResultPage.this, home_screen.class);
        startActivity(intent);
        finish();
    }
}