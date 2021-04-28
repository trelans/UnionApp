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
    String[] userActs;
    String achievementLocationsWComma;
    TextView lastActsTextView;
    TextView achsTextView;
    ListView lastActsList;
    ListView achsListView;
    ImageView directMessage;
    Dialog calendarDialog;
    TextView usernameTW;
    ImageView userPP;
    String hisUid;

    ImageView back_bt;
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
        databaseReference = firebaseDatabase.getReference("BilkentUniversity/Users");

        lastActsIsActive = true;
        achsIsActive = false;

        //init views
        usernameTW = findViewById(R.id.userNameTextView);
        userPP = findViewById(R.id.userPp);
        tagButton1 = findViewById(R.id.profileTagButton1);
        tagButton2 = findViewById(R.id.profileTagButton2);
        tagButton3 = findViewById(R.id.profileTagButton3);
        back_bt = findViewById(R.id.backButtonn);


        allAchs = getResources().getStringArray(R.array.user_achievements);
        achievementLocationsWComma = "1,3,5".replace(",", "");
        userAchs = new String[achievementLocationsWComma.length()];

        for (i = 0; i < achievementLocationsWComma.length(); i++){
            userAchs[i] = allAchs[Integer.parseInt(String.valueOf(achievementLocationsWComma.charAt(i)))];
        }

        userActs = new String[]{"asdasd", "asdadd", "asdadad", "sadasdasdads", "asdadasdasdads", "asdasasd", "asdasads"};

        Query query = databaseReference.orderByChild("uid").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check until required data get
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String name = "@" + ds.child("username").getValue();
                    String pp = "" + ds.child("pp").getValue();

                    // burada yapılacak

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

        directMessage = (ImageView) findViewById(R.id.directMessage);
        lastActsTextView = (TextView) findViewById(R.id.lastActsTextView);
        achsTextView = (TextView) findViewById(R.id.achsTextView);
        achsTextView.setTextColor(Color.parseColor("#5F5E5D"));
        achsTextView.setBackgroundTintList(null);

        /*Achievements için listview kısmı*/
        achsListView = (ListView) findViewById(R.id.achsListPU);
        lastActsList = (ListView) findViewById(R.id.lastActsListPU);

        CustomAdapterAchsPU customAdapterAchsPU = new CustomAdapterAchsPU(getApplicationContext(), userAchs);
        CustomAdapterLastActsPU customAdapterLastActsPU = new CustomAdapterLastActsPU(getApplicationContext(), userActs);

        achsListView.setAdapter(customAdapterAchsPU);
        lastActsList.setAdapter(customAdapterLastActsPU);

        lastActsList.setVisibility(View.VISIBLE);
        lastActsList.setEnabled(true);
        achsListView.setVisibility(View.INVISIBLE);
        achsListView.setEnabled(false);

        lastActsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lastActsIsActive){
                    lastActsIsActive = true;
                    lastActsTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    lastActsTextView.getBackground().setTint(Color.parseColor("#4D4D4D"));
                    lastActsList.setVisibility(View.VISIBLE);
                    lastActsList.setEnabled(true);

                    achsTextView.setTextColor(Color.parseColor("#5F5E5D"));
                    achsTextView.setBackgroundTintList(null);
                    achsListView.setVisibility(View.INVISIBLE);
                    achsListView.setEnabled(false);
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
                    achsListView.setVisibility(View.VISIBLE);
                    achsListView.setEnabled(true);

                    lastActsTextView.setTextColor(Color.parseColor("#5F5E5D"));
                    lastActsTextView.setBackgroundTintList(null);
                    lastActsList.setVisibility(View.INVISIBLE);
                    lastActsList.setEnabled(false);
                    lastActsIsActive = false;
                }
            }

        });

        directMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( getApplicationContext(), ChatActivity.class);
                i.putExtra("Hisuid",hisUid);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(i);
            }
        });


        // Back button
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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