package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class RecoverAccountActivity extends AppCompatActivity {
    // Variables
    TextView tw_email;
    TextView tw_enter_code;
    TextView tw_incorrect_code;
    TextView tw_login;
    Button sendAgainButton;
    Button verifyButton;
    String code ;
    String email = "";
    private FirebaseAuth mAuth;
    final String VERIFICATION_SUBJECT = " Your UnI0n verification code";
    final String VERIFICATION_MAIL = "Your UnI0n verification code is " + code + ".";


    public void SignUp (View view) {
        // Button onClick
        Log.i("Signup" , " YESS");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_account);

        mAuth = FirebaseAuth.getInstance();
        tw_email = findViewById(R.id.emailTextView);
        tw_enter_code = findViewById(R.id.EnterCodeTextView);
        tw_incorrect_code = findViewById(R.id.incorrectCodeTextView);
        tw_login = findViewById(R.id.loginTextView);
        sendAgainButton = findViewById(R.id.sendAgainButton);
        verifyButton = findViewById(R.id.VerifyButton);

        // set timer
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                sendAgainButton.setText(Long.toString(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                sendAgainButton.setEnabled(true);
                sendAgainButton.setText("Send Again");
            }
        };

        // initial
        tw_incorrect_code.setVisibility(View.INVISIBLE);
        sendAgainButton.setText("SEND");


        // onClickListeners
        sendAgainButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                email = tw_email.getText().toString().trim();

                    // generates a verif code
                  code =  generateVerifCode();

                    // Sends this code to the server
                            //TODO
                    // Inform user
                    tw_incorrect_code.setVisibility(View.VISIBLE);
                    tw_incorrect_code.setText("Code has been sent to your mail");
                    // Starts timer
                        countDownTimer.start();
                    // Sends email
                    JavaMailAPI javaMailAPI = new JavaMailAPI(RecoverAccountActivity.this , email, " Union App Password Recovery Request" , "Hi, you sent a password recovery password. Your recovery password code is" + code );
                    javaMailAPI.execute();
                    // wait for
                    sendAgainButton.setEnabled(false);
                    //mail




            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO currentcodu databasedekine eşitle
                // gets current code from data base
                String currentCode = "123";
                if (currentCode.equals(tw_enter_code.getText())) {
                    tw_incorrect_code.setVisibility(View.INVISIBLE);
                    verifyButton.setText("Change Password");
//                    tw_enter_code.setTransformationMethod(PasswordTransformationMethod.getInstance());
  //                  tw_email.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    tw_email.setHint("New Password");
                    tw_enter_code.setHint("New Password");

                }
                else {
                    tw_incorrect_code.setText("Verification code is wrong");
                }
            }
        });

        tw_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    public static String generateVerifCode() {
        int code1 = (int) (Math.random() * 9) + 1 ;
        int code2 = (int) (Math.random() * 9) + 1 ;
        int code3 = (int) (Math.random() * 9) + 1 ;
        int code4 = (int) (Math.random() * 9) + 1 ;
        int code5 = (int) (Math.random() * 9) + 1 ;
        int code6 = (int) (Math.random() * 9) + 1 ;
        return  String.valueOf(code1) + String.valueOf(code2) + String.valueOf(code2) + String.valueOf(code3) +  String.valueOf(code4) + String.valueOf(code5) + String.valueOf(code6);
    }
}
