package com.example.mind.utilities;

import android.os.Handler;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.mind.R;
import com.example.mind.data.ConstantValues;
import com.example.mind.interfaces.Include;

public class ModifyButtons {
    public static void setBatchEnabled(boolean isEnabled, Button... buttons) {
        for (Button button : buttons)
            button.setEnabled(isEnabled);
    }

    public static void showCorrectButton(String answer, int color, Include include, Button... buttons) {
        for (Button button : buttons) {
            if (button.getText().toString().equalsIgnoreCase(answer)) {
                button.setBackgroundColor(color);
                break;
            }
        }

        new Handler().postDelayed(include::execute, ConstantValues.QUESTION_DELAY);
    }
}
