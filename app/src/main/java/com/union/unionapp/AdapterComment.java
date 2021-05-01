package com.union.unionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyHolder> {
    Context context;
    List<ModelComment> commentList;
    String postId;
    String commentId;
    DatabaseReference ref;

    public AdapterComment(Context context, List<ModelComment> commentList, String postId, String commentId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
        this.commentId = commentId;
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
        String upNumber = commentList.get(position).getUpNumber();


        //set data
        holder.answerTV.setText(getShortComment(uName, comment, anonymous));
        holder.upIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 4/26/21 burayı ve üstüne basıldığında yeni diyalog olarak commentin büyük halinin açılmasını yap
                HashMap<String, Object> updateUpNumber = new HashMap<>();
                updateUpNumber.put("upNumber", Integer.valueOf(commentList.get(position).getUpNumber()) + 1 + "");
                ref.child(commentList.get(position).getCId()).updateChildren(updateUpNumber);
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
        ImageView upIconIV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            rowLL = itemView.findViewById(R.id.rowLL);
            answerTV = itemView.findViewById(R.id.answerTV);
            upIconIV = itemView.findViewById(R.id.upIconIV);
        }

    }
}
