package com.example.mind;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialog {

    private Activity loading;
    private AlertDialog dialog;

    LoadingDialog(Activity myActivity){
        loading = myActivity;
    }

    void LoadingAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(loading);

        LayoutInflater inflater = loading.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog(){
        dialog.dismiss();
    }

}
