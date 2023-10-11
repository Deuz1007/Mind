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
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class BooleanQuizPage extends AppCompatActivity {

    TextView numberOfQuestions;
    TextView questionItem;
    TextView tv_hint, tv_streak;
    Button choiceA, choiceB, hint;

    List<Question> questionList;

    int streakCounter = 0;
    int hintCounter = 0;

    int score = 0;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    Topic topic;
    Quiz quiz;

    ProgressBar progressBar; // UI For Timer
    CountDownTimer countDownTimer; // Timer

    final long totalTimeInMillis = 20000; // Timer time
    final long intervalInMillis = 1000; // Timer interval
    final long bonusTime = 5000;
    long timerTime;

    Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boolean_quiz_page);

        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        choiceA = findViewById(R.id.choice_one_button);
        choiceB = findViewById(R.id.choice_two_button);
        hint = findViewById(R.id.hint_btn);

        tv_hint = findViewById(R.id.hint_count);
        tv_streak = findViewById(R.id.streak_count);

        // Get topic from intent from library sheet
        String quizId = getIntent().getStringExtra("quizId");
        String topicId = getIntent().getStringExtra("topicId");

        // Load topic and quiz from their ids
        topic = User.current.topics.get(topicId);
        quiz = topic.quizzes.get(quizId);

        // Get the true or false questions
        questionList = quiz.questions
                .values()
                .stream()
                .filter(question -> question.type == Question.QuestionType.TRUE_OR_FALSE)
                .collect(Collectors.toList());

        // Set the number of questions per level
        numberOfQuestions.setText(quiz.itemsPerLevel + "");

        updateCounterText();

        // Hint Button set to invisible (Default)
        hint.setOnClickListener(v -> {
            if (hintCounter < 1) return;

            hintCounter--;
            updateCounterText();

            countDownTimer.cancel();
            startTimer(timerTime + bonusTime);
        });

        // Load the question
        loadNewQuestion();

        progressBar = findViewById(R.id.timerprogressBar);

        // To display upload option popup layout
//        popupDialog = new Dialog(this);
    }

    @Override
    public void onBackPressed() {
        // Show popup "Are you sure to end quiz the quiz? The progress won't save"
        // Implement popup here

      AlertDialog.Builder builder = new AlertDialog.Builder(BooleanQuizPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(BooleanQuizPage.this).inflate(R.layout.exit_quiz_popup,(LinearLayout)findViewById(R.id.exit_popup));

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

        if (btnId == R.id.choice_one_button || btnId == R.id.choice_two_button) {
            // Change button design
            selectedAnswer = clickedButton.getText().toString();

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
            Intent intent = new Intent(BooleanQuizPage.this, MultiChoiceQuizPage.class);
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
            startTimer(totalTimeInMillis);

            selectedAnswer = ""; // Selected answer
            // Selected button color

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
        countDownTimer = new CountDownTimer(totalTime, intervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the progress bar with the remaining time
                timerTime = millisUntilFinished;
                progressBar.setProgress((int) (millisUntilFinished / intervalInMillis));
            }

            @Override
            public void onFinish() {
                // Increase question index
                currentQuestionIndex++;
                // Load new question
                loadNewQuestion();
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void updateCounterText() {
        // Set counters text
        tv_hint.setText(hintCounter + "");
        tv_streak.setText(streakCounter + "");
    }

    public void showExitPopup(Activity context) {
        // Create a View that contains your layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.exit_quiz_popup, null);

        // Create a PopupWindow
        int width = (int) 1100;
        int height = (int) 2000;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // Initialize UI elements in the popup layout
        Button yesButton = popupView.findViewById(R.id.yes_btn);
        Button noButton = popupView.findViewById(R.id.no_btn);

        // Exiting the quiz and closing the progress
        yesButton.setOnClickListener(view -> {
            startActivity(new Intent(this, home_screen.class));
            finish();
        });

        // dismissing the popup
        noButton.setOnClickListener(view -> popupWindow.dismiss());

        // Show the popup at the center of the screen
        popupWindow.showAtLocation(context.getWindow().getDecorView(), 0, 0, 0);
    }
}