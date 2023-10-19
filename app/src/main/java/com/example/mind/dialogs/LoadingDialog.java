package com.example.mind.dialogs;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind.R;

public class LoadingDialog extends CustomDialog {
    private TextView tv_purpose;

    public LoadingDialog(AppCompatActivity activity) {
        super(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.loading_dialog, null);
        tv_purpose = view.findViewById(R.id.loding_purpose);

        this.view = view;
        this.create(false);
    }

    public void setPurpose(String purpose) {
        tv_purpose.setText(purpose);
    }
}
