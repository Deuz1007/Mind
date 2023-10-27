package com.example.mind.dialogs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mind.GlobalPopupFragment;
import com.example.mind.R;

public class UpperNotif extends AppCompatActivity {

    Fragment

    public void replaceFragment (){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.upper_notif, new GlobalPopupFragment());
        fragmentTransaction.commit();
    }

}
