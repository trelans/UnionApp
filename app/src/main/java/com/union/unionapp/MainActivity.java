package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.union.unionapp.notifications.Token;


import java.util.ArrayList;
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
    final int[] clickCount = {0};
    Fragment active;
    String mUID;

    private static String[] allTagz;

    int currentActivity = 3;     // 1 Messages / 2 Buddy / 3 Club / 4 Stack / 5 Profile
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK__GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    // arrays of permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //storage
    StorageReference storageReference;
    //path where images of user profile will be stored
    String storagePath = "Users_Profile_Imgs/";

    // uri of picked images
    Uri image_uri;


    // settings
    AppCompatButton tagButton1;
    AppCompatButton tagButton2;
    AppCompatButton tagButton3;
    int[] tagTextsIndexArray = new int[3];
    AppCompatButton[] tagsArray;
    String[] allTags;
    boolean[] tagsStatus = {false, false, false};

    // String[] allTags = getResources().getStringArray( R.array.all_tags );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Main activity", Toast.LENGTH_SHORT).show();
        fm.beginTransaction().add(R.id.fragment_container, clubFragment, "3").commit();

        active = clubFragment;

       checkUserStatus();
        // server time listener attached
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot);
                double offset = snapshot.getValue(Double.class);
                dateServer = ((long) offset) + System.currentTimeMillis() - SystemClock.elapsedRealtime();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("BilkentUniversity/Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        /* firebaseden bir şey silme kodu
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                    FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" + modelUsers.getUid() + "/Notifications").removeValue();
                    System.out.println("oluyor");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         */



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        //for settings - ege
        //*
        allTags = getResources().getStringArray(R.array.all_tags);
        allTagz = allTags;

        //*

        searchBarEmpty = true;
        //init List
        userList = new ArrayList<>();


        //inits arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        mAuth = FirebaseAuth.getInstance();



        // update token
        updateToken(FirebaseInstanceId.getInstance().getToken());

        popUpButton = (ImageView) findViewById(R.id.showPopUpCreate);
        myDialog = new Dialog(this);
        myDialog.setCanceledOnTouchOutside(true);

        //initial popup icon
        popUpButton.setBackground(null);
        popUpButton.setImageResource(R.drawable.notif);

        // searchView
        searchView = findViewById(R.id.searchTool);





        //clubtan başlatıyor
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_club);


        }

        // usersearch fragment


        recyclerView = findViewById(R.id.users_recyclerView);
        // Settings its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        // visibility ayarları
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setVisibility(View.GONE);
                return false;
            }
        });
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (currentActivity == 5) {
                    popUpButton.setImageResource(R.drawable.settings_icon);
                } else {
                    popUpButton.setImageResource(R.drawable.notif);
                }
            }
        });


    }

    // klavyeyi dışarı tıklayınca kapatmaya yarıyor
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            searchView.clearFocus();
            if (searchBarEmpty) {
                searchView.setIconified(true);
            }
            clickCount[0]++;
        } else if (recyclerView.getVisibility() == View.VISIBLE) {
            if (clickCount[0] > 1) {
                recyclerView.setVisibility(View.GONE);
                clickCount[0] = 0;
            } else {
                clickCount[0]++;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void searchUsersForProfile(String query) {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users");
        // get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUsers modelUser = ds.getValue(ModelUsers.class);
                    // get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        if (modelUser.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
                            // silincek
                            Toast.makeText(getApplicationContext(), modelUser.getEmail(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    // adapter
                    adapterSearchProfile = new AdapterSearchProfile(getApplicationContext(), userList);
                    // reflesh adapter
                    adapterSearchProfile.notifyDataSetChanged();
                    // set adapter to recyler view
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapterSearchProfile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchUsersForMessage(String query) {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users");
        // get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUsers modelUser = ds.getValue(ModelUsers.class);
                    // get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        if (modelUser.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
                            // silincek
                            Toast.makeText(getApplicationContext(), modelUser.getEmail(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    // adapter
                    adapterUsers = new AdapterUsers(getApplicationContext(), userList);
                    // reflesh adapter
                    adapterUsers.notifyDataSetChanged();
                    // set adapter to recyler view
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapterUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllUsers() {

        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        // get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUsers modelUser = ds.getValue(ModelUsers.class);
                    // get all users except currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        userList.add(modelUser);
                    }
                    // adapter
                    adapterUsers = new AdapterUsers(getApplicationContext(), userList);
                    // set adapter to recyler view


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showPopup(View view) {
        Dialog dialog;
        // Settings için olan kodlar
        if (currentActivity == 5) {
            // Setings codu buraya
            int[] i = new int[1];
            myDialog.setContentView(R.layout.custom_settings);


            EditText currentPassword = myDialog.findViewById(R.id.currentPasswordPT);
            EditText newPassword = myDialog.findViewById(R.id.newPasswordPT);
            Button logout = myDialog.findViewById(R.id.logOutButton);
            Button changePassword = myDialog.findViewById(R.id.changePasswordButton);
            AppCompatButton clearTagsButton = myDialog.findViewById(R.id.clearTagsButton);
            AppCompatButton saveTagsButton = myDialog.findViewById(R.id.saveTagsButton);
            tagButton1 = myDialog.findViewById(R.id.sampleTag1);
            tagButton2 = myDialog.findViewById(R.id.sampleTag2);
            tagButton3 = myDialog.findViewById(R.id.sampleTag3);

            tagButton1.setText("");
            tagButton2.setText("");
            tagButton3.setText("");

            tagsStatus[0] = false;
            tagsStatus[1] = false;
            tagsStatus[2] = false;

            //saveTagsButton.setEnabled(false);

            tagsArray = new AppCompatButton[]{tagButton1, tagButton2, tagButton3};
            Spinner tagSpinner = myDialog.findViewById(R.id.tagSpinner);

            ImageView changePp = myDialog.findViewById(R.id.changePp);

            if (!getTagsSaved()) {
                setAllSettingsTagsInvisible();
            }


            tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        if (i[ 0 ] < tagsStatus.length) {

                            for (int j = 0; j < 3; j++) {

                                if (!tagsStatus[j]) {

                                    tagsArray[j].setText(selectedItem);
                                    if (!tagHasSelectedBefore(tagButton1,tagButton2,tagButton3)) {
                                        tagsArray[j].setVisibility(View.VISIBLE);
                                        i[0]++;
                                        tagsStatus[j] = true;
                                        if (i[0] == 3 ) {
                                            saveTagsButton.setEnabled(true);
                                        }

                                    }
                                    else {
                                        tagsStatus[j] = false;
                                        tagsArray[j].setText("");
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if( i[ 0 ] == tagsStatus.length ) {
                        //Toast.makeText( getApplicationContext(), "All tags are fixed", Toast.LENGTH_LONG ).show();
                        tagSpinner.setEnabled( false );
                        //tagSpinner.setClickable( false );
                        //tagSpinner.setTop( 1 );
                        //setTagsSaved( true );
                    }
                }

                public void onNothingSelected (AdapterView < ? > parent) {
                    //TODO
                }
            });

            clearTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i[0] = 0;
                    tagSpinner.setEnabled(true);
                    for (int i = 0; i < tagsStatus.length; i++) {
                        tagsStatus[i] = false;
                    }
                    //saveTagsButton.setEnabled(false);
                    //tagSpinner.setClickable( true );
                    setAllSettingsTagsInvisible();
                    setTagsSaved(false);
                    Toast.makeText(getApplicationContext(), "All tags are cleared", Toast.LENGTH_LONG).show();
                }
            });

            //TODO hashmap
            saveTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i[0] == 3) {
                        saveTagsButton.setError(null);
                        String tagIndexes = "";

                        for (int k = 1; k < allTags.length; k++) {
                            if (allTags[k].equals(tagButton1.getText().toString()) || allTags[k].equals(tagButton2.getText().toString()) || allTags[k].equals(tagButton3.getText().toString())) {
                                tagIndexes = tagIndexes + k + ",";
                            }
                        }

                        if (tagIndexes.length() > 0) {
                            StringBuilder tempString = new StringBuilder(tagIndexes);
                            tempString.deleteCharAt(tempString.length() - 1);
                            tagIndexes = tempString.toString();
                        }

                        DatabaseReference reference = firebaseDatabase.getReference("BilkentUniversity/Users/" + mUID);
                        // put data within hashmap in database
                        reference.child("tags").setValue(tagIndexes);

                        Toast.makeText(getApplicationContext(), tagIndexes, Toast.LENGTH_LONG).show();
                    }
                    else {
                        saveTagsButton.setError("3 tags must be selected!");
                    }
                }
            });

            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (newPassword.getText().toString().trim().isEmpty() || currentPassword.getText().toString().trim().isEmpty() ) {
                        newPassword.setError("Cannot be left empty!");
                        currentPassword.setError("Cannot be left empty!");
                    }
                    else {
                        mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), currentPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String password = newPassword.getText().toString();

                                if (password.equals(currentPassword.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "New Password should be different than old one", Toast.LENGTH_SHORT).show();
                                } else if (password.length() >= 6) {
                                    mAuth.getCurrentUser().updatePassword(newPassword.getText().toString());
                                    Toast.makeText(MainActivity.this, "Password was succesfully changed", Toast.LENGTH_SHORT).show();
                                    currentPassword.setText("");
                                    newPassword.setText("");
                                } else {
                                    Toast.makeText(MainActivity.this, "Password must be at least 6 character", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Current password is wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                    mAuth.signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            changePp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String options[] = {"Camera", "Gallery"};
                    Context context;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    // set items to dialog
                    builder.setTitle("Pick Image From");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                //camera clicked
                                if (!checkCameraPermission()) {
                                    requestCameraPermission();
                                } else {
                                    pickFromCamera();
                                }
                            } else if (i == 1) {
                                // gallery clicked
                                if (!checkStoragePermission()) {
                                    requestStoragePermission();
                                } else {
                                    pickFromGallery();
                                }
                            }
                        }
                    });
                    builder.show();
                }
            });

            // Setings code bitimi
        }
        // notification için olan kodlar
        else {
            myDialog.setContentView(R.layout.custom_notification_popup);



            notificationsRv = myDialog.findViewById(R.id.notificationsRv);
            getAllnotificiations();

        }

        myDialog.show();
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        popUpButton = (ImageView) findViewById(R.id.showPopUpCreate);

        if (currentActivity == 5)
            popUpButton.setImageResource(R.drawable.settings_icon_open);
        else
            popUpButton.setImageResource(R.drawable.notifo);


    }



    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        // request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        // request runtime storage permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //This method will be called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK__GALLERY_CODE) {
                //image is picked from gallery, get uri of image
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //image is picked from camera, get uri of image

                image_uri = data.getData();
                uploadProfilePhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
        //path and name of the image to be stored in firebase storage
        String filePathAndName = storagePath + "images/" + mAuth.getCurrentUser().getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image is uploaded to firebase storage, now get it's url and store in user's database
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUri = uriTask.getResult();
                // check if image is uploaded or not and url is received from
                if (uriTask.isSuccessful()) {
                    //image uploaded
                    //add/update url in user's database
                    HashMap<String, Object> results = new HashMap<>();
                    results.put("images", downloadUri.toString());
                    databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Image Updated...", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error Updating Image...", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //error
                    Toast.makeText(MainActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //there were some error(s), get and show error message dismis progress dialog
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);


    }

    private void pickFromGallery() {
        // pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK__GALLERY_CODE);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_message:
                            if (fm.findFragmentByTag("1") == null) {
                                fm.beginTransaction().add(R.id.fragment_container, messageFragment, "1").hide(messageFragment).commit();
                            }
                            fm.beginTransaction().hide(active).show(messageFragment).commit();
                            active = messageFragment;
                            searchView.setQueryHint("search to message");
                            currentActivity = 1;
                            popUpButton.setImageResource(R.drawable.notif);


                            // Yeni mesaj için search için

                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(query.trim())) {
                                        searchUsersForMessage(query);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(newText.trim())) {
                                        searchUsersForMessage(newText);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            });
                            return true;
                        case R.id.nav_buddy:
                            if (fm.findFragmentByTag("2") == null) {
                                fm.beginTransaction().add(R.id.fragment_container, buddyFragment, "2").hide(buddyFragment).commit();
                            }
                            fm.beginTransaction().hide(active).show(buddyFragment).commit();
                            active = buddyFragment;
                            searchView.setQueryHint("search users");
                            currentActivity = 2;
                            popUpButton.setImageResource(R.drawable.notif);

                            // Profile search için
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(query.trim())) {
                                        searchUsersForProfile(query);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(newText.trim())) {
                                        searchUsersForProfile(newText);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            });


                            return true;
                        case R.id.nav_club:
                            fm.beginTransaction().hide(active).show(clubFragment).commit();
                            active = clubFragment;
                            currentActivity = 3;
                            searchView.setQueryHint("search users");
                            popUpButton.setImageResource(R.drawable.notif);

                            // Profile search için
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(query.trim())) {
                                        searchUsersForProfile(query);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(newText.trim())) {
                                        searchUsersForProfile(newText);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            });
                            return true;
                        case R.id.nav_stack:
                            if (fm.findFragmentByTag("4") == null) {
                                fm.beginTransaction().add(R.id.fragment_container, stackFragment, "4").hide(stackFragment).commit();
                            }
                            fm.beginTransaction().hide(active).show(stackFragment).commit();
                            active = stackFragment;
                            searchView.setQueryHint("search users");
                            currentActivity = 4;
                            popUpButton.setImageResource(R.drawable.notif);

                            // Profile search için
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(query.trim())) {
                                        searchUsersForProfile(query);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(newText.trim())) {
                                        searchUsersForProfile(newText);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            });
                            return true;
                        case R.id.nav_profile:
                            if (fm.findFragmentByTag("5") == null) {
                                fm.beginTransaction().add(R.id.fragment_container, profileFragment, "5").hide(profileFragment).commit();
                            }
                            fm.beginTransaction().hide(active).show(profileFragment).commit();
                            active = profileFragment;
                            searchView.setQueryHint("search users");
                            currentActivity = 5;
                            popUpButton.setImageResource(R.drawable.settings_icon);

                            // Profile search için
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    // user bastıgında cagrılıo
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(query.trim())) {
                                        searchUsersForProfile(query);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }


                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    // user yazı yazdıgında cagrılıyor
                                    // if search query is not empty then search
                                    if (!TextUtils.isEmpty(newText.trim())) {
                                        searchUsersForProfile(newText);
                                        searchBarEmpty = false;
                                    } else {
                                        // search text empty, get all users
                                        searchBarEmpty = true;
                                    }
                                    return false;
                                }
                            });
                            return true;
                    }
                    return false;
                }
            };

    private void NotifOrSettingsDecider() {
        if (currentActivity == 5) {


        } else {
            myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    popUpButton.setImageResource(R.drawable.notif);
                }
            });
        }

    }


    // setttings additional  methods
    private void setAllSettingsTagsInvisible() {
        for (int i = 0; i < tagsArray.length; i++) {
            tagsArray[i].setVisibility(View.INVISIBLE);
        }
    }

    private boolean settingsTagsSavedCondition;

    private void setTagsSaved(boolean boo) {
        settingsTagsSavedCondition = boo;
    }

    private boolean getTagsSaved() {
        return settingsTagsSavedCondition;
    }

    public static long getServerDate(){
        return dateServer + SystemClock.elapsedRealtime();
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Tokens");
        Token mToken = new Token(token);
        System.out.println(" " + mUID);
        ref.child(mUID).setValue(mToken);
    }
    private void checkUserStatus() {
        FirebaseAuth kAuth = FirebaseAuth.getInstance();
        FirebaseUser user = kAuth.getCurrentUser();

        if (user != null) {
            mUID = user.getUid();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    // Tag converter example
     //input 1,2,3 -> output dance,music,party (inş yani uykuluyken yazdım denemedim)
    public static String serverToPhoneTagConverter(String tags) {
        String[] tagIndexes = tags.split( "," );
        StringBuilder returnTags = new StringBuilder();
        for (int i = 0; i < tagIndexes.length; i++) {
            returnTags.append(Integer.parseInt(tagIndexes[i]));
            if (i != returnTags.length() - 1){
                returnTags.append(",");
            }
        }
        return returnTags.toString();
    }

    //input 1 -> output #Party
    public String tagIndexToString(String indexString) {
        int index = Integer.parseInt(indexString);
        return "#" + allTags[index];
    }

    private void getAllnotificiations() {
        notificationsList = new ArrayList<>();
       DatabaseReference databaseReferenceNotif = firebaseDatabase.getReference("BilkentUniversity/Notifications/");
        databaseReferenceNotif.child(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // get data
                            ModelNotification model = ds.getValue(ModelNotification.class);
                            // add to list
                            notificationsList.add(model);
                        }
                        // adapter
                        adapterNotification = new AdapterNotification(getApplicationContext() , notificationsList);
                        // set to recycler view
                        notificationsRv.setAdapter(adapterNotification);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        notificationsRv.setLayoutManager(linearLayoutManager);
                        linearLayoutManager.setStackFromEnd(true);
                        linearLayoutManager.setReverseLayout(true);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    protected void onResume() {

        // save uid of currently signed in user in shared preferences
        checkUserStatus();
        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID", mUID);
        editor.apply();
        super.onResume();
    }

    public static String[] getAllTags() {
        return allTagz;
    }

    public boolean tagHasSelectedBefore(AppCompatButton tag1, AppCompatButton tag2, AppCompatButton tag3) {

        boolean boo;

        String tag1String = tag1.getText().toString();
        String tag2String = tag2.getText().toString();
        String tag3String = tag3.getText().toString();

        if (!tag1String.equals("") && !tag2String.equals("") && !tag3String.equals("")) {

            return (tag1String.equals(tag2String) || tag2String.equals(tag3String) || tag1String.equals(tag3String));
        } else {
            return (tag1String.equals(tag2String) && tag2String.equals(tag3String) && tag1String.equals(tag3String));
        }
    }

}

//String[] strings = getResources().getStringArray(R.array.stack_tags); / tagleri arraye yerleştirme kodu