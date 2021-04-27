package com.union.unionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class CustomAdapterAchsPU extends ArrayAdapter<String> {

    //Properties
    Context c;
    String[] achs;
    LayoutInflater inflater;

    //Constructors
    public CustomAdapterAchsPU(@NonNull Context context, String[] achs) {
        super(context, R.layout.achs_list_layout_pu, achs);
        this.c = context;
        this.achs = achs;

    }

    //Methods
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if ( convertView == null ){
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.achs_list_layout_pu, null);
        }

        //Initializations
        TextView achsTexts = (TextView) convertView.findViewById(R.id.achsItemPU);

        //Set Text
        achsTexts.setText(achs[position]);
        //image varsa setImageResource();

        return convertView;

    }
}
