package com.union.unionapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterStackPosts extends RecyclerView.Adapter<AdapterStackPosts.MyHolder> {

    Context context;
    List<ModelStackPost> postList;
    DatabaseReference ref1;
    String[] allTags;

    public AdapterStackPosts(Context context, List<ModelStackPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout custom_stack_over_flow_feed_cards.xml
        View view = LayoutInflater.from(context).inflate(R.layout.custom_stack_over_flow_feed_cards, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getUEmail();
        String username = postList.get(position).getUsername();
        String pAnon = postList.get(position).getPAnon();
        String pId = postList.get(position).getPId();
        String pTitle = postList.get(position).getPTitle();
        String pDetails = postList.get(position).getPDetails();
        String pImage = postList.get(position).getPImage();
        String pTime = postList.get(position).getPTime();
        String pTag = postList.get(position).getpTagIndex();

        final String[] upVoteNumber = {postList.get(position).getPUpvoteNumber()};

        /*
        //Convert timestamp to dd//mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
         */

        //set data
        holder.contentTextView.setText(pDetails);
        holder.titleTextView.setText(pTitle);
        holder.upNumber.setText(upVoteNumber[0]);
        //holder.upNumber.setText("1");


        //allTags = getResources.getStringArray(R.array.all_tags); !!!!! getResources metodu fragment classı için var.
        allTags = MainActivity.getAllTags();
        System.out.println();

        if (Integer.valueOf(pTag) != 0) {
            holder.topicTag.setText(allTags[Integer.valueOf(pTag)]); //TODO
        }
        else {
            holder.topicTag.setText("No Tag");
        }
        //if there is no image
        if (pImage.equals("noImage")) {
            //hide imageView
        }

        /* aynısından image için de yaptı
        //set user dp
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.user_pp_template).into(holder.uPictureIv);
        }catch (Exception e){
            e.printStackTrace();
        }
         */
        ref1 = FirebaseDatabase.getInstance().getReference("BilkentUniversity/StackPosts/" + postList.get(position).pId);
        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> updateUpNumber = new HashMap<>();
                // Checking the background and do the increment or decrement accordingly
                //if (Objects.equals(holder.upButton.getBackground().getConstantState(), ContextCompat.getDrawable(context,R.drawable.up_icon).getConstantState())) {
                updateUpNumber.put("pUpvoteNumber", Integer.parseInt(postList.get(position).getPUpvoteNumber()) + 1 + "");
                //}
                ref1.updateChildren(updateUpNumber);
                holder.upNumber.setText(upVoteNumber[0]);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Dialog dialog;
                RecyclerView recyclerView;
                List<ModelComment> commentList;
                final AdapterComment[] adapterComment = new AdapterComment[1];
                dialog = new Dialog(context);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.custom_view_stack_post_popup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                System.out.println(allTags[1]);

                //recycler view and its properties
                recyclerView = dialog.findViewById(R.id.commentStackRecyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                //show newest post first, for this load from last
                layoutManager.setStackFromEnd(true);
                layoutManager.setReverseLayout(true);
                //set layout to recyclerview
                recyclerView.setLayoutManager(layoutManager);

                //init post list
                commentList = new ArrayList<>();

                ProgressDialog pd = new ProgressDialog(context);
                pd.setMessage("Adding comment...");

                TextView relatedTagTW = dialog.findViewById(R.id.relatedTagTextView);
                TextView pTitleTW = dialog.findViewById(R.id.pTitle);
                TextView pUserNameTW = dialog.findViewById(R.id.pUserName);
                TextView upNumberTW = dialog.findViewById(R.id.upNumberTextView);
                TextView questionContentTW = dialog.findViewById(R.id.askedQuestionTextView);
                TextView pPostedTimeTW = dialog.findViewById(R.id.pPostedTime);
                ImageView addPhotoIV = dialog.findViewById(R.id.imageViewAddPhoto);
                EditText commentET = dialog.findViewById(R.id.answerEditText);
                CheckBox isAnonCB = dialog.findViewById(R.id.anonymCheckBox);
                ImageButton sendButton = dialog.findViewById(R.id.sendButtonIB);



                //Convert TimeStamp to dd/mm/yyyy hh:mm am/pm
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(pTime));
                String pPostedTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

                //set data
                pTitleTW.setText(pTitle);
                questionContentTW.setText(pDetails);
                upNumberTW.setText(upVoteNumber[0]);
                pPostedTimeTW.setText(pTime);
                relatedTagTW.setText("#Math-102"); //TODO databaseden çek
                if (!pAnon.equals("1")) {
                    pUserNameTW.setText(username);
                } else {
                    pUserNameTW.setText("Anonymous user");
                }
                //TODO comment görünümünde foto gösterme üzerine düşün
                //TODO user info gerekirse 23. video 32.dakikanın başına git

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String timeStamp;
                        FirebaseUser user;
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                        } else {
                            Toast.makeText(context, "There is no current user!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //get data from comment editText
                        String comment = commentET.getText().toString().trim();
                        // validate
                        if (TextUtils.isEmpty(comment)) {
                            //no value is entered
                            Toast.makeText(context, "Comment is empty...", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            adapterComment[0] = new AdapterComment(context, commentList, pId, modelComment.getCId());
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

                dialog.show();
                return false;
            }

        });


    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from custom_stack_over_flow_feed_cards.xml
        ImageButton upButton;
        TextView contentTextView, titleTextView;
        TextView upNumber;
        TextView topicTag;
        CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            upButton = itemView.findViewById(R.id.upButtonImageView);
            contentTextView = itemView.findViewById(R.id.contentTW);
            titleTextView = itemView.findViewById(R.id.titleTW);
            upNumber = itemView.findViewById(R.id.textViewUpNumber);
            topicTag = itemView.findViewById(R.id.topicTagTW);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}
