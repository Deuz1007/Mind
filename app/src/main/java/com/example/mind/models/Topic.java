package com.example.mind.models;

import com.example.mind.exceptions.MaxContentTokensReachedException;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.AIRequest;
import com.example.mind.utilities.UniqueID;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.List;
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

    public DatabaseReference getCollection() {
        return User.collection
                .child("topics")
                .child(topicId);
    }

    public void editTopic(String newTitle, String newContent, PostProcess callback) {
        Map<String, String> updates = new HashMap<>();
        updates.put("title", newTitle);
        updates.put("content", newContent);

        getCollection().setValue(updates)
                .addOnSuccessListener(unused -> {
                    title = newTitle;
                    content = newContent;

                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }

    public void createQuiz(String quizContent, int itemsPerLevel, PostProcess callback) throws MaxContentTokensReachedException {
        // Check if the quizContent exceeds token max length
        if (quizContent.split("\\W+").length > MaxContentTokensReachedException.MAX_TOKEN)
            throw new MaxContentTokensReachedException();

        // Create new quiz
        Quiz newQuiz = new Quiz(itemsPerLevel);

        /* Create requests */

        // Level 1
        AIRequest.QuestionRequest level1 = new AIRequest.QuestionRequest(
                Question.QuestionType.TRUE_OR_FALSE,
                AIRequest.createRequest(newQuiz.createContent(
                        quizContent,
                        Quiz.Description.TRUE_OR_FALSE,
                        Quiz.XML.ANSWER_ONLY
                ))
        );

        // Level 2
        AIRequest.QuestionRequest level2 = new AIRequest.QuestionRequest(
                Question.QuestionType.MULTIPLE_CHOICE,
                AIRequest.createRequest(newQuiz.createContent(
                        quizContent,
                        Quiz.Description.MULTIPLE_CHOICE,
                        Quiz.XML.HAS_CHOICES
                ))
        );

        // Level 3
        AIRequest.QuestionRequest level3 = new AIRequest.QuestionRequest(
                Question.QuestionType.IDENTIFICATION,
                AIRequest.createRequest(newQuiz.createContent(
                        quizContent,
                        Quiz.Description.IDENTIFICATION,
                        Quiz.XML.ANSWER_ONLY
                ))
        );

        // Send requests
        AIRequest.send(
                new AIRequest.QuestionRequest[]{ level1, level2, level3 },
                new PostProcess() {
                    @Override
                    public void Success(Object... o) {
                        // Extract the questions
                        for (Object obj : (List<?>) o[0]) {
                            Question question = (Question) obj;

                            newQuiz.questions.put(question.questionId, question);
                        }

                        // Save new quiz to database
                        Quiz.add(newQuiz, Topic.this, callback);
                    }

                    @Override
                    public void Failed(Exception e) {
                        callback.Failed(e);
                    }
                }
        );
    }

    public static void add(Topic newTopic, PostProcess callback) {
//        System.out.print(newTopic.getCollection() == null);

        newTopic.getCollection()
                .setValue(newTopic)
                .addOnSuccessListener(unused -> {
                    // Save new topic
                    User.current.topics.put(newTopic.topicId, newTopic);

                    callback.Success();
                })
                .addOnFailureListener(callback::Failed);
    }
}
