package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mind.data.ActiveQuiz;
import com.example.mind.data.ConstantValues;
import com.example.mind.dialogs.QuitDialog;
import com.example.mind.models.Question;
import com.example.mind.utilities.ButtonToggleEnable;

import java.util.List;
import java.util.stream.Collectors;

public class IdentificationQuizPage extends AppCompatActivity {

    MediaPlayer buttonClickSound; // For Button Sound Effect
//    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    EditText answer;
    TextView numberOfQuestions, questionItem, tv_hint, tv_streak, tv_hintText, tv_correct_ans;
    Button hint;

    List<Question> questionList;
    int currentQuestionIndex = 0;
    Question currentQuestion;
    String selectedAnswer = "";

    boolean isHinted;

    ProgressBar progressBar; // UI For Timer
    CountDownTimer timer; // Timer

    QuitDialog quitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_quiz_page);

        // Button Sound Effect
        buttonClickSound = MediaPlayer.create(this, R.raw.button_click);

        // BGM
//        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.quiz_bgm);
//        backgroundMusicPlayer.start();

        // TextView
        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        tv_hint = findViewById(R.id.hint_count);
        tv_streak = findViewById(R.id.streak_count);
        tv_hintText = findViewById(R.id.show_hint_text);
        tv_correct_ans = findViewById(R.id.correct_ans_text);

        // Hide correct answer (default)
        tv_correct_ans.setVisibility(View.INVISIBLE);

        // Button
        hint = findViewById(R.id.hint_btn);

        // EditText
        answer = findViewById(R.id.user_answer);

        // ProgressBar
        progressBar = findViewById(R.id.timerprogressBar);

        // Create new QuitDialog
        quitDialog = new QuitDialog(this);
        quitDialog.setDoThisOnQuit(objects -> {
            ActiveQuiz.active = null;
            timer.cancel();
        });

        // Get the identification questions
        questionList = ActiveQuiz.active.quiz.questions
                .values()
                .stream()
                .filter(question -> question.type == Question.QuestionType.IDENTIFICATION)
                .collect(Collectors.toList());

        System.out.println("Fill in the Blanks questions: " + questionList.size());

        System.out.println(ActiveQuiz.active.quiz.questions.size());

        // Set the number of questions per level
        numberOfQuestions.setText(ActiveQuiz.active.quiz.itemsPerLevel + "");

        // Hint Button set to invisible (Default)
        hint.setOnClickListener(v -> {
//            buttonClickSound.start();

            if (ActiveQuiz.active.hints < 1) return;
            if (isHinted) return;

            ActiveQuiz.active.hints--;
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
        quitDialog.show();
    }

    public void btnClick(View v) {
        Button clickedButton = (Button) v;
        int btnId = clickedButton.getId();

        buttonClickSound.start();

        if (btnId == R.id.submitAnswer_btn) {
            try {
                // Disable buttons
                ButtonToggleEnable.setBatchEnabled(false, hint);
                System.out.println(currentQuestionIndex);

                // Get the user input in EditText
                selectedAnswer = answer.getText().toString();
                Question current = questionList.get(currentQuestionIndex);

                ActiveQuiz.active.updateScore(
                        selectedAnswer,
                        current.answer,
                        current.question
                );
                updateCounterText();

                // Increment current question index
                currentQuestionIndex++;

                // Proceed to new question
                loadNewQuestion();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void loadNewQuestion() {
        if (currentQuestionIndex == questionList.size()) {
            timer.cancel();

            Intent intent = new Intent(IdentificationQuizPage.this, QuizResultPage.class);
            startActivity(intent);
            finish();

            return;
        }

        /* Reset values: */
        // Timer
        if (timer != null) timer.cancel();
        startTimer();
        selectedAnswer = ""; // Selected answer
        tv_hintText.setText(""); // Hint
        answer.setText(""); // Answer
        isHinted = false;
        // Enable buttons
        ButtonToggleEnable.setBatchEnabled(true, hint);

        currentQuestion = questionList.get(currentQuestionIndex);

        // Reset UI texts
        questionItem.setText(currentQuestion.question);
    }

    private void startTimer() {
        timer = new CountDownTimer(ConstantValues.TIMER_TIME, ConstantValues.INTERVAL_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the progress bar with the remaining time
                int progress = (int) (millisUntilFinished / ConstantValues.INTERVAL_TIME);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                currentQuestionIndex++;
                ActiveQuiz.active.streak = 0;
                loadNewQuestion();
            }
        };

        timer.start();
    }

    private void updateCounterText() {
        // Set counters text
        tv_hint.setText(ActiveQuiz.active.hints + "");
        tv_streak.setText(ActiveQuiz.active.streak + "");
    }
}