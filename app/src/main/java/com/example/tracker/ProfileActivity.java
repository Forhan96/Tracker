package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView pName, pPhone, pEmail, pSteps;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference pRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        profileImage = findViewById(R.id.profileImage);
        pName = findViewById(R.id.profileName);
        pEmail = findViewById(R.id.profileEmail);
        pPhone = findViewById(R.id.profilePhone);
        pSteps = findViewById(R.id.profileSteps);

        pRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        pRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String picture = dataSnapshot.child("url").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String phone = dataSnapshot.child("phone_no").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String steps = dataSnapshot.child("steps").getValue().toString();

                Picasso.get().load(picture).into(profileImage);

                pName.setText(name);
                pPhone.setText(phone);
                pEmail.setText(email);
                pSteps.setText(steps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}