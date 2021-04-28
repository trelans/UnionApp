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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ClubsFragment extends Fragment {

    Dialog clubDialog;
    Spinner tagSpinner;

    EditText postDetailsEt,
            postQuotaEt,
            postLocationEt;

    ProgressBar pb;

    TextView postDateEt,
             postTimeEt;

    TextView[] textViewTags;

    AppCompatButton tag1,
                    tag2,
                    tag3;

    String[] allTags;

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

    String date;
    String time;

    DatePickerDialog.OnDateSetListener setListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    RecyclerView recyclerView;
    List<ModelBuddyAndClubPost> postList;
    AdapterClubPosts adapterBuddyPosts;

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
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        pb = view.findViewById(R.id.progressBar);

        ImageView filterImageView = (ImageView) view.findViewById(R.id.showClubFilterPopup);
        ImageView createPost = (ImageView) view.findViewById(R.id.showPopUpCreate);
        clubDialog = new Dialog(getActivity());
        // Layoutu transparent yapıo
        clubDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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



        //recycler view and its properties
        recyclerView = view.findViewById(R.id.clubPostsRecyclerView);
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
                Dialog dialog;
                clubDialog.setContentView(R.layout.custom_create_club_post);

                tagSpinner = clubDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.stack_tags, android.R.layout.simple_spinner_item);
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
                postDetailsEt = clubDialog.findViewById(R.id.editTextPostDetails);
                postDateEt = clubDialog.findViewById(R.id.dateTextView);
                postQuotaEt = clubDialog.findViewById(R.id.editTextQuota);
                postTimeEt = clubDialog.findViewById(R.id.editTextTime);
                sendButtonIv = clubDialog.findViewById(R.id.imageViewSendButton);
                addPhotoIv = clubDialog.findViewById(R.id.uploadPhotoImageView);
                postLocationEt = clubDialog.findViewById(R.id.editTextLocation);

                //init tags
                tag1 = clubDialog.findViewById(R.id.textViewTag1);
                tag2 = clubDialog.findViewById(R.id.textViewTag2);
                tag3 = clubDialog.findViewById(R.id.textViewTag3);

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
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(MainActivity.getServerDate());
                calendarToString(calendar);
                postDateEt.setText(date);
                postTimeEt.setText(time);

                //setting up the calendar dialog
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                postDateEt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,day,month,year
                        );
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.updateDate(year,month,day);
                        datePickerDialog.show();
                    }
                });

                postTimeEt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,timeSetListener,hourOfDay,minute,true
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

                        String tagsToUpload = "";

                        for ( int k = 1; k < allTags.length; k++ ) {
                            if ( allTags[ k ].equals( tag1.getText().toString() ) || allTags[ k ].equals( tag2.getText().toString() ) || allTags[ k ].equals( tag3.getText().toString() ) ) {
                                tagsToUpload = tagsToUpload + k + ",";
                            }
                        }

                        //tags to upload'un sonundaki virgülü atıyor
                        StringBuilder tempString = new StringBuilder(tagsToUpload);
                        tempString.deleteCharAt(tempString.length()-1);
                        tagsToUpload = tempString.toString();


                        if (TextUtils.isEmpty(postDetails)) {
                            Toast.makeText(getActivity(),"Enter post Details",Toast.LENGTH_SHORT);
                            return;
                        }

                        if (image_uri==null) {
                            //post without image
                            uploadData(postDetails,postDate,postTime,postQuotaStr,"noImage",postLocation,tagsToUpload);
                        }
                        else {
                            //post with image
                            uploadData(postDetails,postDate,postTime,postQuotaStr,String.valueOf(image_uri),postLocation,tagsToUpload);
                        }
                        clubDialog.dismiss();

                    }
                });

                clubDialog.show();
            }
        });
        checkUserStatus();

        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clubDialog.setContentView(R.layout.custom_club_filter);

                tagSpinner = clubDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.buddy_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(tagAdapter);

                clubDialog.show();
            }
        });


        //dialog dismiss listener
        clubDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
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



        return view;
    }




    private void loadPosts() {
        // path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/ClubPosts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelBuddyAndClubPost modelBuddyPost = ds.getValue(ModelBuddyAndClubPost.class);
                    postList.add(modelBuddyPost);

                    // adapter
                    adapterBuddyPosts = new AdapterClubPosts(getActivity(), postList);
                    // set adapter to recyclerView
                    recyclerView.setAdapter(adapterBuddyPosts);
                }
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // in case of error
               // Toast.makeText(getActivity(), "Error on load post method 214. line", Toast.LENGTH_SHORT).show();
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
            username = email.split("@")[0].replace(".","_");
            uid = user.getUid();

        }
    }

    private void uploadData(String postDetails, String postDate, String postTime, String postQuotaStr, String uri, String postLocation, String tagsToUpload) {
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

                                checkUserStatus();
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
                                hashMap.put("pTags", tagsToUpload);

                                //path to store post data
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("ClubPosts");

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

            checkUserStatus();
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
            hashMap.put("pTags", tagsToUpload);

            //path to store post data
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("ClubPosts");

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

    public void calendarToString(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        calendar.setTimeInMillis(MainActivity.getServerDate());
        time = timeFormat.format(calendar.getTime());
        date = dateFormat.format(calendar.getTime());
    }

    public boolean tagHasSelectedBefore(AppCompatButton tag1, AppCompatButton tag2, AppCompatButton tag3) {
        String tag1String = tag1.getText().toString();
        String tag2String = tag2.getText().toString();
        String tag3String = tag3.getText().toString();

        return ( tag1String.equals(tag2String) && tag2String.equals(tag3String) && tag1String.equals(tag3String) );
    }
}