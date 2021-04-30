package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.HashMap;

public class VerifyAccountActivity extends AppCompatActivity {

    LocalDataManager localDataManager;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);
        localDataManager = new LocalDataManager();
        Button verifyAccountButton = findViewById(R.id.verifyButton);
        Button sendAgainButton = findViewById(R.id.sendAgainButton);
        EditText enterCodeET = findViewById(R.id.enterCodeET);
        TextView incorrectCodeTW = findViewById(R.id.incorrectCodeTextView);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ActivationCodes");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());


        final String[] key = new String[1];

        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendAgainButton.setText(Long.toString(millisUntilFinished / 1000));
            }
            @Override
            public void onFinish() {
                sendAgainButton.setEnabled(true);
                sendAgainButton.setText("Send Again");
            }
        };
        sendAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                // Inform user
                incorrectCodeTW.setVisibility(View.VISIBLE);
                incorrectCodeTW.setText("Code has been sent to your mail");

                // Starts timer
                countDownTimer.start();
                sendAgainButton.setEnabled(false);

                // Sends email
                HashMap<String, Object> hashMap = new HashMap<>();
                String code = RecoverAccountActivity.generateVerifCode();
                key[0] = ref.push().getKey();
                hashMap.put("code", code);
                hashMap.put("timeStamp", ServerValue.TIMESTAMP);
                ref.child(key[0]).updateChildren(hashMap);
                JavaMailAPI javaMailAPI = new JavaMailAPI(VerifyAccountActivity.this, email, " Union App Account Activation Code", "Hi, we glad to see you among us. Here is your activation code: " + code);
                javaMailAPI.execute();
            }
        });

        verifyAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(key[0]).child("code").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue().equals(enterCodeET.getText().toString())) {
                            incorrectCodeTW.setText("");
                            countDownTimer.cancel();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("accountType", "0");
                            System.out.println("girdi2");
                            localDataManager.setSharedPreference(VerifyAccountActivity.this, "isAccountVerified", email);
                            reference.updateChildren(map);
                            ref.child(key[0]).removeValue();
                            startActivity(new Intent(VerifyAccountActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        incorrectCodeTW.setText("The code is invalid");
                        ref.child(key[0]).removeValue();
                    }
                });
            }
        });
    }
}