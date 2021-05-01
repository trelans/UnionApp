package com.union.unionapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.HolderNotification>{

    private Context context;
    private ArrayList<ModelNotification> notificationsList;
    private FirebaseAuth firebaseAuth;

    public AdapterNotification(Context context, ArrayList<ModelNotification> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
        firebaseAuth = FirebaseAuth.getInstance();
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
        final  ModelNotification modelNotification = notificationsList.get(position);
        String name = modelNotification.getsName();
        String notification = modelNotification.getNotification();
        String image = modelNotification.getsImage();
        String timestamp = modelNotification.getTimestamp();
        String senderUid = modelNotification.getsUid();
        String pId = modelNotification.getpId();
        // conver timestamp to dd//mm/yyyy hh:mm

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
        final String postType;

        //TODO notificationsı commentlerde yapıcaksak eklenti yapılmalı
        if (notification.contains("pro")) {
            postType = "Stack";
        }
        else if (notification.contains("announc")) {
            postType = "Club";
        }
        else {
            postType = "Buddy";
        }
        // we will get the name, e mail image of notif
            //TODO burası yanlıs foto çekerken hata olur düzeltilcek
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Notifications/");
        reference.orderByChild("uid").equalTo(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String username = "" + ds.child("username").getValue();
                            String image = "" + ds.child("image").getValue();
                            String email = "" + ds.child("email").getValue(); // gereksiz gibi

                            // set to views
                            holder.nameTv.setText("@" + username);
                            try {
                                Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(holder.avatarIv);
                            } catch (Exception e) {
                                holder.avatarIv.setImageResource(R.drawable.profile_icon);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
        //TODO Tıklandığında postu acıcak
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( context, PostActivity.class);
                i.putExtra("pType",postType);
                i.putExtra("source", "outside");
                i.putExtra("pId", pId);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);

            }
        });
        // long press to delete
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // show conf dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this notification?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete notif
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BilkentUniversity/Users");
                        ref.child(firebaseAuth.getUid()).child("Notifications").child(senderUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //deleted
                            }
                        }). addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // failed
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel
                        dialog.dismiss();
                    }
                });
                return false;
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
        TextView nameTv, notificationTv, timeTv;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.usernameTv);
            notificationTv = itemView.findViewById(R.id.notificationTv);
            timeTv = itemView.findViewById(R.id.timeTv);



        }
    }
}
