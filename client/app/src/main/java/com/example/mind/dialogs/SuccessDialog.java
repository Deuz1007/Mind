package com.example.mind.dialogs;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind.R;

public class SuccessDialog extends CustomDialog {
    private TextView tv_message;

    public SuccessDialog(AppCompatActivity activity) {
        super(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.success_popup_message, null);
        tv_message = view.findViewById(R.id.success_comment);

        this.view = view;
        this.create();
    }

    public void setMessage(String message) { tv_message.setText(message); }
}
