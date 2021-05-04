package com.union.unionapp.views;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.union.unionapp.controllers.AdapterAchievements;
import com.union.unionapp.controllers.AdapterCalendar;
import com.union.unionapp.controllers.AdapterLastActivities;
import com.union.unionapp.R;
import com.union.unionapp.models.ModelAchievements;
import com.union.unionapp.models.ModelCalendar;
import com.union.unionapp.models.ModelLastActivities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    static int i = 0;

    private ArrayList<ModelLastActivities> LastActList;
    private AdapterLastActivities adapterLastAct;

    private ArrayList<ModelAchievements> AchivementList;
    private AdapterAchievements adapterAchivement;

    private ArrayList<ModelCalendar> CalendarList;
    private AdapterCalendar adapterCalendar;

    boolean lastActsIsActive;
    boolean achsIsActive;
    String tagNums;
    String uid;
    String[] allAchs;
    String[] userAchs;
    String[] userActs;
    String achievementLocationsWComma;
    TextView lastActsTextView;
    TextView achsTextView;
    RecyclerView lastActsRv;
    RecyclerView achsListRv;
    RecyclerView calendarRecycleView;
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
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_profile, container, false );
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference( "BilkentUniversity/Users" );

        lastActsIsActive = true;
        achsIsActive = false;

        //init views
        usernameTW = view.findViewById( R.id.userNameTextView );
        userPP = view.findViewById( R.id.userPp );

        tagButton1 = view.findViewById( R.id.profileTagButton1 );
        tagButton2 = view.findViewById( R.id.profileTagButton2 );
        tagButton3 = view.findViewById( R.id.profileTagButton3 );

        tagButtons = new AppCompatButton[]{ tagButton1, tagButton2, tagButton3 };
        allTags = getResources().getStringArray( R.array.all_tags );

        tagButton1.setVisibility( View.INVISIBLE );
        tagButton2.setVisibility( View.INVISIBLE );
        tagButton3.setVisibility( View.INVISIBLE );

        allAchs = getResources().getStringArray( R.array.user_achievements );
        achievementLocationsWComma = "1,3,5".replace( ",", "" );
        userAchs = new String[achievementLocationsWComma.length()];

        for ( i = 0; i < achievementLocationsWComma.length(); i++ ) {
            userAchs[i] = allAchs[Integer.parseInt( String.valueOf( achievementLocationsWComma.charAt( i ) ) )];
        }

        userActs = new String[]{ "asdasd", "asdadd", "asdadad", "sadasdasdads", "asdadasdasdads" };


        Query query = databaseReference.orderByChild( "email" ).equalTo( user.getEmail() );
        query.addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                // check until required data get
                for ( DataSnapshot ds : snapshot.getChildren() ) {
                    //get data
                    String name = "@" + ds.child( "username" ).getValue();
                    String pp = "" + ds.child( "pp" ).getValue();
                    tagNums = "" + ds.child( "tags" ).getValue();
                    uid = "" + ds.child( "uid" ).getValue();

                    if ( tagNums != null ) {
                        tagIndexes = tagNums.split( "," );
                        int[] k = new int[1];
                        k[0] = 0;
                        int temp;
                        for ( String str : tagIndexes ) {
                            temp = Integer.parseInt( str );
                            tagButtons[k[0]].setText( allTags[temp] );
                            tagButtons[k[0]].setVisibility( View.VISIBLE );
                            k[0]++;

                        }
                    }

                    String achivements = "" + ds.child( "achievements" ).getValue();

                    //set data
                    usernameTW.setText( name );
                    Handler handler = new Handler();
                    handler.postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            if ( !pp.equals( "drawable-v24/profile_icon.png" ) ) {
                                try {
                                    //if image received, set
                                    StorageReference image = FirebaseStorage.getInstance().getReference( "BilkentUniversity/pp/" + pp );
                                    image.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess( Uri uri ) {
                                            Picasso.get().load( uri ).into( userPP );
                                        }
                                    } );
                                } catch ( Exception e ) {
                                    //if there is any exception while getting image then set default
                                    Picasso.get().load( R.drawable.user_pp_template ).into( userPP );
                                }
                            }
                        }
                    }, 2500 );
                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );


        calendarDialog = new Dialog( getActivity() );
        // Layoutu transparent yapıo
        calendarDialog.getWindow().setBackgroundDrawable( new ColorDrawable( android.graphics.Color.TRANSPARENT ) );
        calendarDialog.setCanceledOnTouchOutside( true );

        openCalendar = (ImageView) view.findViewById( R.id.directMessage );
        lastActsTextView = (TextView) view.findViewById( R.id.lastActsTextView );

        achsTextView = (TextView) view.findViewById( R.id.achsTextView );
        achsTextView.setTextColor( Color.parseColor( "#5F5E5D" ) );
        achsTextView.setBackgroundTintList( null );


        /*Achievements için listview kısmı*/
        achsListRv = (RecyclerView) view.findViewById( R.id.achsList );
        lastActsRv = (RecyclerView) view.findViewById( R.id.lastActsList );


        lastActsRv.setVisibility( View.VISIBLE );
        lastActsRv.setEnabled( true );


        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            public void run() {
                loadLastAct();
            }
        }, 1000 );

        achsListRv.setVisibility( View.INVISIBLE );
        achsListRv.setEnabled( false );

        lastActsTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Log.i( "tagler:", "" + tagNums );
                if ( !lastActsIsActive ) {
                    lastActsIsActive = true;
                    lastActsTextView.setTextColor( Color.parseColor( "#FFFFFF" ) );
                    lastActsTextView.getBackground().setTint( Color.parseColor( "#4D4D4D" ) );
                    lastActsRv.setVisibility( View.VISIBLE );
                    lastActsRv.setEnabled( true );


                    achsTextView.setTextColor( Color.parseColor( "#5F5E5D" ) );
                    achsTextView.setBackgroundTintList( null );
                    achsListRv.setVisibility( View.INVISIBLE );
                    achsListRv.setEnabled( false );
                    achsIsActive = false;
                    loadLastAct();

                }
            }

        } );

        achsTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if ( !achsIsActive ) {
                    achsIsActive = true;
                    achsTextView.setTextColor( Color.parseColor( "#FFFFFF" ) );
                    achsTextView.getBackground().setTint( Color.parseColor( "#4D4D4D" ) );
                    achsListRv.setVisibility( View.VISIBLE );
                    achsListRv.setEnabled( true );

                    lastActsTextView.setTextColor( Color.parseColor( "#5F5E5D" ) );
                    lastActsTextView.setBackgroundTintList( null );
                    lastActsRv.setVisibility( View.INVISIBLE );
                    lastActsRv.setEnabled( false );
                    lastActsIsActive = false;
                    loadAchievements();
                }
            }

        } );

        openCalendar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Calendar calendar;

                Dialog dialog;

                calendarDialog.setContentView( R.layout.custom_user_calendar_popup );

                forwardDateImageView = calendarDialog.findViewById( R.id.forwardImageView );
                backwardDateImageView = calendarDialog.findViewById( R.id.backwardsImageView );
                dateTextView = calendarDialog.findViewById( R.id.dateTextView );
                calendarRecycleView = (RecyclerView) calendarDialog.findViewById( R.id.calendarRv );

                calendar = Calendar.getInstance();
                calendar.setTimeInMillis( MainActivity.getServerDate() ); // retrieve the date from the server
                calendarToString( calendar );

                //date variablı 02/05/2021

                //current date - initial date
                dateTextView.setText( date );
                getCurrentDateActivities( date, calendarRecycleView ); // displays specified dates activities.

                forwardDateImageView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        //date 1 gün ileriye
                        calendar.add( Calendar.DATE, 1 );
                        calendarToString( calendar );
                        dateTextView.setText( date );
                        getCurrentDateActivities( date, calendarRecycleView );


                    }
                } );


                backwardDateImageView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        //date 1 gün geriye
                        calendar.add( Calendar.DATE, -1 );
                        calendarToString( calendar );
                        dateTextView.setText( date );
                        getCurrentDateActivities( date, calendarRecycleView );
                    }
                } );

                calendarDialog.show();
            }
        } );

        return view;


    }

    public void getCurrentDateActivities( String date, RecyclerView rv ) {
        String fixedDate = date.replace( "/", "_" );
        CalendarList = new ArrayList<>();
        DatabaseReference databaseReferenceNotif = firebaseDatabase.getReference( "BilkentUniversity/Users/" + uid + "/Calendar/" + fixedDate );
        databaseReferenceNotif
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
                        CalendarList.clear();
                        for ( DataSnapshot ds : snapshot.getChildren() ) {
                            // get data
                            ModelCalendar model = ds.getValue( ModelCalendar.class );
                            // add to list
                            CalendarList.add( model );
                        }
                        // adapter
                        adapterCalendar = new AdapterCalendar( getActivity(), CalendarList );
                        // set to recycler view
                        rv.setAdapter( adapterCalendar );
                        rv.setLayoutManager( new LinearLayoutManager( getActivity() ) );

                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError error ) {

                    }
                } );
    }

    public void calendarToString( Calendar calendar ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        date = dateFormat.format( calendar.getTime() );
    }

    public static String removeALetter( StringBuilder s, char c ) {

        if ( s.charAt( i ) == c ) {
            s.deleteCharAt( i );
        }

        if ( i < s.length() - 1 ) {
            i++;
            removeALetter( s, c );
        }

        return s.toString();
    }

    public void loadLastAct() {
        LastActList = new ArrayList<>();
        DatabaseReference databaseReferenceNotif = firebaseDatabase.getReference( "BilkentUniversity/Users/" + uid + "/LastActivities/" );
        databaseReferenceNotif
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
                        LastActList.clear();
                        for ( DataSnapshot ds : snapshot.getChildren() ) {
                            // get data
                            ModelLastActivities model = ds.getValue( ModelLastActivities.class );
                            // add to list
                            LastActList.add( model );
                        }
                        // adapter
                        adapterLastAct = new AdapterLastActivities( getActivity(), LastActList );
                        // set to recycler view
                        lastActsRv.setAdapter( adapterLastAct );
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity() );
                        lastActsRv.setLayoutManager( linearLayoutManager );
                        linearLayoutManager.setStackFromEnd( true );
                        linearLayoutManager.setReverseLayout( true );


                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError error ) {

                    }
                } );
    }

    private void loadAchievements() {
        AchivementList = new ArrayList<>();
        DatabaseReference databaseReferenceNotif = firebaseDatabase.getReference( "BilkentUniversity/Users/" + uid + "/Achievements/" );
        databaseReferenceNotif
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
                        AchivementList.clear();
                        for ( DataSnapshot ds : snapshot.getChildren() ) {
                            // get data
                            ModelAchievements model = ds.getValue( ModelAchievements.class );
                            // add to list
                            AchivementList.add( model );
                        }
                        // adapter
                        adapterAchivement = new AdapterAchievements( getActivity(), AchivementList );
                        // set to recycler view
                        achsListRv.setAdapter( adapterAchivement );
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity() );
                        achsListRv.setLayoutManager( linearLayoutManager );
                        linearLayoutManager.setStackFromEnd( true );
                        linearLayoutManager.setReverseLayout( true );

                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError error ) {

                    }
                } );
    }


}