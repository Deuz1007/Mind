package com.example.mind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mind.exceptions.FileSizeLimitException;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.ExtractText;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

public class home_screen extends AppCompatActivity {
    Button libraryButton; // For Library Bottom Sheet
    Dialog popupDialog;

    final int FILE_PICKER_REQUEST_CODE = 1;
    final int CAMERA_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // To Open the library bottom sheet
        libraryButton = findViewById(R.id.library_btn);
        libraryButton.setOnClickListener(view -> startActivity(new Intent(this, library_sheet.class)));

        // Go to Profile Page
        Button goToProfile = findViewById(R.id.userprofile_btn);
        goToProfile.setOnClickListener(view -> startActivity(new Intent(this, UserProfilePage.class)));

        // Go to Settings Page
        Button gotoSettings = findViewById(R.id.settings_btn);
        gotoSettings.setOnClickListener(view -> startActivity(new Intent(this, SettingsPage.class)));

        // Go to Analytics Page
        Button gotoAnalytics = findViewById(R.id.analytics_btn);
        gotoAnalytics.setOnClickListener(view -> startActivity(new Intent(this, AnalyticsPage.class)));

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

                if (!mimetype.contains("image")) {
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
                }
                else {
                    String extractedText = null;

                    switch(mimetype) {
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
            }
            catch(Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    public void ShowUploadOption(View view){
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
}