package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mind.data.SocketIO;
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import java.util.ArrayList;
import java.util.List;

public class library_sheet extends AppCompatActivity {

    TextView notificationBar;
    ErrorDialog errorDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_sheet);

        notificationBar = findViewById(R.id.notification);
        errorDialog = new ErrorDialog(this);
        SocketIO.setNotificationBar(notificationBar, errorDialog);

        // Container of the Recycleview
        RecyclerView recyclerView = findViewById(R.id.content_items_container);

        List<Topic> topics = new ArrayList<>();
        if (User.current.topics != null)
            topics = new ArrayList<>(User.current.topics.values());

        LibraryContentAdapter contentAdapter = new LibraryContentAdapter(this, topics);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }
}