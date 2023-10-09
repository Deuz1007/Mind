package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    List<Question> questionList;

    Topic topic;
    Quiz quiz;

    int score;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_quiz_page);

        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);

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

        // Load the question
        loadNewQuestion();
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
}