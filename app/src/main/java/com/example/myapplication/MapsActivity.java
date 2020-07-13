package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.*;
import android.os.Bundle;
import android.location.Address.*;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public int count = 0;
    int index=-1;
    PolylineOptions polylineOptions = new PolylineOptions();
    private GoogleMap mMap;
    public int a = 65;
    Polyline polyline1=null;
    Polygon polygon = null;

    public List<LatLng> latLngList = new ArrayList<>(4);
    public List<Marker> markerList = new ArrayList<>(4);

    LocationManager locationManager;
    LocationListener locationListener;
    private Marker homeMarker;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng latLng) {

                char t = (char) a;
                String st = Character.toString((char) a);
                count++;
                if (count <= 4) {

                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
                    String address = getCompleteAddress(latLng.latitude, latLng.longitude);
                    String cityprov_ = cityProvince(latLng.latitude, latLng.longitude);
                    markerOptions.title(address);
                    markerOptions.snippet(cityprov_);
                    Marker marker = mMap.addMarker(markerOptions);

                    latLngList.add(latLng);
                    markerList.add(marker);
                    a++;
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(3);
                    polylineOptions.addAll(latLngList);
                    polyline1 = mMap.addPolyline(polylineOptions);

                    if (count == 4) {
                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.fillColor(Color.argb(47, 0, 255, 0));
                        polygonOptions.addAll(latLngList);

                        polygon = mMap.addPolygon(polygonOptions);
                    }

                }
            }


            private String getCompleteAddress(double LATITUDE, double LONGITUDE) {
                String str = "";
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");
                        strReturnedAddress.append(returnedAddress.getThoroughfare()).append(" " + returnedAddress.getSubThoroughfare()).append(" " + returnedAddress.getPostalCode());

                        str = strReturnedAddress.toString();
                        Log.w("My Currents", strReturnedAddress.toString());
                    } else {
                        Log.w("My Current", "No Address returned!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("My Currents", "Cannot get Address!");
                }
                return str;
            }

            private String cityProvince(double LATITUDE, double LONGITUDE) {
                String st = "";
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");
                        strReturnedAddress.append(returnedAddress.getAddressLine(0)).append(" " + returnedAddress.getAddressLine(2));
                        st = strReturnedAddress.toString();
                        Log.w("My Currents", strReturnedAddress.toString());
                    } else {
                        Log.w("My Current", "No Address returned!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("My Currents", "Cannot get Address!");
                }
                return st;
            }


        });
        
        /*mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng)
            {

                for(LatLng l: latLngList)
                {
                    if(Math.abs(l.latitude - latLng.latitude < 0.5))
                    {

                    }
                }

            }
        });*/
    }




    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

        /*Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeMarker(lastKnownLocation);*/
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
}
