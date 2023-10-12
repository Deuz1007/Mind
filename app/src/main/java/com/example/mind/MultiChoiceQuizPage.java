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

import com.example.mind.models.Question;
import com.example.mind.models.User;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;

import java.util.List;
import java.util.stream.Collectors;

public class MultiChoiceQuizPage extends AppCompatActivity {

    MediaPlayer buttonClickSound; // For Button Sound Effect

    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    Button choiceA, choiceB, choiceC, choiceD, hint;
    TextView numberOfQuestions;
    TextView questionItem;
    TextView tv_hint, tv_streak;

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
    int hintChoiceBtnId = -1;

    long totalTimeInMillis = 20000; // Timer time
    long intervalInMillis = 1000; // Timer interval

    Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

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
        // Button
        choiceA = findViewById(R.id.choice_one_button);
        choiceB = findViewById(R.id.choice_two_button);
        choiceC = findViewById(R.id.choice_three_button);
        choiceD = findViewById(R.id.choice_four_button);
        hint = findViewById(R.id.hint_btn);
        // Progress Bar
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

        // Get the multiple choice questions
        questionList = quiz.questions
                .values()
                .stream()
                .filter(question -> question.type == Question.QuestionType.MULTIPLE_CHOICE)
                .collect(Collectors.toList());

        // Set the number of questions per level
        numberOfQuestions.setText(quiz.itemsPerLevel + "");

        Button[] choiceButtons = new Button[] { choiceA, choiceB, choiceC, choiceD };

        // Hint Button set to invisible (Default)
        hint.setOnClickListener(v -> {
            if (hintCounter < 1) return;
            if (hintChoiceBtnId != -1) return;

            hintCounter--;
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
        // Show popup "Are you sure to end quiz the quiz? The progress won't save"
        // Implement popup here

        AlertDialog.Builder builder = new AlertDialog.Builder(MultiChoiceQuizPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MultiChoiceQuizPage.this).inflate(R.layout.exit_quiz_popup,(LinearLayout)findViewById(R.id.exit_popup));

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

        if (btnId == R.id.choice_one_button || btnId == R.id.choice_two_button || btnId == R.id.choice_three_button || btnId == R.id.choice_four_button) {
            buttonClickSound.start();

            // Disable button action if choice is hint
            if (btnId == hintChoiceBtnId) return;

            // Change button design
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.DKGRAY);

            // Check if selected answer is correct
            if (selectedAnswer.equals(questionList.get(currentQuestionIndex).answer)) {
                score++;    // Add score
                streakCounter++;    // Add streak

                // Check if streak counter is divisible by items per level
                if (streakCounter % quiz.itemsPerLevel == 0)
                    hintCounter++;  // Add hint
            }
            // If incorrect, reset streak to 0
            else streakCounter = 0;

            updateCounterText();

            // Increment current question index
            currentQuestionIndex++;

            // Proceed to new question
            loadNewQuestion();
        }
    }

    public void loadNewQuestion() {
        if (currentQuestionIndex == quiz.itemsPerLevel) {
            Intent intent = new Intent(MultiChoiceQuizPage.this, IdentificationQuizPage.class);
            intent.putExtra("score", score + "");
            intent.putExtra("streak", streakCounter + "");
            intent.putExtra("hints", hintCounter + "");
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
            hintChoiceBtnId = -1; // Hint

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