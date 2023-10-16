package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mind.models.User;

import java.util.ArrayList;
import java.util.List;

public class library_sheet extends AppCompatActivity {

    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_sheet);

        // BGM
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.bgm1);
        backgroundMusicPlayer.start();

        // Container of the Recycleview
        RecyclerView recyclerView = findViewById(R.id.content_items_container);
        LibraryContentAdapter adapter = (LibraryContentAdapter) recyclerView.getAdapter();

        LibraryContentAdapter contentAdapter = new LibraryContentAdapter(this, new ArrayList<>(User.current.topics.values()));
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Deleting the item in the recycler view
        contentAdapter.setOnItemClickListener(new LibraryContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int Position) {
                // deleting the specific item
                contentAdapter.items.remove(Position);

                // notifying the adapter
                contentAdapter.notifyDataSetChanged();
            }
        });
    }

    public void buttonOpenFile(View view){
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            intent = new Intent(Intent.ACTION_VIEW, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
        }
        intent.setType("*/*");
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}