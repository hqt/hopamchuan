package com.hqt.hac.view;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.ArtistDataAccessLayer;
import com.hqt.hac.model.dal.SongDataAccessLayer;

import java.util.ArrayList;
import java.util.Collection;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Search Activity acts as Search Dialog
 * System will call when receive Search Intent
 * See this link for more Detail:
 *
 * @see <a href="http://developer.android.com/guide/topics/search/search-dialog.html#UsingSearchWidget">
 */
public class SearchViewActivity extends ActionBarActivity implements InfinityListView.ILoaderContent, AdapterView.OnItemSelectedListener {

    private static String TAG = makeLogTag(SearchViewActivity.class);

    /** List contains search data */
    InfinityListView mListView;

    /** Adapter for ListView
     * use old ones become it's same
     */
    BaseAdapter mAdapter;

    /** current type of search query */
    int type;

    /** current query string */
    String queryStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);


        mListView = (InfinityListView) findViewById(R.id.list_view);

        /** Spinner : create mAdapter for Spinner */
        Spinner spinner = (Spinner) findViewById(R.id.spinner_method_list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.
                createFromResource(getApplicationContext(),
                        R.array.song_list_method, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(adapter);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);    // because this fragment has implemented method

        handleIntent(getIntent());

        /** config mode for this ListView.
         *  this ListView is full rich function. See document for more detail
         */
        InfinityListView.ListViewProperty property = new InfinityListView.ListViewProperty();
        property.Loader(this).Adapter(mAdapter).FirstProcessLoading(true).LoadingView(R.layout.list_item_loading)
                .NumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD).RunningBackground(true);
        mListView.setListViewProperty(property);

    }

    /**
     * If the currently Activity is searchable Activity, then one of two things will be happens :
     *
     * 1) <b> By default: </b>
     * the searchable mActivity receives the ACTION_SEARCH intent with a call to onCreate()
     * and a new instance of the mActivity is brought to the top of the mActivity stack.
     * There are now two instances of your searchable mActivity in the mActivity stack
     * (so pressing the Back button goes back to the previous instance of the searchable mActivity,
     * rather than exiting the searchable mActivity).
     *
     * 2) <b> If you set android:launchMode to "singleTop" (Set in Android Manifest): </b>
     * then the searchable mActivity receives the ACTION_SEARCH intent with a call to onNewIntent(Intent),
     * passing the new ACTION_SEARCH intent here.
     * <hqt>
     * This action usually ideal.
     * Because chances are goods that once a search is done, the user will performs additional searches
     * bad practice if app creates multiple instances of the searchable mActivity !!!
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
            queryStr = intent.getStringExtra(SearchManager.QUERY);
            type = 0;   // default is search by song
            doSearch(queryStr);
        }
    }

    private void doSearch(String queryStr) {
        // assign work for inf ListView
    }


    @Override
    public Collection load(int offset, int count) {
        Collection res = null;
        switch (type) {
            case 0:
                res = SongDataAccessLayer.searchSongByTitle(queryStr, offset, count);
                break;
            case 1:
                res = ArtistDataAccessLayer.searchSongBySinger(queryStr, 100);
                break;
            case 2:
                res = ArtistDataAccessLayer.searchSongByAuthor(queryStr, 100);
        }
        return res;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                // search by artist
                mAdapter = new SongListAdapter(getApplicationContext(), new ArrayList<Song>());
                break;
            case 1:
                // search by author
                break;
            case 2:
                // search by singer
                break;
            default:
                // do nothing
        }
        mListView.resetListView(mAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
