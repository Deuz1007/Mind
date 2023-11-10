package com.example.mind.data;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIO {
    public static Socket instance;
    public static TextView quizNotification;
    public static ErrorDialog errorDialog;
    public static boolean isNotificationShowing = false;

    public static void createInstance() throws URISyntaxException {
        instance = IO.socket("https://192.168.18.155:3000");
        instance.connect();

        instance.on("chatgpt", SocketIO::onChatGPT);
        instance.on("error", SocketIO::onError);
    }

    public static void setNotificationBar(TextView notificationBar, ErrorDialog errorPopup) {
        quizNotification = notificationBar;
        if (isNotificationShowing)
            quizNotification.setVisibility(View.VISIBLE);

        errorDialog = errorPopup;
    }

    private static void onChatGPT(Object... args) {
        String userId = (String) args[0];
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId))
            User.collection.get().addOnSuccessListener(snapshot -> {
                try {
                    isNotificationShowing = true;
                    quizNotification.setVisibility(View.VISIBLE);

                    User.current = new User(snapshot);

                    new Handler().postDelayed(() -> {
                        isNotificationShowing = false;
                        quizNotification.setVisibility(View.GONE);
                    }, 5000);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
    }

    private static void onError(Object... args) {
        String userId = (String) args[0];
        String error = (String) args[1];

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId))
            User.collection.get().addOnSuccessListener(snapshot -> {
                errorDialog.setMessage(error);
                errorDialog.show();
            });
    }
}
