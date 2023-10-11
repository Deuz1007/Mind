package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mind.models.Question;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.List;
import java.util.stream.Collectors;

public class IdentificationQuizPage extends AppCompatActivity {

    EditText answer;
    TextView numberOfQuestions;
    TextView questionItem;

    Button hint;

    List<Question> questionList;

    Topic topic;
    Quiz quiz;

    int streakCounter;
    int hintCounter;
    int score;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    ProgressBar progressBar; // UI For Timer
    CountDownTimer countDownTimer; // Timer

    long totalTimeInMillis = 20000; // Timer time
    long intervalInMillis = 1000; // Timer interval

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_quiz_page);

        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
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
        hint.setVisibility(View.INVISIBLE);

        // Load the question
        loadNewQuestion();
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

        if (btnId == R.id.submitAnswer_btn) {
            // Get the user input in EditText
            selectedAnswer = answer.getText().toString();

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
            // Selected button color

            Question current = questionList.get(currentQuestionIndex);

            // Reset UI texts
            questionItem.setText(current.question);
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}