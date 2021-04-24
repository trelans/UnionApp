package com.union.unionapp;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    EditText postDetailsEt;
    String postAnonymously;
    String postDetails;
    CheckBox anonym;

    ImageView sendButtonIv,
            addPhotoIv;

    DatabaseReference userDbRef;
    FirebaseAuth firebaseAuth;
    Uri image_uri;

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;

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

        //recycler view and its properties
        recyclerView = view.findViewById(R.id.stackPostsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post firs, for this load from last
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

                stackTagSpinner = stackDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.stack_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stackTagSpinner.setAdapter(tagAdapter);

                //init views
                sendButtonIv = stackDialog.findViewById(R.id.sendButtonImageView);
                addPhotoIv = stackDialog.findViewById(R.id.uploadPhotoImageView);
                postDetailsEt = stackDialog.findViewById(R.id.postDetailsEt);
                anonym = stackDialog.findViewById(R.id.checkBoxAnonymous);


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
                            uploadData(postDetails, "noImage", postAnonymously);
                        } else {
                            //post with image
                            uploadData(postDetails, String.valueOf(image_uri), postAnonymously);
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

                stackTagSpinner = stackDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.stack_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stackTagSpinner.setAdapter(tagAdapter);

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
                    System.out.println(ds);
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);

                    // adapter
                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    // set adapter to recyclerView
                    recyclerView.setAdapter(adapterPosts);
                }
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

    private void checkUserStatus() {
        //get curremt user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in

            email = user.getEmail();
            username = email.split("@")[0].replace(".", "_");
            uid = user.getUid();

        } //TODO else navigate to login
    }

    private void uploadData(String postDetails, String uri, String postAnonymously) {
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

                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put post info
                                checkUserStatus();
                                hashMap.put("uid", uid); //çekememiş
                                hashMap.put("username", username); //çekmemiş
                                //hashMap.put("uEmail",email);
                                hashMap.put("uDp", dp); // ?
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pAnon", postAnonymously);
                                hashMap.put("pDetails", postDetails);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timeStamp);
                                hashMap.put("pTags", "1"); //TODO tagler için değişicek


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

            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            checkUserStatus();
            hashMap.put("uid", uid);
            hashMap.put("username", username);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pAnon", postAnonymously);
            hashMap.put("pDetails", postDetails);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);


            //path to store post data
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

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

}