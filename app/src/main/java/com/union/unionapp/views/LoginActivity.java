package com.union.unionapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.union.unionapp.controllers.KeyboardUtils;
import com.union.unionapp.controllers.LocalDataManager;
import com.union.unionapp.R;


/**
 * This activity starting point of the app and enable user to login their account
 *
 * @author unionTeam
 * @version 04.05.2021
 */
public class LoginActivity extends AppCompatActivity {


    // Variables
    private TextView tw_email;
    private TextView tw_password;
    private TextView tw_forgot_password;
    private TextView tw_error;
    private CheckBox check_box_remember_me;
    private Button button_login;
    private ProgressBar pb_waiting;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private LocalDataManager localDataManager; // for sharedPreferences
    private boolean isThereError = false;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        localDataManager = new LocalDataManager();

        tw_email = findViewById( R.id.nameTextView );
        tw_password = findViewById( R.id.passwordTextView );
        tw_forgot_password = findViewById( R.id.loginTextView );
        tw_error = findViewById( R.id.errorTextView );
        tw_error.setVisibility( View.INVISIBLE );

        /*
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams);
        cardView.getLayoutParams();
        layoutParams.height = 10;
         */

        check_box_remember_me = findViewById( R.id.rememberMeCheckBox );
        button_login = findViewById( R.id.VerifyButton );
        pb_waiting = findViewById( R.id.waitingProgressBar );

        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and if there is a user go to main activity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if ( currentUser != null ) {
            Intent intent = new Intent( getApplicationContext(), MainActivity.class );
            startActivity( intent );
        }


        tw_email.setText( localDataManager.getSharedPreference( LoginActivity.this, "email", "" ) );
        tw_password.setText( localDataManager.getSharedPreference( LoginActivity.this, "password", "" ) );
        check_box_remember_me.setChecked( true );

        // Is keyboard on or off
        KeyboardUtils.addKeyboardToggleListener( this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard( boolean isVisible ) {
                if ( !isVisible && !isThereError ) {
                    button_login.setEnabled( true );
                }
            }

        } );

        // Close keyboard clicking outside
        findViewById( R.id.slideButtonForward ).setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch( View view, MotionEvent motionEvent ) {
                if ( getCurrentFocus() != null ) {
                    InputMethodManager imm = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
                    imm.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0 );
                    return true;
                }
                return false;
            }
        } );


        // Focus Listeners
        tw_email.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View view, boolean b ) {
                tw_email.setBackgroundResource( R.drawable.edittext_begining_border_template );
                tw_email.setHint( "Email" );
                email = tw_email.getText().toString().trim();
                // if something wrong with email
                if ( !tw_email.hasFocus() && email.length() != 0 ) {
                    if ( !email.contains( "ug.bilkent.edu.tr" ) ) {
                        isThereError = true;
                        button_login.setEnabled( false );
                        //tw_email.setError("Your university hasn't registered yet");
                        tw_email.setBackgroundResource( R.drawable.edittext_error_border_template );
                        tw_email.setHint( "Your university hasn't registered yet" );
                        //tick1.setVisibility(View.INVISIBLE);
                        return;
                    } else {
                        //tick1.setVisibility(View.VISIBLE);
                        tw_email.setBackgroundResource( R.drawable.edittext_correct_border_template );
                        tw_email.setHint( "Email" );
                        isThereError = false;
                        button_login.setEnabled( true );
                        return;
                    }
                }
            }
        } );

        tw_password.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View view, boolean b ) {
                tw_password.setBackgroundResource( R.drawable.edittext_begining_border_template );
                tw_email.setHint( "Password" );
                password = tw_password.getText().toString().trim();
                // if something wrong with password
                if ( !tw_password.hasFocus() && tw_password.getText().toString().length() != 0 ) {
                    if ( password.length() < 6 ) {
                        tw_password.setBackgroundResource( R.drawable.edittext_error_border_template );
                        tw_password.setHint( "Password length must be at least 6 character" );
                        isThereError = true;
                        button_login.setEnabled( false );
                        //tw_password.setError("Password length must be at least 6 character");
                        //tick2.setVisibility(View.INVISIBLE);
                        return;
                    } else {
                        //tick2.setVisibility(View.VISIBLE);
                        tw_password.setBackgroundResource( R.drawable.edittext_correct_border_template );
                        tw_password.setHint( "Password" );
                        isThereError = false;
                        button_login.setEnabled( true );
                        return;
                    }
                }
            }
        } );


        // click listeners
        tw_forgot_password.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                startActivity( new Intent( getApplicationContext(), RecoverAccountActivity.class ) );

            }
        } );


        button_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {


                email = tw_email.getText().toString().trim();
                password = tw_password.getText().toString().trim();


                if ( TextUtils.isEmpty( email ) ) {
                    //tw_email.setError("Email is required");
                    tw_email.setBackgroundResource( R.drawable.edittext_error_border_template );
                    tw_email.setHint( "Email is required" );
                    isThereError = true;
                }
                if ( TextUtils.isEmpty( password ) ) {
                    //tw_password.setError("Password is required");
                    tw_password.setBackgroundResource( R.drawable.edittext_error_border_template );
                    tw_password.setHint( "Password is required" );
                    isThereError = true;
                }

                if ( !isThereError ) {
                    pb_waiting.setVisibility( View.VISIBLE );

                    if ( check_box_remember_me.isChecked() ) {
                        localDataManager.setSharedPreference( LoginActivity.this, "email", email );
                        localDataManager.setSharedPreference( LoginActivity.this, "password", password );
                    } else {
                        localDataManager.removeSharedPreference( LoginActivity.this, "email" );
                        localDataManager.removeSharedPreference( LoginActivity.this, "password" );
                    }


                    // authenticate user
                    mAuth.signInWithEmailAndPassword( email, CreateAnAccountActivity.computeMD5Hash( password ) ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( @NonNull Task<AuthResult> task ) {
                            if ( task.isSuccessful() ) {
                                if ( localDataManager.getSharedPreference( LoginActivity.this, "isAccountVerified", "" ).equals( email ) ) {
                                    Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                                    startActivity( intent );
                                    finish();
                                    return;
                                }
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "BilkentUniversity/Users/" + mAuth.getCurrentUser().getUid() );
                                reference.addValueEventListener( new ValueEventListener() {
                                    @Override
                                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
                                        for ( DataSnapshot ds : snapshot.getChildren()
                                        ) {
                                            if ( ds.getKey().equals( "accountType" ) ) {
                                                if ( ds.getValue().equals( "-1" ) ) {
                                                    startActivity( new Intent( LoginActivity.this, VerifyAccountActivity.class ) );
                                                } else {
                                                    startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                                                }
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled( @NonNull DatabaseError error ) {

                                    }
                                } );
                            } else {
                                tw_error.setText( task.getException().getMessage() );
                                tw_error.setVisibility( View.VISIBLE );
                            }
                        }
                    } );
                }
            }
        } );

        //setContentView( R.layout.activity_main );

    }

    public void openCreateAnAccountActivity( View view ) {
        Intent intent = new Intent( this, CreateAnAccountActivity.class );
        startActivity( intent );
    }
}