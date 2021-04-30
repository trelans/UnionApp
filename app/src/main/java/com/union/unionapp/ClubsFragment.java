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
import android.os.Handler;
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
    DatabaseReference userDbRefPosts;
    Spinner tagSpinner;


    EditText postDetailsEt,
            postQuotaEt,
            postLocationEt,
            postTitleEt;

    ProgressBar pb;

    TextView postDateEt,
             postTimeEt,
             filterDateTv,
             filterTimeTv;

    TextView[] textViewTags;

    AppCompatButton tag1,
                    tag2,
                    tag3;

    String[] allTags;

    AppCompatButton[] tagsArray;

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
    String timestamp;
    String tagsToUpload;

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
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.club_tags, android.R.layout.simple_spinner_item);
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
                postTitleEt =  clubDialog.findViewById(R.id.editTextHeadLine);

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
                        timestamp = String.valueOf(MainActivity.getServerDate());
                        String postDetails = postDetailsEt.getText().toString().trim();
                        String postDate = postDateEt.getText().toString().trim();
                        String postQuotaStr = postQuotaEt.getText().toString().trim();
                        String postTime = postTimeEt.getText().toString().trim();
                        String postLocation = postLocationEt.getText().toString().trim();
                        String postTitle = postTitleEt.getText().toString().trim();

                         tagsToUpload = "";

                        for ( int k = 1; k < allTags.length; k++ ) {
                            if ( allTags[ k ].equals( tag1.getText().toString() ) || allTags[ k ].equals( tag2.getText().toString() ) || allTags[ k ].equals( tag3.getText().toString() ) ) {
                                tagsToUpload = tagsToUpload + k + ",";
                            }
                        }

                        //tags to upload'un sonundaki virgülü atıyor
                        if (tagsToUpload.length() > 0) {
                            StringBuilder tempString = new StringBuilder(tagsToUpload);
                            tempString.deleteCharAt(tempString.length() - 1);
                            tagsToUpload = tempString.toString();
                        }

                        if (TextUtils.isEmpty(postDetails)) {
                            Toast.makeText(getActivity(),"Enter post Details",Toast.LENGTH_SHORT);
                            return;
                        }

                        if (image_uri==null) {
                            //post without image
                            uploadData(postDetails,postDate,postTime,postQuotaStr,"noImage",postLocation,tagsToUpload,postTitle);
                        }
                        else {
                            //post with image
                            uploadData(postDetails,postDate,postTime,postQuotaStr,String.valueOf(image_uri),postLocation,tagsToUpload,postTitle);
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
                clubDialog.setCanceledOnTouchOutside(true);

                tag1 = clubDialog.findViewById(R.id.filterTag1TextView);
                tag2 = clubDialog.findViewById(R.id.filterTag2TextView);
                tag3 = clubDialog.findViewById(R.id.filterTag3TextView);

                filterDateTv = clubDialog.findViewById(R.id.dateFilterEditText);
                filterTimeTv = clubDialog.findViewById(R.id.timeFilterEditText);

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

                tagSpinner = clubDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.buddy_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tagSpinner.setAdapter(tagAdapter);

                //set the postDateEt to current date for default
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(MainActivity.getServerDate());
                calendarToString(calendar);
                filterDateTv.setText(date);
                filterTimeTv.setText(time);

                //setting up the calendar dialog
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);



                filterDateTv.setOnClickListener(new View.OnClickListener() {
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

                filterTimeTv.setOnClickListener(new View.OnClickListener() {
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
                        filterDateTv.setText(date);

                    }
                };

                timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        filterTimeTv.setText(time);
                    }
                };


                clubDialog.show();
            }
        });


        //dialog dismiss listener
        clubDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {


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

    private void uploadData(String postDetails, String postDate, String postTime, String postQuotaStr, String uri, String postLocation, String tagsToUpload, String postTitle) {
        //for post-image name, post-id, post-publish-time
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_";

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
                                hashMap.put("pDetails",postDetails);
                                hashMap.put("pDate",postDate);
                                hashMap.put("pHour",postTime);
                                hashMap.put("pQuota",postQuotaStr);
                                hashMap.put("pImage",downloadUri);
                                hashMap.put("pTime",timeStamp);
                                hashMap.put("pLocation",postLocation);
                                hashMap.put("pTitle",postTitle);

                                //tagsToUpload achievements KUTAY
                                String[] achsTagsToUpload = tagsToUpload.split(",");
                                for (int i = 0; i < achsTagsToUpload.length; i++) {
                                    if (Integer.valueOf(achsTagsToUpload[i]) < 4) {
                                        //TODO KUTAY MAT PUANI EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("1");
                                            }
                                        }, 2000);

                                    }
                                    else if (Integer.valueOf(achsTagsToUpload[i]) < 6) {
                                        //TODO KUTAY CAREER PUAN EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("2");
                                            }
                                        }, 2000);

                                    }
                                    else if (Integer.valueOf(achsTagsToUpload[i]) < 11) {
                                        //TODO KUTAY SPORT PUAN EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("3");
                                            }
                                        }, 2000);
                                    }
                                    else if (Integer.valueOf(achsTagsToUpload[i]) < 14) {
                                        //TODO KUTAY TECH PUAN EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("4");
                                            }
                                        }, 2000);
                                    }
                                    else if (Integer.valueOf(achsTagsToUpload[i]) < 17) {
                                        //TODO KUTAY ENGLISH PUAN EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("5");
                                            }
                                        }, 2000);
                                    }
                                    else if (Integer.valueOf(achsTagsToUpload[i]) < 19) {
                                        //TODO KUTAY ENGLISH PUAN EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("6");
                                            }
                                        }, 2000);
                                    }
                                    else if (Integer.valueOf(achsTagsToUpload[i]) < 21) {
                                        //TODO KUTAY TURKCE PUAN EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("7");
                                            }
                                        }, 2000);
                                    }
                                    else if (Integer.valueOf(achsTagsToUpload[i]) < 23) {
                                        //TODO KUTAY STUDY PUAN EKLE
                                        loadProfileScoreAchievements();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                increaseOnePoints("8");
                                            }
                                        }, 2000);
                                    }

                                }

                                if (!tagsToUpload.equals("")) {
                                    hashMap.put("pTags", tagsToUpload);
                                }
                                else {
                                    hashMap.put("pTags","0");
                                }

                                //path to store post data
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("ClubPosts");
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
                                                String completeTag = tagsToUpload;
                                                String[] partialTag = completeTag.split(",");
                                                int randomIndex = (int)  Math.random() *  (partialTag.length-1);
                                                final String luckyOnesToBeSendNotification = partialTag[randomIndex];

                                                userDbRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users");
                                                //   final String luckyOnesToBeSendNotification = "2";
                                                System.out.println(luckyOnesToBeSendNotification);


                                                userDbRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot ds: snapshot.getChildren()){
                                                            System.out.println("User arıyorum");
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
                                                                    addToHisNotifications("" + userUI, "" + pUid, ""+ username +" has a new announcement !" + " " + alltags[Integer.parseInt(luckyOnesToBeSendNotification)]);
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

            checkUserStatus();
            HashMap<Object,String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid",uid);
            hashMap.put("username",username);
            hashMap.put("uEmail",email);
            hashMap.put("uDp",dp);
            hashMap.put("pDetails",postDetails);
            hashMap.put("pDate",postDate);
            hashMap.put("pHour",postTime);
            hashMap.put("pQuota",postQuotaStr);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timeStamp);
            hashMap.put("pLocation",postLocation);
            hashMap.put("pTitle",postTitle);

            //tagsToUpload achievements KUTAY
            String[] achsTagsToUpload = tagsToUpload.split(",");
            for (int i = 0; i < achsTagsToUpload.length; i++) {
                if (Integer.valueOf(achsTagsToUpload[i]) < 4) {
                    //TODO KUTAY MAT PUANI EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("1");
                        }
                    }, 2000);

                }
                else if (Integer.valueOf(achsTagsToUpload[i]) < 6) {
                    //TODO KUTAY CAREER PUAN EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("2");
                        }
                    }, 2000);

                }
                else if (Integer.valueOf(achsTagsToUpload[i]) < 11) {
                    //TODO KUTAY SPORT PUAN EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("3");
                        }
                    }, 2000);
                }
                else if (Integer.valueOf(achsTagsToUpload[i]) < 14) {
                    //TODO KUTAY TECH PUAN EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("4");
                        }
                    }, 2000);
                }
                else if (Integer.valueOf(achsTagsToUpload[i]) < 17) {
                    //TODO KUTAY ENGLISH PUAN EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("5");
                        }
                    }, 2000);
                }
                else if (Integer.valueOf(achsTagsToUpload[i]) < 19) {
                    //TODO KUTAY ENGLISH PUAN EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("6");
                        }
                    }, 2000);
                }
                else if (Integer.valueOf(achsTagsToUpload[i]) < 21) {
                    //TODO KUTAY TURKCE PUAN EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("7");
                        }
                    }, 2000);
                }
                else if (Integer.valueOf(achsTagsToUpload[i]) < 23) {
                    //TODO KUTAY STUDY PUAN EKLE
                    loadProfileScoreAchievements();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            increaseOnePoints("8");
                        }
                    }, 2000);
                }

            }

            if (!tagsToUpload.equals("")) {
                hashMap.put("pTags", tagsToUpload);
            }
            else {
                hashMap.put("pTags","0");
            }

            //path to store post data
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("ClubPosts");
            String pUid = reference.push().getKey();
            hashMap.put("pId", pUid);

            //put data in this ref
            reference.child(pUid).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added in database
                            Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT);
                            addToHisLastActivities(pUid,"Published an announcement");
                            //TODO reset views


                            // Sends notification to people who have same tag numbers with this post

                            //getting users who have that spesific tag
                            // for now notification will send for random tag
                            String completeTag = tagsToUpload;
                            String[] partialTag = completeTag.split(",");
                            int randomIndex = (int)  Math.random() *  (partialTag.length-1);
                            final String luckyOnesToBeSendNotification = partialTag[randomIndex];
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
                                        System.out.println(luckyOnesToBeSendNotification);
                                        if ( luckyOnesToBeSendNotification.equals(firstTag)  || luckyOnesToBeSendNotification.equals(secondTag) || luckyOnesToBeSendNotification.equals(thirdTag) ){
                                            if (!userUI.equals(uid)) {
                                                addToHisNotifications("" + userUI, "" + pUid, ""+ username +" has a new announcement !" + " " + alltags[Integer.parseInt(luckyOnesToBeSendNotification)]);
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

    private void addToHisNotifications(String hisUid, String pId , String notification) {

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId" , pId);
        hashMap.put("timestamp" ,timestamp );
        hashMap.put("pUid" , hisUid);
        hashMap.put("notification" , notification);
        hashMap.put("sUid" , uid);
        hashMap.put("sName" , username);
        hashMap.put("sTag", tagsToUpload);

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
    // Users score will be checked and if he/she wins the achievement achievement's unique id will be added to his database
    private void didIAchieveAny(String usersUid) {


    }
    private void addToHisLastActivities( String pId , String notification) {

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId" , pId);
        hashMap.put("timestamp" ,timestamp );
        hashMap.put("notification" , notification);
        hashMap.put("sUid" , uid);
        hashMap.put("sName" , username);
        hashMap.put("sTag", tagsToUpload);
        hashMap.put("type", "2");  // 1 buddy 2 club 3 stack

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

    public void calendarToString(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        calendar.setTimeInMillis(MainActivity.getServerDate());
        time = timeFormat.format(calendar.getTime());
        date = dateFormat.format(calendar.getTime());
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