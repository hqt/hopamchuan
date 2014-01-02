package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hqt.hac.helper.service.Mp3PlayerService;
import com.hqt.hac.helper.widget.MusicPlayerController;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.FullscreenSongActivity;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDetailFragment extends Fragment implements MusicPlayerController.IMediaPlayerControl, IHacFragment {

    private static String TAG = makeLogTag(PlaylistDetailFragment.class);

    /**
     * Main Activity for reference
     */
    MainActivity activity;

    View rootView;

    /** Song object for this fragment */
    public Song song;

    /** resource for title int */
    int mResTitle = R.string.title_activity_song_list_fragment;

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
    public int getTitle() {
        return mResTitle;
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
        rootView = inflater.inflate(R.layout.fragment_song_detail, container, false);

        // Set song info
        TextView songTitleTextView = (TextView) rootView.findViewById(R.id.songTitle);
        TextView songAuthorsTextView = (TextView) rootView.findViewById(R.id.songAuthorsTV);
        TextView songSingersTextView = (TextView) rootView.findViewById(R.id.songSingersTV);
        TextView songContentTextView = (TextView) rootView.findViewById(R.id.songContent);
        Button btnFullScreen = (Button) rootView.findViewById(R.id.btnFullScreen);

        songTitleTextView.setText(song.title);
        songAuthorsTextView.setText(song.getAuthorsString(activity.getApplicationContext()));
        songSingersTextView.setText(song.getSingersString(activity.getApplicationContext()));

        // Set song content
        // HacUtils.setSongFormatted(activity.getApplicationContext(), songContentTV, song.getContent(activity.getApplicationContext()), activity);
        songContentTextView.setText(song.getContent(activity.getApplicationContext()));
        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });
        songContentTextView.setSelected(true);

        // Fullscreen button
        ImageView fullScreenButton = (ImageView) rootView.findViewById(R.id.fullscreen);
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

        setupMediaPlayer();

        return rootView;
    }

    private void openFullScreenSong() {
        Intent intent = new Intent(activity, FullscreenSongActivity.class);
        intent.putExtra("song", song);
        activity.startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////
    /////////////////// CONFIG MP3 PLAYER //////////////////////////////

    /** Controller for Media Player */
    MusicPlayerController controller;
    /** Android Built-in Media Player : reference object from service object */
    MediaPlayer player;
    /** ref to current Service */
    Mp3PlayerService mp3Service;

    /** setup start from here */
    private void setupMediaPlayer() {
        mp3Service = MainActivity.mp3Service;
        player = MainActivity.player;
        controller = new MusicPlayerController(rootView);
        controller.setMediaPlayer(SongDetailFragment.this);
    }

    @Override
    public void start() {
        LOGD(TAG, "Start Player");
        player.start();
        Bundle arguments = new Bundle();
        arguments.putParcelable("song", song);
        DialogUtils.createNotification(activity.getApplicationContext(), MainActivity.class, arguments,
                song.title, song.getAuthors(getActivity().getApplicationContext()).get(0).artistName, Mp3PlayerService.NOTIFICATION_ID);
    }

    @Override
    public void pause() {
        LOGD(TAG, "Pause Player");
        player.pause();
        DialogUtils.closeNotification(activity.getApplicationContext(), Mp3PlayerService.NOTIFICATION_ID);
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    /** Choosing Component here */

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }
}
