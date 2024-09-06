package com.example.test5;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class SearchViewManager {
    private final Context context;
    private final ListView suggestionsList;
    private final PlacesClient placesClient;

    public SearchViewManager(Context context, ListView suggestionsList) {
        this.context = context;
        this.suggestionsList = suggestionsList;
        Places.initialize(context, context.getString(R.string.my_map_api_key));
        this.placesClient = Places.createClient(context);
    }

    public void setUpSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Când utilizatorul apasă enter
                suggestionsList.setVisibility(android.view.View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 2) {
                    System.out.println("Intru in suggestion list");
                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(token)
                            .setQuery(newText)
                            .build();

                    placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                        List<String> suggestions = new ArrayList<>();
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            suggestions.add(prediction.getFullText(null).toString());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.custom_list_item, R.id.text1, suggestions);
                        suggestionsList.setAdapter(adapter);
                        suggestionsList.setVisibility(android.view.View.VISIBLE);

                        suggestionsList.setOnItemClickListener((parent, view, position, id) -> {
                            String selectedSuggestion = adapter.getItem(position);
                            searchView.setQuery(selectedSuggestion, true);
                            suggestionsList.setVisibility(android.view.View.GONE);
                        });
                    }).addOnFailureListener(exception -> {
                        suggestionsList.setVisibility(android.view.View.GONE);
                    });
                } else {
                    suggestionsList.setVisibility(android.view.View.GONE);
                }
                return false;
            }
        });
    }
}