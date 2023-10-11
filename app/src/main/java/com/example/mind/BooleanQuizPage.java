package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
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

    long totalTimeInMillis = 20000; // Timer time
    long intervalInMillis = 1000; // Timer interval

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boolean_quiz_page);

        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        choiceA = findViewById(R.id.choice_one_button);
        choiceB = findViewById(R.id.choice_two_button);
        hint = findViewById(R.id.hint_btn);

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

        // Hint Button set to invisible (Default)
        hint.setVisibility(View.INVISIBLE);

        // Load the question
        loadNewQuestion();

        progressBar = findViewById(R.id.timerprogressBar);
    }

    @Override
    public void onBackPressed() {
        // Show popup "Are you sure to end quiz the quiz? The progress won't save"

        startActivity(new Intent(this, home_screen.class));
        finish();
    }

    public void btnClick(View v) {
        Button clickedButton = (Button) v;
        int btnId = clickedButton.getId();

        if (btnId == R.id.choice_one_button || btnId == R.id.choice_two_button) {
            // Change button design
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.DKGRAY);

            // Check if selected answer is correct
            if (selectedAnswer.equals(questionList.get(currentQuestionIndex).answer)) {
                score++;    // Add score
                streakCounter++;    // Add streak

                // Check if streak counter got same as items per level
                if (streakCounter == quiz.itemsPerLevel) {
                    streakCounter = 0;  // Reset streak count
                    hintCounter++;  // Add hint
                }
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
            countDownTimer = null;
            startTimer();

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
}