package com.hac.android.provider;

import android.content.SearchRecentSuggestionsProvider;

import com.hac.android.utils.LogUtils;

/**
 * Use this class to handler search recent suggestion
 * View more details in those link :
 * http://developer.android.com/guide/topics/search/adding-recent-query-suggestions.html
 * http://developer.android.com/reference/android/content/SearchRecentSuggestionsProvider.html
 * Created by ThaoHQSE60963 on 1/7/14.
 */
public class SearchRecentProvider extends SearchRecentSuggestionsProvider {
    public final static String TAG = LogUtils.makeLogTag(SearchRecentProvider.class);
    public final static String AUTHORITY = "com.meta.data";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchRecentProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
