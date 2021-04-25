package com.union.unionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class CustomAdapterLastActsPU extends ArrayAdapter<String> {

    //Properties
    Context c;
    String[] lastActs;
    LayoutInflater inflater;

    //Constructors
    public CustomAdapterLastActsPU(@NonNull Context context, String[] achs) {
        super(context, R.layout.last_acts_list_layout_pu, achs);
        this.c = context;
        this.lastActs = achs;

    }

    //Methods
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if ( convertView == null ){
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.last_acts_list_layout_pu, null);
        }

        //Initializations
        TextView lastActsTexts = (TextView) convertView.findViewById(R.id.lastActsItemPU);

        //Set Text
        lastActsTexts.setText(lastActs[position]);
        //image varsa setImageResource();

        return convertView;

    }
}
