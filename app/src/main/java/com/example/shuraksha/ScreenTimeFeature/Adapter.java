package com.example.shuraksha.ScreenTimeFeature;

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

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.myViewHolder> {
    Context context;
    private ArrayList<App> mUsageStatDTOArrayList;

    public Adapter(Context context, ArrayList<App> mUsageStatDTOArrayList) {
        this.context = context;
        this.mUsageStatDTOArrayList = mUsageStatDTOArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_row,parent,false);//f
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        App usageStats = mUsageStatDTOArrayList.get(position);

        holder.appName.setText(usageStats.appName);
        holder.UasgeDuration.setText(usageStats.usageDuration);
        holder.usagePercent.setText(usageStats.usagePercentage + "%");
        holder.icon_img.setImageDrawable(usageStats.appIcon);
        holder.progressBar.setProgress(usageStats.usagePercentage);
    }

    @Override
    public int getItemCount() {
        return mUsageStatDTOArrayList.size();
    }

    public class  myViewHolder extends RecyclerView.ViewHolder{
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
