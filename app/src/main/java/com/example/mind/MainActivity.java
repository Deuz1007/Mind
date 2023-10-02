package com.example.mind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Question;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;
import com.example.mind.utilities.AIRequest;
import com.example.mind.utilities.ExtractText;
import com.example.mind.utilities.FBInstances;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FBInstances.auth = FirebaseAuth.getInstance();
        FBInstances.database = FirebaseDatabase.getInstance();

        User.collection = FBInstances.database.getReference("users");

        // Check if there is saved user log in information
        if (FBInstances.auth.getCurrentUser() != null)
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