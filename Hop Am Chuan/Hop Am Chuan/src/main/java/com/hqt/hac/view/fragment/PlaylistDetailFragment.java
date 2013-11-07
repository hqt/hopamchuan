package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.hqt.hac.helper.adapter.PlaylistDetailAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

public class PlaylistDetailFragment extends  Fragment {

    /** Main Activity for reference */
    MainActivity activity;

    /** ListView : contains all items of this fragment */
    ListView mListView;

    /** Adapter for this fragment */
    PlaylistDetailAdapter adapter;

    Playlist playlist;
    List<Song> songs;

    public PlaylistDetailFragment(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        songs = PlaylistDataAccessLayer.getAllSongsFromPlaylist(activity.getApplicationContext(),
                playlist.playlistId);
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
        adapter = new PlaylistDetailAdapter(getActivity().getApplicationContext(), playlist, songs);
        mListView.setAdapter(adapter);

        return rootView;
    }
}
