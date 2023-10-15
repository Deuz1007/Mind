package com.example.mind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mind.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfilePage extends AppCompatActivity {

    AlertDialog ad_verify, ad_changePass;

    FirebaseUser authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        TextView tv_username = findViewById(R.id.display_username);
        TextView tv_email = findViewById(R.id.display_email);

        Button btn_logout = findViewById(R.id.signout_btn);
        Button btn_home = findViewById(R.id.go_back_btn);
        Button btn_change = findViewById(R.id.changepass_btn);

        authUser = FirebaseAuth.getInstance().getCurrentUser();

        setVerifyPopup();
        setChangePasswordPopup();

        tv_username.setText(User.current.username);
        tv_email.setText(User.current.email);

        btn_home.setOnClickListener(view -> startActivity(new Intent(UserProfilePage.this, home_screen.class)));
        btn_change.setOnClickListener(view -> ad_verify.show());

        btn_logout.setOnClickListener(view -> {
            // Logout user
            User.logout();

            Intent intent = new Intent(UserProfilePage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setVerifyPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfilePage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(UserProfilePage.this).inflate(R.layout.verify_user_page, findViewById(R.id.verify_userview));

        EditText et_email = view.findViewById(R.id.email);
        EditText et_password = view.findViewById(R.id.oldpass);
        Button btn_verify = view.findViewById(R.id.verify_btn);

        btn_verify.setOnClickListener(v -> {
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();

            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            authUser.reauthenticate(credential)
                    .addOnSuccessListener(unused -> {
                        // Hide verify popup
                        ad_verify.dismiss();

                        // Show change password popup
                        ad_changePass.show();

                    })
                    .addOnFailureListener(e -> {
                        // Show error message
                        Toast.makeText(UserProfilePage.this, "Invalid user credentials", Toast.LENGTH_LONG).show();

                        et_email.setText("");
                        et_password.setText("");

                        ad_verify.dismiss();
                    });
        });

        builder.setView(view);
        ad_verify = builder.create();

        Window window = ad_verify.getWindow();
        if (window != null)
            window.setBackgroundDrawable(new ColorDrawable(0));
    }

    private void setChangePasswordPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfilePage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(UserProfilePage.this).inflate(R.layout.activity_change_password, findViewById(R.id.changepassview));

        EditText et_newPassword = view.findViewById(R.id.newpassword);
        EditText et_reEnterNewPassword = view.findViewById(R.id.re_enter_newpassword);
        Button btn_changePassword = view.findViewById(R.id.changepass_button);

        btn_changePassword.setOnClickListener(v -> {
            String newPassword = et_newPassword.getText().toString();
            String reEnterNewPassword = et_reEnterNewPassword.getText().toString();

            if (!newPassword.equals(reEnterNewPassword)) {

                // Show error
                Toast.makeText(this, "Passwords are not the same", Toast.LENGTH_LONG).show();
                return;
            }

            // Change password Function
            authUser.updatePassword(newPassword)
                    .addOnSuccessListener(unused -> {
                        // Show success message
                        Toast.makeText(UserProfilePage.this, "Password updated", Toast.LENGTH_LONG).show();

                        et_newPassword.setText("");
                        et_reEnterNewPassword.setText("");

                        // Hide change pass popup
                        ad_changePass.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        // Show error
                        Toast.makeText(this, "Passwords are not changed", Toast.LENGTH_LONG).show();

                        et_newPassword.setText("");
                        et_reEnterNewPassword.setText("");

                        // Hide change pass popup
                        ad_changePass.dismiss();
                    });
        });

        builder.setView(view);
        ad_changePass = builder.create();

        Window window = ad_changePass.getWindow();
        if (window != null)
            window.setBackgroundDrawable(new ColorDrawable(0));
    }
}