package com.union.unionapp;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StackFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stack, container, false);


        Dialog myDialog;
        ImageView createPost = (ImageView) view.findViewById(R.id.showPopUpCreate);
        myDialog = new Dialog(getActivity());



        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                myDialog.setContentView(R.layout.custom_stack_createpost_popup);
                myDialog.show();
            }
        });



        return view;
    }
}