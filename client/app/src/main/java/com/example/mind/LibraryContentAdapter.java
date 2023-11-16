package com.example.mind;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind.models.Topic;

public class LibraryContentAdapter extends RecyclerView.Adapter<LibraryContentAdapter.LibraryContentViewHolder> {
    MediaPlayer buttonClickSound;
    private OnItemClickListener listener;

    // Interface for item click
    public interface OnItemClickListener{
        void onItemClick(int Position);
    }

    // Method for Click Listerer
    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public static class LibraryContentViewHolder extends RecyclerView.ViewHolder  {
        TextView contentView;
        ImageView deleteBtn;

        public LibraryContentViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            contentView = itemView.findViewById(R.id.content_title);

//            listener.onItemClick(getAdapterPosition());
        }
    }

    Context context;
    List<Topic> items;
    private List<Integer> selectedItems = new ArrayList<>();

    public LibraryContentAdapter(Context context, List<Topic> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public LibraryContentAdapter.LibraryContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.library_content_view, parent, false);

        return new LibraryContentAdapter.LibraryContentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryContentAdapter.LibraryContentViewHolder holder, int position) {
        holder.contentView.setText(items.get(position).title);

        // Set click listener for content view
        holder.contentView.setOnClickListener(view -> {
            // Play button click sound effect
            MediaPlayer buttonClickSound = MediaPlayer.create(context, R.raw.btn_click3);
            buttonClickSound.start();

            // Navigate to the Quiz Content Page
            Intent intent = new Intent(context, QuizContentPage.class);
            intent.putExtra("topicId", items.get(position).topicId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
