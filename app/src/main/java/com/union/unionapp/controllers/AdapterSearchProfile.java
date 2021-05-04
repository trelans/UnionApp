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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.union.unionapp.R;
import com.union.unionapp.models.ModelUsers;
import com.union.unionapp.views.OtherProfile;

import java.util.List;

public class AdapterSearchProfile extends RecyclerView.Adapter<AdapterSearchProfile.MyHolder> {
    Context context;
    List<ModelUsers> userList;


    //constructor
    public AdapterSearchProfile( Context context, List<ModelUsers> userList ) {

        this.context = context;
        this.userList = userList;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        // inflate layout(row_user.xml)
        View view = LayoutInflater.from( context ).inflate( R.layout.row_users, parent, false );
        return new MyHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull MyHolder holder, int position ) {
        // get data
        String hisUID = userList.get( position ).getUid();
        String userPP = userList.get( position ).getPp();
        String userName = userList.get( position ).getUsername();
        // set data
        holder.username_TextView.setText( userName );

        holder.avatar_ImageView.setBackground( ContextCompat.getDrawable( context, R.drawable.profile_icon ) );
        try {
            //if image received, set
            StorageReference image = FirebaseStorage.getInstance().getReference( "BilkentUniversity/pp/" + hisUID );
            image.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess( Uri uri ) {
                    Picasso.get().load( uri ).into( holder.avatar_ImageView );
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure( @NonNull Exception e ) {
                    holder.avatar_ImageView.setBackground( ContextCompat.getDrawable( context, R.drawable.profile_icon ) );
                }
            } );
        } catch ( Exception e ) {
            //if there is any exception while getting image then set default
            holder.avatar_ImageView.setBackground( ContextCompat.getDrawable( context, R.drawable.profile_icon ) );
        }

        // handle item click
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                Intent i = new Intent( context, OtherProfile.class );
                i.putExtra( "Hisuid", hisUID );
                i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                context.startActivity( i );

            }
        } );
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // view holder class
    class MyHolder extends RecyclerView.ViewHolder {
        ImageView avatar_ImageView;
        TextView username_TextView;
        ImageView profileIv;

        public MyHolder( @NonNull View itemView ) {
            super( itemView );
            // init views
            avatar_ImageView = itemView.findViewById( R.id.userPPRow );
            username_TextView = itemView.findViewById( R.id.usernameRow );
        }
    }

}
