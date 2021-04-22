package com.union.unionapp;



import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView openCalendar;
    Dialog calendarDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        calendarDialog = new Dialog(getActivity());
        // Layoutu transparent yapıo
        calendarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        openCalendar = (ImageView) view.findViewById(R.id.openCalendar);
        lastActsTextView = (TextView) view.findViewById(R.id.lastActsTextView);
        achsTextView = (TextView) view.findViewById(R.id.achsTextView);

        lastActsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastActsIsActive){
                    lastActsIsActive = false;
                    lastActsTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    lastActsTextView.getBackground().setTint(Color.parseColor("#4D4D4D"));

                    achsTextView.setTextColor(Color.parseColor("#5F5E5D"));
                    achsTextView.setBackgroundTintList(null);
                    achsIsActive = true;
                }
            }

        });

        achsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (achsIsActive){
                    achsIsActive = false;
                    achsTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    achsTextView.getBackground().setTint(Color.parseColor("#4D4D4D"));

                    lastActsTextView.setTextColor(Color.parseColor("#5F5E5D"));
                    lastActsTextView.setBackgroundTintList(null);
                    lastActsIsActive = true;
                }
            }

        });

        openCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                calendarDialog.setContentView(R.layout.custom_user_calendar_popup);
                calendarDialog.show();
            }
        });

        return view;


    }




}