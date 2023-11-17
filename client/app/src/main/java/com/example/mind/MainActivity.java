package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.dialogs.LoadingDialog;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    MediaPlayer buttonClickSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EditText
        EditText et_password = findViewById(R.id.password);

        // TextInputLayouts
        TextInputLayout emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        TextInputLayout passwordTextInputLayout = findViewById(R.id.passwordContainer);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);

        // Setup user defaults
        User.setStatics();

        // Setup loading popup
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setPurpose("Logging in");

        // Error dialog
        errorDialog = new ErrorDialog(this);

        // Button click sound
        buttonClickSound = MediaPlayer.create(this, R.raw.btn_click3);

        // Check if there is saved user log in information
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Show logging in popup
            loadingDialog.show();

            // Login user
            User.initialize(new PostProcess() {
                @Override
                public void Success(Object... o) {
                    if (User.current.email != null) {
                        dashboard();
                        return;
                    }

                    User.logout();
                    loadingDialog.dismiss();
                }

                @Override
                public void Failed(Exception e) {
                    loadingDialog.dismiss();

                    errorDialog.setMessage("User login failed. Please login again.");
                    errorDialog.show();
                }
            });
        }

        // Going to Home Screen
        Button login = findViewById(R.id.login_button);

        login.setOnClickListener(view -> {
            buttonClickSound.start();

            String email = emailEditText.getText().toString();
            String password = et_password.getText().toString();

            String emailError = null;
            String passwordError = null;

            if (TextUtils.isEmpty(email)) emailError = "Email is required";
            else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) emailError = "Invalid email ";

            if (TextUtils.isEmpty(password)) passwordError = "Password is empty";

            if (emailError != null || passwordError != null) {
                emailTextInputLayout.setError(emailError);
                passwordTextInputLayout.setError(passwordError);

                return;
            }

            loadingDialog.show();
            User.login(email, password, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    dashboard();
                }

                @Override
                public void Failed(Exception e) {
                    loadingDialog.dismiss();

                    errorDialog.setMessage("User login failed.");
                    errorDialog.show();
                }
            });
        });

        // Go to Register Page
        Button goToRegister = findViewById(R.id.create_account_btn);

        goToRegister.setOnClickListener(v -> {
            // Play button click sound effect
            buttonClickSound.start();

            startActivity(new Intent(MainActivity.this, RegisterPage.class));
        });
    }

    private void dashboard() {
        startActivity(new Intent(MainActivity.this, home_screen.class));
        finish();
    }
}