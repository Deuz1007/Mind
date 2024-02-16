package com.mindapps.mind.models;

import com.mindapps.mind.data.SocketIO;
import com.mindapps.mind.exceptions.InvalidQuizCodeException;
import com.mindapps.mind.interfaces.PostProcess;
import com.mindapps.mind.utilities.UniqueID;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String email;
    public String username;
    public String fullName;
    public String birthDate;
    public Map<String, Topic> topics;

    public User() {}

    public User(String fullName, String username, String email, String birthDate) {
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.birthDate = birthDate;
        this.topics = new HashMap<>();
    }

    public User(DataSnapshot snapshot) {
        this.email = snapshot.child("email").getValue(String.class);
        this.fullName = snapshot.child("fullName").getValue(String.class);
        this.username = snapshot.child("username").getValue(String.class);
        this.birthDate = snapshot.child("birthDate").getValue(String.class);
        this.topics = snapshot.child("topics").getValue(new GenericTypeIndicator<Map<String, Topic>>() {});
    }

    public static User current;
    public static DatabaseReference collection;

    public static void setStatics() {
        current = null;
        collection = FirebaseDatabase.getInstance().getReference("users");
    }

    public static void initialize(PostProcess callback) {
        // Get the uid if the logged in user
        String uid = FirebaseAuth.getInstance().getUid();

        collection.child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    try {
                        SocketIO.createInstance();
                    } catch (Exception e) {
                        callback.Failed(e);
                        return;
                    }

                    collection = collection.child(uid);
                    // Get the user value of the snapshot and make it the current user
                    current = new User(snapshot);

                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void register(User newUser, String password, PostProcess callback) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(newUser.email, password)
                .addOnSuccessListener(authResult -> {
                    try {
                        SocketIO.createInstance();
                    } catch (Exception e) {
                        callback.Failed(e);
                        return;
                    }

                    // Get the uid if the registered user
                    String uid = authResult.getUser().getUid();
                    collection = collection.child(uid);

                    // Save registered user's details
                    collection
                            .setValue(newUser)
                            .addOnSuccessListener(unused -> {
                                // Make the registered user the current user
                                current = newUser;

                                callback.Success();
                            })
                            .addOnFailureListener(callback::Failed);
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void login(String email, String password, PostProcess callback) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    try {
                        SocketIO.createInstance();
                    } catch (Exception e) {
                        callback.Failed(e);
                        return;
                    }

                    // Get the uid if the logged in user
                    String uid = authResult.getUser().getUid();
                    collection = collection.child(uid);

                    // Get the user details of the logged in user
                    collection.get()
                            .addOnSuccessListener(snapshot -> {
                                // Get the user value of the snapshot and make it the current user
                                current = new User(snapshot);

                                callback.Success();
                            })
                            .addOnFailureListener(callback::Failed);
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();

        // Reset values
        setStatics();
    }

    public static void updateEmailAndUserName(String email, String username, PostProcess callback) {
        Map<String, Object> update = new HashMap<>();
        update.put("username", username);
        update.put("email", email);

        collection.updateChildren(update)
                .addOnSuccessListener(unused1 -> {
                    User.current.email = email;
                    User.current.username = username;
                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void getUser(String code, PostProcess callback) {
        try {
            int quizStart = code.length() - UniqueID.BYTES_LENGTH;
            int topicStart = quizStart - UniqueID.BYTES_LENGTH;
            String userId = code.substring(0, topicStart);

            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        User user = new User(snapshot);
                        Topic topic = user.topics.get(code.substring(topicStart, quizStart));

                        if (topic != null) {
                            Quiz quiz = topic.quizzes.get(code.substring(quizStart));

                            if (quiz != null) {
                                callback.Success(quiz, topic);
                                return;
                            }
                        }

                        callback.Failed(new InvalidQuizCodeException());
                    })
                    .addOnFailureListener(callback::Failed);
        } catch (Exception e) {
            callback.Failed(e);
        }
    }
}
