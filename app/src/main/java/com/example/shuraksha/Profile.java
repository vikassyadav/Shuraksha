package com.example.shuraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Intent;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity {

    GoogleSignInAccount account;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;
    FirebaseAuth auth;
    TextView userName, userEmail, logout;
    ImageView profileDp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        logout=findViewById(R.id.logout);
        profileDp=findViewById(R.id.profile_dp);

        initvar();
    }

    private void initvar() {
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            userName.setText(account.getDisplayName());
            userEmail.setText(account.getEmail());
            if (account.getPhotoUrl() != null) {
                Glide.with(this).load(account.getPhotoUrl()).into(profileDp);
            } else {
                // Handle the case when the photo URL is null
            }
        } else {
            // Handle the case when no account is signed in
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("No Account Signed In");
//            builder.setMessage("Please sign in to view your profile.");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // Close the dialog or navigate to the sign-in screen
//                    dialog.dismiss();
//                    // Example: Navigate to the sign-in screen
//                    Intent intent = new Intent(Profile.this, login.class);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            builder.show();
            userName.setText("Shuraksha");
            userEmail.setText("Shuraksha.care@gmail.com");
        }

        logoutUser();
    }

    private void logoutUser() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                signInClient = GoogleSignIn.getClient(Profile.this, signInOptions);

                new AlertDialog.Builder(Profile.this)
                        .setTitle("LogOut")
                        .setMessage("Are you sure to logout from app??")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();//logout from firebase

                                signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>(){

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        startActivity(new Intent(Profile.this, login.class));
                                        finish();
                                    }
                                });
                            }
                        }).show();
            }
        });
    }
}