package com.example.mithun.factly;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by mithun on 3/26/16.
 */
public class LocationConnectionManager
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private final static String LOCATION_MANAGER_TAG = "LOCATION_MANAGER";

    public LocationConnectionManager(Activity activity) {
        // Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }
    }

    // GOOGLE API CLIENT METHODS
    @Override
    public void onConnectionSuspended(int i) {Log.i(LOCATION_MANAGER_TAG, "Connection suspended");}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {Log.e(LOCATION_MANAGER_TAG, "Connection failed");}

    @Override
    public void onConnected(Bundle connectionHint) {Log.i(LOCATION_MANAGER_TAG, "Connection successful"); }

    // HELPER METHODS

    public boolean isConnected() {return mGoogleApiClient.isConnected();}
    public void connect() {mGoogleApiClient.connect();}
    public void disconnect() {mGoogleApiClient.disconnect();}

    public Location getLastLocation() throws SecurityException {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i(LOCATION_MANAGER_TAG, String.valueOf(mLastLocation.getLatitude()));
            Log.i(LOCATION_MANAGER_TAG, String.valueOf(mLastLocation.getLongitude()));
        }
        return mLastLocation;
    }
}
