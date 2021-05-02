package com.union.unionapp;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterCalendar extends RecyclerView.Adapter<AdapterCalendar.HolderCalendar>{

    private Context context;
    private ArrayList<ModelCalendar> calendarList;
    private FirebaseAuth firebaseAuth;

    public AdapterCalendar(Context context, ArrayList<ModelCalendar> calendarList) {
        this.context = context;
        this.calendarList = calendarList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderCalendar onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate view row_notification
        View view = LayoutInflater.from(context).inflate(R.layout.row_calendar, parent,false);
        return new HolderCalendar(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCalendar holder, int position) {
        // get and set data to views

        // get data
        final  ModelCalendar modelCalendar = calendarList.get(position);
        String username = modelCalendar.getUsername();
        String postId = modelCalendar.pId;
        String postTitle = modelCalendar.getpTitle();
        String postType = modelCalendar.getpType();
        String date = modelCalendar.getpDate();
        String hour = modelCalendar.getpHour();
        // conver timestamp to dd//mm/yyyy hh:mm





        // set to views
        holder.calendartitletv.setText("@"+username + ": " +postTitle);
        holder.calendartimeTv.setText(hour);
        if (postType.equals("Buddy")) {
            holder.avatarIv.setImageResource(R.drawable.buddy_icon);
        }
        else if (postType.equals("Club")) {
            holder.avatarIv.setImageResource(R.drawable.club_icon);
        }
        else {
            holder.avatarIv.setImageResource(R.drawable.stack_icon); // öylesine çökmesin diye
        }

        //TODO Tıklandığında postu acıcak
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( context, PostActivity.class);
                i.putExtra("pType",postType);
                i.putExtra("source", "outside");
                i.putExtra("pId", postId);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);
            }
        });



    }

    @Override
    public int getItemCount() {
        return calendarList.size();
    }

    // holder class for views of row_notifications.xlm
    class HolderCalendar extends RecyclerView.ViewHolder {
        // declare views
        ImageView avatarIv;
        TextView  calendartitletv, calendartimeTv;

        public HolderCalendar(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.calendariconIv);
            calendartitletv = itemView.findViewById(R.id.calendartitleTv);
            calendartimeTv = itemView.findViewById(R.id.calendarTimeTv);




        }
    }




}

