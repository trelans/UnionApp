package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class CreateAnAccountActivity extends AppCompatActivity {
    // Variables
    TextView tw_name;
    TextView tw_email;
    TextView tw_surname;
    TextView tw_password;
    TextView tw_password_Auth;
    CheckBox cb_aggrement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_an_account);

        tw_name = findViewById(R.id.nameTextView);
        tw_surname = findViewById(R.id.surnameTextView);
        tw_email = findViewById(R.id.emailTextView);
        tw_password = findViewById(R.id.passwordTextView);
        tw_password_Auth = findViewById(R.id.passwordAuthTextView);
        cb_aggrement = findViewById(R.id.AgreementCheckBox);
    }

    public static void signUp(View view){

    }

    public static void login(View view){

    }
    public static void previewTerms(View view){

    }
}