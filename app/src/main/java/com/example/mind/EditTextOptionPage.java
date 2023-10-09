package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Topic;

public class EditTextOptionPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_option_page);

        /* Get components */

        // Edit text
        EditText et_content = findViewById(R.id.editField);
        EditText et_title = findViewById(R.id.content_title_text);

        // Button
        Button discard = findViewById(R.id.discard_btn);
        Button saveContext = findViewById(R.id.submitEdit);

        // For Movement Action of the scrolls
        et_content.setMovementMethod(new ScrollingMovementMethod());

        // Get text from intent
        String extractedText = getIntent().getStringExtra("extractedText");
        et_content.setText(extractedText);

        // Discard
        discard.setOnClickListener(view -> {
            startActivity(new Intent(this, home_screen.class));
            finish();
        });

        saveContext.setOnClickListener(view -> {
            String title = et_title.getText().toString();
            String content = et_content.getText().toString();

            Topic newTopic = new Topic(title, content);

            Topic.add(newTopic, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    Toast.makeText(EditTextOptionPage.this, "Topic saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditTextOptionPage.this, home_screen.class));
                }

                @Override
                public void Failed(Exception e) {
                    Toast.makeText(EditTextOptionPage.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}