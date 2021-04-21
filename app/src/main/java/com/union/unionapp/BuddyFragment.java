package com.union.unionapp;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BuddyFragment extends Fragment {

    Dialog buddyDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context;
        buddyDialog = new Dialog(getActivity().getApplicationContext());
        return inflater.inflate(R.layout.fragment_buddy, container,false);
    }

    public void buddyCreatePost(View view){
        buddyDialog.setContentView(R.layout.custom_create_post);

    }
}