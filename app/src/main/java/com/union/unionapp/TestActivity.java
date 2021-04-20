package com.union.unionapp;


import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        /* calendar
        Calendar cal = Calendar.getInstance();
        cal.set(2021,0,13,10,30);
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rule", "FREQ=YEARLY");
        intent.putExtra("eventLocation", "https://zoom.us");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", "Yes Etkinliği");
        startActivity(intent);
         */
    }
}