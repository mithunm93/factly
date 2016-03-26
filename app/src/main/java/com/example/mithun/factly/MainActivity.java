package com.example.mithun.factly;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static MainActivity _activity;
    private final static String MAIN_ACTIVITY_TAG = "MAIN_ACTIVITY";
    public final static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private LocationConnectionManager mLocationConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationConnectionManager = new LocationConnectionManager(this);
        _activity = this;
        this.beginLocationTracking();
    }

    //I'm making a new comment right here
    @Override
    protected void onStart() {
        mLocationConnectionManager.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mLocationConnectionManager.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    this.updateLocation();
                else
                    Log.i(MAIN_ACTIVITY_TAG, "Didn't get permission to use location!");

            }
        }
    }

    public boolean updateLocation() {
        if (!mLocationConnectionManager.isConnected()) return false;
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Can only get location if we have permission
        if (locationPermission == PackageManager.PERMISSION_GRANTED){
            TextView latText = (TextView)findViewById(R.id.latText);
            TextView lonText = (TextView)findViewById(R.id.lonText);
            Location loc = mLocationConnectionManager.getLastLocation();

            if (loc == null) return false;
            latText.setText(String.valueOf(loc.getLatitude()));
            lonText.setText(String.valueOf(loc.getLongitude()));
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }

        return true;
    }

    // Maybe use AsyncTask instead?
    private void beginLocationTracking() {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                int delay = MainActivity._activity.updateLocation() ? 20000 : 1000;
                h.postDelayed(this, delay);
            }
        }, 0);
    }

}
