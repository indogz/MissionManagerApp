package com.example.matteo.app1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String address;
    private float zoomLevel = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        System.out.println("Maps Activity" + address);
        LatLng myAddressCoordinates = getLocationFromAddress(address);


        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(myAddressCoordinates).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myAddressCoordinates, zoomLevel));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //AUTOGENERATED
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            /**
             * From github:https://github.com/fabiomanfrin/Scuola/blob/master/CarFinder/app/src/main/java/fabiomanfrin/carfinder/Home.java
             */
            getPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);


        //mMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationFromAddress("Via Giovanni Gabrieli Mestre")));
    }


    private static final int PERMISSIONS_REQUEST = 1;

    public void getPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            }
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission granted");
            } else {
                System.out.println("Permission denied");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * This method convert an address to a LatLng object
     * Obviusly there are two main fields, Latitude and Logitued store as degree.
     * See more at: https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng
     *
     * @param strAddress
     * @return
     */
    public LatLng getLocationFromAddress(String strAddress) {

        /**
         * A class for handling geocoding and reverse geocoding.
         * Geocoding is the process of transforming a street address or other description of a location into a (latitude, longitude) coordinate.
         * Reverse geocoding is the process of transforming a (latitude, longitude) coordinate into a (partial) address.
         * The amount of detail in a reverse geocoded location description may vary,
         * for example one might contain the full street address of the closest building,
         * while another might contain only a city name and postal code.
         *
         * See more at: https://developer.android.com/reference/android/location/Geocoder.html
         */
        Geocoder coder = new Geocoder(MapsActivity.this, Locale.getDefault());

        //A list beacuse of getFromLocationName will return a list of address depending on how many result I want
        List<Address> address;
        LatLng p1 = null;

        try {
            /**
             * List<Address> getFromLocationName (String locationName, int maxResults)             *
             * | locationName | String: |           a user-supplied description of a location                      |
             * | maxResults	  |   int:  |max number of results to return. Smaller numbers (1 to 5) are recommended |
             * See more at: https://developer.android.com/reference/android/location/Geocoder.html
             */
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }            //I take just the first result even if I've got list of 5 different LAT;LNG results
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            return p1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
