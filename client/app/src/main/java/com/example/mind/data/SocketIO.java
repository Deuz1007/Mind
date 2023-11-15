package com.example.mind.data;

import static androidx.core.app.AppOpsManagerCompat.Api23Impl.getSystemService;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.mind.R;
import com.example.mind.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIO {
    public static Socket instance;
    public static TextView quizNotification;
    public static boolean isNotificationShowing = false;

    public static void createInstance() throws URISyntaxException {
        instance = IO.socket("https://mind-api.onrender.com");
        instance.connect();

        instance.on("chatgpt", SocketIO::onChatGPT);
    }

    public static void setNotificationBar(TextView notificationBar) {
        quizNotification = notificationBar;
        if (isNotificationShowing)
            quizNotification.setVisibility(View.VISIBLE);
    }

    private static void onChatGPT(Object... args) {
        String userId = (String) args[0];
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId))
            User.collection.get().addOnSuccessListener(snapshot -> {
                try {
                    isNotificationShowing = true;
                    quizNotification.setVisibility(View.VISIBLE);

                    // trigger notif
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(quizNotification.getContext(), "NOTIFICATION_CHANNEL");
                    notificationBuilder.setSmallIcon(R.drawable.baseline_lightbulb_circle_24)
                            .setContentTitle("QUIZ GENERATAED")
                            .setContentText("you may view your quiz")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManager notificationManager =
                            NotificationManager getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());

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

    private static void getSystemService(String notificationService) {
    }

    public void makeNotification(){

    }
}
