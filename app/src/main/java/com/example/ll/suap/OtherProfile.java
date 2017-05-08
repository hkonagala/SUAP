package com.example.ll.suap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OtherProfile extends AppCompatActivity implements View.OnClickListener{

    private UserInformation userInformation;
    private FirebaseAuth mAuth;
    TextView success_rides, miss_rides, driverName, driverInfo;
    Button menu, call;
    ImageView profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

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

        menu = (Button) findViewById(R.id.otherprofile_menubutton);
        call = (Button) findViewById(R.id.otherprofile_callbutton);
        success_rides = (TextView) findViewById(R.id.otherprofile_rides_tv);
        miss_rides = (TextView) findViewById(R.id.otherprofile_missedrides_tv);
        driverName = (TextView) findViewById(R.id.otherprofile_drivername_tv);
        driverInfo = (TextView) findViewById(R.id.otherprofile_info_tv);
        profilepic = (ImageView) findViewById(R.id.otherprofile_profileimage);

        //TODO imageview for profile picture do something



        //TODO drawer layout
      //  mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       // mDrawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        menu.setOnClickListener(this);
        call.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //TODO set intents
        switch(v.getId()){
            case R.id.otherprofile_menubutton:
             //   mDrawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.otherprofile_callbutton:
                //TODO implement this call with intents
                //get phone number in database and replace phone number below with driver #
               // Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(driverPhone));
                //startActivity(intent);
                break;

        }

    }
}
