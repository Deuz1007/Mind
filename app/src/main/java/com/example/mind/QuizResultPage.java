package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.ArrayList;
import java.util.List;

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

        setPopupDialog();

        // Set onclick listeners
        btn_mainMenu.setOnClickListener(v -> mainMenu());
        btn_quizAgain.setOnClickListener(v -> quizAgain());
        btn_showResult.setOnClickListener(v -> popupDialog.show());

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

    private void setPopupDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_quiz_result_detail_popup, null);

        // Container of the RecycleView
        RecyclerView correctRecyclerView = view.findViewById(R.id.correct_items);
        RecyclerView incorrectRecyclerView = view.findViewById(R.id.incorrect_items);

        List<QuizResultAdapter.QuizItemInfo> correctItems = new ArrayList<>();
        List<QuizResultAdapter.QuizItemInfo> wrongItems = new ArrayList<>();

        for (QuizResultAdapter.QuizItemInfo item : BooleanQuizPage.quizItems)
            (item.answer.equals(item.response) ? correctItems : wrongItems).add(item);

        // RecycleView of Correct Items
        correctRecyclerView.setAdapter(new QuizResultAdapter(this, correctItems));
        correctRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // RecycleView of Incorrect Items
        incorrectRecyclerView.setAdapter(new QuizResultAdapter(this, wrongItems));
        incorrectRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        popupDialog = new Dialog(this);
        popupDialog.setContentView(view);
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setTexts() {
        int score = BooleanQuizPage.score;
        double items = BooleanQuizPage.quiz.questions.size();
        int wrong = (int) (items - score);

        // Get grade messages
        String[] messages = letterGrade(score / items * 100);

        // Set text values
        tv_correctScore.setText(score + "");
        tv_wrongScore.setText(wrong + "");
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

    private String[] letterGrade(double grade) {
        if (grade >= 90) return new String[] {"A", "Outstanding!"};
        if (grade >= 80) return new String[] {"B", "Well done!"};
        if (grade >= 70) return new String[] {"C", "Try harder"};
        if (grade >= 60) return new String[] {"D", "Failing"};
        return new String[] {"F", "Unsatisfactory"};
    }
}