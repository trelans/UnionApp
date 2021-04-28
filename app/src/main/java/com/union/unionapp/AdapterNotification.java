package com.union.unionapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.HolderNotification>{

    private Context context;
    private ArrayList<ModelNotification> notificationsList;

    public AdapterNotification(Context context, ArrayList<ModelNotification> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate view row_notification
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent,false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        // get and set data to views

        // get data
        ModelNotification modelNotification = notificationsList.get(position);
        String name = modelNotification.getsName();
        String notification = modelNotification.getNotification();
        String image = modelNotification.getsImage();
        String timestamp = modelNotification.getTimestamp();
        // conver timestamp to dd//mm/yyyy hh:mm

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
      //  cal.setTimeInMillis(Long.parseLong(MainActivity.getServerDate()));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();


        // set to views
        holder.nameTv.setText(name);
        holder.notificationTv.setText(notification);
        holder.timeTv.setText(dateTime);
        try {
            Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(holder.avatarIv);
        }
        catch (Exception e) {
         holder.avatarIv.setImageResource(R.drawable.profile_icon);
        }

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    // holder class for views of row_notifications.xlm
    class HolderNotification extends RecyclerView.ViewHolder {
        // declare views
        ImageView avatarIv;
        TextView nameTv, notificationTv, timeTv;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            notificationTv = itemView.findViewById(R.id.notificationTv);
            timeTv = itemView.findViewById(R.id.timeTv);



        }
    }
}
