package com.union.unionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Bu class userdaki achievemnts list view için oluşturulmuştur.
 * */
public class CustomAdapterAchievements extends ArrayAdapter<String> {

    //Properties
    Context c;
    String[] achs;
    LayoutInflater inflater;

    //Constructors
    public CustomAdapterAchievements(@NonNull Context context, String[] achs) {
        super(context, R.layout.achs_list_layout, achs);
        this.c = context;
        this.achs = achs;

    }

    //Methods
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if ( convertView == null ){
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.achs_list_layout, null);
        }

        //Initializations
        TextView achsTexts = (TextView) convertView.findViewById(R.id.achsItem);

        //Set Text
        achsTexts.setText(achs[position]);
        //image varsa setImageResource();

        return convertView;

    }
}
