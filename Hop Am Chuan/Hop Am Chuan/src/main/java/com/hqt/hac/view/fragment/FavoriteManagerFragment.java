package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.IHacFragment;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.FavoriteDataAccessLayer;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.SortUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;

public class FavoriteManagerFragment extends  Fragment implements AdapterView.OnItemSelectedListener, IHacFragment {

    public int titleRes = R.string.title_activity_my_favorite_fragment;

    /** Main Activity for reference */
    private MainActivity activity;

    /** ListView : contains all items of this fragment */
    private ListView mListView;

    /** List of All songs in favorite */
    private List<Song> songs;

    /** Adapter for this fragment */
    private SongListAdapter mAdapter;

    /** One popup menu for all items **/
    private PopupWindow popupWindow = null;

    /** spinner of this fragment
     * use for user select display setting
     */
    Spinner spinner;

    public FavoriteManagerFragment() {
    }


    @Override
    public int getTitle() {
        return titleRes;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        songs = FavoriteDataAccessLayer.getAllFavoriteSongs(getActivity().getApplicationContext());
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
        spinner.setAdapter(choices);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        /** ListView Configure */
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mAdapter = new SongListAdapter(getActivity(), songs);

        // Event for right menu click
        popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

        // Event received from mAdapter.
        mAdapter.contextMenuDelegate = new SongListAdapter.IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar);
            }
        };

        mListView.setAdapter(mAdapter);
        View emptyView = inflater.inflate(R.layout.list_item_playlist_empty, container, false);
        mListView.setEmptyView(emptyView);


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
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
