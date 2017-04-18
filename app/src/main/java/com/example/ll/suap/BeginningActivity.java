package com.example.ll.suap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BeginningActivity extends AppCompatActivity implements View.OnClickListener{

    EditText username, password;
    Button loginButton, createProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginning);
        username = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        loginButton = (Button)findViewById(R.id.loginButton);
        createProfile = (Button)findViewById(R.id.button2);
        loginButton.setOnClickListener(this);
        createProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.loginButton:
                //store email
                String loginText = username.getText().toString();
                String passText = password.getText().toString();
                //check to see if email and password in database (to be implemented)
                //if correct, proceed to open MainMenu
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this, ProfileCreation.class));
                break;
        }
    }

}
