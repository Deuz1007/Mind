package com.example.mind.models;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.UniqueID;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz {
    public String quizId;
    public int score;
    public int itemsPerLevel;
    public Map<String, Question> questions;

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

    public Map<Question.QuestionType, List<Question>> getGroupedQuestions() {
        List<Question> level1 = new ArrayList<>();
        List<Question> level2 = new ArrayList<>();
        List<Question> level3 = new ArrayList<>();

        for (Question question : questions.values()) {
            switch (question.type) {
                case TRUE_OR_FALSE:
                    level1.add(question);
                    break;
                case MULTIPLE_CHOICE:
                    level2.add(question);
                    break;
                case IDENTIFICATION:
                    level3.add(question);
            }
        }

        Map<Question.QuestionType, List<Question>> grouped = new HashMap<>();
        grouped.put(Question.QuestionType.TRUE_OR_FALSE, level1);
        grouped.put(Question.QuestionType.MULTIPLE_CHOICE, level2);
        grouped.put(Question.QuestionType.IDENTIFICATION, level3);

        return grouped;
    }

    public String createContent(String content, String description, String xml) {
        return "With this given content:\\n\\n" + content +
                "\\n\\nWrite me a " + itemsPerLevel + " " + description +
                ", written in this xml format:\\n\\n" + xml +
                "\\n\\nCompile all generated questions inside only one <response></response>";
    }

    public static class Description {
        public static final String MULTIPLE_CHOICE = "multiple choice questions with 4 choices for each question";
        public static final String TRUE_OR_FALSE = "true or false questions";
        public static final String IDENTIFICATION =  "identification questions with a 1 to 3 words answer";
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
