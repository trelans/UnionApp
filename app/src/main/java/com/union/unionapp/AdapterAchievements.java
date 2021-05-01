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
        String[] genreString = {"Admin" , "Math" , "Carrier" , "Sport" , "Technology", "Social", "English", "Turkish", "Study"};
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
        int lvl = Integer.parseInt(level);
/*
                // "Math" , "Carrier" , "Sport" , "Technology", "Social", "English", "Turkish", "Study"
                       1          2         3           4           5           6       7           8
   */

        if (genre == 1) { //Math
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_math_rookie);
            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_math_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_math_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_math_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_math_slayer);
            }

        } else if (genre == 2) { //Career
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_career_rookie);

            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_career_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_career_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_career_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_career_slayer);
            }

        } else if (genre == 3) { //Sport
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_sport_rookie);

            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_sport_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_sport_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_sport_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_sport_slayer);
            }

        } else if (genre == 4) { //Technology
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_tech_rookie);

            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_tech_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_tech_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_tech_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_tech_slayer);
            }

        } else if (genre == 5) { //Social
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_social_rookie);

            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_social_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_social_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_social_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_social_slayer);
            }

        } else if (genre == 6) { //English
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_english_rookie);

            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_english_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_english_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_english_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_english_slayer);
            }

        } else if (genre == 7) { //Turkish
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_turkish_rookie);

            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_turkish_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_turkish_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_turkish_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_turkish_slayer);
            }

        } else { //8. Study
            if (lvl == 1) {
                holder.avatarIv.setImageResource(R.drawable.medal_study_rookie);

            } else if (lvl == 2) {
                holder.avatarIv.setImageResource(R.drawable.medal_study_pro);

            } else if (lvl == 3) {
                holder.avatarIv.setImageResource(R.drawable.medal_study_titan);

            } else if (lvl == 4) {
                holder.avatarIv.setImageResource(R.drawable.medal_study_maestro);

            } else {
                // level 5
                holder.avatarIv.setImageResource(R.drawable.medal_study_slayer);
            }

        }

        //TODO Tıklandığında postu acıcak
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.show(); //TODO other profile buglu
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


