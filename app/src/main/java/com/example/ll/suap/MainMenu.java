package com.example.ll.suap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.ll.suap.ActiveUser.ActiveState.online;
import static com.example.ll.suap.ActiveUser.UserType.Driver;
import static com.example.ll.suap.ActiveUser.UserType.Rider;
import static com.example.ll.suap.ActiveUser.status.available;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button passenger, driver, finder, map, menu, profile, logout;
    ImageView toggle;
    boolean pass = true;
    UserInformation userInformation;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;
    private DatabaseReference mydbchildusers;
    ActiveUser activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mydb = FirebaseDatabase.getInstance().getReference();
        //String name, String phone, String makeModel, String year, String color, String permit, Long timestamp, Boolean matched, String match, double latitude, double longitude
        SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (user != null) {
            userInformation = new UserInformation(user.getUid(),
                    userDetails.getString("user.email", ""),
                    userDetails.getString("user.name", ""),
                    userDetails.getString("user.phone", ""),
                    userDetails.getString("user.makeModel", ""),
                    userDetails.getString("user.year", ""),
                    userDetails.getString("user.color", ""),
                    userDetails.getString("user.permit", ""),
                    userDetails.getLong("user.timestamp", 0),
                    userDetails.getBoolean("user.matched", false),
                    userDetails.getString("user.match", ""),
                    userDetails.getFloat("user.latitude", 0),
                    userDetails.getFloat("user.longitude", 0)
                    );
        }else {
            startActivity(new Intent(this, BeginningActivity.class));
        }

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
            getDriverInfo();
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

    private void getDriverInfo() {
        FirebaseUser myactiveuser = mAuth.getCurrentUser();
        mydbactiveusers = mydb.child("active_users");
        final SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(myactiveuser!=null){
            activeUser = new ActiveUser(myactiveuser.getUid(),
                    userDetails.getString("myactiveuser.name", ""),
                    userDetails.getString("myactiveuser.phone", ""),
                    userDetails.getString("myactiveuser.makeModel", ""),
                    userDetails.getString("myactiveuser.year", ""),
                    userDetails.getString("myactiveuser.color", ""),
                    userDetails.getString("myactiveuser.permit", ""),
                    userDetails.getFloat("myactiveuser.latitude", 0),
                    userDetails.getFloat("myactiveuser.longitude", 0),
                    Driver,
                    userDetails.getLong("myactiveuser.timestamp", 0),
                    online,
                    available
            );
        }
    }
}
