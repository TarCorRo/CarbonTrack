package com.example.test5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

public class UIManager {
    private Activity activity;
    private LocationManager locationManager;
    private MapDrawer mapDrawer;
    private GoogleMap myMap;
    private GeoApiContext geoApiContext;
    private SearchView mapSearchView, destinationSearchView;
    private ListView suggestionsList;
    private Button buttonGo;
    private Button showLocation;
    private GridLayout gridLayout, gridSettings;
    private BottomNavigationView bottomNavigationView;
    private Button gridCar1,gridCar2,gridCar3,delGridCar1,delGridCar2,delGridCar3;
    private Button gridSet1,gridSet2,gridSet3,gridSet4,gridSet5;
    private PreferenceManager preferenceManager;
    private Button buttonClearRoute;
    private Button closeCards;
    private Button themeDefault, themeDark, themeRetro, themeStandard;
    private CardView cardInfoApp,cardCo2,cardInfoMath,cardChangeTheme,cardLoading;

    private LatLng sourceLatLng;
    private LatLng destinationLatLng;
    private LatLng currentLocationLatLng;

    private static final long LONG_PRESS_DURATION = 2000; // Durata în milisecunde pentru un "long press"
    private Handler handler = new Handler();
    private Runnable longPressRunnable;

    private boolean hasActiveRoute = false;

