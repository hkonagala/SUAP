package com.example.ll.suap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class ProfileCreation2 extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    EditText name, phone, carmakemodel, caryear, carcolor;
    Spinner permitType;
    String emailText, passwordText;
    Button finish;
    ProgressDialog progressDialog;
    Handler handler = new Handler();
    ImageView mySelfie;

    private static final String TAG = "ProfileCreation";
    private DatabaseReference mydb;
    private DatabaseReference mydbchildusers;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String permitText;
    private String nameText;
    private String phoneText;
    private String makeModelText;
    private String yearText;
    private String colorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation2);

        final Intent myIntent = getIntent();
        emailText = myIntent.getStringExtra("registeremail");
        passwordText = myIntent.getStringExtra("registerpassword");
        name = (EditText)findViewById(R.id.profilecreate2_et_name);
        phone = (EditText)findViewById(R.id.profilecreate2_et_phone);
        carmakemodel = (EditText)findViewById(R.id.profilecreate2_et_model);
        caryear = (EditText)findViewById(R.id.profilecreate2_et_year);
        carcolor = (EditText)findViewById(R.id.profilecreate2_et_color);
        permitType = (Spinner)findViewById(R.id.profilecreate2_spinner_permit);
        finish = (Button)findViewById(R.id.profilecreate2_buttonRegister);

        finish.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        mydb = FirebaseDatabase.getInstance().getReference();
        mydbchildusers = mydb.child("users");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    long currentDate = Calendar.getInstance().getTimeInMillis();
                    UserInformation myUser = new UserInformation(mAuth.getCurrentUser().getUid(), emailText, nameText, phoneText, makeModelText
                            , yearText, colorText, permitText, currentDate, false, "", 0, 0);
                    mydbchildusers.child(mAuth.getCurrentUser().getUid()).setValue(myUser);
                    handler.post(new RunOnMainUI("Registration Successful"));
                    // add items to shared preferences
                    SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor =  userDetails.edit();
                    editor.putString("user.email", myUser.email);
                    editor.putString("user.name", myUser.name);
                    editor.putString("user.phone", myUser.phone);
                    editor.putString("user.makeModel", myUser.makeModel);
                    editor.putString("user.year", myUser.year);
                    editor.putString("user.color", myUser.color);
                    editor.putString("user.permit", myUser.permit);
                    editor.putLong("user.timestamp", myUser.timestamp);
                    editor.putBoolean("user.matched", myUser.matched);
                    editor.putString("user.match", myUser.match);
                    editor.putFloat("user.latitude", (float) myUser.latitude);
                    editor.putFloat("user.longitude", (float) myUser.longitude);
                    editor.apply();
                    finish();
                    startActivity(new Intent(ProfileCreation2.this, MainMenu.class));
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
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

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void getPictureAndSaveInfo(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mySelfie.setImageBitmap(imageBitmap);
        }
        saveUserInformation();
    }

    @Override
    public void onClick(View v){
        //here we'll ask for a photo
        getPictureAndSaveInfo();

        //saveUserInformation();
    }

    private void saveUserInformation() {
        //Getting values from database
        nameText = name.getText().toString().trim();
        phoneText = phone.getText().toString().trim();
        makeModelText = carmakemodel.getText().toString().trim();
        yearText = caryear.getText().toString().trim();
        colorText = carcolor.getText().toString().trim();
        permitText = permitType.getSelectedItem().toString().trim();
        //creating a userinformation object

        //creating a new user
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        //checking if success
                        if(task.isSuccessful() && mAuth.getCurrentUser() != null){
                            progressDialog.dismiss();
                        } else{
                            //display some message here
                            handler.post(new RunOnMainUI("Registration Error"));
                        }
                    }
                });
    }

    private class RunOnMainUI implements Runnable{

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
