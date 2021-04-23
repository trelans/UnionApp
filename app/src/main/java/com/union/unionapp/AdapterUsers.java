package com.union.unionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{
    Context context;
    List<ModelUsers> userList;

    //constructor
    public  AdapterUsers(Context context , List<ModelUsers> userList) {

        this.context = context;
        this.userList = userList;

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout(row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent , false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            // get data
        String userPP = userList.get(position).getPp();
        String userName = userList.get(position).getUsername();
            // set data
        holder.username_TextView.setText(userName);
        try {
            Picasso.get().load(userPP).placeholder(R.drawable.profile_icon).into(holder.avatar_ImageView);

        }
        catch (Exception e) {

        }

        // handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Tıklandıı" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // view holder class
    class MyHolder extends RecyclerView.ViewHolder{
        ImageView avatar_ImageView;
        TextView username_TextView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            // init views
           avatar_ImageView = itemView.findViewById(R.id.userPPRow);
           username_TextView = itemView.findViewById(R.id.usernameRow);
        }
}


}
