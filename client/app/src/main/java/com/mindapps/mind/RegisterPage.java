package com.mindapps.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mindapps.mind.dialogs.ErrorDialog;
import com.mindapps.mind.dialogs.LoadingDialog;
import com.mindapps.mind.interfaces.PostProcess;
import com.mindapps.mind.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import android.media.MediaPlayer;


public class RegisterPage extends AppCompatActivity {

    private Button datePickerButton;
    MediaPlayer buttonClickSound;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        // Initialize buttonClickSound
        buttonClickSound = MediaPlayer.create(this, R.raw.btn_click3);

        initDatePicker(); // For Date Picker

        // Getting user input in edit text
        EditText usernameEditText = findViewById(R.id.username_input);
        EditText fullnameEditText = findViewById(R.id.fullname_input);
        TextInputEditText emailEditText = findViewById(R.id.email_input);
        TextInputEditText passwordEditText = findViewById(R.id.password_input);
        TextInputEditText reEnterPasswordEditText = findViewById(R.id.re_enterpassword_input);

        // TextInputLayout of the email input field
        TextInputLayout emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        TextInputLayout passswordTextInputLayout = findViewById(R.id.input_password);
        TextInputLayout reenterpassswordTextInputLayout = findViewById(R.id.input_reEnterPassword);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setPurpose("Registering...");

        errorDialog = new ErrorDialog(this);

        datePickerButton = findViewById(R.id.birth_input);
        datePickerButton.setText(getTodaysDate());

        Button createAccount = findViewById(R.id.createAccount_btn);
        createAccount.setOnClickListener(view -> {
            buttonClickSound.start();
            String username = usernameEditText.getText().toString().trim();
            String fullName = fullnameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String birthdate = datePickerButton.getText().toString();
            String password = passwordEditText.getText().toString();
            String reEnterPassword = reEnterPasswordEditText.getText().toString();

            String emailError = null;
            String passwordError = null;
            String reEnterPasswordError = null;

            if (TextUtils.isEmpty(email)) emailError = "Email is required";
            else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) emailError = "Invalid email";

            if (TextUtils.isEmpty(password)) passwordError = "Password is required";
            else if (!password.equals(reEnterPassword)) {
                passwordError = "Password are not the same";
                reEnterPasswordError = "Password are not the same";
            }

            if (emailError != null || passwordError != null || reEnterPasswordError != null) {
                emailTextInputLayout.setError(emailError);
                passswordTextInputLayout.setError(passwordError);
                reenterpassswordTextInputLayout.setError(reEnterPasswordError);

                return;
            }

            loadingDialog.show();
            User newuser = new User(fullName, username, email, birthdate);
            User.register(newuser, password, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    buttonClickSound.start();
                    Intent intent = new Intent(RegisterPage.this, home_screen.class);
                    startActivity(intent);
                }

                @Override
                public void Failed(Exception e) {
                    loadingDialog.dismiss();

                    errorDialog.setMessage("Register failed.");
                    errorDialog.show();
                }
            });
        });

        // Go to Login Page
        Button goToLoginPage = findViewById(R.id.goinglogin_btn);

        goToLoginPage.setOnClickListener(view -> {
            buttonClickSound.start();
            Intent intent = new Intent(RegisterPage.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Creating Date Picker
    private DatePickerDialog datePickerDialog;

    private String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            datePickerButton.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + "/" + day + "/" + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        // Default Value
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}