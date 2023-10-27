package com.example.mind.dialogs;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind.R;

public class QuizGeneratedDialog extends CustomDialog {

    private TextView tv_message;

    public QuizGeneratedDialog(AppCompatActivity activity) {
        super(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.activity_global_popup_dialog, null);
        tv_message = view.findViewById(R.id.notif_text);

        this.view = view;
        this.create(false);

        WindowManager.LayoutParams layoutParams = this.window.getAttributes();
        layoutParams.gravity = Gravity.TOP;

        this.window.setAttributes(layoutParams);
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void setMessage(String message) { tv_message.setText(message); }
}
