package com.example.ll.suap;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Driving extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button cancel,menu,profile,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        cancel = (Button) findViewById(R.id.buttonRegister);
        menu = (Button) findViewById(R.id.button2);
        profile = (Button)findViewById(R.id.button4);
        logout = (Button)findViewById(R.id.button5);

        cancel.setOnClickListener(this);
        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {

        switch(v.getId()){
            case R.id.buttonRegister:
                startActivity(new Intent(Driving.this, MainMenu.class));
                break;
            case R.id.button2:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.button4:
                startActivity(new Intent(Driving.this,Profile.class));
                break;
            case R.id.button5:
                mAuth.signOut();
                finish();
                startActivity(new Intent(Driving.this,BeginningActivity.class));
        }
    }
}
