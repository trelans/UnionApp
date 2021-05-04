package com.union.unionapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.union.unionapp.R;
import com.union.unionapp.models.ModelUsers;
import com.union.unionapp.views.ChatActivity;

import java.util.List;

/**
 * This class binds given users into the view
 *
 * @author unionTeam
 * @version 04.05.2021
 */
public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {
    private Context context;
    private List<ModelUsers> userList;


    //constructor
    public AdapterUsers( Context context, List<ModelUsers> userList ) {

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


        // handle item click
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                Intent i = new Intent( context, ChatActivity.class );
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

        public MyHolder( @NonNull View itemView ) {
            super( itemView );
            // init views
            avatar_ImageView = itemView.findViewById( R.id.userPPRow );
            username_TextView = itemView.findViewById( R.id.usernameRow );
        }
    }

}
