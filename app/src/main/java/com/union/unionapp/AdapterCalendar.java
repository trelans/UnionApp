package com.union.unionapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.union.unionapp.notifications.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
/*
public class AdapterCalendar extends RecyclerView.Adapter<AdapterCalendar.HolderNotification>{

    private Context context;
    private ArrayList<ModelLastActivities> notificationsList;
    private FirebaseAuth firebaseAuth;

    public AdapterCalendar(Context context, ArrayList<ModelCalendar> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate view row_notification
        View view = LayoutInflater.from(context).inflate(R.layout.row_calendar, parent,false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        // get and set data to views

        // get data
        final  ModelLastActivities modelNotification = notificationsList.get(position);
        String name = modelNotification.getsName();
        String notification = modelNotification.getNotification();
        String timestamp = modelNotification.getTimestamp();
        String senderUid = modelNotification.getsUid();
        String pId = modelNotification.getpId();
        String type = modelNotification.getType();
        // conver timestamp to dd//mm/yyyy hh:mm

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();





        // set to views
        holder.AchnotificationTv.setText(notification);
        holder.AchtimeTv.setText(dateTime);
        if (type.equals("Buddy")) {
            holder.avatarIv.setImageResource(R.drawable.buddy_icon);
        }
        else if (type.equals("Club")) {
            holder.avatarIv.setImageResource(R.drawable.club_icon);
        }
        else {
            holder.avatarIv.setImageResource(R.drawable.stack_icon); // öylesine çökmesin diye
        }

        //TODO Tıklandığında postu acıcak
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    // holder class for views of row_notifications.xlm
    class HolderNotification extends RecyclerView.ViewHolder {
        // declare views
        ImageView avatarIv;
        TextView  AchnotificationTv, AchtimeTv;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.AchavatarIv);
            AchnotificationTv = itemView.findViewById(R.id.AchnotificationTv);
            AchtimeTv = itemView.findViewById(R.id.AchtimeTv);



        }
    }




}

*/
