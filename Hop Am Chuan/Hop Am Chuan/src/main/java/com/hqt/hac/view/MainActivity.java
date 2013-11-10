package com.hqt.hac.view;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.view.fragment.NavigationDrawerFragment;
import com.hqt.hac.view.fragment.WelcomeFragment;
import com.unittest.DatabaseTest;

import static com.hqt.hac.utils.LogUtils.makeLogTag;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = makeLogTag(MainActivity.class);

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    SearchView mSearchView;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // delete all database
         //HopAmChuanDatabase.deleteDatabase(getApplicationContext());

        // create sample database
        DatabaseTest.prepareLocalDatabaseByHand(getApplicationContext());

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    /**
     *  Activity that using navigation drawer will implement this interface
     *  base on position that return suitable view
     */
    @Override
    public void onNavigationDrawerItemSelected(Fragment fragment) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }
        else {
            //Toast.makeText(getBaseContext(), "NULL POINT", Toast.LENGTH_LONG);
            // default to Welcome Fragment
            Fragment welcomeFragment = new WelcomeFragment();
            fragmentManager.beginTransaction().replace(R.id.container, welcomeFragment).commit();
        }
    }

    public void switchFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }


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
        if (mNavigationDrawerFragment.isDrawerOpen()) {
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
        switch (item.getItemId()) {
            case R.id.action_bar:
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

}
