package com.hac.android.model.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.hac.android.provider.HopAmChuanDBContract;
import com.hac.android.utils.LogUtils;

import java.util.List;

/**
 * Created by Quang Trung on 11/4/13.
 *
 *
 * Middle table : Playlist-Song
 * Playlist and Song MUST already exists in table
 */
public class PlaylistSongDataAccessLayer {
    private static String TAG = LogUtils.makeLogTag(PlaylistSongDataAccessLayer.class);

    public static boolean insertPlaylist_Song(Context context, int playlistId, List<Integer> ids) {
        boolean res = true;
        for (Integer id : ids) {
            String uri = insertPlaylist_Song(context, playlistId, id);
            if (uri == null) res = false;
        }
        return res;
    }

    public static String insertPlaylist_Song(Context context, int playlistId, int songId) {
        LogUtils.LOGD(TAG, "Adding song " + songId + " to playlist " + playlistId);

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID, playlistId);
        cv.put(HopAmChuanDBContract.PlaylistSongs.SONG_ID, songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.PlaylistSongs.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LogUtils.LOGD(TAG, "inserted uri: " + insertedUri);
        if (insertedUri == null) return null;
        else return insertedUri.toString();
    }
    public static int removePlaylist_Song(Context context, int playlistId, int songId) {
        LogUtils.LOGD(TAG, "Remove an Playlist_Song: playlist " + playlistId + " , song " + songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.PlaylistSongs.CONTENT_URI;
        int deleteUri = resolver.delete(uri, HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID + "=? AND " +
                HopAmChuanDBContract.PlaylistSongs.SONG_ID + "=?",
                new String[]{String.valueOf(playlistId), String.valueOf(songId)});
        LogUtils.LOGD(TAG, "deleted Playlist_Song: " + deleteUri);
        return deleteUri;
    }

    public static int removePlaylist_Song(Context context, int playlistId) {
        LogUtils.LOGD(TAG, "Remove all Playlist_Song: playlist " + playlistId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.PlaylistSongs.CONTENT_URI;
        int deleteUri = resolver.delete(uri, HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID + "=?",
                new String[]{String.valueOf(playlistId)});
        LogUtils.LOGD(TAG, "deleted all Playlist_Song: " + deleteUri);
        return deleteUri;
    }

}
