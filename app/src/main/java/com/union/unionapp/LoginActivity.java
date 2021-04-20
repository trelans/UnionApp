package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // FirebaseAuth.getInstance().signOut(); çıkış yapılacak yerde kullanılacak

    // Variables
    TextView tw_email;
    TextView tw_password;
    TextView tw_forgot_password;
    TextView tw_error;
    CheckBox check_box_remember_me;
    Button button_login;
    CardView cardView;
    ProgressBar pb_waiting;
    ImageView tick1;
    ImageView tick2;
    private FirebaseAuth mAuth;
    String email;
    String password;
    boolean isThereError = false;



    public void ForgotPassword(View view) {
        // Button onClick

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tw_email = findViewById(R.id.nameTextView);
        tw_password = findViewById(R.id.passwordTextView);
        tw_forgot_password = findViewById(R.id.loginTextView);
        tw_error = findViewById(R.id.errorTextView);
        tw_error.setVisibility(View.INVISIBLE);

        cardView = findViewById(R.id.cardView);
        /*
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams);
        cardView.getLayoutParams();
        layoutParams.height = 10;
         */

        check_box_remember_me = findViewById(R.id.rememberMeCheckBox);
        button_login = findViewById(R.id.signUpButton);
        pb_waiting = findViewById(R.id.waitingProgressBar);
        tick1 = findViewById(R.id.tickView1);
        tick2 = findViewById(R.id.tickView2);

        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and if there is a user go to main activity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        // Klavye açık mı kapalı mı onu dinlio
     KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                if (!isVisible && !isThereError ) {
                    button_login.setEnabled(true);
                }
            }

        });


        // Focus Listeners
        tw_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                email = tw_email.getText().toString().trim();
                // if something wrong with email
                if (!tw_email.hasFocus() && email.length() != 0) {
                    if (!email.contains("ug.bilkent.edu.tr")) {
                        isThereError = true;
                        button_login.setEnabled(false);
                        tw_email.setError("Your university hasn't registered yet");
                        tick1.setVisibility(View.INVISIBLE);
                        return;
                    } else {
                        tick1.setVisibility(View.VISIBLE);
                        isThereError = false;
                        button_login.setEnabled(true);
                        return;
                    }
                }
            }
        });

        tw_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                password = tw_password.getText().toString().trim();
                // if something wrong with password
                if (!tw_password.hasFocus() && tw_password.getText().toString().length() != 0) {
                    if (password.length() < 6) {
                        isThereError = true;
                        button_login.setEnabled(false);
                        tw_password.setError("Password length must be at least 6 character");
                        tick2.setVisibility(View.INVISIBLE);
                        return;
                    } else {
                        tick2.setVisibility(View.VISIBLE);
                        isThereError = false;
                        button_login.setEnabled(true);
                        return;
                    }
                }
            }
        });


            // click listeners
        tw_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RecoverAccountActivity.class));

            }
        });


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                email = tw_email.getText().toString().trim();
                password = tw_password.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    tw_email.setError("Email is required");
                    isThereError = true;
                }
                if (TextUtils.isEmpty(password)) {
                    tw_password.setError("Password is required");
                    isThereError = true;
                }


                if (!isThereError) {
                    pb_waiting.setVisibility(View.VISIBLE);

                    // authenticate user
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Giriş yapıldı", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                tw_error.setText(task.getException().getMessage());
                                tw_error.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }



            }
        });
    }
}