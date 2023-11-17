package com.example.mind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mind.data.SocketIO;
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import android.media.MediaPlayer;
import android.widget.Toast;

public class UserProfilePage extends AppCompatActivity {

    AlertDialog ad_editUser;
    TextView notificationBar;
    ErrorDialog errorDialog;
    FirebaseUser authUser;
    Dialog popupDialog;

    private void playButtonClickSound() {
        MediaPlayer buttonClickSound = MediaPlayer.create(this, R.raw.btn_click3);
        if (buttonClickSound != null) {
            buttonClickSound.start();
        }
    }

    int totalTopics;
    int totalQuizzes;
    int totalRetries;
    double average;
    double accuracy;

    final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    BackgroundMusicPlayer backgroundMusicPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        // Initialize BackgroundMusicPlayer
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.bgm1);

        TextView tv_username = findViewById(R.id.display_username);
        TextView tv_email = findViewById(R.id.display_email);
        TextView tv_topics = findViewById(R.id.total_generated_topics);
        TextView tv_quizzes = findViewById(R.id.total_generated_quiz);
        TextView tv_retries = findViewById(R.id.total_retry);
        TextView tv_average = findViewById(R.id.total_average_score);
        TextView tv_accuracy = findViewById(R.id.answers_accuracy);
        notificationBar = findViewById(R.id.notification);

        Button btn_logout = findViewById(R.id.signout_btn);
        Button btn_home = findViewById(R.id.go_back_btn);
        Button btn_edit = findViewById(R.id.edit_username_email_btn);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        errorDialog = new ErrorDialog(this);

        calculateAnalytics();

        tv_username.setText(User.current.username);
        tv_email.setText(User.current.email);
        tv_topics.setText(totalTopics + "");
        tv_quizzes.setText(totalQuizzes + "");
        tv_retries.setText(totalRetries + "");
        tv_average.setText(decimalFormat.format(average));
        tv_accuracy.setText(decimalFormat.format(accuracy) + "%");

        tv_username.setText(User.current.username);
        tv_email.setText(User.current.email);

        SocketIO.setNotificationBar(notificationBar, errorDialog);

        btn_home.setOnClickListener(view -> {
            playButtonClickSound();
            startActivity(new Intent(UserProfilePage.this, home_screen.class));
        });

        btn_edit.setOnClickListener(view -> {
            playButtonClickSound();
            ad_editUser.show();
        });

        setEditPopup();


        btn_logout.setOnClickListener(view -> {
            playButtonClickSound();
            // Logout user
            User.logout();

            Intent intent = new Intent(UserProfilePage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        popupDialog = new Dialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
        backgroundMusicPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
        backgroundMusicPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusicPlayer.pause();
    }

    private void calculateAnalytics() {
        // Get total topics by topics size
        totalTopics = User.current.topics.size();

        // Initiate temporary values
        totalQuizzes = 0;
        totalRetries = 0;
        average = 0;

        if (totalTopics == 0) {
            accuracy = 0;
            return;
        }

        double totalAccuracy = 0;

        // Traverse to each topic
        for (Topic topic : User.current.topics.values()) {
            // Increment the total quizzes by the quizzes size
            totalQuizzes += topic.quizzes.size();

            // Traverse to each quiz
            for (Quiz quiz : topic.quizzes.values()) {
                // Increment the values respectively
                totalRetries += quiz.retries;
                average += quiz.average;
                // Accuracy = average / no. of items
                totalAccuracy += quiz.average / quiz.questions.size();
            }
        }

        if (totalTopics == 0) {
            accuracy = 0;
            return;
        }

        // The temporary value of average is the sum of each quiz's average
        // To get the average, divide the total average by the total quiz size
        average /= totalQuizzes;
        // To get the accuracy, divide the total accuracy by the total quiz size
        // You'll get the decimal value. Multiply it by 100 to get the percentage
        accuracy = totalAccuracy / totalQuizzes * 100;
    }

    public void setEditPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfilePage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(UserProfilePage.this).inflate(R.layout.edit_username_email_popup, findViewById(R.id.editview));

        EditText et_newUsername = view.findViewById(R.id.new_username_text);
        EditText et_newEmail = view.findViewById(R.id.new_email_text);
        Button btn_saveEdit = view.findViewById(R.id.save_edit_btn);

        btn_saveEdit.setOnClickListener(v -> {
            // Show popup for user input password
            popupDialog.findViewById(R.id.save_edit_btn);
            showAuth();

            // Starting from this line, code below may migrate inside the onclick listener of the button in the password popup
            // Please move it if necessary

            // Get the user input
            String email = et_newEmail.getText().toString().trim();
            String password = "" /* GET PASSWORD FROM EDIT TEXT */;
            String username = et_newUsername.getText().toString().trim();


            PostProcess callback = new PostProcess() {
                @Override
                public void Success(Object... o) {
                    Toast.makeText(UserProfilePage.this, "Update successfully", Toast.LENGTH_LONG).show();
                }

                @Override
                public void Failed(Exception e) {
                    errorDialog.setMessage("Failed to update details");
                    errorDialog.show();
                }
            };

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
//
//            user.reauthenticate(credential)
//                    .addOnSuccessListener(unused -> User.updateEmailAndUserName(email, username, callback))
//                    .addOnFailureListener(callback::Failed);
        });

        builder.setView(view);
        ad_editUser = builder.create();

        Window window = ad_editUser.getWindow();
        if (window != null)
            window.setBackgroundDrawable(new ColorDrawable(0));
    }

    public void showAuth(){
        popupDialog.setContentView(R.layout.edit_username_email_auth);

        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }
}