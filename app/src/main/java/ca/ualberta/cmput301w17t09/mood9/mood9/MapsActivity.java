package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

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

        LinkedList<Mood> moodList = new LinkedList<Mood>((Collection<? extends Mood>) getIntent().getSerializableExtra("moodList"));
        for(int i = 0; i < moodList.size(); i++){
            Mood tempMood = moodList.get(i);
//            If both the long and lat don't equal 0 (the default) then create the mood on the map
            if(!(tempMood.getLatitude() == 0 & tempMood.getLongitude() == 0)){
                LatLng tempCord = new LatLng(tempMood.getLatitude(), tempMood.getLongitude());
                Mood9Application app = (Mood9Application) getApplication();
                EmotionModel em = app.getEmotionModel();
                Emotion emotion = em.getEmotion(tempMood.getEmotionId());
                String emotionName = emotion.getName();
                int iconNumber = getResources().getIdentifier(emotionName.toLowerCase().trim(), "drawable", getPackageName());
                Marker tempMarker = mMap.addMarker(new MarkerOptions().title(tempMood.getTrigger()).position(tempCord).icon(BitmapDescriptorFactory.fromBitmap(makeSmallerIcon(iconNumber))));
                markers.add(tempMarker);
            }
        }





        // Add a marker at tim hortons and move the camera
        LatLng timmiesCord = new LatLng(53.526599, -113.524596);
        Marker timmies = mMap.addMarker(new MarkerOptions().title("Tim Horton").position(timmiesCord).icon(BitmapDescriptorFactory.fromBitmap(makeSmallerIcon(R.drawable.anger))));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(timmiesCord, 10));


        LatLng test_cord = new LatLng(53.52611, -113.524596);
        Marker new_test = mMap.addMarker(new MarkerOptions().title("Test Marker").position(test_cord).icon(BitmapDescriptorFactory.fromBitmap(makeSmallerIcon(R.drawable.happiness))));

        markers.add(timmies);
        markers.add(new_test);

        //TODO Need to get the list of moods and if they have a location put it on the map.

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //TODO Instead of a toast, show the MOod that was created in this location.
                Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
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
        requestLocationUpdates();

    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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