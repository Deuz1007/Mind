package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mind.data.SocketIO;
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.dialogs.LoadingDialog;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.User;
import com.example.mind.utilities.ExtractText;

public class home_screen extends AppCompatActivity {
    Dialog popupDialog;
    ErrorDialog errorDialog;
    LoadingDialog loadingDialog;
    TextView notificationBar;

    final int FILE_PICKER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button btn_library = findViewById(R.id.library_btn);
        Button btn_profile = findViewById(R.id.userprofile_btn);
        Button btn_settings = findViewById(R.id.settings_btn);
        Button btn_global = findViewById(R.id.global_btn);

        errorDialog = new ErrorDialog(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setPurpose("Extracting text...");

        notificationBar = findViewById(R.id.notification);
        SocketIO.setNotificationBar(notificationBar);

        btn_profile.setText(User.current.username);

        btn_profile.setOnClickListener(view -> startActivity(new Intent(this, UserProfilePage.class)));
        btn_library.setOnClickListener(view -> startActivity(new Intent(this, library_sheet.class)));
        btn_settings.setOnClickListener(view -> startActivity(new Intent(this, SettingsPage.class)));
        btn_global.setOnClickListener(view -> startActivity(new Intent(this, GlobalForum.class)));

        // To display upload option popup layout
        popupDialog = new Dialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            try {
                String mimetype = home_screen.this.getContentResolver().getType(selectedFileUri);

                if (mimetype.contains("image")) {
                    loadingDialog.show();

                    ExtractText.Image(home_screen.this, selectedFileUri, new PostProcess() {
                        @Override
                        public void Success(Object... o) {
                            Intent intent = new Intent(home_screen.this, EditTextOptionPage.class);
                            intent.putExtra("extractedText", (String) o[0]);
                            startActivity(intent);
                        }

                        @Override
                        public void Failed(Exception e) {
                            errorDialog.setMessage("Image processing failed");
                            errorDialog.show();
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
                errorDialog.setMessage("File upload error");
                errorDialog.show();
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

    public void ShowEnterQuizCode(View view) {
        popupDialog.setContentView(R.layout.quiz_code_popup);

        // EditText of the Quiz Code
        EditText et_quizCode = popupDialog.findViewById(R.id.quiz_code);

        // Identify the Code
        Button enterCode = popupDialog.findViewById(R.id.enter_code);
        enterCode.setOnClickListener(v -> {
            String quizCode = et_quizCode.getText().toString();

            Intent intent = new Intent(home_screen.this, BooleanQuizPage.class);
            intent.putExtra("code", quizCode);

//            backgroundMusicPlayer.stop();

            startActivity(intent);
            finish();
        });

        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }

}
