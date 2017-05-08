package com.example.ll.suap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.ll.suap.ActiveUser.ActiveState.online;
import static com.example.ll.suap.ActiveUser.UserType.Rider;
import static com.example.ll.suap.ActiveUser.status.available;

public class DriverArriving extends AppCompatActivity implements View.OnClickListener, LocationListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button complete,missed,menu,profile,logout;
    private UserInformation userInformation;
    private ActiveUser activeUser;
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;
    private LocationManager locationManager;
    private Handler mHandler;
    private LatLng currentLocation;
    private LatLng driverLocation;
    String pickupLocation, additionalInfo, driverName, driverPhone;
    int driverId;
    TextView eta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_arriving);

        Intent myIntent = getIntent();
        pickupLocation = myIntent.getStringExtra("pickup_location");
        additionalInfo = myIntent.getStringExtra("additional_info");
        driverName = myIntent.getStringExtra("driver_user_name");
        driverPhone = myIntent.getStringExtra("driver_user_phone");
        driverId = myIntent.getIntExtra("driver_user_id", 0);

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

        mydbactiveusers = mydb.child("active_users");
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
                    Rider,
                    userInformation.timestamp,
                    online,
                    available
            );
        }

        menu = (Button)findViewById(R.id.button);
        complete = (Button)findViewById(R.id.button3);
        missed = (Button)findViewById(R.id.button2);
        profile = (Button)findViewById(R.id.button5);
        logout = (Button)findViewById(R.id.button6);
        eta = (TextView) findViewById(R.id.textView11);

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
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mHandler = new Handler();
        mydbactiveusers.child(String.valueOf(driverId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActiveUser driver = dataSnapshot.getValue(ActiveUser.class);
                if (driver != null && driver.latitude > 0 && driver.longitude > 0){
                    driverLocation = new LatLng(driver.latitude, driver.longitude);
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
            case R.id.button:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case(R.id.button3):
                //add successful ride to driver's page
                //mydbactiveusers.child(activeUser.getUserId()).child("status").setValue(completed);
                finish();
                startActivity(new Intent(this, MainMenu.class));
                //after going to main menu the driver should be available again
                break;
            case(R.id.button2):
                //add missed Ride to driver's page
                //mydbactiveusers.child(activeUser.getUserId()).child("status").setValue(missed);
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

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (driverLocation != null) {
            LatLng[] data = new LatLng[2];
            data[1] = currentLocation;
            data[2] = driverLocation;
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

    private class UpdateLocationTask extends AsyncTask<LatLng, Integer, StringBuilder> {


        @Override
        protected StringBuilder doInBackground(LatLng... params) {
            LatLng driverLoc = params[0];
            LatLng passengerLoc = params[1];
            String API_KEY = getString(R.string.GMAPS_API_KEY);
            String requestUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=" + driverLoc.latitude + "," + driverLoc.longitude + "&destination="
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
            return null;
        }
    }
}
