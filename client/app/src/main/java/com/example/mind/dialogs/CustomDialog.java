package com.example.mind.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mind.R;

public class CustomDialog {
    protected View view;
    public AlertDialog dialog;
    protected Context context;
    protected AlertDialog.Builder dialogBuilder;
    protected Window window;

    public CustomDialog(AppCompatActivity activity) {
        this.context = activity;
    }

    public CustomDialog(View view, Context context) {
        this.view = view;
        this.context = context;
        create();
    }

    protected void init(boolean cancelable) {
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(cancelable);
        dialog = dialogBuilder.create();

        window = dialog.getWindow();
        if (window != null)
            window.setBackgroundDrawable(new ColorDrawable(0));
    }

    protected void create() {
        dialogBuilder = new AlertDialog.Builder(context);
        init(true);
    }

    protected void create(int style) {
        dialogBuilder = new AlertDialog.Builder(context, style);
        init(true);
    }

    protected void create(boolean cancelable) {
        System.out.println("Here");
        dialogBuilder = new AlertDialog.Builder(context);
        init(cancelable);
    }

    protected void create(boolean cancelable, int style) {
        dialogBuilder = new AlertDialog.Builder(context, style);
        init(cancelable);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() { return dialog.isShowing(); }
}
