package com.union.unionapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterStackPosts extends RecyclerView.Adapter<AdapterStackPosts.MyHolder> {

    Context context;
    List<ModelStackPost> postList;
    DatabaseReference ref1;
    String[] allTags;
    Activity currentActivity;
    FirebaseUser firebaseUser;

    public AdapterStackPosts(Context context, List<ModelStackPost> postList, Activity currentActivity) {
        this.context = context;
        this.postList = postList;
        this.currentActivity = currentActivity;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        String pDetails = "      " +postList.get(position).getPDetails();
        String pImage = postList.get(position).getPImage();
        String pTime = postList.get(position).getPTime();
        String pTag = postList.get(position).getpTagIndex();
        List<String> pUpUsers = postList.get(position).getpUpUsers();

        final int[] upVoteNumber = {postList.get(position).getpUpvoteNumber()};

        //set data
        holder.contentTextView.setText(pDetails);
        holder.titleTextView.setText(pTitle);
        holder.upNumber.setText(upVoteNumber[0] + "");

        allTags = MainActivity.getAllTags();

        if (Integer.parseInt(pTag) != 0) {
            holder.topicTag.setVisibility(View.VISIBLE);
            holder.topicTag.setText(allTags[Integer.parseInt(pTag)]); //TODO
            System.out.println("Tag: " + pTag);
        } else {
            holder.topicTag.setVisibility(View.INVISIBLE);
            System.out.println("Tag: " + pTag);
        }
        //if there is no image
        if (pImage.equals("noImage")) {
            //hide imageView
        }


        if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Toast.makeText(context, "It is your post!", Toast.LENGTH_LONG).show();
            holder.upButton.setEnabled(false);
        }else{
            holder.upButton.setEnabled(true);
        }

        if (pUpUsers != null && pUpUsers.contains(firebaseUser.getUid())){
            holder.upButton.setEnabled(true);
            holder.upButton.setBackground(ContextCompat.getDrawable(context,R.drawable.up_pressed));
        }else{
            holder.upButton.setEnabled(true);
            holder.upButton.setBackground(ContextCompat.getDrawable(context,R.drawable.up_icon));
        }

        ref1 = FirebaseDatabase.getInstance().getReference("BilkentUniversity/StackPosts");
        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> updateUpNumber = new HashMap<>();

                //if button is already pressed
                if (holder.upButton.getBackground().getConstantState().equals(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.up_pressed)).getConstantState())){
                    holder.upButton.setBackground(ContextCompat.getDrawable(context,R.drawable.up_icon));
                    updateUpNumber.put("pUpvoteNumber", postList.get(position).getpUpvoteNumber() - 1);
                    pUpUsers.remove(firebaseUser.getUid());
                    upVoteNumber[0] = postList.get(position).getpUpvoteNumber() - 1;
                    System.out.println("giriyor");
                }else{
                    holder.upButton.setBackground(ContextCompat.getDrawable(context,R.drawable.up_pressed));
                    updateUpNumber.put("pUpvoteNumber", postList.get(position).getpUpvoteNumber() + 1);
                    pUpUsers.add(firebaseUser.getUid());
                    upVoteNumber[0] = postList.get(position).getpUpvoteNumber() + 1;
                }

                updateUpNumber.put("pUpUsers", pUpUsers);
                ref1.child(pId).updateChildren(updateUpNumber);
                holder.upNumber.setText(upVoteNumber[0] + "");
            }
        });


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(currentActivity, PostActivity.class);
                intent.putExtra("pType", "Stack");
                intent.putExtra("pTime", pTime);
                intent.putExtra("pTitle", pTitle);
                intent.putExtra("pDetails", pDetails);
                intent.putExtra("username", username);
                intent.putExtra("pId", pId);
                intent.putExtra("pImage", pImage);
                intent.putExtra("uPp", uid);

                // Dif. from buddy
                intent.putExtra("upVoteNumber", upVoteNumber[0] + "");
                intent.putExtra("pAnon", pAnon);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);

                return true;
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
