package com.example.shuraksha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class ParentChildOpt extends AppCompatActivity {
    CardView cardView1 ,cardView2 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_opt);

        cardView1 = findViewById(R.id.parentCard);
        cardView2= findViewById(R.id.childCard);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent iHome = new Intent(ParentChildOpt.this , dashboard.class);
                        startActivity(iHome);
                        finish();//poping from stack so is activity can't get open even after pressing back button

                    }
                },1000);

            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent iHome = new Intent(ParentChildOpt.this , childDashboard.class);
                        startActivity(iHome);
                        finish();//poping from stack so is activity can't get open even after pressing back button

                    }
                },1000);

            }
        });
    }

}