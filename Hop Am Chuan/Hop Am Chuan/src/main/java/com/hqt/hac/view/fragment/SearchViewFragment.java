package com.hqt.hac.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.ArtistDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.model.dal.SongDataAccessLayer;
import com.hqt.hac.view.BunnyApplication;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.Collection;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Fragment to show search result
 * Created by ThaoHQSE60963 on 1/8/14.
 */
public class SearchViewFragment extends Fragment implements IHacFragment, InfinityListView.ILoaderContent, AdapterView.OnItemSelectedListener {

    private static String TAG = makeLogTag(SearchViewFragment.class);

    /** List contains search data */
    InfinityListView mListView;

    /** Adapter for ListView
     * use old ones become it's same
     */
    BaseAdapter mAdapter;

    /** current type of search query */
    int type = 0;

    /** current query string */
    String queryStr;

    /** empty constructor
     * must have for fragment
     */
    public SearchViewFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // get arguments from main mActivity
        Bundle arguments = getArguments();
        if ((arguments.getString("search_key_word") != null)) {
            queryStr = arguments.getString("search_key_word");
            LOGE(TAG, "Query String: " + queryStr);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search_view, container, false);

        mListView = (InfinityListView) rootView.findViewById(R.id.list_view);

        /** Spinner : create mAdapter for Spinner */
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_method_list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.
                createFromResource(BunnyApplication.getAppContext(),
                        R.array.song_list_method, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(adapter);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);    // because this fragment has implemented method


        /** config mode for this ListView.
         *  this ListView is full rich function. See document for more detail
         */
        InfinityListView.ListViewProperty property = new InfinityListView.ListViewProperty();
        property.Loader(this).Adapter(mAdapter).FirstProcessLoading(true).LoadingView(R.layout.list_item_loading)
                .NumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD).RunningBackground(true);
        mListView.setListViewProperty(property);

        return rootView;
    }

    @Override
    public Collection load(int offset, int count) {
        Collection res = null;
        switch (type) {
            case 0:
                res = SongDataAccessLayer.searchSongByTitle(queryStr, offset, count);
                break;
            case 1:
                res = ArtistDataAccessLayer.searchSongBySinger(queryStr, 100);
                break;
            case 2:
                res = ArtistDataAccessLayer.searchSongByAuthor(queryStr, 100);
        }
        return res;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                // search by artist
                mAdapter = new SongListAdapter(BunnyApplication.getAppContext(), new ArrayList<Song>());
                break;
            case 1:
                // search by author
                break;
            case 2:
                // search by singer
                break;
            default:
                // do nothing
        }
        // mListView.resetListView(mAdapter);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public int getTitle() {
        return 0;
    }
}
