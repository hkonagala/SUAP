package com.example.ll.suap;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class DriverFoundMatch extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button passenger, confirm, call, cancel, menu, profile, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_found_match);
        menu = (Button)findViewById(R.id.buttonRegister);
        profile = (Button)findViewById(R.id.button3);
        logout = (Button)findViewById(R.id.button4);
        confirm = (Button)findViewById(R.id.button5);
        call = (Button)findViewById(R.id.button6);
        cancel = (Button)findViewById(R.id.button7);
        passenger = (Button)findViewById(R.id.button8);

        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);
        confirm.setOnClickListener(this);
        call.setOnClickListener(this);
        cancel.setOnClickListener(this);
        passenger.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonRegister:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.button3:
                startActivity(new Intent(this,Profile.class));
                break;
            case R.id.button4:
                mAuth.signOut();
                finish();
                startActivity(new Intent(this,BeginningActivity.class));
                break;
            case R.id.button5:
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.button6:
                //get phone number in database and replace phone number below with passenger #
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4109003684"));
                startActivity(intent);
                break;
            case R.id.button7:
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.button8:
                startActivity(new Intent(this, OtherProfile.class));
                break;

        }
    }
}
