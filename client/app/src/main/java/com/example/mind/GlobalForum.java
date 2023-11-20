package com.example.mind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.mind.data.SocketIO;
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.dialogs.LoadingDialog;
import com.example.mind.models.Topic;
import com.example.mind.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalForum extends AppCompatActivity {
    public static Map<String, Topic> allTopics;

    ErrorDialog errorDialog;
    TextView notificationBar;
    BackgroundMusicPlayer backgroundMusicPlayer; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_forum);

        notificationBar = findViewById(R.id.notification);
        errorDialog = new ErrorDialog(this);
        SocketIO.setNotificationBar(notificationBar, errorDialog);

        // Initialize the BackgroundMusicPlayer
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.bgm1);
        backgroundMusicPlayer.setVolume(0.5f, 0.5f); // Set the volume to half

        // Container of the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.global_content_items_container);

        allTopics = new HashMap<>();

        // Create loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setPurpose("Gathering all topics...");
        loadingDialog.show();

        errorDialog.setMessage("Failed gathering all topics");

        FirebaseDatabase.getInstance().getReference("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    System.out.println("Entry: 1");
                    Map<String, User> users = snapshot.getValue(new GenericTypeIndicator<Map<String, User>>() {});

                    System.out.println("Entry: 2");

                    allTopics = users.values().stream()
                            .filter(user -> user.topics != null)
                            .flatMap(user -> user.topics.values().stream())
                            .filter(topic -> topic.quizzes.size() > 0)
                            .collect(Collectors.toMap(topic -> topic.topicId, topic -> topic));

                    List<Topic> topics = allTopics.values().stream()
                            .sorted((o1, o2) -> o1.title.compareToIgnoreCase(o2.title))
                            .collect(Collectors.toList());

                    loadingDialog.dismiss();

                    recyclerView.setAdapter(new GlobalForumAdapter(this, topics));
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                })
                .addOnFailureListener(e -> errorDialog.show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
        backgroundMusicPlayer.start(); // Start playing background music when the activity starts
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusicPlayer.pause(); // Pause background music when the activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
        backgroundMusicPlayer.start(); // Resume background music when the activity is resumed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundMusicPlayer.release(); // Release resources when the activity is destroyed
    }
}