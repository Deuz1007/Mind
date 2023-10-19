package com.example.mind.utilities;

import android.widget.Button;

public class ButtonToggleEnable {
    public static void setBatchEnabled(boolean isEnabled, Button... buttons) {
        for (Button button : buttons)
            button.setEnabled(isEnabled);
    }
}
