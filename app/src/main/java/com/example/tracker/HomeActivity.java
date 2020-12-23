package com.example.tracker;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button StepCounter, Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StepCounter = (Button) findViewById(R.id.btnStepCounterId);
        Profile = (Button) findViewById(R.id.profileId);

        StepCounter.setOnClickListener(this);
        Profile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnStepCounterId:
                Intent counter = new Intent(getApplicationContext(), CounterActivity.class);
                startActivity(counter);

                break;

            case R.id.profileId:
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);

                break;

        }

    }
}
