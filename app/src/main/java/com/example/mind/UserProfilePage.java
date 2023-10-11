package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mind.models.User;

public class UserProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        TextView tv_username = findViewById(R.id.display_username);
        TextView tv_email = findViewById(R.id.display_email);

        Button btn_logout = findViewById(R.id.signout_btn);
        Button btn_home = findViewById(R.id.go_back_btn);

        tv_username.setText(User.current.username);
        tv_email.setText(User.current.email);

        btn_home.setOnClickListener(view -> startActivity(new Intent(UserProfilePage.this, home_screen.class)));

        btn_logout.setOnClickListener(view -> {
            // Logout user
            User.logout();

            Intent intent = new Intent(UserProfilePage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
}