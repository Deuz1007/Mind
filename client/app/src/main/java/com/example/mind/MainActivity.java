package com.example.mind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.dialogs.LoadingDialog;
import com.example.mind.dialogs.SuccessDialog;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Topic;
import com.example.mind.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Dialog popupDialog;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;

    SuccessDialog successDialog;
    MediaPlayer buttonClickSound;

    AlertDialog alertDialog;

    // Google Signin
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Imaage View
        ImageView googleSigninButton = findViewById(R.id.googleSigninBtn);

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

        // Success dialog
        successDialog = new SuccessDialog(this);

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

        // Forgot password
        Button resetPassword = findViewById(R.id.forgotPassword_btn);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show popup
            }
        });

        Button forgotPassword = findViewById(R.id.forgotPassword_btn);
        forgotPassword.setOnClickListener(v -> {
            showForgotEmailPopup();
        });

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleSigninButton.setOnClickListener(v -> {
            SignInGoogle();
        });
    }

    private void dashboard() {
        startActivity(new Intent(MainActivity.this, home_screen.class));
        finish();
    }

    // Call this function for reset/forgot password
    // A reset link will be sent to the provided email
    private void resetPassword(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    // Show something to user that a reset password link was sent
                    successDialog.setMessage("EMAIL SENT!");
                    successDialog.show();
                })
                .addOnFailureListener(e -> {
                    // Show something to user that send email reset link was failed to send
                    errorDialog.setMessage("Send Failed!");
                    errorDialog.show();
                });
    }

    public void showForgotEmailPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.forgot_password_email_popup, null);

        EditText et_email = view.findViewById(R.id.email_forgotPassword);
        TextView errorMessage = view.findViewById(R.id.error_message_tv);
        Button submitBtn = view.findViewById(R.id.submit_btn);

        errorMessage.setVisibility(View.INVISIBLE);

        submitBtn.setOnClickListener(v -> {
            String email = et_email.getText().toString();

            if (TextUtils.isEmpty(email)) {
                errorMessage.setText("Email is empty");
                errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            resetPassword(email);
        });

        System.out.println("Entry 4");

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    void SignInGoogle()
    {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                dashboard();
            } catch (ApiException e){
                e.printStackTrace();
                System.out.println(e);
            }
        }
    }

}