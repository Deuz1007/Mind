package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mind.models.User;

public class UserProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        // Go back to Home Screen
        Button goBackToHomeScreen = findViewById(R.id.go_back_btn);

        goBackToHomeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfilePage.this, home_screen.class);
                startActivity(intent);
            }
        });

        // Logging out, going to Login Page
        Button signingOut = findViewById(R.id.signout_btn);

        signingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.logout();
                Intent intent = new Intent(UserProfilePage.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}