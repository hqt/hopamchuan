package com.hac.android.guitarchord;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.Toast;

import com.hac.android.config.Config;
import com.hac.android.guitarchord.fragment.ChordViewFragment;
import com.hac.android.guitarchord.fragment.CustomFragment;
import com.hac.android.guitarchord.fragment.FavoriteManagerFragment;
import com.hac.android.guitarchord.fragment.PlaylistDetailFragment;
import com.hac.android.guitarchord.fragment.PlaylistManagerFragment;
import com.hac.android.guitarchord.fragment.SearchChordFragment;
import com.hac.android.guitarchord.fragment.SearchResultFragment;
import com.hac.android.guitarchord.fragment.SongDetailFragment;
import com.hac.android.guitarchord.fragment.SongListFragment;
import com.hac.android.guitarchord.fragment.WelcomeFragment;
import com.hac.android.helper.adapter.MergeAdapter;
import com.hac.android.helper.adapter.NavigationDrawerAdapter;
import com.hac.android.helper.service.Mp3PlayerService;
import com.hac.android.helper.widget.BunnyLayout;
import com.hac.android.helper.widget.SlidingMenuActionBarActivity;
import com.hac.android.helper.widget.SongListRightMenuHandler;
import com.hac.android.model.Playlist;
import com.hac.android.model.Song;
import com.hac.android.model.dal.PlaylistDataAccessLayer;
import com.hac.android.provider.SearchRecentProvider;
import com.hac.android.utils.LogUtils;
import com.hac.android.utils.StringUtils;
import com.hac.android.utils.UIUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.Calendar;
import java.util.List;

import static com.hac.android.utils.LogUtils.LOGE;

