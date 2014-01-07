package com.hqt.hac.model.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.provider.HopAmChuanProvider;
import com.hqt.hac.provider.helper.Query;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
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
     *
     * @param context
     * @param chords
     * @return
     */
    public static List<Song> getAllSongsByChordArrays(Context context, List<Chord> chords) {

        // LOGE("TRUNGDQ", "get random songs by chords: " + offset + " : " + limit);

        /** Get chord id list **/
        StringBuilder chordIds = new StringBuilder();
        for (Chord chord : chords) {
            chordIds.append(getChordByName(context, chord.name).chordId + ", ");
        }

        String ids = chordIds.toString().substring(0, chordIds.length() - 2);
        // LOGE("TRUNGDQ", "ids: " + ids);

        HopAmChuanDatabase db = new HopAmChuanDatabase(context);
        if (db.getReadableDatabase() == null) return new ArrayList<Song>();
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT rs." + HopAmChuanDBContract.Songs.SONG_ID + ", COUNT(*) AS c FROM (SELECT s."
                        + HopAmChuanDBContract.Songs.SONG_ID + " FROM " + HopAmChuanDBContract.Tables.SONG + " s JOIN " +
                        "  " + HopAmChuanDBContract.Tables.SONG_CHORD + " sc USING (" + HopAmChuanDBContract.Songs.SONG_ID
                        + ") WHERE  sc." + HopAmChuanDBContract.Chords.CHORD_ID + " IN (" + ids + ") GROUP " +
                        "  BY 1 HAVING COUNT(*) = "+chords.size()+") AS rs JOIN " + HopAmChuanDBContract.Tables.SONG_CHORD + " ssc USING ("
                        + HopAmChuanDBContract.Songs.SONG_ID + ") " +
                        "  GROUP BY " + HopAmChuanDBContract.Songs.SONG_ID
                        + " ORDER BY c ",
                new String[]{});

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int songId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_ID));
            songs.add(SongDataAccessLayer.getSongById(context, songId));
        }
        // LOGE("TRUNGDQ", "size: " + songs.size());
        c.close();

        return songs;
    }

    public static void removeChord(Context context, int chordId) {
        LOGD(TAG, "Delete Chord");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Chords.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, chordId + "");
        resolver.delete(deleteUri, null, null);
    }

    /**
     * Get random song with chords
     *
     * @param context
     * @param chords
     * @param limit
     * @return
     */
    public static List<Song> getRandomSongsByChords(Context context, List<Chord> chords, int offset, int limit) {

        /*
        SELECT rs.song_id, COUNT(*) AS c FROM (SELECT s.song_id FROM   song s JOIN
		  song_chord sc USING (song_id) WHERE  sc.chord_id IN (".implode(",", $chords).") GROUP
		   BY 1 HAVING COUNT(*) = ".count($chords).") AS rs JOIN song_chord ssc USING (song_id)
			GROUP BY song_id ORDER BY c LIMIT 0, 90
         */

        // LOGE("TRUNGDQ", "get random songs by chords: " + offset + " : " + limit);

        /** Get chord id list **/
        StringBuilder chordIds = new StringBuilder();
        for (Chord chord : chords) {
            chordIds.append(getChordByName(context, chord.name).chordId + ", ");
        }

        String ids = chordIds.toString().substring(0, chordIds.length() - 2);
        // LOGE("TRUNGDQ", "ids: " + ids);

        HopAmChuanDatabase db = new HopAmChuanDatabase(context);
        if (db.getReadableDatabase() == null) return new ArrayList<Song>();
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT rs." + HopAmChuanDBContract.Songs.SONG_ID + ", COUNT(*) AS c FROM (SELECT s."
                        + HopAmChuanDBContract.Songs.SONG_ID + " FROM " + HopAmChuanDBContract.Tables.SONG + " s JOIN " +
                        "  " + HopAmChuanDBContract.Tables.SONG_CHORD + " sc USING (" + HopAmChuanDBContract.Songs.SONG_ID
                        + ") WHERE  sc." + HopAmChuanDBContract.Chords.CHORD_ID + " IN (" + ids + ") GROUP " +
                        "  BY 1 HAVING COUNT(*) = "+chords.size()+") AS rs JOIN " + HopAmChuanDBContract.Tables.SONG_CHORD + " ssc USING ("
                        + HopAmChuanDBContract.Songs.SONG_ID + ") " +
                        "  GROUP BY " + HopAmChuanDBContract.Songs.SONG_ID
                        + " ORDER BY c LIMIT "+offset+", " + limit,
                new String[]{});

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int songId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_ID));
            songs.add(SongDataAccessLayer.getSongById(context, songId));
        }
        // LOGE("TRUNGDQ", "size: " + songs.size());
        c.close();

        return songs;
    }
}
