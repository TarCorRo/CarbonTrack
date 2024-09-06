package com.example.test5;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.DirectionsApi;
import com.google.maps.model.DirectionsResult;

import android.util.Log;
import com.google.maps.model.Duration;
import com.google.maps.model.Distance;

import java.io.IOException;
import java.util.List;

public class MapDrawer {
    private final GoogleMap myMap;
    private final GeoApiContext geoApiContext;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Polyline currentPolyline;

    public MapDrawer(GoogleMap myMap, GeoApiContext geoApiContext) {
        this.myMap = myMap;
        this.geoApiContext = geoApiContext;
    }

    public void addMarker(LatLng latLng, String title, boolean isCurrentLocation) {
        if (isCurrentLocation) {
            if (sourceMarker != null) {
                sourceMarker.remove();
            }
            sourceMarker = myMap.addMarker(new MarkerOptions().position(latLng).title(title));
        } else {
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
            destinationMarker = myMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DirectionsResult drawRoute(Context context, LatLng origin, LatLng destination) {
        // Remove existing polyline
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .units(com.google.maps.model.Unit.METRIC) // Adăugați această linie pentru a seta unitățile la metric
                .awaitIgnoreError();

        if (result != null && result.routes.length > 0) {
            List<com.google.maps.model.LatLng> path = result.routes[0].overviewPolyline.decodePath();
            PolylineOptions polylineOptions = new PolylineOptions().color(0xFF00FF00);

            for (com.google.maps.model.LatLng point : path) {
                polylineOptions.add(new LatLng(point.lat, point.lng));
            }

            currentPolyline = myMap.addPolyline(polylineOptions);

            // Move camera to show both markers with animation
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(destination);
            LatLngBounds bounds = builder.build();
            myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000, null);

            // Extrage distanța și durata
            Distance distance = result.routes[0].legs[0].distance;
            Duration duration = result.routes[0].legs[0].duration;

            // Afișează distanța și durata în terminal
            System.out.println("Distance: " + distance.humanReadable);
            System.out.println("Duration: " + duration.humanReadable);

            // Alternativ, poți folosi Log pentru a afișa în Logcat
            Log.d("MapDrawer", "Distance: " + distance.humanReadable);
            Log.d("MapDrawer", "Duration: " + duration.humanReadable);
        }
        return result;
    }

    public void clearRoute() {
        if (sourceMarker != null) {
            sourceMarker.remove();
            sourceMarker = null;
        }
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;
        }
    }

}
