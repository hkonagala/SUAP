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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference mydb;
    private DatabaseReference mydbactiveusers;

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

        Query search = mydbactiveusers.equalTo("myType", String.valueOf(Rider))
                .equalTo("myState", String.valueOf(ActiveUser.ActiveState.online))
                .equalTo("status", String.valueOf(available));
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null){
                    //get list of riders and update their status to
                    ActiveUser rider = dataSnapshot.getValue(ActiveUser.class);
                    mydbactiveusers.child(rider.getUserId()).child("status").setValue(taken);
                    //go to next page and update the status to taken
                    Intent myIntent = new Intent(Driving.this, DriverFoundMatch.class);
                    myIntent.putExtra("rider_user_id", rider.getUserId());
                    myIntent.putExtra("rider_user_name", rider.getName());
                    myIntent.putExtra("rider_phone_number", rider.getPhone());
                    finish();
                    startActivity(myIntent);
                }else {
                    //else toast error message
                    Toast.makeText(Driving.this,"Please wait while we find you a rider",Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(Driving.this, MainMenu.class));
                break;
            case R.id.driving_menubutton:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.driving_profilebutton:
                startActivity(new Intent(Driving.this,Profile.class));
                break;
            case R.id.driving_logoutbutton:
                mAuth.signOut();
                finish();
                startActivity(new Intent(Driving.this,BeginningActivity.class));
        }
    }
}
