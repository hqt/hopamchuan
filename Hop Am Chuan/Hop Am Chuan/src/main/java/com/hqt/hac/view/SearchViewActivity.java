package com.hqt.hac.view;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.hqt.hac.utils.ReflectionUtils;
import com.hqt.hac.utils.UIUtils;

/**
 * Search Activity acts as Search Dialog
 * System will call when receive Search Intent
 * See this link for more Detail:
 *
 * @see <a href="http://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget">
        * // Using Search Widget</a>
 */
public class SearchViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

       /* if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/

        handleIntent(getIntent());


    }

    /**
     * If the currently Activity is searchable Activity, then one of two things will be happens :
     *
     * 1) <b> By default: </b>
     * the searchable activity receives the ACTION_SEARCH intent with a call to onCreate()
     * and a new instance of the activity is brought to the top of the activity stack.
     * There are now two instances of your searchable activity in the activity stack
     * (so pressing the Back button goes back to the previous instance of the searchable activity,
     * rather than exiting the searchable activity).
     *
     * 2) <b> If you set android:launchMode to "singleTop" (Set in Android Manifest): </b>
     * then the searchable activity receives the ACTION_SEARCH intent with a call to onNewIntent(Intent),
     * passing the new ACTION_SEARCH intent here.
     * <hqt>
     * This action usually ideal.
     * Because chances are goods that once a search is done, the user will performs additional searches
     * bad practice if app creates multiple instances of the searchable activity !!!
     * </hqt>
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * private helper method. So both way will do the same for Search Activity
     */
    private void handleIntent(Intent intent) {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // perform search base on query here
        }
    }

  /*  @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null && UIUtils.hasHoneycomb()) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                searchView.setQueryRefinementEnabled(true);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        ReflectionUtils.tryInvoke(searchItem, "collapseActionView");
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });
                searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                    @Override
                    public boolean onSuggestionSelect(int i) {
                        return false;
                    }

                    @Override
                    public boolean onSuggestionClick(int i) {
                        ReflectionUtils.tryInvoke(searchItem, "collapseActionView");
                        return false;
                    }
                });
            }
        }
        return true;
    }*/


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                if (!UIUtils.hasHoneycomb()) {
                    startSearch(null, false, Bundle.EMPTY, false);
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

}
