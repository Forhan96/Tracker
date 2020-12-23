package com.example.tracker;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CHOOSE_IMAGE = 101;
    private EditText signUpNameEditText;
    private EditText signUpPhoneNoEditText;
    private EditText signUpEmailEditText;
    private EditText signUpPasswordEditText;
    private TextView signInTextView;
    private ImageView signUpImageView;
    private Button signUpButton;
    private ProgressBar imageProgressbar, signUpProgressBar;
    private FirebaseAuth mAuth;

    Uri uriProfilePicture;
    String profilePictureUrl;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //a digit must occur at least once
                    "(?=.*[a-z])" +         //a lower case letter must occur at least once
                    "(?=.*[A-Z])" +         // an upper case letter must occur at least once
                    ".{6,}" +               //at least six places though
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("Sign Up");

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        signUpNameEditText = findViewById(R.id.signUpNameEditTextId);
        signUpPhoneNoEditText=findViewById(R.id.signUpPhoneNoEditTextId);
        signUpEmailEditText = findViewById(R.id.signUpEmailEditTextId);
        signUpPasswordEditText = findViewById(R.id.signUpPasswordEditTextId);
        signUpImageView = findViewById(R.id.signUpImageViewId);
        signUpButton = findViewById(R.id.signUpButtonId);
        signInTextView = findViewById(R.id.signInTextViewId);
        imageProgressbar = findViewById(R.id.imageProgressbarId);
        signUpProgressBar = findViewById(R.id.signUpProgressBarId);

        signInTextView.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        signUpImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.signUpImageViewId:
                showImageChooser();
                break;

            case R.id.signUpButtonId:
                userRegister();
                break;

            case R.id.signInTextViewId:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profilePictureRef = FirebaseStorage.getInstance().getReference("profilePics/"+System.currentTimeMillis() + ".jpg");

        if(uriProfilePicture != null){
            imageProgressbar.setVisibility(View.VISIBLE);
            profilePictureRef.putFile(uriProfilePicture)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageProgressbar.setVisibility(View.GONE);
                            //profilePictureUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                            profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profilePictureUrl = uri.toString();
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            imageProgressbar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showImageChooser() {
        Intent intent1 = new Intent();
        intent1.setType("image/*");
        intent1.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent1, CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriProfilePicture = data.getData();
            Picasso.get().load(uriProfilePicture).into(signUpImageView);

            uploadImageToFirebaseStorage();



        }
    }

    private void userRegister() {
        final String name  = signUpNameEditText.getText().toString();
        final String phoneNo  = signUpPhoneNoEditText.getText().toString();
        final String email  = signUpEmailEditText.getText().toString().trim();
        String password  = signUpPasswordEditText.getText().toString().trim();
        final int steps = 0;


        if(name.isEmpty()){
            signUpNameEditText.setError("Enter your name");
            signUpNameEditText.requestFocus();
            return;
        }

        if(phoneNo.isEmpty()){
            signUpPhoneNoEditText.setError("Enter your phone no.");
            signUpPhoneNoEditText.requestFocus();
            return;
        }

        if(phoneNo.length() != 11){
            signUpPhoneNoEditText.setError("Minimum length of phone no. should be 11");
            signUpPhoneNoEditText.requestFocus();
            return;
        }

//        Validity check for email
        if(email.isEmpty()){
            signUpEmailEditText.setError("Enter an email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmailEditText.setError("Enter a valid email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        //Validity check for password
        if(password.isEmpty()){
            signUpPasswordEditText.setError("Enter a password");
            signUpPasswordEditText.requestFocus();
            return;
        }

        if(!PASSWORD_PATTERN.matcher(password).matches()){

            signUpPasswordEditText.setError("Password must contain: Uppercase, Lowercase, Digit, Min. Length: 6");
            signUpPasswordEditText.requestFocus();
            return;
        }


        signUpProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signUpProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {

                    UserProfileChangeRequest.Builder profile = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(profilePictureUrl));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                    Map newPost = new HashMap();
                    newPost.put("name", name);
                    newPost.put("phone_no", phoneNo);
                    newPost.put("email", email);
                    newPost.put("steps", steps);
                    newPost.put("url", profilePictureUrl);

                    current_user_db.setValue(newPost);

                }
                else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"User is already Registered", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
