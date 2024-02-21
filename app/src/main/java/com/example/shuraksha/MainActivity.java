package com.example.shuraksha;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.shuraksha.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.units.qual.A;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding  binding;

    DrawerLayout drawerLayout ;
    NavigationView navigationView;
    Toolbar toolbar ;
    ActionBarDrawerToggle  toggle;

    GoogleSignInAccount account;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //step 1 first setup toolbar
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigatioView);
        toolbar = findViewById(R.id.toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //when any feature is open drawer will get close
//        drawerLayout.closeDrawer(GravityCompat.START);






    }
    @Override
    //check if drawer is open then when backpressed first drawer will get backed
    public void onBackPressed() {
//        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed(); //if there is nothing(stack) to get back then home screen apper app is closed
        }
    }

}


//logout code
//        binding.logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                  //siginout method
//                    FirebaseAuth.getInstance().signOut();
//
//                    Intent intent = new Intent(getApplicationContext(), login.class);
//                    startActivity(intent);
//                    finish();
//
//
//            }
//        });