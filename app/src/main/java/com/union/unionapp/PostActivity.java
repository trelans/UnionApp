package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private SimpleGestureFilter detector;
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
    int previousMargin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        commentCardView = findViewById(R.id.commentCard);
        postCardView = findViewById(R.id.cardView);

        TextView relatedTagTW = findViewById(R.id.relatedTagTextView);
        //TextView pTitleTW = findViewById(R.id.pTitle);
        TextView pUserNameTW = findViewById(R.id.userNameTextView);
        TextView upNumberTW = findViewById(R.id.upNumberTextView);
        TextView questionContentTW = findViewById(R.id.askedQuestionTextView);
        //TextView pPostedTimeTW = findViewById(R.id.pPostedTime);
        ImageView addPhotoIV = findViewById(R.id.imageViewAddPhoto);
        EditText commentET = findViewById(R.id.answerEditText);
        CheckBox isAnonCB = findViewById(R.id.anonymCheckBox);
        ImageView sendButton = findViewById(R.id.sendButtonIB);
        LinearLayoutCompat clickToOpenCardLL = findViewById(R.id.openCardLL);
        ImageView backButton = findViewById(R.id.backButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pType = extras.getString("pType","0");
            if (pType.equals("Buddy") || pType.equals("Club")){
                if (pType.equals("Buddy")){
                    pGender = extras.getString("pGender","0");
                }
                pDate = extras.getString("pDate","0");
                pHour = extras.getString("pHour","0");
                pLocation = extras.getString("pLocation","0");
                pQuota = extras.getString("pQuota","0");
                pImage = extras.getString("pImage","0");
                pTags = extras.getString("pTags","0");
                pAnon = "0";
                upNumberTW.setVisibility(View.INVISIBLE);
            }else if (pType.equals("Stack")){
                upVoteNumber = extras.getString("upVoteNumber", "0");
                pAnon = extras.getString("pAnon", "0");
                upNumberTW.setText(upVoteNumber);
            }else{
                Toast.makeText(this, "Burada bir hata var Ömere haber ver", Toast.LENGTH_SHORT).show();
            }

            pTime = extras.getString("pTime", "0");
            pTitle = extras.getString("pTitle", "0");
            pDetails = extras.getString("pDetails", "0");
            username = extras.getString("username", "0");
            pId = extras.getString("pId", "0");
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

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Adding comment...");

        //Convert TimeStamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(pTime));
        String pPostedTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        //pTitleTW.setText(pTitle);
        questionContentTW.setText(pDetails);
        //pPostedTimeTW.setText(pTime);
        relatedTagTW.setText("#Math-102"); //TODO databaseden çek
        if (!pAnon.equals("1")) {
            pUserNameTW.setText(username);
        } else {
            pUserNameTW.setText("Anonymous user");
        }
        //TODO comment görünümünde foto gösterme üzerine düşün
        //TODO user info gerekirse 23. video 32.dakikanın başına git



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
                    hashMap.put("upNumber", "0");
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
                                    pd.dismiss();
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
                                    pd.dismiss();
                                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

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

                    // adapter
                    adapterComment[0] = new AdapterComment(PostActivity.this, commentList, pId, modelComment.getCId());
                    // set adapter to recyclerView
                    recyclerView.setAdapter(adapterComment[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // in case of error
                //Toast.makeText(context, "Error on load comments 248. line", Toast.LENGTH_SHORT).show();
            }
        });


        // Detect touched area
        detector = new SimpleGestureFilter(PostActivity.this, this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        //Detect the swipe gestures and display toast
        String showToastMessage = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                showToastMessage = "You have Swiped Right.";
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                showToastMessage = "You have Swiped Left.";
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                showToastMessage = "You have Swiped Down.";
                break;
            case SimpleGestureFilter.SWIPE_UP:
                showToastMessage = "You have Swiped Up.";
                System.out.println("swipe up");
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) commentCardView.getLayoutParams();
                layoutParams.topMargin = 590;
                break;

        }
        Toast.makeText(PostActivity.this, showToastMessage, Toast.LENGTH_SHORT).show();
    }


    //Toast shown when double tapped on screen
    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "You have Double Tapped.", Toast.LENGTH_SHORT)
                .show();
        System.out.println("asdasdas");
    }

    private void openOrCloseCard(){
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) commentCardView.getLayoutParams();
        if (layoutParams.topMargin == 0){
            layoutParams.topMargin = previousMargin;
        }else{
            previousMargin = layoutParams.topMargin;
            layoutParams.topMargin = 0;
        }
        commentCardView.setLayoutParams(layoutParams);
    }
}