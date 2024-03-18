package com.example.shuraksha.ScreenTimeFeature.fetchFromFirebase;

public class fireStoreModel {
    private String packageName;
    private long totalTimeInForeground;

    public fireStoreModel() {
    }

    public fireStoreModel(String packageName, long totalTimeInForeground) {
        this.packageName = packageName;
        this.totalTimeInForeground = totalTimeInForeground;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }
}
