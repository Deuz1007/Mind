package com.example.mind;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.List;

public class GlobalTopicQuizContentAdapter extends RecyclerView.Adapter<GlobalTopicQuizContentAdapter.GlobalTopicQuizContentHolder> {
    public static class GlobalTopicQuizContentHolder extends RecyclerView.ViewHolder  {

        TextView quizcontentView;

        public GlobalTopicQuizContentHolder(@NonNull View itemView) {
            super(itemView);
            quizcontentView = itemView.findViewById(R.id.quiz_content);
        }
    }

    public static class QuizItem {
        public Topic topic;
        public Quiz quiz;

        public QuizItem(Topic topic, Quiz quiz) {
            this.topic = topic;
            this.quiz = quiz;
        }
    }

    Context context;
    List<GlobalTopicQuizContentAdapter.QuizItem> quizItems;
    Dialog quizStartPopup;

    final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public GlobalTopicQuizContentAdapter(Context context, List<GlobalTopicQuizContentAdapter.QuizItem> quizItems) {
        this.context = context;
        this.quizItems = quizItems;
    }

    @NonNull
    @Override
    public GlobalTopicQuizContentAdapter.GlobalTopicQuizContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.topic_quiz_content_view, parent, false);

        return new GlobalTopicQuizContentAdapter.GlobalTopicQuizContentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlobalTopicQuizContentAdapter.GlobalTopicQuizContentHolder holder, int position) {
        GlobalTopicQuizContentAdapter.QuizItem quizItem = quizItems.get(position);

        holder.quizcontentView.setText(quizItem.quiz.quizId);
        holder.quizcontentView.setOnClickListener(view -> {
            quizStartPopup = new Dialog(context);
            quizStartPopup.setContentView(R.layout.global_quiz_analytics_popup);

            TextView tv_itemsCount = quizStartPopup.findViewById(R.id.itemsCount);
            Button btn_start = quizStartPopup.findViewById(R.id.retry_btn);

            tv_itemsCount.setText(quizItem.quiz.questions.size() + "");

            btn_start.setOnClickListener(v -> {
                Intent intent = new Intent(context, BooleanQuizPage.class);
                intent.putExtra("global", quizItem.topic.topicId + "^" + quizItem.quiz.quizId);
                context.startActivity(intent);
            });

            quizStartPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            quizStartPopup.show();
        });
    }

    @Override
    public int getItemCount() {
        return quizItems.size();
    }
}
