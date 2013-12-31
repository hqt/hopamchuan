package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hqt.hac.helper.adapter.PlaylistManagerAdapter;
import com.hqt.hac.helper.widget.IHacFragment;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.helper.widget.PlaylistRightMenuHandler;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

public class PlaylistManagerFragment extends Fragment implements PlaylistManagerAdapter.IPlaylistManagerAdapter, IHacFragment {

    public int titleRes = R.string.title_activity_my_playlist_fragment;

    /** Main Activity for reference */
    private MainActivity activity;

    /**
     * ListView of this fragment
     */
    private ListView mListView;

    /**
     * Model for this View
     */
    private List<Playlist> allPlaylists;

    /** One popup menu for all items **/
    private PopupWindow popupWindow = null;

    /**
     * Adapter for this View
     */
    PlaylistManagerAdapter adapter;

    public PlaylistManagerFragment() {
    }



    @Override
    public int getTitle() {
        return titleRes;
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
        allPlaylists = PlaylistDataAccessLayer.getAllPlayLists(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_manager, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list);
        adapter = new PlaylistManagerAdapter(activity.getApplicationContext(), allPlaylists);

        popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_playlist_list_menu);
        PlaylistRightMenuHandler.setRightMenuEvents(activity, popupWindow, adapter);

        // Event received from mAdapter.
        adapter.rightMenuClick = new PlaylistManagerAdapter.RightMenuClick() {
            @Override
            public void onRightMenuClick(View view, Playlist playlist) {
                // Show the popup menu and set selectedSong
                /** Store the song that user clicked on the right menu (the star) **/
                PlaylistRightMenuHandler.selectedPlaylist = playlist;
                popupWindow.showAsDropDown(view);
            }
        };


        mListView.setAdapter(adapter);
        View emptyView = inflater.inflate(R.layout.list_item_playlist_empty, container, false);
        mListView.setEmptyView(emptyView);

        // add click event item for this ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistDetailFragment fragment = new PlaylistDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("playlist", allPlaylists.get(position));
                fragment.setArguments(arguments);
                activity.changeTitleBar(allPlaylists.get(position).playlistName);
                activity.switchFragmentNormal(fragment);
            }
        });

        return rootView;
    }

    @Override
    public void sharePlaylist() {

    }

    @Override
    public void renamePlaylist() {

    }

    @Override
    public void deletePlaylist() {
    }
}
