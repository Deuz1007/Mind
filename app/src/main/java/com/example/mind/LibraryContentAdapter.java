package com.example.mind;

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

public class LibraryContentAdapter extends RecyclerView.Adapter<LibraryContentAdapter.LibraryContentViewHolder> {
    public static class LibraryContentViewHolder extends RecyclerView.ViewHolder  {
        TextView contentView;

        public LibraryContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.content_title);
        }
    }

    Context context;
    List<Topic> items;

    public LibraryContentAdapter(Context context, List<Topic> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public LibraryContentAdapter.LibraryContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.library_content_view, parent, false);

        return new LibraryContentAdapter.LibraryContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryContentAdapter.LibraryContentViewHolder holder, int position) {
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
}
