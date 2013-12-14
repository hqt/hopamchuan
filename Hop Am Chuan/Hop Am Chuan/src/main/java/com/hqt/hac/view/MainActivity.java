package com.hqt.hac.view;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.hqt.hac.helper.adapter.MergeAdapter;
import com.hqt.hac.helper.adapter.NavigationDrawerAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.view.fragment.ChordViewFragment;
import com.hqt.hac.view.fragment.FavoriteManagerFragment;
import com.hqt.hac.view.fragment.FindByChordFragment;
import com.hqt.hac.view.fragment.PlaylistDetailFragment;
import com.hqt.hac.view.fragment.PlaylistManagerFragment;
import com.hqt.hac.view.fragment.SongListFragment;
import com.hqt.hac.view.fragment.WelcomeFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.unittest.DatabaseTest;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerAdapter.IHeaderDelegate, NavigationDrawerAdapter.IItemDelegate,
        NavigationDrawerAdapter.IPlaylistHeaderDelegate, NavigationDrawerAdapter.IPlaylistItemDelegate {

    private static final String TAG = makeLogTag(MainActivity.class);

    /** SearchView widget */
    SearchView mSearchView;

    /** DrawerLayout for MainActivity */
    DrawerLayout mDrawerLayout;

    /** ListView contains all item categories */
    ListView mDrawerListView;

    /** Helper component that ties the action bar to the navigation drawer */
    private ActionBarDrawerToggle mDrawerToggle;

    /** All Adapter for Navigation Drawer */
    MergeAdapter mergeAdapter;
    NavigationDrawerAdapter.HeaderAdapter headerAdapter;
    NavigationDrawerAdapter.ItemAdapter itemAdapter;
    NavigationDrawerAdapter.PlaylistHeaderAdapter playlistHeaderAdapter;
    NavigationDrawerAdapter.PlaylistItemAdapter playlistItemAdapter;

    /**
     * All playlist currently on system
     * loading first for performance
     */
    List<Playlist> playlistList;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /////////////////////////////////////////////////////////////////
    ////////////////// LIFE CYCLE ACTIVITY METHOD ///////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // delete all database
        // HopAmChuanDatabase.deleteDatabase(getApplicationContext());

        // create sample database
        // DatabaseTest.prepareLocalDatabaseByHand(getApplicationContext());

        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerListView = (ListView) findViewById(R.id.navigation_drawer);

        /** load all playlist here */
        playlistList = PlaylistDataAccessLayer.getAllPlayLists(getApplicationContext());

        mTitle = getTitle();

        // Set up the drawer.
        setUpActionBar();

        // set up the list view
        setUpListView();


        // Load default fragment
        WelcomeFragment fragment = new WelcomeFragment();
        // ChordViewFragment fragment = new ChordViewFragment();
        switchFragment(fragment);

        SlidingMenu s;

    }

    @Override
    protected void onResume() {
        super.onResume();
        headerAdapter.setDelegate(this);
        itemAdapter.setDelegate(this);
        playlistHeaderAdapter.setDelegate(this);
        playlistItemAdapter.setDelegate(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        headerAdapter.setDelegate(null);
        itemAdapter.setDelegate(null);
        playlistHeaderAdapter.setDelegate(null);
        playlistItemAdapter.setDelegate(null);
    }

    //////////////////////////////////////////////////////////////////////
    ////////////////////// CONFIGURATION METHOD /////////////////////////

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    /**
     * Create ActionBar items
     * Items of Activity will be created before
     * Items of Fragment will be created after that
     * always try to call super method
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // if Drawer is open. Call Default Method
        // Let the Drawer decide what to show in the action bar
        if (isDrawerOpen()) {
            return super.onCreateOptionsMenu(menu);
        }

        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // because SearchView just exist in HoneyComb 3.0 to above
        // we should check version of users here
        // if version is lower. We use SearchDialog instead
        // TODO: search google. Find SearchWidget library for API Lower than 11
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        if (searchItem != null && UIUtils.hasHoneycomb()) {
            // Get the SearchView and set the Search Configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            // Assumes current activity is the searchable activity
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            // Do not icon the widget. expand it.
            mSearchView.setIconifiedByDefault(false);
        }

        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * when search dialog is active. activity will lost input focus
     * so. do some stuffs (such as saving state) before search
     * Unless passing search context data.
     * should end method by calling super class implementation
     */
    @Override
    public boolean onSearchRequested() {
        // doing some stuff before here
        return super.onSearchRequested();
    }

    /**
     * set up navigation drawer view
     */
    public void setUpActionBar() {

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };


        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * set up adapter for list view
     * include add all views and adapters to currently ListView
     * @return
     */
    public void setUpListView() {

        /**
         * setCacheColorHint : color background when scroll
         */
        mDrawerListView.setSelector(android.R.color.transparent);
        mDrawerListView.setCacheColorHint(Color.TRANSPARENT);

        /**
         * Using MergeAdapter for complex view
         * just create as many type of view in list as we want and Add to this Adapter
         */
        mergeAdapter = new MergeAdapter();

        /** setup adapter
         * create all adapters as we want
         * set delegate in onResume()
         * remove delegate on onPause()
         */
        // create all adapters as we want
        headerAdapter = new NavigationDrawerAdapter.HeaderAdapter(getApplicationContext());
        itemAdapter = new NavigationDrawerAdapter.ItemAdapter(getApplicationContext());
        playlistHeaderAdapter = new NavigationDrawerAdapter.PlaylistHeaderAdapter(getApplicationContext());
        playlistItemAdapter = new NavigationDrawerAdapter.PlaylistItemAdapter(getApplicationContext(), playlistList);

        /** assign each adapter to this composite adapter */
        mergeAdapter.addAdapter(headerAdapter);
        mergeAdapter.addAdapter(itemAdapter);
        mergeAdapter.addAdapter(playlistHeaderAdapter);
        mergeAdapter.addAdapter(playlistItemAdapter);

        /** assign this complex adapter to navigation drawer list*/
        mDrawerListView.setAdapter(mergeAdapter);
    }


    ////////////////////////////////////////////////////////////////////////
    //////////////////// SIMPLE HELPER METHOD //////////////////////////////

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerListView);
    }

    public void switchFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }


    ////////////////////////////////////////////////////////////////
    //////////////// METHOD OVERRIDE USE FOR ADAPTER //////////////

    @Override
    public void gotoCategoryPage(NavigationDrawerAdapter.ItemAdapter.TYPE pageType) {
        Log.e("DEBUG", "category: " + pageType);
        Fragment fragment = null;
        Bundle arguments = new Bundle();
        switch (pageType) {
            case HOME:
                fragment = new SongListFragment();
                break;
            case MYPLAYLIST:
                fragment = new PlaylistManagerFragment();
                break;
            case FAVORITE:
                fragment = new FavoriteManagerFragment();
                break;
            case FIND_BY_CHORD:
                fragment = new FindByChordFragment();
                break;
            case SEARCH_CHORD:
                fragment = new ChordViewFragment();
                break;
        }

        // close Navigation Drawer
        if (mDrawerListView != null) {
            // mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerListView);
        }

        // Open Custom Fragment
        if (fragment != null) fragment.setArguments(arguments);
        switchFragment(fragment);
    }

    @Override
    public void gotoPlayList(int playlistId) {
        Playlist playlist = playlistList.get(playlistId);
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();

        // setting parameters
        Bundle arguments = new Bundle();
        // TODO : can change to parcelable later for performance
        arguments.putSerializable("playlist", playlist);
        fragment.setArguments(arguments);

        // setting for Drawer List View
        if (mDrawerListView != null) {
            //mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mDrawerListView);
        }
        switchFragment(fragment);
    }

    /////////////////// HQT //////////////////////////////////////////
    //////////// NOTES ABOUT CALLBACK ///////////////////////////////
    /**
     * Callbacks interface that send data back from fragment to activity
     * Activity will use this function to determine what should to do
     * All activities using this fragment must implement this interface.
     * See this link for more detail:
     * @link{http://stackoverflow.com/questions/14213947/onattach-callback-from-fragment-to-activity}
     *
     * STEP :
     *  1. create interface
     *  2. assign activity to mCallback on onAttach() method
     *      (can do this because activity must be implements this method)
     *  on NavigationDrawer, when event arises. use mCallback
     */
}
