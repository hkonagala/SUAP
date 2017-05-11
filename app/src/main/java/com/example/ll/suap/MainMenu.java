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
        mydbactiveusers = mydb.child("active_users");
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

        toggle = (ImageView)findViewById(R.id.main_toggleimage);
        passenger = (Button) findViewById(R.id.main_passengerbutton);
        finder = (Button)findViewById(R.id.main_finderbutton);
        driver = (Button)findViewById(R.id.main_driverbutton);
        map = (Button)findViewById(R.id.main_mapbutton);
        menu = (Button)findViewById(R.id.main_menubutton);
        profile = (Button)findViewById(R.id.main_profilebutton);
        logout = (Button)findViewById(R.id.main_logoutbutton);

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
            case R.id.main_passengerbutton:
                pass = true;
                toggle.setImageResource(R.drawable.passenger);
                finder.setBackgroundResource(R.drawable.roundedgreenrectangle);
                finder.setText("Find Ride");
                map.setBackgroundResource(R.drawable.roundedgreenrectangle);
                profile.setBackgroundResource(R.drawable.roundedgreenrectangle);
                logout.setBackgroundResource(R.drawable.roundedgreenrectangle);
                break;
            case R.id.main_driverbutton:
                pass = false;
                toggle.setImageResource(R.drawable.driver);
                finder.setBackgroundResource(R.drawable.roundedbluerectangle);
                finder.setText("Find Passenger");
                map.setBackgroundResource(R.drawable.roundedbluerectangle);
                profile.setBackgroundResource(R.drawable.roundedbluerectangle);
                logout.setBackgroundResource(R.drawable.roundedbluerectangle);
                break;
            case R.id.main_finderbutton:
                if(pass)
                    startActivity(new Intent(MainMenu.this, Finder.class));
                /*{
                    Intent myIntent = new Intent(MainMenu.this, Finder.class);
                    myIntent.putExtra("driver_user_id", activeUser.getUserId());//TODO check this

                }*/
                else
                    getDriverInfo();
                break;
            case R.id.main_mapbutton:
                startActivity(new Intent(MainMenu.this, CampusMap.class));
                break;
            case R.id.main_menubutton:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.main_profilebutton:
                startActivity(new Intent(MainMenu.this,Profile.class));
                break;
            case R.id.main_logoutbutton:
                mAuth.signOut();
                finish();
                startActivity(new Intent(MainMenu.this,BeginningActivity.class));
                break;
        }
    }

    private void getDriverInfo() {
        //load driver details into active user on button click
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Long timestamp = System.currentTimeMillis();
            activeUser = new ActiveUser(user.getUid(),
                    userInformation.name,
                    userInformation.phone,
                    userInformation.makeModel,
                    userInformation.year,
                    userInformation.color,
                    userInformation.permit,
                    userInformation.latitude,
                    userInformation.longitude,
                    "0",
                    Driver,
                    timestamp,
                    online,
                    available
            );
            mydbactiveusers.child(user.getUid()).setValue(activeUser);
        }

        finish();
        startActivity(new Intent(MainMenu.this,Driving.class));
        /*Intent myIntent = new Intent(MainMenu.this, Driving.class);
        myIntent.putExtra("rider_user_id", userInformation.userId);
*/
    }

}