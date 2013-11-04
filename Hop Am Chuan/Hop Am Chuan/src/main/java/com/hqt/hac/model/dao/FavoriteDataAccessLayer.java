package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
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

    public static void deleteSongFromFavorite(Context context, int songId) {
        throw new UnsupportedOperationException();
    }

    public static List<Song> getAllSongs(Context context) {
        throw new UnsupportedOperationException();
    }

    public static int getNumberOfSongs(Context context) {
        throw new UnsupportedOperationException();
    }

}
