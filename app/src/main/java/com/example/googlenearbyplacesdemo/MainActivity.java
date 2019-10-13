package com.example.googlenearbyplacesdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int LOCATION_REQUEST_CODE = 123;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

    }

    public void requestNearbyPlaces(View view){
        StringBuilder requestUrl = new StringBuilder();
        requestUrl.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        requestUrl.append("?location=" + currentLocation.getLatitude() +"," + currentLocation.getLongitude());
        requestUrl.append("&radius="+500);
        requestUrl.append("&keyword="+"restaurant");
        requestUrl.append("&key="+getResources().getString(R.string.api_key));

        GetNearbyPlaces nearbyPlaces = new GetNearbyPlaces();
        Object[] object = new Object[2];
        object[0] = gMap;
        object[1] = requestUrl.toString();
        nearbyPlaces.execute(object);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(this, "Current Location not Found", Toast.LENGTH_LONG).show();
        }else{
            currentLocation = location;
            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions mo = new MarkerOptions();
            mo.position(current).title("Current Location");
            gMap.addMarker(mo);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,15));
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest =  LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            return;
        }

        requestForLocationUpdates();


    }

    private void requestForLocationUpdates() {
        /*LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        }, getMainLooper()); */
        gMap.setMyLocationEnabled(true);  // to show the blue icon on top right corner for my location
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    requestForLocationUpdates();
                }
                break;
        }
    }
}
