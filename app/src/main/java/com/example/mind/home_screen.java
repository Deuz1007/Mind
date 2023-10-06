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
    Button popupOption; // For popup Upload option button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        // To Open the library bottom sheet
        libraryButton = findViewById(R.id.library_btn);
        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLibrary();

            }
        });

        // Go to Profile Page
        Button goToProfile = findViewById(R.id.userprofile_btn);

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen.this, UserProfilePage.class);
                startActivity(intent);
            }
        });

        // Go to Settings Page
        Button gotoSettings = findViewById(R.id.settings_btn);

        gotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen.this, SettingsPage.class);
                startActivity(intent);
            }
        });

        // Go to Analytics Page
        Button gotoAnalytics = findViewById(R.id.analytics_btn);

        gotoAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen.this, AnalyticsPage.class);
                startActivity(intent);
            }
        });

        // Go to Capture Page
//        Button goToCapture = findViewById(R.id.capture_button);

//        goToCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(home_screen.this, Capture_ShowImage_Page.class);
//                startActivity(intent);
//            }
//        });

        // To display upload option popup layout
        popupDialog = new Dialog(this);

    }

    // To Open Library Bottom Sheet
    private void showLibrary() {

        final Dialog library = new Dialog(this);
        library.requestWindowFeature(Window.FEATURE_NO_TITLE);
        library.setContentView(R.layout.activity_library_sheet);

        library.show();
        library.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        library.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        library.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        library.getWindow().setGravity(Gravity.BOTTOM);

        // Show Quiz Content
        Button showContent = library.findViewById(R.id.ict);

        showContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen.this, QuizContentPage.class);
                startActivity(intent);

            }
        });

    }

    // For Uploading File
//    public void buttonOpenFile(View view){
//        Intent intent = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            intent = new Intent(Intent.ACTION_VIEW, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
//        }
//        intent.setType("*/*");
//        this.startActivity(intent);
//    }

    // Upload File
//    public void buttonOpenFile(View view) {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        // Set MIME types for PDF, Word, and image files
//        String[] mimeTypes = {"application/pdf", "application/msword", "image/*"};
//        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//
//        // Start the file picker activity
//        startActivityForResult(intent, 1);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedFileUri = data.getData();

                // Handle the selected file (e.g., open, display, or process it)
//                openSelectedFile(selectedFileUri);

                final String[] extractedText = new String[1];
                try {
                    extractedText[0] = "";

                    System.out.println(home_screen.this.getContentResolver().getType(selectedFileUri));

                    switch(home_screen.this.getContentResolver().getType(selectedFileUri)) {
                        case "application/pdf":
                            extractedText[0] = ExtractText.PDF(home_screen.this, selectedFileUri);
                            break;
                        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                            extractedText[0] = ExtractText.Word(home_screen.this, selectedFileUri);
                            break;
                        default:
                            ExtractText.Image(home_screen.this, selectedFileUri, new PostProcess() {
                                @Override
                                public void Success(Object... o) {
                                    extractedText[0] = (String) o[0];
                                    System.out.println(extractedText[0]);
                                }

                                @Override
                                public void Failed(Exception e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(home_screen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    }

                }
                catch(Exception e) {
                    System.out.println(e.getMessage());
                    Toast.makeText(home_screen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void openSelectedFile(Uri fileUri) {
        // Here, you can implement code to open, display, or process the selected file.
        // For example, you can use Intent.ACTION_VIEW to open it with the appropriate application.
        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
        openFileIntent.setData(fileUri);
        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Make sure to catch any ActivityNotFoundException
        try {
            startActivity(openFileIntent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(home_screen.this, "Unnexpected File Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void ShowUploadOption(View view){
        popupDialog.setContentView(R.layout.upload_option_popup);

        // Uploading File
        Button uploadFileOption = (Button) popupDialog.findViewById(R.id.uplaod_option);
        uploadFileOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                startActivityForResult(intent, 1);
            }
        });

        // Go to Capture Page for Capture Option
        Button goToCapture = (Button) popupDialog.findViewById(R.id.capture_option);
        goToCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen.this, Capture_ShowImage_Page.class);
                startActivity(intent);
            }
        });

        Button editFileOption = (Button) popupDialog.findViewById(R.id.edit_option);
        final int PICK_FILE_REQUEST_CODE = 1;
        editFileOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen.this, EditTextOptionPage.class);
                startActivity(intent);
            }
        });

        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }
}