package com.example.mind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CorrectResultAdapter extends RecyclerView.Adapter<CorrectResultAdapter.DetailHolder>{

    Context context;

    @NonNull
    @Override
    public CorrectResultAdapter.DetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.quiz_result_details_view, parent, false);

        return new CorrectResultAdapter.DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailHolder holder, int position) {
        // Set Text inside the CardView

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class DetailHolder extends RecyclerView.ViewHolder  {

        TextView questionView;
        TextView correctView;
        TextView userAnswerView;

        public DetailHolder(@NonNull View itemView) {
            super(itemView);
            questionView = itemView.findViewById(R.id.question_item);
            correctView = itemView.findViewById(R.id.correct_items);
            userAnswerView = itemView.findViewById(R.id.user_answer_item);
        }
    }

}
