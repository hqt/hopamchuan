package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.FrameLayout;

import com.hqt.hac.helper.widget.alpha.VideoControllerView;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongViewFragment extends CustomFragment implements
        SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener,
        VideoControllerView.MediaPlayerControl {


public static String TAG = makeLogTag(SongViewFragment.class);

    /** Main Activity for reference */
    MainActivity activity;

    /** FrameLayout contains for SurfaceView */
    FrameLayout mediaPlayerContainer;

    /** SurfaceView for Media Player Control */
    SurfaceView videoSurface;

    /** Android Built-in Media Player */
    MediaPlayer player;

    /** Controller for Media Player */
    VideoControllerView controller;

    public SongViewFragment() {

    }



    @Override
    public int getTitle() {
        return 0;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        setRetainInstance(true);
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
        View rootView = inflater.inflate(R.layout.fragment_song_view, container, false);

/*
        */
/** Media Player *//*

        mediaPlayerContainer = (FrameLayout) rootView.findViewById(R.id.videoSurfaceContainer);
        videoSurface = (SurfaceView) rootView.findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(getActivity().getApplicationContext());
        controller = (VideoControllerView) mediaPlayerContainer;

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // testing for source
            AssetFileDescriptor afd = getActivity().getApplicationContext().getAssets().openFd("aaa.mp3");
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
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
*/
        /** List Data */
        // ListView listView = (ListView) rootView.findViewById(R.id.list_song_view);

        return rootView;
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////// SURFACE HOLDER CALLBACK ////////////////////////

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // assign holder to this player
        // so. if currently player is video player. will use this holder to drawn
        player.setDisplay(holder);
        // after move to initialize state. call prepareAsync for loading at aysnc
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    /////////////////////////////////////////////////////////////////////
    /////////////////// MEDIA PLAYER SETTING ////////////////////////////

    // those methods belong to VideoControllerView interface

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

    /////////////////////////////////////////////////////////////////////
    /////////////////// MEDIA PLAYER CONTROL ////////////////////////////

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void pause() {
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


    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
}