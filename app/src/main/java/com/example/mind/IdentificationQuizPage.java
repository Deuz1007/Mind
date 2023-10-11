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

    EditText answer;
    TextView numberOfQuestions;
    TextView questionItem;
    TextView tv_hint, tv_streak, tv_hintText;

    Button hint;

    List<Question> questionList;

    Topic topic;
    Quiz quiz;
    Question currentQuestion;

    int streakCounter;
    int hintCounter;
    int score;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    ProgressBar progressBar; // UI For Timer
    CountDownTimer countDownTimer; // Timer

    long totalTimeInMillis = 20000; // Timer time
    long intervalInMillis = 1000; // Timer interval

    Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_quiz_page);

        // Button Sound Effect
        buttonClickSound = MediaPlayer.create(this, R.raw.button_click);

        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        tv_hint = findViewById(R.id.hint_count);
        tv_streak = findViewById(R.id.streak_count);
        tv_hintText = findViewById(R.id.show_hint_text);

        hint = findViewById(R.id.hint_btn);

        answer = findViewById(R.id.user_answer);

        progressBar = findViewById(R.id.timerprogressBar);

        // Get strings from intent
        String quizId = getIntent().getStringExtra("quizId");
        String topicId = getIntent().getStringExtra("topicId");
        String scoreStr = getIntent().getStringExtra("score");
        String streakStr = getIntent().getStringExtra("streak");
        String hintStr = getIntent().getStringExtra("hints");

        // Set topic and quiz
        topic = User.current.topics.get(topicId);
        quiz = topic.quizzes.get(quizId);

        // Set counters
        score = Integer.parseInt(scoreStr);
        streakCounter = Integer.parseInt(streakStr);
        hintCounter = Integer.parseInt(hintStr);

        // Get the identification questions
        questionList = quiz.questions
                .values()
                .stream()
                .filter(question -> question.type == Question.QuestionType.IDENTIFICATION)
                .collect(Collectors.toList());

        // Set the number of questions per level
        numberOfQuestions.setText(quiz.itemsPerLevel + "");

        // Hint Button set to invisible (Default)
        hint.setOnClickListener(v -> {
            if (hintCounter < 1) return;
            if (!tv_hintText.getText().toString().equals("")) return;

            hintCounter--;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(IdentificationQuizPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(IdentificationQuizPage.this).inflate(R.layout.exit_quiz_popup,(LinearLayout)findViewById(R.id.exit_popup));

        builder.setView(view);
        ((TextView) view.findViewById(R.id.quit_comment)).setText("Exiting Already?");

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.yes_btn).setOnClickListener(View -> {
            finish();
            System.exit(0);
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
            selectedAnswer = answer.getText().toString().toLowerCase();

            // Check if selected answer is correct
            if (selectedAnswer.equals(questionList.get(currentQuestionIndex).answer.toLowerCase())) {
                score++;    // Add score
                streakCounter++;    // Add streak

                // Check if streak counter is divisible by items per level
                if (streakCounter % quiz.itemsPerLevel == 0)
                    hintCounter++;  // Add hint
            }
            // If incorrect, reset streak to 0
            else streakCounter = 0;

            // Increment current question index
            currentQuestionIndex++;

            // Proceed to new question
            loadNewQuestion();
        }
    }

    public void loadNewQuestion() {
        if (currentQuestionIndex == quiz.itemsPerLevel) {
            Intent intent = new Intent(IdentificationQuizPage.this, QuizResultPage.class);
            intent.putExtra("score", score + "");
            intent.putExtra("quizId", quiz.quizId);
            intent.putExtra("topicId", topic.topicId);

            startActivity(intent);
            finish();
        }
        else {
            /* Reset values: */
            // Timer
            if (countDownTimer != null)
                countDownTimer.cancel();
            countDownTimer = null;
            startTimer();

            selectedAnswer = ""; // Selected answer
            tv_hintText.setText(""); // Hint

            currentQuestion = questionList.get(currentQuestionIndex);

            // Reset UI texts
            questionItem.setText(currentQuestion.question);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeInMillis, intervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the progress bar with the remaining time
                int progress = (int) (millisUntilFinished / intervalInMillis);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                currentQuestionIndex++;
                loadNewQuestion();
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Button Sound
        if (buttonClickSound != null) {
            buttonClickSound.release();
            buttonClickSound = null;
        }
    }

    private void updateCounterText() {
        // Set counters text
        tv_hint.setText(hintCounter + "");
        tv_streak.setText(streakCounter + "");
    }
}