package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.InfinityAdapter;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Fragment uses for viewing songs as categories
 */
public class SongListFragment extends Fragment implements AdapterView.OnItemSelectedListener, InfinityListView.ILoaderContent, InfinityAdapter.ILoaderContent {

    public static final String TAG = makeLogTag(SongListFragment.class);

    /** Main Activity for reference */
    MainActivity activity;
    InfinityListView mListView;
    List<Song> songs;

    /** One popup menu for all items **/
    PopupWindow popupWindow = null;

    /** Adapter for this fragment */
    SongListAdapter songlistAdapter;

    /** Adapter use for loading when go to ending list */
    InfinityAdapter infAdapter;

    public SongListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
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
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

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


        /** Default song list **/
        songs = SongDataAccessLayer.getRecentSongs(activity.getApplicationContext(), 0, 3);

        /** ListView Configure */
        mListView = (InfinityListView) rootView.findViewById(R.id.list_view);
        mListView.setLoader(this);
        songlistAdapter = new SongListAdapter(activity, songs);
        infAdapter = new InfinityAdapter(activity.getApplicationContext(), songlistAdapter);
        infAdapter.setLoader(this);

        // Event for right menu click
        popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

        // Event received from mAdapter.
        songlistAdapter.contextMenuDelegate = new SongListAdapter.IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar);
//
// TrungDQ: moved TO SongListRightMenuHandler.openPopupMenu
/***************************************************************************************
//                int availableHeight = popupWindow.getMaxAvailableHeight(view);
//                popupWindow.showAsDropDown(view);
//                int height = popupWindow.getHeight();
//                LOGE(TAG, "HQT POPUP Height: " + height);
//                if (availableHeight < popupWindow.getHeight()) {
//                    int[] loc_int = new int[2];
//                    // popupWindow.showAsDropDown(view, 10, 10);
//                    LOGE(TAG, "Not Enough Room Space");
//                    popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 35, 35);
//                } else {
//
//                }
//                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 35, 35);
//**************************************************************************************/

            }
        };

        mListView.setAdapter(songlistAdapter);
        // mListView.setAdapter(infAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SongDetailFragment fragment = new SongDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("song", songs.get(position));
                fragment.setArguments(arguments);
                activity.switchFragmentNormal(fragment);
            }
        });

       /* mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int id = 1;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                LOGE(TAG, "On Scroll State Changed");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                LOGE(TAG, "On Scroll");
            }
        });*/

        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                // Moi xem gan day
                songs = SongDataAccessLayer.getRecentSongs(activity.getApplicationContext(), 0, Config.DEFAULT_SONG_LIST_COUNT);
                songlistAdapter.setSongs(songs);
                break;
            case 1:
                // Moi cap nhat
                songs = SongDataAccessLayer.getNewSongs(activity.getApplicationContext(), 0, Config.DEFAULT_SONG_LIST_COUNT);
                songlistAdapter.setSongs(songs);
                break;
            case 2:
                // Bai hat ngau nhien
                songs = SongDataAccessLayer.getRandSongs(activity.getApplicationContext(), Config.DEFAULT_SONG_LIST_COUNT);
                songlistAdapter.setSongs(songs);
                break;
            default:
                // do nothing
        }

        songlistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////// METHOD FOR ENDLESS LOADING //////////////////////////
    Song s;
    int cth = 0;
    @Override
    public void load(int index) {
        NetworkUtils.stimulateNetwork(3);
        LOGE(TAG, "Add a Song to Inf ListView");
        s = SongDataAccessLayer.getSongById(getActivity().getApplicationContext(), 1);
        s.title = s.title + " " + cth++;
    }

    @Override
    public void load(int from, int to) {

    }

    @Override
    public void append() {
        songs.add(s);
    }
}
