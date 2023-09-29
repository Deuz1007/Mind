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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

public class home_screen extends AppCompatActivity {
    // For Library Bottom Sheet
    Button libraryButton;

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

        // Go to Capture Page
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
        Button goToCapture = findViewById(R.id.capture_button);

        goToCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_screen.this, Capture_ShowImage_Page.class);
                startActivity(intent);
            }
        });
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

    }

    public void buttonOpenFile(View view){
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            intent = new Intent(Intent.ACTION_VIEW, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
        }
        intent.setType("*/*");
        this.startActivity(intent);
    }
}