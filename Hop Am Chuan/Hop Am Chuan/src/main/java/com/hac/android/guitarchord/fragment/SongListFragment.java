package com.hac.android.guitarchord.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.hac.android.config.Config;
import com.hac.android.guitarchord.MainActivity;
import com.hac.android.helper.adapter.IContextMenu;
import com.hac.android.helper.adapter.SongListAdapter;
import com.hac.android.helper.widget.InfinityListView;
import com.hac.android.helper.widget.SongListRightMenuHandler;
import com.hac.android.model.Song;
import com.hac.android.model.dal.SongDataAccessLayer;
import com.hac.android.utils.DialogUtils;
import com.hac.android.utils.LogUtils;
import com.hac.android.utils.NetworkUtils;
import com.hac.android.guitarchord.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment uses for viewing songs as categories
 */
public class SongListFragment extends CustomFragment implements AdapterView.OnItemSelectedListener,
        InfinityListView.ILoaderContent {

    public static final String TAG = LogUtils.makeLogTag(SongListFragment.class);

    public int titleRes = R.string.title_activity_song_list_fragment;

    /** Main Activity for reference */
    private MainActivity activity;
    private InfinityListView mListView;
    private List<Song> songs;

    /** One popup menu for all items **/
    private PopupWindow popupWindow = null;

    /** Adapter for this fragment */
    private SongListAdapter songlistAdapter;


    /** song list mode
     * 0: recent songs
     * 1: new songs
     * 2: rand songs
     * **/
    private int songListMode = 0;

//    private ComponentLoadHandler mHandler;
    View rootView;
    LayoutInflater inflater;

    public SongListFragment() {
    }



    @Override
    public int getTitle() {
        return titleRes;
    }
    @Override
    public void onAttach(Activity activity) {
        LogUtils.LOGE(TAG, "On Attach");
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.LOGE(TAG, "OnCreateView");
        rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        this.inflater = inflater;

        /** Spinner : create mAdapter for Spinner */
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_method_list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.song_list_method, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(adapter);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);    // because this fragment has implemented method

        // Load component with a delay to reduce lag
//        mHandler = new ComponentLoadHandler();
//        Thread componentLoad = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(Config.LOADING_SMOOTHING_DELAY);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mHandler.sendMessage(mHandler.obtainMessage());
//            }
//        });
//        UIUtils.setOrientationLock(getActivity());
//        componentLoad.start();
        setUpComponents();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getSlidingMenu().showContent();
        activity.getSlidingMenu().setEnabled(true);
    }

    private void setUpComponents() {
        /** Default song list **/
        //songs = SongDataAccessLayer.getRecentSongs(activity.getApplicationContext(), 0, 0);
        songs = new ArrayList<Song>();
        songlistAdapter = new SongListAdapter(activity, songs);

        /** ListView Configure */
        mListView = (InfinityListView) rootView.findViewById(R.id.list_view);


        // Event for right menu click
        popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

        // Event received from mAdapter.
        songlistAdapter.contextMenuDelegate = new IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar);
            }
        };

        // Event for Item Click on ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SongDetailFragment fragment = new SongDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("song", songs.get(position));
                fragment.setArguments(arguments);
                activity.switchFragmentNormal(fragment);
                activity.changeTitleBar(songs.get(position).title);
            }
        });
    }
    int loadCount = 0;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.LOGE(TAG, "---------- On Item Selected, firstLoad: " + loadCount);
        songs = new ArrayList<Song>();
        songlistAdapter.setSongs(songs);
        // Set mode
        songListMode = position;
        if (position == 0) {
            mListView.ignoreIgnoreFirstChange = true;
        }
        // Reset the ListView
        reloadInfListView();
        loadCount++;
    }

    private void reloadInfListView() {
        /** config mode for this ListView.
         *  this ListView is full rich function. See document for more detail
         */
        InfinityListView.ListViewProperty property = new InfinityListView.ListViewProperty();
        property.Loader(this).FirstProcessLoading(true).LoadingView(R.layout.list_item_loading)
                .NumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD).RunningBackground(true);
        mListView.setListViewProperty(property);
        mListView.setEmptyView(rootView.findViewById(R.id.empty));

        mListView.resetListView(songlistAdapter);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////// METHOD FOR ENDLESS LOADING //////////////////////////
    @Override
    public List load(int offset, int count) {
        NetworkUtils.stimulateNetwork(Config.LOADING_SMOOTHING_DELAY);
        List<Song> result;
        switch (songListMode) {
            case 0:
                result = SongDataAccessLayer.getRecentSongs(getActivity().getApplicationContext(), offset, count);
                break;
            case 1:
                result = SongDataAccessLayer.getNewSongs(getActivity().getApplicationContext(), offset, count);
                break;
            case 2:
                result = SongDataAccessLayer.getRandSongs(getActivity().getApplicationContext(), count);
                break;
            default:
                result = new ArrayList<Song>();
        }
        return result;
    }

    /////////////////
    //
    /////////////////
//    private class ComponentLoadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            setUpComponents();
//            UIUtils.releaseOrientationLock(getActivity());
//        }
//    }
}
