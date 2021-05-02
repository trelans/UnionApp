package com.union.unionapp;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdapterBuddyPosts extends RecyclerView.Adapter<AdapterBuddyPosts.MyHolder> {

    Context context;
    List<ModelBuddyAndClubPost> postList;

    public AdapterBuddyPosts(Context context, List<ModelBuddyAndClubPost> postList) {
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
    public void onBindViewHolder(@NonNull AdapterBuddyPosts.MyHolder holder, int position) {

        //get data
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDate = postList.get(position).getpDate();
        String pHour = postList.get(position).getpHour();
        String pLocation = postList.get(position).getpLocation();
        String pQuota = postList.get(position).getpQuota();
        String pDetails = "      " + postList.get(position).getpDetails();
        String pImage = postList.get(position).getpImage();
        String pTime = postList.get(position).getpTime();
        String hisUid = postList.get(position).getUid();
        String pTags = postList.get(position).getpTags();
        String pGender = postList.get(position).getpGender();
        String username = postList.get(position).getUsername();
        String uPp = postList.get(position).getuPp();

        //set data
        holder.contentTextView.setText(pDetails);
        holder.titleTextView.setText(pTitle);
        holder.dateTW.setText(pDate + " " + pHour);
        holder.zoomLinkTW.setText("Location: " + pLocation);
        holder.genderTW.setText("Gender: " + pGender);
        holder.quotaTW.setText("Quota:      " + pQuota);
        holder.publisherNameTW.setText("@" + username);
        // TODO resim eklemeyi yap holder.publisherPP.setImageResource();

        convertStringTagsToRealTags(holder, pTags);


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

        holder.calendarIB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //TODO calendar ekleme işlemini yap
                Toast.makeText(context, "calendara basıldı", Toast.LENGTH_SHORT).show();

                //Kutay's calendar code
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String uid = mAuth.getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users/" + uid);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("pType", "Buddy");
                hashMap.put("pTitle", pTitle);
                hashMap.put("pHour", pHour);
                hashMap.put("pDate", pDate);
                hashMap.put("pId", pId);
                hashMap.put("username", username);

                String fixedDate = pDate.replace("/", "_");
                userRef.child("Calendar").child(fixedDate).child(pId).setValue(hashMap);

                //User calender bitişi

                String[] calendarDate = pDate.split("/");
                String[] calendarTime = pHour.split(":");
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(calendarDate[2]), Integer.parseInt(calendarDate[1]) - 1, Integer.parseInt(calendarDate[0]), Integer.parseInt(calendarTime[1]), Integer.parseInt(calendarTime[0]));
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("eventLocation", pLocation);
                //TODO etkinlik bitiş saati intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                intent.putExtra("title", "@" + username + "'s Event");
                intent.putExtra(CalendarContract.Events.DESCRIPTION, pTitle);
                context.startActivity(intent);


            }
        });

        holder.sendButtonIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("Hisuid", hisUid);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                context.startActivity(i);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("pType", "Buddy");
                intent.putExtra("pTime", pTime);
                intent.putExtra("pTitle", pTitle);
                intent.putExtra("pDetails", pDetails);
                intent.putExtra("username", username);
                intent.putExtra("pId", pId);
                intent.putExtra("uPp", uPp);

                //different from stack
                intent.putExtra("pDate", pDate);
                intent.putExtra("pHour", pHour);
                intent.putExtra("pLocation", pLocation);
                intent.putExtra("pQuota", pQuota);
                intent.putExtra("pImage", pImage);
                intent.putExtra("pGender", pGender);
                intent.putExtra("pTags", pTags);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                return true;
            }
        });


    }

    private void convertStringTagsToRealTags(@NonNull MyHolder holder, String pTags) {
        String[] newTags = new String[3];
        newTags[0] = "";
        newTags[1] = "";
        newTags[2] = "";


        String[] tags = pTags.split(",");
        String[] allTags = MainActivity.getAllTags();

        if (tags.length == 0) {
            newTags[0] = "";
        } else if (tags.length == 1) {
            newTags[0] = allTags[Integer.parseInt(tags[0])]; // newTags[0] = allTags[Integer.valueOf(tags[0])];
        } else {
            for (int i = 0; i < tags.length; i++) {
                newTags[i] = allTags[Integer.parseInt(tags[i])];
            }
        }

        if (newTags[0].equals("")) {
            holder.topicTagTW1.setVisibility(View.INVISIBLE);
        } else {
            if (tags[0].equals("0")) {
                holder.topicTagTW1.setVisibility(View.INVISIBLE);
            } else {
                holder.topicTagTW1.setVisibility(View.VISIBLE);
                holder.topicTagTW1.setText(newTags[0]);
            }
        }

        if (newTags[1].equals("")) {
            holder.topicTagTW2.setVisibility(View.INVISIBLE);
        } else {
            holder.topicTagTW2.setVisibility(View.VISIBLE);
            holder.topicTagTW2.setText(newTags[1]);
        }

        if (newTags[2].equals("")) {
            holder.topicTagTW3.setVisibility(View.INVISIBLE);
        } else {
            holder.topicTagTW3.setVisibility(View.VISIBLE);
            holder.topicTagTW3.setText(newTags[2]);
        }
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
        TextView contentTextView, titleTextView, dateTW, zoomLinkTW, genderTW, quotaTW, topicTagTW1, topicTagTW2, topicTagTW3, publisherNameTW;
        ImageView publisherPP;
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
            publisherNameTW = itemView.findViewById(R.id.publisherNameTextView);
            publisherPP = itemView.findViewById(R.id.publisherPp);
            topicTagTW1 = itemView.findViewById(R.id.topicTagTW);
            topicTagTW2 = itemView.findViewById(R.id.topicTagTW2);
            topicTagTW3 = itemView.findViewById(R.id.topicTagTW3);

        }
    }
}
