package com.union.unionapp;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;

    boolean lastActsIsActive = true;
    boolean achsIsActive = false;
    TextView lastActsTextView;
    TextView achsTextView;
    ListView lastActsList;
    ListView achsListView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        lastActsTextView = (TextView) view.findViewById(R.id.lastActsTextView);
        achsTextView = (TextView) view.findViewById(R.id.achsTextView);

        lastActsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastActsIsActive){
                    lastActsIsActive = false;
                    lastActsTextView.setTextColor(Color.parseColor("#5F5E5D"));
                    lastActsTextView.getBackground().setTint(Color.parseColor("#FFFFFF"));
                }
            }
        });

        return view;


    }




}