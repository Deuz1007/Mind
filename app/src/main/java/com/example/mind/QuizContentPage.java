package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mind.exceptions.MaxContentTokensReachedException;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

public class QuizContentPage extends AppCompatActivity {

    private Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_content_page);

        // Edit Text
        EditText editContentField = findViewById(R.id.edit_content_field);

        // Get topic from intent from library sheet
        String topicId = getIntent().getStringExtra("topicId");
        topic = User.current.topics.get(topicId);

        editContentField.setText(topic.content); // display the intent text in the editText

        boolean[] editEnabled = {false};
        editContentField.setEnabled(editEnabled[0]);

        Button editContent = findViewById(R.id.edit_btn);

        Button saveContent = findViewById(R.id.save_btn);
        saveContent.setVisibility(View.INVISIBLE);

        // Get the selected text
        Editable editable = editContentField.getText();
        int selectionStart = editContentField.getSelectionStart();
        int selectionEnd = editContentField.getSelectionEnd();

        if (selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
            String selectedText = editable.subSequence(selectionStart, selectionEnd).toString();

            // Display the selected text in a Toast
            Toast.makeText(QuizContentPage.this, "Selected Text: " + selectedText, Toast.LENGTH_SHORT).show();
        }

        // edit
        editContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContentField.setEnabled(true);
                editEnabled[0] = false;

                editContent.setVisibility(View.INVISIBLE);
                saveContent.setVisibility(View.VISIBLE);
            }
        });

        // save
        saveContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContentField.setEnabled(false);
                editEnabled[0] = true;

                editContent.setVisibility(View.VISIBLE);
                saveContent.setVisibility(View.INVISIBLE);
            }
        });

        // Generate Quiz
        Button generate = findViewById(R.id.generate_quiz_btn);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check topic content
                // true if less than max
                if(Quiz.isValidContent(topic.content)){
                    try {
                        Topic.createQuiz(topic, topic.content, 5, new PostProcess() {
                            @Override
                            public void Success(Object... o) {
                                Toast.makeText(QuizContentPage.this, "Quiz Generation Success", Toast.LENGTH_SHORT).show();

                                try {
                                    System.out.println(o.length);
                                    Quiz quiz = (Quiz) o[0];

                                    Intent intent = new Intent(QuizContentPage.this, BooleanQuizPage.class);
                                    intent.putExtra("quizId", quiz.quizId);
                                    intent.putExtra("topicId", topic.topicId);
                                    startActivity(intent);
                                } catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                            }

                            @Override
                            public void Failed(Exception e) {
                                Toast.makeText(QuizContentPage.this, "Quiz Generation Failed", Toast.LENGTH_SHORT).show();
                                System.out.println(e.getMessage());
                            }
                        });
                    } catch (MaxContentTokensReachedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //
                else {

                }
            }
        });

        // Go back to home screen
        Button goBack = findViewById(R.id.back_btn);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizContentPage.this, home_screen.class);
                startActivity(intent);
            }
        });

    }
}