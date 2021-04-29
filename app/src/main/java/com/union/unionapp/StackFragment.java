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
import android.provider.MediaStore;
import android.text.TextUtils;
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

                        String selectedTag = stackTagSpinner.getSelectedItem().toString();
                        for ( int k = 1; k < allTags.length; k++ ) {
                            if ( allTags[ k ].equals( selectedTag ) ) {
                                tagTextIndex = k;
                                break;
                            }
                        }

                        tagToUpload = tagTextIndex+"";

                        postTitle = postTitleEt.getText().toString().trim();

                        postDetails = postDetailsEt.getText().toString().trim();
                        if (anonym.isChecked()) {
                            postAnonymously = "1";
                        }
                        else {
                            postAnonymously = "0";
                        }

                        if (TextUtils.isEmpty(postDetails)) {
                            Toast.makeText(getActivity(),"Enter post Details",Toast.LENGTH_SHORT);
                            return;
                        }

                        if (image_uri==null) {
                            //post without image
                            uploadData(postTitle, postDetails,"noImage",postAnonymously,tagToUpload);
                        }
                        else {
                            //post with image
                            uploadData(postTitle, postDetails,String.valueOf(image_uri),postAnonymously,tagToUpload);
                        }
                        stackDialog.dismiss();
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
                                adapterStackPosts = new AdapterStackPosts(getActivity(), postList);
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
        // path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/StackPosts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelStackPost modelStackPost = ds.getValue(ModelStackPost.class);
                    postList.add(modelStackPost);

                    // adapter
                    adapterStackPosts = new AdapterStackPosts(getActivity(), postList);
                    // set adapter to recyclerView
                    recyclerView.setAdapter(adapterStackPosts);
                }
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
        hashMap.put("sTag", tagToUpload);

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

                                HashMap<Object,String> hashMap = new HashMap<>();
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
                                hashMap.put("pUpvoteNumber","0");
                                hashMap.put("pTitle", postTitle);
                                hashMap.put("pTagIndex",tagToUpload);


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
                                                        for (DataSnapshot ds: snapshot.getChildren()){

                                                            ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                                            String[] userTags = modelUsers.getTags().split(",");
                                                            String firstTag = userTags[0];
                                                            String secondTag = userTags[1];
                                                            String thirdTag = userTags[2];
                                                            String userUI = modelUsers.getUid();
                                                            String[] alltags = MainActivity.getAllTags();
                                                            System.out.println("ssdsdf");
                                                            if ( luckyOnesToBeSendNotification.equals(firstTag)  || luckyOnesToBeSendNotification.equals(secondTag) || luckyOnesToBeSendNotification.equals(thirdTag) ){
                                                                if (!userUI.equals(uid)) {
                                                                    addToHisNotifications("" + userUI, "" + pUid, " Someone looking for new buddy!" + " " + alltags[Integer.parseInt(luckyOnesToBeSendNotification)]);
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

            HashMap<Object,String> hashMap = new HashMap<>();
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
            hashMap.put("pUpvoteNumber","0");
            hashMap.put("pTitle",postTitle);
            hashMap.put("pTagIndex",tagToUpload);



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
                                    for (DataSnapshot ds: snapshot.getChildren()){

                                        ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                        String[] userTags = modelUsers.getTags().split(",");
                                        String firstTag = userTags[0];
                                        String secondTag = userTags[1];
                                        String thirdTag = userTags[2];
                                        String userUI = modelUsers.getUid();
                                        String[] alltags = MainActivity.getAllTags();
                                        System.out.println("ssdsdf");
                                        if ( luckyOnesToBeSendNotification.equals(firstTag)  || luckyOnesToBeSendNotification.equals(secondTag) || luckyOnesToBeSendNotification.equals(thirdTag) ){
                                            if (!userUI.equals(uid)) {
                                                addToHisNotifications("" + userUI, "" + pUid, " Someone looking for new buddy!" + " " + alltags[Integer.parseInt(luckyOnesToBeSendNotification)]);
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

}