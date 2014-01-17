package com.hac.android.model.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.hac.android.provider.HopAmChuanDBContract;
import com.hac.android.utils.LogUtils;

/**
 * Created by Quang Trung on 11/5/13.
 */
public class SongChordDataAccessLayer {

    private static String TAG = LogUtils.makeLogTag(SongChordDataAccessLayer.class);

    public static String insertSong_Chord(Context context, int songId, int chordId) {
        LogUtils.LOGD(TAG, "Adding an song_chord: chord " + chordId + " to song " + songId);

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.SongsChords.SONG_ID, songId);
        cv.put(HopAmChuanDBContract.SongsChords.CHORD_ID, chordId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.SongsChords.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LogUtils.LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }
    public static int removeSong_Chord(Context context, int songId, int chordId) {
        LogUtils.LOGD(TAG, "Remove an song_chord: chord " + chordId + " , song " + songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.SongsChords.CONTENT_URI;
        int deleteUri = resolver.delete(uri, HopAmChuanDBContract.SongsChords.SONG_ID + "=? AND " +
                HopAmChuanDBContract.SongsChords.CHORD_ID + "=?",
                new String[]{String.valueOf(songId), String.valueOf(chordId)});
        LogUtils.LOGD(TAG, "deleted removeSong_Chord: " + deleteUri);
        return deleteUri;
    }


}
