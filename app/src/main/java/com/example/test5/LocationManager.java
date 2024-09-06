package com.example.test5;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationManager {
    private final FusedLocationProviderClient fusedLocationProviderClient;

    public LocationManager(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(Context context, final OnSuccessListener<Location> listener) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionManager.FINE_PERMISSION_CODE);
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    listener.onSuccess(location);
                }
                fusedLocationProviderClient.removeLocationUpdates(this);
            }
        }, Looper.getMainLooper());
    }

    public interface OnSuccessListener<T> {
        void onSuccess(T result);
    }
}
