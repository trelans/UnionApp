package com.union.unionapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.union.unionapp.R;
import com.union.unionapp.models.ModelUsers;
import com.union.unionapp.views.ChatActivity;

import java.util.HashMap;
import java.util.List;

/**
 * This class binds given chatlist into the view
 *
 * @author unionTeam
 * @version 04.05.2021
 */
public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder> {


    private Context context;
    private List<ModelUsers> userList; // get user info
    private HashMap<String, String> lastMessageMap;

    // constructor
    public AdapterChatlist( Context context, List<ModelUsers> userList ) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        // inflate layout row_chatlist.xml
        View view = LayoutInflater.from( context ).inflate( R.layout.row_chatlist, parent, false );
        return new MyHolder( view );

    }

    @Override
    public void onBindViewHolder( @NonNull MyHolder holder, int position ) {

        // get data
        String hisUid = userList.get( position ).getUid();
        String userImage = userList.get( position ).getPp();
        String userName = "@" + userList.get( position ).getUsername();
        String lastMessage = lastMessageMap.get( hisUid );

        // set data
        holder.usernameTv.setText( userName );
        if ( lastMessage == null || lastMessage.equals( "default" ) ) {
            holder.lastmessageTv.setVisibility( View.GONE );
        } else {
            holder.lastmessageTv.setVisibility( View.VISIBLE );
            holder.lastmessageTv.setText( lastMessage );
        }
        try {
            //if image received, set
            StorageReference image = FirebaseStorage.getInstance().getReference( "BilkentUniversity/pp/" + userImage );
            image.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess( Uri uri ) {
                    Picasso.get().load( uri ).into( holder.profileIv );
                }
            } );
        } catch ( Exception e ) {
            //if there is any exception while getting image then set default
            Picasso.get().load( R.drawable.user_pp_template ).into( holder.profileIv );
        }

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                // start chat activity
                Intent intent = new Intent( context, ChatActivity.class );
                intent.putExtra( "Hisuid", hisUid );
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );

                context.startActivity( intent );


            }
        } );
    }

    public void setLastMessageMap( String userId, String lastMessage ) {
        lastMessageMap.put( userId, lastMessage );
    }

    @Override
    public int getItemCount() {

        return userList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        // view row_chatlist
        ImageView profileIv;
        TextView usernameTv, lastmessageTv;

        public MyHolder( @NonNull View itemView ) {
            super( itemView );

            profileIv = itemView.findViewById( R.id.profile_Iv );
            usernameTv = itemView.findViewById( R.id.nameTv );
            lastmessageTv = itemView.findViewById( R.id.lastMessageTv );
        }
    }
}
