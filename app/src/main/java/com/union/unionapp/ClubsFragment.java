package com.union.unionapp;



import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ClubsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);


        Dialog myDialog;
        ImageView createPost = (ImageView) view.findViewById(R.id.showPopUpCreate);
        myDialog = new Dialog(getActivity());
        // Layoutu transparent yapıo
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                myDialog.setContentView(R.layout.custom_create_club_post);
                myDialog.show();
            }
        });



        return view;
    }
}