package com.example.ll.suap;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MatchingService extends IntentService {

    ResultReceiver resultReceiver;
    DatabaseReference myDatabase;
    Query search;
    String myKey;
    FirebaseAuth mAuth;

    public static final String ATTACHED_HANDLER_ResultReceiver = "handler";
    //public static final String DB_IDENTIFIER_String="key";
    public static final String SERVICE_RETURN_UserInformation = "myPerson";

    public MatchingService() {
        super("MatchingService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        resultReceiver = intent.getParcelableExtra(ATTACHED_HANDLER_ResultReceiver);
        //myKey=intent.getParcelableExtra(DB_IDENTIFIER_String);
        myDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //works if getUid is same as key in DB, pretty sure it is
        myKey = mAuth.getCurrentUser().getUid();

        //gets which permit I'm using
        String myPermit = myDatabase.child(myKey).child("permit").toString();

        //now look for those who share permit and order them by time
        boolean found = false;
        //Query myQuery;
        //should just get the person
        search = myDatabase.orderByChild("matched").equalTo(false).
                orderByChild("permit").equalTo(myPermit).orderByChild("timestamp").limitToFirst(1);
        //don't know why once isn't being recognized...
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation myMatchedUser;
                int i = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    /* actually, these two are a bad idea
                    public String email=child.child("email");
                    public String password=child.child("password"); */
                    String name = child.child("name").toString();
                    String phone = child.child("phone").toString();
                    String makeModel = child.child("makeModel").toString();
                    String year = child.child("year").toString();
                    String color = child.child("color").toString();
                    String permit = child.child("permit").toString();
                    //for matching
                    Long timestamp = Long.parseLong(child.child("timestamp").toString());
                    Boolean matched = Boolean.parseBoolean(child.child("matched"));
                    String match = child.child("match").toString();
                    myMatchedUser = new UserInformation("", "", name, phone,
                            makeModel, year, color, permit);
                    i++;

                }
                if (i == 1) {
                    UserInformation myMatchedUser = dataSnapshot.
                            Bundle returnVal = new Bundle();

                    resultReceiver.send(1, );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
