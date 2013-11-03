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

public class SongDataAccessLayer {
    private static final String TAG = makeLogTag(SongDataAccessLayer.class);

    public static String insertSong(Context context, Song song) {
        LOGD(TAG, "Adding a song");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Songs.SONG_ID, song.songId);
        cv.put(HopAmChuanDBContract.Songs.SONG_TITLE, song.title);
        cv.put(HopAmChuanDBContract.Songs.SONG_CONTENT, song.content);
        cv.put(HopAmChuanDBContract.Songs.SONG_LINK, song.link);
        cv.put(HopAmChuanDBContract.Songs.SONG_FIRST_LYRIC, song.firstLyric);
        cv.put(HopAmChuanDBContract.Songs.SONG_DATE, song.date.toString());

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

    public static void insertListOfSongs(Context context, List<Song> songs) {
        for (Song song : songs) {
            insertSong(context, song);
        }
    }

    public static Song getSongById(Context context, int songId) {
        throw new UnsupportedOperationException();
    }

    /**
     * for testing purpose
     * Note : limit = 0 : No limit
     */
    public static Song getAllSongs(Context context, int limit) {
        throw new UnsupportedOperationException();
    }

    public static List<Artist> findAllAuthorsOfSong(Context context, int id) {
        throw new UnsupportedOperationException();
    }

    public static List<Artist> findAllSingersOfSong(Context context, int id) {
        throw new UnsupportedOperationException();
    }

}
