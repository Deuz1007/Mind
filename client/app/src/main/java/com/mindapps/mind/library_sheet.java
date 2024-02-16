package com.mindapps.mind;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mindapps.mind.data.SocketIO;
import com.mindapps.mind.dialogs.ErrorDialog;
import com.mindapps.mind.models.Topic;
import com.mindapps.mind.models.User;

import java.util.ArrayList;
import java.util.List;

public class library_sheet extends AppCompatActivity {
    private BackgroundMusicPlayer backgroundMusicPlayer;
    private TextView notificationBar;
    private ErrorDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_sheet);

        notificationBar = findViewById(R.id.notification);
        errorDialog = new ErrorDialog(this);
        SocketIO.setNotificationBar(notificationBar, errorDialog);

        // Initialize BackgroundMusicPlayer
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.bgm1);

        // Container of the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.content_items_container);

        List<Topic> topics = new ArrayList<>();
        if (User.current.topics != null)
            topics = new ArrayList<>(User.current.topics.values());

        LibraryContentAdapter contentAdapter = new LibraryContentAdapter(this, topics);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(library_sheet.this, home_screen.class));
        finish();
    }

    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
        backgroundMusicPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusicPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
        backgroundMusicPlayer.start();
    }
}