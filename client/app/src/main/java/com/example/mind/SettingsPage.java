package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mind.data.SocketIO;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsPage extends AppCompatActivity {

    AlertDialog ad_verify, ad_changePass;

    FirebaseUser authUser;

    SeekBar volumeSeekBar; // to control music volume
    AudioManager audioManager;

    TextView notificationBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        notificationBar = findViewById(R.id.notification);
        SocketIO.setNotificationBar(notificationBar);

        Button btn_change = findViewById(R.id.changepass_btn);
        btn_change.setOnClickListener(view -> ad_verify.show());

        authUser = FirebaseAuth.getInstance().getCurrentUser();

        setVerifyPopup();
        setChangePasswordPopup();

        // Volume Control
        volumeSeekBar = findViewById(R.id.music);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Get Max Volume
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // Get Current Volume
        int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar.setMax(maxVol);
        volumeSeekBar.setProgress(currentVol);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Go back to Home Screen
        Button goBackToHomeScreen = findViewById(R.id.go_back_btn);
        goBackToHomeScreen.setOnClickListener(view ->  {
                Intent intent = new Intent(SettingsPage.this, home_screen.class);
                startActivity(intent);
        });

        Button btn_guide = findViewById(R.id.support_btn);
        btn_guide.setOnClickListener(v -> startActivity(new Intent(this, Instructions_Popup.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar);
    }

    private void setVerifyPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SettingsPage.this).inflate(R.layout.verify_user_page, findViewById(R.id.verify_userview));

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
                        Toast.makeText(SettingsPage.this, "Invalid user credentials", Toast.LENGTH_LONG).show();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SettingsPage.this).inflate(R.layout.activity_change_password, findViewById(R.id.changepassview));

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
                        Toast.makeText(SettingsPage.this, "Password updated", Toast.LENGTH_LONG).show();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}