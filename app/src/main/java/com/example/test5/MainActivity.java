package com.example.test5;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.GeoApiContext;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap myMap;
    private GeoApiContext geoApiContext;
    private LocationManager locationManager;
    private MapDrawer mapDrawer;
    private UIManager uiManager;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        locationManager = new LocationManager(this);

        if (!PermissionManager.checkFineLocationPermission(this)) {
            // Cerem permisiunea dacă nu este deja acordată
            PermissionManager.requestFineLocationPermission(this);
        } else {
            // Dacă permisiunea este deja acordată, inițializăm locația curentă
            initializeMapAndLocation();
        }

        setupUI();

        // Inițializarea butonului și setarea listenerului pentru click
        addButton = findViewById(R.id.add_button_main);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crearea intenției de a porni SplashActivity
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("MapStatePreferences", MODE_PRIVATE);

        if (myMap != null) {
            float savedLatitude = preferences.getFloat("camera_latitude", 0);
            float savedLongitude = preferences.getFloat("camera_longitude", 0);
            float savedZoomLevel = preferences.getFloat("zoom_level", 8);

            if (savedLatitude != 0 && savedLongitude != 0) {
                LatLng savedCameraPosition = new LatLng(savedLatitude, savedLongitude);
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savedCameraPosition, savedZoomLevel));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myMap != null) {
            LatLng currentCameraPosition = myMap.getCameraPosition().target;
            float currentZoomLevel = myMap.getCameraPosition().zoom;

            SharedPreferences preferences = getSharedPreferences("MapStatePreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat("camera_latitude", (float) currentCameraPosition.latitude);
            editor.putFloat("camera_longitude", (float) currentCameraPosition.longitude);
            editor.putFloat("zoom_level", currentZoomLevel);
            editor.apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setMyLocationButtonEnabled(true);
        checkLocationPermissionAndEnableMyLocation();
        myMap.setPadding(0, 400, 0, 200);

        applySavedMapStyle();

        geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.my_map_api_key)).build();
        mapDrawer = new MapDrawer(myMap, geoApiContext);

        uiManager = new UIManager(this, myMap, geoApiContext, locationManager, mapDrawer);

        // Obține locația curentă după ce harta este gata
        if (PermissionManager.checkFineLocationPermission(this)) {
            initializeMapAndLocation();
        }

        uiManager.setupUI();
    }

    private void checkLocationPermissionAndEnableMyLocation() {
        if (PermissionManager.checkFineLocationPermission(this)) {
            myMap.setMyLocationEnabled(true);
        } else {
            PermissionManager.requestFineLocationPermission(this);
        }
    }

    private void applySavedMapStyle() {
        SharedPreferences preferences = getSharedPreferences("MapPreferences", MODE_PRIVATE);
        int styleResId = preferences.getInt("selectedMapStyle", R.raw.night_mode_style); // Valoare default
        try {
            boolean success = myMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, styleResId));
            if (!success) {
                Toast.makeText(this, "Failed to apply map style.", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            Toast.makeText(this, "Map style not found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionManager.FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMapAndLocation();
                checkLocationPermissionAndEnableMyLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeMapAndLocation() {
        locationManager.getCurrentLocation(this, location -> {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapDrawer.addMarker(currentLatLng, "You are here", true);
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8), 2000, null);
        });
    }

    private void setupUI() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map fragment is null", Toast.LENGTH_SHORT).show();
        }
    }

}