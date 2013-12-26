package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDetailFragment extends Fragment {

    private static String TAG = makeLogTag(PlaylistDetailFragment.class);

    /**
     * Main Activity for reference
     */
    MainActivity activity;

    /**
     * ListView : contains all items of this fragment
     */
    ListView mListView;

    /**
     * Adapter for this fragment
     */
    // PlaylistDetailAdapter mAdapter;
    Song song;

    // Stuff for popup menu
    private PopupWindow mPopupMenu;
    private View mMenuLayout;
    private boolean isPopupOpened = false;

    /**
     * empty constructor
     * must have for fragment
     */
    public SongDetailFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;

        // get arguments from main mActivity
        Bundle arguments = getArguments();
        if (arguments.get("song") != null) {
            this.song = (Song) arguments.get("song");
        } else {
            LOGE(TAG, "no suitable arguments to continues");
            return;
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_song_detail, container, false);

        // Set song info
        TextView songTitleTV = (TextView) rootView.findViewById(R.id.songTitle);
        TextView songAuthorsTV = (TextView) rootView.findViewById(R.id.songAuthorsTV);
        TextView songSingersTV = (TextView) rootView.findViewById(R.id.songSingersTV);
        TextView songContentTV = (TextView) rootView.findViewById(R.id.songContent);

        songTitleTV.setText(song.title);
        songAuthorsTV.setText(song.getAuthorsString(activity.getApplicationContext()));
        songSingersTV.setText(song.getSingersString(activity.getApplicationContext()));

        // Set song content
        // HacUtils.setSongFormatted(mActivity.getApplicationContext(), songContentTV, song.getContent(mActivity.getApplicationContext()), mActivity);
        songContentTV.setText(song.getContent(activity.getApplicationContext()));
        songContentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });
        songContentTV.setSelected(true);

        // The header
//        final RelativeLayout songHeader = (RelativeLayout) rootView.findViewById(R.id.songHeader);

        // Set top menu hidden
//        CustomScrollView scrollView = (CustomScrollView) rootView.findViewById(R.id.songContentScrollView);
//        scrollView.mOnScrollListener = new CustomScrollView.OnScrollListener() {
//            @Override
//            public void onScroll(int delta) {
//                // delta > 0: scroll down, delta < 0: scroll up
//                if (delta > 20) {
//                    songHeader.setVisibility(View.VISIBLE);
//                } else if (delta < -20) {
//                    songHeader.setVisibility(View.GONE);
//                }
//            }
//        };

        // Fullscreen button
        ImageView fullScreenButton = (ImageView) rootView.findViewById(R.id.songFullScreen);
        // Star menu button
        final ImageView starMenuButton = (ImageView) rootView.findViewById(R.id.songMenuBtn);

        if (song.isFavorite > 0) {
            starMenuButton.setImageResource(R.drawable.star_liked);
        } else {
            starMenuButton.setImageResource(R.drawable.star);
        }

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });

        // Event for star menu click
        final PopupWindow popupWindows = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindows);

        starMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, starMenuButton);
            }
        });

        return rootView;
    }

    private void openFullScreenSong() {
//        Intent intent = new Intent(mActivity, SongPlayFullScreen.class);
//        intent.putExtra("song", (Serializable)song);
//        mActivity.startActivity(intent);
    }


}
