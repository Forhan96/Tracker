package com.example.tracker;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnSignIn, btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnRegister.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnSignIn:
                Intent signIn = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(signIn);

                break;

            case R.id.btnRegister:
                Intent signUp = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUp);

                break;
        }

    }
}
