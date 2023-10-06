package com.example.mind;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryContentViewHolder extends RecyclerView.ViewHolder  {

    TextView contentView;

    public LibraryContentViewHolder(@NonNull View itemView) {
        super(itemView);
        contentView = itemView.findViewById(R.id.content_container);
    }
}
