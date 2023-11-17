package com.example.mind.data;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mind.R;
import com.example.mind.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIO{
    public static Socket instance;
    public static TextView quizNotification;
    public static ErrorDialog errorDialog;
    public static boolean isNotificationShowing = false;
    private static final String CHANNEL_ID = "mind";
    private static final int NOTIFICATION_ID = 1;


    public static void createInstance() throws URISyntaxException {
        instance = IO.socket("https://mind-api.onrender.com");
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

                    System.out.println(quizNotification.getContext());

                    // trigger notif
                    // Create a notification manager
                    NotificationManager notificationManager = (NotificationManager) quizNotification.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                    // Create a notification channel (required for Android Oreo and above)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(
                                CHANNEL_ID,
                                "MIND",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    // Build the notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(quizNotification.getContext(), CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentTitle("QUIZ GENERATED")
                            .setContentText("you may view your quiz now")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    // Show the notification
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

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
