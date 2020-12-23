package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CounterActivity extends AppCompatActivity implements SensorEventListener {

    TextView counterTextView;
    SensorManager sensorManager;
    private Sensor StepCounterSensor, StepDetectorSensor;
    boolean running = false;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference pRef, stepRef;

    int total = 0;
    int stepsFB;
    int currentStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        counterTextView = findViewById(R.id.counterTextViewId);

        pRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        pRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String steps = dataSnapshot.child("steps").getValue().toString();
                stepsFB = Integer.parseInt(steps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        StepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        StepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;


        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            String sinceReboot = String.valueOf(event.values[0]);
            currentStep++;


            counterTextView.setText(String.valueOf(currentStep));

        }


    }

    protected void onResume() {

        super.onResume();


        sensorManager.registerListener(this, StepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, StepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        currentStep = -1;
    }

    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, StepCounterSensor);
        sensorManager.unregisterListener(this, StepDetectorSensor);

        if(currentStep > -1){
            total = stepsFB + currentStep;
        }
        else{
            total = stepsFB;
        }

        stepRef = pRef.child("steps");
        stepRef.setValue(total);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
