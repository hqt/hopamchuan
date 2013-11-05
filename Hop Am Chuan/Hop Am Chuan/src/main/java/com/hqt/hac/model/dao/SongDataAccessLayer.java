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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDataAccessLayer {
    private static final String TAG = makeLogTag(SongDataAccessLayer.class);

    /**
     * TODO: insert related authors, singers, chords, synchronize
     * @param context
     * @param song
     * @return
     */
    public static String insertSong(Context context, Song song) {
        LOGD(TAG, "Adding a song");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Songs.SONG_ID, song.songId);
        cv.put(HopAmChuanDBContract.Songs.SONG_TITLE, song.title);
        cv.put(HopAmChuanDBContract.Songs.SONG_CONTENT, song.content);
        cv.put(HopAmChuanDBContract.Songs.SONG_LINK, song.link);
        cv.put(HopAmChuanDBContract.Songs.SONG_FIRST_LYRIC, song.firstLyric);
        cv.put(HopAmChuanDBContract.Songs.SONG_DATE,(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(song.date));

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

    /**
     * Notice: foreign keys contrains MUST BE RIGHT, or the this function
     * will ignore the missing foreign key records
     *
     * @param context
     * @param songId
     * @return
     */
    public static Song getSongById(Context context, int songId) {
        LOGD(TAG, "Get Song By Id");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri songUri = Uri.withAppendedPath(uri, songId + "");

        Cursor c = resolver.query(songUri,
                Query.Projections.SONG_PROJECTION,    // projection
                null,                             // selection string
                null,                             // selection args of strings
                null);                            //  sort order

        int idCol = c.getColumnIndex(HopAmChuanDBContract.Songs._ID);
        int songidCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_ID);
        int titleCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_TITLE);
        int contentCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_CONTENT);
        int firstlyricCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_FIRST_LYRIC);
        int linkCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_LINK);
        int dateCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_DATE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                int id = c.getInt(songidCol);
                String title = c.getString(titleCol);
                String content = c.getString(contentCol);
                String firstLyric = c.getString(firstlyricCol);
                String link = c.getString(linkCol);
                Date date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(c.getString(dateCol));
                List<Artist> authors = getAuthorsBySongId(context, id);
                List<Artist> singers = getSingersBySongId(context, id);
                List<Chord> chords = getChordsBySongId(context, id);

                if (c != null) {
                    c.close();
                }
                return new Song(id, title, link, content, firstLyric, date, authors, chords, singers);
                //return new Song(id, title, link, content, firstLyric, date);
            }
        } catch (Exception e) {
            LOGE(TAG, "Parse song fail!");
            e.printStackTrace();
        }
        if (c != null) {
            c.close();
        }
        return null;
    }

    /**
     * for testing purpose
     * Note : limit = 0 : No limit
     */
    public static Song getAllSongs(Context context, int limit) {
        throw new UnsupportedOperationException();
    }

    public static List<Artist> getAuthorsBySongId(Context context, int id) {
        LOGD(TAG, "Get Author by song id");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri songUri = Uri.withAppendedPath(uri, "author/" + id + "");
        Cursor c = resolver.query(songUri,
                Query.Projections.ARTIST_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        List<Artist> result = new ArrayList<Artist>();

        int artistidCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ID);
        int nameCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_NAME);
        int asciiCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ASCII);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int ArtistId = c.getInt(artistidCol);
            String name = c.getString(nameCol);
            String ascii = c.getString(asciiCol);
            result.add(new Artist(ArtistId, name, ascii));
        }
        c.close();
        return result;
    }

    public static List<Artist> getSingersBySongId(Context context, int id) {
        LOGD(TAG, "Get singers by song id");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri songUri = Uri.withAppendedPath(uri, "singer/" + id + "");
        Cursor c = resolver.query(songUri,
                Query.Projections.ARTIST_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        List<Artist> result = new ArrayList<Artist>();

        int artistidCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ID);
        int nameCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_NAME);
        int asciiCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ASCII);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int artistId = c.getInt(artistidCol);
            String name = c.getString(nameCol);
            String ascii = c.getString(asciiCol);
            result.add(new Artist(artistId, name, ascii));
        }
        c.close();
        return result;
    }
    public static List<Chord> getChordsBySongId(Context context, int id) {
        LOGD(TAG, "Get chords by song id");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Uri songUri = Uri.withAppendedPath(uri, "chord/" + id + "");
        Cursor c = resolver.query(songUri,
                Query.Projections.CHORD_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        List<Chord> result = new ArrayList<Chord>();

        int chordIdCol = c.getColumnIndex(HopAmChuanDBContract.Chords.CHORD_ID);
        int chordNameCol = c.getColumnIndex(HopAmChuanDBContract.Chords.CHORD_NAME);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int chordId = c.getInt(chordIdCol);
            String name = c.getString(chordNameCol);
            result.add(new Chord(chordId, name));
        }
        c.close();
        return result;
    }

    public static int removeSongById(Context context, int songId) {
        LOGD(TAG, "Remove an removeSongById:  song " + songId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        int deleteUri = resolver.delete(uri, HopAmChuanDBContract.Songs.SONG_ID + "=?",
                new String[]{String.valueOf(songId)});
        LOGD(TAG, "deleted removeSongById: " + deleteUri);
        return deleteUri;
    }
}
