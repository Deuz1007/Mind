package com.example.mind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_forum);

        // Container of the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.global_content_items_container);

        allTopics = new HashMap<>();

        // Create loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setPurpose("Gathering all topics...");
        loadingDialog.show();

        errorDialog = new ErrorDialog(this);
        errorDialog.setMessage("Failed gathering all topics");

        FirebaseDatabase.getInstance().getReference("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Map<String, User> users = snapshot.getValue(new GenericTypeIndicator<Map<String, User>>() {});

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
        SocketIO.currentActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.currentActivity = this;
    }
}