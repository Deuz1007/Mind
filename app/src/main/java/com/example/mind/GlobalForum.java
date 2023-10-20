package com.example.mind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_forum);

        allTopics = new HashMap<>();

        // Container of the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.global_content_items_container);

        // Create loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(this);

        // Show loading
        loadingDialog.setPurpose("Gathering all topics...");
        loadingDialog.show();

        FirebaseDatabase.getInstance().getReference("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Map<String, User> users = snapshot.getValue(new GenericTypeIndicator<Map<String, User>>() {});
                    for (User user : users.values()) {
                        if (user.topics == null) continue;

                        for (Topic topic : user.topics.values())
                            allTopics.put(topic.topicId, topic);
                    }

                    List<Topic> topics = allTopics.values().stream()
                            .sorted((o1, o2) -> o1.title.compareToIgnoreCase(o2.title))
                            .collect(Collectors.toList());

                    loadingDialog.dismiss();

                    recyclerView.setAdapter(new GlobalForumAdapter(this, topics));
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                })
                .addOnFailureListener(e -> {
                    // Show error
                    System.out.println(e.getMessage());
                });
    }
}