package com.example.ll.suap;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.ll.suap.ActiveUser.ActiveState.online;
import static com.example.ll.suap.ActiveUser.UserType.Driver;
import static com.example.ll.suap.ActiveUser.UserType.Rider;
import static com.example.ll.suap.ActiveUser.status.available;
import static com.example.ll.suap.ActiveUser.status.hold;

public class Finder extends AppCompatActivity implements View.OnClickListener, LocationListener, AdapterView.OnItemSelectedListener {

    private static final int REQUEST_LOCATION_ID = 101;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button finder, menu, profile, logout;
    TextView count;
    private UserInformation userInformation;
    ActiveUser activeUser;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;
    private DatabaseReference mydbchildusers;
    Handler handler = new Handler();
    private LocationManager locationManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Spinner pickupLocation;
    private String selectedPickupLocation;
    EditText additionalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mydb = FirebaseDatabase.getInstance().getReference();
        pickupLocation = (Spinner) findViewById(R.id.finder_spinner_location);
        pickupLocation.setOnItemSelectedListener(this);
        additionalInfo = (EditText) findViewById(R.id.finder_et_info);

        mydbchildusers = mydb.child("users");
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

        count = (TextView) findViewById(R.id.finder_tv_count);
        // eta = (TextView) findViewById(R.id.textView3);
        finder = (Button) findViewById(R.id.finder_pickupbutton);
        menu = (Button) findViewById(R.id.finder_menubutton);
        profile = (Button) findViewById(R.id.finder_profilebutton);
        logout = (Button) findViewById(R.id.finder_logoutbutton);

        finder.setOnClickListener(this);
        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);


        mydbactiveusers = mydb.child("active_users");


    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Finder.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_ID);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        final Query countQuery = mydbactiveusers
                .orderByChild("myState").equalTo("myState", String.valueOf(online));
                /*.orderByChild("myType").equalTo("myType", String.valueOf(Driver))
                .orderByChild("permit").equalTo("permit",userInformation.permit);*/
        //too many where conditions
        countQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //dataSnapshot.getChildrenCount();
                if(dataSnapshot !=null){
                    for(DataSnapshot search : dataSnapshot.getChildren()){
                        if (search.getValue() == Driver)
                            if (search.getValue() == userInformation.permit) {
                                count.setText(dataSnapshot.getChildrenCount() + " people are arriving on campus");
                            }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //int estimatedTimeMin = 1;
        //int estimatedTimeSec = 30;
        //eta.setText(estimatedTimeMin + " : " + estimatedTimeSec + " min");
    }

    public void onClick(View v)
    {
        switch(v.getId()) {
            case R.id.finder_pickupbutton:
                getActiveRiderInfo();
                break;
            case R.id.finder_menubutton:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.finder_profilebutton:
                startActivity(new Intent(Finder.this,Profile.class));
                break;
            case R.id.finder_logoutbutton:
                mAuth.signOut();
                finish();
                startActivity(new Intent(Finder.this,BeginningActivity.class));
        }
    }

    private void getActiveRiderInfo() {
        // mAuth = FirebaseAuth.getInstance();
        //mydb = FirebaseDatabase.getInstance().getReference();
        //load passenger details into active user on button click
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
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
                    Rider,
                    userInformation.timestamp,
                    online,
                    available
            );
        }

        final Query query = mydbactiveusers.child(userInformation.userId).equalTo("myState", String.valueOf(online));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //update the entry
                    mydbactiveusers.child(activeUser.getUserId()).child("myState").setValue(ActiveUser.ActiveState.offline);
                }
                //add new entry
                mydbactiveusers.child(activeUser.getUserId()).setValue(activeUser);
                //query to search list of drivers
                Log.d("VALUE", String.valueOf(Driver));
                //Query search = mydbactiveusers.orderByChild("myType").equalTo("myType", String.valueOf(Driver));
                        /*.equalTo("status", String.valueOf(available))
                        .orderByChild("timestamp");*/
                mydbactiveusers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot !=null && dataSnapshot.getValue()!=null){
                            ActiveUser oldestDriver = null;
                            for(DataSnapshot search : dataSnapshot.getChildren()){
                                ActiveUser currentDriver = search.getValue(ActiveUser.class);
                                Log.d("DRIVERINFO", currentDriver.getName());
                                if(currentDriver.getMyState().equals(online) && currentDriver.getMyType().equals(Driver) && currentDriver.getStatus().equals(available)){
                                    if (oldestDriver == null || oldestDriver.getTimestamp() > currentDriver.getTimestamp()){
                                        oldestDriver = currentDriver;
                                    }
                                }
                            }
                            if (oldestDriver != null) {
                                mydbactiveusers.child(oldestDriver.getUserId()).child("status").setValue(hold);
                                //go to next page and update the status to taken
                                Intent myIntent = new Intent(Finder.this, FoundMatch.class);
                                myIntent.putExtra("driver_user_id", oldestDriver.getUserId());
                                myIntent.putExtra("driver_user_name", oldestDriver.getName());
                                myIntent.putExtra("driver_user_phone", oldestDriver.getPhone());
                                myIntent.putExtra("pickup_location", selectedPickupLocation);
                                myIntent.putExtra("additional_info", additionalInfo.getText().toString());
                                finish();
                                startActivity(myIntent);
                            }else{
                                //else toast error message
                                Toast.makeText(Finder.this,"Please wait while we find a driver",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            //else toast error message
                            Toast.makeText(Finder.this,"Please wait while we find a driver",Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                query.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        handler.post(new LocationTask(location));
        //handler.post(new DistanceTask(location));


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPickupLocation = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class LocationTask implements Runnable{

        Location location;

        public LocationTask(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            mydbchildusers.child(userInformation.userId).child("latitude").setValue(location.getLatitude());
            mydbchildusers.child(userInformation.userId).child("longitude").setValue(location.getLongitude());
        }
    }


   /* private class DistanceTask implements Runnable {
        Location location;
        public DistanceTask(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            mydbactiveusers.child(activeUser.userId).child("latitude").setValue(location.getLatitude());
            mydbactiveusers.child(activeUser.userId).child("longitude").setValue(location.getLongitude());
            //not sure though if the exact user's location is loaded
           *//*?? Query location = mydbactiveusers.child(activeUser.getUserId()).child("status").equalTo("status", String.valueOf(taken))
                    .equalTo("myType", String.valueOf(Driver));*//*
        }
    }*/
}
