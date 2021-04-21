package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.union.unionapp.R;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    TextView selectedOptionTextView;
    Dialog myDialog;
    ImageView popUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        mAuth = FirebaseAuth.getInstance();

        popUpButton = (ImageView) findViewById(R.id.showPopUpMenu);
        myDialog = new Dialog(this);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity =  Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                popUpButton.setImageResource(R.drawable.notif);
            }
        });


    }

    public void showPopup (View view) {
        Dialog dialog;
        myDialog.setContentView(R.layout.custom_popup);
        myDialog.show();


        popUpButton = (ImageView) findViewById(R.id.showPopUpMenu);
        popUpButton.setImageResource(R.drawable.notifo);
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









    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_message:
                            selectedFragment = new MessageFragment();
                            break;
                        case R.id.nav_buddy:
                            selectedFragment = new BuddyFragment();
                            break;
                        case R.id.nav_club:
                            selectedFragment = new ClubsFragment();
                            break;
                        case R.id.nav_stack:
                            selectedFragment = new StackFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };



}