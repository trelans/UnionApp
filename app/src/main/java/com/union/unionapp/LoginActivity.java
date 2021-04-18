package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // FirebaseAuth.getInstance().signOut(); çıkış yapılacak yerde kullanılacak

    // Variables
    TextView tw_email;
    TextView tw_password;
    TextView tw_forgot_password;
    CheckBox check_box_remember_me;
    Button button_login;
    ProgressBar pb_waiting;
    private FirebaseAuth mAuth;

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
        tw_email = findViewById(R.id.nameTextView);
        tw_password = findViewById(R.id.passwordTextView);
        tw_forgot_password = findViewById(R.id.forgot_pass_tw);
        check_box_remember_me = findViewById(R.id.rememberMeCheckBox);
        button_login = findViewById(R.id.signUpButton);
        pb_waiting = findViewById(R.id.waitingProgressBar);

        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and if there is a user go to main activity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

    }
}