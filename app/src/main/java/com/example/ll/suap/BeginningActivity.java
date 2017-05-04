package com.example.ll.suap;

import android.content.Intent;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BeginningActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    Button loginButton, createProfile;
    EditText email,password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginning);
        loginButton = (Button) findViewById(R.id.loginButton);
        createProfile = (Button) findViewById(R.id.button);
        email = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        loginButton.setOnClickListener(this);
        createProfile.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        //check if user already logged in and if so bypass and move to main menu
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainMenu.class));
        }

    }
    //method for user login
    private void userLogin() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(emailText)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(passwordText)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
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
                        if (task.isSuccessful()) {
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainMenu.class));
                        }
                        else
                        {
                            Toast.makeText(BeginningActivity.this, "User not registered", Toast.LENGTH_LONG).show();
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
                case R.id.button:
                    startActivity(new Intent(BeginningActivity.this, ProfileCreation.class));
                    break;
            }
        }
    }