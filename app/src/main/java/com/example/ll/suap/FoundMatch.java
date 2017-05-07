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

public class FoundMatch extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button driver, confirm, call, cancel, menu, profile, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_match);
        driver = (Button)findViewById(R.id.buttonPassenger);
        menu = (Button)findViewById(R.id.button2);
        confirm = (Button)findViewById(R.id.button4);
        call = (Button)findViewById(R.id.button5);
        cancel = (Button)findViewById(R.id.button6);
        profile = (Button)findViewById(R.id.button7);
        logout = (Button)findViewById(R.id.button8);

        driver.setOnClickListener(this);
        confirm.setOnClickListener(this);
        call.setOnClickListener(this);
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
            case R.id.buttonPassenger:
                startActivity(new Intent(this, DriverProfile.class));
                break;
            case R.id.button2:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.button4:
                startActivity(new Intent(this, DriverArriving.class));
                break;
            case R.id.button5:
                //get phone number in database and replace phone number below with driver #
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4109003684"));
                startActivity(intent);
                break;
            case R.id.button6:
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.button7:
                startActivity(new Intent(FoundMatch.this,Profile.class));
                break;
            case R.id.button8:
                mAuth.signOut();
                finish();
                startActivity(new Intent(FoundMatch.this,BeginningActivity.class));
        }
    }
}
