package com.example.ll.suap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileCreation extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    EditText email, password, name, phone, carmakemodel, caryear, carcolor;
    Spinner permitType;
    Button register;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        email = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        name = (EditText)findViewById(R.id.editText3);
        phone = (EditText)findViewById(R.id.editText4);
        carmakemodel = (EditText)findViewById(R.id.editText5);
        caryear = (EditText)findViewById(R.id.editText6);
        carcolor = (EditText)findViewById(R.id.editText7);
        permitType = (Spinner)findViewById(R.id.spinner);
        register = (Button)findViewById(R.id.button);

        register.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void onClick(View v){
        registerUser();
        saveUserInformation();
        finish();
        startActivity(new Intent(ProfileCreation.this,BeginningActivity.class));
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

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(!task.isSuccessful()){
                            //display some message here
                            Toast.makeText(ProfileCreation.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
    private void saveUserInformation() {
        //Getting values from database
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String nameText = name.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String makeModelText = carmakemodel.getText().toString().trim();
        String yearText = caryear.getText().toString().trim();
        String colorText = carcolor.getText().toString().trim();
        String permitText = permitType.getSelectedItem().toString().trim();

        //creating a userinformation object
        UserInformation userInformation = new UserInformation(emailText,passwordText,nameText,phoneText,
                makeModelText,yearText,colorText,permitText);

        //getting the current logged in user
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        //adding information to database
        databaseReference.child(uid).setValue(userInformation);

        //displaying a success toast
        Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show();
    }
}
