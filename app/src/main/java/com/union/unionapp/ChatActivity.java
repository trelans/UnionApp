package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView profileIw , send_bt , back_bt;
    TextView tw_username , tw_status;
    EditText messageEt;
    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
// for checking message is seen
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

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
        back_bt = findViewById(R.id.backButton);

        // Layour for recycler view
        Context context;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        // recycler view properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // firebase
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
         hisUid = intent.getStringExtra("Hisuid");


        firebaseDatabase = FirebaseDatabase.getInstance();
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

        readMessages();
        seenMessage();

        // back button
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });

    }
    private  void seenMessage () {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)) {
                        HashMap<String, Object> hasSeenHasmap = new HashMap<>();
                        hasSeenHasmap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenHasmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)
                     || chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)  ) {
                        chatList.add(chat);
                    }
                    // adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList );
                    adapterChat.notifyDataSetChanged();
                    // set adapter to recycler view
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void sendMessage (String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(MainActivity.getServerDate());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeem", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        // reset editText after sending message

        messageEt.setText("");
        // create chatList node/child in firebase database
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myUid).child(hisUid);

        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final  DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(hisUid).child(myUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }
}