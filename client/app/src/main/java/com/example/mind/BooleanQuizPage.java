package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mind.data.ActiveQuiz;
import com.example.mind.data.ConstantValues;
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.dialogs.LoadingDialog;
import com.example.mind.dialogs.QuitDialog;
import com.example.mind.interfaces.Include;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Question;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;
import com.example.mind.utilities.ModifyButtons;

import java.util.List;
import java.util.stream.Collectors;

public class BooleanQuizPage extends AppCompatActivity {

    MediaPlayer buttonClickSound; // For Button Sound Effect

    TextView numberOfQuestions, questionItem, tv_hint, tv_streak;
    Button choiceA, choiceB, hint;

    List<Question> questionList;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    ProgressBar progressBar; // UI For Timer
    CountDownTimer timer; // Timer

    QuitDialog quitDialog;
    ErrorDialog errorDialog;

    long currentTimerTime;
    int correctColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boolean_quiz_page);

        // Button Sound Effect
        buttonClickSound = MediaPlayer.create(this, R.raw.button_click);

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

        // Color of correct answer
        correctColor = ContextCompat.getColor(this, R.color.correct_ans);

        // Create new QuitDialog
        quitDialog = new QuitDialog(this);
        quitDialog.setDoThisOnQuit(objects -> {
            ActiveQuiz.active = null;
            timer.cancel();
        });

        errorDialog = new ErrorDialog(this);
        errorDialog.dialog.setOnDismissListener(dialog -> {
            startActivity(new Intent(BooleanQuizPage.this, home_screen.class));
            finish();
        });

        // Get topic from intent from library sheet
        String code = getIntent().getStringExtra("code");
        String global = getIntent().getStringExtra("global");

        if (ActiveQuiz.active != null) {
            ActiveQuiz.active.reset();
            initialize(ActiveQuiz.active);

            return;
        }

        if (code != null) {
            // Show loading
            LoadingDialog fromCodeDialog = new LoadingDialog(this);
            fromCodeDialog.setPurpose("Getting quiz...");
            fromCodeDialog.show();

            User.getUser(code, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    fromCodeDialog.dismiss();

                    initialize((Quiz) o[0], (Topic) o[1], true);
                }

                @Override
                public void Failed(Exception e) {
                    // Invalid code
                    errorDialog.setMessage("Invalid quiz code");
                    errorDialog.show();
                }
            });

            return;
        }

        if (global != null) {
            String[] quizInfo = global.split("\\^");

            Topic topic = GlobalForum.allTopics.get(quizInfo[0]);
            Quiz quiz = topic.quizzes.get(quizInfo[1]);

            initialize(quiz, topic, true);

            return;
        }

        String quizId = getIntent().getStringExtra("quizId");
        String topicId = getIntent().getStringExtra("topicId");

        Topic topic = User.current.topics.get(topicId);
        Quiz quiz = topic.quizzes.get(quizId);

        initialize(quiz, topic, false);
    }

    private void initialize(ActiveQuiz activeQuiz) {
        initialize(activeQuiz.quiz, activeQuiz.topic, activeQuiz.isFromCode);
    }

    private void initialize(Quiz quiz,  Topic topic, boolean isFromCode) {
        ActiveQuiz.active = new ActiveQuiz(topic, quiz, isFromCode);

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
            if (ActiveQuiz.active.hints < 1) return;

            ActiveQuiz.active.hints--;
            updateCounterText();

            timer.cancel();
            startTimer(ConstantValues.TIMER_TIME + ConstantValues.BONUS_TIME);
        });

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

        if (btnId == R.id.choice_one_button || btnId == R.id.choice_two_button) {
            // Disable buttons
            ModifyButtons.setBatchEnabled(false, choiceA, choiceB);

            // Get the string inside the button
            selectedAnswer = clickedButton.getText().toString();
            Question current = questionList.get(currentQuestionIndex++);

            ActiveQuiz.active.updateScore(selectedAnswer, current.answer, current.question);
            updateCounterText();

            // Show correct and proceed to new question
            ModifyButtons.showCorrectButton(
                    current.answer,
                    correctColor,
                    o -> loadNewQuestion(),
                    choiceA, choiceB
            );
        }
    }

    public void loadNewQuestion() {
        if (currentQuestionIndex == questionList.size()) {
            timer.cancel();

            Intent intent = new Intent(BooleanQuizPage.this, MultiChoiceQuizPage.class);
            startActivity(intent);
            finish();

            return;
        }

        /* Reset values: */
        // Timer
        if (timer != null) timer.cancel();
        startTimer(ConstantValues.TIMER_TIME);
        selectedAnswer = ""; // Selected answer
        // Enable buttons
        ModifyButtons.setBatchEnabled(true, choiceA, choiceB);

        Question current = questionList.get(currentQuestionIndex);

        // Reset UI texts
        questionItem.setText(current.question);

        // Reset Color of the Buttons
        int color = ContextCompat.getColor(this, R.color.cool);
        choiceA.setBackgroundColor(color);
        choiceB.setBackgroundColor(color);
    }

    private void startTimer(long totalTime) {
        timer = new CountDownTimer(totalTime, ConstantValues.INTERVAL_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the progress bar with the remaining time
                currentTimerTime = millisUntilFinished;
                progressBar.setProgress((int) (millisUntilFinished / ConstantValues.INTERVAL_TIME));
            }

            @Override
            public void onFinish() {
                ActiveQuiz.active.streak = 0;

                // Load new question
                ModifyButtons.showCorrectButton(
                        questionList.get(currentQuestionIndex++).answer,
                        correctColor,
                        o -> loadNewQuestion(),
                        choiceA, choiceB
                );
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