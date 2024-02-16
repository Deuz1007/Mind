package com.mindapps.mind.dialogs;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.mindapps.mind.R;
import com.mindapps.mind.interfaces.Include;

public class ItemCountDialog extends CustomDialog {
    private Include startGeneration;

    public ItemCountDialog(AppCompatActivity activity) {
        super(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.number_of_items_popup, null);
        RadioGroup radioGroup = view.findViewById(R.id.items_to_gen);
        Button btn_confirm = view.findViewById(R.id.confirm_item);

        btn_confirm.setOnClickListener(v -> {
            // Get the id selection radio button
            int selectedItem = radioGroup.getCheckedRadioButtonId();

            // Check if there isn't selected option
            if (selectedItem == -1) return;

            // Get the radio button from the id
            RadioButton selectedRadioBtn = radioGroup.findViewById(selectedItem);
            // Extract the text
            String selectedTxt = selectedRadioBtn.getText().toString();

            startGeneration.execute(Integer.parseInt(selectedTxt) / 3);
        });

        this.view = view;
        this.create();
    }

    public void setStartGeneration(Include startGeneration) {
        this.startGeneration = startGeneration;
    }
}
