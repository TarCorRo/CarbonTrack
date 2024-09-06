package com.example.test5;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDataManager {
    private static final String TAG = "FirebaseDataManager";

    private AutoCompleteTextView autoCompleteTextViewBrand;
    private AutoCompleteTextView autoCompleteTextViewModel;
    private AutoCompleteTextView autoCompleteTextViewEngine;

    private ArrayAdapter<String> adapterBrand;
    private ArrayAdapter<String> adapterModel;
    private ArrayAdapter<String> adapterEngine;

    private List<String> brandNames = new ArrayList<>();
    private List<String> modelNames = new ArrayList<>();
    private List<String> engineNames = new ArrayList<>();

    private Map<String, Double> engineCombinedMap = new HashMap<>();
    private Map<String, String> engineEmissionsMap = new HashMap<>();
    private Map<String, String> brandIdMap = new HashMap<>();
    private Map<String, String> modelIdMap = new HashMap<>();

    public FirebaseDataManager(AutoCompleteTextView brandView, AutoCompleteTextView modelView, AutoCompleteTextView engineView) {
        this.autoCompleteTextViewBrand = brandView;
        this.autoCompleteTextViewModel = modelView;
        this.autoCompleteTextViewEngine = engineView;

        this.adapterBrand = new ArrayAdapter<>(brandView.getContext(), android.R.layout.simple_dropdown_item_1line, brandNames);
        this.adapterModel = new ArrayAdapter<>(modelView.getContext(), android.R.layout.simple_dropdown_item_1line, modelNames);
        this.adapterEngine = new ArrayAdapter<>(engineView.getContext(), android.R.layout.simple_dropdown_item_1line, engineNames);

        this.autoCompleteTextViewBrand.setAdapter(adapterBrand);
        this.autoCompleteTextViewModel.setAdapter(adapterModel);
        this.autoCompleteTextViewEngine.setAdapter(adapterEngine);

        loadBrands(); // Initial load
        setupListeners();
    }

    private void loadBrands() {
        String brandsUrl = "https://brands-dd956.firebaseio.com";
        FirebaseDatabase brandsDatabase = FirebaseDatabase.getInstance(brandsUrl);
        DatabaseReference brandsRef = brandsDatabase.getReference("RECORDS");

        brandsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    brandNames.clear();
                    brandIdMap.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String brandId = snapshot.child("id").getValue(String.class);
                        String brandName = snapshot.child("name").getValue(String.class);
                        if (brandId != null && brandName != null) {
                            brandNames.add(brandName);
                            brandIdMap.put(brandName, brandId);
                        }
                    }
                    adapterBrand.notifyDataSetChanged(); // Actualizează adapter-ul
                    Log.d(TAG, "Brands loaded: " + brandNames);
                } else {
                    Log.d(TAG, "Brands - DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Brands - Failed to read value: " + error.toException());
            }
        });
    }

    public void loadModels(String brandId) {
        String modelsUrl = "https://automobiles.firebaseio.com";
        FirebaseDatabase modelsDatabase = FirebaseDatabase.getInstance(modelsUrl);
        DatabaseReference modelsRef = modelsDatabase.getReference("RECORDS");

        modelsRef.orderByChild("brand_id").equalTo(brandId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    modelNames.clear();
                    modelIdMap.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String modelId = snapshot.child("id").getValue(String.class);
                        String modelName = snapshot.child("name").getValue(String.class);
                        if (modelId != null && modelName != null) {
                            modelNames.add(modelName);
                            modelIdMap.put(modelName, modelId);
                        }
                    }
                    // Actualizează adapter-ul și notifică despre schimbare
                    adapterModel.clear();
                    adapterModel.addAll(modelNames);
                    adapterModel.notifyDataSetChanged();

                    // Forțează afișarea listei de sugestii
                    if (!modelNames.isEmpty()) {
                        autoCompleteTextViewModel.showDropDown();
                    }

                    Log.d(TAG, "Models loaded for brandId " + brandId + ": " + modelNames);
                } else {
                    Log.d(TAG, "No models found for brandId " + brandId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Failed to read models: " + error.toException());
            }
        });
    }

    public void loadEngines(String modelId) {
        String enginesUrl = "https://engines.firebaseio.com";
        FirebaseDatabase enginesDatabase = FirebaseDatabase.getInstance(enginesUrl);
        DatabaseReference enginesRef = enginesDatabase.getReference("RECORDS");

        enginesRef.orderByChild("automobile_id").equalTo(modelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    engineNames.clear();
                    engineEmissionsMap.clear();
                    engineCombinedMap.clear(); // Curăță harta pentru date noi

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String engineName = snapshot.child("name").getValue(String.class);
                        String co2Emissions = snapshot.child("CO2 emissions").getValue(String.class);
                        Object combinedValue = snapshot.child("combined").getValue();

                        // Verificăm și procesăm valorile obținute
                        if (engineName != null) {
                            engineNames.add(engineName);

                            // Gestionăm valorile pentru "CO2 emissions"
                            if (co2Emissions != null && !"N/A".equals(co2Emissions)) {
                                engineEmissionsMap.put(engineName, co2Emissions);
                            } else {
                                engineEmissionsMap.put(engineName, String.valueOf(0));
                            }

                            // Gestionăm valorile pentru "combined"
                            if (combinedValue instanceof Double) {
                                engineCombinedMap.put(engineName, (Double) combinedValue);
                            } else if (combinedValue instanceof String) {
                                try {
                                    String combinedStr = (String) combinedValue;
                                    if (!"N/A".equals(combinedStr)) {
                                        double combined = Double.parseDouble(combinedStr);
                                        engineCombinedMap.put(engineName, combined);
                                    } else {
                                        engineCombinedMap.put(engineName, 0.0);
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "Failed to parse combined value for engine: " + engineName, e);
                                    engineCombinedMap.put(engineName, null);
                                }
                            } else {
                                Log.e(TAG, "Unexpected data type for combined value: " + combinedValue.getClass().getName());
                                engineCombinedMap.put(engineName, null);
                            }
                        }
                    }

                    // Actualizează adapter-ul și notifică despre schimbare
                    adapterEngine.clear();
                    adapterEngine.addAll(engineNames);
                    adapterEngine.notifyDataSetChanged();

                    // Forțează afișarea listei de sugestii
                    if (!engineNames.isEmpty()) {
                        autoCompleteTextViewEngine.showDropDown();
                    }

                    Log.d(TAG, "Engines loaded for modelId " + modelId + ": " + engineNames);
                } else {
                    Log.d(TAG, "No engines found for modelId " + modelId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Failed to read engines: " + error.toException());
            }
        });
    }



    public Map<String, String> getEngineEmissionsMap() {
        return engineEmissionsMap;
    }

    public Map<String, Double> getEngineCombinedMap() {
        return engineCombinedMap;
    }

    private void setupListeners() {


        autoCompleteTextViewBrand.setOnClickListener(v -> {
            autoCompleteTextViewBrand.showDropDown(); // Arată toate elementele
        });

        autoCompleteTextViewModel.setOnClickListener(v -> {
                autoCompleteTextViewModel.showDropDown(); // Arată toate elementele
        });

        autoCompleteTextViewEngine.setOnClickListener(v -> {
                autoCompleteTextViewEngine.showDropDown(); // Arată toate elementele
        });


        autoCompleteTextViewBrand.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBrand = adapterBrand.getItem(position);
            if (selectedBrand != null) {
                String brandId = brandIdMap.get(selectedBrand);
                if (brandId != null) {
                    Log.d(TAG, "Selected brandId: " + brandId);
                    loadModels(brandId); // Transmite brandId pentru a încărca modelele

                    // Activează AutoCompleteTextView pentru Model și dezactivează Engine
                    autoCompleteTextViewModel.setEnabled(true);
                    autoCompleteTextViewModel.setAlpha(1f);
                    autoCompleteTextViewEngine.setEnabled(false);

                    // Curăță și ascunde modelele și motoarele dacă se selectează un brand
                    autoCompleteTextViewModel.setText("");
                    modelNames.clear();
                    adapterModel.notifyDataSetChanged();
                    autoCompleteTextViewEngine.setText("");
                    engineNames.clear();
                    adapterEngine.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Brand ID is null for selected brand: " + selectedBrand);
                }
            } else {
                Log.d(TAG, "Selected brand is null");
            }
        });

        autoCompleteTextViewModel.setOnItemClickListener((parent, view, position, id) -> {
            String selectedModel = adapterModel.getItem(position);
            if (selectedModel != null) {
                String modelId = modelIdMap.get(selectedModel);
                if (modelId != null) {
                    Log.d(TAG, "Selected modelId: " + modelId);
                    loadEngines(modelId); // Transmite modelId pentru a încărca motoarele

                    // Activează AutoCompleteTextView pentru Engine
                    autoCompleteTextViewEngine.setEnabled(true);
                    autoCompleteTextViewEngine.setAlpha(1f);

                    // Curăță motoarele dacă se selectează un model
                    autoCompleteTextViewEngine.setText("");
                    engineNames.clear();
                    adapterEngine.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Model ID is null for selected model: " + selectedModel);
                }
            } else {
                Log.d(TAG, "Selected model is null");
            }
        });

        autoCompleteTextViewModel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Model text changed: " + s);
                if (s.length() > 0 && autoCompleteTextViewModel.isEnabled()) {
                    autoCompleteTextViewModel.showDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
