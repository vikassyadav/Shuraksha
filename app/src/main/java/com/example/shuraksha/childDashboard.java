package com.example.shuraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shuraksha.ScreenTimeFeature.Adapter;
import com.example.shuraksha.ScreenTimeFeature.AppUsageStats;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


public class childDashboard extends AppCompatActivity {

    ImageView profile;
    Button allAppStats;
    CardView cardView1;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = "childDashboard";
    private int DEFAULT_UPDATE_INTERVAL = 2;
    private int FAST_UPDATE_INTERVAL = 2;
    public static int REQUEST_LOCATION_PERMISSION = 1;
    LocationRequest locationRequest;

    LocationCallback locationCallback;
    DatabaseReference databaseRef;
    FirebaseFirestore db;
    AppUsageStats appUsageStats = new AppUsageStats();



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_dashboard);
        allAppStats=findViewById(R.id.showAppStats);
        profile = findViewById(R.id.profile_icon);
        cardView1=findViewById(R.id.stats);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(childDashboard.this, Profile.class);
                startActivity(intent);
            }
        });
        allAppStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(childDashboard.this, AppUsageStats.class);
                startActivity(intent);
            }
        });

        fusedLocationProviderClient  = LocationServices.getFusedLocationProviderClient(childDashboard.this);
        databaseRef = FirebaseDatabase.getInstance().getReference("locations");

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        storeLocationInFirebase(location);
                    }
                }
            }
        };

        requestLocationPermission();

    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    private void storeLocationInFirebase(Location location) {
        String key = databaseRef.push().getKey();
        if (key != null) {
            databaseRef.child("USER_1").setValue(location)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Location data stored in Firebase", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error storing location data in Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stops sharing location when app is closed
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

}
