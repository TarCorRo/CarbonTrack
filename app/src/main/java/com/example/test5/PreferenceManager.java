package com.example.test5;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static PreferenceManager instance;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("CarPreferences", Context.MODE_PRIVATE);
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    public String getModelName(int index) {
        return sharedPreferences.getString("buttonCar" + index, "NULL");
    }

    public String getCo2Emissions(int index) {
        return sharedPreferences.getString("co2Emissions" + index, "N/A");
    }

    public double getCombined(int index) {
        return Double.longBitsToDouble(sharedPreferences.getLong("combined" + index, Double.doubleToRawLongBits(0.0)));
    }

    public void setModelName(int index, String modelName) {
        sharedPreferences.edit().putString("buttonCar" + index, modelName).apply();
    }

    public void setCo2Emissions(int index, String co2Emissions) {
        sharedPreferences.edit().putString("co2Emissions" + index, co2Emissions).apply();
    }

    public void setCombined(int index, double combined) {
        sharedPreferences.edit().putLong("combined" + index, Double.doubleToRawLongBits(combined)).apply();
    }
}

