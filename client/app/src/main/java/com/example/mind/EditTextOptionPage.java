package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.example.mind.data.SocketIO;
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.dialogs.LoadingDialog;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Topic;

public class EditTextOptionPage extends AppCompatActivity {
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    TextView notificationBar;
    MediaPlayer buttonClickSound;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_option_page);

        // Initialize button click sound
        buttonClickSound = MediaPlayer.create(this, R.raw.btn_click3);

        notificationBar = findViewById(R.id.notification);
        errorDialog = new ErrorDialog(this);
        SocketIO.setNotificationBar(notificationBar, errorDialog);

        /* Get components */

        // Edit text
        EditText et_content = findViewById(R.id.editField);
        EditText et_title = findViewById(R.id.content_title_text);

        // Button
        Button discard = findViewById(R.id.discard_btn);
        Button saveContext = findViewById(R.id.submitEdit);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setPurpose("Saving topic...");

        // For Movement Action of the scrolls
        et_content.setMovementMethod(new ScrollingMovementMethod());

        // Get text from intent
        String extractedText = getIntent().getStringExtra("extractedText");
        et_content.setText(extractedText);

        // Discard
        discard.setOnClickListener(view -> {
            buttonClickSound.start();

            startActivity(new Intent(this, home_screen.class));
            finish();
        });

        saveContext.setOnClickListener(view -> {
            buttonClickSound.start();

            String title = et_title.getText().toString().trim();
            String content = et_content.getText().toString().trim();

            String inputError = null;

            if (TextUtils.isEmpty(title)) inputError = "Topic title is required";
            if (TextUtils.isEmpty(content)) inputError = "Topic content is required";

            if (inputError != null) {
                errorDialog.setMessage(inputError);
                errorDialog.show();

                 return;
            }

            loadingDialog.show();
            Topic newTopic = new Topic(title, content);
            Topic.add(newTopic, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    // Play button click sound effect
                    buttonClickSound.start();
                    startActivity(new Intent(EditTextOptionPage.this, home_screen.class));
                }

                @Override
                public void Failed(Exception e) {
                    loadingDialog.dismiss();

                    errorDialog.setMessage("Saving topic failed");
                    errorDialog.show();
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }
}