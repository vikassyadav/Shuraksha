package com.example.shuraksha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.example.shuraksha.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivityLoginBinding binding;
    GoogleSignInClient signInClient;
    GoogleSignInOptions signInOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, signInOptions);


        binding.loginbtn.setOnClickListener(v -> {
            String email = binding.editTextTextEmailAddress3.getText().toString();
            String pass = binding.editTextTextPassword.getText().toString();

            if (!email.isEmpty() && !pass.isEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(login.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(login.this, ParentChildOpt.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(login.this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.dontHActbtn.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
        });


        binding.btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    @Override
    protected void onStart () {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (currentUser != null) {
            //showhomescreen
            startActivity(new Intent( this , ParentChildOpt.class));
            finish();
        }
        super.onStart();

    }


    void signIn(){
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent,100);
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //we are getting credential from google account and storing them in firebase
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Login Sucessfully!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext() , ParentChildOpt.class));  //parnt child opt aayega
                            finish();
                        } else {
                            Toast.makeText(login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            finish();//close the activity and delete from stack
                        }
                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }




}