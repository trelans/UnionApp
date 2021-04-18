package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecoverAccountActivity extends AppCompatActivity {
    // Variables
    TextView tw_email;
    TextView tw_enter_code;
    TextView tw_incorrect_code;
    Button sendAgainButton;
    Button signUpButton;


    public void login (View view) {
        // Login onClick
        Log.i("Login" , " YESS");
    }
    public void SendAgain (View view) {
        // Button onClick
        Log.i("SendAgain" , " YESS");
    }

    public void SignUp (View view) {
        // Button onClick
        Log.i("Signup" , " YESS");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_account);


        tw_email = findViewById(R.id.emailTextView);
        tw_enter_code = findViewById(R.id.EnterCodeTextView);
        tw_incorrect_code = findViewById(R.id.incorrectCodeTextView);
        sendAgainButton = findViewById(R.id.sendAgainButton);
        signUpButton = findViewById(R.id.SignUpButton);

    }
}