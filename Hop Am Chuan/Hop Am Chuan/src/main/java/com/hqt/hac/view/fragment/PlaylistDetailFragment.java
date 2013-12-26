package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class PlaylistDetailFragment extends  Fragment {

    private static String TAG = makeLogTag(PlaylistDetailFragment.class);

    /** Main Activity for reference */
    MainActivity activity;

    /** One popup menu for all items **/
    PopupWindow popupWindows = null;

    /** ListView : contains all items of this fragment */
    ListView mListView;

    /** Adapter for this fragment */
    SongListAdapter mAdapter;

    Playlist playlist;
    List<Song> songs;

    /** empty constructor
     * must have for fragment
     */
    public PlaylistDetailFragment() {

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

        songs = PlaylistDataAccessLayer.getAllSongsFromPlaylist(activity.getApplicationContext(),
                playlist.playlistId);
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

        mListView = (ListView) rootView.findViewById(R.id.list);
        mAdapter = new SongListAdapter(activity, songs);


        // Event for right menu click
        popupWindows = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindows);

        // Event received from mAdapter.
        mAdapter.contextMenuDelegate = new SongListAdapter.IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song) {
                // Show the popup menu and set selectedSong
                /** Store the song that user clicked on the right menu (the star) **/
                SongListRightMenuHandler.selectedSong = song;
                popupWindows.showAsDropDown(view);
            }
        };


        mListView.setAdapter(mAdapter);

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


        return rootView;
    }


}
