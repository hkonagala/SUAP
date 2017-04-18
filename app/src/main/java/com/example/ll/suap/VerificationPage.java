package com.example.ll.suap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class VerificationPage extends AppCompatActivity implements View.OnClickListener{

    EditText code;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);
        code = (EditText)findViewById(R.id.editText);
        submit = (Button)findViewById(R.id.button4);
    }

    @Override
    public void onClick(View v){
    //verify if code correct - to be implemented
        startActivity(new Intent(this, VerificationSuccess.class));
    }
}
