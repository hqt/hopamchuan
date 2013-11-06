package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.helper.Query;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class FavoriteDataAccessLayer {

    private static final String TAG = makeLogTag(FavoriteDataAccessLayer.class);

    public static String addSongToFavorite(Context context, int songId) {
        LOGD(TAG, "Adding a song to favorite");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Favorites.SONG_ID, songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Favorites.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "Inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

    public static Song inFavorite(Context context, int songId) {
        LOGD(TAG, "Check song " + songId + " if is in favorite");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Favorites.CONTENT_URI;
        Uri favoriteUri = Uri.withAppendedPath(uri, songId + "");

        Cursor c = resolver.query(favoriteUri,
                Query.Projections.FAVORITE_PROJECTION,    // projection
                null,                             // selection string
                null,                             // selection args of strings
                null);                            //  sort order

        int songidCol = c.getColumnIndex(HopAmChuanDBContract.Favorites.SONG_ID);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int favoriteSongId = c.getInt(songidCol);
            if (c != null) {
                c.close();
            }
            return SongDataAccessLayer.getSongById(context, favoriteSongId);
        }
        if (c != null) {
            c.close();
        }
        return null;
    }

    public static List<Song> getAllFavoriteSongs(Context context) {
        LOGD(TAG, "Get All favorite songs");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Favorites.CONTENT_URI;
        Cursor c = resolver.query(uri,
                Query.Projections.FAVORITE_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int id = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Favorites._ID));
                int songId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Favorites.SONG_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;
    }

    public static void removeSongFromFavorite(Context context, int songId) {
        LOGD(TAG, "Delete song " + songId + " from favorite");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Favorites.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, songId + "");
        resolver.delete(deleteUri, null, null);
    }
}
