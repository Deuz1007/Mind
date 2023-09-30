package com.example.mind.models;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.AIRequest;
import com.example.mind.utilities.ParseXML;
import com.example.mind.utilities.UniqueID;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

public class Topic {
    public String topicId;
    public String title;
    public String content;
    public Map<String, Quiz> quizzes;

    public Topic() {
        this.quizzes = new HashMap<>();
        this.topicId = UniqueID.generate();
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

    public static DatabaseReference collection;

    public static void add(Topic newTopic, PostProcess callback) {
        collection
                .child(newTopic.topicId)
                .setValue(newTopic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save new topic
                        User.current.topics.put(newTopic.topicId, newTopic);

                        callback.Success();
                    }
                    else callback.Failed(task.getException());
                });
    }

    public void createQuiz(int itemsPerLevel, PostProcess callback) throws IOException, ParserConfigurationException, SAXException {
        // Create new quiz
        Quiz newQuiz = new Quiz();
        // Assign the itemsPerLevel
        newQuiz.itemsPerLevel = itemsPerLevel;

        // Generate level 1 questions (multiple choice)
        List<Question> level1 = ParseXML.parse(
                Question.QuestionType.MULTIPLE_CHOICE,
                AIRequest.send(newQuiz.createContent(
                        content,
                        Quiz.Description.MULTIPLE_CHOICE,
                        Quiz.XML.HAS_CHOICES
                ))
        );

        // Generate level 2 questions (true or false)
        List<Question> level2 = ParseXML.parse(
                Question.QuestionType.TRUE_OR_FALSE,
                AIRequest.send(newQuiz.createContent(
                        content,
                        Quiz.Description.TRUE_OR_FALSE,
                        Quiz.XML.HAS_CHOICES
                ))
        );

        // Generate level 3 questions (identification)
        List<Question> level3 = ParseXML.parse(
                Question.QuestionType.IDENTIFICATION,
                AIRequest.send(newQuiz.createContent(
                        content,
                        Quiz.Description.IDENTIFICATION,
                        Quiz.XML.HAS_CHOICES
                ))
        );

        // Combine all questions
        level1.addAll(level2);
        level1.addAll(level3);

        // Save the questions to the quiz
        for (Question question : level1)
            newQuiz.questions.put(question.questionId, question);

        // Add the new quiz
        Quiz.add(newQuiz, this, callback);
    }

    public void createQuiz(PostProcess callback) throws IOException, ParserConfigurationException, SAXException {
        createQuiz(10, callback);
    }
}
