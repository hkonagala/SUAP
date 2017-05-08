package com.example.ll.suap;

import android.content.Intent;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class BeginningActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "BeginningActivity:";


    Button loginButton, createProfile;
    EditText email,password;
    private DatabaseReference mydb;
    private DatabaseReference mydbchildusers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private String emailText;
    private String passwordText;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginning);
        loginButton = (Button) findViewById(R.id.loginButton);
        createProfile = (Button) findViewById(R.id.profileButton);
        email = (EditText) findViewById(R.id.et_name);
        password = (EditText) findViewById(R.id.et_pwd);



        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mydb = FirebaseDatabase.getInstance().getReference();
        mydbchildusers = mydb.child("users");

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = mAuth.getCurrentUser();
                //check if user already logged in and if so bypass and move to main menu
                if (user != null) {
                    String userId = user.getUid();
                    // Get details from database
                    mydbchildusers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserInformation dbUser = dataSnapshot.getValue(UserInformation.class);
                            // add items to shared preferences
                            if (dbUser != null) {
                                SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = userDetails.edit();
                                editor.putString("user.email", dbUser.email);
                                editor.putString("user.name", dbUser.name);
                                editor.putString("user.phone", dbUser.phone);
                                editor.putString("user.makeModel", dbUser.makeModel);
                                editor.putString("user.year", dbUser.year);
                                editor.putString("user.color", dbUser.color);
                                editor.putString("user.permit", dbUser.permit);
                                editor.putLong("user.timestamp", dbUser.timestamp);
                                editor.putBoolean("user.matched", dbUser.matched);
                                editor.putString("user.match", dbUser.match);
                                editor.putFloat("user.latitude", (float) dbUser.latitude);
                                editor.putFloat("user.longitude", (float) dbUser.longitude);
                                editor.apply();
                                finish();
                                startActivity(new Intent(BeginningActivity.this, MainMenu.class));
                            } else{
                                handler.post(new RunOnMainUI("Cannot retreive user details!"));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mAuth.getCurrentUser().getUid());
                }
                else
                {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginButton.setOnClickListener(this);
        createProfile.setOnClickListener(this);

        /*ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation tmp = dataSnapshot.getValue(UserInformation.class);
                if (tmp != null) {
                    Toast.makeText(getApplicationContext(), tmp.name, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
       mydbchildusers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(eventListener);
       */
    }

    //method for user login
    private void userLogin() {
        emailText = email.getText().toString().trim();
        passwordText = password.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(emailText)) {
            handler.post(new RunOnMainUI("Please enter email"));
            return;
        }

        if (TextUtils.isEmpty(passwordText)) {
            handler.post(new RunOnMainUI("Please enter password"));
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Logging In Please Wait...");
        progressDialog.show();

        //logging in the user
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();
                        //if the task is successful
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            handler.post(new RunOnMainUI("User not registered"));
                        }
                    }
                });
    }
        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.loginButton:
                    userLogin();
                    break;
                case R.id.profileButton:
                    finish();
                    startActivity(new Intent(BeginningActivity.this, ProfileCreation.class));
                    break;
            }
        }

    public class RunOnMainUI implements Runnable{

        String message;

        public RunOnMainUI(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
    }