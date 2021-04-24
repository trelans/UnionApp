package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView profileIw , send_bt;
    TextView tw_username , tw_status;
    EditText messageEt;
    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    String hisUid;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recyclerView);
        tw_status = findViewById(R.id.userStatus);
        tw_username = findViewById(R.id.userNameTextView);
        messageEt = findViewById(R.id.editTextChat);
        profileIw = findViewById(R.id.profilePhoto);
        send_bt = findViewById(R.id.send);

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String hisuid = intent.getStringExtra("hisUid");


        firebaseDatabase = firebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        // search user to get that users' info
        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);
        // get user picture and username

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check until requitrf info is received
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // get data
                    String username ="" + ds.child("username").getValue();
                    String pp = "" + ds.child("pp").getValue();
                    // set data
                    tw_username.setText(username);
                    try {
                        Picasso.get().load(pp).placeholder(R.drawable.profile_icon).into(profileIw);
                    }
                    catch (Exception e) {
                        Picasso.get().load(R.drawable.profile_icon).into(profileIw);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        send_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tw_username.setText(hisuid);
                // get message text from edit text
                String message = messageEt.getText().toString().trim();
                // check if text if empty
                if (TextUtils.isEmpty(message)) {
                    // empty text
                }
                else {
                    sendMessage(message);
                }
            }
        });



    }
    private void sendMessage (String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message", message);
        databaseReference.child("Chats").push().setValue(hashMap);

        // reset editText after sending message

        messageEt.setText("");

    }

    private void  checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed stay
            // set email of logged in user
            myUid = user.getUid();
        }
        else  {
            //go back to login


        }
    }

    @Override
    public void onStart() {
        checkUserStatus();
        super.onStart();
    }
}