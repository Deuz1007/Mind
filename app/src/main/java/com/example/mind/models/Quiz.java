package com.example.mind.models;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.UniqueID;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Map;

public class Quiz {
    public String quizId;
    public int score;
    public int itemsPerLevel;
    public Map<String, Question> questions;

    public static class Description {
        public static final String MULTIPLE_CHOICE = "multiple choice questions with 2 choices for each question";
        public static final String TRUE_OR_FALSE = "true or false questions";
        public static final String IDENTIFICATION =  "identification questions with a 1 to 3 words answer";
    }

    public static class XML {
        public static final String HAS_CHOICES = "<item>\n" +
                "<question></question>\n" +
                "<choice></choice>\n" +
                "<choice></choice>\n" +
                "<answer>CHOICE INDEX</answer>\n" +
                "</item>";
        public static final String ANSWER_ONLY = "<item>\n" +
                "<question></question>\n" +
                "<answer></answer>\n" +
                "</item>";
    }

    public Quiz() {
        this.quizId = UniqueID.generate();
        this.score = 0;
        this.questions = new HashMap<>();
    }

    public Quiz(DataSnapshot snapshot) {
        this.quizId = snapshot.child("quizId").getValue(String.class);
        this.score = snapshot.child("score").getValue(int.class);
        this.itemsPerLevel = snapshot.child("itemsPerLevel").getValue(int.class);
        this.questions = snapshot.child("questions").getValue(new GenericTypeIndicator<Map<String, Question>>() {});
    }

    public String createContent(String content, String description, String xml) {
        return "With this given content:\n\n" + content +
                "\n\nWrite me a " + itemsPerLevel + " " + description +
                ", written in this xml format:\n\n" + xml +
                "\n\nCompile all generated questions inside only one <response></response>";
    }

    public static void add(Quiz newQuiz, Topic topic, PostProcess callback) {
        Topic.collection
                .child(topic.topicId)
                .child("quizzes")
                .child(newQuiz.quizId)
                .setValue(newQuiz)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save new quiz
                        User.current
                                .topics.get(topic.topicId)
                                .quizzes.put(newQuiz.quizId, newQuiz);

                        callback.Success();
                    }
                    else callback.Failed(task.getException());
                });
    }
}
