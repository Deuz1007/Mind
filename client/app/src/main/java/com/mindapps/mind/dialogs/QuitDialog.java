package com.mindapps.mind.dialogs;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mindapps.mind.R;
import com.mindapps.mind.home_screen;
import com.mindapps.mind.interfaces.Include;

public class QuitDialog extends CustomDialog {
    private Include doThisOnQuit;

    public QuitDialog(AppCompatActivity activity) {
        super(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.exit_quiz_popup, activity.findViewById(R.id.exit_popup));
        TextView tv_comment = view.findViewById(R.id.quit_comment);

        view.findViewById(R.id.yes_btn).setOnClickListener(v -> {
            doThisOnQuit.execute();

            Intent intent = new Intent(activity, home_screen.class);
            activity.startActivity(intent);
            activity.finish();
        });

        view.findViewById(R.id.no_btn).setOnClickListener(v -> this.dismiss());

        tv_comment.setText("Exiting Already?");

        this.view = view;
        this.create(R.style.AlertDialogTheme);
    }

    public void setDoThisOnQuit(Include doThisOnQuit) {
        this.doThisOnQuit = doThisOnQuit;
    }
}
