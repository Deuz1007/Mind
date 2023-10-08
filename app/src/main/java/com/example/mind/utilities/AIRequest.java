package com.example.mind.utilities;

import androidx.annotation.NonNull;

import com.example.mind.BuildConfig;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIRequest {
    private static final OkHttpClient client = new OkHttpClient.Builder().readTimeout(150, TimeUnit.SECONDS).build();
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("<response>(.|\\n)*<\\/response>", Pattern.CASE_INSENSITIVE);
    private static final String BEARER = "Bearer " + BuildConfig.CHATGPT_KEY;

    public static class QuestionRequest {
        public Request request;
        public Question.QuestionType type;
        public QuestionRequest(Question.QuestionType type, Request request) {
            this.request = request;
            this.type = type;
        }
    }

    public static Request createRequest(String content) {
        // Format the json body
        String payload = "{\"model\":\"gpt-3.5-turbo-16k\",\"messages\":[{\"role\":\"user\",\"content\":\"" + content + "\"}],\"temperature\":0}";

        // Create a request body
        RequestBody body = RequestBody.create(payload, MediaType.get("application/json; charset=utf-8"));

        // Build and return the request
        return new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", BEARER)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
    }

    public static void send(QuestionRequest[] requests, PostProcess callback) {
        // Mapping for the generated questions per question type
        Map<Question.QuestionType, List<Question>> generatedQuestions = new HashMap<>();

        System.out.println("Sending request...");

        // Loop through each question request
        for (QuestionRequest questionRequest : requests)
            // Initiate the request
            client.newCall(questionRequest.request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // Get the response body
                    String responseBody = response.body().string();

                    System.out.println(responseBody);

                    // Create a matcher for the response body
                    Matcher matcher = RESPONSE_PATTERN.matcher(responseBody);
                    // Check if there is matched with the pattern;
                    if (matcher.find())
                        try {
                            // Save the generated questions to the mapping
                            generatedQuestions.put(questionRequest.type, ParseXML.parse(questionRequest.type, matcher.group()));

                            // Check if all the questions for each question types are generated
                            if (generatedQuestions.size() == 3) {
                                // If so, compile all the generated questions into single list

                                // Create list
                                List<Question> questions = new ArrayList<>();

                                // Append the level 1 questions
                                questions.addAll(generatedQuestions.get(Question.QuestionType.TRUE_OR_FALSE));

                                // Append the level 2 questions
                                questions.addAll(generatedQuestions.get(Question.QuestionType.MULTIPLE_CHOICE));

                                // Append the level 3 questions
                                questions.addAll(generatedQuestions.get(Question.QuestionType.IDENTIFICATION));

                                // Call the success callback with the list of questions
                                callback.Success(questions);
                            }
                            else System.out.println("Generated questions: " + generatedQuestions.size());
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                            callback.Failed(e);
                        }
                    else {
                        System.out.println("No response found");
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.out.println(e.getMessage());
                    callback.Failed(e);
                }
            });
    }
}
