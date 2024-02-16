package com.mindapps.mind;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

public class AlertMessage {

    private Activity loading;
    private AlertDialog alertPopup;

    AlertMessage(Activity myActivity){
        loading = myActivity;
    }

    void AlertMessagePopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(loading);

        LayoutInflater inflater = loading.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(true);

        alertPopup = builder.create();

        if (alertPopup.getWindow() != null) {
            alertPopup.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertPopup.show();
    }

    void dismissPopup(){
        alertPopup.dismiss();
    }

}
