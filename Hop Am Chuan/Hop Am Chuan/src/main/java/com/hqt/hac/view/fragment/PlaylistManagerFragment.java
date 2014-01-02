package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hqt.hac.helper.adapter.PlaylistManagerAdapter;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allPlaylists = PlaylistDataAccessLayer.getAllPlayLists(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_playlist_manager, container, false);

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
                // popupWindow.showAsDropDown(view);
                int x = view.getLeft();
                int y = view.getBottom();
                LOGE(TAG, "Location On Screen Of View: " + x + "\t" + y);
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
            }
        };

       /* ViewTreeObserver vto = rootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("TEST", "Height = " + rootView.getHeight() + " Width = " + rootView.getWidth());
                ViewTreeObserver obs = rootView.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });*/

        mListView.setAdapter(adapter);
        View emptyView = inflater.inflate(R.layout.list_item_playlist_empty, container, false);
        mListView.setEmptyView(emptyView);

        // add click event item for this ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistDetailFragment fragment = new PlaylistDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("playlist", allPlaylists.get(position));
                fragment.setArguments(arguments);
                activity.changeTitleBar(allPlaylists.get(position).playlistName);
                activity.switchFragmentNormal(fragment);
            }
        });

        return rootView;
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
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
