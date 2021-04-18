package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.GetChars;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAnAccountActivity extends AppCompatActivity {
    // Variables
    final String regexStr = "[a-zA-ZığüşöçİĞÜŞÖÇ ]+$";
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-ZığüşöçİĞÜŞÖÇ ]+$"
    );
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
    String email;
    String name;
    String surname;
    String password;

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
        email = tw_email.getText().toString().trim();
        name = tw_name.getText().toString().trim();
        surname = tw_surname.getText().toString().trim();
        password = tw_password.getText().toString().trim();
        tick1 = findViewById(R.id.thickView1);
        tick2 = findViewById(R.id.thickView2);
        tick3 = findViewById(R.id.thickView3);
        tick4 = findViewById(R.id.thickView4);
        tick5 = findViewById(R.id.thickView5);
        tickIcon = getResources().getDrawable(R.drawable.ic_action_name);
        tickIcon.setBounds(0, 0, tickIcon.getIntrinsicWidth(), tickIcon.getIntrinsicHeight());

        tw_password_Auth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!tw_password_Auth.hasFocus() && tw_password_Auth.getText().toString().length() != 0) {
                    if (tw_password.getText().toString().equals(tw_password_Auth.getText().toString().trim())) {
                        tick5.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"adasda",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        if (!password.equals(tw_password_Auth.getText().toString().trim())) {
                            tw_password_Auth.setError("Passwords don't match");
                            tick5.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }
                }
            }
        });

        tw_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!tw_password.hasFocus() && tw_password.getText().toString().length() != 0) {
                    if (password.length() < 6) {
                        tw_password.setError("Password length must be at least 6 character");
                        tick4.setVisibility(View.INVISIBLE);
                        return;
                    }else{
                        tick4.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"adasda",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        tw_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                name = tw_name.getText().toString();
                if (!tw_name.hasFocus() && name.length() != 0) {
                    Pattern pattern = Pattern.compile(regexStr);
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.find()) {
                        Toast.makeText(getApplicationContext(),"a",Toast.LENGTH_SHORT).show();
                        tick1.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        tw_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!tw_email.hasFocus() && tw_email.getText().toString().length() != 0) {
                    if (!email.contains("ug.bilkent.edu.tr")) {
                        tw_email.setError("Your university hasn't registered yet");
                        tick3.setVisibility(View.INVISIBLE);
                        return;
                    }else{
                        tick3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        tw_surname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!tw_surname.hasFocus() && tw_surname.getText().toString().length() != 0) {
                    if (surname.matches("[A-Za-z0-9]+")){
                        tick2.setVisibility(View.VISIBLE);
                    }else{
                        tick2.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });











        bt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(email)) {
                    tw_email.setError("Email is required");
                    return; // metodu bitirir
                }
                if (TextUtils.isEmpty(password)) {
                    tw_password.setError("Password is required");
                    return;
                }

                pb_waiting.setVisibility(View.VISIBLE);
            }
        });


    }

    public static void signUp(View view) {
    }

    public void login(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public static void previewTerms(View view) {

    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}