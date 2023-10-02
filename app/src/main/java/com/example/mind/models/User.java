package com.example.mind.models;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.FBInstances;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
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

    public static void initialize(PostProcess callback) {
        // Get the uid if the logged in user
        String uid = FBInstances.auth.getUid();

        collection.child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    collection = collection.child(uid);

                    loadUser(snapshot);
                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void register(User newUser, String password, PostProcess callback) {
        FBInstances.auth
                .createUserWithEmailAndPassword(newUser.email, password)
                .addOnSuccessListener(authResult -> {
                    // Get the uid if the registered user
                    String uid = authResult.getUser().getUid();
                    collection = collection.child(uid);

                    // Save registered user's details
                    collection
                            .setValue(newUser)
                            .addOnSuccessListener(unused -> {
                                // Make the registered user the current user
                                current = newUser;
                                Topic.collection = collection.child("topics");

                                callback.Success();
                            })
                            .addOnFailureListener(callback::Failed);
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void login(String email, String password, PostProcess callback) {
        FBInstances.auth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Get the uid if the logged in user
                    String uid = authResult.getUser().getUid();
                    collection = collection.child(uid);

                    // Get the user details of the logged in user
                    collection.get()
                            .addOnSuccessListener(snapshot -> {
                                loadUser(snapshot);
                                callback.Success();
                            })
                            .addOnFailureListener(callback::Failed);
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void resetPassword(User user, PostProcess callback) {
        FBInstances.auth
                .sendPasswordResetEmail(user.email)
                .addOnSuccessListener(callback::Success)
                .addOnFailureListener(callback::Failed);
    }

    public static void logout() {
        FBInstances.auth.signOut();

        // Reset user
        current = null;
    }

    private static void loadUser(DataSnapshot snapshot) {
        // Get the user value of the snapshot and make it the current user
        current = snapshot.getValue(User.class);
        Topic.collection = collection.child("topics");
    }
}