public class MainActivity extends SlidingMenuActionBarActivity
        implements NavigationDrawerAdapter.IHeaderDelegate, NavigationDrawerAdapter.IItemDelegate,
        NavigationDrawerAdapter.IPlaylistHeaderDelegate, NavigationDrawerAdapter.IPlaylistItemDelegate {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

    /**
     * SearchView widget
     */
    public SearchView mSearchView;

    /**
     * ListView contains all item categories
     */
    ListView mDrawerListView;

    /**
     * SlidingMenu : use for slide to see like NavigationDrawer
     */
    public SlidingMenu slidingMenu;

    /**
     * Layout of Navigation Drawer
     * Use this for Reference
     */
    View sideBarLayout;

    /**
     * Main View of App
     */
    BunnyLayout mainView;

    /**
     * All Adapter for Navigation Drawer
     */
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

    /**
     * variable to control last time user has pressed back button
     */
    long mTimePressBackBtn = 0;

    /** ServiceConnection : use to bind with Activity */
    public static ServiceConnection serviceConnection;
    /** Mp3 Service Reference */
    public static Mp3PlayerService mp3Service;
    /** Mp3 Service Intent */
    public static Intent mp3ServiceIntent;
    /** Android Built-in Mp3 Player */
    public static MediaPlayer player;

    //region Activity Life Cycle Method
    /////////////////////////////////////////////////////////////////
    ////////////////// LIFE CYCLE ACTIVITY METHOD ///////////////////

    public static final boolean DEVELOPER_MODE = true;

    /** Open sliding menu when press menu option **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // Open left menu
            getSlidingMenu().toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // add strict mode for searching performance issue
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            /*StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());*/
        }

        super.onCreate(savedInstanceState);

        // Language setting
        UIUtils.setLanguage(getBaseContext());

        // set Main View
        setContentView(R.layout.activity_main_frame);
        mainView = (BunnyLayout) findViewById(R.id.content_frame);

        // set navigation drawer View
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        sideBarLayout = inflater.inflate(R.layout.activity_navigation_drawer, null);
        mDrawerListView = (ListView) sideBarLayout.findViewById(R.id.navigation_drawer);
        setBehindContentView(sideBarLayout);

        /** load all playlist here */
        playlistList = getIntent().getParcelableArrayListExtra("playlistList");

        /** if playlist is not in intent, then load it again **/
        if (playlistList == null) {
            playlistList = PlaylistDataAccessLayer.getAllPlayLists(getApplicationContext());
        }

        mTitle = getTitle();

        // Set up the actionbar
        setUpActionBar();

        // Set up SlidingMenu
        setUpSlidingMenu();

        // set up the ListView
        setUpListView();

        // set up Mp3Service
        setUpMp3Service();

        // implement first fragment for MainActivity
        Bundle arguments = getIntent().getBundleExtra("notification");
        if (arguments != null) {
            Song s;
            try {
                s = (Song) arguments.get("song");
            } catch (ClassCastException e) {
                s = null;
            }
            if (s!= null) {
                SongDetailFragment fragment = new SongDetailFragment();
                fragment.setArguments(arguments);
                switchFragmentClearStack(fragment);
            }
        } else if (savedInstanceState == null) {
            // Load default fragment in this case. else. maybe configuration change, android will do their work
            Fragment fragment = new WelcomeFragment();
            switchFragmentClearStack(fragment);
        }

        mainView.addKeyboardStateChangedListener(new BunnyLayout.IKeyboardChanged() {
            @Override
            public void onKeyboardShown() {
            }

            @Override
            public void onKeyboardHidden() {
                LOGE(TAG, "On Keyboard Hidden");
                if (searchItem != null) MenuItemCompat.collapseActionView(searchItem);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL) {
        /*Just switch out keycode if KEYCODE_FORWARD_DEL if its not the correct one*/
            Toast.makeText(MainActivity.this, "YOU CLICKED Delete KEY",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        Toast.makeText(MainActivity.this, "Didnt work", Toast.LENGTH_SHORT)
                .show();
        return super.dispatchKeyEvent(e);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService(mp3ServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
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
        if (Mp3PlayerService.isRunning(getApplicationContext())) {
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putParcelableArrayList("playlist", playlistList);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            // Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
            MenuItemCompat.collapseActionView(searchItem);
            MenuItemCompat.collapseActionView(searchItem);
        }
    }

    //endregion

    //region Configuration Method
    //////////////////////////////////////////////////////////////////////
    ////////////////////// CONFIGURATION METHOD //////////////////////////


    //////////////////////////////////////////////////////////////////////
    // Back button navigation flow and title bar text change            //
    // 1. Every fragment is added to the stack. Use hashCode as name    //
    //    and tag.                                                      //
    //                                                                  //
    //     PlaylistManagerFragment                                      //
    //         PlaylistDetailFragment                                   //
    //              SongDetailFragment                                  //
    //                                                                  //
    // 2. Back button navigation: follow the stack. Only exit in        //
    //    WelcomeFragment (other level 0 fragment will change to        //
    //    WelcomeFragment when tap Back button).                        //
    //                                                                  //
    // 3. Action bar title: use getCurrentFragment to get the fragment  //
    //    after back button (afterBackFragment), then set title. If the //
    //    fragment is PlaylistDetailFragment or SongDetailFragment we   //
    //    need to set title as the playlist/song name.                  //
    //                                                                  //
    //                                                                  //
    //                                                                  //
    //                                                                  //

    @Override
    public void onBackPressed() {
        // put this setter here. for clearer rather than put in onCreate() method.
        // a missing magic number :)
        if (mTimePressBackBtn == 0) mTimePressBackBtn = -14181147;
        FragmentManager fragmentManager = getSupportFragmentManager();

        // close search view
        if (searchItem != null) {
            // should close search view
            LOGE(TAG, "Close SearchView on BackPress");
            MenuItemCompat.collapseActionView(searchItem);
            MenuItemCompat.collapseActionView(searchItem);
        }

        // Variable to know if the current fragment is WelcomeFragment or not.
        Fragment currentFragment = getCurrentFragment(fragmentManager, 0);

        // If current fragment is level 0 (only 1 fragment left in the stack)
        if (fragmentManager.getBackStackEntryCount() == 1) {
            if (currentFragment instanceof WelcomeFragment) {
                // If current fragment is WelcomeFragment, show the toast to user

                // in Welcome Fragment. Just exit when double click back press as Zing MP3
                long currentTime = Calendar.getInstance().getTimeInMillis();
                LOGE(TAG, mTimePressBackBtn + "/" + currentTime);
                if (currentTime < mTimePressBackBtn + Config.TOAST_LENGTH_SHORT) {
                    // in fact. exit app
                    // super.onBackPressed(); // << This will cause a blank screen (as described in BUG.txt)
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();
                    mTimePressBackBtn = currentTime;
                }

            } else {
                // If current fragment is not the WelcomeFragment, then navigate to WelcomeFragment

                // Open welcome fragment
                Fragment fragment = new WelcomeFragment();
                switchFragmentClearStack(fragment);
                changeTitleBar(getString(R.string.title_activity_welcome_fragment));

            }
        } else {
            // If there is more than 1 entry in the stack, we just follow the stack to navigate
            // fragments by call super.onBackPressed() method. But before that, we need to change
            // the title in action bar.

            // This will get the fragment after the back button (ignore the first fragment)
            Fragment afterBackFragment = getCurrentFragment(fragmentManager, 1);
            // Change title bar after change fragment.
            if (afterBackFragment != null) {
                int titleRes = ((CustomFragment) afterBackFragment).getTitle();
                if (titleRes > 0) {
                    // If this fragment has title, then set it.
                    changeTitleBar(getString(titleRes));
                } else {
                    // If this fragment is PlaylistDetailFragment or SongDetailFragment or things
                    // like that, we set the title as the playlist/song name.

                    // For playlist
                    if (afterBackFragment instanceof PlaylistDetailFragment) {
                        changeTitleBar(((PlaylistDetailFragment) afterBackFragment).playlist.playlistName);
                    }
                    // For song
                    else if (afterBackFragment instanceof SongDetailFragment) {
                        changeTitleBar(((SongDetailFragment) afterBackFragment).song.title);
                    }
                }
            }

            // Do the back
            super.onBackPressed();

        }
    }

    public void changeTitleBar(String title) {
        mTitle = title;
        restoreActionBar();
    }

    public void restoreActionBar() {
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MenuItem  searchItem;
    /**
     * Create ActionBar items
     * Items of Activity will be created before
     * Items of Fragment will be created after that
     * always try to call super method
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        searchItem = menu.findItem(R.id.search_bar);

        /** Setup SearchView */
        if (searchItem != null) {
            // Get the SearchView and set the Search Configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            // Use MenuItemCompat for comparable backward with API 10
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            // Assumes current mActivity is the searchable mActivity
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            // always iconic the search view
            mSearchView.setIconifiedByDefault(true);
            // enable submit button
            mSearchView.setSubmitButtonEnabled(true);
            // Returns whether query refinement is enabled for all items or only specific ones.
            //mSearchView.setQueryRefinementEnabled(true);
            // setup SearchView that lost focus after search
            mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        LOGE(TAG, "Close SearchView on LostFocus");
                        MenuItemCompat.collapseActionView(searchItem);
                    }

                }
            });
            mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        LOGE(TAG, "Close SearchView in QueryTextFocus");
                        MenuItemCompat.collapseActionView(searchItem);
                    }
                }
       });
        }
        // use this method for convenience
        bindCLoseSearchViewEvent(getWindow().getDecorView().getRootView());
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent mActivity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
            case R.id.search_bar:
                startSearch(null, false, Bundle.EMPTY, false);
                return true;
        }

         return super.onOptionsItemSelected(item);
    }

    /**
     * when search dialog is active. mActivity will lost input focus
     * so. do some stuffs (such as saving state) before search
     * Unless passing search mAppContext data.
     * should end method by calling super class implementation
     */
  /*  @Override
    public boolean onSearchRequested() {
        // doing some stuff before here
        LOGE(TAG, "On Search request");
        return super.onSearchRequested();
    }*/

    /**
     * set up navigation drawer view
     */
    public void setUpActionBar() {

        // set a custom shadow that overlays the main content when the drawer opens
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public void setUpSlidingMenu() {
        slidingMenu = getSlidingMenu();

        // customize look for SlidingMenu
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        // recommend width for navigation drawer. use same for SlidingViewer
        slidingMenu.setBehindWidthRes(R.dimen.navigation_drawer_width);
        slidingMenu.setFadeDegree(0.35f);

        // set custom action for SlidingMenu
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // just use this option if want include both two sidebar as some apps on Android App
        // slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
    }

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
            // get query string
            String queryStr = intent.getStringExtra(SearchManager.QUERY);
            // LOGE(TAG, "Search query: " + queryStr);
            // should close search view
            MenuItemCompat.collapseActionView(searchItem);
            // cache data for searching
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchRecentProvider.AUTHORITY, SearchRecentProvider.MODE);
            suggestions.saveRecentQuery(queryStr, null);
            // handle this search to fragment
            SearchResultFragment fragment = new SearchResultFragment();
            Bundle arguments = new Bundle();
            arguments.putString("search_key_word", queryStr);
            fragment.setArguments(arguments);
            switchFragmentClearStack(fragment);
        }
    }

    /**
     * set up mAdapter for list view
     * include add all views and adapters to currently ListView
     *
     * @return
     */
    public void setUpListView() {

        /**
         * setCacheColorHint : color background when scroll
         */
        mDrawerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerListView.setSelector(android.R.color.transparent);
        mDrawerListView.setCacheColorHint(Color.TRANSPARENT);

        /**
         * Using MergeAdapter for complex view
         * just create as many type of view in list as we want and Add to this Adapter
         */
        mergeAdapter = new MergeAdapter();

        /** setup mAdapter
         * create all adapters as we want
         * set delegate in onResume()
         * remove delegate on onPause()
         */
        // create all adapters as we want
        headerAdapter = new NavigationDrawerAdapter.HeaderAdapter(this);
        itemAdapter = new NavigationDrawerAdapter.ItemAdapter(getApplicationContext());
        playlistHeaderAdapter = new NavigationDrawerAdapter.PlaylistHeaderAdapter(getApplicationContext());
        playlistItemAdapter = new NavigationDrawerAdapter.PlaylistItemAdapter(this, playlistList);

        /**
         * Setting up for playlist changes callback.
         * Where there is changes of playlists, call SongListRightMenuHandler.updateNavDrawerPlaylistList();
         **/
        SongListRightMenuHandler.navDrawerPlaylistItemAdapter = playlistItemAdapter;

        /** assign each mAdapter to this composite mAdapter */
        mergeAdapter.addAdapter(headerAdapter);
        mergeAdapter.addAdapter(itemAdapter);
        mergeAdapter.addAdapter(playlistHeaderAdapter);
        mergeAdapter.addAdapter(playlistItemAdapter);

        /** assign this complex mAdapter to navigation drawer list*/
        mDrawerListView.setAdapter(mergeAdapter);

        /** Highlight Home fragment **/
        mDrawerListView.setItemChecked(headerAdapter.getCount(), true);
    }

    /** Set up Mp3 Service Start From MainActivity
     *  So easily to unbind later
     */
    public void setUpMp3Service() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                mp3Service = ((Mp3PlayerService.BackgroundAudioServiceBinder)iBinder).getService();
                player = mp3Service.player;
                if (player == null) {
                    LOGE(TAG, "PLAYER IS NULL WHEN BIND TO SERVICE");
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mp3Service = null;
            }
        };
        mp3ServiceIntent = new Intent(this, Mp3PlayerService.class);
        //mp3ServiceIntent.putExtra("song", song);
        startService(mp3ServiceIntent);
    }
    //endregion

    //region Main Activity Helper
    ////////////////////////////////////////////////////////////////////////
    //////////////////// SIMPLE HELPER METHOD //////////////////////////////

    public static enum COMMIT_TYPE {
        MAIN,
        SUB
    }

    public void switchFragmentNormal(Fragment fragment) {
        if (fragment == null) return;

        String tag = String.valueOf(StringUtils.randInt(
                Config.FRAGMENT_TAG_MIN, Config.FRAGMENT_TAG_MAX));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                // Add tag for back button tracking
                .add(R.id.content_frame, fragment, tag)
                // Hide the fragment instead of replace.
                // This will increase RAM usage but helpful for back button nav and infinity list view.
                .hide(getCurrentFragment(fragmentManager, 0))
                // Add this transaction to the back stack
                .addToBackStack(tag)
                .commit();
        slidingMenu.showContent();
        slidingMenu.setEnabled(false);
        // should close search view
        MenuItemCompat.collapseActionView(searchItem);
        MenuItemCompat.collapseActionView(searchItem);
        int titleRes = ((CustomFragment) fragment).getTitle();
        if (titleRes > 0) {
            changeTitleBar(getString(titleRes));
        }
    }

    public void switchFragmentClearStack(final Fragment fragment) {
        if (fragment == null) return;

        String tag = String.valueOf(StringUtils.randInt(
                Config.FRAGMENT_TAG_MIN, Config.FRAGMENT_TAG_MAX));

        FragmentManager fragmentManager = getSupportFragmentManager();
        // clear whole stack before add new fragment to stack
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                // Add tag for back button tracking
                .replace(R.id.content_frame, fragment, tag)
                .addToBackStack(tag)
                .commit();

        if (searchItem != null) { // << Could not run without this.
            // should close search view
            MenuItemCompat.collapseActionView(searchItem);
            MenuItemCompat.collapseActionView(searchItem);
        }
        int titleRes = ((CustomFragment) fragment).getTitle();
        if (titleRes > 0) {
            changeTitleBar(getString(titleRes));
        }


    }

    public void switchFragment(Fragment fragment, COMMIT_TYPE type) {
        switch (type) {
            case MAIN:
                switchFragmentClearStack(fragment);
                break;
            case SUB:
                switchFragmentNormal(fragment);
                break;
            default:
                // do nothing
        }
    }

    /**
     * Get current fragment using tag
     * http://stackoverflow.com/questions/15028527/is-there-a-way-to-get-fragment-from-top-of-stack
     * @param fragmentManager
     * @param ignoreTop: ignore number of entry on the top. Use 0 to get the top entry.
     * @return
     */
    private Fragment getCurrentFragment(FragmentManager fragmentManager, int ignoreTop){
        try {
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - ignoreTop - 1).getName();
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentByTag(fragmentTag);
            return currentFragment;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Nice function base on method have learnt at TMA Solution
     * recursive all view to bind event :)
     * */
    private void bindCLoseSearchViewEvent(View view) {

        if(!(view instanceof SearchView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    LOGE(TAG, "Close Search View in bindEvent");
                    MenuItemCompat.collapseActionView(searchItem);
                    return false;
                }
            });
        }

        // If currently layout is a layout container
        // iterate over its children and call recursive
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                bindCLoseSearchViewEvent(innerView);
            }
        }
    }
    //endregion

    ////////////////////////////////////////////////////////////////
    //////////////// METHOD OVERRIDE USE FOR ADAPTER ///////////////

    @Override
    public void gotoCategoryPage(NavigationDrawerAdapter.ItemAdapter.TYPE pageType, int position) {
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
                fragment = new SearchChordFragment();
                break;
            case SEARCH_CHORD:
                fragment = new ChordViewFragment();
                break;
            case SETTING:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        // setting for Drawer List View
        if (mDrawerListView != null) {
            LOGE("TRUNGDQ", "Main: set category: " + position);
            mDrawerListView.setItemChecked(position + headerAdapter.getCount(), true);
        }
        // Open Custom Fragment
        if (fragment != null) fragment.setArguments(arguments);
        switchFragmentClearStack(fragment);

    }

    @Override
    public void gotoPlayList(int playlistId) {
        Playlist playlist = playlistItemAdapter.playlists.get(playlistId);
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();

        // setting parameters
        Bundle arguments = new Bundle();
        arguments.putParcelable("playlist", playlist);
        fragment.setArguments(arguments);

        // setting for Drawer List View
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(
                    playlistId
                            + headerAdapter.getCount()
                            + itemAdapter.getCount()
                            + playlistHeaderAdapter.getCount(),
                    true);
        }

        changeTitleBar(playlist.playlistName);
        switchFragmentNormal(fragment);
    }

    /////////////////// HQT //////////////////////////////////////////
    //////////// NOTES ABOUT CALLBACK ///////////////////////////////
    /**
     * Callbacks interface that send data back from fragment to mActivity
     * Activity will use this function to determine what should to do
     * All activities using this fragment must implement this interface.
     * See this link for more detail:
     * @link{http://stackoverflow.com/questions/14213947/onattach-callback-from-fragment-to-activity}
     *
     * STEP :
     *  1. create interface
     *  2. assign mActivity to mCallback on onAttach() method
     *      (can do this because mActivity must be implements this method)
     *  on NavigationDrawer, when event arises. use mCallback
     */

}
