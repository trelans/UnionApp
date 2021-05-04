package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;
import com.union.unionapp.notifications.Data;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity{

    CardView commentCardView;
    CardView postCardView;
    RecyclerView recyclerView;
    List<ModelComment> commentList;
    final AdapterComment[] adapterComment = new AdapterComment[1];
    String pTime;
    String pTitle;
    String pDetails;
    String upVoteNumber;
    String pAnon;
    String username;
    String pId;
    String pDate;
    String pHour;
    String pLocation;
    String pQuota;
    String pImage;
    String pGender;
    String pTags;
    String pType;
    String source;
    String publisherPp;
    DatabaseReference postRef;
    int previousMargin;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    String currentPhotoPath;
    String cUid;
    String image_uri;

    TextView pUserNameTW;
    TextView upNumberTW;
    TextView questionContentTW;
    TextView postDateTW;
    TextView postLocationTW;
    TextView pGenderTW;
    TextView pQuotaTW;
    ImageView postImageIW;
    ImageView addPhotoIV;
    EditText commentET;
    CheckBox isAnonCB;
    ImageView sendButton;
    LinearLayoutCompat clickToOpenCardLL;
    ImageView backButton;
    ImageView  profilePhoto;
    TextView pTitleTW;
    AppCompatButton topicTagTW1;
    AppCompatButton topicTagTW2;
    AppCompatButton topicTagTW3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        commentCardView = findViewById(R.id.commentCard);
        postCardView = findViewById(R.id.cardView);
        pTitleTW = findViewById(R.id.postTitleTW);
        pUserNameTW = findViewById(R.id.userNameTextView);
        upNumberTW = findViewById(R.id.upNumberTextView);
        questionContentTW = findViewById(R.id.askedQuestionTextView);
        postDateTW = findViewById(R.id.postDateTW);
        addPhotoIV = findViewById(R.id.imageViewAddPhoto);
        commentET = findViewById(R.id.answerEditText);
        isAnonCB = findViewById(R.id.anonymCheckBox);
        sendButton = findViewById(R.id.sendButtonIB);
        clickToOpenCardLL = findViewById(R.id.openCardLL);
        backButton = findViewById(R.id.backButton);
        postLocationTW = findViewById(R.id.postLocationTW);
        pGenderTW = findViewById(R.id.postGenderTW);
        pQuotaTW = findViewById(R.id.postQuotaTW);
        postImageIW = findViewById(R.id.postImageIW);
        topicTagTW1 = findViewById(R.id.topicTagTW1);
        topicTagTW2 = findViewById(R.id.topicTagTW2);
        topicTagTW3 = findViewById(R.id.topicTagTW3);
        profilePhoto = findViewById(R.id.profilePhoto);

        image_uri = "noImage";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            source = extras.getString("source", "inside");
            pType = extras.getString("pType", "0");
            pId = extras.getString("pId", "0");
            //TODO şuradaki yapıyla database efficiencysi artırabilirsin
            if (source.equals("outside")) {
                postRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity/" + pType + "Posts/" + pId);
                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("Key " + snapshot.getKey() + "Value " + snapshot.getValue());
                        if (pType.equals("Stack")) {
                            ModelStackPost modelStackPost = snapshot.getValue(ModelStackPost.class);
                            assert modelStackPost != null;
                            upVoteNumber = modelStackPost.pUpvoteNumber + "";
                            pAnon = modelStackPost.pAnon;
                            upNumberTW.setText(upVoteNumber);
                            pTime = modelStackPost.pTime;
                            pTitle = modelStackPost.pTitle;
                            pDetails = modelStackPost.pDetails;
                            username = modelStackPost.username;
                            pImage = modelStackPost.getPImage();
                            publisherPp = modelStackPost.getUid();

                        } else {
                            ModelBuddyAndClubPost modelBuddyAndClubPost = snapshot.getValue(ModelBuddyAndClubPost.class);
                            if (pType.equals("Buddy")) {
                                assert modelBuddyAndClubPost != null;
                                pGender = modelBuddyAndClubPost.getpGender();
                            }
                            assert modelBuddyAndClubPost != null;
                            pDate = modelBuddyAndClubPost.pDate;
                            pHour = modelBuddyAndClubPost.pHour;
                            pLocation = modelBuddyAndClubPost.pLocation;
                            pQuota = modelBuddyAndClubPost.pQuota;
                            pImage = modelBuddyAndClubPost.pImage;
                            pTags = modelBuddyAndClubPost.pTags;
                            pAnon = "0";
                            upNumberTW.setVisibility(View.INVISIBLE);
                            pTime = modelBuddyAndClubPost.pTime;
                            pTitle = modelBuddyAndClubPost.pTitle;
                            pDetails = modelBuddyAndClubPost.pDetails;
                            username = modelBuddyAndClubPost.username;
                            publisherPp = modelBuddyAndClubPost.getuPp();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                if (pType.equals("Buddy") || pType.equals("Club")) {
                    if (pType.equals("Buddy")) {
                        pGender = extras.getString("pGender", "0");
                    }
                    pDate = extras.getString("pDate", "0");
                    pHour = extras.getString("pHour", "0");
                    pLocation = extras.getString("pLocation", "0");
                    pQuota = extras.getString("pQuota", "0");
                    pTags = extras.getString("pTags", "0");
                    pAnon = "0";
                } else if (pType.equals("Stack")) {
                    upVoteNumber = extras.getString("upVoteNumber", "0");
                    pAnon = extras.getString("pAnon", "0");
                }
                pTime = extras.getString("pTime", "0");
                pTitle = extras.getString("pTitle", "0");
                pDetails = extras.getString("pDetails", "0");
                username = extras.getString("username", "0");
                pImage = extras.getString("pImage", "0");
                publisherPp = extras.getString("uPp", "0");
            }
        }

        //recycler view and its properties
        recyclerView = findViewById(R.id.commentStackRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        commentList = new ArrayList<>();

        if (source.equals("outside")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    loadView("Buddy");
                }
            }, 1000);
        } else {
            loadView(pType);
        }

    }

    private void loadView(String pType) {
        ArrayList<String> cUpUsers = new ArrayList<>();
        cUpUsers.add("empty");
        //Convert TimeStamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(pTime));
        String pPostedTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        FirebaseUser user;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
        } else {
            Toast.makeText(PostActivity.this, "There is no current user!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("Comments").child(pId);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("Users").child(user.getUid()).child("comments");

        postDateTW.setText("Posted on: " + pPostedTime);
        if (!pType.equals("Stack")) {
            postLocationTW.setText("Location:  " + pLocation);
            pGenderTW.setText("Gender Preference:  " + pGender);
            pQuotaTW.setText("Quota: " + pQuota);
            upNumberTW.setVisibility(View.INVISIBLE);
        } else {
            postLocationTW.setHeight(0);
            pGenderTW.setHeight(0);
            pQuotaTW.setHeight(0);
            upNumberTW.setText(upVoteNumber);
        }

        pTitleTW.setText(pTitle);
        System.out.println("geldim gördüm çalışmadım");
        System.out.println(pImage);
        if (pImage != null && !pImage.equals("noImage") && !pImage.equals("")) {
            try {
                System.out.println("burada");
                //if image received, set
                StorageReference image = FirebaseStorage.getInstance().getReference("BilkentUniversity/"+ pType + "Posts/" + pImage);
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(postImageIW);
                    }
                });
            } catch (Exception e) {
                //if there is any exception while getting image then set default
                System.out.println("resim yüklemede hata çıktı");
            }
        } else {
            postImageIW.getLayoutParams().height = 0;
        }

        if (!pAnon.equals("1")) {
            try {
                //if image received, set
                StorageReference image = FirebaseStorage.getInstance().getReference("BilkentUniversity/pp/" + publisherPp);
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePhoto);
                    }
                });
            } catch (Exception e) {
                //if there is any exception while getting image then set default
                Picasso.get().load(R.drawable.user_pp_template).into(profilePhoto);
            }
        }



        questionContentTW.setText("     " + pDetails);
        if (!pAnon.equals("1")) {
            pUserNameTW.setText("@" + username);
        } else {
            pUserNameTW.setText("@" + "anonymous");
        }

        convertStringTagsToRealTags(topicTagTW1, topicTagTW2, topicTagTW3, pTags);

        commentET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                openOrCloseCard();
            }
        });

        clickToOpenCardLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseCard();
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog(ref);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String timeStamp;

                //get data from comment editText
                String comment = commentET.getText().toString().trim();
                // validate
                if (TextUtils.isEmpty(comment)) {
                    //no value is entered
                    Toast.makeText(PostActivity.this, "Comment is empty...", Toast.LENGTH_SHORT).show();
                } else {
                    timeStamp = String.valueOf(MainActivity.getServerDate());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    //put info in hashmap
                    hashMap.put("cId", cUid);
                    hashMap.put("comment", comment);
                    hashMap.put("timeStamp", timeStamp);
                    hashMap.put("upNumber", 0);
                    hashMap.put("cUpUsers", cUpUsers);
                    hashMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("cAnon", isAnonCB.isChecked() ? "1" : "0");
                    //isAnonCB.isChecked() ? "1" : "0")
                    hashMap.put("cPhoto", image_uri); //TODO resim butonunun işlevi
                    hashMap.put("uName", user.getEmail().split("@")[0].replace(".", "_"));

                    //reset commentEditText
                    commentET.setText("");

                    // put this data into db
                    ref.child(timeStamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //added
                                            /*
                                            Toast.makeText(context, "Comment Added...", Toast.LENGTH_SHORT).show();
                                            commentET.setText("");
                                            HashMap<String, Object> cIdHashMap = new HashMap<>();
                                            cIdHashMap.put(timeStamp, pId + "PostComment" + "Stack");
                                            userRef.updateChildren(cIdHashMap);
                                             */
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed, not added
                                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // adapter
        adapterComment[0] = new AdapterComment(PostActivity.this, commentList, pId);
        // set adapter to recyclerView
        recyclerView.setAdapter(adapterComment[0]);
        // path of all comments
        Query query = ref.orderByChild("upNumber");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelComment modelComment = ds.getValue(ModelComment.class);
                    commentList.add(modelComment);
                }
                adapterComment[0].notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // in case of error
                //Toast.makeText(context, "Error on load comments 248. line", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openOrCloseCard() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) commentCardView.getLayoutParams();
        if (layoutParams.topMargin == 0) {
            layoutParams.topMargin = previousMargin;
        } else {
            previousMargin = layoutParams.topMargin;
            layoutParams.topMargin = 0;
        }
        commentCardView.setLayoutParams(layoutParams);
    }

    public void convertStringTagsToRealTags(Button topicTagTW1, Button topicTagTW2, Button topicTagTW3, String pTags) {
        String[] newTags = new String[3];
        newTags[0] = "";
        newTags[1] = "";
        newTags[2] = "";

        if (pTags == null) {
            return;
        }
        String[] tags = pTags.split(",");
        String[] allTags = MainActivity.getAllTags();

        if (tags.length == 0) {
            newTags[0] = "";
        } else if (tags.length == 1) {
            newTags[0] = allTags[Integer.valueOf(tags[0])]; // newTags[0] = allTags[Integer.valueOf(tags[0])];
        } else {
            for (int i = 0; i < tags.length; i++) {
                newTags[i] = allTags[Integer.valueOf(tags[i])];
            }
        }

        if (newTags[0].equals("")) {
            topicTagTW1.setVisibility(View.INVISIBLE);
        } else {
            if (tags[0].equals("0")) {
                topicTagTW1.setVisibility(View.INVISIBLE);
            } else {
                topicTagTW1.setText(newTags[0]);
            }
        }

        if (newTags[1].equals("")) {
            topicTagTW2.setVisibility(View.INVISIBLE);
        } else {
            topicTagTW2.setText(newTags[1]);
        }

        if (newTags[2].equals("")) {
            topicTagTW3.setVisibility(View.INVISIBLE);
        } else {
            topicTagTW3.setText(newTags[2]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("buradaki çalıştı");
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                //userPpInDialog.setImageURI(Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);


                uploadImageToFirebase(cUid, contentUri);

            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                //userPpInDialog.setImageURI(contentUri);

                uploadImageToFirebase(cUid, contentUri);

            }
        }

            /*gelen resmi direkt koymak için
            Bitmap image = (Bitmap) data.getExtras().get("data");
            userPpInDialog.setImageBitmap(image);
             */


    }

    private void uploadImageToFirebase(String cId, Uri contentUri) {
        System.out.println("girdi buraya sıkıntı yok");

        StorageReference image = FirebaseStorage.getInstance().getReference("BilkentUniversity/Comments/" + cId);
        image.putFile(contentUri);

        image_uri = cId;
        addPhotoIV.setBackground(ContextCompat.getDrawable(this, R.drawable.upload_photo_icon));
    }

    void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
            System.out.println("burada");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        }
    }
    private void dispatchTakePictureIntent() {
        System.out.println("dispatch sdsad");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        try {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("Error while creating the file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.union.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }else{
                System.out.println("photo file is null");
            }
        }catch (Exception e){
            System.out.println("hiçbir şey getirmedi");
        }
    }

    private File createImageFile() throws IOException {
        System.out.println("gjajdajsdj");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Private için kod File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showImagePickDialog(DatabaseReference ref) {
        String[] options = {"Camera", "Gallery", "Delete Photo"};
        Context context;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // set items to dialog
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //camera clicked
                    cUid = ref.push().getKey();
                    askCameraPermissions();

                } else if (i == 1) {
                    // gallery clicked
                    cUid = ref.push().getKey();
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, GALLERY_REQUEST_CODE);
                } else if (i == 2){
                    addPhotoIV.setBackground(ContextCompat.getDrawable(PostActivity.this, R.drawable.ic_baseline_add_a_photo_24));
                    StorageReference image = FirebaseStorage.getInstance().getReference("BilkentUniversity/Comments/" + cUid);
                    image_uri = "noImage";
                    image.delete();
                }
            }
        });
        builder.show();
    }

}