package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class CreateAnAccountActivity extends AppCompatActivity {
    // Variables
    TextView tw_name;
    TextView tw_email;
    TextView tw_surname;
    TextView tw_password;
    TextView tw_password_Auth;
    CheckBox cb_aggrement;
    ImageView tick1;
    ImageView tick2;
    ImageView tick3;
    ImageView tick4;
    ImageView tick5;

    ProgressBar pb_waiting;
    Button bt_signUp;
    Drawable tickIcon;
    private FirebaseAuth mAuth;


    // yükleme metodu yazınca progress barı üste al, arkayı blur yap ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_an_account);

        mAuth = FirebaseAuth.getInstance();
        tw_name = findViewById(R.id.nameTextView);
        tw_surname = findViewById(R.id.surnameTextView);
        tw_email = findViewById(R.id.emailTextView);
        tw_password = findViewById(R.id.passwordTextView);
        tw_password_Auth = findViewById(R.id.passwordAuthTextView);
        cb_aggrement = findViewById(R.id.rememberMeCheckBox);
        pb_waiting = findViewById(R.id.waitingProgressBar);
        bt_signUp = findViewById(R.id.signUpButton);
        tick1 = findViewById(R.id.thickView1);
        tickIcon = getResources().getDrawable(R.drawable.ic_action_name);
        tickIcon.setBounds(0, 0, tickIcon.getIntrinsicWidth(), tickIcon.getIntrinsicHeight());

        tw_password_Auth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!tw_password_Auth.hasFocus() && tw_password_Auth.getText().toString().length() != 0) {
                    if (tw_password.getText().toString().equals(tw_password_Auth.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "dasada",Toast.LENGTH_SHORT).show();
                        tw_password_Auth.setError("", tickIcon);
                        return;
                    }
                }
            }
        });



        bt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tw_email.getText().toString().trim();
                String name = tw_name.getText().toString().trim();
                String surname = tw_surname.getText().toString().trim();
                String password = tw_password.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    tw_email.setError("Email is required");
                    return; // metodu bitirir
                }
                if (TextUtils.isEmpty(password)) {
                    tw_password.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    tw_password.setError("Password length must be at least 6 character");
                    return;
                }
                pb_waiting.setVisibility(View.VISIBLE);
                if (!password.equals(tw_password_Auth.getText().toString().trim())) {
                    tw_password_Auth.setError("Passwords don't match");
                    return;
                }

                if (!email.contains("ug.bilkent.edu.tr")) {
                    tw_email.setError("Your university hasn't registered yet");
                    return;
                }

            }
        });


    }

    public static void signUp(View view) {
    }

    public static void login(View view) {

    }

    public static void previewTerms(View view) {

    }
}