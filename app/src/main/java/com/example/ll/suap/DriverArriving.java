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
import android.os.AsyncTask;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.ll.suap.ActiveUser.ActiveState.offline;
import static com.example.ll.suap.ActiveUser.ActiveState.online;
import static com.example.ll.suap.ActiveUser.UserType.Rider;
import static com.example.ll.suap.ActiveUser.status.available;

public class DriverArriving extends AppCompatActivity implements View.OnClickListener, LocationListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button complete,missed,menu,profile,logout;
    Button call;
    private UserInformation userInformation;
    private ActiveUser activeUser;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers, mydbrides;
    private LocationManager locationManager;
    private Handler mHandler;
    private LatLng currentLocation;
    private LatLng driverLocation;
    String pickupLocation, additionalInfo, driverName, driverPhone;
    String driverId;
    TextView eta;
    private String rideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_arriving);

        mydb = FirebaseDatabase.getInstance().getReference();
        mydbactiveusers = mydb.child("active_users");
        mydbrides = mydb.child("rides");
        Intent myIntent = getIntent();
        pickupLocation = myIntent.getStringExtra("pickup_location");
        additionalInfo = myIntent.getStringExtra("additional_info");
        driverName = myIntent.getStringExtra("driver_user_name");
        driverPhone = myIntent.getStringExtra("driver_user_phone");
        driverId = myIntent.getStringExtra("driver_user_id");
        rideId = myIntent.getStringExtra("ride_id");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
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

        menu = (Button)findViewById(R.id.driver_arriving_menubutton);
        complete = (Button)findViewById(R.id.driver_arriving_completebutton);
        missed = (Button)findViewById(R.id.driver_arriving_missedbutton);
        profile = (Button)findViewById(R.id.driver_arriving_profilebutton);
        logout = (Button)findViewById(R.id.driver_arriving_logoutbutton);
        eta = (TextView) findViewById(R.id.driver_arriving_tv_eta);
        call = (Button) findViewById(R.id.driver_arriving_callbutton);

        menu.setOnClickListener(this);
        complete.setOnClickListener(this);
        missed.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);
        call.setOnClickListener(this);
        //implement map here

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onResume() {
        super.onResume();
         call.setText("CALL " + driverName);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 121212);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mHandler = new Handler();
        mydbactiveusers.child(String.valueOf(driverId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActiveUser driver = dataSnapshot.getValue(ActiveUser.class);
                if (driver != null && driver.latitude != 0 && driver.longitude != 0){
                    driverLocation = new LatLng(driver.latitude, driver.longitude);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mydbrides.child(rideId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ride currentRide = dataSnapshot.getValue(Ride.class);
                if (currentRide.status.equals(Ride.ride_status.cancelled)){
                    Toast.makeText(getApplicationContext(), "Driver cancelled the ride"
                            , Toast.LENGTH_LONG).show();
                    startActivity(new Intent(DriverArriving.this, MainMenu.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.driver_arriving_menubutton:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case(R.id.driver_arriving_completebutton):
                //add successful ride to driver's page
                mydbrides.child(rideId).child("status").setValue(Ride.ride_status.completed);
                mydbactiveusers.child(driverId).child("status").setValue(available);
                mydbactiveusers.child(driverId).child("myState").setValue(offline);
                mydbactiveusers.child(userInformation.userId).child("myState").setValue(offline);
                finish();
                startActivity(new Intent(this, MainMenu.class));
                //after going to main menu the driver should be available again
                break;
            case(R.id.driver_arriving_missedbutton):
                //add missed Ride to driver's page
                mydbrides.child(rideId).child("status").setValue(Ride.ride_status.missed);
                mydbactiveusers.child(driverId).child("status").setValue(available);
                mydbactiveusers.child(driverId).child("myState").setValue(offline);
                mydbactiveusers.child(userInformation.userId).child("myState").setValue(offline);
                finish();
                startActivity(new Intent(this, MainMenu.class));
                //after going to main menu the driver should be available again
                break;
            case R.id.driver_arriving_profilebutton:
                startActivity(new Intent(DriverArriving.this,Profile.class));
                break;
            case R.id.driver_arriving_logoutbutton:
                signOffFromDatabase();
                mAuth.signOut();
                finish();
                startActivity(new Intent(DriverArriving.this,BeginningActivity.class));
                break;
            case R.id.driver_arriving_callbutton:
                String number = "tel:"+ driverPhone.trim();
                Log.d("DIALING: ", number);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(number));
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DriverArriving.this, new String[]{Manifest.permission.CALL_PHONE}, 125);
                    return;
                }
                if (intent.resolveActivity(DriverArriving.this.getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }
    }

    private void signOffFromDatabase() {
        mydbactiveusers.child(userInformation.userId).child("myState").setValue(offline);
        mydbactiveusers.child(driverId).child("myState").setValue(offline);
        mydbrides.child(rideId).child("status").setValue(Ride.ride_status.cancelled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signOffFromDatabase();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (driverLocation != null) {
            LatLng[] data = new LatLng[2];
            data[0] = currentLocation;
            data[1] = driverLocation;
            UpdateLocationTask updateLocationTask = new UpdateLocationTask();
            updateLocationTask.execute(data);
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

    private class UpdateLocationTask extends AsyncTask<LatLng, Integer, String> {


        @Override
        protected String doInBackground(LatLng... params) {
            LatLng driverLoc = params[0];
            LatLng passengerLoc = params[1];
            String API_KEY = getString(R.string.GMAPS_API_KEY);
            String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + driverLoc.latitude + "," + driverLoc.longitude + "&destination="
                    + passengerLoc.latitude + "," + passengerLoc.longitude + "&mode=driving&key=" + API_KEY;
            String result;
            String inputLine;

            final String REQUEST_METHOD = "GET";
            final int READ_TIMEOUT = 15000;
            final int CONNECTION_TIMEOUT = 15000;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(requestUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            Log.d("ETARESULT", result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject mainJson = new JSONObject(s);
                JSONArray mainArray = mainJson.getJSONArray("routes");
                JSONObject route = mainArray.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);
                JSONObject duration = leg.getJSONObject("duration");
                String etaString = duration.getString("text");
                eta.setText(etaString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}