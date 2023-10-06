package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Topic;

public class EditTextOptionPage extends AppCompatActivity {

    Handler handler = new Handler();

    private static final int PICK_FILE_REQUEST_CODE = 1; // Request code for file picker
    private EditText editField;
    Button save, discard, submitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_option_page);

        editField = findViewById(R.id.editField);

        // For Movement Action of the scrolls
        editField.setMovementMethod(new ScrollingMovementMethod());

        // Discard
        discard = findViewById(R.id.discard_btn);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTextOptionPage.this, home_screen.class);
                startActivity(intent);
            }
        });

        // Getting the extracted text from the upload
        EditText displayText = findViewById(R.id.editField);

        // Get text from intent
        Intent getExtractedText = getIntent(); // initializing get intent
        String extractedText = getExtractedText.getStringExtra("extractedtextData"); // calling the intent named extractedtextData
        displayText.setText(extractedText); // display the intent text in the editText

        // Upload the text after clicking save
        Button saveContext = findViewById(R.id.submitEdit);
        saveContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = ((EditText) findViewById(R.id.content_title_text)).getText().toString();
                String content = ((EditText) findViewById(R.id.editField)).getText().toString();

                Topic newTopic = new Topic(title, content);

                Topic.add(newTopic, new PostProcess() {
                    @Override
                    public void Success(Object... o) {
                        Toast.makeText(EditTextOptionPage.this, "Upload Success", Toast.LENGTH_SHORT);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(EditTextOptionPage.this, home_screen.class);
                                startActivity(intent);
                            }
                        }, 3000);
                    }

                    @Override
                    public void Failed(Exception e) {
                        System.out.print(e.getMessage());
                        Toast.makeText(EditTextOptionPage.this, "Upload Failed", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }
}