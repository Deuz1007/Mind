package com.mindapps.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.ImageView;
import pl.droidsonroids.gif.GifDrawable;
import android.util.Log;

import com.mindapps.mind.data.ActiveQuiz;
import com.mindapps.mind.data.ConstantValues;
import com.mindapps.mind.dialogs.QuitDialog;
import com.mindapps.mind.models.Question;
import com.mindapps.mind.utilities.ModifyButtons;

import java.util.List;
import java.util.stream.Collectors;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class MultiChoiceQuizPage extends AppCompatActivity {

    Vibrator vibrator;
    MediaPlayer buttonClickSound; // For Button Sound Effect
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
    int correctColor;
    Animation shakeAnimation;
    Button[] choiceButtons;
    private BackgroundMusicManager BackgroundMusicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_page);

        // Button Sound Effect
        buttonClickSound = MediaPlayer.create(this, R.raw.button_click);

        // Initialize BackgroundMusicPlayer
        BackgroundMusicManager = BackgroundMusicManager.getInstance(this, R.raw.bgm2);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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

        choiceButtons = new Button[]{choiceA, choiceB, choiceC, choiceD};
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);

        if (shakeAnimation == null) {
            Log.e("Animation", "Failed to load shakeAnimation");
        } else {
            Log.d("Animation", "shakeAnimation loaded successfully");
        }

        // Color of correct answer
        //correctColor = ContextCompat.getColor(this, R.color.correct_ans);

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
        numberOfQuestions.setText(currentQuestionIndex + "");

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

                    // Change the background to themed_grad_button_inv.xml
                    hintChoiceBtn.setBackgroundResource(R.drawable.themed_grad_button_inv);

                    // Change the text color to black
                    hintChoiceBtn.setTextColor(Color.BLACK);

                    hintChoiceBtnId = hintChoiceBtn.getId();

                    // Disable the hinted choice button
                    hintChoiceBtn.setEnabled(false);

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

            // Disable buttons
            ModifyButtons.setBatchEnabled(false, choiceA, choiceB, choiceC, choiceD);

            // Change button design
            selectedAnswer = clickedButton.getText().toString();
            Question current = questionList.get(currentQuestionIndex++);

            if (!selectedAnswer.equals(current.answer)) {
                clickedButton.setBackgroundResource(R.drawable.red_warning);
                // Wrong answer, apply screen shake animation
                findViewById(R.id.hint_btn).startAnimation(shakeAnimation);
                findViewById(R.id.linearLayout3).startAnimation(shakeAnimation);
                findViewById(R.id.constraintLayout).startAnimation(shakeAnimation);
                vibrateDevice();
                playWrongSoundEffect();

            } else {
                // Correct answer, speed up the GIF
                clickedButton.setBackgroundResource(R.drawable.themed_correct_button);
                ImageView gifImageView = findViewById(R.id.animated_background);
                GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
                buttonClickSound.start();

                // Check if the drawable is a GifDrawable
                if (gifDrawable != null) {
                    gifDrawable.setSpeed(24.0f);  // Speed up the GIF

                    // Revert back to the original speed after 1 second
                    new Handler().postDelayed(() -> {
                        gifDrawable.setSpeed(1.0f);  // Original speed
                    }, 1000);
                }
            }

            ActiveQuiz.active.updateScore(selectedAnswer, current.answer, current.question);
            updateCounterText();

            // Show correct and proceed to a new question
            new Handler().postDelayed(this::loadNewQuestion, 1000);
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

        // change the item number once the button is pressed
        numberOfQuestions.setText(currentQuestionIndex + "");

        /* Reset values: */
        // Timer
        if (timer != null) timer.cancel();
        startTimer();
        selectedAnswer = ""; // Selected answer
        hintChoiceBtnId = -1; // Hint

        for (int i = 0; i < 4; i++) {
            Button choiceBtn = choiceButtons[i];
            choiceBtn.setEnabled(true);  // Enable the button
            choiceBtn.setBackgroundResource(R.drawable.themed_grad_button);  // Reset background
            choiceBtn.setTextColor(Color.WHITE);  // Reset text color
        }
        currentQuestion = questionList.get(currentQuestionIndex);

        // Reset UI texts
        questionItem.setText(currentQuestion.question);
        choiceA.setText(currentQuestion.choices.get(0));
        choiceB.setText(currentQuestion.choices.get(1));
        choiceC.setText(currentQuestion.choices.get(2));
        choiceD.setText(currentQuestion.choices.get(3));

        questionItem.setText(currentQuestion.question);

        // Reset Color of the Buttons
        int originalButtonBackground = R.drawable.themed_grad_button; // Replace with the original background resource ID
        choiceA.setBackgroundResource(originalButtonBackground);
        choiceB.setBackgroundResource(originalButtonBackground);
        choiceC.setBackgroundResource(originalButtonBackground);
        choiceD.setBackgroundResource(originalButtonBackground);
//        updateCounterText();
        // Use a Handler to post both actions with the same delay
        new Handler().post(() -> {
            runOnUiThread(() -> {
                // Update counter text
                updateCounterText();
                // Perform other actions if needed
            });
        });
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

                // Load new question
                ModifyButtons.showCorrectButton(
                        questionList.get(currentQuestionIndex++).answer,
                        correctColor,
                        o -> loadNewQuestion(),
                        choiceA, choiceB, choiceC, choiceD
                );
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
        }, 1000);
    }

    private void playWrongSoundEffect() {
        // Play the wrong sound effect
        MediaPlayer wrongSound = MediaPlayer.create(this, R.raw.wrong_sfx);
        wrongSound.start();
    }

    private void vibrateDevice() {
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(150);
            }
        }

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