package com.example.ll.suap;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONObject;

public class ProfileCreation extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileCreationActivity:";

    EditText email, password;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        email = (EditText)findViewById(R.id.profilecreate_et_email);
        password = (EditText)findViewById(R.id.profilecreate_et_pwdl);
        register = (Button)findViewById(R.id.profilecreate_buttonRegister);

    }

    @Override
    protected void onResume() {
        super.onResume();
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        registerUser();
    }

    private void registerUser(){
        //getting email and password from edit texts
        String emailText = email.getText().toString().trim();
        String passwordText  = password.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(emailText)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(passwordText)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        Intent myIntent = new Intent(ProfileCreation.this,ProfileCreation2.class);
        myIntent.putExtra("registeremail", emailText);
        myIntent.putExtra("registerpassword", passwordText);
        finish();
        startActivity(myIntent);
    }
}