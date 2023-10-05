package com.example.mind.models;

import com.example.mind.interfaces.PostProcess;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    public String email;
    public String fullName;
    public String birthDate;
    public Map<String, Topic> topics;

    public User(String fullName, String email, String birthDate) {
        this.email = email;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.topics = new HashMap<>();
    }

    public User(DataSnapshot snapshot) {
        this.email = snapshot.child("email").getValue(String.class);
        this.fullName = snapshot.child("fullName").getValue(String.class);
        this.birthDate = snapshot.child("birthDate").getValue(String.class);
        this.topics = snapshot.child("topics").getValue(new GenericTypeIndicator<Map<String, Topic>>() {});
    }

    public static User current;
    public static DatabaseReference collection;

    public static void initialize(PostProcess callback) {
        // Get the uid if the logged in user
        String uid = FirebaseAuth.getInstance().getUid();

        collection.child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
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

    public static void resetPassword(String email, PostProcess callback) {
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(callback::Success)
                .addOnFailureListener(callback::Failed);
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();

        // Reset user
        current = null;
    }
}
