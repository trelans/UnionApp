package com.union.unionapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterStackPosts extends RecyclerView.Adapter<AdapterStackPosts.MyHolder> {

    Context context;
    List<ModelStackPost> postList;

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
        String uEmail = postList.get(position).getuEmail();
        String username = postList.get(position).getUsername();
        String pAnon = postList.get(position).getpAnon();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDetails = postList.get(position).getpDetails();
        String pImage = postList.get(position).getpImage();
        String pTime = postList.get(position).getpTime();
        final String[] upVoteNumber = {postList.get(position).getpUpvoteNumber()};

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
        holder.topicTag.setText("Math-101");

        //if there is no image
        if (pImage.equals("noImage")){
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

        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upVoteNumber[0] = Integer.valueOf(upVoteNumber[0]) + 1 + "";
                holder.upNumber.setText(upVoteNumber[0]);
                Toast.makeText(context, "up +1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Dialog dialog;
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_view_stack_post_popup);
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                //TODO tanımlamaları yap
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
        EditText upNumber;
        TextView topicTag;
        CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            upButton = itemView.findViewById(R.id.upButtonImageView);
            contentTextView = itemView.findViewById(R.id.contentTW);
            titleTextView = itemView.findViewById(R.id.titleTW);
            upNumber = itemView.findViewById(R.id.editTextUpNumber);
            topicTag = itemView.findViewById(R.id.topicTagTW);
            cardView = itemView.findViewById(R.id.card);

        }
    }
}
