package com.hqt.hac.helper.service;

import android.content.Context;
import android.content.Intent;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dal.FavoriteDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistSongDataAccessLayer;
import com.hqt.hac.model.json.JsonPlaylist;
import com.hqt.hac.utils.APIUtils;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Service use to sync playlist/favorite between client and server
 * Created by ThaoHQSE60963 on 1/6/14.
 */
public class SyncService extends WakefulIntentService {

    private static String TAG = makeLogTag(SyncService.class);

    public SyncService() {
        super(TAG);
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        String username = PrefStore.getLoginUsername();
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
    }
}
