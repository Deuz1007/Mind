package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mind.models.Question;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class IdentificationQuizPage extends AppCompatActivity {

    MediaPlayer buttonClickSound; // For Button Sound Effect
    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    EditText answer;
    TextView numberOfQuestions, questionItem, tv_hint, tv_streak, tv_hintText;
    Button hint;

    List<Question> questionList;
    int currentQuestionIndex = 0;
    Question currentQuestion;
    String selectedAnswer = "";

    boolean isHinted;

    ProgressBar progressBar; // UI For Timer
    CountDownTimer timer; // Timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_quiz_page);

        // Button Sound Effect
        buttonClickSound = MediaPlayer.create(this, R.raw.button_click);

        // BGM
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.quiz_bgm);
        backgroundMusicPlayer.start();

        // TextView
        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        tv_hint = findViewById(R.id.hint_count);
        tv_streak = findViewById(R.id.streak_count);
        tv_hintText = findViewById(R.id.show_hint_text);

        // Button
        hint = findViewById(R.id.hint_btn);

        // EditText
        answer = findViewById(R.id.user_answer);

        // ProgressBar
        progressBar = findViewById(R.id.timerprogressBar);

        // Get the identification questions
        questionList = BooleanQuizPage.quiz.questions
                .values()
                .stream()
                .filter(question -> question.type == Question.QuestionType.IDENTIFICATION)
                .collect(Collectors.toList());

        // Set the number of questions per level
        numberOfQuestions.setText(BooleanQuizPage.quiz.itemsPerLevel + "");

        // Hint Button set to invisible (Default)
        hint.setOnClickListener(v -> {
            buttonClickSound.start();

            if (BooleanQuizPage.hintCounter < 1) return;
            if (isHinted) return;

            BooleanQuizPage.hintCounter--;
            updateCounterText();

            String hintChoice = Question.hint(currentQuestion);
            tv_hintText.setText(hintChoice);
        });

        updateCounterText();

        // Load the question
        loadNewQuestion();
    }

    @Override
    public void onBackPressed() {
        // Show popup "Are you sure to end quiz the quiz? The progress won't save"
        // Implement popup here
        BooleanQuizPage.quiz = null;
        BooleanQuizPage.topic = null;
        timer.cancel();
        buttonClickSound.release();

        AlertDialog.Builder builder = new AlertDialog.Builder(IdentificationQuizPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(IdentificationQuizPage.this).inflate(R.layout.exit_quiz_popup,(LinearLayout)findViewById(R.id.exit_popup));

        builder.setView(view);
        ((TextView) view.findViewById(R.id.quit_comment)).setText("Exiting Already?");

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.yes_btn).setOnClickListener(View -> {
            startActivity(new Intent(this, home_screen.class));
            finish();
        });

        view.findViewById(R.id.no_btn).setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void btnClick(View v) {
        Button clickedButton = (Button) v;
        int btnId = clickedButton.getId();

        if (btnId == R.id.submitAnswer_btn) {
            buttonClickSound.start();

            // Get the user input in EditText
            selectedAnswer = answer.getText().toString();

            BooleanQuizPage.updateScore(selectedAnswer, questionList.get(currentQuestionIndex).answer);
            updateCounterText();

            // Increment current question index
            currentQuestionIndex++;

            // Proceed to new question
            loadNewQuestion();
        }
    }

    public void loadNewQuestion() {
        if (currentQuestionIndex == BooleanQuizPage.quiz.itemsPerLevel) {
            timer.cancel();

            Intent intent = new Intent(IdentificationQuizPage.this, QuizResultPage.class);
            startActivity(intent);
            backgroundMusicPlayer.stop();
            finish();
        }
        else {
            /* Reset values: */
            // Timer
            if (timer != null) timer.cancel();
            startTimer();
            selectedAnswer = ""; // Selected answer
            tv_hintText.setText(""); // Hint
            answer.setText(""); // Answer
            isHinted = false;

            currentQuestion = questionList.get(currentQuestionIndex);

            // Reset UI texts
            questionItem.setText(currentQuestion.question);
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(BooleanQuizPage.TIMER_TOTAL_TIME, BooleanQuizPage.TIMER_INTERVAL_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the progress bar with the remaining time
                int progress = (int) (millisUntilFinished / BooleanQuizPage.TIMER_INTERVAL_TIME);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                currentQuestionIndex++;
                loadNewQuestion();
            }
        };

        timer.start();
    }

    private void updateCounterText() {
        // Set counters text
        tv_hint.setText(BooleanQuizPage.hintCounter + "");
        tv_streak.setText(BooleanQuizPage.streakCounter + "");
    }
}