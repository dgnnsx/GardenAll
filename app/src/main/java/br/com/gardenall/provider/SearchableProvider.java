package br.com.gardenall.provider;

/**
 * Created by diego on 09/10/16.
 */

import android.content.SearchRecentSuggestionsProvider;

public class SearchableProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "br.com.gardenall.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider(){
        setupSuggestions(AUTHORITY, MODE);
    }
}