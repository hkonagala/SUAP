package com.example.ll.suap;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.ll.suap.ActiveUser.ActiveState.offline;
import static com.example.ll.suap.ActiveUser.status.available;

public class DriverFoundMatch extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button passenger, call, cancel, menu, profile, logout;
    private UserInformation userInformation;
    ActiveUser activeUser;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;
    private DatabaseReference mydbrides;
    String pickupLocation, additionalInfo, riderName, riderPhone;// dName;
    String riderId;
    private ActiveUser riderUser;
    TextView driverName, location_here;// passengerName,riderInfo;
    private String rideId;
    LocationManager locationManager;
    private LatLng currentLocation;
    private Handler handler = new Handler();
    private TextView passengerName;

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
        riderId = myIntent.getStringExtra("rider_user_id");
        rideId = myIntent.getStringExtra("ride_id");
        //dName = myIntent.getStringExtra("driver_user_name");

        mydb = FirebaseDatabase.getInstance().getReference();
        mydbactiveusers = mydb.child("active_users");
        mydbrides = mydb.child("rides");

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
        } else {
            startActivity(new Intent(this, BeginningActivity.class));
        }
        menu = (Button) findViewById(R.id.driver_found_menubutton);
        profile = (Button) findViewById(R.id.driver_found_profilebutton);
        logout = (Button) findViewById(R.id.driver_found_logoutbutton);
        //confirm = (Button) findViewById(R.id.driver_found_confirmbutton);
        call = (Button) findViewById(R.id.driver_found_callbutton);
        cancel = (Button) findViewById(R.id.driver_found_cancelbutton);
        passenger = (Button) findViewById(R.id.driver_found_passengerbutton);
        driverName = (TextView) findViewById(R.id.driver_found_drivername_tv);
        passengerName = (TextView) findViewById(R.id.driver_found_passengername_tv);
        location_here = (TextView) findViewById(R.id.driver_found_location_tv);
        //riderInfo = (TextView) findViewById(R.id.driver_found_additionalInfo_tv);


        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);
        //confirm.setOnClickListener(this);
        call.setOnClickListener(this);
        cancel.setOnClickListener(this);
        passenger.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mydbrides.child(rideId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ride currentRide = dataSnapshot.getValue(Ride.class);
                if (currentRide.status.equals(Ride.ride_status.completed)
                        || currentRide.status.equals(Ride.ride_status.missed)
                        || currentRide.status.equals(Ride.ride_status.cancelled)) {
                    Toast.makeText(getApplicationContext(), "Passenger marked ride as " + currentRide.status
                            , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(DriverFoundMatch.this, MainMenu.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mydbrides.child(rideId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ride currentRide = dataSnapshot.getValue(Ride.class);
                if (currentRide.status.equals(Ride.ride_status.ongoing)) {
                    location_here.setText(currentRide.pickupLocation);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        passengerName.setText(riderName);
        driverName.setText(userInformation.name);
        //location_here.setText();
         //

        call.setText("CALL " + riderName);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10274);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
                signOffFromDatabase();
                mAuth.signOut();
                finish();
                startActivity(new Intent(this,BeginningActivity.class));
                break;
            /*case R.id.driver_found_confirmbutton:
                startActivity(new Intent(this, MainMenu.class));
                break;*/
            case R.id.driver_found_callbutton:
                //get phone number in database and replace phone number below with driver #
                String number = "tel:"+ riderPhone.trim();
                Log.d("DIALING: ", number);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(number));
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DriverFoundMatch.this, new String[]{Manifest.permission.CALL_PHONE}, 125);
                    return;
                }
                if (intent.resolveActivity(DriverFoundMatch.this.getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.driver_found_cancelbutton:
                mydbrides.child(rideId).child("status").setValue(Ride.ride_status.cancelled);
                mydbactiveusers.child(userInformation.userId).child("status").setValue(available);
                mydbactiveusers.child(userInformation.userId).child("myState").setValue(offline);
                mydbactiveusers.child(riderId).child("myState").setValue(offline);
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.driver_found_passengerbutton:
                startActivity(new Intent(this, OtherProfile.class));
                break;

        }
    }

    private void signOffFromDatabase() {
        mydbactiveusers.child(userInformation.userId).child("myState").setValue(offline);
        mydbactiveusers.child(riderId).child("myState").setValue(offline);
        mydbrides.child(rideId).child("status").setValue(Ride.ride_status.completed);//TODO check this
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signOffFromDatabase();
    }

    @Override
    public void onLocationChanged(Location location) {
        handler.post(new LocationTask(location));
    }

    private class LocationTask implements Runnable{

        Location location;

        public LocationTask(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            mydbactiveusers.child(userInformation.userId).child("latitude").setValue(location.getLatitude());
            mydbactiveusers.child(userInformation.userId).child("longitude").setValue(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
