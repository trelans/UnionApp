package com.union.unionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.union.unionapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Oke Yunus");
    }
}