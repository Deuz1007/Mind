package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.User;
import com.example.mind.utilities.ExtractText;

public class home_screen extends AppCompatActivity {

    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM & sound effect

    Button libraryButton; // For Library Bottom Sheet
    Dialog popupDialog;

    final int FILE_PICKER_REQUEST_CODE = 1;
    final int CAMERA_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // BGM
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.bgm1);
        backgroundMusicPlayer.start();

        // To Open the library bottom sheet
        libraryButton = findViewById(R.id.library_btn);
        libraryButton.setOnClickListener(view -> {
            startActivity(new Intent(this, library_sheet.class));
        });

        // Go to Profile Page
        Button goToProfile = findViewById(R.id.userprofile_btn);
        goToProfile.setText(User.current.username);
        goToProfile.setOnClickListener(view -> {
            startActivity(new Intent(this, UserProfilePage.class));
        });

        // Go to Settings Page
        Button gotoSettings = findViewById(R.id.settings_btn);
        gotoSettings.setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsPage.class));
        });

        // Go to Analytics Page
        Button gotoAnalytics = findViewById(R.id.analytics_btn);
        System.out.println(gotoAnalytics);
        gotoAnalytics.setOnClickListener(view -> {
            System.out.println("pressed");
            startActivity(new Intent(this, AnalyticsPage.class));
        });
        gotoAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(home_screen.this, QuizContentPage.class));
            }
        });

        // To display upload option popup layout
        popupDialog = new Dialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            try {
                String mimetype = home_screen.this.getContentResolver().getType(selectedFileUri);

                if (mimetype.contains("image")) {
                    ExtractText.Image(home_screen.this, selectedFileUri, new PostProcess() {
                        @Override
                        public void Success(Object... o) {
                            Intent intent = new Intent(home_screen.this, EditTextOptionPage.class);
                            intent.putExtra("extractedText", (String) o[0]);
                            startActivity(intent);
                        }

                        @Override
                        public void Failed(Exception e) {
                            Toast.makeText(home_screen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String extractedText = null;

                    switch (mimetype) {
                        case "application/pdf":
                            extractedText = ExtractText.PDF(this, selectedFileUri);
                            break;
                        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                            extractedText = ExtractText.Word(this, selectedFileUri);
                            break;
                    }

                    Intent intent = new Intent(this, EditTextOptionPage.class);
                    intent.putExtra("extractedText", extractedText);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Show popup "Are you sure to end quiz the quiz? The progress won't save"
        // Implement popup here

        AlertDialog.Builder builder = new AlertDialog.Builder(home_screen.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(home_screen.this).inflate(R.layout.exit_quiz_popup, (LinearLayout) findViewById(R.id.exit_popup));

        builder.setView(view);
        ((TextView) view.findViewById(R.id.quit_comment)).setText("Exiting Already?");

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.yes_btn).setOnClickListener(View -> {
            finish();
            System.exit(0);
        });

        view.findViewById(R.id.no_btn).setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void ShowUploadOption(View view) {
        popupDialog.setContentView(R.layout.upload_option_popup);

        // Uploading File
        Button uploadFileOption = popupDialog.findViewById(R.id.uplaod_option);
        uploadFileOption.setOnClickListener(view13 -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Set MIME types for PDF, Word, and image files
            String[] mimeTypes = {
                    "application/pdf",
                    "image/*",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            // Start the file picker activity
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
        });

        // Go to Capture Page for Capture Option
        Button goToCapture = popupDialog.findViewById(R.id.capture_option);
        goToCapture.setOnClickListener(view1 -> startActivity(new Intent(this, Capture_ShowImage_Page.class)));

        Button editFileOption = popupDialog.findViewById(R.id.edit_option);
        editFileOption.setOnClickListener(view1 -> startActivity(new Intent(this, EditTextOptionPage.class)));

        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}