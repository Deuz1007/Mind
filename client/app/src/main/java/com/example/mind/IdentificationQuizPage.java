package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mind.data.ActiveQuiz;
import com.example.mind.data.ConstantValues;
import com.example.mind.dialogs.QuitDialog;
import com.example.mind.models.Question;
import com.example.mind.utilities.ModifyButtons;

import java.util.List;
import java.util.stream.Collectors;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;


public class IdentificationQuizPage extends AppCompatActivity {

    MediaPlayer buttonClickSound; // For Button Sound Effect
    private boolean isRedWarning = false;
    Vibrator vibrator;
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
    private Animation shakeAnimation;
    private BackgroundMusicManager BackgroundMusicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        BackgroundMusicManager = BackgroundMusicManager.getInstance(this, R.raw.bgm2);


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

    private void showCorrectAnswer(String correct) {
        // Show answer
        tv_correct_ans.setText(correct.toUpperCase());
        tv_correct_ans.setVisibility(View.VISIBLE);

        Question current = questionList.get(currentQuestionIndex - 1);
        if (selectedAnswer.equalsIgnoreCase(current.answer)) {
            // Correct answer, change background to themed_correct_button.xml
            answer.setBackgroundResource(R.drawable.themed_correct_button);
            buttonClickSound.start();
        }

        new Handler().postDelayed(this::loadNewQuestion, ConstantValues.QUESTION_DELAY);
    }

    public void btnClick(View v) {
        Button clickedButton = (Button) v;
        int btnId = clickedButton.getId();

        if (isRedWarning) {
            return;
        }



        if (btnId == R.id.submitAnswer_btn) {
            // Disable buttons
            ModifyButtons.setBatchEnabled(false, hint);
            System.out.println(currentQuestionIndex);

            // Get the user input in EditText
            selectedAnswer = answer.getText().toString();
            Question current = questionList.get(currentQuestionIndex++);

            ActiveQuiz.active.updateScore(selectedAnswer, current.answer, current.question);
            updateCounterText();

            if (!selectedAnswer.equalsIgnoreCase(current.answer)) {
                // Incorrect answer, apply screen shake animation
                new Handler().postDelayed(() -> {
                    findViewById(R.id.hint_btn).startAnimation(shakeAnimation);
                    findViewById(R.id.linearLayout3).startAnimation(shakeAnimation);
                    findViewById(R.id.constraintLayout3).startAnimation(shakeAnimation);
                    findViewById(R.id.InfoBar).startAnimation(shakeAnimation);

                    vibrateDevice();
                    playWrongSoundEffect();

                    answer.setBackgroundResource(R.drawable.red_warning);
                    // Disable submit button
                    findViewById(R.id.submitAnswer_btn).setEnabled(false);

                    // Enable submit button after a delay
                    new Handler().postDelayed(() -> {
                        findViewById(R.id.submitAnswer_btn).setEnabled(true);
                    }, 1000); // Delay of 1000 milliseconds
                }, 0); // Delay of 1000 milliseconds
            }



            showCorrectAnswer(current.answer);
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
        tv_correct_ans.setText(""); // Correct answer
        answer.setText(""); // User answer
        isHinted = false;
        // Enable buttons
        ModifyButtons.setBatchEnabled(true, hint);

        currentQuestion = questionList.get(currentQuestionIndex);

        // Reset UI texts
        questionItem.setText(currentQuestion.question);
        answer.setBackgroundResource(R.drawable.light_rounded_bg);
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
                ActiveQuiz.active.streak = 0;
                showCorrectAnswer(questionList.get(currentQuestionIndex++).answer);
            }
        };

        timer.start();
    }

    private void updateCounterText() {
        // Set counters text
        int totalQuestions = questionList.size();
        int currentQuestionNumber = Math.min(currentQuestionIndex + 1, totalQuestions); // Add 1 to start from 1

        // Display the current question number and total number of questions after a delay of 1000ms
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                String counterText = currentQuestionNumber + " / " + totalQuestions;
                numberOfQuestions.setText(counterText);

                tv_hint.setText(ActiveQuiz.active.hints + "");
                tv_streak.setText(ActiveQuiz.active.streak + "");
            });
        }, 2000);
    }
    private void vibrateDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(150);
        }
    }

    private void playWrongSoundEffect() {
        // Play the wrong sound effect
        MediaPlayer wrongSound = MediaPlayer.create(this, R.raw.wrong_sfx);
        wrongSound.start();
    }
    protected void onStart() {
        super.onStart();
        BackgroundMusicManager.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicManager.start();
    }

}