package com.mindapps.mind.models;

import com.mindapps.mind.exceptions.MaxContentTokensReachedException;
import com.mindapps.mind.interfaces.PostProcess;
import com.mindapps.mind.utilities.UniqueID;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Map;

public class Quiz {
    public String quizId;
    public int itemsPerLevel;
    public double average;
    public int retries;
    public Map<String, Question> questions;

    public Quiz() {
        this.quizId = UniqueID.generate();
        this.average = 0;
        this.retries = 0;
        this.questions = new HashMap<>();
    }

    public Quiz(int itemsPerLevel) {
        this();
        this.itemsPerLevel = itemsPerLevel;
    }

    public Quiz(DataSnapshot snapshot) {
        this.quizId = snapshot.child("quizId").getValue(String.class);
        this.itemsPerLevel = snapshot.child("itemsPerLevel").getValue(int.class);
        this.average = snapshot.child("average").getValue(double.class);
        this.retries = snapshot.child("retries").getValue(int.class);
        this.questions = snapshot.child("questions").getValue(new GenericTypeIndicator<Map<String, Question>>() {});
    }

    private static DatabaseReference getCollection(Topic topic, Quiz quiz) {
        return Topic.getCollection(topic)
                .child("quizzes")
                .child(quiz.quizId);
    }

    public static void saveScore(Quiz quiz, int score, Topic topic, PostProcess callback) {
        int retries = quiz.retries == 0 ? 1 : quiz.retries + 1;
        double average = quiz.retries == 0 ? score : (quiz.average * quiz.retries + score) / retries;

        quiz.retries = retries;
        quiz.average = average;

        // Save updates
        getCollection(topic, quiz)
                .setValue(quiz)
                .addOnSuccessListener(unused -> {
                    // Replace old quiz data with new data
                    User.current.topics.get(topic.topicId).quizzes.replace(quiz.quizId, quiz);

                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }

    public static void add(Quiz newQuiz, Topic topic, PostProcess callback) {
        Quiz.getCollection(topic, newQuiz)
                .setValue(newQuiz)
                .addOnSuccessListener(unused -> {
                    // Save new quiz
                    User.current
                            .topics.get(topic.topicId)
                            .quizzes.put(newQuiz.quizId, newQuiz);

                    callback.Success(newQuiz);
                })
                .addOnFailureListener(callback::Failed);
    }

    public static String[] contentToTokenArray(String content) {
        return content.split("\\W+");
    }

    public static boolean isValidContent(String content) {
        // Check if the quizContent exceeds token max length
        return contentToTokenArray(content).length <= MaxContentTokensReachedException.MAX_TOKEN;
    }
}
