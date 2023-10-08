package com.example.mind;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind.models.Topic;

public class LibraryContetntAdapter extends RecyclerView.Adapter<LibraryContetntAdapter.LibraryContentViewHolder> {

    Context context;
    List<Topic> items;

    public LibraryContetntAdapter(Context context, List<Topic> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public LibraryContetntAdapter.LibraryContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.library_content_view, parent, false);

        return new LibraryContetntAdapter.LibraryContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryContetntAdapter.LibraryContentViewHolder holder, int position) {
        holder.contentView.setText(items.get(position).title);
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the Quiz Content Page
                Intent intent = new Intent(context, QuizContentPage.class);

                intent.putExtra("topicId", items.get(position).topicId);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class LibraryContentViewHolder extends RecyclerView.ViewHolder  {

        TextView contentView;

        public LibraryContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.content_title);
        }
    }
}
