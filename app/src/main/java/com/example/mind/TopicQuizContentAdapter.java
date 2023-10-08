package com.example.mind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind.models.Quiz;

import java.util.List;

public class TopicQuizContentAdapter extends RecyclerView.Adapter<TopicQuizContentAdapter.QuizContentHolder>{

    Context context;
    List<Quiz> quizzes;

    public TopicQuizContentAdapter(Context context, List<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
    }

    @NonNull
    @Override
    public TopicQuizContentAdapter.QuizContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.topic_quiz_content_view, parent, false);

        return new TopicQuizContentAdapter.QuizContentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicQuizContentAdapter.QuizContentHolder holder, int position) {
        holder.quizcontentView.setText(quizzes.get(position).quizId);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public static class QuizContentHolder extends RecyclerView.ViewHolder  {

        TextView quizcontentView;

        public QuizContentHolder(@NonNull View itemView) {
            super(itemView);
            quizcontentView = itemView.findViewById(R.id.quiz_content);
        }
    }
}
