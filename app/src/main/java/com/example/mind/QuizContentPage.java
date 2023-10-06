package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class QuizContentPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_content_page);

//        ReviewQuizContentFragment contentFragment = new ReviewQuizContentFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        fragmentTransaction.replace(R.id.contentFrameLayout, contentFragment);
//        fragmentTransaction.commit();

        // Edit Text
        EditText editContentField = findViewById(R.id.edit_content_field);

        boolean[] editEnabled = {false};
        editContentField.setEnabled(editEnabled[0]);

        Button editContent = findViewById(R.id.edit_btn);

        Button saveContent = findViewById(R.id.save_btn);
        saveContent.setVisibility(View.INVISIBLE);

        editContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContentField.setEnabled(true);
                editEnabled[0] = false;

                editContent.setVisibility(View.INVISIBLE);
                saveContent.setVisibility(View.VISIBLE);
            }
        });

        saveContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContentField.setEnabled(false);
                editEnabled[0] = true;

                editContent.setVisibility(View.VISIBLE);
                saveContent.setVisibility(View.INVISIBLE);
            }
        });

        // Go to Capture Page
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