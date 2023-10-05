package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User.setStatics();

        // Check if there is saved user log in information
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            // If so, use that to login user
            User.initialize(new PostProcess() {
                @Override
                public void Success(Object... o) {
                    // Proceed to dashboard
//                    dashboard();
                }

                @Override
                public void Failed(Exception e) {
                    // Failed login with saved user info
                }
            });

        // Going to Home Screen
        Button login = findViewById(R.id.login_button);

        login.setOnClickListener(view -> {
            String email = "sample@gmail.com";
            String password = "Testing!123";
            User.login(email, password, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    // Success login
                    dashboard();
                }

                @Override
                public void Failed(Exception e) {
                    // Display error
                }
            });
        });

        // Go to Register Page
        Button goToRegister = findViewById(R.id.create_account_btn);

        goToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterPage.class);
            startActivity(intent);
        });

//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE},
//                PackageManager.PERMISSION_GRANTED);
    }

    private void dashboard() {
        Intent intent = new Intent(MainActivity.this, home_screen.class);
        startActivity(intent);
    }
}