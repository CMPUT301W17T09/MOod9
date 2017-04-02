package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
/**
 * Location Service class for getting the current location
 * <p>
 * Created by cdkushni on 3/13/17.
 * code referenced from http://clover.studio/2016/08/09/getting-current-location-in-android-using-location-manager/
 * </p>
 */

public class LocationService implements LocationListener {
    private final Context mContext;

    private boolean canGetLocation = false;

    private Location loc;
    private double latitude;
    private double longitude;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private LocationManager locationManager;

    LocationService(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    /**
     * Verifies permissions and gets location by either network or gps depending on availability
     * @return found location
     */
    private Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                Toast.makeText(mContext, "No Service Provider Available", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (checkNetwork) {

                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }

                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                            Toast.makeText(mContext,"Network Longitude: "+longitude+" Latitude: "+latitude,Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(SecurityException e){
                        return loc;
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (checkGPS) {
                if (loc == null) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                                Toast.makeText(mContext,"GPS Longitude: "+longitude+" Latitude: "+latitude,Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (SecurityException e) {
                        getLocation();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return loc;
    }

    double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
