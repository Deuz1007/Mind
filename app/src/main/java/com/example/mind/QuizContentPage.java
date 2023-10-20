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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.dialogs.ItemCountDialog;
import com.example.mind.dialogs.LoadingDialog;
import com.example.mind.exceptions.MaxContentTokensReachedException;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.interfaces.ProcessMessage;
import com.example.mind.models.Quiz;
import com.example.mind.models.Topic;
import com.example.mind.models.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class QuizContentPage extends AppCompatActivity {
    EditText et_contentField;
    Button btn_back, btn_edit, btn_save, btn_generate, btn_delete, btn_quizzes;
    AlertDialog alertDialog;
    LoadingDialog generationDialog;
    ItemCountDialog itemCountDialog;
    ErrorDialog errorDialog;

    TextView textLoading;

    Topic topic;

    LibraryContentAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_content_page);

        // Edit Text
        et_contentField = findViewById(R.id.edit_content_field);

        // TextView
        TextView tv_tokenCount = findViewById(R.id.tokenCount_text);

        // Button
        btn_quizzes = findViewById(R.id.check_content_btn);
        btn_back = findViewById(R.id.back_btn);
        btn_generate = findViewById(R.id.generate_quiz_btn);
        btn_edit = findViewById(R.id.edit_btn);
        btn_save = findViewById(R.id.save_btn);
        btn_delete = findViewById(R.id.delete_content_btn);

        // Setup popup
        generationDialog = new LoadingDialog(this);
        errorDialog = new ErrorDialog(this);

        itemCountDialog = new ItemCountDialog(this);
        itemCountDialog.setStartGeneration(objects -> {
            int items = (int) objects[0];

            try {
                // Disable generate button
                btn_generate.setEnabled(false);

                // Generate new quiz
                generate(items);
            } catch (MaxContentTokensReachedException e) {
                // Show generation error
                errorDialog.setMessage(e.getMessage());
                errorDialog.show();
            }

            if (itemCountDialog.isShowing()) itemCountDialog.dismiss();
        });

        // Get topic from intent from library sheet
        String topicId = getIntent().getStringExtra("topicId");
        topic = User.current.topics.get(topicId);

        // Assign the token count to the Textview
        tv_tokenCount.setText(Quiz.contentToTokenArray(topic.content).length + "");

        // Assign topic content to content field
        et_contentField.setText(topic.content);
        et_contentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_tokenCount.setText(Quiz.contentToTokenArray(s.toString()).length + "");
            }
        });

        // Set text container and button visibility
        toggleContentContainer(false, View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);

        // Set onclick listener to edit and save buttons
        btn_edit.setOnClickListener(v -> toggleContentContainer(true, View.INVISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE));
        btn_save.setOnClickListener(v -> toggleContentContainer(false, View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE));

        // Adapter for the content items of the contents in the library sheet
        contentAdapter = new LibraryContentAdapter(this, new ArrayList<>(User.current.topics.values()));

        // Set onclick listener to delete the quiz content
        btn_delete.setOnClickListener(v -> {
            deleteAlertPopup();
        });

        // Set onclick listener for list of quizzes
        btn_quizzes.setOnClickListener(v -> {
            Intent intent = new Intent(QuizContentPage.this, TopicQuizContentPage.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        });

        // Set onclick listener for generate button
        btn_generate.setOnClickListener(v -> itemCountDialog.show());

        // Go back to home screen
        btn_back.setOnClickListener(v -> startActivity(new Intent(QuizContentPage.this, home_screen.class)));
    }

    private void toggleContentContainer(boolean contentFieldEnabled, int editVisibility, int saveVisibility, int deleteVisibility, int quizVisibility) {
        et_contentField.setEnabled(contentFieldEnabled);

        btn_edit.setVisibility(editVisibility);
        btn_save.setVisibility(saveVisibility);
        btn_delete.setVisibility(deleteVisibility);
        btn_quizzes.setVisibility(quizVisibility);
    }

    private void generate(int itemsPerLevel) throws MaxContentTokensReachedException {
        Topic.createQuiz(
                topic,
                topic.content,
                itemsPerLevel,
                message -> {
                    // Show message
                    QuizContentPage.this.runOnUiThread(() -> {
                        if (!generationDialog.isShowing())
                            generationDialog.show();

                        generationDialog.setPurpose(message);
                    });
                },
                new PostProcess() {
                    @Override
                    public void Success(Object... o) {
                        generationDialog.setPurpose("Quiz generation success!");

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
                        generationDialog.dismiss();

                        errorDialog.setMessage(e.getMessage());
                        errorDialog.show();
                    }
                });
    }

    public void deleteAlertPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizContentPage.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(QuizContentPage.this).inflate(R.layout.exit_quiz_popup, (LinearLayout) findViewById(R.id.exit_popup));

        builder.setView(view);
        ((TextView) view.findViewById(R.id.quit_comment)).setText("Are You Sure You wanna delete this topic?");
        ((ImageView) view.findViewById(R.id.quit_image)).setImageResource(R.drawable.warning);
        ((LinearLayout) view.findViewById(R.id.exit_popup)).setBackgroundResource(R.drawable.library_gradient_bg);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.yes_btn).setOnClickListener(View -> {
            contentAdapter.setOnItemClickListener(Position -> {
                loadingScreen();

                Topic topic = contentAdapter.items.get(Position);
                Topic.removeTopic(topic, new PostProcess() {

                    @Override
                    public void Success(Object... o) {
                        // deleting the specific item
                        contentAdapter.items.remove(Position);

                        // notifying the adapter
                        contentAdapter.notifyDataSetChanged();

                        // redirecting to library sheet
                        startActivity(new Intent(QuizContentPage.this, library_sheet.class));
                    }

                    @Override
                    public void Failed(Exception e) {
                        // Show error
                        errorDialog.setMessage("Topic deletion failed");
                        errorDialog.show();
                    }
                });
            });
        });

        view.findViewById(R.id.no_btn).setOnClickListener(View -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void loadingScreen(){
        // Loading Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(QuizContentPage.this);
        LayoutInflater inflater = QuizContentPage.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false); // Prevent user from dismissing the dialog
        alertDialog = dialogBuilder.create();
        alertDialog.show();

        textLoading = dialogView.findViewById(R.id.loding_purpose);
        textLoading.setText("Deleting...");
    }
}