package com.union.unionapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterClubPosts extends RecyclerView.Adapter<AdapterClubPosts.MyHolder> {

    Context context;
    List<ModelBuddyAndClubPost> postList;

    public AdapterClubPosts(Context context, List<ModelBuddyAndClubPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout custom_stack_over_flow_feed_cards.xml
        View view = LayoutInflater.from(context).inflate(R.layout.custom_feed_card, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClubPosts.MyHolder holder, int position) {
        //get data
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDate = postList.get(position).getpDate();
        String pHour = postList.get(position).getpHour();
        String pLocation = postList.get(position).getpLocation();
        String pQuota = postList.get(position).getpQuota();
        String pDetails = postList.get(position).getpDetails();
        String pImage = postList.get(position).getpImage();
        String pTime = postList.get(position).getpTime();
        String hisUid = postList.get(position).getUid();

        //set data
        holder.contentTextView.setText(pDetails);
        holder.titleTextView.setText(pTitle);
        holder.dateTW.setText(pDate + " " + pHour);
        holder.zoomLinkTW.setText(pLocation);
        holder.genderTW.setText("optional");
        holder.quotaTW.setText(pQuota);
        holder.topicTagTW.setText("#AI");

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

        holder.calendarIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO calendar ekleme işlemini yap
                Toast.makeText(context, "calendara basıldı", Toast.LENGTH_SHORT).show();
            }
        });

        holder.sendButtonIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO mesaj kısmına iletmeyi yap
                Toast.makeText(context, "Profile mesaj göndermeye basıldı", Toast.LENGTH_SHORT).show();
                Intent i = new Intent( context, ChatActivity.class);
                i.putExtra("Hisuid", hisUid);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                context.startActivity(i);
            }
        });



        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Dialog dialog;
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_view_club_post_popup);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                //TODO tanımlamaları yap
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

        //views from custom_feed_card.xml
        ImageButton calendarIB;
        ImageButton sendButtonIB;
        TextView contentTextView, titleTextView, dateTW, zoomLinkTW, genderTW, quotaTW, topicTagTW;
        CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            calendarIB = itemView.findViewById(R.id.calendarIB);
            sendButtonIB = itemView.findViewById(R.id.sendButtonIB);
            contentTextView = itemView.findViewById(R.id.contentTW);
            titleTextView = itemView.findViewById(R.id.titleTW);
            dateTW = itemView.findViewById(R.id.dateTWBuddy);
            zoomLinkTW = itemView.findViewById(R.id.zoomLinkTW);
            genderTW = itemView.findViewById(R.id.genderPreferenceTW);
            quotaTW = itemView.findViewById(R.id.quotaTW);
            cardView = itemView.findViewById(R.id.card);
            topicTagTW = itemView.findViewById(R.id.topicTagTW);

        }
    }
}
