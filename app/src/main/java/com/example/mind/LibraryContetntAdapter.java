package com.example.mind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryContetntAdapter extends RecyclerView.Adapter<LibraryContentViewHolder> {

    Context context;
    List<LibraryContentItem> items;

    public LibraryContetntAdapter(Context context, List<LibraryContentItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public LibraryContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryContentViewHolder(LayoutInflater.from(context).inflate(R.layout.library_content_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryContentViewHolder holder, int position) {
        holder.contentView.setText(items.get(position).getContentname());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
