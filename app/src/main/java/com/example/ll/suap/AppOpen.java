package com.example.ll.suap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class AppOpen extends AppCompatActivity {

    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_open);
        timer.schedule(new TimerTask(){
            public void run(){
                startActivity(new Intent(AppOpen.this,BeginningActivity.class));
                finish();
            }
        },3000);
    }


}
