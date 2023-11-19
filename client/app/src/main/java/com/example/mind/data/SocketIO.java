package com.example.mind.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.home_screen;
import com.example.mind.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIO {
    public static Socket instance;
    public static TextView quizNotification;
    public static ErrorDialog errorDialog;

    public static NotificationManagerCompat notificationManagerCompat;
    public static Notification notification;
    public static boolean isNotificationShowing = false;
    private static final String CHANNEL_ID = "mind";
    private static final int NOTIFICATION_ID = 1;
    public static Context context;

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

    @SuppressLint("MissingPermission")
    private static void onChatGPT(Object... args) {
        String userId = (String) args[0];
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId))
            User.collection.get().addOnSuccessListener(snapshot -> {
                try {
                    isNotificationShowing = true;
                    quizNotification.setVisibility(View.VISIBLE);

                    System.out.println(quizNotification.getContext());

                    context = quizNotification.getContext();
                    showQuizGeneratedNotification(context);

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

    @SuppressLint("MissingPermission")
    public static void showQuizGeneratedNotification(Context context) {
        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Generate a unique CHANNEL_ID
        String channelId = "mind_channel_" + System.currentTimeMillis();

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "MIND",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(context, SocketIO.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("QUIZ GENERATED")
                .setContentText("You may view your quiz now")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        // Use the same notification ID throughout your app to avoid conflicts
        int notificationId = 1;

        // Display the notification
        notificationManager.notify(notificationId, notification);
    }

}
