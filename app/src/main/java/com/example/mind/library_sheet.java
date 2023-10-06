package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

public class library_sheet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_sheet);

        // Container of the Recycleview
        RecyclerView recyclerView = findViewById(R.id.content_items_container);

        // To populate content data and creating list

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new LibraryContetntAdapter(getApplicationContext()));

    }

    public void buttonOpenFile(View view){
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            intent = new Intent(Intent.ACTION_VIEW, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
        }
        intent.setType("*/*");
        this.startActivity(intent);
    }
}