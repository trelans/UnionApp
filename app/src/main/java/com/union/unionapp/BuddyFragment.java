package com.union.unionapp;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class BuddyFragment extends Fragment {

    Dialog buddyDialog;
    Spinner genderSpinner;
    Spinner tagSpinner;
    Spinner preferredGenderFilterSpinner;
    Spinner filterTagSpinner;

    EditText postDetailsEt,
            postQuotaEt,
            postLocationEt,
            filterQuotaEt,
            filterLocationEt;

    TextView postDateEt,
             postTimeEt,
             filterDate,
             filterTime;


    AppCompatButton tag1,
                    tag2,
                    tag3,
                    filterTag1,
                    filterTag2,
                    filterTag3;

    AppCompatButton[] tagsArray;

    boolean[] tagsStatus = {false, false, false};
    int[] tagTextsIndexArray = new int[3];
    int[] i = new int[1];

    int lastDeletedtag = 0;

    ImageView imageIv,
            sendButtonIv,
            addPhotoIv;

    DatabaseReference userDbRef;
    FirebaseAuth firebaseAuth;
    Uri image_uri;

    RecyclerView recyclerView;
    List<ModelBuddyAndClubPost> postList;
    AdapterBuddyPosts adapterBuddyPosts;

    String date;
    String[] allTags;
    TextView[] textViewTags;
    DatePickerDialog.OnDateSetListener setListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;



    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK__GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    // arrays of permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //user info
    String username, email, uid, dp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_buddy, container, false);

        ImageView filterImageView = (ImageView) view.findViewById(R.id.showBuddyFilterPopup);
        ImageView createPost = (ImageView) view.findViewById(R.id.showPopUpCreate);

        buddyDialog = new Dialog(getActivity());

        // Layoutu transparent yapıo
        buddyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //genderSpinner.setOnItemSelectedListener(this);

        allTags = getResources().getStringArray(R.array.all_tags);

        //inits arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();

        //get some info of the current user to include in the post
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    username = "" + ds.child("username").getValue();
                    dp = "" + ds.child("pp").getValue();
                    //email = "" + ds.child("email").getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //recycler view and its properties
        recyclerView = view.findViewById(R.id.buddyPostsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        postList = new ArrayList<>();
        loadPosts();


        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                buddyDialog.setContentView(R.layout.custom_create_post_buddy_popup);

                genderSpinner = buddyDialog.findViewById(R.id.genderSpinner);
                ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.gender_preferences, android.R.layout.simple_spinner_item);
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                genderSpinner.setAdapter(genderAdapter);

                tagSpinner = buddyDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.buddy_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(tagAdapter);

                tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            if (i[ 0 ] < tagsStatus.length) {

                                for (int j = 0; j < 3; j++) {

                                    if (!tagsStatus[j]) {

                                            tagsArray[j].setText(selectedItem);
                                            if (!tagHasSelectedBefore(tag1,tag2,tag3)) {
                                                tagsArray[j].setVisibility(View.VISIBLE);
                                                i[0]++;
                                                tagsStatus[j] = true;
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

                //init views
                postDetailsEt = buddyDialog.findViewById(R.id.editTextPostDetails);
                postDateEt = buddyDialog.findViewById(R.id.editTextDate);
                postQuotaEt = buddyDialog.findViewById(R.id.editTextQuota);
                postTimeEt = buddyDialog.findViewById(R.id.editTextTime);
                sendButtonIv = buddyDialog.findViewById(R.id.imageViewSendButton);
                addPhotoIv = buddyDialog.findViewById(R.id.uploadPhotoImageView);
                postLocationEt = buddyDialog.findViewById(R.id.editTextLocation);

                //init tags
                tag1 = buddyDialog.findViewById(R.id.textViewTag1);
                tag2 = buddyDialog.findViewById(R.id.textViewTag2);
                tag3 = buddyDialog.findViewById(R.id.textViewTag3);

                //set tag texts to empty string
                tag1.setText("");
                tag2.setText("");
                tag3.setText("");

                //set tags to invisible
                tag1.setVisibility(View.INVISIBLE);
                tag2.setVisibility(View.INVISIBLE);
                tag3.setVisibility(View.INVISIBLE);

                //set tags to disabled -- not needed
                //tag1.setEnabled(false);
                //tag2.setEnabled(false);
                //tag3.setEnabled(false);

                textViewTags = new TextView[]{tag1, tag2, tag3};
                tagsArray = new AppCompatButton[]{tag1, tag2, tag3};

                //set onClickListeners for tags
                tag1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tag1.setVisibility(View.INVISIBLE);
                        tagsStatus[0] = false;
                        tagSpinner.setEnabled( true );
                        i[0]--;
                        lastDeletedtag = 0;
                    }
                });

                tag2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tag2.setVisibility(View.INVISIBLE);
                        tagsStatus[1] = false;
                        tagSpinner.setEnabled( true );
                        i[0]--;
                        lastDeletedtag = 1;
                    }
                });

                tag3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tag3.setVisibility(View.INVISIBLE);
                        tagsStatus[2] = false;
                        tagSpinner.setEnabled( true );
                        i[0]--;
                        lastDeletedtag = 2;
                    }
                });



                //set the postDateEt to current date for default
                Calendar defaultCalendar = Calendar.getInstance();
                calendarToString(defaultCalendar);
                postDateEt.setText(date);

                //setting up the calendar dialog
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                postDateEt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, day, month, year
                        );
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();
                    }
                });

                postTimeEt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hourOfDay, minute, true
                        );
                        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        timePickerDialog.show();
                    }
                });
                setListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = dayOfMonth + "/" + month + "/" + year;
                        postDateEt.setText(date);

                    }
                };

                timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        postTimeEt.setText(time);
                    }
                };

                addPhotoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //show image pick dialog
                        showImagePickDialog();
                    }
                });

                sendButtonIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String postDetails = postDetailsEt.getText().toString().trim();
                        String postDate = postDateEt.getText().toString().trim();
                        String postQuotaStr = postQuotaEt.getText().toString().trim();
                        String postTime = postTimeEt.getText().toString().trim();
                        String postLocation = postLocationEt.getText().toString().trim();
                        String postGender = genderSpinner.getSelectedItem().toString();
                        String tagsToUpload = "";

                        for ( int k = 1; k < allTags.length; k++ ) {
                            if ( allTags[ k ].equals( tag1.getText().toString() ) || allTags[ k ].equals( tag2.getText().toString() ) || allTags[ k ].equals( tag3.getText().toString() ) ) {
                                tagsToUpload = tagsToUpload + k + ",";
                            }
                        }
                        /*
                        //tags to upload'un sonundaki virgülü atıyor //TODO ömerin yolu
                        StringBuilder tempString = new StringBuilder(tagsToUpload);
                        tempString.deleteCharAt(tempString.length()-1);
                        tagsToUpload = tempString.toString();

                         */

                        if (TextUtils.isEmpty(postDetails)) {
                            Toast.makeText(getActivity(), "Enter post Details", Toast.LENGTH_SHORT);
                            return;
                        }

                        if (image_uri == null) {
                            //post without image
                            uploadData(postDetails, postDate, postTime, postQuotaStr, "noImage", postLocation, tagsToUpload, postGender);
                        } else {
                            //post with image
                            uploadData(postDetails, postDate, postTime, postQuotaStr, String.valueOf(image_uri), postLocation, tagsToUpload, postGender);
                        }
                        buddyDialog.dismiss();
                    }
                });


                buddyDialog.show();
            }
        });



        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buddyDialog.setContentView(R.layout.custom_buddy_filter);

                //init views
                ImageView filterImageView = buddyDialog.findViewById(R.id.searchFiltersImageView);
                ImageView resetFiltersImageView = buddyDialog.findViewById(R.id.cancelFilterImageView);
                TextView filterDateTv = buddyDialog.findViewById(R.id.dateFilterEditText);
                TextView filterTimeTv = buddyDialog.findViewById(R.id.timeFilterEditText);
                EditText filterLocationEt = buddyDialog.findViewById(R.id.locationFilterEditText);
                EditText filterQuotaEt = buddyDialog.findViewById(R.id.quotaFilterEditText);

                tag1 = buddyDialog.findViewById(R.id.filterTag1TextView);
                tag2 = buddyDialog.findViewById(R.id.filterTag2TextView);
                tag3 = buddyDialog.findViewById(R.id.filterTag3TextView);


                tagSpinner = buddyDialog.findViewById(R.id.tagSpinnerFilter);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.buddy_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(tagAdapter);




                String filterTagsToUpload = "";
                /*

                for ( int k = 1; k < allTags.length; k++ ) {
                    if ( allTags[ k ].equals( tag1.getText().toString() ) || allTags[ k ].equals( tag2.getText().toString() ) || allTags[ k ].equals( tag3.getText().toString() ) ) {
                        filterTagsToUpload = filterTagsToUpload + k + ",";
                    }
                }

                //tags to upload'un sonundaki virgülü atıyor
                StringBuilder tempString = new StringBuilder(filterTagsToUpload);
                tempString.deleteCharAt(tempString.length()-1);
                filterTagsToUpload = tempString.toString();
                */

                filterImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //save the user's filter choices
                        String filterDate = filterDateTv.getText().toString().trim();
                        String filterQuota = filterQuotaEt.getText().toString().trim();
                        String filterTime = filterTimeTv.getText().toString().trim();
                        String filterLocation = filterLocationEt.getText().toString().trim();

                        DatabaseReference queryRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity/BuddyPosts");
                        Query query = queryRef.orderByChild("pQuota").equalTo(filterQuota);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Toast.makeText(getActivity(), "Oluyor buraya kadar", Toast.LENGTH_SHORT).show();

                                postList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    System.out.println(filterQuota);
                                    System.out.println(ds.getValue());
                                    ModelBuddyAndClubPost modelBuddyPost = ds.getValue(ModelBuddyAndClubPost.class);

                                    if (modelBuddyPost.getpQuota().contains(filterQuota)) {
                                        postList.add(modelBuddyPost);
                                        System.out.println();
                                    }
                                }
                                // adapter
                                adapterBuddyPosts = new AdapterBuddyPosts(getActivity(), postList);
                                adapterBuddyPosts.notifyDataSetChanged();
                                // set adapter to recyclerView
                                recyclerView.setAdapter(adapterBuddyPosts);


                                    /*
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

                                     */



                            }




                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });






                //set on click listener to reset filters
                resetFiltersImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO Reset Selected filters and set the feed back to normal feed.
                    }
                });


                buddyDialog.show();
            }
        });

        /*
        //dialog dismiss listener
        buddyDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                //set to 0 zero in order to prevent blocking spinner due to the previous posts.
                i[0] = 0;

                //set tags to empty strings
                tag1.setText("");
                tag2.setText("");
                tag3.setText("");

                //set boolean array to false only
                tagsStatus[0] = false;
                tagsStatus[1] = false;
                tagsStatus[2] = false;

            }
        });
        */

        return view;

    }

    private void loadPosts() {
        // path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/BuddyPosts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelBuddyAndClubPost modelBuddyPost = ds.getValue(ModelBuddyAndClubPost.class);
                    postList.add(modelBuddyPost);

                    // adapter
                    adapterBuddyPosts = new AdapterBuddyPosts(getActivity(), postList);
                    // set adapter to recyclerView
                    recyclerView.setAdapter(adapterBuddyPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // in case of error
                Toast.makeText(getActivity(), "Error on load post method 214. line", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(String searchQuery) {

    }

    private void showImagePickDialog() {
        //options (camera, gallery) to show in dialog
        String[] options = {"Camera", "Gallery"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image from");

        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //item click handle
                if (which == 0) {
                    //camera clicked

                }
                if (which == 1) {
                    //gallery clicked
                }
            }
        });
        //create and show dialog
    }


    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        // request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        // request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromCamera() {

        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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

    private void checkUserStatus() {
        //get curremt user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in

            email = user.getEmail();
            username = email.split("@")[0].replace(".", "_");
            uid = user.getUid();

        }
    }

    private void uploadData(String postDetails, String postDate, String postTime, String postQuotaStr, String uri, String postLocation, String tagsToUpload, String postGender) {
        //for post-image name, post-id, post-publish-time
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;

        if (!uri.equals("noImage")) {
            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase, now get its uri
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                //uri is received upload post to firebase database
                                checkUserStatus();
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put post info
                                hashMap.put("uid", uid); //çekememiş
                                hashMap.put("username", username); //çekmemiş
                                //hashMap.put("uEmail",email);
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pDetails", postDetails);
                                hashMap.put("pDate", postDate);
                                hashMap.put("pHour", postTime);
                                hashMap.put("pQuota", postQuotaStr);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timeStamp);
                                hashMap.put("pLocation", postLocation);
                                hashMap.put("pTags", tagsToUpload); //TODO tagler için değişicek
                                hashMap.put("pGender", postGender);

                                //path to store post data
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("BuddyPosts");

                                //put data in this ref
                                reference.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added in database
                                                Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT);
                                                //TODO reset views
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed adding post in database
                                                Toast.makeText(getActivity(), "Failed publishing post", Toast.LENGTH_SHORT);
                                            }
                                        });


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            Toast.makeText(getActivity(), "Failed uploading image", Toast.LENGTH_SHORT);
                        }
                    });

        } else {
            //post without image
            checkUserStatus();
            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid", uid);
            hashMap.put("username", username);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pDetails", postDetails);
            hashMap.put("pDate", postDate);
            hashMap.put("pHour", postTime);
            hashMap.put("pQuota", postQuotaStr);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);
            hashMap.put("pLocation", postLocation);
            hashMap.put("pTags", tagsToUpload);
            hashMap.put("pGender", postGender);

            //path to store post data
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("BuddyPosts");

            //put data in this ref
            reference.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT);
                            //TODO reset views

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding post in database
                            Toast.makeText(getActivity(), "Failed publishing post", Toast.LENGTH_SHORT);
                        }
                    });


        }

    }

    public void calendarToString(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());
    }

    /*
    i[ 0 ] = 0;
    if( !getTagsSaved() ) {
        setAllSettingsTagsInvisible();
    }


            tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            String selectedItem = parent.getItemAtPosition(position).toString();
            while (i[ 0 ] < tagsStatus.length) {
                if (!tagsStatus[ i[ 0 ] ] ) {
                    tagsStatus[ i[ 0 ] ] = true;
                    tagsArray[ i[ 0 ] ].setText( selectedItem );
                    tagsArray[ i[ 0 ] ].setVisibility( View.VISIBLE );
                    i[ 0 ]++;
                    break;
                }
            }
        }

        if( i[ 0 ] == tagsStatus.length ) {
            Toast.makeText( getApplicationContext(), "All tags are fixed", Toast.LENGTH_LONG ).show();
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

    private void setAllSettingsTagsInvisible() {
        for ( int i = 0; i < textViewTags.length; i++ ) {
            textViewTags[ i ].setVisibility( View.INVISIBLE );
        }
    }

    private boolean settingsTagsSavedCondition;

    private void setTagsSaved( boolean boo ) {
        settingsTagsSavedCondition = boo;
    }
    private boolean getTagsSaved() {
        return settingsTagsSavedCondition;
    }*/

    public boolean tagHasSelectedBefore(AppCompatButton tag1, AppCompatButton tag2, AppCompatButton tag3) {
        String tag1String = tag1.getText().toString();
        String tag2String = tag2.getText().toString();
        String tag3String = tag3.getText().toString();

        return ( tag1String.equals(tag2String) && tag2String.equals(tag3String) && tag1String.equals(tag3String) );
    }
}


