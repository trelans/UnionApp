package com.union.unionapp;



import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    static int i = 0;


    boolean lastActsIsActive;
    boolean achsIsActive;
    String tagNums;
    String[] allAchs;
    String[] userAchs;
    String[] userActs;
    String achievementLocationsWComma;
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
    AppCompatButton[] tagButtons;
    String[] tagIndexes;

    String[] allTags;

    ImageView forwardDateImageView;
    ImageView backwardDateImageView;
    TextView dateTextView;
    String date;
    SimpleDateFormat dateFormat;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("BilkentUniversity/Users");

        lastActsIsActive = true;
        achsIsActive = false;

        //init views
        usernameTW = view.findViewById(R.id.userNameTextView);
        userPP = view.findViewById(R.id.userPp);

        tagButton1 = view.findViewById(R.id.profileTagButton1);
        tagButton2 = view.findViewById(R.id.profileTagButton2);
        tagButton3 = view.findViewById(R.id.profileTagButton3);

        tagButtons = new AppCompatButton[]{ tagButton1, tagButton2, tagButton3 };
        allTags = getResources().getStringArray( R.array.all_tags );

        tagButton1.setVisibility( View.INVISIBLE );
        tagButton2.setVisibility( View.INVISIBLE );
        tagButton3.setVisibility( View.INVISIBLE );

        allAchs = getResources().getStringArray(R.array.user_achievements);
        achievementLocationsWComma = "1,3,5".replace(",", "");
        userAchs = new String[achievementLocationsWComma.length()];

        for (i = 0; i < achievementLocationsWComma.length(); i++){
            userAchs[i] = allAchs[Integer.parseInt(String.valueOf(achievementLocationsWComma.charAt(i)))];
        }

        userActs = new String[]{"asdasd", "asdadd", "asdadad", "sadasdasdads", "asdadasdasdads"};


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // check until required data get
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String name = "@" + ds.child("username").getValue();
                    String pp = "" + ds.child("pp").getValue();
                    tagNums = "" + ds.child( "tags" ).getValue();

                    if( tagNums != null ) {
                         tagIndexes = tagNums.split( "," );
                        int[] k = new int [ 1 ] ;
                        k [ 0 ] = 0;
                         int temp;
                        for ( String str: tagIndexes ) {
                            temp = Integer.parseInt( str );
                            tagButtons[ k[ 0 ] ].setText( allTags [ temp ] );
                            tagButtons[ k[ 0 ] ].setVisibility( View.VISIBLE );
                            k[ 0 ]++;

                        }
                    }


                    String achivements =  "" + ds.child("achievements").getValue();

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
        calendarDialog.setCanceledOnTouchOutside(true);

        openCalendar = (ImageView) view.findViewById(R.id.directMessage);
        lastActsTextView = (TextView) view.findViewById(R.id.lastActsTextView);

        achsTextView = (TextView) view.findViewById(R.id.achsTextView);
        achsTextView.setTextColor(Color.parseColor("#5F5E5D"));
        achsTextView.setBackgroundTintList(null);


        /*Achievements için listview kısmı*/
        achsListView = (ListView) view.findViewById(R.id.achsList);
        lastActsList = (ListView) view.findViewById(R.id.lastActsList);

        CustomAdapterAchievements customAdapterAchs = new CustomAdapterAchievements(getActivity(), userAchs);
        CustomAdapterLastActs customAdapterLastActs = new CustomAdapterLastActs(getActivity(), userActs);

        achsListView.setAdapter(customAdapterAchs);
        lastActsList.setAdapter(customAdapterLastActs);

        lastActsList.setVisibility(View.VISIBLE);
        lastActsList.setEnabled(true);

        achsListView.setVisibility(View.INVISIBLE);
        achsListView.setEnabled(false);

        lastActsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i( "tagler:", ""+ tagNums );
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
                calendar.setTimeInMillis(MainActivity.getServerDate()); // retrieve the date from the server
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