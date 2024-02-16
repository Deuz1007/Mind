package com.mindapps.mind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizResultAdapter extends RecyclerView.Adapter<QuizResultAdapter.DetailHolder>{
    Context context;
    List<QuizItemInfo> quizItems;

    public QuizResultAdapter(Context context, List<QuizItemInfo> quizItems) {
        this.context = context;
        this.quizItems = quizItems;
    }

    @NonNull
    @Override
    public QuizResultAdapter.DetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.quiz_result_details_view, parent, false);

        return new QuizResultAdapter.DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailHolder holder, int position) {
        QuizItemInfo item = quizItems.get(position);

        holder.correctView.setText(item.answer);
        holder.userAnswerView.setText(item.response);
        holder.questionView.setText(item.question);

        int color = item.isCorrect ? R.color.saturated : R.color.wrong_bg_color;
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, color));
    }

    @Override
    public int getItemCount() {
        return quizItems.size();
    }

    public static class DetailHolder extends RecyclerView.ViewHolder  {

        TextView questionView;
        TextView correctView;
        TextView userAnswerView;

        CardView cardView;

        public DetailHolder(@NonNull View itemView) {
            super(itemView);
            questionView = itemView.findViewById(R.id.question_item);
            correctView = itemView.findViewById(R.id.correct_items);
            userAnswerView = itemView.findViewById(R.id.user_answer_item);

            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public static class QuizItemInfo {
        public String question;
        public String answer;
        public String response;
        public boolean isCorrect;

        public QuizItemInfo(String question, String answer, String response, boolean isCorrect) {
            this.question = question;
            this.answer = answer;
            this.response = response;
            this.isCorrect = isCorrect;
        }
    }
}
