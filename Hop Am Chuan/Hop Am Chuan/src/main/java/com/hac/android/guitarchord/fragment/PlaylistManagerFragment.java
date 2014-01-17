package com.hac.android.guitarchord.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hac.android.guitarchord.MainActivity;
import com.hac.android.helper.adapter.PlaylistManagerAdapter;
import com.hac.android.helper.widget.PlaylistRightMenuHandler;
import com.hac.android.helper.widget.SongListRightMenuHandler;
import com.hac.android.model.Playlist;
import com.hac.android.model.dal.PlaylistDataAccessLayer;
import com.hac.android.utils.DialogUtils;
import com.hac.android.utils.LogUtils;
import com.hac.android.guitarchord.R;

import java.util.List;

public class PlaylistManagerFragment extends CustomFragment implements
        PlaylistManagerAdapter.IPlaylistManagerAdapter {

    private static final String TAG = LogUtils.makeLogTag(PlaylistManagerFragment.class);

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


//    private ComponentLoadHandler mHandler;
    private View rootView;
    private LayoutInflater inflater;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.playlist_manager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_playlist:
                createPlaylist();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allPlaylists = PlaylistDataAccessLayer.getAllPlayLists(getActivity().getApplicationContext());
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_playlist_manager, container, false);
        this.inflater = inflater;

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
        mListView = (ListView) rootView.findViewById(R.id.list);
        adapter = new PlaylistManagerAdapter(getActivity().getApplicationContext(), allPlaylists);

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
        mListView.setEmptyView(rootView.findViewById(R.id.empty));

        // add click event item for this ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistDetailFragment fragment = new PlaylistDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("playlist", adapter.playLists.get(position));
                fragment.setArguments(arguments);
                activity.changeTitleBar(adapter.playLists.get(position).playlistName);
                activity.switchFragmentNormal(fragment);
            }
        });


        /***** New playlist dialog *****/
        SongListRightMenuHandler.mListView = mListView;
        SongListRightMenuHandler.activity = getActivity();
        SongListRightMenuHandler.playlistManagerAdapter = adapter;
        SongListRightMenuHandler.newPlaylistDialog = DialogUtils.createDialog(activity, R.string.new_playlist,
                activity.getLayoutInflater(), R.layout.dialog_newplaylist);
        Button createPlaylistBtn =
                (Button) SongListRightMenuHandler.newPlaylistDialog
                        .findViewById(R.id.btnCreatePlaylist);
        createPlaylistBtn.setOnClickListener(new SongListRightMenuHandler.NewPlaylistOnClick());
        /**************/
        // Event to add new playlist
        SongListRightMenuHandler.txtNewPlaylistName =
                (EditText) SongListRightMenuHandler.newPlaylistDialog
                        .findViewById(R.id.txtNewPlaylistName);
        SongListRightMenuHandler.txtNewPlaylistDescription =
                (EditText) SongListRightMenuHandler.newPlaylistDialog
                        .findViewById(R.id.txtNewPlaylistDescription);

        /** Create new playlist button (when playlist is empty) **/
        Button createPlaylistBtnEmptyLayout = (Button) rootView.findViewById(R.id.createPlaylistBtn);
        createPlaylistBtnEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPlaylist();
            }
        });


        /** Call create playlist dialog if called **/
        Bundle arguments = getArguments();
        if (arguments.getBoolean("createPlaylist", false)) {
            createPlaylist();
        }
    }


    public void createPlaylist() {
        SongListRightMenuHandler.txtNewPlaylistName.setText("");
        SongListRightMenuHandler.txtNewPlaylistDescription.setText("");
        SongListRightMenuHandler.txtNewPlaylistName.requestFocus();
        SongListRightMenuHandler.newPlaylistDialog.show();
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


    /////////////////
    //
    /////////////////
//    private class ComponentLoadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            setUpComponents();
//            UIUtils.releaseOrientationLock(getActivity());
//
//        }
//    }

}
