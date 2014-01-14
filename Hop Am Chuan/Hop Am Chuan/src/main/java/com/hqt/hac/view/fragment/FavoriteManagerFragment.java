package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.IContextMenu;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.FavoriteDataAccessLayer;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;

public class FavoriteManagerFragment extends CustomFragment implements
        AdapterView.OnItemSelectedListener,
        InfinityListView.ILoaderContent {

    public int titleRes = R.string.title_activity_my_favorite_fragment;

    /** Main Activity for reference */
    private MainActivity activity;

    /** ListView : contains all items of this fragment */
    private InfinityListView mListView;

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
    private String orderMode = HopAmChuanDBContract.Songs.SONG_ISFAVORITE;

//    private ComponentLoadHandler mHandler;
    private View rootView;
    private LayoutInflater inflater;

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
        rootView = inflater.inflate(R.layout.fragment_myfavorite, container, false);
        this.inflater = inflater;

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

        // Load component with a delay to reduce lag
//        mHandler = new ComponentLoadHandler();
//        Thread componentLoad = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(Config.LOADING_SMOOTHING_DELAY);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mHandler.sendMessage(mHandler.obtainMessage());
//            }
//        });
//        UIUtils.setOrientationLock(getActivity());
//        componentLoad.start();
        setUpComponents();
        return rootView;
    }

    private void setUpComponents() {
        songs = new ArrayList<Song>();
        mAdapter = new SongListAdapter(getActivity(), songs);

        /** ListView Configure */
        mListView = (InfinityListView) rootView.findViewById(R.id.list_view);
        InfinityListView.ListViewProperty property = new InfinityListView.ListViewProperty();
        property.Loader(this).Adapter(mAdapter).FirstProcessLoading(true).LoadingView(R.layout.list_item_loading)
                .NumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD).RunningBackground(true);
        mListView.setListViewProperty(property);
        mListView.setEmptyView(rootView.findViewById(R.id.empty));


        // Event for right menu click
        popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

        // Event received from mAdapter.
        mAdapter.contextMenuDelegate = new IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar);
            }
        };

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
    }

    /** if user click. the list will be sorted again base on choice */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            switch(position) {
                case 0:
                    // sort by times
                    orderMode = HopAmChuanDBContract.Songs.SONG_ISFAVORITE + " DESC";
                    songs = new ArrayList<Song>();
                    mAdapter.setSongs(songs);
                    break;
                case 1:
                    // sort by ABC
                    orderMode = HopAmChuanDBContract.Songs.SONG_TITLE;
                    songs = new ArrayList<Song>();
                    mAdapter.setSongs(songs);
                    break;
                default:
                    // do nothing
            }
            // refresh ListView
            mListView.resetListView(mAdapter);
            // mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public List load(int offset, int count) {
        NetworkUtils.stimulateNetwork(Config.LOADING_SMOOTHING_DELAY);
        return FavoriteDataAccessLayer.getSongsFromFavorite(
                getActivity().getApplicationContext(),
                orderMode,
                offset,
                count);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////// METHOD FOR ENDLESS LOADING //////////////////////////

    /////////////////
    //
    /////////////////
//    private class ComponentLoadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            setUpComponents();
//            UIUtils.releaseOrientationLock(getActivity());
//        }
//    }
}