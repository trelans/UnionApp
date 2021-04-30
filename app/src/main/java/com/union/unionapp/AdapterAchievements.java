package com.union.unionapp;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
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
import com.union.unionapp.notifications.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterAchievements extends RecyclerView.Adapter<AdapterAchievements.HolderNotification>{

    private Context context;
    private ArrayList<ModelAchievements> notificationsList;
    private FirebaseAuth firebaseAuth;

    public AdapterAchievements(Context context, ArrayList<ModelAchievements> notificationsList) {
        this.context = context;
        this.notificationsList = notificationsList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate view row_notification
        View view = LayoutInflater.from(context).inflate(R.layout.row_achievements, parent,false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        // get and set data to views

        // get data
        final  ModelAchievements modelNotification = notificationsList.get(position);
        String description = modelNotification.getDescription();
        int genre = Integer.parseInt(modelNotification.getGenre());
        String level = modelNotification.getLevel();
        String nId = modelNotification.getnId();
        String point = modelNotification.getPoint();
        String title = modelNotification.getTitle();
        String[] genreString = {"empty" , "Math" , "Carrier" , "Sport" , "Technology", "Social", "English", "Turkish", "Study"};
        Dialog myDialog;
        myDialog = new Dialog(context);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.setContentView(R.layout.custom_popup_achievements);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView titleTv = (TextView) myDialog.findViewById(R.id.achtitle);
        TextView genreTv = (TextView) myDialog.findViewById(R.id.Achgenre);
        TextView descriptionTv = (TextView) myDialog.findViewById(R.id.achDescripton);
        ImageView achicon = (ImageView) myDialog.findViewById(R.id.achicon);
        // set to views
        holder.AchnotificationTv.setText(title);

        // TODO YUNUSTAN ALINAN ICONLAR

        // set  dialog views
        titleTv.setText(title);
        genreTv.setText("Genre : " + genreString[genre]);
        // TODO ICON
        achicon.setImageResource(R.drawable.bronze_medal);
        descriptionTv.setText(description);
/*
        if (type.equals("1")) {
            holder.avatarIv.setImageResource(R.drawable.buddy_icon);
        }
        else if (type.equals("2")) {
            holder.avatarIv.setImageResource(R.drawable.club_icon);
        }
        else if (type.equals("3")) {
            holder.avatarIv.setImageResource(R.drawable.stack_icon);
        }
        else {
            holder.avatarIv.setImageResource(R.drawable.stack_icon); // öylesine çökmesin diye
        }
*/
        //TODO Tıklandığında postu acıcak
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.show();
                System.out.println("DSADAS");

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
        TextView  AchnotificationTv;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            //init views
            avatarIv = itemView.findViewById(R.id.AchavatarIv);
            AchnotificationTv = itemView.findViewById(R.id.AchnotificationTv);




        }
    }
}


