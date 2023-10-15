package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.models.User;

import java.util.Calendar;

public class RegisterPage extends AppCompatActivity {

    private Button datePickerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        initDatePicker(); // For Date Picker

        // Getting user input in edit text
        EditText usernameEditText = findViewById(R.id.username_input);
        EditText fullnameEditText = findViewById(R.id.fullname_input);
        EditText emailEditText = findViewById(R.id.email_input);
        EditText passwordEditText = findViewById(R.id.password_input);
        EditText reEnterPasswordEditText = findViewById(R.id.re_enterpassword_input);

        datePickerButton = findViewById(R.id.birth_input);
        datePickerButton.setText(getTodaysDate());

        Button createAccount = findViewById(R.id.createAccount_btn);
        createAccount.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String fullname = fullnameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String birthdate = datePickerButton.getText().toString();
            String password = passwordEditText.getText().toString();
            String reEnterPassword = reEnterPasswordEditText.getText().toString();

            if (!password.equals(reEnterPassword)) {
                // Show error message
                Toast.makeText(this, "Passwords are not the same", Toast.LENGTH_LONG).show();
                return;
            }

            User newuser = new User(fullname, username, email, birthdate);

            User.register(newuser, password, new PostProcess() {
                @Override
                public void Success(Object... o) {
                    Intent intent = new Intent(RegisterPage.this, home_screen.class);
                    startActivity(intent);
                }

                @Override
                public void Failed(Exception e) {
                    Toast.makeText(RegisterPage.this, "Register Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Go to Login Page
        Button goToLoginPage = findViewById(R.id.goinglogin_btn);

        goToLoginPage.setOnClickListener(view -> {
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