package com.example.test5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {

    private Button buttonAdd;
    private Button buttonCar1;
    private Button buttonCar2;
    private Button buttonCar3;
    private Button buttonDelCar1;
    private Button buttonDelCar2;
    private Button buttonDelCar3;
    private Button buttonSave;
    private Button buttonSaveAndGo;
    private Button buttonBack;
    private TextView loading;
    private ProgressBar progressBar;
    private AutoCompleteTextView autoCompleteTextViewBrand, autoCompleteTextViewModel, autoCompleteTextViewEngine;
    private FirebaseDataManager firebaseDataManager;
    private PreferenceManager preferenceManager;

    private String co2EmissionsCar1 = "NULL";
    private String co2EmissionsCar2 = "NULL";
    private String co2EmissionsCar3 = "NULL";

    private int lastValidButtonIndex = -1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(this);

        preferenceManager = PreferenceManager.getInstance(this);

        VideoView splashVideo = findViewById(R.id.splashVideo);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        splashVideo.setVideoURI(video);

        splashVideo.setOnCompletionListener(mp -> {
            setContentView(R.layout.activity_start);

            buttonCar1 = findViewById(R.id.button_car1);
            buttonCar2 = findViewById(R.id.button_car2);
            buttonCar3 = findViewById(R.id.button_car3);
            buttonDelCar1 = findViewById(R.id.delete_car1);
            buttonDelCar2 = findViewById(R.id.delete_car2);
            buttonDelCar3 = findViewById(R.id.delete_car3);
            buttonAdd = findViewById(R.id.button_add);
            buttonSave = findViewById(R.id.button_save);
            buttonSaveAndGo = findViewById(R.id.button_save_and_go);
            buttonBack = findViewById(R.id.cancel);
            loading = findViewById(R.id.textView_loading);
            progressBar = findViewById(R.id.progressBarIndeterminate);

            // Încarcă textele butoanelor salvate
            loadButtonTexts();

            // Configurează listener-ele
            setupListeners();

            autoCompleteTextViewBrand = findViewById(R.id.auto_complete_text_view_brand);
            autoCompleteTextViewModel = findViewById(R.id.auto_complete_text_view_model);
            autoCompleteTextViewEngine = findViewById(R.id.auto_complete_text_view_engine);

            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

            buttonAdd.startAnimation(fadeIn);
            buttonAdd.setVisibility(View.VISIBLE);
            buttonCar1.startAnimation(fadeIn);
            buttonCar1.setVisibility(View.VISIBLE);
            buttonCar2.startAnimation(fadeIn);
            buttonCar2.setVisibility(View.VISIBLE);
            buttonCar3.startAnimation(fadeIn);
            buttonCar3.setVisibility(View.VISIBLE);
            buttonDelCar1.startAnimation(fadeIn);
            buttonDelCar1.setVisibility(View.VISIBLE);
            buttonDelCar2.startAnimation(fadeIn);
            buttonDelCar2.setVisibility(View.VISIBLE);
            buttonDelCar3.startAnimation(fadeIn);
            buttonDelCar3.setVisibility(View.VISIBLE);

            // Initialize FirebaseDataManager
            firebaseDataManager = new FirebaseDataManager(autoCompleteTextViewBrand, autoCompleteTextViewModel, autoCompleteTextViewEngine);


            buttonAdd.setOnClickListener(v -> {
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

                autoCompleteTextViewModel.setEnabled(false);
                autoCompleteTextViewModel.setAlpha(0.5f);

                autoCompleteTextViewEngine.setEnabled(false);
                autoCompleteTextViewEngine.setAlpha(0.5f);

                buttonAdd.startAnimation(fadeOut);
                buttonCar1.startAnimation(fadeOut);
                buttonCar2.startAnimation(fadeOut);
                buttonCar3.startAnimation(fadeOut);
                buttonDelCar1.startAnimation(fadeOut);
                buttonDelCar2.startAnimation(fadeOut);
                buttonDelCar3.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        autoCompleteTextViewBrand.startAnimation(fadeIn);
                        autoCompleteTextViewBrand.setVisibility(View.VISIBLE);
                        autoCompleteTextViewEngine.startAnimation(fadeIn);
                        autoCompleteTextViewEngine.setVisibility(View.VISIBLE);
                        autoCompleteTextViewModel.startAnimation(fadeIn);
                        autoCompleteTextViewModel.setVisibility(View.VISIBLE);
                        buttonSaveAndGo.startAnimation(fadeIn);
                        buttonSaveAndGo.setVisibility(View.VISIBLE);
                        buttonSave.startAnimation(fadeIn);
                        buttonBack.startAnimation(fadeIn);
                        buttonBack.setVisibility(View.VISIBLE);
                        buttonSave.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.GONE);
                        buttonCar1.setVisibility(View.GONE);
                        buttonCar2.setVisibility(View.GONE);
                        buttonCar3.setVisibility(View.GONE);
                        buttonDelCar1.setVisibility(View.GONE);
                        buttonDelCar2.setVisibility(View.GONE);
                        buttonDelCar3.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            });

            buttonBack.setOnClickListener(v ->{

                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                buttonSave.startAnimation(fadeOut);
                buttonSaveAndGo.startAnimation(fadeOut);
                buttonBack.startAnimation(fadeOut);
                autoCompleteTextViewBrand.startAnimation(fadeOut);
                autoCompleteTextViewEngine.startAnimation(fadeOut);
                autoCompleteTextViewModel.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        autoCompleteTextViewModel.setVisibility(View.GONE);
                        autoCompleteTextViewBrand.setVisibility(View.GONE);
                        autoCompleteTextViewEngine.setVisibility(View.GONE);
                        autoCompleteTextViewEngine.setText(null);
                        autoCompleteTextViewBrand.setText(null);
                        autoCompleteTextViewModel.setText(null);
                        buttonSave.setVisibility(View.GONE);
                        buttonSaveAndGo.setVisibility(View.GONE);
                        buttonBack.setVisibility(View.GONE);
                        buttonAdd.startAnimation(fadeIn);
                        buttonAdd.setVisibility(View.VISIBLE);
                        buttonCar1.startAnimation(fadeIn);
                        buttonCar1.setVisibility(View.VISIBLE);
                        buttonCar2.startAnimation(fadeIn);
                        buttonCar2.setVisibility(View.VISIBLE);
                        buttonCar3.startAnimation(fadeIn);
                        buttonCar3.setVisibility(View.VISIBLE);
                        buttonDelCar1.startAnimation(fadeIn);
                        buttonDelCar1.setVisibility(View.VISIBLE);
                        buttonDelCar2.startAnimation(fadeIn);
                        buttonDelCar2.setVisibility(View.VISIBLE);
                        buttonDelCar3.startAnimation(fadeIn);
                        buttonDelCar3.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            });


            buttonDelCar1.setOnClickListener(v -> {
                buttonCar1.setText("DELETED");
                new Handler().postDelayed(() -> {
                    buttonCar1.setText("NULL");
                    saveButtonText(1, "NULL", "NULL",0);
                }, 1000);
            });

            buttonDelCar2.setOnClickListener(v -> {
                buttonCar2.setText("DELETED");
                new Handler().postDelayed(() -> {
                    buttonCar2.setText("NULL");
                    saveButtonText(2, "NULL", "NULL", 0);
                }, 1000);
            });

            buttonDelCar3.setOnClickListener(v -> {
                buttonCar3.setText("DELETED");
                new Handler().postDelayed(() -> {
                    buttonCar3.setText("NULL");
                    saveButtonText(3, "NULL", "NULL",0);
                }, 1000);
            });

            buttonCar1.setOnClickListener(v -> {
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                saveSelectedButtonIndex(1);
                buttonAdd.startAnimation(fadeOut);
                buttonCar1.startAnimation(fadeOut);
                buttonCar2.startAnimation(fadeOut);
                buttonCar3.startAnimation(fadeOut);
                buttonDelCar1.startAnimation(fadeOut);
                buttonDelCar2.startAnimation(fadeOut);
                buttonDelCar3.startAnimation(fadeOut);
                loading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startMainActivity();
                        buttonAdd.setVisibility(View.GONE);
                        buttonCar1.setVisibility(View.GONE);
                        buttonCar2.setVisibility(View.GONE);
                        buttonCar3.setVisibility(View.GONE);
                        buttonDelCar1.setVisibility(View.GONE);
                        buttonDelCar2.setVisibility(View.GONE);
                        buttonDelCar3.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            });

            buttonCar2.setOnClickListener(v -> {
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                saveSelectedButtonIndex(2);
                buttonAdd.startAnimation(fadeOut);
                buttonCar1.startAnimation(fadeOut);
                buttonCar2.startAnimation(fadeOut);
                buttonCar3.startAnimation(fadeOut);
                buttonDelCar1.startAnimation(fadeOut);
                buttonDelCar2.startAnimation(fadeOut);
                buttonDelCar3.startAnimation(fadeOut);
                loading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startMainActivity();
                        buttonAdd.setVisibility(View.GONE);
                        buttonCar1.setVisibility(View.GONE);
                        buttonCar2.setVisibility(View.GONE);
                        buttonCar3.setVisibility(View.GONE);
                        buttonDelCar1.setVisibility(View.GONE);
                        buttonDelCar2.setVisibility(View.GONE);
                        buttonDelCar3.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            });

            buttonCar3.setOnClickListener(v -> {
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                saveSelectedButtonIndex(3);
                buttonAdd.startAnimation(fadeOut);
                buttonCar1.startAnimation(fadeOut);
                buttonCar2.startAnimation(fadeOut);
                buttonCar3.startAnimation(fadeOut);
                buttonDelCar1.startAnimation(fadeOut);
                buttonDelCar2.startAnimation(fadeOut);
                buttonDelCar3.startAnimation(fadeOut);
                loading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startMainActivity();
                        buttonAdd.setVisibility(View.GONE);
                        buttonCar1.setVisibility(View.GONE);
                        buttonCar2.setVisibility(View.GONE);
                        buttonCar3.setVisibility(View.GONE);
                        buttonDelCar1.setVisibility(View.GONE);
                        buttonDelCar2.setVisibility(View.GONE);
                        buttonDelCar3.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            });
        });

        splashVideo.start();
    }

    // Setează indexul pentru butonul apăsat în SharedPreferences
    private void saveSelectedButtonIndex(int index) {
        SharedPreferences sharedPreferences = getSharedPreferences("CarPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedButtonIndex", index);
        editor.apply();
    }


    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);

        new Handler().postDelayed(() -> {
            finish();
        }, 500);
    }

    private boolean hasAvailableSlot() {
        return "NULL".equals(buttonCar1.getText().toString()) ||
                "NULL".equals(buttonCar2.getText().toString()) ||
                "NULL".equals(buttonCar3.getText().toString());
    }

    private void setupListeners() {
        buttonSave.setOnClickListener(v -> {
            if (hasAvailableSlot()) {
                String modelName = autoCompleteTextViewModel.getText().toString();
                String selectedEngine = autoCompleteTextViewEngine.getText().toString();
                String co2Emissions = firebaseDataManager.getEngineEmissionsMap().get(selectedEngine);
                Double combined = firebaseDataManager.getEngineCombinedMap().get(selectedEngine);

                if (!modelName.isEmpty() && co2Emissions != null && combined != null) {
                    updateButtonText(modelName, co2Emissions, combined);
                }

                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                buttonSave.startAnimation(fadeOut);
                buttonSaveAndGo.startAnimation(fadeOut);
                buttonBack.startAnimation(fadeOut);
                autoCompleteTextViewBrand.startAnimation(fadeOut);
                autoCompleteTextViewEngine.startAnimation(fadeOut);
                autoCompleteTextViewModel.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        autoCompleteTextViewModel.setVisibility(View.GONE);
                        autoCompleteTextViewBrand.setVisibility(View.GONE);
                        autoCompleteTextViewEngine.setVisibility(View.GONE);
                        autoCompleteTextViewEngine.setText(null);
                        autoCompleteTextViewBrand.setText(null);
                        autoCompleteTextViewModel.setText(null);
                        buttonSave.setVisibility(View.GONE);
                        buttonSaveAndGo.setVisibility(View.GONE);
                        buttonBack.setVisibility(View.GONE);
                        buttonAdd.startAnimation(fadeIn);
                        buttonAdd.setVisibility(View.VISIBLE);
                        buttonCar1.startAnimation(fadeIn);
                        buttonCar1.setVisibility(View.VISIBLE);
                        buttonCar2.startAnimation(fadeIn);
                        buttonCar2.setVisibility(View.VISIBLE);
                        buttonCar3.startAnimation(fadeIn);
                        buttonCar3.setVisibility(View.VISIBLE);
                        buttonDelCar1.startAnimation(fadeIn);
                        buttonDelCar1.setVisibility(View.VISIBLE);
                        buttonDelCar2.startAnimation(fadeIn);
                        buttonDelCar2.setVisibility(View.VISIBLE);
                        buttonDelCar3.startAnimation(fadeIn);
                        buttonDelCar3.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            } else {
                Log.e("SplashActivity", "No available slots for saving a new car.");
                Toast.makeText(this, "Nu există sloturi disponibile pentru a salva o nouă mașină.", Toast.LENGTH_LONG).show();
            }
        });

        buttonSaveAndGo.setOnClickListener(v -> {
            if (hasAvailableSlot()) {
                String modelName = autoCompleteTextViewModel.getText().toString();
                String selectedEngine = autoCompleteTextViewEngine.getText().toString();
                String co2Emissions = firebaseDataManager.getEngineEmissionsMap().get(selectedEngine);
                Double combined = firebaseDataManager.getEngineCombinedMap().get(selectedEngine); // Exemplu pentru obținerea valorii combined

                if (!modelName.isEmpty() && co2Emissions != null && combined != null) {
                    updateButtonText(modelName, co2Emissions, combined);
                }

                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                buttonSave.startAnimation(fadeOut);
                buttonSaveAndGo.startAnimation(fadeOut);
                autoCompleteTextViewBrand.startAnimation(fadeOut);
                autoCompleteTextViewEngine.startAnimation(fadeOut);
                autoCompleteTextViewModel.startAnimation(fadeOut);
                buttonBack.startAnimation(fadeOut);
                loading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        autoCompleteTextViewModel.setVisibility(View.GONE);
                        autoCompleteTextViewBrand.setVisibility(View.GONE);
                        autoCompleteTextViewEngine.setVisibility(View.GONE);
                        autoCompleteTextViewEngine.setText(null);
                        autoCompleteTextViewBrand.setText(null);
                        autoCompleteTextViewModel.setText(null);
                        buttonSave.setVisibility(View.GONE);
                        buttonSaveAndGo.setVisibility(View.GONE);
                        buttonBack.setVisibility(View.GONE);
                        loadButtonTexts();
                        if (lastValidButtonIndex != -1) {
                            saveSelectedButtonIndex(lastValidButtonIndex);
                        }
                        startMainActivity();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            } else {
                Log.e("SplashActivity", "No available slots for saving a new car.");
                Toast.makeText(this, "Nu există sloturi disponibile pentru a salva o nouă mașină.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadButtonTexts() {
        String model1 = preferenceManager.getModelName(1);
        String model2 = preferenceManager.getModelName(2);
        String model3 = preferenceManager.getModelName(3);

        buttonCar1.setText(model1);
        buttonCar2.setText(model2);
        buttonCar3.setText(model3);

        String co2Emissions1 = preferenceManager.getCo2Emissions(1);
        String co2Emissions2 = preferenceManager.getCo2Emissions(2);
        String co2Emissions3 = preferenceManager.getCo2Emissions(3);

        double combined1 = preferenceManager.getCombined(1);
        double combined2 = preferenceManager.getCombined(2);
        double combined3 = preferenceManager.getCombined(3);

        Log.d("SplashActivity", "Button 1 Model: " + model1 + ", CO2 Emissions: " + co2Emissions1 + ", Combined: " + combined1);
        Log.d("SplashActivity", "Button 2 Model: " + model2 + ", CO2 Emissions: " + co2Emissions2 + ", Combined: " + combined2);
        Log.d("SplashActivity", "Button 3 Model: " + model3 + ", CO2 Emissions: " + co2Emissions3 + ", Combined: " + combined3);

        if (!"NULL".equals(model1)) {
            lastValidButtonIndex = 1;
        }
        if (!"NULL".equals(model2)) {
            lastValidButtonIndex = 2;
        }
        if (!"NULL".equals(model3)) {
            lastValidButtonIndex = 3;
        }
    }

    private void saveButtonText(int buttonIndex, String modelName, String co2Emissions, double combined) {
        SharedPreferences sharedPreferences = getSharedPreferences("CarPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("buttonCar" + buttonIndex, modelName);
        editor.putString("co2Emissions" + buttonIndex, co2Emissions);
        editor.putLong("combined" + buttonIndex, Double.doubleToRawLongBits(combined));
        editor.apply();
    }

    private void updateButtonText(String modelName, String co2Emissions, double combined) {
        if ("NULL".equals(buttonCar1.getText().toString())) {
            buttonCar1.setText(modelName);
            preferenceManager.setModelName(1, modelName);
            preferenceManager.setCo2Emissions(1, co2Emissions);
            preferenceManager.setCombined(1, combined);
        } else if ("NULL".equals(buttonCar2.getText().toString())) {
            buttonCar2.setText(modelName);
            preferenceManager.setModelName(2, modelName);
            preferenceManager.setCo2Emissions(2, co2Emissions);
            preferenceManager.setCombined(2, combined);
        } else if ("NULL".equals(buttonCar3.getText().toString())) {
            buttonCar3.setText(modelName);
            preferenceManager.setModelName(3, modelName);
            preferenceManager.setCo2Emissions(3, co2Emissions);
            preferenceManager.setCombined(3, combined);
        }
    }

}
