package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Provides a google maps view so that the user can see moods that are near them.
 * The icons on the map are clickable and allows the user to see the specific mood when clicked.
 * @author CMPUT301W17T09
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private static final int REQUEST_PERMISSION_FINE = 0;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private ArrayList<Marker> markers = new ArrayList<>();
    private LatLng myCord = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    // Code based off of http://stackoverflow.com/questions/34582370/how-can-i-show-current-location-on-a-google-map-on-android-marshmallow/34582595#34582595
    // Found on March. 5, 2017
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();
                Intent viewMood = new Intent(MapsActivity.this, MoodViewActivity.class);
                viewMood.putExtra("moodIndex", Integer.valueOf(marker.getTitle()));
                startActivity(viewMood);
                return false;
            }
        });



        //Check for permissions to use phone's location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermission();
        }

    }

    private void makeMarkers() {
        LinkedList<Mood> moodList = new LinkedList<Mood>((Collection<? extends Mood>) getIntent().getSerializableExtra("moodList"));
        for(int i = 0; i < moodList.size(); i++){
            Mood tempMood = moodList.get(i);
//            If both the long and lat don't equal 0 (the default) then create the mood on the map
            if(!(tempMood.getLatitude() == null | tempMood.getLongitude() == null)) {
                if (!(tempMood.getLatitude() == 0 & tempMood.getLongitude() == 0)) {
                    LatLng tempCord = new LatLng(tempMood.getLatitude(), tempMood.getLongitude());
                    System.out.println(computeDistance(myCord, tempCord));
                    if(computeDistance(myCord, tempCord) <= 5.0){
                        Mood9Application app = (Mood9Application) getApplication();
                        EmotionModel em = app.getEmotionModel();
                        Emotion emotion = em.getEmotion(tempMood.getEmotionId());
                        String emotionName = emotion.getName();
                        int iconNumber = getResources().getIdentifier(emotionName.toLowerCase().trim(), "drawable", getPackageName());
                        Marker tempMarker = mMap.addMarker(new MarkerOptions().title(Integer.toString(i)).position(tempCord).icon(BitmapDescriptorFactory.fromBitmap(makeSmallerIcon(iconNumber))));
                        markers.add(tempMarker);
                    }
                }
            }
        }
    }

    //Three following functions found on http://www.geodatasource.com/developers/java
    //March 30, 2017
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private static double computeDistance(LatLng myCord, LatLng theirCord) {
        double lon1 = myCord.longitude;
        double lon2 = theirCord.longitude;
        double lat1 = myCord.latitude;
        double lat2 = theirCord.latitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1.609344);
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINE);
            } else {
                //Request the location permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_FINE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission was granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    //Permission denied. Close the Map Activity
                    finish();
                }
                return;
            }
        }
    }


    /**
     * Makes the icon smaller so it fits on the map properly
     */
    Bitmap makeSmallerIcon(int icon) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getDrawable(icon);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap newIcon = Bitmap.createScaledBitmap(bitmap, 55, 55, false);
        return newIcon;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Double myLong = location.getLongitude();
        Double myLat = location.getLatitude();
        LatLng newCord = new LatLng(myLat, myLong);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCord, 10));
        myCord = newCord;
        makeMarkers();
        System.out.println(myCord);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        makeMarkers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()){
            makeMarkers();
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
}