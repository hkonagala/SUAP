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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.ll.suap.ActiveUser.ActiveState.offline;
import static com.example.ll.suap.ActiveUser.UserType.Driver;
import static com.example.ll.suap.ActiveUser.UserType.Rider;
import static com.example.ll.suap.ActiveUser.status.available;
import static com.example.ll.suap.ActiveUser.status.hold;
import static com.example.ll.suap.ActiveUser.status.taken;

public class Driving extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    Button cancel,menu,profile,logout;
    private UserInformation userInformation;
    private ActiveUser activeUser;
    private DatabaseReference mydbactiveusers;
    private DatabaseReference mydbrides;
    private DatabaseReference mydb;
    String riderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

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
        cancel = (Button) findViewById(R.id.driving_cancelbutton);
        menu = (Button) findViewById(R.id.driving_menubutton);
        profile = (Button)findViewById(R.id.driving_profilebutton);
        logout = (Button)findViewById(R.id.driving_logoutbutton);

        cancel.setOnClickListener(this);
        menu.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mydb = FirebaseDatabase.getInstance().getReference();
        mydbactiveusers = mydb.child("active_users");
        mydbrides = mydb.child("rides");
        Intent myIntent = getIntent();
        riderId = myIntent.getStringExtra("rider_user_id");//TODO check this


        mydbactiveusers.child(userInformation.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null){
                    ActiveUser driver = dataSnapshot.getValue(ActiveUser.class);
                    if (driver.getStatus().equals(taken) && !driver.getRideId().equals(0)) {
                        final String rideId = driver.getRideId();
                        mydbrides.child(rideId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Ride ride = dataSnapshot.getValue(Ride.class);
                                String riderId = ride.passengerId;
                                mydbactiveusers.child(riderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ActiveUser rider = dataSnapshot.getValue(ActiveUser.class);
                                        Intent myIntent = new Intent(Driving.this, DriverFoundMatch.class);
                                        myIntent.putExtra("rider_user_id", rider.getUserId());
                                        myIntent.putExtra("rider_user_name", rider.getName());
                                        myIntent.putExtra("rider_phone_number", rider.getPhone());
                                        myIntent.putExtra("ride_id", rideId);
                                        finish();
                                        startActivity(myIntent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onClick(View v) {

        switch(v.getId()){
            case R.id.driving_cancelbutton:
                mydbactiveusers.child(userInformation.userId).child("myState").setValue(offline);
                startActivity(new Intent(Driving.this, MainMenu.class));
                break;
            case R.id.driving_menubutton:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.driving_profilebutton:
                startActivity(new Intent(Driving.this,Profile.class));
                break;
            case R.id.driving_logoutbutton:
                signOffFromDatabase();
                mAuth.signOut();
                finish();
                startActivity(new Intent(Driving.this,BeginningActivity.class));
        }
    }

    private void signOffFromDatabase() {
        mydbactiveusers.child(userInformation.userId).child("myState").setValue(offline);
        mydbactiveusers.child(riderId).child("myState").setValue(offline);//TODO check this
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signOffFromDatabase();
    }
}
