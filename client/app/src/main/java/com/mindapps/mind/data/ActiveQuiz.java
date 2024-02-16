package com.mindapps.mind.data;

import com.mindapps.mind.QuizResultAdapter;
import com.mindapps.mind.models.Quiz;
import com.mindapps.mind.models.Topic;

import java.util.ArrayList;
import java.util.List;

public class ActiveQuiz {
    public static ActiveQuiz active;

    public Topic topic;
    public Quiz quiz;

    public List<QuizResultAdapter.QuizItemInfo> items;

    public boolean isFromCode;

    public int streak;
    public int hints;
    public int score;

    public ActiveQuiz(Topic topic, Quiz quiz, boolean isFromCode) {
        this.topic = topic;
        this.quiz = quiz;
        this.isFromCode = isFromCode;
        reset();
    }

    public void reset() {
        items = new ArrayList<>();
        streak = 0;
        hints = 0;
        score = 0;
    }

    public boolean updateScore(String response, String answer, String question) {
        response = response.trim().replaceAll("\\s+", " ");
        answer = answer.trim();

        // Check if answer is correct
        boolean isCorrect = response.equalsIgnoreCase(answer);

        // Add new QuizInfoItem to list
        items.add(new QuizResultAdapter.QuizItemInfo(question, answer, response, isCorrect));

        // Check if selected answer is correct
        if (isCorrect) {
            score++; // Add score
            streak++; // Add streak

            // Check if streak counter is divisible by items per level
            if (streak % quiz.itemsPerLevel == 0)
                hints++; // Add hint
        }
        // If incorrect, reset streak to 0
        else streak = 0;

        return isCorrect;
    }
}
