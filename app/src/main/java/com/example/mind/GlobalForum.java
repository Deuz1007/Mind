package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mind.models.User;

import java.util.ArrayList;

public class GlobalForum extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_forum);

        // Container of the Recycleview
        RecyclerView recyclerView = findViewById(R.id.global_content_items_container);
        LibraryContentAdapter adapter = (LibraryContentAdapter) recyclerView.getAdapter();

        LibraryContentAdapter contentAdapter = new LibraryContentAdapter(this, new ArrayList<>(User.current.topics.values()));
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}