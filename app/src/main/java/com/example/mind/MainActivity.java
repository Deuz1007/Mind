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

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Handler handler; // For delaying the process
    AlertDialog alertDialog;
    TextView textLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextInputLayouts
        TextInputLayout emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);

        // Setup user defaults
        User.setStatics();

        // Check if there is saved user log in information
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Show logging in popup
            loggingIn();

            // Login user
            User.initialize(new PostProcess() {
                @Override
                public void Success(Object... o) {
                    dashboard();
                }

                @Override
                public void Failed(Exception e) {
                    // Failed login with saved user info
                }
            });
        }

        // Going to Home Screen
        Button login = findViewById(R.id.login_button);

        login.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();
            User.login(email, password, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    loggingIn();
                }

                @Override
                public void Failed(Exception e) {
                    // Display error
//                    Toast.makeText(MainActivity.this, "user not registered", Toast.LENGTH_SHORT).show();
                    String email = emailEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        emailTextInputLayout.setError("Email is required");
                    } else if (!isValidEmail(email)) {
                        emailTextInputLayout.setError("Invalid email address");
                    } else {
                        emailTextInputLayout.setError(null); // Clear the error
                    }
                }
            });
        });

        // Go to Register Page
        Button goToRegister = findViewById(R.id.create_account_btn);

        goToRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterPage.class)));
    }

    private void dashboard() {
        startActivity(new Intent(MainActivity.this, home_screen.class));
        finish();
    }

    public void loggingIn(){
        // Loading Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false); // Prevent user from dismissing the dialog
        alertDialog = dialogBuilder.create();
        alertDialog.show();

        textLoading = dialogView.findViewById(R.id.loding_purpose);
        textLoading.setText("Logging in...");
    }

    // Function to validate email
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}