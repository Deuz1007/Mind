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
    Dialog quizAnalyticsPopup;

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

    public void ShowAnalyticsPopup(GlobalTopicQuizContentAdapter.QuizItem quizItem) {
        quizAnalyticsPopup.setContentView(R.layout.global_quiz_analytics_popup);

        // Get components
        Button startBtn = quizAnalyticsPopup.findViewById(R.id.start_btn);
        Button shareBtn = quizAnalyticsPopup.findViewById(R.id.share_btn);
        TextView tv_itemsPerLevel = quizAnalyticsPopup.findViewById(R.id.perlevel_text);
        TextView tv_average = quizAnalyticsPopup.findViewById(R.id.average_score_text);
        TextView tv_retries = quizAnalyticsPopup.findViewById(R.id.num_retires_text);

        // Set text values
        tv_itemsPerLevel.setText(quizItem.quiz.itemsPerLevel + "");
        tv_average.setText(decimalFormat.format(quizItem.quiz.average));
        tv_retries.setText(quizItem.quiz.retries + "");

        // Button click listener
        startBtn.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(context, BooleanQuizPage.class);
                intent.putExtra("topicId", quizItem.topic.topicId);
                intent.putExtra("quizId", quizItem.quiz.quizId);
                context.startActivity(intent);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        shareBtn.setOnClickListener(view -> {
            // Create quiz code
            String quizCode = FirebaseAuth.getInstance().getUid() + quizItem.topic.topicId + quizItem.quiz.quizId;

            // Past code to clipboard
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("quizCode", quizCode);
            clipboardManager.setPrimaryClip(clipData);

            // Show toast
            Toast.makeText(context, "Quiz code copied!", Toast.LENGTH_LONG).show();
        });

        quizAnalyticsPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        quizAnalyticsPopup.show();
    }
}
