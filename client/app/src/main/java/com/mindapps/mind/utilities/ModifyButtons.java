package com.mindapps.mind.utilities;

import android.os.Handler;
import android.widget.Button;

import com.mindapps.mind.data.ConstantValues;
import com.mindapps.mind.interfaces.Include;

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
