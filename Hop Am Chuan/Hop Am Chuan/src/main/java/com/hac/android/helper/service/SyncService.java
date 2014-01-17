package com.hac.android.helper.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.hac.android.utils.LogUtils;

/**
 * Service use to sync playlist/favorite between client and server
 * Created by ThaoHQSE60963 on 1/6/14.
 */
public class SyncService extends WakefulIntentService {

    private static String TAG = LogUtils.makeLogTag(SyncService.class);

    public SyncService() {
        super(TAG);
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        /*String username = PrefStore.getLoginUsername();
        String password = PrefStore.getLoginPassword();
        boolean res;
        Context mAppContext = getApplicationContext();

        // sync playlist
        List<Playlist> oldPlaylists = PlaylistDataAccessLayer.getAllPlayLists(mAppContext);
        List<JsonPlaylist> jsonPlaylists = JsonPlaylist.convert(oldPlaylists, mAppContext);
        List<Playlist> newPlaylists = APIUtils.syncPlaylist(username, password, jsonPlaylists);

        // update playlist
        if (newPlaylists != null) {
            // delete all playlist in system
            PlaylistDataAccessLayer.removeAllPlaylists(mAppContext);

            // insert all song of its playlist to database
            for (Playlist playlist : newPlaylists) {
                // insert playlist
                PlaylistDataAccessLayer.insertPlaylist(mAppContext, playlist);
                // insert songs of playlist
                List<Integer> ids = playlist.getAllSongIds(mAppContext);
                res = PlaylistSongDataAccessLayer.insertPlaylist_Song(mAppContext, playlist.playlistId, ids);
                if (!res) return;
            }
        }

        // sync favorite
        int[] favorite = FavoriteDataAccessLayer.getAllFavoriteSongIds(mAppContext);
        List<Integer> newFavorite = APIUtils.syncFavorite(username, password, favorite);

        // update favorite
        FavoriteDataAccessLayer.syncFavorites(mAppContext, newFavorite);
        */
        LogUtils.LOGE(TAG, "Hard Working Work @@@");
    }

    public static class SyncServiceAlarm implements WakefulIntentService.AlarmListener {

        @Override
        public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
            mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+600,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
        }

        @Override
        public void sendWakefulWork(Context ctx) {
            WakefulIntentService.sendWakefulWork(ctx, SyncService.class);
        }

        @Override
        public long getMaxAge() {
            return(AlarmManager.INTERVAL_FIFTEEN_MINUTES*2);
        }
    }
}

