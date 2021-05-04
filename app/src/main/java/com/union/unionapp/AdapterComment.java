package com.union.unionapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyHolder> {

    Context context;
    List<ModelComment> commentList;
    String postId;
    DatabaseReference ref;
    String userUid;

    public AdapterComment(Context context, List<ModelComment> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
        this.userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public AdapterComment.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout custom_stack_over_flow_feed_cards.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Comments/").child(postId);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComment.MyHolder holder, int position) {

        //get data
        String uName = commentList.get(position).getUName();
        String comment = commentList.get(position).getComment();
        String anonymous = commentList.get(position).getCAnon();
        String cPhoto = commentList.get(position).getCPhoto();
        int upNumber = commentList.get(position).getUpNumber();
        List<String> cUpUsers = commentList.get(position).getcUpUsers();


        if (cUpUsers != null && cUpUsers.contains(userUid)){
            holder.upIconIV.setBackground(ContextCompat.getDrawable(context,R.drawable.up_pressed));
        }else{
            holder.upIconIV.setBackground(ContextCompat.getDrawable(context,R.drawable.up_icon));
        }
        System.out.println("buralarda");
        //set data
        holder.answerTV.setText(getShortComment(uName, comment, anonymous));
        holder.upIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> updateUpNumber = new HashMap<>();
                System.out.println("basdtı");
                if (holder.upIconIV.getBackground().getConstantState().equals(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.up_pressed)).getConstantState())) {
                    holder.upIconIV.setBackground(ContextCompat.getDrawable(context, R.drawable.up_icon));
                    updateUpNumber.put("upNumber", commentList.get(position).getUpNumber() - 1);
                    cUpUsers.remove(userUid);
                }else{
                    holder.upIconIV.setBackground(ContextCompat.getDrawable(context,R.drawable.up_pressed));
                    updateUpNumber.put("upNumber", commentList.get(position).getUpNumber() + 1);
                    cUpUsers.add(userUid);
                }
                updateUpNumber.put("cUpUsers", cUpUsers);
                ref.child(commentList.get(position).getCId()).updateChildren(updateUpNumber);
            }
        });

        System.out.println("Cphoto: " + cPhoto);
        if(cPhoto != null && !cPhoto.equals("noImage")){
            holder.clickToSeeImage.setVisibility(View.VISIBLE);
        }else{
            holder.clickToSeeImage.setVisibility(View.GONE);
        }

        holder.clickToSeeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.custom_comment_image_view);
                ImageView commentImageIV = dialog.findViewById(R.id.commentImageIV);
                try {
                    //if image received, set
                    FirebaseStorage.getInstance().getReference("BilkentUniversity/Comments/" + cPhoto).getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Picasso.get().load(R.drawable.user_pp_template).into(commentImageIV);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(commentImageIV);
                        }
                    });
                } catch (Exception e) {
                    //if there is any exception while getting image then set default
                    Picasso.get().load(R.drawable.user_pp_template).into(commentImageIV);
                }

                dialog.show();
            }
        });
    }

    private String getShortComment(String uName, String comment, String anonymous) {

        String name;
        String shortComment;

        if (anonymous.equals("1")){
            name = "@anonymous_user: ";
        }else{
            name = "@" + uName + ": ";
        }

        shortComment = name + comment;
        return shortComment;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // View holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from row_comment.xml
        LinearLayout rowLL;
        TextView answerTV;
        ImageButton upIconIV;
        TextView clickToSeeImage;

        public MyHolder(@NonNull View itemView) {

            super(itemView);
            rowLL = itemView.findViewById(R.id.rowLL);
            answerTV = itemView.findViewById(R.id.answerTV);
            upIconIV = itemView.findViewById(R.id.upIconIV);
            clickToSeeImage = itemView.findViewById(R.id.clickToSeeImageTW);
        }

    }
}
