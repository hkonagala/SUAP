package com.example.ll.suap;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class DriverArriving extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button complete,missed,menu,profile,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_arriving);
        menu = (Button)findViewById(R.id.button);
        complete = (Button)findViewById(R.id.button2);
        missed = (Button)findViewById(R.id.button3);
        profile = (Button)findViewById(R.id.button5);
        logout = (Button)findViewById(R.id.button6);

        menu.setOnClickListener(this);
        complete.setOnClickListener(this);
        missed.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);
        //implement map here

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(Gravity.START);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            case(R.id.button2):
                //add successful ride to driver's page
                startActivity(new Intent(this, MainMenu.class));
                break;
            case(R.id.button3):
                //add missed rides to driver's page
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.button5:
                startActivity(new Intent(DriverArriving.this,Profile.class));
                break;
            case R.id.button6:
                mAuth.signOut();
                finish();
                startActivity(new Intent(DriverArriving.this,BeginningActivity.class));
        }
    }
}
