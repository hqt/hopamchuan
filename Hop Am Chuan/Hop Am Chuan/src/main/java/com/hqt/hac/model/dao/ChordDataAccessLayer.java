package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.helper.Query;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ChordDataAccessLayer {

    private static final String TAG = makeLogTag(ChordDataAccessLayer.class);

    public static String insertChord(Context context, Chord chord) {
        LOGD(TAG, "Adding a Chord");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Chords.CHORD_ID, chord.chordId);
        cv.put(HopAmChuanDBContract.Chords.CHORD_NAME, chord.name);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Chords.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

    public static void insertListOfChords(Context context, List<Chord> chords) {
        for (Chord chord : chords) {
            insertChord(context, chord);
        }
    }

    public static Chord getChordByName(Context context, String chordName) {
        LOGD(TAG, "Get Chord by Name");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Chords.CONTENT_URI;
        Uri chordUri = Uri.withAppendedPath(uri, "name/" + chordName + "");

        Cursor c = resolver.query(chordUri,
                Query.Projections.CHORD_PROJECTION,    // projection
                null,                             // selection string
                null,                             // selection args of strings
                null);                            //  sort order

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int chordId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Chords.CHORD_ID));
            String _chordName = c.getString(c.getColumnIndex(HopAmChuanDBContract.Chords.CHORD_NAME));
            c.close();
            return new Chord(chordId, _chordName);
        }
        c.close();
        return null;
    }

    /**
     * TODO: re-used query from web version
     * @param context
     * @param chords
     * @return
     */
    public static List<Song> getAllSongsByChordArrays(Context context, List<Chord> chords) {
        throw new UnsupportedOperationException();
    }

    public static void removeChord(Context context, int chordId){
        LOGD(TAG, "Delete Chord");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Chords.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, chordId + "");
        resolver.delete(deleteUri, null, null);
    }
}
