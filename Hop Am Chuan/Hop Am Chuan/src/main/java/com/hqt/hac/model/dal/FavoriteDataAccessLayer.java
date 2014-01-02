package com.hqt.hac.model.dal;

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

    public static List<Song> getSongsFromFavorite(Context context, String orderby, int offset, int count) {
        LOGD(TAG, "get Songs From Favorite");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Cursor c = resolver.query(uri,
                Query.Projections.SONG_ID_PROJECTION,       // projection
                Songs.SONG_ISFAVORITE + " > 0",             // selection string
                null,                                       // selection args of strings
                orderby + " LIMIT " + offset + ", " + count);

        int songIdCol = 0;
        List<Song> songs = new ArrayList<Song>();
        if (c != null) {
            songIdCol = c.getColumnIndex(Songs.SONG_ID);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                int songId = c.getInt(songIdCol);
                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            c.close();

        }
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
     */
    public static boolean syncFavorites(Context context, List<Integer> ids) {
        // Add new song to favorite
        // waiting to remove favorite later
        for (Integer id : ids) {
            // this song is not from favorite
            if (isInFavorite(context, id) == 0) {
                addSongToFavorite(context, id);
            }
        }

        // Remove songs
        // view again all favorite songs. if not exist in ids (favorite sync from network)
        // meaning, this songs has been delete. remove it
        List<Song> songs = getAllFavoriteSongs(context);
        for (Song song : songs) {
            // not from ids
            if (ids.indexOf(song.songId) == -1) {
                removeSongFromFavorite(context, song.songId);
            }
        }

        // always return true
        return true;
    }

    public static void removeAllFavorites(Context context) {
        List<Song> songs = getAllFavoriteSongs(context);
        for (Song song : songs) {
            removeSongFromFavorite(context, song.songId);
        }
    }
}
