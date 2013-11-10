package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.DialogInterface;
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

import com.hqt.hac.helper.adapter.FavoriteManagerAdapter;
import com.hqt.hac.helper.adapter.PlaylistDetailAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.utils.SortUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;

public class FavoriteManagerFragment extends  Fragment implements AdapterView.OnItemSelectedListener {

    /** Main Activity for reference */
    MainActivity activity;

    /** ListView : contains all items of this fragment */
    ListView mListView;

    /** List of All songs in favorite */
    List<Song> songs;

    /** Adapter for this fragment */
    FavoriteManagerAdapter adapter;

    /** spinner of this fragment
     * use for user select display setting
     */
    Spinner spinner;

    public FavoriteManagerFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        songs = FavoriteDataAccessLayer.getAllFavoriteSongs(getActivity().getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myfavorite, container, false);

        /** Spinner configure */
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.favorite_sort_method, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        choices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(choices);    // Apply the adapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        /** ListView Configure */
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        adapter = new FavoriteManagerAdapter(getActivity().getApplicationContext(), songs);
        mListView.setAdapter(choices);

        return rootView;
    }

    /** if user click. the list will be sorted again base on choice */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            // sort by ABC
            case 0:
                SortUtils.sortSongByABC(songs);
                break;
            // sort by times
            case 1:
                SortUtils.sortSongByDate(songs);
                break;
            default:
                // do nothing
        }
        // refresh ListView

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
