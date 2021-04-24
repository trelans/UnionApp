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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    boolean lastActsIsActive = true;
    boolean achsIsActive = false;
    TextView lastActsTextView;
    TextView achsTextView;
    ListView lastActsList;
    ListView achsListView;
    ImageView openCalendar;
    Dialog calendarDialog;
    TextView usernameTW;
    ImageView userPP;

    AppCompatButton tagButton1;
    AppCompatButton tagButton2;
    AppCompatButton tagButton3;
    ImageView forwardDateImageView;
    ImageView backwardDateImageView;
    TextView dateTextView;
    String date;
    long dateServer;
    SimpleDateFormat dateFormat;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        //init views
        usernameTW = view.findViewById(R.id.userNameTextView);
        userPP = view.findViewById(R.id.userPp);
        tagButton1 = view.findViewById(R.id.tagButton1);
        tagButton2 = view.findViewById(R.id.tagButton2);
        tagButton3 = view.findViewById(R.id.tagButton3);


        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                double estimatedServerTimeMs = System.currentTimeMillis() + offset;
                dateServer = (long) estimatedServerTimeMs;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });



        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check until required data get
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String name = "@" + ds.child("username").getValue();
                    String pp = "" + ds.child("pp").getValue();

                    //set data
                    usernameTW.setText(name);
                    try {
                        //if image received, set
                        Picasso.get().load(pp).into(userPP);
                    }catch (Exception e){
                        //if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.user_pp_template).into(userPP);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                Calendar calendar;

                Dialog dialog;

                calendarDialog.setContentView(R.layout.custom_user_calendar_popup);

                forwardDateImageView = calendarDialog.findViewById(R.id.forwardImageView);
                backwardDateImageView = calendarDialog.findViewById(R.id.backwardsImageView);
                dateTextView = calendarDialog.findViewById(R.id.dateTextView);

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateServer); // retrieve the date from the server
                calendarToString(calendar);


                //current date - initial date
                dateTextView.setText(date);
                getCurrentDateActivities(calendar); // displays specified dates activities.

                forwardDateImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //date 1 gün ileriye
                        calendar.add(Calendar.DATE, 1);
                        calendarToString(calendar);
                        dateTextView.setText(date);
                        getCurrentDateActivities(calendar);

                    }
                });


                backwardDateImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //date 1 gün geriye
                        calendar.add(Calendar.DATE, -1);
                        calendarToString(calendar);
                        dateTextView.setText(date);
                        getCurrentDateActivities(calendar);
                    }
                });

                calendarDialog.show();
            }
        });

        return view;


    }

    public void getCurrentDateActivities(Calendar calendar) {

    }

    public void calendarToString(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());
    }


}