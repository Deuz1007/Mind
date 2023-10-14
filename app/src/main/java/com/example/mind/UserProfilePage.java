package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mind.models.User;

public class UserProfilePage extends AppCompatActivity {

    Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        TextView tv_username = findViewById(R.id.display_username);
        TextView tv_email = findViewById(R.id.display_email);

        Button btn_logout = findViewById(R.id.signout_btn);
        Button btn_home = findViewById(R.id.go_back_btn);
        Button btn_change = findViewById(R.id.changepass_btn);

        tv_username.setText(User.current.username);
        tv_email.setText(User.current.email);

        btn_home.setOnClickListener(view -> startActivity(new Intent(UserProfilePage.this, home_screen.class)));

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowChangePass();
            }
        });

        btn_logout.setOnClickListener(view -> {
            // Logout user
            User.logout();

            Intent intent = new Intent(UserProfilePage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }

    public void ShowChangePass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfilePage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(UserProfilePage.this).inflate(R.layout.activity_change_password, (LinearLayout) findViewById(R.id.changepassview));

        builder.setView(view);
        EditText newpass = view.findViewById(R.id.newpassword);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.changepass_button).setOnClickListener(View -> {
            // Change password Function
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}