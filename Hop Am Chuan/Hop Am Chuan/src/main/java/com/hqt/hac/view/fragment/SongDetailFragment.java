package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hqt.hac.helper.widget.IHacFragment;
import com.hqt.hac.helper.widget.MusicPlayerController;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.FullscreenSongActivity;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.io.IOException;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDetailFragment extends Fragment implements
        MediaPlayer.OnPreparedListener,
        MusicPlayerController.IMediaPlayerControl,
        IHacFragment {

    private static String TAG = makeLogTag(PlaylistDetailFragment.class);

    /**
     * Main Activity for reference
     */
    private MainActivity activity;

    /**
     * ListView : contains all items of this fragment
     */
    private ListView mListView;

    /**
     * Adapter for this fragment
     */
    // PlaylistDetailAdapter mAdapter;

    /** Need public for access in MainActivity.onBackPressed (change title bar) **/
    public Song song;

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


        // Star menu button
        final ImageView starMenuButton = (ImageView) rootView.findViewById(R.id.songMenuBtn);

        if (song.isFavorite > 0) {
            starMenuButton.setImageResource(R.drawable.star_liked);
        } else {
            starMenuButton.setImageResource(R.drawable.star);
        }

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

        setupMediaPlayer(rootView);

        return rootView;
    }

    private void openFullScreenSong() {
        Intent intent = new Intent(activity, FullscreenSongActivity.class);
        intent.putExtra("song", song);
        activity.startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////
    /////////////////// CONFIG MP3 PLAYER //////////////////////////////

    /**
     * Reference link  :
     * http://developer.android.com/reference/android/media/MediaPlayer.html
     * Using State Machine on this page to prevent IllegalStateException
     */

    /** Android Built-in Media Player */
    MediaPlayer player;
    /** Controller for Media Player */
    MusicPlayerController controller;

    private void setupMediaPlayer(View rootView) {
        player = new MediaPlayer();
        controller = new MusicPlayerController(rootView);
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // testing for source
            AssetFileDescriptor afd = getActivity().getApplicationContext().getAssets().openFd("aaa.mp3");
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            // this line should put after set DataSource
            player.prepareAsync();
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // set player for this control
        controller.setMediaPlayer(this);
        // father view that this media controller belongs too
        // controller.setAnchorView(mediaPlayerContainer);
        // after set AnchorView. can show Media Controller
        // controller.show();
        // after get into prepare state. call start() to go to started state
        player.start();
    }

    @Override
    public void start() {
        LOGD(TAG, "Start Player");
        player.start();
    }

    @Override
    public void pause() {
        LOGD(TAG, "Pause Player");
        player.pause();
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

    @Override
    public int getTitle() {
        return 0;
    }
}
