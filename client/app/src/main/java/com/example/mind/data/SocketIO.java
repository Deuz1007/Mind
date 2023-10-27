package com.example.mind.data;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind.dialogs.QuizGeneratedDialog;
import com.example.mind.interfaces.Include;
import com.example.mind.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIO {
    public static Socket instance;
    public static AppCompatActivity currentActivity;

    private static QuizGeneratedDialog dialog;

    public static void createInstance() throws URISyntaxException {
//        instance = IO.socket("http://192.168.1.4:3000");
        instance = IO.socket("https://mind-api.onrender.com");
        instance.connect();

        instance.on("chatgpt", args -> {
            String userId = (String) args[0];
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId))
                User.collection.get().addOnSuccessListener(snapshot -> {
                    // Show dialog
                    dialog = new QuizGeneratedDialog(currentActivity);
                    dialog.setMessage("Quiz generated");
                    dialog.show();

                    User.current = new User(snapshot);

                    new Handler().postDelayed(() -> {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }, 3000);
                });
        });
    }

    public static void onDataError(Include include) {
        instance.on("data_error", include::execute);
    }
}
