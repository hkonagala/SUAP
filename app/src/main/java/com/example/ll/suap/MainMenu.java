package com.example.ll.suap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    Button passenger, driver, finder, map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        passenger = (Button) findViewById(R.id.button1);
        driver = (Button)findViewById(R.id.button2);
        finder = (Button)findViewById(R.id.button3);
        map = (Button)findViewById(R.id.button4);
        passenger.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){}
        });
        driver.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){}
        });
        finder.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){startActivity(new Intent(MainMenu.this, Finder.class));}
        });
        map.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){startActivity(new Intent(MainMenu.this, CampusMap.class));}
        });
    }

}
