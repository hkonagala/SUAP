package com.example.ll.suap;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverFoundMatch extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button passenger, confirm, call, cancel, menu, profile, logout;
    private UserInformation userInformation;
    ActiveUser activeUser;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;
    String pickupLocation, additionalInfo, riderName, riderPhone;
    int riderId;
    private ActiveUser riderUser;
    TextView driverName, location_here;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_found_match);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Intent myIntent = getIntent();
        pickupLocation = myIntent.getStringExtra("pickup_location");
        additionalInfo = myIntent.getStringExtra("additional_info");
        riderName = myIntent.getStringExtra("rider_user_name");
        riderPhone = myIntent.getStringExtra("rider_user_phone");
        riderId = myIntent.getIntExtra("rider_user_id", 0);

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
        menu = (Button)findViewById(R.id.driver_found_menubutton);
        profile = (Button)findViewById(R.id.driver_found_profilebutton);
        logout = (Button)findViewById(R.id.driver_found_logoutbutton);
        call = (Button)findViewById(R.id.driver_found_callbutton);
        cancel = (Button)findViewById(R.id.driver_found_cancelbutton);
        passenger = (Button)findViewById(R.id.driver_found_passengerbutton);
        driverName = (TextView) findViewById(R.id.driver_found_drivername_tv);
        location_here = (TextView) findViewById(R.id.driver_found_location_tv);


        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);
        call.setOnClickListener(this);
        cancel.setOnClickListener(this);
        passenger.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onResume() {
        super.onResume();
        call.setText("CALL "+riderName);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.driver_found_menubutton:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.driver_found_profilebutton:
                startActivity(new Intent(this,Profile.class));
                break;
            case R.id.driver_found_logoutbutton:
                mAuth.signOut();
                finish();
                startActivity(new Intent(this,BeginningActivity.class));
                break;
            case R.id.driver_found_callbutton:
                ///TODO get phone number in database and replace phone number below with driver #
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+ riderPhone));
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DriverFoundMatch.this, new String[]{android.Manifest.permission.CALL_PHONE}, 125);
                    return;
                }
                startActivity(intent);
                break;
            case R.id.driver_found_cancelbutton:
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.driver_found_passengerbutton:
                startActivity(new Intent(this, OtherProfile.class));
                break;

        }
    }
}
