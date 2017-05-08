package com.example.ll.suap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import static com.example.ll.suap.ActiveUser.status.available;
import static com.example.ll.suap.R.id.foundmatch_cancelbutton;

public class FoundMatch extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button driver, confirm, call, cancel, menu, profile, logout;
    UserInformation userInformation;
    ActiveUser activeUser;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;
    String pickupLocation, additionalInfo, driverName, driverPhone;
    int driverId;
    private ActiveUser driverUser;
    ImageView passengerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_match);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Intent myIntent = getIntent();
        pickupLocation = myIntent.getStringExtra("pickup_location");
        additionalInfo = myIntent.getStringExtra("additional_info");
        driverName = myIntent.getStringExtra("driver_user_name");
        driverPhone = myIntent.getStringExtra("driver_user_phone");
        driverId = myIntent.getIntExtra("driver_user_id", 0);

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

        driver = (Button)findViewById(R.id.foundmatch_driverbutton);
        menu = (Button)findViewById(R.id.foundmatch_menubutton);
        confirm = (Button)findViewById(R.id.foundmatch_confirmbutton);
        call = (Button)findViewById(R.id.foundmatch_callbutton);
        cancel = (Button)findViewById(foundmatch_cancelbutton);
        profile = (Button)findViewById(R.id.foundmatch_profilebutton);
        logout = (Button)findViewById(R.id.foundmatch_logoutbutton);
        passengerImage = (ImageView) findViewById(R.id.foundmatch_passengerimgage); 
        

        //TODO display passenger name/picture in imageview2
        driver.setOnClickListener(this);
        confirm.setOnClickListener(this);
        call.setOnClickListener(this);
        cancel.setOnClickListener(this);
        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onResume() {
        super.onResume();
        call.setText("CALL "+driverName);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.foundmatch_driverbutton:
                startActivity(new Intent(this, DriverProfile.class));
                break;
            case R.id.foundmatch_menubutton:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.foundmatch_confirmbutton:
                getDriverInfo();
                finish();
                Intent myIntent = new Intent(FoundMatch.this, DriverArriving.class);
                myIntent.putExtra("driver_user_id", driverId);
                myIntent.putExtra("driver_user_name", driverName);
                myIntent.putExtra("driver_user_phone", driverPhone);
                myIntent.putExtra("pickup_location", pickupLocation);
                myIntent.putExtra("additional_info", additionalInfo);
                startActivity(myIntent);
                break;
            case R.id.foundmatch_callbutton:
                //get phone number in database and replace phone number below with driver #
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(driverPhone));
                startActivity(intent);
                break;
            case foundmatch_cancelbutton:
                mydbactiveusers.child(String.valueOf(driverId)).child("status").setValue(available);
                finish();
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.foundmatch_profilebutton:
                startActivity(new Intent(FoundMatch.this,Profile.class));
                break;
            case R.id.foundmatch_logoutbutton:
                mAuth.signOut();
                finish();
                startActivity(new Intent(FoundMatch.this,BeginningActivity.class));
        }
    }

    private void getDriverInfo() {
        mydbactiveusers = mydb.child("Ride");
        String randString = getSaltString();
        Ride ride = new Ride(activeUser.getUserId(), String.valueOf(driverId), randString, Ride.ride_status.ongoing);
        mydbactiveusers.child(randString).setValue(ride);
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
}
