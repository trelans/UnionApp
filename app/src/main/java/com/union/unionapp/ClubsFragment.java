package com.union.unionapp;



import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ClubsFragment extends Fragment {

    Dialog clubDialog;
    Spinner clubTagSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);


        ImageView filterImageView = (ImageView) view.findViewById(R.id.showClubFilterPopup);
        ImageView createPost = (ImageView) view.findViewById(R.id.showPopUpCreate);
        clubDialog = new Dialog(getActivity());
        // Layoutu transparent yapıo
        clubDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                clubDialog.setContentView(R.layout.custom_create_club_post);

                clubTagSpinner = clubDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.stack_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                clubTagSpinner.setAdapter(tagAdapter);

                clubDialog.show();
            }
        });

        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clubDialog.setContentView(R.layout.custom_club_filter);

                clubTagSpinner = clubDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.buddy_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                clubTagSpinner.setAdapter(tagAdapter);

                clubDialog.show();
            }
        });


        return view;
    }
}