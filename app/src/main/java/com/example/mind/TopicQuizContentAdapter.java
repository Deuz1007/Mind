package com.example.mind;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind.models.Quiz;

import java.util.List;

public class TopicQuizContentAdapter extends RecyclerView.Adapter<TopicQuizContentAdapter.QuizContentHolder>{

    Context context;
    List<Quiz> quizzes;

    Dialog quizAnalyticsPopup;

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

        holder.quizcontentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To display upload option popup layout
                quizAnalyticsPopup = new Dialog(context);

                ShowAnalyticsPopup();
            }
        });
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

    public void ShowAnalyticsPopup(){
        quizAnalyticsPopup.setContentView(R.layout.quiz_analytics_popup);

        Button retryBtn = quizAnalyticsPopup.findViewById(R.id.retry_btn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BooleanQuizPage.class);
                context.startActivity(intent);
            }
        });

        quizAnalyticsPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        quizAnalyticsPopup.show();
    }
}
