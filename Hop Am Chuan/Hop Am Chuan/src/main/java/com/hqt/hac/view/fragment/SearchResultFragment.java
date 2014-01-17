package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.ArtistAdapter;
import com.hqt.hac.helper.adapter.IContextMenu;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.ArtistDataAccessLayer;
import com.hqt.hac.model.dal.ChordDataAccessLayer;
import com.hqt.hac.model.dal.SongDataAccessLayer;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.StringUtils;
import com.hqt.hac.view.BunnyApplication;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Fragment to show search result
 * Created by ThaoHQSE60963 on 1/8/14.
 */
public class SearchResultFragment extends CustomFragment implements
        InfinityListView.ILoaderContent,
        AdapterView.OnItemSelectedListener {

    private static String TAG = makeLogTag(SearchResultFragment.class);

    /** Activity running this fragment */
    MainActivity activity;

    /** List contains search data */
    InfinityListView mListView;

    /** One popup menu for all items **/
    PopupWindow popupWindow = null;

    /** inflater use to inflate layout */
    LayoutInflater inflater;

    /** Adapter for ListView
     * use old ones become it's same
     */
    BaseAdapter mAdapter;

    /** current type of search query */
    int type = 0;

    /** current query string */
    String queryStr;

    private List<Chord> chords;
    private View rootView;

    /** empty constructor
     * must have for fragment
     */
    public SearchResultFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        // get arguments from main mActivity
        Bundle arguments = getArguments();
        if ((arguments.getString(Config.BUNDLE_KEYWORD) != null)) {
            String tmpQueryStr = arguments.getString(Config.BUNDLE_KEYWORD);
            this.activity.changeTitleBar(getString(R.string.search_title) + " \"" + tmpQueryStr + "\"");
            queryStr = StringUtils.removeAcients(tmpQueryStr).trim();
            // LOGE(TAG, "Query String::: " + queryStr);
        }

        if (arguments.getString(Config.BUNDLE_IS_CHORD_SEARCH) != null) {
            // Search chords: hide spinner.
            String chordsStr = arguments.getString(Config.BUNDLE_IS_CHORD_SEARCH);
            if (chordsStr != null) {
                chords = new ArrayList<Chord>();
                String[] strChords = chordsStr.split(",");
                for (String chord : strChords) {
                    chords.add(ChordDataAccessLayer.getChordByName(
                            getActivity().getApplicationContext(), chord));
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_search_view, container, false);
        this.inflater = inflater;

        mListView = (InfinityListView) rootView.findViewById(R.id.list_view);


        if (chords == null) {
            /** Spinner : create mAdapter for Spinner */
            Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_method_list);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.
                    createFromResource(BunnyApplication.getAppContext(),
                            R.array.search_method, R.layout.custom_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            spinner.setAdapter(adapter);    // Apply the mAdapter to the spinner
            spinner.setOnItemSelectedListener(this);    // because this fragment has implemented method
        } else {
            /** Hide the spinner **/
            RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.relative_layout_spinner);
            RelativeLayout layoutListView = (RelativeLayout) rootView.findViewById(R.id.relative_layout_listview);
            layout.setVisibility(View.GONE);
            layoutListView.setPadding(0,0,0,0);

            /** Set SongListAdapter as adapter **/
            mAdapter = new SongListAdapter(BunnyApplication.getAppContext(), new ArrayList<Song>());
            // Event for right menu click
            popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
            SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

            // Event received from mAdapter.
            ((SongListAdapter)mAdapter).contextMenuDelegate = new IContextMenu() {
                @Override
                public void onMenuClick(View view, Song song, ImageView theStar) {
                    // Show the popup menu and set selectedSong, theStar
                    SongListRightMenuHandler.openPopupMenu(view, song, theStar);
                }
            };
            bindEventListView();

            mListView.setAdapter(mAdapter);
        }



        return rootView;
    }

    @Override
    public Collection load(int offset, int count) {
        NetworkUtils.stimulateNetwork(Config.LOADING_SMOOTHING_DELAY);
        Collection res = null;
        if (chords == null) {
            switch (type) {
                case 0:
                    res = SongDataAccessLayer.searchSongByTitle(queryStr, offset, count);
                    break;
                case 1:
                    res = ArtistDataAccessLayer.searchArtistByName(queryStr, offset, count);
                    break;
            }
        } else {
            res = ChordDataAccessLayer.getAllSongsByChordArrays(
                    getActivity().getApplicationContext(),
                    chords,
                    offset,
                    count);
        }
        return res;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                // search by artist
                type = 0;
                mAdapter = new SongListAdapter(BunnyApplication.getAppContext(), new ArrayList<Song>());
                // Event for right menu click
                popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
                SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

                // Event received from mAdapter.
                ((SongListAdapter)mAdapter).contextMenuDelegate = new IContextMenu() {
                    @Override
                    public void onMenuClick(View view, Song song, ImageView theStar) {
                        // Show the popup menu and set selectedSong, theStar
                        SongListRightMenuHandler.openPopupMenu(view, song, theStar);
                    }
                };
                bindEventListView();
                break;
            case 1:
                // search by author and song
                type  = 1;
                mAdapter = new ArtistAdapter(BunnyApplication.getAppContext());
                bindEventListView();
                break;
            case 2:
                // search by singer
                // currently no need this option
                type = 2;
                break;
            default:
                // do nothing
        }
        // mListView.setAdapter(mAdapter);
        // TrungDQ: We NEED this for footer and stuffs when changing dropdown menu item.
        if (position == 0) {
            mListView.ignoreIgnoreFirstChange = true;
        }
        // refresh ListView
        reloadInfListView();
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

        mListView.resetListView(mAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public int getTitle() {
        return R.string.search_title;
    }

    private void bindEventListView() {
        switch (type) {
            case 0 : {
                // Event for Item Click on ListView
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        SongDetailFragment fragment = new SongDetailFragment();
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("song", ((SongListAdapter) mAdapter).songs.get(position));
                        fragment.setArguments(arguments);
                        activity.switchFragmentNormal(fragment);
                        activity.changeTitleBar(((SongListAdapter)mAdapter).songs.get(position).title);
                    }
                });
                break;
            }
            case 1: {
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArtistViewFragment fragment = new ArtistViewFragment();
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("artist", ((ArtistAdapter) mAdapter).artists.get(position));
                        fragment.setArguments(arguments);
                        activity.switchFragmentNormal(fragment);
                        activity.changeTitleBar(((ArtistAdapter)mAdapter).artists.get(position).artistName);
                    }
                });
                break;
            }
            case 2: {
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // currently no need this option
                    }
                });
                break;
            }
        }
    }
}
