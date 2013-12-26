package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hqt.hac.helper.adapter.MergeAdapter;
import com.hqt.hac.helper.adapter.NavigationDrawerAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.helper.adapter.NavigationDrawerAdapter.IHeaderDelegate;
import static com.hqt.hac.helper.adapter.NavigationDrawerAdapter.IItemDelegate;
import static com.hqt.hac.helper.adapter.NavigationDrawerAdapter.IPlaylistHeaderDelegate;
import static com.hqt.hac.helper.adapter.NavigationDrawerAdapter.IPlaylistItemDelegate;
import static com.hqt.hac.helper.adapter.NavigationDrawerAdapter.ItemAdapter.TYPE;

;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment
        implements IHeaderDelegate,IItemDelegate, IPlaylistHeaderDelegate, IPlaylistItemDelegate {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    /**
     * All mAdapter type for this view
     */
    MergeAdapter mergeAdapter;
    NavigationDrawerAdapter.HeaderAdapter headerAdapter;
    NavigationDrawerAdapter.ItemAdapter itemAdapter;
    NavigationDrawerAdapter.PlaylistHeaderAdapter playlistHeaderAdapter;
    NavigationDrawerAdapter.PlaylistItemAdapter playlistItemAdapter;

    /**
     * load all playlist in system
     * use this playlist list assign to ListViewAdapter for performance
     */
    List<Playlist> playlistList;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** load all playlist here */
        playlistList = PlaylistDataAccessLayer.getAllPlayLists(getActivity().getApplicationContext());

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
       // mCallbacks.onNavigationDrawerItemSelected(null);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        /**
         * setCacheColorHint : color background when scroll
         */
        mDrawerListView.setSelector(android.R.color.transparent);
        mDrawerListView.setCacheColorHint(Color.TRANSPARENT);


        // Simple view for drawer listview
        // can use this code for testing purpose
        /*String[] categories = getResources().getStringArray(R.array.navigation_drawer_default_items);
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                categories
                ));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        */

        // Old way to use Adapter
        // Not stability. Need framework to work on this
        // NavigationDrawerAdapterDraft mAdapter = new NavigationDrawerAdapterDraft(getActivity().getApplicationContext());
        // mDrawerListView.setAdapter(mAdapter);


        // TODO need modify and watch carefully here
        /*mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });*/

        /**
         * Using MergeAdapter for complex view
         * just create as many type of view in list as we want and Add to this Adapter
         */
        mergeAdapter = new MergeAdapter();

        // create all adapters as we want
         headerAdapter = new NavigationDrawerAdapter.HeaderAdapter(getActivity());
         itemAdapter = new NavigationDrawerAdapter.ItemAdapter(getActivity().getApplicationContext());
         playlistHeaderAdapter = new NavigationDrawerAdapter.PlaylistHeaderAdapter(getActivity().getApplicationContext());
         playlistItemAdapter = new NavigationDrawerAdapter.PlaylistItemAdapter(getActivity().getApplicationContext(),
                 playlistList);

        // assign each adapters to this composite mAdapter
        mergeAdapter.addAdapter(headerAdapter);
        mergeAdapter.addAdapter(itemAdapter);
        mergeAdapter.addAdapter(playlistHeaderAdapter);
        mergeAdapter.addAdapter(playlistItemAdapter);

        // assign this complex mAdapter to navigation drawer list
        mDrawerListView.setAdapter(mergeAdapter);

        return mDrawerListView;
    }

    /**
     * managed code : onResume and onPause
     * add and remove delegate in those method
     */
    @Override
    public void onResume() {
        super.onResume();
        headerAdapter.setDelegate(this);
        itemAdapter.setDelegate(this);
        playlistHeaderAdapter.setDelegate(this);
        playlistItemAdapter.setDelegate(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        headerAdapter.setDelegate(null);
        itemAdapter.setDelegate(null);
        playlistHeaderAdapter.setDelegate(null);
        playlistItemAdapter.setDelegate(null);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /*private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        // mCallbacks : in fact. is MainActivity
        // Main Activity will implement this interface
        // and render appropriate view
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * When user click a category in listview
     */
        @Override
        public void gotoCategoryPage(TYPE pageType) {
            Log.e("DEBUG", "category: " + pageType);
            Fragment fragment = null;
            Bundle arguments = new Bundle();
            switch (pageType) {
                case HOME:
                    fragment = new WelcomeFragment();
                    break;
                case SONGS:
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
                case SETTING:
                    //fragment = new SettingFragment();
                    break;

            }

        // assign parameters to fragment
        fragment.setArguments(arguments);

        // close Drawer List View
        if (mDrawerListView != null) {
            //mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        // assign this work to main activity
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(fragment);
        }
    }

    /**
     * When user click to a playlist row and want to show detail
     */
    @Override
    public void gotoPlayList(int playlistId) {
        Playlist playlist = playlistList.get(playlistId);
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        // setting parameters
        Bundle arguments = new Bundle();
        arguments.putParcelable("playlist", playlist);
        fragment.setArguments(arguments);

        // setting for Drawer List View
        if (mDrawerListView != null) {
            //mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        // assign this work to main activity
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(fragment);
        }
    }

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
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(Fragment fragment);
    }
}
