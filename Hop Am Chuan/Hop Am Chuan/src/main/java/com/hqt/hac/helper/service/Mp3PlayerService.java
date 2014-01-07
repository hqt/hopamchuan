package com.hqt.hac.helper.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.io.IOException;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

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

    private static final String TAG = makeLogTag(Mp3PlayerService.class);

    /** Android Built-in Media Player */
    public MediaPlayer player;

    /** to know which song that service is holding */
    Song currentSong;

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

        /*Notification note=new Notification(R.drawable.ic_launcher,
                "Can you hear the music?",
                System.currentTimeMillis());
        Intent i=new Intent(this, MainActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi= PendingIntent.getActivity(this, 0,
                i, 0);

        note.setLatestEventInfo(this, "Fake Player",
                "Now Playing: \"Ummmm, Nothing\"",
                pi);
        note.flags|= Notification.FLAG_NO_CLEAR;

        startForeground(1337, note);*/

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
        LOGD(TAG, "OnStartCommand Service");
        if (intent == null) return START_STICKY;
        Song intentSong = intent.getParcelableExtra("song");
        if (currentSong == null) currentSong = intentSong;
        if (currentSong == null) return START_STICKY;

        if (intentSong.songId != currentSong.songId) {
            currentSong = intentSong;
            if (!player.isPlaying()) {
                player.start();
            }
        }
        return START_STICKY;
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
        LOGD(TAG, "On Destroy Service");
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
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}