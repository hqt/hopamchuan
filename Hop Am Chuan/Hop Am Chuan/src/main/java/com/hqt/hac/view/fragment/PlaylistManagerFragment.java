package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hqt.hac.helper.adapter.PlaylistManagerAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

public class PlaylistManagerFragment extends Fragment implements PlaylistManagerAdapter.IPlaylistManagerAdapter {
    /** Main Activity for reference */
    MainActivity activity;

    /**
     * ListView of this fragment
     */
    private ListView mListView;

    /**
     * Model for this View
     */
    List<Playlist> allPlaylists;

    /**
     * Adapter for this View
     */
    PlaylistManagerAdapter adapter;

    public PlaylistManagerFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
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
        mListView.setAdapter(adapter);

        // add click event item for this ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistDetailFragment fragment = new PlaylistDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable("playlist", allPlaylists.get(position));
                fragment.setArguments(arguments);
                activity.switchFragment(fragment);
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
