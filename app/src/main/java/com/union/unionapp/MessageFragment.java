package com.union.unionapp;



import android.os.Bundle;
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

public class MessageFragment extends Fragment {

    RecyclerView recyclerView;
    ImageView profileIw , send;
    TextView tw_username , tw_status;
    EditText messageEt;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
            /*recyclerView = view.findViewsWithText(R.id.chat_recyclerView);
            tw_status = view.findViewById(R.id.userStatus);
            tw_username = view.findViewById(R.id.userNameTextView);
            messageEt = view.findViewById(R.id.editTextChat);
            profileIw = view.findViewsWithText(R.id.profilePhoto);
            send_bt = view.findViewById(R.id.send);*/
        return view;
    }
}
