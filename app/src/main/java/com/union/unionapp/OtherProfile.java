package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OtherProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    static int i = 0;


    boolean lastActsIsActive;
    boolean achsIsActive;
    String[] allAchs;
    String[] userAchs;
    String achievementLocationsWComma;
    TextView lastActsTextView;
    TextView achsTextView;
    ListView lastActsList;
    ListView achsListView;
    ImageView openCalendar;
    Dialog calendarDialog;
    TextView usernameTW;
    ImageView userPP;
    String hisUid;

    AppCompatButton tagButton1;
    AppCompatButton tagButton2;
    AppCompatButton tagButton3;
    ImageView forwardDateImageView;
    ImageView backwardDateImageView;
    TextView dateTextView;
    String date;
    SimpleDateFormat dateFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("Hisuid");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        lastActsIsActive = true;
        achsIsActive = false;

        //init views
        usernameTW = findViewById(R.id.userNameTextView);
        userPP = findViewById(R.id.userPp);
        tagButton1 = findViewById(R.id.tagButton1);
        tagButton2 = findViewById(R.id.tagButton2);
        tagButton3 = findViewById(R.id.tagButton3);

        allAchs = getResources().getStringArray(R.array.user_achievements);
        achievementLocationsWComma = "1,3,5".replace(",", "");
        userAchs = new String[achievementLocationsWComma.length()];

        for (i = 0; i < achievementLocationsWComma.length(); i++){
            userAchs[i] = allAchs[Integer.parseInt(String.valueOf(achievementLocationsWComma.charAt(i)))];
        }


        Query query = databaseReference.orderByChild("uid").equalTo(hisUid);
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

        calendarDialog = new Dialog(getApplicationContext());
        // Layoutu transparent yapıo
        calendarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        openCalendar = (ImageView) findViewById(R.id.openCalendar);
        lastActsTextView = (TextView) findViewById(R.id.lastActsTextView);
        achsTextView = (TextView) findViewById(R.id.achsTextView);
        achsTextView.setTextColor(Color.parseColor("#5F5E5D"));
        achsTextView.setBackgroundTintList(null);

        /*Achievements için listview kısmı*/
        achsListView = (ListView) findViewById(R.id.achsList);
        CustomAdapterAchievements customAdapter = new CustomAdapterAchievements(getApplicationContext(), userAchs);
        achsListView.setAdapter(customAdapter);

        lastActsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lastActsIsActive){
                    lastActsIsActive = true;
                    lastActsTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    lastActsTextView.getBackground().setTint(Color.parseColor("#4D4D4D"));

                    achsTextView.setTextColor(Color.parseColor("#5F5E5D"));
                    achsTextView.setBackgroundTintList(null);
                    achsIsActive = false;

                }
            }

        });

        achsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!achsIsActive){
                    achsIsActive = true;
                    achsTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    achsTextView.getBackground().setTint(Color.parseColor("#4D4D4D"));

                    lastActsTextView.setTextColor(Color.parseColor("#5F5E5D"));
                    lastActsTextView.setBackgroundTintList(null);
                    lastActsIsActive = false;
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
                calendar.setTimeInMillis(MainActivity.dateServer); // retrieve the date from the server
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


    }

    public void getCurrentDateActivities(Calendar calendar) {

    }

    public void calendarToString(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());
    }

    public static String removeALetter(StringBuilder s, char c) {

        if ( s.charAt(i) == c ) {
            s.deleteCharAt(i);
        }

        if (i < s.length() - 1) {
            i++;
            removeALetter(s, c);
        }

        return s.toString();
    }

}