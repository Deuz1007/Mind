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
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import org.zwobble.mammoth.internal.documents.Text;

import java.text.DecimalFormat;
import java.util.List;

public class TopicQuizContentAdapter extends RecyclerView.Adapter<TopicQuizContentAdapter.QuizContentHolder>{
    public static class QuizContentHolder extends RecyclerView.ViewHolder  {

        TextView quizcontentView;

        public QuizContentHolder(@NonNull View itemView) {
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
    List<QuizItem> quizItems;
    Dialog quizAnalyticsPopup;

    final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public TopicQuizContentAdapter(Context context, List<QuizItem> quizItems) {
        this.context = context;
        this.quizItems = quizItems;
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
        QuizItem quizItem = quizItems.get(position);

        holder.quizcontentView.setText(quizItem.quiz.quizId);
        holder.quizcontentView.setOnClickListener(view -> {
            // To display upload option popup layout
            quizAnalyticsPopup = new Dialog(context);
            // Show popup
            ShowAnalyticsPopup(quizItem);
        });
    }

    @Override
    public int getItemCount() {
        return quizItems.size();
    }

    public void ShowAnalyticsPopup(QuizItem quizItem) {
        quizAnalyticsPopup.setContentView(R.layout.quiz_analytics_popup);

        // Get components
        Button retryBtn = quizAnalyticsPopup.findViewById(R.id.retry_btn);
        TextView tv_itemsPerLevel = quizAnalyticsPopup.findViewById(R.id.perlevel_text);
        TextView tv_average = quizAnalyticsPopup.findViewById(R.id.average_score_text);
        TextView tv_retries = quizAnalyticsPopup.findViewById(R.id.num_retires_text);

        // Set text values
        tv_itemsPerLevel.setText(quizItem.quiz.itemsPerLevel + "");
        tv_average.setText(decimalFormat.format(quizItem.quiz.average));
        tv_retries.setText(quizItem.quiz.retries + "");

        // Button click listener
        retryBtn.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(context, BooleanQuizPage.class);
                intent.putExtra("topicId", quizItem.topic.topicId);
                intent.putExtra("quizId", quizItem.quiz.quizId);
                context.startActivity(intent);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        quizAnalyticsPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        quizAnalyticsPopup.show();
    }
}
