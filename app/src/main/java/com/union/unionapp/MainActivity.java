package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView selectedOptionTextView;
    Dialog myDialog;
    SearchView searchView;
    ImageView popUpButton;
    AdapterUsers adapterUsers;
    List<ModelUsers> userList;
    RecyclerView recyclerView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Offline support, uygulama bitince düşün
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        //init List
        userList = new ArrayList<>();


        //inits arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        mAuth = FirebaseAuth.getInstance();

        popUpButton = (ImageView) findViewById(R.id.showPopUpCreate);
        myDialog = new Dialog(this);

        //initial popup icon
        popUpButton.setBackground(null);
        popUpButton.setImageResource(R.drawable.notif);

        //clubtan başlatıyor
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_club);
        }

        // usersearch fragment

        recyclerView = findViewById(R.id.users_recyclerView);
        // Settings its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // searchView
        searchView = findViewById(R.id.searchTool);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // user bastıgında cagrılıo
                // if search query is not empty then search
                if (!TextUtils.isEmpty(query.trim())) {
                    searchUsers(query);
                } else {
                    // search text empty
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                // user yazı yazdıgında cagrılıyor
                // if search query is not empty then search
                if (!TextUtils.isEmpty(newText.trim())) {
                    searchUsers(newText);
                } else {
                    // search text empty, get all users
                }
                return false;
            }
        });
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

    private void searchUsers(String query) {
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
                    // get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        if (modelUser.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
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
            myDialog.setContentView(R.layout.custom_settings);

            EditText currentPassword = myDialog.findViewById(R.id.currentPasswordPT);
            EditText newPassword = myDialog.findViewById(R.id.newPasswordPT);
            Button logout = myDialog.findViewById(R.id.logOutButton);
            Button changePassword = myDialog.findViewById(R.id.changePasswordButton);

            ImageView changePp = myDialog.findViewById(R.id.changePp);

            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), currentPassword.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            String password = newPassword.getText().toString().trim();
                            System.out.println(password.length());
                            if (password.equals(currentPassword.getText().toString().trim())){
                                Toast.makeText(MainActivity.this, "New Password should be different than old one", Toast.LENGTH_SHORT).show();
                            }else if (password.length() >= 6) {
                                mAuth.getCurrentUser().updatePassword(newPassword.getText().toString());
                                Toast.makeText(MainActivity.this, "Password was succesfully changed", Toast.LENGTH_SHORT).show();
                                currentPassword.setText("");
                                newPassword.setText("");
                            }else{
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
                    System.out.println("burada");
                    System.out.println(grantResults.length);
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    System.out.println(cameraAccepted);
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    System.out.println(writeStorageAccepted);
                    if (cameraAccepted && writeStorageAccepted) {
                        System.out.println("dadsda");
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    System.out.println("şurada");
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    System.out.println(writeStorageAccepted);
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
        System.out.println("geldi");
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
                System.out.println("burada2");
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //TODO Ekranda herhang biyere basıldığında SearchView focus kapama
    // Geriye basıldığında searchü kapatır
    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
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
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_message:
                            selectedFragment = new MessageFragment();
                            currentActivity = 1;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_buddy:
                            selectedFragment = new BuddyFragment();
                            currentActivity = 2;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_club:
                            selectedFragment = new ClubsFragment();
                            currentActivity = 3;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_stack:
                            selectedFragment = new StackFragment();
                            currentActivity = 4;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            currentActivity = 5;
                            popUpButton.setImageResource(R.drawable.settings_icon);
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
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


}

//String[] strings = getResources().getStringArray(R.array.stack_tags); / tagleri arraye yerleştirme kodu