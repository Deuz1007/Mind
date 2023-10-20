package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mind.data.ActiveQuiz;
import com.example.mind.data.ConstantValues;
import com.example.mind.dialogs.QuitDialog;
import com.example.mind.models.Question;
import com.example.mind.models.User;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.utilities.ButtonToggleEnable;

import java.util.List;
import java.util.stream.Collectors;

public class MultiChoiceQuizPage extends AppCompatActivity {

//    MediaPlayer buttonClickSound; // For Button Sound Effect
//
//    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    TextView numberOfQuestions, questionItem, tv_hint, tv_streak;
    Button choiceA, choiceB, choiceC, choiceD, hint;

    List<Question> questionList;
    int currentQuestionIndex = 0;
    Question currentQuestion;
    String selectedAnswer = "";
    int hintChoiceBtnId = -1;

    ProgressBar progressBar; // UI For Timer
    CountDownTimer timer; // Timer

    QuitDialog quitDialog;

    Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        // Button Sound Effect
//        buttonClickSound = MediaPlayer.create(this, R.raw.button_click);

        // BGM
//        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.quiz_bgm);
//        backgroundMusicPlayer.start();

        // TextView
        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        tv_hint = findViewById(R.id.hint_count);
        tv_streak = findViewById(R.id.streak_count);

        // Button
        choiceA = findViewById(R.id.choice_one_button);
        choiceB = findViewById(R.id.choice_two_button);
        choiceC = findViewById(R.id.choice_three_button);
        choiceD = findViewById(R.id.choice_four_button);
        hint = findViewById(R.id.hint_btn);

        // Progress Bar
        progressBar = findViewById(R.id.timerprogressBar);

        // Create new QuitDialog
        quitDialog = new QuitDialog(this);
        quitDialog.setDoThisOnQuit(objects -> {
            ActiveQuiz.active = null;
            timer.cancel();
        });

        // Get the multiple choice questions
        questionList = ActiveQuiz.active.quiz.questions
                .values()
                .stream()
                .filter(question -> question.type == Question.QuestionType.MULTIPLE_CHOICE)
                .collect(Collectors.toList());

        // Set the number of questions per level
        numberOfQuestions.setText(ActiveQuiz.active.quiz.itemsPerLevel + "");

        Button[] choiceButtons = new Button[] { choiceA, choiceB, choiceC, choiceD };

        // Hint Button set to invisible (Default)
        hint.setOnClickListener(v -> {
            if (ActiveQuiz.active.hints < 1) return;
            if (hintChoiceBtnId != -1) return;

            ActiveQuiz.active.hints--;
            updateCounterText();

            String hintChoice = Question.hint(currentQuestion);
            for (int i = 0; i < 4; i++) {
                if (hintChoice.equals(currentQuestion.choices.get(i))) {
                    Button hintChoiceBtn = choiceButtons[i];

                    hintChoiceBtn.setBackgroundColor(Color.DKGRAY);
                    hintChoiceBtnId = hintChoiceBtn.getId();

                    break;
                }
            }
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

        if (btnId == R.id.choice_one_button || btnId == R.id.choice_two_button || btnId == R.id.choice_three_button || btnId == R.id.choice_four_button) {
            // Disable button action if choice is hint
            if (btnId == hintChoiceBtnId) return;

            // Diable buttons
            ButtonToggleEnable.setBatchEnabled(false, choiceA, choiceB, choiceC, choiceD);

            // Change button design
            selectedAnswer = clickedButton.getText().toString();
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
    }

    public void loadNewQuestion() {
        if (currentQuestionIndex == questionList.size()) {
            timer.cancel();

            Intent intent = new Intent(MultiChoiceQuizPage.this, IdentificationQuizPage.class);
            startActivity(intent);
            finish();

            return;
        }

        /* Reset values: */
        // Timer
        if (timer != null) timer.cancel();
        startTimer();
        selectedAnswer = ""; // Selected answer
        hintChoiceBtnId = -1; // Hint
        // Enable buttons
        ButtonToggleEnable.setBatchEnabled(true, choiceA, choiceB, choiceC, choiceD);

        currentQuestion = questionList.get(currentQuestionIndex);

        // Reset UI texts
        questionItem.setText(currentQuestion.question);
        choiceA.setText(currentQuestion.choices.get(0));
        choiceB.setText(currentQuestion.choices.get(1));
        choiceC.setText(currentQuestion.choices.get(2));
        choiceD.setText(currentQuestion.choices.get(3));

        // Reset Color of the Buttons
        int color = ContextCompat.getColor(this, R.color.cool);
        choiceA.setBackgroundColor(color);
        choiceB.setBackgroundColor(color);
        choiceC.setBackgroundColor(color);
        choiceD.setBackgroundColor(color);
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