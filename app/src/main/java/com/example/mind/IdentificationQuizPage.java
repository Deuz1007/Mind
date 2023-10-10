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

public class IdentificationQuizPage extends AppCompatActivity {

    EditText answer;
    TextView numberOfQuestions;
    TextView questionItem;

    Button hint;

    List<Question> questionList;

    Topic topic;
    Quiz quiz;

    int score;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    private ProgressBar progressBar; // UI For Timer
    private CountDownTimer countDownTimer; // Timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_quiz_page);

        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        hint = findViewById(R.id.hint_btn);

        answer = findViewById(R.id.user_answer);

        // Get topic from intent from library sheet
        String quizId = getIntent().getStringExtra("quizId");
        String topicId = getIntent().getStringExtra("topicId");

        score = Integer.parseInt(getIntent().getStringExtra("score"));

        System.out.println(score);

        topic = User.current.topics.get(topicId);
        quiz = topic.quizzes.get(quizId);

        // Get the true or false questions
        questionList = Quiz.getQuestionsByType(quiz, Question.QuestionType.IDENTIFICATION); // get list of Multiple Choice items

        // Set the number of questions per level
        numberOfQuestions.setText(quiz.itemsPerLevel + "");

        // Hint Button set to invinsible (Default)
        hint.setVisibility(View.INVISIBLE);

        // Load the question
        loadNewQuestion();

        progressBar = findViewById(R.id.timerprogressBar);
        startTimer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Show popup "Are you sure to end quiz the quiz? The progress won't save"
    }

    public void btnClick(View v) {
        Button clickedButton = (Button) v;
        int btnId = clickedButton.getId();

        if (btnId == R.id.submitAnswer_btn) {
            // Get the user input in EditText
            selectedAnswer = answer.getText().toString();

            // Increment score if answer is correct
            if (selectedAnswer.equals(questionList.get(currentQuestionIndex).answer))
                score++;

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
            selectedAnswer = ""; // Selected answer
            // Selected button color

            Question current = questionList.get(currentQuestionIndex);

            // Reset UI texts
            questionItem.setText(current.question);
        }

    }

    private void startTimer() {
        // Set the total time in milliseconds (e.g., 30 seconds)
        long totalTimeInMillis = 30000; // 30 seconds

        // Set the interval for updating the progress bar (e.g., every second)
        long intervalInMillis = 1000; // 1 second

        countDownTimer = new CountDownTimer(totalTimeInMillis, intervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the progress bar with the remaining time
                int progress = (int) (millisUntilFinished / intervalInMillis);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                // Handle what happens when the timer finishes
                // For example, display a message or end the quiz
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