package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import androidx.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EditTextOptionPage extends AppCompatActivity {

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
    }
}