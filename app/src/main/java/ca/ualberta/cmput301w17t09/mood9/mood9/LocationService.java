package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by cdkushni on 3/13/17.
 * code referenced from http://stackoverflow.com/questions/17591147/how-to-get-current-location-in-android
 */

public class LocationService implements LocationListener {

    private final Context mContext;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for status
    boolean canGetLocation = false;

    Location location; // location
    double latitude = 100.0; // latitude
    double longitude = 100.0; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // meters

    private static final long MIN_TIME_BW_UPDATES = 100; // milliseconds

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public LocationService(Context context) {
        this.mContext = context;
        getLocation();
    }

    public void setNetworkEnabled(boolean networkEnabled) {
        isNetworkEnabled = networkEnabled;
    }

    /**
     * Function to get the user's current location
     *
     * @return
     */
    public Location getLocation() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(Context.LOCATION_SERVICE);

                // getting network status
                //isNetworkEnabled = locationManager
                        //.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

                if (isNetworkEnabled == false) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        location=null;
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return location;
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check wifi enabled
     *
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


}
