package com.example.ll.suap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static com.example.ll.suap.ActiveUser.ActiveState.online;
import static com.example.ll.suap.ActiveUser.UserType.Rider;
import static com.example.ll.suap.ActiveUser.status.available;
import static com.example.ll.suap.ActiveUser.status.completed;
import static com.example.ll.suap.ActiveUser.status.missed;

public class DriverArriving extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button complete,missed,menu,profile,logout;
    private UserInformation userInformation;
    private ActiveUser activeUser;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_arriving);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
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

        mydbactiveusers = mydb.child("active_users");
        FirebaseUser myactiveuser = mAuth.getCurrentUser();
        final SharedPreferences details = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(myactiveuser!=null){
            activeUser = new ActiveUser(myactiveuser.getUid(),
                    details.getString("myactiveuser.name", ""),
                    details.getString("myactiveuser.phone", ""),
                    details.getString("myactiveuser.makeModel", ""),
                    details.getString("myactiveuser.year", ""),
                    details.getString("myactiveuser.color", ""),
                    details.getString("myactiveuser.permit", ""),
                    details.getFloat("myactiveuser.latitude", 0),
                    details.getFloat("myactiveuser.longitude", 0),
                    Rider,
                    details.getLong("myactiveuser.timestamp", 0),
                    online,
                    available
            );
        }

        menu = (Button)findViewById(R.id.buttonRegister);
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
        mDrawerLayout.closeDrawer(GravityCompat.START);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonRegister:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case(R.id.button2):
                //add successful ride to driver's page
                mydbactiveusers.child(activeUser.getUserId()).child("status").setValue(completed);
                finish();
                startActivity(new Intent(this, MainMenu.class));
                //after going to main menu the driver should be available again
                break;
            case(R.id.button3):
                //add missed Ride to driver's page
                mydbactiveusers.child(activeUser.getUserId()).child("status").setValue(missed);
                finish();
                startActivity(new Intent(this, MainMenu.class));
                //after going to main menu the driver should be available again
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
