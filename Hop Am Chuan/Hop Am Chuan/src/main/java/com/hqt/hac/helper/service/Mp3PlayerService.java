package com.hqt.hac.helper.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Service for listening to music background
 * Created by ThaoHQSE60963 on 12/31/13.
 */
public class Mp3PlayerService extends Service implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {

    private static final String TAG = makeLogTag(Mp3PlayerService.class);

    /** Android Built-in Media Player */
    public MediaPlayer player;

    /** to know which song that service is holding */
    int currentSongId;

    /** Binder for Mp3 Service */
    private final IBinder iBinder = new Mp3PlayerService.BackgroundAudioServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player = new MediaPlayer();
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // testing for source
            AssetFileDescriptor afd = getApplicationContext().getAssets().openFd("aaa.mp3");
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            // this line should put after set DataSource and should use prepareAsync() rather than just only prepare()
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

    /**
     * When the startService command is issued on this Service, the onStartCommand method will be triggered.
     * We first check that the MediaPlayer object isn’t already playing, as this method may be called multiple times.
     * If it isn’t, we start it.
     *
     * *Notes* The onStartCommand method was introduced with Android 2.0 (API level 5)
     * Previous to that, the method used was onStart.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOGD(TAG, "OnStartCommand Service");
        if (!player.isPlaying()) {
            player.start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public void onDestroy() {
        LOGD(TAG, "On Destroy Service");
        if (player.isPlaying())
        {
            player.stop();
        }
        player.release();
    }

    /** return itself when beeing asked */
    public class BackgroundAudioServiceBinder extends Binder {
        public Mp3PlayerService getService() {
            return Mp3PlayerService.this;
        }
    }

    ////////////////////////////////////////////////////////////////////////

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}