package com.example.ll.suap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileCreation extends AppCompatActivity implements View.OnClickListener{

    EditText permit, license, car, email, username, password, passwordverify;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        permit = (EditText)findViewById(R.id.editText);
        license = (EditText)findViewById(R.id.editText2);
        car = (EditText)findViewById(R.id.editText3);
        email = (EditText)findViewById(R.id.editText4);
        username = (EditText)findViewById(R.id.editText5);
        password = (EditText)findViewById(R.id.editText6);
        passwordverify = (EditText)findViewById(R.id.editText7);
        submit = (Button)findViewById(R.id.button);
    }

    @Override
    public void onClick(View v){
        String emailText = email.getText().toString();
        String passText = password.getText().toString();
        String passVerifText = passwordverify.getText().toString();
        if(!passText.equals(passVerifText)) {
            Toast.makeText(getApplicationContext(), "Non-matching Password", Toast.LENGTH_LONG).show();
        }
        else if(!emailText.substring(emailText.length()-9).equals("@umbc.edu")){
            Toast.makeText(getApplicationContext(), "Invalid UMBC Email", Toast.LENGTH_LONG).show();
        }
        else{
            //get remainder of information and add info to database
            String permitText = permit.getText().toString();
            String licenseText = license.getText().toString();
            String carText = car.getText().toString();
            String userText = username.getText().toString();
            //add info to database - to be implemented
            //use email to send verification - to be implemented
        }
        startActivity(new Intent(this, VerificationPage.class));
        //implement other verification steps such as verifying ID, car type verification?
        //perhaps make Car ID a dropdown window instead of an editable field?
    }
}
