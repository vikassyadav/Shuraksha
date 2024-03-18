package com.example.shuraksha.ScreenTimeFeature.fetchFromFirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.shuraksha.R;


public class liveScreenUsageStats extends AppCompatActivity {
    RecyclerView fireStoreRecyclerView;
    fireStoreRvAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_screen_usage_stats);
        fireStoreRecyclerView.findViewById(R.id.rvfireStore);


    }
}