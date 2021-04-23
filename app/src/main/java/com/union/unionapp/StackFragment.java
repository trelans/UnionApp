package com.union.unionapp;



import android.app.Dialog;
import android.content.Context;
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

public class StackFragment extends Fragment {

    Dialog stackDialog;
    Spinner stackTagSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stack, container, false);



        ImageView createPost = (ImageView) view.findViewById(R.id.showPopUpCreate);
        stackDialog = new Dialog(getActivity());
        // Layoutu transparent yapıo
        stackDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stackDialog.setContentView(R.layout.custom_stack_createpost_popup);

                stackTagSpinner = stackDialog.findViewById(R.id.tagSpinner);
                ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.stack_tags, android.R.layout.simple_spinner_item);
                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stackTagSpinner.setAdapter(tagAdapter);

                stackDialog.show();
            }
        });



        return view;
    }
}