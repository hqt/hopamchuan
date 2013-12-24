package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanDBContract.Songs;
import com.hqt.hac.provider.helper.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class FavoriteDataAccessLayer {

    private static final String TAG = makeLogTag(FavoriteDataAccessLayer.class);

    public static int addSongToFavorite(Context context, int songId) {
        LOGD(TAG, "Adding a song to favorite");

        ContentValues cv = new ContentValues();
        cv.put(Songs.SONG_ISFAVORITE, (new Date()).getTime());

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri updateUri = Uri.withAppendedPath(uri, songId + "");
        int updatedUriResult = resolver.update(updateUri, cv, Songs.SONG_ID + "=" + songId, null);
        LOGD(TAG, "Updated uri: " + updatedUriResult);
        return updatedUriResult;
    }

    /**
     * Check if the song is in favorite list or not
     * @param context
     * @param songId
     * @return song id (return zero if the song is not in favorite list)
     */
    public static int isInFavorite(Context context, int songId) {
        LOGD(TAG, "Check song " + songId + " if is in favorite");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri favoriteUri = Uri.withAppendedPath(uri, "/isfavorite/" + songId + "");

        Cursor c = resolver.query(favoriteUri,
                Query.Projections.SONG_PROJECTION,    // projection
                null,                             // selection string
                null,                             // selection args of strings
                null);                            //  sort order

        int songidCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_ID);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int favoriteSongId = c.getInt(songidCol);
            c.close();
            return favoriteSongId;
        }
        c.close();
        return 0;
    }

    public static List<Song> getAllFavoriteSongs(Context context) {
        LOGD(TAG, "get All Favorite Songs");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri favoriteUri = Uri.withAppendedPath(uri, "/isfavorite/all");

        Cursor c = resolver.query(favoriteUri,
                Query.Projections.SONG_PROJECTION,      // projection
                null,                                   // selection string
                null,                                   // selection args of strings
                null);                                  //  sort order

        int songidCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_ID);
        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int favoriteSongId = c.getInt(songidCol);
            songs.add(SongDataAccessLayer.getSongById(context, favoriteSongId));
        }
        c.close();
        return songs;
    }

    public static int removeSongFromFavorite(Context context, int songId) {
        LOGD(TAG, "remove song " + songId + " from favorite");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Songs.SONG_ISFAVORITE, 0);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        int insertedUri = resolver.update(uri, cv, Songs.SONG_ID + "=?", new String[]{String.valueOf(songId)});
        LOGD(TAG, "Inserted uri: " + insertedUri);
        return insertedUri;
    }

    public static int[] getAllFavoriteSongIds(Context context) {
        List<Song> songs = getAllFavoriteSongs(context);
        int[] songids = new int[songs.size()];
        for (int i = 0; i < songs.size(); ++i) {
            songids[i] = songs.get(i).songId;
        }
        return songids;
    }

    public static boolean addAllSongIdsToFavorite(Context context, List<Integer> ids) {
        int fails = 0;
        for (Integer id : ids) {
            fails += addSongToFavorite(context, id);
        }
        return fails == 0;
    }

    /**
     * Synchronize favorite from server to local
     * We need to: Add first > remove then
     * to make sure that the timestamps are not affected.
     *
     * @param context
     * @param ids
     * @return
     */
    public static boolean syncFavorites(Context context, List<Integer> ids) {
        // Add new song to favorite
        for (Integer id : ids) {
            if (isInFavorite(context, id) == 0) {
                addSongToFavorite(context, id);
            }
        }

        // Remove songs
        List<Song> songs = getAllFavoriteSongs(context);
        for (Song song : songs) {
            if (ids.indexOf(song.songId) == -1) {
                removeSongFromFavorite(context, song.songId);
            }
        }

        return true;
    }

}
