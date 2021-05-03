package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Calendar;
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
        System.out.println(pPostedTime);

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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String timeStamp;
                FirebaseUser user;
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                } else {
                    Toast.makeText(PostActivity.this, "There is no current user!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get data from comment editText
                String comment = commentET.getText().toString().trim();
                // validate
                if (TextUtils.isEmpty(comment)) {
                    //no value is entered
                    Toast.makeText(PostActivity.this, "Comment is empty...", Toast.LENGTH_SHORT).show();
                } else {
                    timeStamp = String.valueOf(MainActivity.getServerDate());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("Comments").child(pId);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity").child("Users").child(user.getUid()).child("comments");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    //put info in hashmap
                    hashMap.put("cId", timeStamp);
                    hashMap.put("comment", comment);
                    hashMap.put("timeStamp", timeStamp);
                    hashMap.put("upNumber", 0);
                    hashMap.put("cUpUsers", cUpUsers);
                    hashMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("cAnon", isAnonCB.isChecked() ? "1" : "0");
                    //isAnonCB.isChecked() ? "1" : "0")
                    hashMap.put("cPhoto", "noImage"); //TODO resim butonunun işlevi
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Comments/" + pId);
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
}