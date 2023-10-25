package com.example.mind.dialogs;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind.R;

public class UpperDialog extends CustomDialog {

    private TextView tv_message;

    public UpperDialog(AppCompatActivity activity) {
        super(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.activity_global_popup_dialog, null);
        tv_message = view.findViewById(R.id.notif_text);

        this.view = view;

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        window.setAttributes(layoutParams);

        this.create();
    }

    public void setMessage(String message) { tv_message.setText(message); }
}
