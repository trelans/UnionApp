package com.union.unionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
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

    Button button;
    Button showSettingsButton;
    TextView selectedOptionTextView;
    Dialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        myDialog = new Dialog(this);





        /*
        //init UI view
        showSettingsButton = findViewById(R.id.showSettingsButton);
        selectedOptionTextView = findViewById(R.id.selectedOptionTextView);

        //pop up menu
        PopupMenu popupMenu = new PopupMenu(this,showSettingsButton);

        //add menu items in the popup menu
        popupMenu.getMenu().add(Menu.NONE,0,0,"Log Out"); // 2. parametre id oluyor. (bunda 0 aşağıda 1)
        popupMenu.getMenu().add(Menu.NONE,1,1,"Nothing"); // 3. parametre menu listesindeki pozisyonu.
        popupMenu.getMenu().add(Menu.NONE,2,2,"Merhaba");

        //handle menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //get id of the clicked item
                int id = item.getItemId();

                //handle clicks
                if ( id==0 ) { //log out secilmis
                    selectedOptionTextView.setText("Log out yapiliyor...");
                }
                else if ( id==1 ) {
                    selectedOptionTextView.setText("Nothing");
                }
                else if ( id==2 ) {
                    selectedOptionTextView.setText("Sana da merhaba");
                }

                return false;
            }
        });

        //handle button click, show popup menu
        showSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        /*
        //button = findViewById(R.id.button);
        //button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }


    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


}
*/
    }

    public void showPopup (View view) {

        myDialog.setContentView(R.layout.custom_popup);
        myDialog.show();
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