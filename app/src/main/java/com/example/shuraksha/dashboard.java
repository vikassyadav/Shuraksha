package com.example.shuraksha;

import static java.lang.Double.valueOf;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.shuraksha.MapsFeatures.MapsActivity;
import com.example.shuraksha.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class dashboard extends AppCompatActivity implements OnMapReadyCallback {

    TextView contolCenter;
    FloatingActionButton addChild;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    DatabaseReference databaseRef;
    FirebaseFirestore db;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        contolCenter=findViewById(R.id.tv_controlCenter);
        addChild=findViewById(R.id.btn_addChild);
//        pieChart=findViewById(R.id.pieChart);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        databaseRef = FirebaseDatabase.getInstance().getReference("locations/USER_1");

        contolCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Attach a listener to read the data
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get latitude and longitude from dataSnapshot
                  // Create a LatLng object
                LatLng location = new LatLng( (Double) dataSnapshot.child("latitude").getValue(), (Double)dataSnapshot.child("longitude").getValue());
//                mMap.getMyLocation();
                mMap.clear(); // Clear previous markers
                // Add a marker to the map
                mMap.addMarker(new MarkerOptions().position(location).title("Marker"));
                // Move camera to the marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15f));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(dashboard.this, "Got Error while Fetching Data " +databaseError, Toast.LENGTH_SHORT).show();
            }
        });
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

}