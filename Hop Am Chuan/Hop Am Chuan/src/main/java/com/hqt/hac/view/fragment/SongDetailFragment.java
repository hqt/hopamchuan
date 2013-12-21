package com.hqt.hac.view.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hqt.hac.helper.adapter.PlaylistDetailAdapter;
import com.hqt.hac.helper.widget.CustomScrollView;
import com.hqt.hac.helper.widget.OnSwipeTouchListener;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;
import com.hqt.hac.view.SongPlayFullScreen;

import java.io.Serializable;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDetailFragment extends  Fragment {

    private static String TAG = makeLogTag(PlaylistDetailFragment.class);

    /** Main Activity for reference */
    MainActivity activity;

    /** ListView : contains all items of this fragment */
    ListView mListView;

    /** Adapter for this fragment */
    // PlaylistDetailAdapter adapter;
    Song song;

    /** empty constructor
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
        }
        else {
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
        View rootView = inflater.inflate(R.layout.fragment_song_detail, container, false);

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

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });

        return rootView;
    }

    private void openFullScreenSong() {
        Intent intent = new Intent(activity, SongPlayFullScreen.class);
        intent.putExtra("song", (Serializable)song);
        activity.startActivity(intent);
    }
}
