package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hqt.hac.helper.widget.DropdownPopup;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.io.Serializable;

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
    // PlaylistDetailAdapter adapter;
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

        // get arguments from main activity
        Bundle arguments = getArguments();
        if (arguments.get("song") != null) {
            this.song = (Song) arguments.get("song");
        } else {
            LOGE(TAG, "no suitable arguments to continues");
            return;
        }

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
        // HacUtils.setSongFormatted(activity.getApplicationContext(), songContentTV, song.getContent(activity.getApplicationContext()), activity);
        songContentTV.setText(song.getContent(activity.getApplicationContext()));
        songContentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });
        songContentTV.setSelected(true);
        // The header
        final RelativeLayout songHeader = (RelativeLayout) rootView.findViewById(R.id.songHeader);

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
        // Menu button
        final ImageView menuButton = (ImageView) rootView.findViewById(R.id.songMenuBtn);

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });

        // Create popup menu
        final PopupWindow pw = DropdownPopup.createPopup(inflater, R.layout.popup_song_detail_menu);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.showAsDropDown(view);
            }
        });

        return rootView;
    }

    private void openFullScreenSong() {
//        Intent intent = new Intent(activity, SongPlayFullScreen.class);
//        intent.putExtra("song", (Serializable)song);
//        activity.startActivity(intent);
    }


}
