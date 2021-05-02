package com.union.unionapp;



import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StackFragment extends Fragment {

    Dialog stackDialog;
    Spinner stackTagSpinner;

    EditText postTitleEt;
    EditText postDetailsEt;

    String postAnonymously;
    String postDetails;
    String tagToUpload;
    String postTitle;
    String timestamp;
    String filterTagsToUpload;

    //Achievements
    int mathScore;// = 0;
    int careerScore;// = 0;
    int sportScore;// = 0;
    int technologyScore;// = 0;
    int socialScore;// = 0;
    int englishScore;// = 0;
    int turkishScore;// = 0;
    int studyScore;// = 0;

    String title, description, point  , nId , level;
    // Achievements

    DatabaseReference userDbRefPosts;

    AppCompatButton stackTag;

    CheckBox anonym;

    int tagTextIndex;

    ProgressBar pb;

    ImageView sendButtonIv,
              addPhotoIv;

    DatabaseReference userDbRef;
    FirebaseAuth firebaseAuth;
    Uri image_uri;

    RecyclerView recyclerView;
    List<ModelStackPost> postList;
    AdapterStackPosts adapterStackPosts;

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
        View view = inflater.inflate(R.layout.fragment_stack, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        ImageView filterImageView = (ImageView) view.findViewById(R.id.showStackFilterPopup);
        ImageView createPost = (ImageView) view.findViewById(R.id.showPopUpCreate);
        stackDialog = new Dialog(getActivity());
        // Layoutu transparent yapıo
        stackDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pb = view.findViewById(R.id.progressBar);

        //recycler view and its properties
        recyclerView = view.findViewById(R.id.stackPostsRecyclerView);
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

                stackDialog.setContentView(R.layout.custom_stack_createpost_popup);
                stackDialog.setCanceledOnTouchOutside(true);

                String[] allTags = getResources().getStringArray( R.array.all_tags );



                stackTagSpinner = stackDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.stack_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stackTagSpinner.setAdapter(tagAdapter);




                //init views
                sendButtonIv = stackDialog.findViewById(R.id.sendButtonImageView);
                addPhotoIv = stackDialog.findViewById(R.id.uploadPhotoImageView);
                postDetailsEt = stackDialog.findViewById(R.id.postDetailsEt);
                anonym = stackDialog.findViewById(R.id.checkBoxAnonymous);
                postTitleEt = stackDialog.findViewById(R.id.editTextHeadLine);

                sendButtonIv.setEnabled(false);

                postTitleEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!postTitleEt.getText().toString().trim().isEmpty()) {
                            sendButtonIv.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


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

                        postTitle = postTitleEt.getText().toString().trim();
                        if (postTitle.isEmpty()) {
                            postTitleEt.setError("The title section cannot be left empty.");
                        }
                        else {
                            String selectedTag = stackTagSpinner.getSelectedItem().toString();
                            for (int k = 1; k < allTags.length; k++) {
                                if (allTags[k].equals(selectedTag)) {
                                    tagTextIndex = k;
                                    break;
                                }
                            }

                            tagToUpload = tagTextIndex + "";


                            postDetails = postDetailsEt.getText().toString().trim();
                            if (anonym.isChecked()) {
                                postAnonymously = "1";
                            } else {
                                postAnonymously = "0";
                            }

                            if (TextUtils.isEmpty(postDetails)) {
                                Toast.makeText(getActivity(), "Enter post Details", Toast.LENGTH_SHORT);
                                return;
                            }

                            if (image_uri == null) {
                                //post without image
                                uploadData(postTitle, postDetails, "noImage", postAnonymously, tagToUpload);
                            } else {
                                //post with image
                                uploadData(postTitle, postDetails, String.valueOf(image_uri), postAnonymously, tagToUpload);
                            }
                            stackDialog.dismiss();
                        }
                    }
                });

                stackDialog.show();
            }
        });

        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stackDialog.setContentView(R.layout.custom_stack_filter);

                ImageView searchFilter = stackDialog.findViewById(R.id.searchFilterImageView);
                stackTag = stackDialog.findViewById(R.id.sampleTag1);

                stackTag.setVisibility(View.INVISIBLE);
                searchFilter.setVisibility(View.INVISIBLE);

                stackTagSpinner = stackDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.stack_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stackTagSpinner.setAdapter(tagAdapter);

                stackTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            stackTag.setText(selectedItem);
                            stackTag.setVisibility(View.VISIBLE);
                            searchFilter.setVisibility(View.VISIBLE);
                            stackTagSpinner.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                stackTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stackTag.setText("");
                        stackTagSpinner.setEnabled(true);
                        stackTag.setVisibility(View.INVISIBLE);
                        searchFilter.setVisibility(View.INVISIBLE);
                    }
                });

                searchFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] allTags = getResources().getStringArray( R.array.all_tags );
                        String filterTag = stackTag.getText().toString().trim();
                        filterTagsToUpload = "";

                        for (int i = 0; i < allTags.length; i++) {
                            if (filterTag.equals(allTags[i])) {
                                filterTagsToUpload = i + "";
                            }
                        }

                        DatabaseReference queryRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity/StackPosts");
                        Query query = queryRef.orderByChild("pTagIndex").equalTo(filterTagsToUpload);

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                postList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    System.out.println(ds.getValue());
                                    ModelStackPost ModelStackPost = ds.getValue(ModelStackPost.class);
                                    postList.add(ModelStackPost);
                                }

                                // adapter
                                adapterStackPosts = new AdapterStackPosts(getActivity(), postList, getActivity());
                                adapterStackPosts.notifyDataSetChanged();

                                // set adapter to recyclerView
                                recyclerView.setAdapter(adapterStackPosts);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }


                        });

                        stackDialog.dismiss();
                    }
                });



                stackDialog.show();
            }
        });


        return view;
    }

    private void loadPosts() {
        // adapter
        adapterStackPosts = new AdapterStackPosts(getActivity(), postList, getActivity());
        // set adapter to recyclerView
        recyclerView.setAdapter(adapterStackPosts);

        // path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/StackPosts");
        Query query = ref.orderByChild("pUpvoteNumber");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelStackPost modelStackPost = ds.getValue(ModelStackPost.class);
                    postList.add(modelStackPost);
                }
                adapterStackPosts.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // in case of error
                Toast.makeText(getActivity(), "Error on load post method 214. line", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void searchPosts( String searchQuery ) {

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
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    System.out.println("şurada");
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    System.out.println(writeStorageAccepted);
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

    private void addToHisNotifications(String hisUid, String pId , String notification) {
        timestamp = String.valueOf(MainActivity.getServerDate());

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId" , pId);
        hashMap.put("timestamp" ,timestamp );
        hashMap.put("pUid" , hisUid);
        hashMap.put("notification" , notification);
        hashMap.put("sUid" , uid);
        hashMap.put("sName" , username);
        if (!tagToUpload.equals("")) {
            hashMap.put("sTag", tagToUpload);
        }
        else {
            hashMap.put("sTag","0");
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Notifications/" + hisUid ); // uid
        String nUid = ref.push().getKey();
        hashMap.put("nId", nUid);
        ref.child(nUid).setValue(hashMap)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                    }
                });

    }
    private void addToHisLastActivities( String pId , String notification) {
        String timeStamp = String.valueOf( MainActivity.getServerDate());
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId" , pId);
        hashMap.put("timestamp" ,timeStamp );
        hashMap.put("notification" , notification);
        hashMap.put("sUid" , uid);
        hashMap.put("sName" , username);
        if (!tagToUpload.equals("")) {
            hashMap.put("sTag", tagToUpload);
        }
        else {
            hashMap.put("sTag","0");
        }
        hashMap.put("type", "3");  // 1 buddy 2 club 3 stack

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" + uid + "/LastActivities" ); // uid
        String laUid = ref.push().getKey();
        hashMap.put("nId", laUid);
        ref.child(laUid).setValue(hashMap)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                    }
                });

    }

    private void checkUserStatus() {
        //get curremt user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in

            email = user.getEmail();
            username = email.split("@")[0].replace(".","_");
            uid = user.getUid();

        } //TODO else navigate to login
    }

    private void uploadData(String postTitle, String postDetails, String uri, String postAnonymously, String tagToUpload) {
        //for post-image name, post-id, post-publish-time
        String timeStamp = String.valueOf( MainActivity.getServerDate());
        String filePathAndName = "Posts/" + "post_" + timeStamp;

        ArrayList<String> pUpvoteUsers = new ArrayList<>();
        pUpvoteUsers.add("empty");

        if (!uri.equals("noImage")) {
            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image is uploaded to firebase, now get its uri
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                //uri is received upload post to firebase database

                                HashMap<String, Object> hashMap = new HashMap<>();
                                //put post info
                                checkUserStatus();
                                hashMap.put("uid",uid); //çekememiş
                                hashMap.put("username",username); //çekmemiş
                                //hashMap.put("uEmail",email);
                                //hashMap.put("uDp",dp); // ?
                                hashMap.put("pAnon",postAnonymously);
                                hashMap.put("pDetails",postDetails);
                                hashMap.put("pImage",downloadUri);
                                hashMap.put("pTime",timeStamp);
                                hashMap.put("pTags", tagToUpload); //TODO tagler için değişicek
                                hashMap.put("pUpvoteNumber", 0);
                                hashMap.put("pUpUsers", pUpvoteUsers);
                                hashMap.put("pTitle", postTitle);
                                hashMap.put("pTagIndex",tagToUpload);

                                //tagsToUpload achievements KUTAY
                                String achTagToUpload = tagToUpload;
                                if (Integer.valueOf(achTagToUpload) < 4 ) {
                                    //TODO KUTAY MAT PUAN HERE
                                    loadProfileScoreAchievements();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            increaseOnePoints("1");
                                        }
                                    }, 2000);
                                }
                                else if (Integer.valueOf(achTagToUpload) < 19 && Integer.valueOf(achTagToUpload) > 16) {
                                    //TODO KUTAY ENG PUAN HERE
                                    //TODO KUTAY STUDY PUAN EKLE
                                    loadProfileScoreAchievements();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            increaseOnePoints("6");
                                        }
                                    }, 2000);
                                }
                                else if (Integer.valueOf(achTagToUpload) < 21 && Integer.valueOf(achTagToUpload) > 18) {
                                    //TODO KUTAY TURK PUAN HERE
                                    //TODO KUTAY STUDY PUAN EKLE
                                    loadProfileScoreAchievements();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            increaseOnePoints("7");
                                        }
                                    }, 2000);
                                }


                                //path to store post data
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("StackPosts");
                                String pUid = reference.push().getKey();
                                hashMap.put("pId", pUid);

                                //put data in this ref
                                reference.child(pUid).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added in database
                                                Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT);
                                                //TODO reset views
                                                // anonim değilse yollar
                                                if (postAnonymously.equals("0")) {
                                                    addToHisLastActivities(pUid, "Asked a question");



                                                // Sends notification to people who have same tag numbers with this post

                                                //getting users who have that spesific tag
                                                // for now notification will send for random tag
                                                final String luckyOnesToBeSendNotification = tagToUpload;
                                                //   final String luckyOnesToBeSendNotification = "2";
                                                System.out.println(luckyOnesToBeSendNotification);

                                                userDbRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users");
                                                userDbRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot ds : snapshot.getChildren()) {

                                                            ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                                            String[] userTags = modelUsers.getTags().split(",");
                                                            String firstTag = userTags[0];
                                                            String secondTag = userTags[1];
                                                            String thirdTag = userTags[2];
                                                            String userUI = modelUsers.getUid();
                                                            String[] alltags = MainActivity.getAllTags();
                                                            System.out.println("ssdsdf");
                                                            if (luckyOnesToBeSendNotification.equals(firstTag) || luckyOnesToBeSendNotification.equals(secondTag) || luckyOnesToBeSendNotification.equals(thirdTag)) {
                                                                if (!userUI.equals(uid)) {
                                                                    addToHisNotifications("" + userUI, "" + pUid, " Someone looking for a pro!" + " " + alltags[Integer.parseInt(luckyOnesToBeSendNotification)]);
                                                                    //TODO telefonuna burda notif yolla
                                                                }
                                                            }

                                                            System.out.println("oluyor");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed adding post in database
                                                Toast.makeText(getActivity(),"Failed publishing post",Toast.LENGTH_SHORT);
                                            }
                                        });


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            Toast.makeText(getActivity(),"Failed uploading image",Toast.LENGTH_SHORT);
                        }
                    });

        }
        else {
            //post without image

            HashMap<String, Object> hashMap = new HashMap<>();
            //put post info
            checkUserStatus();
            hashMap.put("uid",uid);
            hashMap.put("username",username);
            hashMap.put("uEmail",email);
            hashMap.put("uDp",dp);
            hashMap.put("pAnon",postAnonymously);
            hashMap.put("pDetails",postDetails);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timeStamp);
            hashMap.put("pUpvoteNumber", 0);
            hashMap.put("pUpUsers", pUpvoteUsers);
            hashMap.put("pTitle",postTitle);
            hashMap.put("pTagIndex",tagToUpload);

            //tagsToUpload achievements KUTAY
            String achTagToUpload = tagToUpload;
            if (Integer.valueOf(achTagToUpload) < 4 ) {
                //TODO KUTAY MAT PUAN HERE
                loadProfileScoreAchievements();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        increaseOnePoints("1");
                    }
                }, 2000);
            }
            else if (Integer.valueOf(achTagToUpload) < 19 && Integer.valueOf(achTagToUpload) > 16) {
                //TODO KUTAY ENG PUAN HERE
                //TODO KUTAY STUDY PUAN EKLE
                loadProfileScoreAchievements();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        increaseOnePoints("6");
                    }
                }, 2000);
            }
            else if (Integer.valueOf(achTagToUpload) < 21 && Integer.valueOf(achTagToUpload) > 18) {
                //TODO KUTAY TURK PUAN HERE
                //TODO KUTAY STUDY PUAN EKLE
                loadProfileScoreAchievements();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        increaseOnePoints("7");
                    }
                }, 2000);
            }



            //path to store post data
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("StackPosts");
            String pUid = reference.push().getKey();
            hashMap.put("pId", pUid);

            //put data in this ref
            reference.child(pUid).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT);
                            //TODO reset views
                            // anonim değilse yollar
                            if (postAnonymously.equals("0")) {
                                addToHisLastActivities(pUid, "Asked a question");


                            // Sends notification to people who have same tag numbers with this post

                            //getting users who have that spesific tag
                            final String luckyOnesToBeSendNotification = tagToUpload;
                            String firstTag = "1,2,3"; //tagSplitter(tagsToUpload)[0];
                            System.out.println(firstTag + "HAA");
                            userDbRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users");
                            //   final String luckyOnesToBeSendNotification = "2";
                            System.out.println(luckyOnesToBeSendNotification);


                            userDbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {

                                        ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                        String[] userTags = modelUsers.getTags().split(",");
                                        String firstTag = userTags[0];
                                        String secondTag = userTags[1];
                                        String thirdTag = userTags[2];
                                        String userUI = modelUsers.getUid();
                                        String[] alltags = MainActivity.getAllTags();
                                        System.out.println("ssdsdf");
                                        if (luckyOnesToBeSendNotification.equals(firstTag) || luckyOnesToBeSendNotification.equals(secondTag) || luckyOnesToBeSendNotification.equals(thirdTag)) {
                                            if (!userUI.equals(uid)) {
                                                addToHisNotifications("" + userUI, "" + pUid, " Someone looking for a pro!" + " " + alltags[Integer.parseInt(luckyOnesToBeSendNotification)]);
                                                //TODO telefonuna burda notif yolla
                                            }
                                        }

                                        System.out.println("oluyor");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding post in database
                            Toast.makeText(getActivity(),"Failed publishing post",Toast.LENGTH_SHORT);
                        }
                    });


        }

    }

    private void loadProfileScoreAchievements() {


        // getting user's scores
        DatabaseReference  usersDbRefAchscore = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" + uid );
        usersDbRefAchscore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals("AchievementsScores")) {

                        System.out.println(ds.getValue());
                        System.out.println(ds.getKey());
                        ModelAchievementsScores modelAchievementsScores = ds.getValue(ModelAchievementsScores.class);
                        //Parsing in database user's scores
                        mathScore = Integer.parseInt("" + modelAchievementsScores.getMath());
                        careerScore = Integer.parseInt("" + modelAchievementsScores.getCareer());
                        System.out.println( "ahahaahhahahaah" + careerScore);
                        sportScore = Integer.parseInt("" + modelAchievementsScores.getSport());
                        technologyScore = Integer.parseInt("" + modelAchievementsScores.getTechnology());
                        socialScore = Integer.parseInt("" + modelAchievementsScores.getSocial());
                        englishScore = Integer.parseInt("" + modelAchievementsScores.getEnglish());
                        turkishScore = Integer.parseInt("" + modelAchievementsScores.getTurkish());
                        studyScore = Integer.parseInt("" + modelAchievementsScores.getStudy());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void increaseOnePoints(String genre ) {
        //TODO create all users with default scores

        title = "";
        description = "";
        point = "";
        nId = "";
        level = "";


        // getting user's scores
        DatabaseReference  usersDbRefAchscore = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" + uid );
        usersDbRefAchscore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals("AchievementsScores")) {

                        System.out.println(ds.getValue());
                        System.out.println(ds.getKey());
                        ModelAchievementsScores modelAchievementsScores = ds.getValue(ModelAchievementsScores.class);
                        //Parsing in database user's scores
                        mathScore = Integer.parseInt("" + modelAchievementsScores.getMath());
                        careerScore = Integer.parseInt("" + modelAchievementsScores.getCareer());
                        System.out.println( "ahahaahhahahaah" + careerScore);
                        sportScore = Integer.parseInt("" + modelAchievementsScores.getSport());
                        technologyScore = Integer.parseInt("" + modelAchievementsScores.getTechnology());
                        socialScore = Integer.parseInt("" + modelAchievementsScores.getSocial());
                        englishScore = Integer.parseInt("" + modelAchievementsScores.getEnglish());
                        turkishScore = Integer.parseInt("" + modelAchievementsScores.getTurkish());
                        studyScore = Integer.parseInt("" + modelAchievementsScores.getStudy());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        System.out.println("Math SCore:" + mathScore);
        System.out.println("Career Score:" + careerScore);
        // increasing the points depending on the genre
        //increase 1 point to mathScore
        if (genre.equals("1")) {
            mathScore++;
            if (mathScore == 10 || mathScore == 50 || mathScore == 100 || mathScore == 500 || mathScore == 100) {
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("1") && modelAchievements.getPoint().equals(String.valueOf(mathScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        //increase 1 point to careerScore
        else if (genre.equals("2")) {
            careerScore++;
            System.out.println(careerScore);

            if (careerScore == 10 || careerScore == 50 || careerScore == 100 || careerScore == 500 || careerScore == 100) {
                System.out.println(careerScore);
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("2") && modelAchievements.getPoint().equals(String.valueOf(careerScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        //increase 1 point to sportScore
        else if (genre.equals("3")) {
            sportScore++;
            if (sportScore == 10 || sportScore == 50 || sportScore == 100 || sportScore == 500 || sportScore == 100) {
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("3") && modelAchievements.getPoint().equals(String.valueOf(sportScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        //increase 1 point to technologyScore
        else if (genre.equals("4")) {
            technologyScore++;
            if (technologyScore == 10 || technologyScore == 50 || technologyScore == 100 || technologyScore == 500 || technologyScore == 100) {
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("4") && modelAchievements.getPoint().equals(String.valueOf(technologyScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        //increase 1 point to socialScore
        else if (genre.equals("5")) {
            socialScore++;
            if (socialScore == 10 || socialScore == 50 || socialScore == 100 || socialScore == 500 || socialScore == 100) {
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("5") && modelAchievements.getPoint().equals(String.valueOf(socialScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        //increase 1 point to englishScore
        else if (genre.equals("6")) {
            englishScore++;
            if (englishScore == 10 || englishScore == 50 || englishScore == 100 || englishScore == 500 || englishScore == 100) {
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("6") && modelAchievements.getPoint().equals(String.valueOf(englishScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        //increase 1 point to turkishScore
        else if (genre.equals("7")) {
            turkishScore++;
            if (turkishScore == 10 || turkishScore == 50 || turkishScore == 100 || turkishScore == 500 || turkishScore == 100) {
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("7") && modelAchievements.getPoint().equals(String.valueOf(turkishScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        //increase 1 point to studyScore
        else if (genre.equals("8")) {
            studyScore++;
            if (studyScore == 10 || studyScore == 50 || studyScore == 100 || studyScore == 500 || studyScore == 100) {
                // query ile bilgileri getirt
                DatabaseReference  DbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Achievements/");
                DbRefAchs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAchievements modelAchievements = ds.getValue(ModelAchievements.class);
                            if (modelAchievements.getGenre().equals("8") && modelAchievements.getPoint().equals(String.valueOf(studyScore))) {
                                // gets achievement information
                                title = modelAchievements.getTitle();
                                description = modelAchievements.getDescription();
                                point = modelAchievements.getPoint();
                                nId = modelAchievements.getnId();
                                level = modelAchievements.getLevel();
                                //puts this to user
                                HashMap<Object, String> hashMapd = new HashMap<>();
                                hashMapd.put("title" , title);
                                hashMapd.put("description" , description );
                                hashMapd.put("point" , point);
                                hashMapd.put("genre" , genre);
                                hashMapd.put("nId" , nId);
                                hashMapd.put("level" , level);
                                DatabaseReference  usersDbRefAchs = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" +uid +"/Achievements/");

                                usersDbRefAchs.child(nId).setValue(hashMapd);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // userin Achievementsına idsini ekle
            }
        }
        // Prapare scores to make it appropraite to send to server
        String SmathScore = String.valueOf(mathScore);
        String ScareerScore = String.valueOf(careerScore);
        String SsportScore = String.valueOf(sportScore);
        String StechnologyScore = String.valueOf(technologyScore);
        String SsocialScore = String.valueOf(socialScore);
        String SenglishScore = String.valueOf(englishScore);
        String SturkishScore = String.valueOf(turkishScore);
        String SstudyScore = String.valueOf(studyScore);

        // Creating Hashes to send information to database
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("math" , SmathScore);
        hashMap.put("career" ,ScareerScore );
        hashMap.put("sport" , SsportScore);
        hashMap.put("technology" , StechnologyScore);
        hashMap.put("social" , SsocialScore);
        hashMap.put("english" , SenglishScore);
        hashMap.put("turkish", SturkishScore);
        hashMap.put("study", SstudyScore);
        DatabaseReference userAchref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" + uid + "/AchievementsScores/");
        // Sending hashes to database
        userAchref.setValue(hashMap);
    }


}