package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

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

    int score = 0;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    Topic topic;
    Quiz quiz;

    private ProgressBar progressBar; // UI For Timer
    private CountDownTimer countDownTimer; // Timer


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boolean_quiz_page);

        System.out.println("Rendered boolean page");

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

        if (btnId == R.id.choice_one_button || btnId == R.id.choice_two_button) {
            // Change button design
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.DKGRAY);

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
            Intent intent = new Intent(BooleanQuizPage.this, MultiChoiceQuizPage.class);
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
            choiceA.setText(current.choices.get(0));
            choiceB.setText(current.choices.get(1));
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