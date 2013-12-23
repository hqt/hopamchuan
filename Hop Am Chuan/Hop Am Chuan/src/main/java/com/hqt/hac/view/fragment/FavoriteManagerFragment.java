package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.hqt.hac.helper.adapter.FavoriteManagerAdapter;
import com.hqt.hac.helper.adapter.PlaylistDetailAdapter;
import com.hqt.hac.helper.widget.DialogFactory;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.SortUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
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

    /** One popup menu for all items **/
    PopupWindow pw = null;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myfavorite, container, false);

        /** Spinner configure */
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.favorite_sort_method, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        choices.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(choices);    // Apply the adapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        /** ListView Configure */
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        adapter = new FavoriteManagerAdapter(getActivity(), songs);

        // Event for right menu click
        pw = DialogFactory.createPopup(inflater, R.layout.popup_songlist_menu);
        HacUtils.setRightMenuEvents(activity, pw);

        // Event received from adapter.
        adapter.rightMenuClick = new FavoriteManagerAdapter.RightMenuClick() {
            @Override
            public void onRightMenuClick(View view, Song song) {
                // Show the popup menu and set selectedSong
                /** Store the song that user clicked on the right menu (the star) **/
                HacUtils.selectedSong = song;
                pw.showAsDropDown(view);
            }
        };

        mListView.setAdapter(adapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SongDetailFragment fragment = new SongDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putSerializable("song", songs.get(position));
                fragment.setArguments(arguments);
                activity.switchFragment(fragment);
            }
        });


        return rootView;
    }

    /** if user click. the list will be sorted again base on choice */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                // sort by times
                SortUtils.sortSongByDate(songs);
                break;
            case 1:
                // sort by ABC
                SortUtils.sortSongByABC(songs);
                break;
            default:
                // do nothing
        }
        // refresh ListView
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
