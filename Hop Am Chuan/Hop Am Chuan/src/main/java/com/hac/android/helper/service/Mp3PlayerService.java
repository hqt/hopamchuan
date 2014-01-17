package com.hac.android.helper.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.hac.android.model.Song;
import com.hac.android.utils.LogUtils;

import java.io.IOException;

/**
 * Service for listening to music background
 *
 * Reference:
 * Learning about Local Bound Service:
 * http://developer.android.com/guide/components/bound-services.html
 * http://www.vogella.com/articles/AndroidServices/article.html
 * Learning about Media Player (Using State Machine on this page to prevent IllegalStateException)
 * http://developer.android.com/reference/android/media/MediaPlayer.html
 *
 * Created by ThaoHQSE60963 on 12/31/13.
 */
public class Mp3PlayerService extends Service implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {

    private static final String TAG = LogUtils.makeLogTag(Mp3PlayerService.class);

    /** Android Built-in Media Player */
    public MediaPlayer player;

    /** to know which song that service is holding */
    public Song currentSong;

    /** Notification Id for this service */
    public static final int NOTIFICATION_ID = 147141;

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
            // AssetFileDescriptor afd = getApplicationContext().getAssets().openFd("aaa.mp3");
            // player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            // this line should put after set DataSource and should use prepareAsync() rather than just only prepare()
            // player.prepareAsync();
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * When the startService command is issued on this Service, the onStartCommand method will be triggered.
     * We first check that the MediaPlayer object isn’t already playing, as this method may be called multiple times.
     * If it isn’t, we start it.
     *
     * Service.START_STICKY :
     *              Service is restarted if it gets terminated.
     *              Intent data passed to the onStartCommand method is null.
     *              Used for services which manages their own state and do not depend on the Intent data.
     * Service.START_NOT_STICKY :
     *              Service is not restarted.
     *              Used for services which are periodically triggered anyway.
     *              The service is only restarted if the runtime has pending startService() calls since the service termination.
     * Service.START_REDELIVER_INTENT :
     *              Similar to Service.START_STICKY but the original Intent is re-delivered to the onStartCommand method.
     *
     * *Notes* The onStartCommand method was introduced with Android 2.0 (API level 5)
     * Previous to that, the method used was onStart.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /** loading data from intent */
        if (intent == null) return START_STICKY;

        LogUtils.LOGE(TAG, "OnStartCommand Service New Coming");
        boolean stateIsNew = false;
        try {
            Song intentSong = intent.getParcelableExtra("song");
            if (currentSong == null) {
                currentSong = intentSong;
                stateIsNew = true;
                LogUtils.LOGE(TAG, "State is new");
            }
            if (currentSong == null) return START_STICKY;

            LogUtils.LOGE(TAG, "Question check state and new/old song");
            if (stateIsNew) LogUtils.LOGE(TAG, "State is new");
            if (intentSong.songId != currentSong.songId) LogUtils.LOGE(TAG, "Different song");
            else LogUtils.LOGE(TAG, "Same song!!!!!!");
            // if different song. start to listen new song
            if (stateIsNew || (intentSong.songId != currentSong.songId)) {
                LogUtils.LOGD(TAG, "Start new song");
                currentSong = intentSong;
                // stop currently song. if is playing
                if (player.isPlaying()) {
                    player.pause();
                }
                // prepare
                // playsongLocal();
                playsongNetwork();
                // start from new
                // player.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    int oddState = 0;
    private void playsongLocal() {
        LogUtils.LOGE(TAG, "Play song local");
        AssetFileDescriptor afd;
        player.reset();
        try {
            if ((oddState++ % 2) == 0) {
                LogUtils.LOGE(TAG, "playing aaa.mp3");
                afd = getApplicationContext().getAssets().openFd("aaa.mp3");
            } else {
                LogUtils.LOGE(TAG, "Playing bbb.mp3");
                afd = getApplicationContext().getAssets().openFd("bbb.mp3");
                //afd = getApplicationContext().getAssets().openFd("aaa.mp3");
            }
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            // this line should put after set DataSource and should use prepareAsync() rather than just only prepare()
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playsongNetwork() {
        player.reset();
        try {
            player.setDataSource(currentSong.link);
            // this line should put after set DataSource and should use prepareAsync()
            // rather than just only prepare()
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        LogUtils.LOGD(TAG, "On Destroy Service");
        if (player.isPlaying()) {
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

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Mp3PlayerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtils.LOGE(TAG, "On error: " + "code: " + what + "bundle: " + extra);
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}