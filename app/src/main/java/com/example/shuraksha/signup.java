package com.example.shuraksha;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shuraksha.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;


public class signup extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.registerbtn.setOnClickListener(v -> {
            String email = binding.etSEmail.getText().toString();
            String pass = binding.etSPass.getText().toString();
            String confirmPass = binding.etConPass.getText().toString();

            if (!email.isEmpty() && !pass.isEmpty() && !confirmPass.isEmpty()) {
//                if (pass.length() >= 6) {
                if (pass.equals(confirmPass)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(signup.this, task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(signup.this, login.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(signup.this, "Minimum 6 character password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(signup.this, "Password is not matching", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(signup.this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.haveAccountLogin.setOnClickListener(v -> {
            Intent intent = new Intent(signup.this, signup.class);
            startActivity(intent);
        });
    }
}
