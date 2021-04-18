package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

public class CreateAnAccountActivity extends AppCompatActivity {
    // Variables
    TextView tw_email;
    TextView tw_password;
    TextView tw_forgot_password;
    CheckBox check_box_remember_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_an_account);
        tw_email = findViewById(R.id.emailTextView);
        tw_password = findViewById(R.id.passwordTextView);
        tw_forgot_password = findViewById(R.id.forgot_pass_tw);
        check_box_remember_me = findViewById(R.id.CheckBoxRemember);
    }
}