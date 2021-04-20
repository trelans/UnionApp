package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    TextView tw_login;
    Button sendAgainButton;
    Button signUpButton;



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
        tw_login = findViewById(R.id.loginTextView);
        sendAgainButton = findViewById(R.id.sendAgainButton);
        signUpButton = findViewById(R.id.signUpButton);

        // initial
        tw_incorrect_code.setVisibility(View.INVISIBLE);
        sendAgainButton.setText("SEND");


        // onClickListeners
        sendAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tw_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}