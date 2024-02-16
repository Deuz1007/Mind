package com.mindapps.mind.dialogs;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mindapps.mind.R;

public class ErrorDialog extends CustomDialog {
    private TextView tv_message;

    public ErrorDialog(AppCompatActivity activity) {
        super(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.error_popup_message, null);
        tv_message = view.findViewById(R.id.quit_comment);

        this.view = view;
        this.create();
    }

    public void setMessage(String message) { tv_message.setText(message); }
}