    public UIManager(Activity activity, GoogleMap myMap, GeoApiContext geoApiContext, LocationManager locationManager, MapDrawer mapDrawer) {
        this.activity = activity;
        this.myMap = myMap;
        this.geoApiContext = geoApiContext;
        this.locationManager = locationManager;
        this.mapDrawer = mapDrawer;
        this.preferenceManager = PreferenceManager.getInstance(activity); // Inițializează PreferenceManager
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI() {
        mapSearchView = activity.findViewById(R.id.mapSearch);
        destinationSearchView = activity.findViewById(R.id.destinationSearch);
        suggestionsList = activity.findViewById(R.id.suggestions_list);
        buttonGo = activity.findViewById(R.id.button_go);
        showLocation = activity.findViewById(R.id.button_show_location);
        bottomNavigationView = activity.findViewById(R.id.bottom_navigation_view);
        buttonClearRoute = activity.findViewById(R.id.button_clear_route);

        gridLayout = activity.findViewById(R.id.grid_car);
        gridCar1 = activity.findViewById(R.id.car_grid_1);
        gridCar2 = activity.findViewById(R.id.car_grid_2);
        gridCar3 = activity.findViewById(R.id.car_grid_3);
        delGridCar1 = activity.findViewById(R.id.car_del_1);
        delGridCar2 = activity.findViewById(R.id.car_del_2);
        delGridCar3 = activity.findViewById(R.id.car_del_3);

        gridSettings = activity.findViewById(R.id.grid_settings);
        //Info about app
        gridSet1 = activity.findViewById(R.id.car_set_1);
        //Tips to reduce co2
        gridSet2 = activity.findViewById(R.id.car_set_2);
        //Info about math behind
        gridSet3 = activity.findViewById(R.id.car_set_3);
        //Change map theme
        gridSet4 = activity.findViewById(R.id.car_set_4);
        //Clear all car data
        gridSet5 = activity.findViewById(R.id.car_set_5);

        closeCards = activity.findViewById(R.id.close_setting_cards);

        themeDefault = activity.findViewById(R.id.theme1);
        themeDark = activity.findViewById(R.id.theme2);
        themeRetro = activity.findViewById(R.id.theme3);
        themeStandard = activity.findViewById(R.id.theme4);


        cardInfoApp = activity.findViewById(R.id.cardInfoApp);
        cardCo2 = activity.findViewById(R.id.cardCo2);
        cardInfoMath = activity.findViewById(R.id.cardInfoMath);
        cardChangeTheme = activity.findViewById(R.id.cardChangeTheme);
        cardLoading = activity.findViewById(R.id.cardloading);

        closeCards.setOnClickListener(v->{
            if(View.VISIBLE == cardInfoApp.getVisibility())
            {
                cardInfoApp.setVisibility(View.GONE);
                closeCards.setVisibility(View.GONE);
                gridSettings.setVisibility(View.VISIBLE);
            }
            else if(View.VISIBLE == cardCo2.getVisibility())
            {
                cardCo2.setVisibility(View.GONE);
                closeCards.setVisibility(View.GONE);
                gridSettings.setVisibility(View.VISIBLE);
            }
            else if (View.VISIBLE == cardInfoMath.getVisibility())
            {
                cardInfoMath.setVisibility(View.GONE);
                closeCards.setVisibility(View.GONE);
                gridSettings.setVisibility(View.VISIBLE);
            }else if (View.VISIBLE == cardChangeTheme.getVisibility())
            {
                cardChangeTheme.setVisibility(View.GONE);
                closeCards.setVisibility(View.GONE);
                gridSettings.setVisibility(View.VISIBLE);
            }
        });

        gridSet1.setOnClickListener(v->{
            if(View.GONE == cardInfoApp.getVisibility())
            {
                cardInfoApp.setVisibility(View.VISIBLE);
                closeCards.setVisibility(View.VISIBLE);
                gridSettings.setVisibility(View.GONE);
            }
        });

        gridSet2.setOnClickListener(v->{
            if(View.GONE == cardCo2.getVisibility())
            {
                cardCo2.setVisibility(View.VISIBLE);
                closeCards.setVisibility(View.VISIBLE);
                gridSettings.setVisibility(View.GONE);
            }
        });

        gridSet3.setOnClickListener(v->{
            if(View.GONE == cardInfoMath.getVisibility())
            {
                cardInfoMath.setVisibility(View.VISIBLE);
                closeCards.setVisibility(View.VISIBLE);
                gridSettings.setVisibility(View.GONE);
            }
        });

        gridSet4.setOnClickListener(v->{
            if(View.GONE == cardChangeTheme.getVisibility())
            {
                cardChangeTheme.setVisibility(View.VISIBLE);
                closeCards.setVisibility(View.VISIBLE);
                gridSettings.setVisibility(View.GONE);
            }
        });

        gridSet5.setOnClickListener(v->{
            saveButtonText(1, "NULL", "NULL", 0);
            saveButtonText(2, "NULL", "NULL", 0);
            saveButtonText(3, "NULL", "NULL", 0);
            loadButtonTexts();
            Toast.makeText(v.getContext(), "All car data has been erased", Toast.LENGTH_SHORT).show();
        });


        setupButtonListeners();
        setupDeleteButtonListeners();
        loadButtonTexts();

        SearchViewManager searchViewManager = new SearchViewManager(activity, suggestionsList);
        searchViewManager.setUpSearchView(mapSearchView);
        searchViewManager.setUpSearchView(destinationSearchView);

        buttonClearRoute.setOnClickListener(v -> {
            mapDrawer.clearRoute();
            hasActiveRoute = false; // Se resetează starea drumului activ
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Închide toate meniurile deschise înainte de a deschide un nou meniu, cu excepția celui selectat
            if (itemId != R.id.bottom_navigation_car && gridLayout.getVisibility() == View.VISIBLE) {
                gridLayout.setVisibility(View.GONE);
            }
            if (itemId != R.id.bottom_navigation_settings && gridSettings.getVisibility() == View.VISIBLE) {
                gridSettings.setVisibility(View.GONE);
            }

            // Acum deschide meniul specific dacă este necesar
            if (itemId == R.id.bottom_navigation_car) {
                // Dacă grila este deja vizibilă, ascunde-o
                if (gridLayout.getVisibility() == View.VISIBLE) {
                    gridLayout.setVisibility(View.GONE);
                } else {
                    gridLayout.setVisibility(View.VISIBLE);
                }
                return true;

            } else if (itemId == R.id.bottom_navigation_home) {
                // Logica pentru butonul "Home"
                if (hasActiveRoute && sourceLatLng != null && destinationLatLng != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(sourceLatLng);
                    builder.include(destinationLatLng);
                    LatLngBounds bounds = builder.build();
                    myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000, null);
                } else if (currentLocationLatLng != null) {
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 8), 2000, null);
                } else {
                    Toast.makeText(activity, "No location data available.", Toast.LENGTH_SHORT).show();
                }
                return true;

        } else if (itemId == R.id.bottom_navigation_settings) {
                // Dacă grila de setări este deja vizibilă, ascunde-o
                if (gridSettings.getVisibility() == View.VISIBLE) {
                    gridSettings.setVisibility(View.GONE);
                } else {
                    gridSettings.setVisibility(View.VISIBLE);
                }
                return true;
            }

            return false;
        });



        buttonGo.setOnClickListener(v -> {
            // Disable the "Go" button and show the loading card
            buttonGo.setEnabled(false);
            cardLoading.setVisibility(View.VISIBLE);

            String source = mapSearchView.getQuery().toString();
            String destination = destinationSearchView.getQuery().toString();

            if (source.equalsIgnoreCase("Current Location")) {
                locationManager.getCurrentLocation(activity, location -> {
                    sourceLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    destinationLatLng = mapDrawer.getLocationFromAddress(activity, destination);
                    if (destinationLatLng != null) {
                        mapDrawer.addMarker(sourceLatLng, "Current Location", true);
                        mapDrawer.addMarker(destinationLatLng, "Destination", false);
                        DirectionsResult result = mapDrawer.drawRoute(activity, sourceLatLng, destinationLatLng);
                        showDistanceAndTime(result);

                        // Set route as active
                        hasActiveRoute = true;

                        // Re-enable the "Go" button and hide the loading card
                        buttonGo.setEnabled(true);
                        cardLoading.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(activity, "Invalid destination", Toast.LENGTH_SHORT).show();

                        // Re-enable the "Go" button and hide the loading card
                        buttonGo.setEnabled(true);
                        cardLoading.setVisibility(View.GONE);
                    }
                });
            } else {
                sourceLatLng = mapDrawer.getLocationFromAddress(activity, source);
                destinationLatLng = mapDrawer.getLocationFromAddress(activity, destination);
                if (sourceLatLng != null && destinationLatLng != null) {
                    mapDrawer.addMarker(sourceLatLng, "Source", true);
                    mapDrawer.addMarker(destinationLatLng, "Destination", false);
                    DirectionsResult result = mapDrawer.drawRoute(activity, sourceLatLng, destinationLatLng);
                    showDistanceAndTime(result);

                    // Set route as active
                    hasActiveRoute = true;

                    // Re-enable the "Go" button and hide the loading card
                    buttonGo.setEnabled(true);
                    cardLoading.setVisibility(View.GONE);
                } else {
                    Toast.makeText(activity, "Invalid source or destination", Toast.LENGTH_SHORT).show();

                    // Re-enable the "Go" button and hide the loading card
                    buttonGo.setEnabled(true);
                    cardLoading.setVisibility(View.GONE);
                }
            }

            // Hide the suggestions list
            suggestionsList.setVisibility(View.GONE);
        });


