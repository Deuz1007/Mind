package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mind.models.Question;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.List;

public class BooleanQuizPage extends AppCompatActivity {

    TextView numberOfQuestions;
    TextView questionItem;
    Button choiceA, choiceB;

    List<Question> questionList;

    int score = 0;
    int totalQuestions;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    Topic topic;
    public Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boolean_quiz_page);

        numberOfQuestions = findViewById(R.id.question_num);
        questionItem = findViewById(R.id.display_question);
        choiceA = findViewById(R.id.choice_one_button);
        choiceB = findViewById(R.id.choice_two_button);

        // Get topic from intent from library sheet
        String quizId = getIntent().getStringExtra("quizId");
        String topicId = getIntent().getStringExtra("topicId");

        topic = User.current.topics.get(topicId);
        quiz = topic.quizzes.get(quizId);

        totalQuestions = quiz.itemsPerLevel;
        questionList = Quiz.getQuestionsByType(quiz, Question.QuestionType.TRUE_OR_FALSE); // get list of true of false items
//
//        choiceA.setOnClickListener(this::btnClick);
//        choiceB.setOnClickListener(this::btnClick);
//
//        numberOfQuestions.setText(totalQuestions);
//
//        loadNewQuestion();

    }

    private void loadNewQuestion() {

        if (currentQuestionIndex == totalQuestions){
            Intent intent = new Intent(BooleanQuizPage.this, MultiChoiceQuizPage.class);
            intent.putExtra("quizScore", score);
            startActivity(intent);
        }

        questionItem.setText(questionList.get(currentQuestionIndex).question);
        choiceA.setText(questionList.get(currentQuestionIndex).choices.get(0));
        choiceB.setText(questionList.get(currentQuestionIndex).choices.get(1));
    }

    private void btnClick(View v) {
        Button clickedButton = (Button) v;
        if(clickedButton.getId() == R.id.choice_one_button){
            currentQuestionIndex++;
            loadNewQuestion();

            if (selectedAnswer.equals(questionList.get(currentQuestionIndex).answer)){
                score++;
            }
        }
        if (clickedButton.getId() == R.id.choice_one_button){

        }
        else {
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.DKGRAY);
        }
    }
}