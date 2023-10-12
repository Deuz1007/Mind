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

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Question;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class BooleanQuizPage extends AppCompatActivity {

    MediaPlayer buttonClickSound; // For Button Sound Effect
    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    TextView numberOfQuestions, questionItem, tv_hint, tv_streak;
    Button choiceA, choiceB, hint;

    List<Question> questionList;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    ProgressBar progressBar; // UI For Timer
    CountDownTimer timer; // Timer

    public static Topic topic;
    public static Quiz quiz;
    public static boolean isFromCode;

    public static int streakCounter;
    public static int hintCounter;
    public static int score;

    public static final long TIMER_TOTAL_TIME = 20000; // Timer time
    public static final long TIMER_INTERVAL_TIME = 1000; // Timer interval
    final long BONUS_TIME = 5000;
    long currentTimerTime;

    Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boolean_quiz_page);

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
        hint = findViewById(R.id.hint_btn);

        // ProgressBar
        progressBar = findViewById(R.id.timerprogressBar);

        // Get topic from intent from library sheet
        String code = getIntent().getStringExtra("code");

        if (quiz == null) {
            if (code == null) {
                isFromCode = false;

                String quizId = getIntent().getStringExtra("quizId");
                String topicId = getIntent().getStringExtra("topicId");

                Topic topic = User.current.topics.get(topicId);
                Quiz quiz = topic.quizzes.get(quizId);

                initialize(quiz, topic);
            }
            else {
                isFromCode = true;
                User.getUser(code, new PostProcess() {
                    @Override
                    public void Success(Object... o) {
                        initialize((Quiz) o[0], (Topic) o[1]);
                    }

                    @Override
                    public void Failed(Exception e) {
                        // Invalid code
                    }
                });
            }
        }
        else initialize(quiz, topic);
    }

    private void initialize(Quiz quizInstance,  Topic topicInstance) {
        quiz = quizInstance;
        topic = topicInstance;

        streakCounter = 0;
        hintCounter = 0;
        score = 0;

        // Get the true or false questions
        questionList = quiz.questions
                .values()
                .stream()
                .filter(question -> question.type == Question.QuestionType.TRUE_OR_FALSE)
                .collect(Collectors.toList());

        // Set the number of questions per level
        numberOfQuestions.setText(quiz.itemsPerLevel + "");

        // Update texts
        updateCounterText();

        // Set onclick listener to hint button
        hint.setOnClickListener(v -> {
            if (hintCounter < 1) return;

            hintCounter--;
            updateCounterText();

            timer.cancel();
            startTimer(TIMER_TOTAL_TIME + BONUS_TIME);
        });

        // Load the question
        loadNewQuestion();
    }

    @Override
    public void onBackPressed() {
        // Show popup "Are you sure to end quiz the quiz? The progress won't save"
        // Implement popup here
        quiz = null;
        topic = null;
        timer.cancel();
        buttonClickSound.release();

        AlertDialog.Builder builder = new AlertDialog.Builder(BooleanQuizPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(BooleanQuizPage.this).inflate(R.layout.exit_quiz_popup,(LinearLayout)findViewById(R.id.exit_popup));

        builder.setView(view);
        ((TextView) view.findViewById(R.id.quit_comment)).setText("Exiting Already?");

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.yes_btn).setOnClickListener(View -> {
//            buttonClickSound.start();
            startActivity(new Intent(this, home_screen.class));
            finish();
        });

        view.findViewById(R.id.no_btn).setOnClickListener(View -> {
            buttonClickSound.start();
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

        if (btnId == R.id.choice_one_button || btnId == R.id.choice_two_button) {
            buttonClickSound.start();

            // Get the string inside the button
            selectedAnswer = clickedButton.getText().toString();

            updateScore(selectedAnswer, questionList.get(currentQuestionIndex).answer);
            updateCounterText();

            // Increment current question index
            currentQuestionIndex++;

            // Proceed to new question
            loadNewQuestion();
        }
    }

    public void loadNewQuestion() {
        if (currentQuestionIndex == quiz.itemsPerLevel) {
            timer.cancel();

            Intent intent = new Intent(BooleanQuizPage.this, MultiChoiceQuizPage.class);
            startActivity(intent);
            finish();
        }
        else {
            /* Reset values: */
            // Timer
            if (timer != null) timer.cancel();
            startTimer(TIMER_TOTAL_TIME);
            selectedAnswer = ""; // Selected answer

            Question current = questionList.get(currentQuestionIndex);

            // Reset UI texts
            questionItem.setText(current.question);
            choiceA.setText(current.choices.get(0));
            choiceB.setText(current.choices.get(1));

            // Reset Color of the Buttons
            int color = ContextCompat.getColor(this, R.color.cool);
            choiceA.setBackgroundColor(color);
            choiceB.setBackgroundColor(color);
        }
    }

    private void startTimer(long totalTime) {
        timer = new CountDownTimer(totalTime, TIMER_INTERVAL_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the progress bar with the remaining time
                currentTimerTime = millisUntilFinished;
                progressBar.setProgress((int) (millisUntilFinished / TIMER_INTERVAL_TIME));
            }

            @Override
            public void onFinish() {
                // Increase question index
                currentQuestionIndex++;
                // Load new question
                loadNewQuestion();
            }
        };

        timer.start();
    }

    private void updateCounterText() {
        // Set counters text
        tv_hint.setText(hintCounter + "");
        tv_streak.setText(streakCounter + "");
    }

    public static void updateScore(String userAnswer, String correctAnswer) {
        userAnswer = userAnswer.toLowerCase();
        correctAnswer = correctAnswer.toLowerCase();

        // Check if selected answer is correct
        if (userAnswer.equals(correctAnswer)) {
            score++;    // Add score
            streakCounter++;    // Add streak

            // Check if streak counter is divisible by items per level
            if (streakCounter % quiz.itemsPerLevel == 0)
                hintCounter++;  // Add hint
        }
        // If incorrect, reset streak to 0
        else streakCounter = 0;
    }
}