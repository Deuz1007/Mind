package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mind.exceptions.MaxContentTokensReachedException;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.interfaces.ProcessMessage;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

public class QuizContentPage extends AppCompatActivity {
    EditText et_contentField;
    Button btn_edit, btn_save, btn_generate;
    AlertDialog ad_itemsDialog, loadingAlertDialog;
    TextView textLoading;

    Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_content_page);

        // Edit Text
        et_contentField = findViewById(R.id.edit_content_field);

        // Button
        Button btn_quizzes = findViewById(R.id.check_content_btn);
        Button btn_back = findViewById(R.id.back_btn);
        btn_generate = findViewById(R.id.generate_quiz_btn);
        btn_edit = findViewById(R.id.edit_btn);
        btn_save = findViewById(R.id.save_btn);

        // Setup popup
        setItemsPopup();
        setGenerationPopup();

        // Get topic from intent from library sheet
        String topicId = getIntent().getStringExtra("topicId");
        topic = User.current.topics.get(topicId);

        // Assign topic content to content field
        et_contentField.setText(topic.content);

        // Set text container and button visibility
        toggleContentContainer(false, View.VISIBLE, View.INVISIBLE);

        // Set onclick listener to edit and save buttons
        btn_edit.setOnClickListener(v -> toggleContentContainer(true, View.INVISIBLE, View.VISIBLE));
        btn_save.setOnClickListener(v -> toggleContentContainer(false, View.VISIBLE, View.INVISIBLE));

        // Set onclick listener for list of quizzes
        btn_quizzes.setOnClickListener(v -> {
            Intent intent = new Intent(QuizContentPage.this, TopicQuizContentPage.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        });

        // Set onclick listener for generate button
        btn_generate.setOnClickListener(v -> ad_itemsDialog.show());

        // Go back to home screen
        btn_back.setOnClickListener(v -> startActivity(new Intent(QuizContentPage.this, home_screen.class)));
    }

    private void setGenerationPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(QuizContentPage.this);
        View dialogView = getLayoutInflater().inflate(R.layout.loading_dialog, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false); // Preventing user from dismissing the dialog
        loadingAlertDialog = dialogBuilder.create();
        textLoading = dialogView.findViewById(R.id.loding_purpose);
    }

    private void setItemsPopup() {
        // Setup dialog view and builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.number_of_items_popup, null);

        // Set view and build dialog
        builder.setView(view);
        ad_itemsDialog = builder.create();

        // Get the radio group
        RadioGroup radioGroup = view.findViewById(R.id.items_to_gen); // Find the RadioGroup inside the inflated view

        // Get the confirm button and assign onclick listener
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

            try {
                // Disable generate button
                btn_generate.setEnabled(false);

                // Generate new quiz
                generate(Integer.parseInt(selectedTxt) / 3);
            } catch (MaxContentTokensReachedException e) {
                // Display toast on error
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            // Dismiss items dialog
            if (ad_itemsDialog.isShowing()) ad_itemsDialog.dismiss();
        });

        // Set darker window
        Window window = ad_itemsDialog.getWindow();
        if (window != null) window.setBackgroundDrawable(new ColorDrawable(0));
    }

    private void toggleContentContainer(boolean contentFieldEnabled, int editVisibility, int saveVisibility) {
        et_contentField.setEnabled(contentFieldEnabled);

        btn_edit.setVisibility(editVisibility);
        btn_save.setVisibility(saveVisibility);
    }

    private void generate(int itemsPerLevel) throws MaxContentTokensReachedException {
        Topic.createQuiz(
                topic,
                topic.content,
                itemsPerLevel,
                message -> {
                    // Show message
                    QuizContentPage.this.runOnUiThread(() -> {
                        if (!loadingAlertDialog.isShowing())
                            loadingAlertDialog.show();

                        textLoading.setText(message);
                    });
                },
                new PostProcess() {
                    @Override
                    public void Success(Object... o) {
                        textLoading.setText("Quiz generation success!");

                        Quiz quiz = (Quiz) o[0];
                        Class<?> targetClass = topic.quizzes.size() - 1 == 0 ? Instructions_Popup.class : BooleanQuizPage.class;

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(QuizContentPage.this, targetClass);
                            intent.putExtra("quizId", quiz.quizId);
                            intent.putExtra("topicId", topic.topicId);
                            startActivity(intent);
                        }, 3000);
                    }

                    @Override
                    public void Failed(Exception e) {
                        // Hide popup
                        loadingAlertDialog.dismiss();
                    }
                });
    }
}