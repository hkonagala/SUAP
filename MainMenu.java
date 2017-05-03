package com.example.ll.suap;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button passenger, driver, finder, map, menu, profile, logout;
    ImageView toggle;
    boolean pass = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        toggle = (ImageView)findViewById(R.id.imageView2);
        passenger = (Button) findViewById(R.id.button1);
        driver = (Button)findViewById(R.id.button2);
        finder = (Button)findViewById(R.id.button3);
        map = (Button)findViewById(R.id.button4);
        menu = (Button)findViewById(R.id.button5);
        profile = (Button)findViewById(R.id.button7);
        logout = (Button)findViewById(R.id.button8);

        passenger.setOnClickListener(this);
        driver.setOnClickListener(this);
        finder.setOnClickListener(this);
        map.setOnClickListener(this);
        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(Gravity.START);
        mAuth = FirebaseAuth.getInstance();
    }

public void onClick(View v){
    switch(v.getId()){
        case R.id.button1:
            pass = true;
            toggle.setImageResource(R.drawable.passenger);
            finder.setBackgroundResource(R.drawable.roundedgreenrectangle);
            finder.setText("Find Ride");
            map.setBackgroundResource(R.drawable.roundedgreenrectangle);
            profile.setBackgroundResource(R.drawable.roundedgreenrectangle);
            logout.setBackgroundResource(R.drawable.roundedgreenrectangle);
            break;
        case R.id.button2:
            pass = false;
            toggle.setImageResource(R.drawable.driver);
            finder.setBackgroundResource(R.drawable.roundedbluerectangle);
            finder.setText("Find Passenger");
            map.setBackgroundResource(R.drawable.roundedbluerectangle);
            profile.setBackgroundResource(R.drawable.roundedbluerectangle);
            logout.setBackgroundResource(R.drawable.roundedbluerectangle);
            break;
        case R.id.button3:
            if(pass)
                startActivity(new Intent(MainMenu.this, Finder.class));
            else
                startActivity(new Intent(MainMenu.this,Driving.class));
            break;
        case R.id.button4:
            startActivity(new Intent(MainMenu.this, CampusMap.class));
            break;
        case R.id.button5:
            mDrawerLayout.openDrawer(Gravity.START);
            break;
        case R.id.button7:
            startActivity(new Intent(MainMenu.this,Profile.class));
            break;
        case R.id.button8:
            mAuth.signOut();
            finish();
            startActivity(new Intent(MainMenu.this,BeginningActivity.class));
            break;
    }
}
}
