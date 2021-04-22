package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    TextView selectedOptionTextView;
    Dialog myDialog;
    ImageView popUpButton;
    int currentActivity = 3;     // 1 Messages / 2 Buddy / 3 Club / 4 Stack / 5 Profile



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);



        mAuth = FirebaseAuth.getInstance();

        popUpButton = (ImageView) findViewById(R.id.showPopUpCreate);
        myDialog = new Dialog(this);

        //initial popup icon
        popUpButton.setBackground(null);
        popUpButton.setImageResource(R.drawable.notif);

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_club); // change to whichever id should be default
        }

        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (currentActivity == 5) {
                    popUpButton.setImageResource(R.drawable.settings_icon);
                }
                else {
                    popUpButton.setImageResource(R.drawable.notif);
                }
            }
        });


    }

    public void showPopup (View view) {
        Dialog dialog;
        if (currentActivity == 5) {
            myDialog.setContentView(R.layout.custom_settings);

            Button logout = myDialog.findViewById(R.id.logOutButton);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                    mAuth.signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            myDialog.setContentView(R.layout.custom_notification_popup);

        }

        myDialog.show();
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        popUpButton = (ImageView) findViewById(R.id.showPopUpCreate);

        if (currentActivity == 5)
            popUpButton.setImageResource(R.drawable.settings_icon_open);
        else
            popUpButton.setImageResource(R.drawable.notifo);



    }









    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_message:
                            selectedFragment = new MessageFragment();
                            currentActivity = 1;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_buddy:
                            selectedFragment = new BuddyFragment();
                            currentActivity = 2;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_club:
                            selectedFragment = new ClubsFragment();
                            currentActivity = 3;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_stack:
                            selectedFragment = new StackFragment();
                            currentActivity = 4;
                            popUpButton.setImageResource(R.drawable.notif);
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            currentActivity = 5;
                            popUpButton.setImageResource(R.drawable.settings_icon);
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    private void NotifOrSettingsDecider() {
        if (currentActivity == 5) {





        }
        else {
            myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    popUpButton.setImageResource(R.drawable.notif);
                }
            });
        }

    }



}