package com.example.mithun.factly;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static MainActivity _activity;
    private AddressResultReceiver mResultReceiver;
    private LocationConnectionManager mLocationConnectionManager;

    private final static String MAIN_ACTIVITY_TAG = "MAIN_ACTIVITY";
    public final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mLocationConnectionManager = new LocationConnectionManager(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
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

    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    public boolean updateLocation() {
        if (!mLocationConnectionManager.isConnected()) return false;
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Can only get location if we have permission
        if (locationPermission == PackageManager.PERMISSION_GRANTED){
            TextView latText = (TextView)findViewById(R.id.latText);
            TextView lonText = (TextView)findViewById(R.id.lonText);
            Location loc = mLocationConnectionManager.getLastLocation();


            if (loc == null) return false;
            if (Geocoder.isPresent()) this.startIntentService(loc);
            latText.setText(String.valueOf(loc.getLatitude()));
            lonText.setText(String.valueOf(loc.getLongitude()));
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    this.updateLocation();
                else
                    Log.i(MAIN_ACTIVITY_TAG, "Didn't get permission to use location!");

            }
        }
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

    // Lint warning about lack of Creator, but it's not required. why?
    @SuppressLint("ParcelCreator")
    private class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String address = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            TextView zipCode = (TextView)MainActivity._activity.findViewById(R.id.zipText);
            zipCode.setText(address);
        }
    }
}
