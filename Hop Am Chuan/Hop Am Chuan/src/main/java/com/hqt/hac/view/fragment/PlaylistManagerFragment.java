package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.PlaylistManagerAdapter;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.helper.widget.PlaylistRightMenuHandler;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class PlaylistManagerFragment extends Fragment implements PlaylistManagerAdapter.IPlaylistManagerAdapter, IHacFragment {

    private static final String TAG = makeLogTag(PlaylistManagerFragment.class);

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


    private ComponentLoadHandler mHandler;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_playlist_manager, container, false);
        this.inflater = inflater;

        // Load component with a delay to reduce lag
        mHandler = new ComponentLoadHandler();
        Thread componentLoad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Config.LOADING_SMOOTHING_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        });
        componentLoad.start();
        return rootView;
    }

    private void setUpComponents() {
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
//        View emptyView = inflater.inflate(R.layout.list_item_playlist_empty, container, false);
//        mListView.setEmptyView(emptyView);

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
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
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
    private class ComponentLoadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            setUpComponents();
        }
    }
}
