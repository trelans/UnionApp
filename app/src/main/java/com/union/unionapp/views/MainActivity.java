package com.union.unionapp.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.union.unionapp.controllers.AdapterNotification;
import com.union.unionapp.controllers.AdapterSearchProfile;
import com.union.unionapp.controllers.AdapterUsers;
import com.union.unionapp.R;
import com.union.unionapp.models.ModelNotification;
import com.union.unionapp.models.ModelUsers;
import com.union.unionapp.controllers.notifications.Token;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static long dateServer;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView selectedOptionTextView;
    Dialog myDialog;
    Boolean searchBarEmpty;
    SearchView searchView;
    ImageView popUpButton;
    AdapterUsers adapterUsers;
    AdapterSearchProfile adapterSearchProfile;
    List<ModelUsers> userList;
    RecyclerView recyclerView;
    RecyclerView notificationsRv;
    private ArrayList<ModelNotification> notificationsList;
    private AdapterNotification adapterNotification;
    final Fragment messageFragment = new MessageFragment();
    final Fragment buddyFragment = new BuddyFragment();
    final Fragment stackFragment = new StackFragment();
    final Fragment clubFragment = new ClubsFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    final int[] clickCount = { 0 };
    Fragment active;
    String mUID;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    String currentPhotoPath;
    StorageReference storageReference;


    private static String[] allTagz;

    int currentActivity = 3;     // 1 Messages / 2 Buddy / 3 Club / 4 Stack / 5 Profile


    //path where images of user profile will be stored
    String storagePath = "Users_Profile_Imgs/";


    // settings
    ImageView userPpInDialog;
    AppCompatButton tagButton1;
    AppCompatButton tagButton2;
    AppCompatButton tagButton3;
    int[] tagTextsIndexArray = new int[3];
    AppCompatButton[] tagsArray;
    String[] allTags;
    boolean[] tagsStatus = { false, false, false };

    // String[] allTags = getResources().getStringArray( R.array.all_tags );
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toast.makeText( this, "Main activity", Toast.LENGTH_SHORT ).show();
        fm.beginTransaction().add( R.id.fragment_container, clubFragment, "3" ).commit();

        active = clubFragment;
        checkUserStatus();
        // server time listener attached
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference( ".info/serverTimeOffset" );
        offsetRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot ) {
                double offset = snapshot.getValue( Double.class );
                dateServer = ( (long) offset ) + System.currentTimeMillis() - SystemClock.elapsedRealtime();
            }

            @Override
            public void onCancelled( DatabaseError error ) {
            }
        } );


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference( "BilkentUniversity/Users" );
        storageReference = FirebaseStorage.getInstance().getReference();


        BottomNavigationView bottomNav = findViewById( R.id.bottom_navigation );
        bottomNav.setOnNavigationItemSelectedListener( navListener );


        //for settings
        allTags = getResources().getStringArray( R.array.all_tags );
        allTagz = allTags;

        searchBarEmpty = true;
        //init List
        userList = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();


        // update token
        if ( mAuth.getCurrentUser() != null ) {
            updateToken( FirebaseInstanceId.getInstance().getToken() );
        }


        popUpButton = (ImageView) findViewById( R.id.showPopUpCreate );
        myDialog = new Dialog( this );
        myDialog.setCanceledOnTouchOutside( true );

        //initial popup icon
        popUpButton.setBackground( null );
        popUpButton.setImageResource( R.drawable.notif );

        // searchView
        searchView = findViewById( R.id.searchTool );


        //clubtan başlatıyor
        if ( savedInstanceState == null ) {
            bottomNav.setSelectedItemId( R.id.nav_club );
        }

        // usersearch fragment


        recyclerView = findViewById( R.id.users_recyclerView );
        // Settings its properties
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );


        // visibility ayarları
        searchView.setOnCloseListener( new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setVisibility( View.GONE );
                return false;
            }
        } );
        searchView.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View v, boolean hasFocus ) {
                recyclerView.setVisibility( View.VISIBLE );
            }
        } );

        searchView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                searchView.setIconified( false );
            }
        } );

        myDialog.setOnDismissListener( new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss( DialogInterface dialog ) {
                if ( currentActivity == 5 ) {
                    popUpButton.setImageResource( R.drawable.settings_icon );
                } else {
                    popUpButton.setImageResource( R.drawable.notif );
                }
            }
        } );


    }

    // klavyeyi dışarı tıklayınca kapatmaya yarıyor
    @Override
    public boolean dispatchTouchEvent( MotionEvent ev ) {
        if ( getCurrentFocus() != null ) {
            InputMethodManager imm = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0 );
            searchView.clearFocus();
            if ( searchBarEmpty ) {
                searchView.setIconified( true );
            }
            clickCount[0]++;
        } else if ( recyclerView.getVisibility() == View.VISIBLE ) {
            if ( clickCount[0] > 1 ) {
                recyclerView.setVisibility( View.GONE );
                clickCount[0] = 0;
            } else {
                clickCount[0]++;
            }
        }
        return super.dispatchTouchEvent( ev );
    }

    private void searchUsersForProfile( String query ) {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Users" );
        // get all data from path
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                userList.clear();
                for ( DataSnapshot ds : snapshot.getChildren() ) {
                    ModelUsers modelUser = ds.getValue( ModelUsers.class );
                    // get all searched users except currently signed in user
                    if ( !modelUser.getUid().equals( fUser.getUid() ) ) {
                        if ( modelUser.getUsername().toLowerCase().contains( query.toLowerCase() ) ) {
                            userList.add( modelUser );
                            // silincek
                            Toast.makeText( getApplicationContext(), modelUser.getEmail(), Toast.LENGTH_SHORT ).show();
                        }

                    }
                    // adapter
                    adapterSearchProfile = new AdapterSearchProfile( getApplicationContext(), userList );
                    // reflesh adapter
                    adapterSearchProfile.notifyDataSetChanged();
                    // set adapter to recyler view
                    recyclerView.setVisibility( View.VISIBLE );
                    recyclerView.setAdapter( adapterSearchProfile );

                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );

    }

    private void searchUsersForMessage( String query ) {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Users" );
        // get all data from path
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                userList.clear();
                for ( DataSnapshot ds : snapshot.getChildren() ) {
                    ModelUsers modelUser = ds.getValue( ModelUsers.class );
                    // get all searched users except currently signed in user
                    if ( !modelUser.getUid().equals( fUser.getUid() ) ) {
                        if ( modelUser.getUsername().toLowerCase().contains( query.toLowerCase() ) ) {
                            userList.add( modelUser );
                            // silincek
                            Toast.makeText( getApplicationContext(), modelUser.getEmail(), Toast.LENGTH_SHORT ).show();
                        }

                    }
                    // adapter
                    adapterUsers = new AdapterUsers( getApplicationContext(), userList );
                    // reflesh adapter
                    adapterUsers.notifyDataSetChanged();
                    // set adapter to recyler view
                    recyclerView.setVisibility( View.VISIBLE );
                    recyclerView.setAdapter( adapterUsers );

                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );

    }

    private void getAllUsers() {

        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );
        // get all data from path
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                userList.clear();
                for ( DataSnapshot ds : snapshot.getChildren() ) {
                    ModelUsers modelUser = ds.getValue( ModelUsers.class );
                    // get all users except currently signed in user
                    if ( !modelUser.getUid().equals( fUser.getUid() ) ) {
                        userList.add( modelUser );
                    }
                    // adapter
                    adapterUsers = new AdapterUsers( getApplicationContext(), userList );
                    // set adapter to recyler view


                }
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {

            }
        } );

    }

    public void showPopup( View view ) {
        Dialog dialog;
        // Settings için olan kodlar
        if ( currentActivity == 5 ) {
            // Setings codu buraya
            int[] i = new int[1];
            myDialog.setContentView( R.layout.custom_settings );


            EditText currentPassword = myDialog.findViewById( R.id.currentPasswordPT );
            EditText newPassword = myDialog.findViewById( R.id.newPasswordPT );
            Button logout = myDialog.findViewById( R.id.logOutButton );
            Button changePassword = myDialog.findViewById( R.id.changePasswordButton );
            AppCompatButton clearTagsButton = myDialog.findViewById( R.id.clearTagsButton );
            AppCompatButton saveTagsButton = myDialog.findViewById( R.id.saveTagsButton );
            tagButton1 = myDialog.findViewById( R.id.sampleTag1 );
            tagButton2 = myDialog.findViewById( R.id.sampleTag2 );
            tagButton3 = myDialog.findViewById( R.id.sampleTag3 );
            userPpInDialog = myDialog.findViewById( R.id.userPp2 );


            tagButton1.setText( "" );
            tagButton2.setText( "" );
            tagButton3.setText( "" );

            tagsStatus[0] = false;
            tagsStatus[1] = false;
            tagsStatus[2] = false;

            //saveTagsButton.setEnabled(false);

            tagsArray = new AppCompatButton[]{ tagButton1, tagButton2, tagButton3 };
            Spinner tagSpinner = myDialog.findViewById( R.id.tagSpinner );

            ImageView changePp = myDialog.findViewById( R.id.changePp );

            try {
                //if image received, set
                StorageReference image = FirebaseStorage.getInstance().getReference( "BilkentUniversity/pp/" + mUID );
                image.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess( Uri uri ) {
                        Picasso.get().load( uri ).into( userPpInDialog );
                    }
                } );
            } catch ( Exception e ) {
                //if there is any exception while getting image then set default
                Picasso.get().load( R.drawable.user_pp_template ).into( userPpInDialog );
            }


            if ( !getTagsSaved() ) {
                setAllSettingsTagsInvisible();
            }


            tagSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
                    if ( position > 0 ) {
                        String selectedItem = parent.getItemAtPosition( position ).toString();
                        if ( i[0] < tagsStatus.length ) {

                            for ( int j = 0; j < 3; j++ ) {

                                if ( !tagsStatus[j] ) {

                                    tagsArray[j].setText( selectedItem );
                                    if ( !tagHasSelectedBefore( tagButton1, tagButton2, tagButton3 ) ) {
                                        tagsArray[j].setVisibility( View.VISIBLE );
                                        i[0]++;
                                        tagsStatus[j] = true;
                                        if ( i[0] == 3 ) {
                                            saveTagsButton.setEnabled( true );
                                        }

                                    } else {
                                        tagsStatus[j] = false;
                                        tagsArray[j].setText( "" );
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if (i[0] == tagsStatus.length) {
                        tagSpinner.setEnabled(false);
                    }
                }

                public void onNothingSelected( AdapterView<?> parent ) {
                    //TODO
                }
            } );

            clearTagsButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    i[0] = 0;
                    tagSpinner.setEnabled( true );
                    for ( int i = 0; i < tagsStatus.length; i++ ) {
                        tagsStatus[i] = false;
                    }

                    setAllSettingsTagsInvisible();
                    setTagsSaved( false );
                    Toast.makeText( getApplicationContext(), "All tags are cleared", Toast.LENGTH_LONG ).show();
                }
            } );

            //TODO hashmap
            saveTagsButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    if ( i[0] == 3 ) {
                        saveTagsButton.setError( null );
                        String tagIndexes = "";

                        for ( int k = 1; k < allTags.length; k++ ) {
                            if ( allTags[k].equals( tagButton1.getText().toString() ) || allTags[k].equals( tagButton2.getText().toString() ) || allTags[k].equals( tagButton3.getText().toString() ) ) {
                                tagIndexes = tagIndexes + k + ",";
                            }
                        }

                        if ( tagIndexes.length() > 0 ) {
                            StringBuilder tempString = new StringBuilder( tagIndexes );
                            tempString.deleteCharAt( tempString.length() - 1 );
                            tagIndexes = tempString.toString();
                        }

                        DatabaseReference reference = firebaseDatabase.getReference( "BilkentUniversity/Users/" + mUID );
                        // put data within hashmap in database
                        reference.child( "tags" ).setValue( tagIndexes );

                        Toast.makeText(getApplicationContext(), "all tags are saved", Toast.LENGTH_LONG).show();
                    } else {
                        saveTagsButton.setError( "3 tags must be selected!" );
                    }
                }
            } );

            changePassword.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {

                    if ( newPassword.getText().toString().trim().isEmpty() || currentPassword.getText().toString().trim().isEmpty() ) {
                        newPassword.setError( "Cannot be left empty!" );
                        currentPassword.setError( "Cannot be left empty!" );
                    } else {
                        mAuth.signInWithEmailAndPassword( mAuth.getCurrentUser().getEmail(), currentPassword.getText().toString() ).addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess( AuthResult authResult ) {
                                String password = newPassword.getText().toString();

                                if ( password.equals( currentPassword.getText().toString() ) ) {
                                    Toast.makeText( MainActivity.this, "New Password should be different than old one", Toast.LENGTH_SHORT ).show();
                                } else if ( password.length() >= 6 ) {
                                    mAuth.getCurrentUser().updatePassword( newPassword.getText().toString() );
                                    Toast.makeText( MainActivity.this, "Password was succesfully changed", Toast.LENGTH_SHORT ).show();
                                    currentPassword.setText( "" );
                                    newPassword.setText( "" );
                                } else {
                                    Toast.makeText( MainActivity.this, "Password must be at least 6 character", Toast.LENGTH_SHORT ).show();
                                }

                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure( @NonNull Exception e ) {
                                Toast.makeText( MainActivity.this, "Current password is wrong!", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                }

            } );

            logout.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    myDialog.dismiss();
                    mAuth.signOut();
                    Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
                    startActivity( intent );
                    finish();
                }
            } );

            changePp.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    String[] options = { "Camera", "Gallery" };
                    Context context;
                    AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
                    // set items to dialog
                    builder.setTitle( "Pick Image From" );
                    builder.setItems( options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialogInterface, int i ) {
                            if ( i == 0 ) {
                                //camera clicked
                                askCameraPermissions();

                            } else if ( i == 1 ) {
                                // gallery clicked
                                Intent gallery = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                                startActivityForResult( gallery, GALLERY_REQUEST_CODE );
                            }
                        }
                    } );
                    builder.show();
                }
            } );
            // Setings code bitimi
        }
        // notification için olan kodlar
        else {
            myDialog.setContentView( R.layout.custom_notification_popup );

            notificationsRv = myDialog.findViewById( R.id.notificationsRv );
            getAllNotifications();

        }

        myDialog.show();
        myDialog.getWindow().setBackgroundDrawable( new ColorDrawable( android.graphics.Color.TRANSPARENT ) );


        popUpButton = (ImageView) findViewById( R.id.showPopUpCreate );

        if ( currentActivity == 5 )
            popUpButton.setImageResource( R.drawable.settings_icon_open );
        else
            popUpButton.setImageResource( R.drawable.notifo );


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected( @NonNull MenuItem item ) {

                    switch ( item.getItemId() ) {
                        case R.id.nav_message:
                            if ( fm.findFragmentByTag( "1" ) == null ) {
                                fm.beginTransaction().add( R.id.fragment_container, messageFragment, "1" ).hide( messageFragment ).commit();
                            }
                            fm.beginTransaction().hide( active ).show( messageFragment ).commit();
                            active = messageFragment;
                            searchView.setQueryHint( "search to message" );
                            currentActivity = 1;
                            popUpButton.setImageResource( R.drawable.notif );


                            // Yeni mesaj için search için

                            searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit( String query ) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( query.trim() ) ) {
                                        searchUsersForMessage( query );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange( String newText ) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( newText.trim() ) ) {
                                        searchUsersForMessage( newText );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            } );
                            return true;
                        case R.id.nav_buddy:
                            if ( fm.findFragmentByTag( "2" ) == null ) {
                                fm.beginTransaction().add( R.id.fragment_container, buddyFragment, "2" ).hide( buddyFragment ).commit();
                            }
                            fm.beginTransaction().hide( active ).show( buddyFragment ).commit();
                            active = buddyFragment;
                            searchView.setQueryHint( "search users" );
                            currentActivity = 2;
                            popUpButton.setImageResource( R.drawable.notif );

                            // Profile search için
                            searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit( String query ) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( query.trim() ) ) {
                                        searchUsersForProfile( query );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange( String newText ) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( newText.trim() ) ) {
                                        searchUsersForProfile( newText );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            } );


                            return true;
                        case R.id.nav_club:
                            fm.beginTransaction().hide( active ).show( clubFragment ).commit();
                            active = clubFragment;
                            currentActivity = 3;
                            searchView.setQueryHint( "search users" );
                            popUpButton.setImageResource( R.drawable.notif );

                            // Profile search için
                            searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit( String query ) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( query.trim() ) ) {
                                        searchUsersForProfile( query );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange( String newText ) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( newText.trim() ) ) {
                                        searchUsersForProfile( newText );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            } );
                            return true;
                        case R.id.nav_stack:
                            if ( fm.findFragmentByTag( "4" ) == null ) {
                                fm.beginTransaction().add( R.id.fragment_container, stackFragment, "4" ).hide( stackFragment ).commit();
                            }
                            fm.beginTransaction().hide( active ).show( stackFragment ).commit();
                            active = stackFragment;
                            searchView.setQueryHint( "search users" );
                            currentActivity = 4;
                            popUpButton.setImageResource( R.drawable.notif );

                            // Profile search için
                            searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit( String query ) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( query.trim() ) ) {
                                        searchUsersForProfile( query );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange( String newText ) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( newText.trim() ) ) {
                                        searchUsersForProfile( newText );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            } );
                            return true;
                        case R.id.nav_profile:
                            if ( fm.findFragmentByTag( "5" ) == null ) {
                                fm.beginTransaction().add( R.id.fragment_container, profileFragment, "5" ).hide( profileFragment ).commit();
                            }
                            fm.beginTransaction().hide( active ).show( profileFragment ).commit();
                            active = profileFragment;
                            searchView.setQueryHint( "search users" );
                            currentActivity = 5;
                            popUpButton.setImageResource( R.drawable.settings_icon );

                            // Profile search için
                            searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit( String query ) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( query.trim() ) ) {
                                        searchUsersForProfile( query );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange( String newText ) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if ( !TextUtils.isEmpty( newText.trim() ) ) {
                                        searchUsersForProfile( newText );
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            } );
                            return true;
                    }
                    return false;
                }
            };

    private void NotifOrSettingsDecider() {
        if ( currentActivity == 5 ) {


        } else {
            myDialog.setOnDismissListener( new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss( DialogInterface dialog ) {
                    popUpButton.setImageResource( R.drawable.notif );
                }
            } );
        }

    }


    // setttings additional  methods
    private void setAllSettingsTagsInvisible() {
        for ( int i = 0; i < tagsArray.length; i++ ) {
            tagsArray[i].setVisibility( View.INVISIBLE );
        }
    }

    private boolean settingsTagsSavedCondition;

    private void setTagsSaved( boolean boo ) {
        settingsTagsSavedCondition = boo;
    }

    private boolean getTagsSaved() {
        return settingsTagsSavedCondition;
    }

    public static long getServerDate() {
        return dateServer + SystemClock.elapsedRealtime();
    }

    public void updateToken( String token ) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Tokens" );
        Token mToken = new Token( token );
        ref.child( mUID ).setValue( mToken );
    }

    private void checkUserStatus() {
        FirebaseAuth kAuth = FirebaseAuth.getInstance();
        FirebaseUser user = kAuth.getCurrentUser();

        if ( user != null ) {
            mUID = user.getUid();
        } else {
            Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
            startActivity( intent );
            finish();
        }
    }

    // Tag converter example
    //input 1,2,3 -> output dance,music,party (inş yani uykuluyken yazdım denemedim)
    public static String serverToPhoneTagConverter( String tags ) {
        String[] tagIndexes = tags.split( "," );
        StringBuilder returnTags = new StringBuilder();
        for ( int i = 0; i < tagIndexes.length; i++ ) {
            returnTags.append( Integer.parseInt( tagIndexes[i] ) );
            if ( i != returnTags.length() - 1 ) {
                returnTags.append( "," );
            }
        }
        return returnTags.toString();
    }

    //input 1 -> output #Party
    public String tagIndexToString( String indexString ) {
        int index = Integer.parseInt( indexString );
        return "#" + allTags[index];
    }

    private void getAllNotifications() {
        notificationsList = new ArrayList<>();
        DatabaseReference databaseReferenceNotif = firebaseDatabase.getReference( "BilkentUniversity/Notifications/" );
        databaseReferenceNotif.child( mAuth.getUid() )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
                        notificationsList.clear();
                        for ( DataSnapshot ds : snapshot.getChildren() ) {
                            // get data
                            ModelNotification model = ds.getValue( ModelNotification.class );
                            // add to list
                            notificationsList.add( model );
                        }
                        // adapter
                        adapterNotification = new AdapterNotification( getApplicationContext(), notificationsList );
                        // set to recycler view
                        notificationsRv.setAdapter( adapterNotification );

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
                        notificationsRv.setLayoutManager( linearLayoutManager );
                        linearLayoutManager.setStackFromEnd( true );
                        linearLayoutManager.setReverseLayout( true );

                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError error ) {

                    }
                } );
    }

    @Override
    protected void onResume() {

        // save uid of currently signed in user in shared preferences
        checkUserStatus();
        SharedPreferences sp = getSharedPreferences( "SP_USER", MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit();
        editor.putString( "Current_USERID", mUID );
        editor.apply();
        super.onResume();
    }

    public static String[] getAllTags() {
        return allTagz;
    }

    public boolean tagHasSelectedBefore( AppCompatButton tag1, AppCompatButton tag2, AppCompatButton tag3 ) {

        boolean boo;

        String tag1String = tag1.getText().toString();
        String tag2String = tag2.getText().toString();
        String tag3String = tag3.getText().toString();

        if ( !tag1String.equals( "" ) && !tag2String.equals( "" ) && !tag3String.equals( "" ) ) {

            return ( tag1String.equals( tag2String ) || tag2String.equals( tag3String ) || tag1String.equals( tag3String ) );
        } else {
            return ( tag1String.equals( tag2String ) && tag2String.equals( tag3String ) && tag1String.equals( tag3String ) );
        }
    }

    void askCameraPermissions() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.CAMERA }, CAMERA_PERM_CODE );
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        if ( requestCode == CAMERA_PERM_CODE ) {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText( this, "Camera Permission is required to use camera", Toast.LENGTH_SHORT ).show();
            }
        }
    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == CAMERA_REQUEST_CODE ) {
            if ( resultCode == Activity.RESULT_OK ) {
                File f = new File( currentPhotoPath );
                //userPpInDialog.setImageURI(Uri.fromFile(f));
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
                Uri contentUri = Uri.fromFile( f );
                mediaScanIntent.setData( contentUri );
                this.sendBroadcast( mediaScanIntent );

                if ( userPpInDialog != null ) {
                    uploadImageToFirebase( userPpInDialog, contentUri );
                }
            }
        }

        if ( requestCode == GALLERY_REQUEST_CODE ) {
            if ( resultCode == Activity.RESULT_OK ) {
                Uri contentUri = data.getData();
                //userPpInDialog.setImageURI(contentUri);
                if ( userPpInDialog != null ) {
                    uploadImageToFirebase( userPpInDialog, contentUri );
                }
            }
        }

            /*gelen resmi direkt koymak için
            Bitmap image = (Bitmap) data.getExtras().get("data");
            userPpInDialog.setImageBitmap(image);
             */


    }

    private String getFileExt( Uri contentUri ) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType( c.getType( contentUri ) );
    }


    private void uploadImageToFirebase( ImageView userPpInDialog, Uri contentUri ) {
        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference image = storageReference.child( "BilkentUniversity/pp/" + user.getUid() );
        image.putFile( contentUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess( UploadTask.TaskSnapshot taskSnapshot ) {
                image.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess( Uri uri ) {
                        Picasso.get().load( uri ).into( userPpInDialog );
                    }
                } );
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure( @NonNull Exception e ) {
                Toast.makeText( MainActivity.this, "Upload Failed", Toast.LENGTH_SHORT ).show();
            }
        } );
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Users/" + user.getUid() );
        HashMap<String, Object> updatePp = new HashMap<>();
        updatePp.put( "pp", "1" );
        userReference.updateChildren( updatePp ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess( Void aVoid ) {
                updatePp.clear();
                updatePp.put( "pp", user.getUid() );
                userReference.updateChildren( updatePp );
            }
        } );
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        // Ensure that there's a camera activity to handle the intent
        try {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch ( IOException ex ) {
                // Error occurred while creating the File
                System.out.println( "Error while creating the file" );
            }
            // Continue only if the File was successfully created
            if ( photoFile != null ) {
                Uri photoURI = FileProvider.getUriForFile( this,
                        "com.union.android.fileprovider",
                        photoFile );
                takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, photoURI );
                startActivityForResult( takePictureIntent, CAMERA_REQUEST_CODE );
            } else {
                System.out.println( "photo file is null" );
            }
        } catch ( Exception e ) {
            Toast.makeText( this, "No camera app", Toast.LENGTH_SHORT ).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Private için kod File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES );
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
        }
    }


    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
    */

}

//String[] strings = getResources().getStringArray(R.array.stack_tags); / tagleri arraye yerleştirme kodu