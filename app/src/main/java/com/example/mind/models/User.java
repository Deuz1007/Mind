package com.example.mind.models;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.FBInstances;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String username;
    public String email;

    public Map<String, Topic> topics;

    public User() {
        this.topics = new HashMap<>();
    }

    public User(DataSnapshot snapshot) {
        this.username = snapshot.child("username").getValue(String.class);
        this.email = snapshot.child("username").getValue(String.class);
        this.topics = snapshot.child("topics").getValue(new GenericTypeIndicator<Map<String, Topic>>() {});
    }

    public User(String username, String email) {
        this();
        this.username = username;
        this.email = email;
    }

    public static User current;
    public static DatabaseReference collection;

    public static void register(User newUser, String password, PostProcess callback) {
        FBInstances.auth
                .createUserWithEmailAndPassword(newUser.email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the uid if the registered user
                        String uid = FBInstances.auth.getCurrentUser().getUid();
                        collection = collection.child(uid);

                        // Save registered user's details
                        collection
                                .setValue(newUser)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Make the registered user the current user
                                        current = newUser;
                                        Topic.collection = collection.child("topics");

                                        callback.Success();
                                    }
                                    else callback.Failed(task1.getException());
                                });
                    }
                    else callback.Failed(task.getException());
                });
    }

    public static void login(String email, String password, PostProcess callback) {
        FBInstances.auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the uid if the logged in user
                        String uid = FBInstances.auth.getCurrentUser().getUid();
                        collection = collection.child(uid);

                        // Get the user details of the logged in user
                        collection
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        // Make the logged in user the current user
                                        current = new User(task1.getResult());
                                        Topic.collection = collection.child("topics");

                                        callback.Success();
                                    }
                                    else callback.Failed(task1.getException());
                                });
                    }
                    else callback.Failed(task.getException());
                });
    }

    public static void resetPassword(User user, PostProcess callback) {
        FBInstances.auth
                .sendPasswordResetEmail(user.email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.Success();
                    else callback.Failed(task.getException());
                });
    }

    public static void logout() {
        FBInstances.auth.signOut();

        // Reset user
        current = null;
    }
}
