package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.hqt.hac.provider.HopAmChuanDBContract;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Middle table : insert Song-Artist
 * Song and Artist MUST already exists in table
 */
public class SongArtistDataAccessLayer {

    private static String TAG = makeLogTag(SongArtistDataAccessLayer.class);

    public static String insertSong_Author(Context context, int songId, int authorId) {
        LOGD(TAG, "Adding an song_author");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.SongsAuthors.ARTIST_ID, authorId);
        cv.put(HopAmChuanDBContract.SongsAuthors.SONG_ID, songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.SongsAuthors.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

    public static String insertSong_Singer(Context context, int songId, int singerId) {
        LOGD(TAG, "Adding an song_author");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.SongsSingers.ARTIST_ID, singerId);
        cv.put(HopAmChuanDBContract.SongsSingers.SONG_ID, songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.SongsSingers.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

}
