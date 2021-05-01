package com.union.unionapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.List;

public class AdapterStackPosts extends RecyclerView.Adapter<AdapterStackPosts.MyHolder> {

    Context context;
    List<ModelStackPost> postList;
    DatabaseReference ref1;
    String[] allTags;
    Activity currentActivity;

    public AdapterStackPosts(Context context, List<ModelStackPost> postList, Activity currentActivity) {
        this.context = context;
        this.postList = postList;
        this.currentActivity = currentActivity;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout custom_stack_over_flow_feed_cards.xml
        View view = LayoutInflater.from(context).inflate(R.layout.custom_stack_over_flow_feed_cards, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getUEmail();
        String username = postList.get(position).getUsername();
        String pAnon = postList.get(position).getPAnon();
        String pId = postList.get(position).getPId();
        String pTitle = postList.get(position).getPTitle();
        String pDetails = postList.get(position).getPDetails();
        String pImage = postList.get(position).getPImage();
        String pTime = postList.get(position).getPTime();
        String pTag = postList.get(position).getpTagIndex();

        final String[] upVoteNumber = {postList.get(position).getPUpvoteNumber()};

        /*
        //Convert timestamp to dd//mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
         */

        //set data
        holder.contentTextView.setText(pDetails);
        holder.titleTextView.setText(pTitle);
        holder.upNumber.setText(upVoteNumber[0]);
        //holder.upNumber.setText("1");


        //allTags = getResources.getStringArray(R.array.all_tags); !!!!! getResources metodu fragment classı için var.
        allTags = MainActivity.getAllTags();
        System.out.println();

        if (Integer.valueOf(pTag) != 0) {
            holder.topicTag.setText(allTags[Integer.valueOf(pTag)]); //TODO
        }
        else {
            holder.topicTag.setText("No Tag");
        }
        //if there is no image
        if (pImage.equals("noImage")) {
            //hide imageView
        }

        /* aynısından image için de yaptı
        //set user dp
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.user_pp_template).into(holder.uPictureIv);
        }catch (Exception e){
            e.printStackTrace();
        }
         */
        ref1 = FirebaseDatabase.getInstance().getReference("BilkentUniversity/StackPosts/" + postList.get(position).pId);
        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> updateUpNumber = new HashMap<>();
                // Checking the background and do the increment or decrement accordingly
                //if (Objects.equals(holder.upButton.getBackground().getConstantState(), ContextCompat.getDrawable(context,R.drawable.up_icon).getConstantState())) {
                updateUpNumber.put("pUpvoteNumber", Integer.parseInt(postList.get(position).getPUpvoteNumber()) + 1 + "");
                //}
                ref1.updateChildren(updateUpNumber);
                holder.upNumber.setText(upVoteNumber[0]);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(currentActivity, PostActivity.class);
                intent.putExtra("pType", "Stack");
                intent.putExtra("pTime", pTime);
                intent.putExtra("pTitle", pTitle);
                intent.putExtra("pDetails", pDetails);
                intent.putExtra("username", username);
                intent.putExtra("pId", pId);

                // Dif. from buddy
                intent.putExtra("upVoteNumber", upVoteNumber[0]);
                intent.putExtra("pAnon", pAnon);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                return true;
            }

        });


    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from custom_stack_over_flow_feed_cards.xml
        ImageButton upButton;
        TextView contentTextView, titleTextView;
        TextView upNumber;
        TextView topicTag;
        CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            upButton = itemView.findViewById(R.id.upButtonImageView);
            contentTextView = itemView.findViewById(R.id.contentTW);
            titleTextView = itemView.findViewById(R.id.titleTW);
            upNumber = itemView.findViewById(R.id.textViewUpNumber);
            topicTag = itemView.findViewById(R.id.topicTagTW);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}
