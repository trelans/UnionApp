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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import java.util.HashMap;

public class BuddyFragment extends Fragment {

    Dialog buddyDialog;
    Spinner genderSpinner;
    Spinner tagSpinner;

    EditText postDetailsEt,
             postQuotaEt,
             postDateEt,
             postTimeEt,
             postLocationEt;

    ImageView imageIv,
              sendButtonIv,
              addPhotoIv;

    DatabaseReference userDbRef;
    FirebaseAuth firebaseAuth;
    Uri image_uri;

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
                for (DataSnapshot ds: snapshot.getChildren()) {
                    username = "" + ds.child("username").getValue();
                    dp = "" + ds.child("pp").getValue();
                    //email = "" + ds.child("email").getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buddyDialog.setContentView(R.layout.custom_create_post_buddy_popup);

                genderSpinner = buddyDialog.findViewById(R.id.genderSpinner);
                ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.gender_preferences, android.R.layout.simple_spinner_item);
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                genderSpinner.setAdapter(genderAdapter);

                tagSpinner = buddyDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.buddy_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(tagAdapter);

                //init views
                postDetailsEt = buddyDialog.findViewById(R.id.editTextPostDetails);
                postDateEt = buddyDialog.findViewById(R.id.editTextDate);
                postQuotaEt = buddyDialog.findViewById(R.id.editTextQuota);
                postTimeEt = buddyDialog.findViewById(R.id.editTextTime);
                sendButtonIv = buddyDialog.findViewById(R.id.imageViewSendButton);
                addPhotoIv = buddyDialog.findViewById(R.id.uploadPhotoImageView);
                postLocationEt = buddyDialog.findViewById(R.id.editTextLocation);

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

                        if (TextUtils.isEmpty(postDetails)) {
                            Toast.makeText(getActivity(),"Enter post Details",Toast.LENGTH_SHORT);
                            return;
                        }

                        if (image_uri==null) {
                            //post without image
                            uploadData(postDetails,postDate,postTime,postQuotaStr,"noImage",postLocation);
                        }
                        else {
                            //post with image
                            uploadData(postDetails,postDate,postTime,postQuotaStr,String.valueOf(image_uri),postLocation);
                        }
                    }
                });


                buddyDialog.show();
            }
        });

        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buddyDialog.setContentView(R.layout.custom_buddy_filter);

                tagSpinner = buddyDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.buddy_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(tagAdapter);

                buddyDialog.show();
            }
        });

        return view;

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
                if (which==0) {
                    //camera clicked

                }
                if (which==1) {
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
            uid = user.getUid();

        }
    }

    private void uploadData(String postDetails, String postDate, String postTime, String postQuotaStr, String uri, String postLocation) {
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
                        while (!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()) {
                            //uri is received upload post to firebase database

                            HashMap<Object,String> hashMap = new HashMap<>();
                            //put post info
                            hashMap.put("uid",uid); //çekememiş
                            hashMap.put("username",username); //çekmemiş
                            //hashMap.put("uEmail",email);
                            hashMap.put("uDp",dp);
                            hashMap.put("pId",timeStamp);
                            hashMap.put("pDetails",postDetails);
                            hashMap.put("pDate",postDate);
                            hashMap.put("pHour",postTime);
                            hashMap.put("pQuota",postQuotaStr);
                            hashMap.put("pImage",downloadUri);
                            hashMap.put("pTime",timeStamp);
                            hashMap.put("pLocation",postLocation);
                            hashMap.put("pTags","1"); //TODO tagler için değişicek

                            //path to store post data
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("BuddyPosts");

                            //put data in this ref
                            reference.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //added in database
                                            Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT);
                                            //TODO reset views
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
            hashMap.put("uid",uid);
            hashMap.put("username",username);
            hashMap.put("uEmail",email);
            hashMap.put("uDp",dp);
            hashMap.put("pId",timeStamp);
            hashMap.put("pDetails",postDetails);
            hashMap.put("pDate",postDate);
            hashMap.put("pHour",postTime);
            hashMap.put("pQuota",postQuotaStr);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timeStamp);
            hashMap.put("pLocation",postLocation);

            //path to store post data
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

            //put data in this ref
            reference.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT);
                            //TODO reset views

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
