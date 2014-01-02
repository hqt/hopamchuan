package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class PlaylistDetailFragment extends  Fragment implements IHacFragment, InfinityListView.ILoaderContent {

    private static String TAG = makeLogTag(PlaylistDetailFragment.class);

    /** Main Activity for reference */
    private MainActivity activity;

    /** One popup menu for all items **/
    private PopupWindow popupWindows = null;

    /** ListView : contains all items of this fragment */
    private InfinityListView mListView;

    /** Adapter for this fragment */
    private SongListAdapter mAdapter;

    /** public for access in MainActivity.onBackPressed (get playlist name) **/
    public Playlist playlist;

    private List<Song> songs;

    /** empty constructor
     * must have for fragment
     */
    public PlaylistDetailFragment() {

    }

    @Override
    public int getTitle() {
        return 0;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;

        // get arguments from main mActivity
        Bundle arguments = getArguments();
        if ((arguments.get("playlistId") != null)) {
            int playlistId = arguments.getInt("playlistId");
            this.playlist = PlaylistDataAccessLayer.getPlaylistById(getActivity().getApplicationContext(), playlistId);
        }

        /** more optimize */
        else if (arguments.get("playlist") != null) {
            this.playlist = (Playlist) arguments.get("playlist");
        }

        else {
            LOGE(TAG, "no suitable arguments to continues");
            return;
        }

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
        View rootView = inflater.inflate(R.layout.fragment_playlist_detail, container, false);

        songs = new ArrayList<Song>();

        mListView = (InfinityListView) rootView.findViewById(R.id.list);
        mAdapter = new SongListAdapter(activity, songs);
        InfinityListView.ListViewProperty property = new InfinityListView.ListViewProperty();
        property.Loader(this).Adapter(mAdapter).FirstProcessLoading(true).LoadingView(R.layout.list_item_loading)
                .NumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD).RunningBackground(true);
        mListView.setListViewProperty(property);

      /*  mListView.setLoader(this);
        mListView.setFirstProcessLoading(true);
        mListView.setNumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD);
        mListView.setRunningBackground(true);
        mListView.setAdapter(mAdapter);*/

        // Event for right menu click
        popupWindows = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindows);

        // Event received from mAdapter.
        mAdapter.contextMenuDelegate = new SongListAdapter.IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar, playlist.playlistId, mAdapter);
            }
        };


//        View emptyView = inflater.inflate(R.layout.list_item_playlist_empty, container, false);
//        mListView.setEmptyView(emptyView);

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


        return rootView;
    }


    @Override
    public List load(int offset, int count) {
        NetworkUtils.stimulateNetwork(Config.LOADING_SMOOTHING_DELAY);
        return PlaylistDataAccessLayer.getSongsFromPlaylist(
                getActivity().getApplicationContext(),
                playlist.playlistId,
                offset,
                count);
    }
}
