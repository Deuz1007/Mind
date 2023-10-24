package com.example.mind;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind.models.Topic;

import java.util.ArrayList;
import java.util.List;

public class GlobalForumAdapter extends RecyclerView.Adapter<GlobalForumAdapter.GlobalForumAdapterHolder> {

    private OnItemClickListener listener;

    // Interface for item click
    public interface OnItemClickListener{
        void onItemClick(int Position);
    }

    // Method for Click Listerer
    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public static class GlobalForumAdapterHolder extends RecyclerView.ViewHolder  {
        TextView contentView;
        ImageView deleteBtn;

        public GlobalForumAdapterHolder(@NonNull View itemView, GlobalForumAdapter.OnItemClickListener listener) {
            super(itemView);
            contentView = itemView.findViewById(R.id.content_title);

//            listener.onItemClick(getAdapterPosition());
        }
    }

    Context context;
    List<Topic> items;
    private List<Integer> selectedItems = new ArrayList<>();

    public GlobalForumAdapter(Context context, List<Topic> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public GlobalForumAdapter.GlobalForumAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.library_content_view, parent, false);

        return new GlobalForumAdapter.GlobalForumAdapterHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GlobalForumAdapter.GlobalForumAdapterHolder holder, int position) {
        holder.contentView.setText(items.get(position).title);

//        holder.deleteBtn.setVisibility(View.GONE);
        holder.contentView.setOnClickListener(view -> {
            // Navigate to the Quiz Content Page
            Intent intent = new Intent(context, GlobalQuizContentPage.class);
            intent.putExtra("topicId", items.get(position).topicId);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
