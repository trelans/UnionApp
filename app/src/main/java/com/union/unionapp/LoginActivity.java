package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    public void login (View view) {
        // Button onClick
        Log.i("Butona basıldı" , " YESS");
    }
    public void ForgotPassword (View view) {
        // Button onClick
        Log.i("Forgot Passworda Basil" , " YESS");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Variables
        TextView tw_email;
        TextView tw_password;
        TextView tw_forgot_password;
        CheckBox check_box_remember_me;


        tw_email = findViewById(R.id.emailTextView);
        tw_password = findViewById(R.id.passwordTextView);
        tw_forgot_password = findViewById(R.id.forgot_pass_tw);
        check_box_remember_me = findViewById(R.id.CheckBoxRemember);


    }
}