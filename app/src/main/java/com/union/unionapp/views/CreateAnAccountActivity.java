package com.union.unionapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.union.unionapp.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAnAccountActivity extends AppCompatActivity {
    // Variables
    final String regexStr = "[a-zA-ZığüşöçİĞÜŞÖÇ ]+$";
    TextView tw_name;
    TextView tw_email;
    TextView tw_surname;
    TextView tw_password;
    TextView tw_password_Auth;
    TextView tw_terms;
    CheckBox cb_aggrement;
    ImageView tick1;
    ImageView tick2;
    ImageView tick3;
    ImageView tick4;
    ImageView tick5;
    private String token;
    String email;
    String name;
    String surname;
    String password;
    ProgressDialog progressDialog;
    boolean isPasswordNotValid;
    boolean isThereError = false; // int yap

    ProgressBar pb_waiting;
    Button bt_signUp;
    Drawable tickIcon;
    private FirebaseAuth mAuth;
    private SlidrInterface slidr;

    // yükleme metodu yazınca progress barı üste al, arkayı blur yap .....


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_create_an_account );
        slidr = Slidr.attach( this );


        mAuth = FirebaseAuth.getInstance();
        if ( mAuth.getCurrentUser() != null ) {
            startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
            finish();
        }

        final Activity activity = this;

        // card denemesi : fail daskjddfdsf
        // RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        // CardView cardView = findViewById(R.id.cardView);
        //cardView.setLayoutParams(new RelativeLayout.LayoutParams(20, 20));


        progressDialog = new ProgressDialog( CreateAnAccountActivity.this );
        progressDialog.setTitle( "Registering User..." );

        tw_name = findViewById( R.id.nameTextView );
        tw_surname = findViewById( R.id.surnameTextView );
        tw_email = findViewById( R.id.emailTextView );
        tw_password = findViewById( R.id.passwordTextView );
        tw_password_Auth = findViewById( R.id.passwordAuthTextView );
        tw_terms = findViewById( R.id.termsTextView );
        cb_aggrement = findViewById( R.id.rememberMeCheckBox );
        pb_waiting = findViewById( R.id.waitingProgressBar );
        bt_signUp = findViewById( R.id.VerifyButton );
        tick1 = findViewById( R.id.thickView1 );
        tick2 = findViewById( R.id.thickView2 );
        tick3 = findViewById( R.id.thickView3 );
        tick4 = findViewById( R.id.thickView4 );
        tick5 = findViewById( R.id.thickView5 );
        tickIcon = getResources().getDrawable( R.drawable.ic_action_name );
        tickIcon.setBounds( 0, 0, tickIcon.getIntrinsicWidth(), tickIcon.getIntrinsicHeight() );
        Pattern pattern = Pattern.compile( regexStr );

        // klavyeyi dışarı tıklayınca kapatmaya yarıyor
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


        // Buttonu disable yapma (until checkbox işaretlenince diğer şeylerde hata yoksa)
        bt_signUp.setEnabled( false );

        tw_password_Auth.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View view, boolean b ) {
                tw_password_Auth.setBackgroundResource( R.drawable.edittext_begining_border_template );
                tw_password_Auth.setHint( "Password" );
                if ( !tw_password_Auth.hasFocus() && tw_password_Auth.getText().toString().length() != 0 ) {
                    if ( password.equals( tw_password_Auth.getText().toString().trim() ) && !isPasswordNotValid ) {
                        tw_password.setBackgroundResource( R.drawable.edittext_correct_border_template );
                        tw_password.setHint( "Password" );
                        //tick5.setVisibility(View.VISIBLE);
                        isThereError = false;
                        bt_signUp.setEnabled( true );
                        return;
                    } else {
                        if ( !password.equals( tw_password_Auth.getText().toString().trim() ) ) {
                            //tw_password_Auth.setError("Passwords don't match");
                            tw_password_Auth.setBackgroundResource( R.drawable.edittext_error_border_template );
                            tw_password_Auth.setHint( "Passwords don't match" );
                            //tick5.setVisibility(View.INVISIBLE);
                            isThereError = true;
                            bt_signUp.setEnabled( false );
                            return;
                        } else {
                            //tw_password_Auth.setError("Password length must be at least 6 character");
                            tw_password_Auth.setBackgroundResource( R.drawable.edittext_error_border_template );
                            tw_password_Auth.setHint( "Password length must be at least 6 character" );
                            //tick5.setVisibility(View.INVISIBLE);
                            isThereError = true;
                            return;
                        }


                    }
                }
            }
        } );

        tw_password.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View view, boolean b ) {
                tw_password.setBackgroundResource( R.drawable.edittext_begining_border_template );
                tw_password.setHint( "Password" );
                password = tw_password.getText().toString().trim();
                if ( !tw_password.hasFocus() && tw_password.getText().toString().length() != 0 ) {
                    if ( password.length() < 6 ) {
                        //tw_password.setError("Password length must be at least 6 character");
                        tw_password.setBackgroundResource( R.drawable.edittext_error_border_template );
                        tw_password.setHint( "Password length must be at least 6 character" );
                        //tick4.setVisibility(View.INVISIBLE);
                        isPasswordNotValid = true;
                        isThereError = true;
                        return;
                    } else {
                        tw_password.setBackgroundResource( R.drawable.edittext_correct_border_template );
                        tw_password.setHint( "Password" );
                        //tick4.setVisibility(View.VISIBLE);
                        isPasswordNotValid = false;
                        isThereError = false;
                        bt_signUp.setEnabled( true );
                        return;
                    }
                }
            }
        } );

        tw_name.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View view, boolean b ) {
                tw_name.setBackgroundResource( R.drawable.edittext_begining_border_template );
                tw_name.setHint( "Name" );
                name = tw_name.getText().toString().trim();
                if ( !tw_name.hasFocus() && name.length() != 0 ) {

                    Matcher matcher = pattern.matcher( name );
                    if ( matcher.find() ) {
                        tw_name.setBackgroundResource( R.drawable.edittext_correct_border_template );
                        //tick1.setVisibility(View.VISIBLE);
                        isThereError = false;
                        bt_signUp.setEnabled( true );
                    } else {
                        tw_name.setBackgroundResource( R.drawable.edittext_error_border_template );
                        tw_name.setHint( "Invalid characters!" );
                        //tick1.setVisibility(View.INVISIBLE);
                        //tw_name.setError("Invalid characters!");
                        isThereError = true;
                    }
                }
            }
        } );
        tw_email.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View view, boolean b ) {
                tw_email.setBackgroundResource( R.drawable.edittext_begining_border_template );
                tw_email.setHint( "Email" );
                email = tw_email.getText().toString().trim();
                if ( !tw_email.hasFocus() && email.length() != 0 ) {
                    if ( !email.contains( "ug.bilkent.edu.tr" ) ) {
                        //tw_email.setError("Your university hasn't registered yet");
                        tw_email.setBackgroundResource( R.drawable.edittext_error_border_template );
                        tw_email.setHint( "Your university hasn't registered yet" );
                        //tick3.setVisibility(View.INVISIBLE);
                        isThereError = true;
                        return;
                    } else {
                        tw_email.setBackgroundResource( R.drawable.edittext_correct_border_template );
                        tw_email.setHint( "Email" );
                        //tick3.setVisibility(View.VISIBLE);
                        isThereError = false;
                        bt_signUp.setEnabled( true );
                        return;
                    }
                }
            }
        } );

        tw_surname.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View view, boolean b ) {
                tw_surname.setBackgroundResource( R.drawable.edittext_begining_border_template );
                tw_surname.setHint( "Surname" );
                surname = tw_surname.getText().toString().trim();
                if ( !tw_surname.hasFocus() && surname.length() != 0 ) {
                    Matcher matcher = pattern.matcher( surname );
                    if ( matcher.find() ) {
                        tw_surname.setBackgroundResource( R.drawable.edittext_correct_border_template );
                        tw_surname.setHint( "Surname" );
                        //tick2.setVisibility(View.VISIBLE);
                        isThereError = false;
                        bt_signUp.setEnabled( true );
                        return;
                    } else {
                        tw_surname.setBackgroundResource( R.drawable.edittext_error_border_template );
                        tw_surname.setHint( "Invalid characters!" );
                        //tick2.setVisibility(View.INVISIBLE);
                        //tw_surname.setError("Invalid characters!");
                        isThereError = true;
                        return;
                    }
                }
            }
        } );

        cb_aggrement.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                if ( cb_aggrement.isChecked() ) {
                    bt_signUp.setEnabled( true );
                } else {
                    bt_signUp.setEnabled( false );
                }
            }
        } );

        bt_signUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {


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
                if ( TextUtils.isEmpty( tw_password_Auth.getText().toString() ) ) {
                    //tw_password_Auth.setError("Password is required");
                    tw_password_Auth.setBackgroundResource( R.drawable.edittext_error_border_template );
                    tw_password_Auth.setHint( "Password is required" );
                    isThereError = true;
                }
                if ( TextUtils.isEmpty( name ) ) {
                    //tw_name.setError("Name is required");
                    tw_name.setBackgroundResource( R.drawable.edittext_error_border_template );
                    tw_name.setHint( "Name is required" );
                    isThereError = true;
                }
                if ( TextUtils.isEmpty( surname ) ) {
                    //tw_surname.setError("Surname is required");
                    tw_surname.setBackgroundResource( R.drawable.edittext_error_border_template );
                    tw_surname.setHint( "Surname is required" );
                    isThereError = true;
                }
                if ( !cb_aggrement.isChecked() ) {
                    //tw_terms.setError("You must agree the terms first");
                    tw_terms.setBackgroundResource( R.drawable.edittext_error_border_template );
                    tw_terms.setHint( "You must agree the terms first" );
                    isThereError = true;
                }


                if ( !isThereError ) {
                    token = computeMD5Hash( password );
                    mAuth.createUserWithEmailAndPassword( email, token ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( @NonNull Task<AuthResult> task ) {
                            if ( task.isSuccessful() ) {
                                progressDialog.dismiss();

                                FirebaseUser user = mAuth.getCurrentUser();
                                //Get user email and uid from auth.
                                String email = user.getEmail();
                                String uid = user.getUid();
                                assert email != null;
                                String username = createUsername( email );

                                // when user is registered store user info in firebase realtime database too
                                // using hashmap
                                HashMap<String, String> hashMap = new HashMap<>();
                                // put info in hashmap
                                hashMap.put( "email", email );
                                hashMap.put( "uid", uid );

                                // will add later!
                                hashMap.put( "username", username );
                                hashMap.put( "pp", "drawable-v24/profile_icon.png" );
                                hashMap.put( "accountType", "-1" ); // -1 unauthenticated user, 0 regular user, 1 club
                                hashMap.put( "tags", "1,2,3" );
                                hashMap.put( "accountState", "0" ); // 0 active, 1 banned, 2 frozen, 3 deleted
                                hashMap.put( "achievements", "1" );

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //path to store user data
                                DatabaseReference reference = database.getReference( "BilkentUniversity/Users/" + uid );
                                // put data within hashmap in database
                                reference.setValue( hashMap );

                                //put token
                                reference = database.getReference( "AuthTokens/" + email.replace( ".", "_" ) );
                                hashMap.clear();
                                hashMap.put( "token", token );
                                reference.setValue( hashMap );
                                startActivity( new Intent( CreateAnAccountActivity.this, VerifyAccountActivity.class ) );
                                finish();
                            } else {
                                Toast.makeText( getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT ).show();
                            }


                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure( @NonNull Exception e ) {
                            progressDialog.dismiss();
                        }
                    } );
                }
            }
        } );


    }

    private String createUsername( String email ) {
        String[] pieces = email.split( "@" );
        return pieces[0].replace( ".", "_" );
    }


    public void login( View view ) {
        Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
        startActivity( intent );
        finish();
    }

    public void openLoginActivity( View view ) {
        Intent intent = new Intent( this, LoginActivity.class );
        startActivity( intent );
    }

    public void previewTerms( View view ) {

    }

    public static String computeMD5Hash( String password ) { //TODO loginle ilgili olan her şeyi bir package e koyup bunu da default seviyesinde accessor yap
        try {
            // Create MD5 hash
            MessageDigest digest = java.security.MessageDigest.getInstance( "MD5" );
            digest.update( password.getBytes() );
            byte[] messageDigest = digest.digest();
            StringBuilder MD5Hash = new StringBuilder();
            for ( int i = 0; i < messageDigest.length; i++ ) {
                String h = Integer.toHexString( 0xFF & messageDigest[i] );
                while ( h.length() < 2 ) {
                    h = "0" + h;
                }
                MD5Hash.append( h );
            }
            return MD5Hash.toString();
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
            return password;
        }
    }


}