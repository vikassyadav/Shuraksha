package com.example.shuraksha.ScreenTimeFeature;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import com.example.shuraksha.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AppUsageStats extends AppCompatActivity {
    RecyclerView recyclerView;
    private UsageStatsManager usageStatsManager;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage_stats);
        // Initialize Firestore


        recyclerView =findViewById(R.id.rvShow);

        // Initialize UsageStatsManager
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        // Find TextView to display usage stats
//        usageStatsTextView = (TextView) findViewById(R.id.usageStatsTextView);

        // Display usage stats
//        displayUsageStats();
        if (getGrantStatus()) {
            loadStatistics();
        } else {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }



    }

    /**
     * load the usage stats for last 24h
     */
    public void loadStatistics() {
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // Set the hour to 0 to get the start of the day
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();

        long endTime = System.currentTimeMillis(); // Set the end time to the current time

        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

//        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis() - 1000 * 3600 * 24, System.currentTimeMillis());



//        appList = appList.stream().filter(app -> app.getTotalTimeInForeground() > 0).collect(Collectors.toList());
        // Group the usageStats by application and sort them by total time in foregro
        Map<String, UsageStats> mySortedMap = null;
        if (appList.size() > 0) {
            mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : appList) {
                // Exclude apps with less than 1 minute of usage
                if (usageStats.getTotalTimeInForeground() > 60000) {
                    mySortedMap.put(usageStats.getPackageName(), usageStats);
                }
            }
            showAppsUsage(mySortedMap);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appUsageRef = db.collection("appUsage");


        for (UsageStats usageStats : appList) {

            try {
                if (usageStats.getTotalTimeInForeground() <= 60000) {
                    continue;
                }
                String packageName = usageStats.getPackageName();
                ApplicationInfo ai = getPackageManager().getApplicationInfo(packageName, 0);
                Drawable icon = getPackageManager().getApplicationIcon(ai);
                String appName = getPackageManager().getApplicationLabel(ai).toString();
                long totalTime = appList.stream().map(UsageStats::getTotalTimeInForeground).mapToLong(Long::longValue).sum();

                String usageDuration = getDurationBreakdown(usageStats.getTotalTimeInForeground());
                int usagePercentage = (int) (usageStats.getTotalTimeInForeground() * 100 / totalTime);

                // Convert drawable icon to Bitmap
                Bitmap bitmap = drawableToBitmap(icon);

                // Convert Bitmap to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // Convert byte array to Base64-encoded string
                String base64Image = Base64.encodeToString(imageData, Base64.DEFAULT);




                // Create a map to represent the app usage data
                Map<String, Object> appUsageData = new HashMap<>();
                appUsageData.put("appName", appName);
                appUsageData.put("usageDuration", usageDuration);
                appUsageData.put("usagePercentage", usagePercentage);
                appUsageData.put("appicon", base64Image);



                // Upload the app usage data to Firestore
                appUsageRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference());
                        }
                        batch.commit().addOnSuccessListener(aVoid -> {
                            // All existing documents deleted successfully
                            // Now add the new app usage data
                            appUsageRef.add(appUsageData)
                                    .addOnSuccessListener(documentReference -> {
                                        // Data successfully added to Firestore
                                        Toast.makeText(this, "Data uploaded", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle any errors
                                        Toast.makeText(this, "Failed uploaded", Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            // Handle any errors
                            Toast.makeText(this, "Failed to clear previous data", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // Handle the error
                        Toast.makeText(this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void showAppsUsage(Map<String, UsageStats> mySortedMap) {
        //public void showAppsUsage(List<UsageStats> usageStatsList) {
        ArrayList<App> appsList = new ArrayList<>();
        List<UsageStats> usageStatsList = new ArrayList<>(mySortedMap.values());

        // sort the applications by time spent in foreground
        Collections.sort(usageStatsList, (z1, z2) -> Long.compare(z1.getTotalTimeInForeground(), z2.getTotalTimeInForeground()));

        // get total time of apps usage to calculate the usagePercentage for each app
        long totalTime = usageStatsList.stream().map(UsageStats::getTotalTimeInForeground).mapToLong(Long::longValue).sum();

        //fill the appsList
        for (UsageStats usageStats : usageStatsList) {
            try {
                String packageName = usageStats.getPackageName();
                Drawable icon = getDrawable(R.drawable.no_image);
                String[] packageNames = packageName.split("\\.");
                String appName = packageNames[packageNames.length-1].trim();


                if(isAppInfoAvailable(usageStats)){
                    ApplicationInfo ai = getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
                    icon = getApplicationContext().getPackageManager().getApplicationIcon(ai);
                    appName = getApplicationContext().getPackageManager().getApplicationLabel(ai).toString();
                }

                String usageDuration = getDurationBreakdown(usageStats.getTotalTimeInForeground());
                int usagePercentage = (int) (usageStats.getTotalTimeInForeground() * 100 / totalTime);

                App usageStatDTO = new App(icon, appName, usagePercentage, usageDuration);
                appsList.add(usageStatDTO);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }



        }




        // reverse the list to get most usage first
        Collections.reverse(appsList);
        // build the adapter
        Adapter adapter = new Adapter(this, appsList);
        // attach the adapter to a ListView
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    /**
     * check if PACKAGE_USAGE_STATS permission is aloowed for this application
     * @return true if permission granted
     */
    public boolean getGrantStatus() {
        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            return (mode == MODE_ALLOWED);
        }
    }

    /**
     * check if the application info is still existing in the device / otherwise it's not possible to show app detail
     * @return true if application info is available
     */
    private boolean isAppInfoAvailable(UsageStats usageStats) {
        try {
            getApplicationContext().getPackageManager().getApplicationInfo(usageStats.getPackageName(), 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    /**
     * helper method to get string in format hh:mm:ss from miliseconds
     *
     * @param millis (application time in foreground)
     * @return string in format hh:mm:ss from miliseconds
     */
    private String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return (hours + " : " +  minutes + " : " + seconds );
//        return (hours + " h " +  minutes + " m " + seconds + " s");

    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}