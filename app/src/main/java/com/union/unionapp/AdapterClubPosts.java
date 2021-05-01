package com.union.unionapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class AdapterClubPosts extends RecyclerView.Adapter<AdapterClubPosts.MyHolder> {

    Context context;
    List<ModelBuddyAndClubPost> postList;
    private static boolean isBackgroundBlurred = false;

    public AdapterClubPosts(Context context, List<ModelBuddyAndClubPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    public static boolean isBlurred() {
        return isBackgroundBlurred ;
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
        String pTags = postList.get(position).getpTags();
        String username = postList.get(position).getUsername();
        String uPp = postList.get(position).getuPp();

        String[] newTags = new String[3];
        newTags[0] = "";
        newTags[1] = "";
        newTags[2] = "";


        String[] tags = pTags.split(",");
        String[] allTags = MainActivity.getAllTags();

        if (tags.length > 0) {
            for (int i = 0; i < tags.length; i++) {
                newTags[i] = allTags[Integer.valueOf(tags[i])];
            }
        }

        //set data
        holder.contentTextView.setText(pDetails);
        holder.titleTextView.setText(pTitle);
        holder.dateTW.setText(pDate + " " + pHour);
        holder.zoomLinkTW.setText(pLocation);
        holder.genderTW.setText("Optional");
        holder.quotaTW.setText(pQuota);
        holder.publisherNameTW.setText("@" + username);


        if( newTags[0].equals("")) {
            holder.topicTagTW1.setVisibility(View.INVISIBLE);
        }
        else {
            if (tags[0].equals("0")) {
                holder.topicTagTW1.setVisibility(View.INVISIBLE);
            }
            else {
                holder.topicTagTW1.setText(newTags[0]);
            }
        }

        if( newTags[1].equals("")) {
            holder.topicTagTW2.setVisibility(View.INVISIBLE);
        }
        else {
            holder.topicTagTW2.setText(newTags[1]);
        }

        if( newTags[2].equals("")) {
            holder.topicTagTW3.setVisibility(View.INVISIBLE);
        }
        else {
            holder.topicTagTW3.setText(newTags[2]);
        }

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

                //Kutay's calendar code
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String uid = mAuth.getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Users/"+ uid );

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("pType", "Club");
                hashMap.put("pTitle", pTitle);
                hashMap.put("pHour", pHour );
                hashMap.put("pDate", pDate );
                hashMap.put("pId", pId);
                String caluid = userRef.push().getKey();

                userRef.child("Calendar").child(caluid).setValue(hashMap);

                //User calender bitişi

                //TODO Ömer's Calendar code

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

                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("pType", "Club");
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
                intent.putExtra("pTags", pTags);
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

    private void calendarToServer() {

    }
}