// Logic for the "Show Location" button
        showLocation.setOnClickListener(v -> {
            locationManager.getCurrentLocation(activity, location -> {
                currentLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mapSearchView.setQuery("Current Location", true);
                mapDrawer.addMarker(currentLocationLatLng, "You are here", true);
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 8), 2000, null);
            });
        });


        setupThemeButtons();
        loadSavedMapTheme();
    }

    private void setupThemeButtons() {
        themeDefault.setOnClickListener(v -> {
            setMapStyle(R.raw.night_mode_style);
            saveMapTheme("default");
        });
        themeDark.setOnClickListener(v -> {
            setMapStyle(R.raw.dark);
            saveMapTheme("dark");
        });
        themeRetro.setOnClickListener(v -> {
            setMapStyle(R.raw.retro);
            saveMapTheme("retro");
        });
        themeStandard.setOnClickListener(v -> {
            setMapStyle(R.raw.standard);
            saveMapTheme("standard");
        });
    }

    private void loadSavedMapTheme() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MapPreferences", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("selectedMapTheme", "default");

        switch (theme) {
            case "dark":
                setMapStyle(R.raw.dark);
                break;
            case "retro":
                setMapStyle(R.raw.retro);
                break;
            case "standard":
                setMapStyle(R.raw.standard);
                break;
            case "default":
            default:
                setMapStyle(R.raw.night_mode_style);
                break;
        }
    }

    private void saveMapTheme(String theme) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MapPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("selectedMapTheme", theme).apply();
    }


    private void setMapStyle(int styleResId) {
        try {
            boolean success = myMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(activity, styleResId)
            );

            if (!success) {
                Log.e("MapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapStyle", "Can't find style. Error: ", e);
        }
    }

    private void setupDeleteButtonListeners() {
        delGridCar1.setOnClickListener(v -> {
            gridCar1.setText("DELETED");
            new android.os.Handler().postDelayed(() -> {
                gridCar1.setText("NULL");
                saveButtonText(1, "NULL", "NULL", 0);
            }, 1000);
        });

        delGridCar2.setOnClickListener(v -> {
            gridCar2.setText("DELETED");
            new android.os.Handler().postDelayed(() -> {
                gridCar2.setText("NULL");
                saveButtonText(2, "NULL", "NULL", 0);
            }, 1000);
        });

        delGridCar3.setOnClickListener(v -> {
            gridCar3.setText("DELETED");
            new android.os.Handler().postDelayed(() -> {
                gridCar3.setText("NULL");
                saveButtonText(3, "NULL", "NULL", 0);
            }, 1000);
        });
    }

    private void saveButtonText(int index, String modelName, String co2Emissions, double combined) {
        preferenceManager.setModelName(index, modelName);
        preferenceManager.setCo2Emissions(index, co2Emissions);
        preferenceManager.setCombined(index, combined);
    }

    private void loadButtonTexts() {
        String model1 = preferenceManager.getModelName(1);
        String model2 = preferenceManager.getModelName(2);
        String model3 = preferenceManager.getModelName(3);

        gridCar1.setText(model1);
        gridCar2.setText(model2);
        gridCar3.setText(model3);

        String co2Emissions1 = preferenceManager.getCo2Emissions(1);
        String co2Emissions2 = preferenceManager.getCo2Emissions(2);
        String co2Emissions3 = preferenceManager.getCo2Emissions(3);

        double combined1 = preferenceManager.getCombined(1);
        double combined2 = preferenceManager.getCombined(2);
        double combined3 = preferenceManager.getCombined(3);

        Log.d("MainActivity", "Button 1 Model: " + model1 + ", CO2 Emissions: " + co2Emissions1 + ", Combined: " + combined1);
        Log.d("MainActivity", "Button 2 Model: " + model2 + ", CO2 Emissions: " + co2Emissions2 + ", Combined: " + combined2);
        Log.d("MainActivity", "Button 3 Model: " + model3 + ", CO2 Emissions: " + co2Emissions3 + ", Combined: " + combined3);
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        gridCar1.setOnClickListener(v -> {
            gridLayout.setVisibility(View.GONE);
            saveSelectedButtonIndex(1);
            showCarSelectedMessage(1);});

        gridCar2.setOnClickListener(v -> {
            gridLayout.setVisibility(View.GONE);
            saveSelectedButtonIndex(2);
            showCarSelectedMessage(2);});

        gridCar3.setOnClickListener(v -> {
            gridLayout.setVisibility(View.GONE);
            saveSelectedButtonIndex(3);
            showCarSelectedMessage(3);});
    }

    private void saveSelectedButtonIndex(int index) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("selectedButtonIndex", index).apply();
        Log.d("UIManager", "Selected button index: " + index);
    }

    private void showCarSelectedMessage(int index) {
        String carName = preferenceManager.getModelName(index);
        String message = "You've selected " + carName;
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("DefaultLocale")
    public void showDistanceAndTime(DirectionsResult result) {
        if (result != null && result.routes.length > 0) {
            Distance distance = result.routes[0].legs[0].distance;
            Duration duration = result.routes[0].legs[0].duration;

            // Obtain the index of the selected button
            SharedPreferences sharedPreferences = activity.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE);
            int selectedButtonIndex = sharedPreferences.getInt("selectedButtonIndex", -1);

            // Use PreferenceManager to get CO2 and fuel consumption values
            PreferenceManager preferenceManager = PreferenceManager.getInstance(activity);
            String co2EmissionsString = preferenceManager.getCo2Emissions(selectedButtonIndex);
            Double combinedFuelConsumption = preferenceManager.getCombined(selectedButtonIndex);

            Log.d("CO2_DEBUG", "CO2 Emissions String: " + co2EmissionsString);
            Log.d("Fuel_DEBUG", "Combined Fuel Consumption: " + combinedFuelConsumption);

            try {
                float totalCo2EmissionsInKg = 0;
                float fuelConsumption = 0;

                if (co2EmissionsString != null && !co2EmissionsString.isEmpty() && distance != null && distance.inMeters > 0) {
                    // Convert string to float
                    float co2EmissionsPerKm = Float.parseFloat(co2EmissionsString);
                    Log.d("CO2_DEBUG", "CO2 Emissions per Km: " + co2EmissionsPerKm);

                    // Convert distance from meters to kilometers
                    float distanceInKm = distance.inMeters / 1000.0f;

                    // Calculate total CO2 emissions in grams
                    float totalCo2EmissionsInGrams = co2EmissionsPerKm * distanceInKm;

                    // Convert total emissions to kilograms
                    totalCo2EmissionsInKg = totalCo2EmissionsInGrams / 1000.0f;

                    Log.d("CO2_DEBUG", "Total CO2 Emissions in Kg: " + totalCo2EmissionsInKg);

                    if (combinedFuelConsumption != null) {
                        // Calculate total fuel consumption in liters
                        fuelConsumption = (float) (combinedFuelConsumption * distanceInKm / 100.0f);
                    }

                    Log.d("Fuel_DEBUG", "Total Fuel Consumption in Liters: " + fuelConsumption);
                }

                // Build the message to be displayed based on available values
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("Distance: ").append(distance.humanReadable)
                        .append("\nDuration: ").append(duration.humanReadable);

                // Check and display CO2 Emissions
                if (totalCo2EmissionsInKg > 0.0) {
                    messageBuilder.append("\nCO2 Emissions: ").append(String.format("%.2f", totalCo2EmissionsInKg)).append(" kg");
                } else {
                    messageBuilder.append("\nCO2 Emissions: Unfortunately, we don't have data for this car at the moment.");
                }

                // Check and display Fuel Consumption
                if (fuelConsumption > 0.0) {
                    messageBuilder.append("\nFuel Consumption: ").append(String.format("%.2f", fuelConsumption)).append(" L");
                } else {
                    messageBuilder.append("\nFuel Consumption: Unfortunately, we don't have data for this car at the moment.");
                }

                // Show the dialog with the constructed message
                new AlertDialog.Builder(activity)
                        .setTitle("Route Information")
                        .setMessage(messageBuilder.toString())
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();

            } catch (NumberFormatException e) {
                // Handle error if string is not a valid number
                e.printStackTrace();
                Log.d("CO2_DEBUG", "Invalid CO2 Emissions data: " + co2EmissionsString);
                new AlertDialog.Builder(activity)
                        .setTitle("Route Information")
                        .setMessage("Distance: " + distance.humanReadable +
                                "\nDuration: " + duration.humanReadable +
                                "\nCO2 Emissions: N/A (invalid CO2 data)" +
                                "\nFuel Consumption: N/A")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        }
    }

}