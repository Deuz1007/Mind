package com.example.mind.models;

import com.example.mind.exceptions.MaxContentTokensReachedException;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.UniqueID;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static class Description {
        public static final String MULTIPLE_CHOICE = "multiple choice questions with 4 choices for each question";
        public static final String TRUE_OR_FALSE = "true or false questions";
        public static final String IDENTIFICATION =  "identification questions strictly with only 1 to 3 words as an answer";
    }

    public static class XML {
        public static final String HAS_CHOICES = "<item>\\n" +
                "<question></question>\\n" +
                "<choice></choice>\\n" +
                "<answer>CHOICE INDEX (0-3)</answer>\\n" +
                "</item>";
        public static final String ANSWER_ONLY = "<item>\\n" +
                "<question></question>\\n" +
                "<answer></answer>\\n" +
                "</item>";
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

    public static String createContent(int itemsPerLevel, String content, String description, String xml) {
        return "With this given content:\\n\\n" + content +
                "\\n\\nWrite me a " + itemsPerLevel + " " + description +
                ", written in this xml format:\\n\\n" + xml +
                "\\n\\nCompile all generated questions inside only one <response></response>";
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

    private static String[] contentToTokenArray(String content) {
        return content.split("\\W+");
    }

    public static boolean isValidContent(String content) {
        // Check if the quizContent exceeds token max length
        return contentToTokenArray(content).length <= MaxContentTokensReachedException.MAX_TOKEN;
    }
}
