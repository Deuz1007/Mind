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
import android.widget.Toast;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.ArrayList;

public class QuizResultPage extends AppCompatActivity {
    TextView tv_correctScore, tv_wrongScore, tv_letterGrade, tv_compliment;

    Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_page);

        tv_correctScore = findViewById(R.id.txt_correct_answers);
        tv_wrongScore = findViewById(R.id.txt_wrong_answers);
        tv_letterGrade = findViewById(R.id.view_text_grade);
        tv_compliment = findViewById(R.id.grade_compliment);

        Button btn_mainMenu = findViewById(R.id.main_menu_btn);
        Button btn_quizAgain = findViewById(R.id.again_btn);
        Button btn_showResult = findViewById(R.id.show_details);

        // Set onclick listeners
        btn_mainMenu.setOnClickListener(v -> mainMenu());
        btn_quizAgain.setOnClickListener(v -> quizAgain());
        btn_showResult.setOnClickListener(v -> showDetails());

        if (BooleanQuizPage.isFromCode) setTexts();
        else
            // Save score
            Quiz.saveScore(BooleanQuizPage.quiz, BooleanQuizPage.score, BooleanQuizPage.topic, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    setTexts();
                }

                @Override
                public void Failed(Exception e) {
                    Toast.makeText(QuizResultPage.this, "Quiz result not saved", Toast.LENGTH_LONG).show();
                }
            });
    }

    private void setTexts() {
        int score = BooleanQuizPage.score;
        int items = BooleanQuizPage.quiz.questions.size();

        // Get grade messages
        String[] messages = letterGrade(score / items * 100);

        // Set text values
        tv_correctScore.setText(score + "");
        tv_wrongScore.setText((items - score) + "");
        tv_letterGrade.setText(messages[0]);
        tv_compliment.setText(messages[1]);
    }

    @Override
    public void onBackPressed() {
        // Go to main menu
        mainMenu();
    }

    private void quizAgain() {
        Intent intent = new Intent(this, BooleanQuizPage.class);
        startActivity(intent);
        finish();
    }

    private void mainMenu() {
        BooleanQuizPage.quiz = null;
        BooleanQuizPage.topic = null;

        Intent intent = new Intent(QuizResultPage.this, home_screen.class);
        startActivity(intent);
        finish();
    }

    private void showDetails(){
        popupDialog.setContentView(R.layout.activity_quiz_result_detail_popup);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }

    private String[] letterGrade(double grade) {
        if (grade >= 90) return new String[] {"A", "Outstanding!"};
        if (grade >= 80) return new String[] {"B", "Well done!"};
        if (grade >= 70) return new String[] {"C", "Try harder"};
        if (grade >= 60) return new String[] {"D", "Failing"};
        return new String[] {"F", "Unsatisfactory"};
    }
}