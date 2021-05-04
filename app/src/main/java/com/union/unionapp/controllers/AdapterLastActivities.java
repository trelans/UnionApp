package com.union.unionapp.controllers;

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
import com.union.unionapp.R;
import com.union.unionapp.models.ModelLastActivities;
import com.union.unionapp.views.PostActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * This class binds given last activities into the view
 *
 * @author unionTeam
 * @version 04.05.2021
 */
public class AdapterLastActivities extends RecyclerView.Adapter<AdapterLastActivities.HolderNotification> {

    private Context context;
    private ArrayList<ModelLastActivities> notificationsList;

    public AdapterLastActivities( Context context, ArrayList<ModelLastActivities> notificationsList ) {
        this.context = context;
        this.notificationsList = notificationsList;
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        // inflate view row_notification
        View view = LayoutInflater.from( context ).inflate( R.layout.row_lastachi, parent, false );
        return new HolderNotification( view );
    }

    @Override
    public void onBindViewHolder( @NonNull HolderNotification holder, int position ) {
        // get and set data to views

        // get data
        final ModelLastActivities modelNotification = notificationsList.get( position );
        String name = modelNotification.getsName();
        String notification = modelNotification.getNotification();
        String timestamp = modelNotification.getTimestamp();
        String senderUid = modelNotification.getsUid();
        String pId = modelNotification.getpId();
        String type = modelNotification.getType();
        final String postType;
        // conver timestamp to dd//mm/yyyy hh:mm

        Calendar cal = Calendar.getInstance( Locale.ENGLISH );
        cal.setTimeInMillis( Long.parseLong( timestamp ) );
        String dateTime = DateFormat.format( "dd/MM/yyyy hh:mm aa", cal ).toString();


        // set to views
        holder.AchnotificationTv.setText( notification );
        holder.AchtimeTv.setText( dateTime );

        if ( type.equals( "1" ) ) {
            holder.avatarIv.setImageResource( R.drawable.buddy_icon );
            postType = "Buddy";
        } else if ( type.equals( "2" ) ) {
            holder.avatarIv.setImageResource( R.drawable.club_icon );
            postType = "Club";
        } else if ( type.equals( "3" ) ) {
            holder.avatarIv.setImageResource( R.drawable.stack_icon );
            postType = "Stack";
        } else {
            holder.avatarIv.setImageResource( R.drawable.stack_icon ); // öylesine çökmesin diye
            postType = "";
        }

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent i = new Intent( context, PostActivity.class );
                i.putExtra( "pType", postType );
                i.putExtra( "source", "outside" );
                i.putExtra( "pId", pId );
                i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                context.startActivity( i );

            }
        } );

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    // holder class for views of row_notifications.xlm
    class HolderNotification extends RecyclerView.ViewHolder {

        // declare views
        ImageView avatarIv;
        TextView AchnotificationTv, AchtimeTv;

        public HolderNotification( @NonNull View itemView ) {
            super( itemView );

            //init views
            avatarIv = itemView.findViewById( R.id.AchavatarIv );
            AchnotificationTv = itemView.findViewById( R.id.AchnotificationTv );
            AchtimeTv = itemView.findViewById( R.id.AchtimeTv );

        }
    }
}


