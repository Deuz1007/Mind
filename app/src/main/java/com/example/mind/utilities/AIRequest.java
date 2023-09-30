package com.example.mind.utilities;

import com.example.mind.BuildConfig;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIRequest {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("<response>(.|\\n)*<\\/response>", Pattern.CASE_INSENSITIVE);

    public static String send(String content) throws IOException {
        // Format the json body
        String json = "{" +
                    "'model': 'gpt-3.5-turbo'," +
                    "'messages': [{ 'role': 'user', 'content': '" + content + "' }]" +
                "}";

        // Create a request body
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        // Build the request
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + BuildConfig.CHATGPT_KEY)
                .post(body)
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            // Return response body as string
            String responseBody = response.body().string();

            // Create a matcher for the response body
            Matcher matcher = RESPONSE_PATTERN.matcher(responseBody);
            // Check if there is matched with the pattern;
            if (matcher.find())
                // If so, return the matched string
                return matcher.group();
            // Otherwise, return null
            return null;
        }
    }
}
