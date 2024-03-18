package com.example.shuraksha.ScreenTimeFeature.fetchFromFirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shuraksha.R;
import com.example.shuraksha.ScreenTimeFeature.Adapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class fireStoreRvAdapter extends FirestoreRecyclerAdapter<fireStoreModel, fireStoreRvAdapter.myViewHolder> {
    Context context;

    public fireStoreRvAdapter(@NonNull FirestoreRecyclerOptions<fireStoreModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull fireStoreModel model) {

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_row,parent,false);//f
        return new fireStoreRvAdapter.myViewHolder(view);


    }


    class myViewHolder extends RecyclerView.ViewHolder{
        TextView appName, UasgeDuration, usagePercent;
        ProgressBar progressBar;
        ImageView icon_img;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            appName =itemView.findViewById(R.id.app_name_tv);
            UasgeDuration=itemView.findViewById(R.id.usage_duration_tv);
            usagePercent=itemView.findViewById(R.id.usage_perc_tv);
            icon_img = itemView.findViewById(R.id.icon_img);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }
}
