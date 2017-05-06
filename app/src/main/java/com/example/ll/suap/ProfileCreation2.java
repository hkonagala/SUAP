package com.example.ll.suap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileCreation2 extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    EditText name, phone, carmakemodel, caryear, carcolor;
    Spinner permitType;
    Button finish;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation2);
        name = (EditText)findViewById(R.id.editText);
        phone = (EditText)findViewById(R.id.editText2);
        carmakemodel = (EditText)findViewById(R.id.editText3);
        caryear = (EditText)findViewById(R.id.editText4);
        carcolor = (EditText)findViewById(R.id.editText5);
        permitType = (Spinner)findViewById(R.id.spinner);
        finish = (Button)findViewById(R.id.button);

        finish.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v){
        saveUserInformation();
        startActivity(new Intent(this,BeginningActivity.class));
    }

    private void saveUserInformation() {
        //Getting values from database
        String nameText = name.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String makeModelText = carmakemodel.getText().toString().trim();
        String yearText = caryear.getText().toString().trim();
        String colorText = carcolor.getText().toString().trim();
        String permitText = permitType.getSelectedItem().toString().trim();

        //creating a userinformation object
        UserInformation userInformation = new UserInformation("", "",nameText,phoneText,makeModelText,yearText,colorText,permitText);

        //adding information to database
        databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(userInformation);
        Toast.makeText(ProfileCreation2.this,"Registration Successful",Toast.LENGTH_LONG).show();
    }
}
