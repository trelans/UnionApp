package com.union.unionapp;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

public class MessageFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView profileIw , send_bt;
    TextView tw_username , tw_status;
    EditText messageEt;
    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    String hisUid;
    String myUid;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
            recyclerView = view.findViewById(R.id.chat_recyclerView);
            tw_status = view.findViewById(R.id.userStatus);
            tw_username = view.findViewById(R.id.userNameTextView);
            messageEt = view.findViewById(R.id.editTextChat);
            profileIw = view.findViewById(R.id.profilePhoto);
            send_bt = view.findViewById(R.id.send);

            // firebase
            firebaseAuth = FirebaseAuth.getInstance();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String hisuid = extras.getString("Hisuid");
            //The key argument here must match that used in the other activity
        }

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



        return view;
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

            startActivity(new Intent(getActivity() , LoginActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onStart() {
        checkUserStatus();
        super.onStart();
    }
}
