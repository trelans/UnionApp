package com.union.unionapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.union.unionapp.controllers.JavaMailAPI;
import com.union.unionapp.R;

import java.util.HashMap;

public class RecoverAccountActivity extends AppCompatActivity {
    // Variables
    TextView tw_email;
    TextView tw_enter_code;
    TextView tw_incorrect_code;
    TextView tw_login;
    TextView tw_password;
    TextView tw_password_verify;
    Button sendAgainButton;
    Button verifyButton;
    Button changePasswordButton;
    String code;
    String email = "";
    String key;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    final String VERIFICATION_SUBJECT = " Your UnI0n verification code";
    final String VERIFICATION_MAIL = "Your UnI0n verification code is " + code + ".";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_account);

        mAuth = FirebaseAuth.getInstance();
        tw_email = findViewById(R.id.emailTextView);
        tw_enter_code = findViewById(R.id.enterCodeET);
        tw_incorrect_code = findViewById(R.id.incorrectCodeTextView);
        tw_login = findViewById(R.id.loginTextView);
        sendAgainButton = findViewById(R.id.sendAgainButton);
        verifyButton = findViewById(R.id.VerifyButton);
        tw_password = findViewById(R.id.passwordTextView);
        tw_password_verify = findViewById(R.id.verifyPasswordTextView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        changePasswordButton = findViewById(R.id.changePasswordButton);
        ref = firebaseDatabase.getReference("VerificationCodes");


        // set timer
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {
                sendAgainButton.setText(Long.toString(millisUntilFinished / 1000));
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

                // Inform user
                tw_incorrect_code.setVisibility(View.VISIBLE);
                tw_incorrect_code.setText("Code has been sent to your mail");
                // Starts timer
                countDownTimer.start();
                sendAgainButton.setEnabled(false);
                // Sends email

                HashMap<String, Object> hashMap = new HashMap<>();
                code = generateVerifCode();
                key = ref.push().getKey();
                hashMap.put("code", code);
                hashMap.put("timeStamp", ServerValue.TIMESTAMP);
                ref.child(key).updateChildren(hashMap);
                JavaMailAPI javaMailAPI = new JavaMailAPI(RecoverAccountActivity.this, email, " Union App Password Recovery Request", "Hi, you sent a password recovery request. Your recovery password code is " + code);
                javaMailAPI.execute();
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener checkPasswordListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue().equals(tw_enter_code.getText().toString())) {
                            tw_incorrect_code.setText("");
                            countDownTimer.cancel();
                            sendAgainButton.setVisibility(View.GONE);
                            verifyButton.setText("Change Password");

//                    tw_enter_code.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            //                  tw_email.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            tw_password.setVisibility(View.VISIBLE);
                            tw_password_verify.setVisibility(View.VISIBLE);
                            tw_email.setVisibility(View.GONE);
                            tw_enter_code.setVisibility(View.GONE);

                            changePasswordButton.setVisibility(View.VISIBLE);
                            verifyButton.setVisibility(View.GONE);
                            changePasswordButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    changePassword();
                                }
                            });
                        } else {
                            tw_incorrect_code.setText("Verification code is wrong");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tw_incorrect_code.setText("The code is invalid");
                        ref.child(key).removeValue();
                    }
                };
                ref.child(key).child("code").addListenerForSingleValueEvent(checkPasswordListener);
            }
        });

        tw_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void changePassword() {
        if (tw_email.getText().toString().equals(tw_enter_code.getText().toString()) && tw_email.getText().toString().length() >= 6) {
            tw_incorrect_code.setText("");
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("AuthTokens/" + email.replace(".", "_") + "/token");
            tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String token = snapshot.getValue(String.class);
                    if (token != null) {
                        mAuth.signInWithEmailAndPassword(email, token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String newToken = CreateAnAccountActivity.computeMD5Hash(tw_email.getText().toString());
                                mAuth.getCurrentUser().updatePassword(newToken);
                                tokenRef.setValue(newToken);
                                mAuth.signOut();
                                ref.child(key).removeValue();
                                startActivity(new Intent(RecoverAccountActivity.this, LoginActivity.class));
                            }
                        });
                    } else {
                        ref.child(key).removeValue();
                        tw_incorrect_code.setText("This email is not registered in database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            tw_incorrect_code.setText("Passwords does not match or too short!");
            tw_password.setText("");
            tw_password_verify.setText("");
        }

    }


    public static String generateVerifCode() {
        int code1 = (int) (Math.random() * 9) + 1;
        int code2 = (int) (Math.random() * 9) + 1;
        int code3 = (int) (Math.random() * 9) + 1;
        int code4 = (int) (Math.random() * 9) + 1;
        int code5 = (int) (Math.random() * 9) + 1;
        int code6 = (int) (Math.random() * 9) + 1;
        return "" + code1 + code2 + code3 + code4 + code5 + code6;
    }
}
