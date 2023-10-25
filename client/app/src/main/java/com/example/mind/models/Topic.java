package com.example.mind.models;

import com.example.mind.exceptions.MaxContentTokensReachedException;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.UniqueID;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Topic {
    public String topicId;
    public String title;
    public String content;
    public Map<String, Quiz> quizzes;

    public Topic() {
        this.topicId = UniqueID.generate();
        this.quizzes = new HashMap<>();
    }

    public Topic(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    public Topic(DataSnapshot snapshot) {
        this.topicId = snapshot.child("topicId").getValue(String.class);
        this.title = snapshot.child("title").getValue(String.class);
        this.content = snapshot.child("content").getValue(String.class);
        this.quizzes = snapshot.child("quizzes").getValue(new GenericTypeIndicator<Map<String, Quiz>>() {});
    }

    public static DatabaseReference getCollection(Topic topic) {
        return User.collection
                .child("topics")
                .child(topic.topicId);
    }

    public static void editTopic(Topic topic, String title, String content, PostProcess callback) {
        Map<String, String> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("content", content);

        getCollection(topic)
                .setValue(updates)
                .addOnSuccessListener(unused -> {
                    User.current.topics.get(topic.topicId).title = title;
                    User.current.topics.get(topic.topicId).content = content;

                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void removeTopic(Topic topic, PostProcess callback) {
        getCollection(topic).removeValue()
                .addOnSuccessListener(unused -> {
                    User.current.topics.remove(topic.topicId);

                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }

    public static JSONObject createQuizData(Topic topic, String quizContent, int itemsPerLevel) throws MaxContentTokensReachedException, JSONException {
        // Check if the quizContent exceeds token max length
        if (!Quiz.isValidContent(quizContent))
            throw new MaxContentTokensReachedException();

        JSONObject quizData = new JSONObject();
        quizData.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        quizData.put("topicId", topic.topicId);
        quizData.put("content", quizContent.replaceAll("\\n", " ").replaceAll("\\s+", " "));
        quizData.put("items", itemsPerLevel);

        return quizData;
    }

    public static void add(Topic newTopic, PostProcess callback) {
        getCollection(newTopic)
                .setValue(newTopic)
                .addOnSuccessListener(unused -> {
                    // Save new topic
                    User.current.topics.put(newTopic.topicId, newTopic);

                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }
}
