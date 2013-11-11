package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.config.Config;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.helper.Query;
import com.hqt.hac.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDataAccessLayer {
    private static final String TAG = makeLogTag(SongDataAccessLayer.class);


    public static boolean insertFullSongSync(Context context, Song song) {
        LOGD(TAG, "Adding a full song");
        try {
            insertSong(context, song);
            ArtistDataAcessLayer.insertListOfArtists(context, song.getAuthors(context));
            for (Artist author : song.getAuthors(context)) {
                SongArtistDataAccessLayer.insertSong_Author(context, song.songId, author.artistId);
            }
            ArtistDataAcessLayer.insertListOfArtists(context, song.getSingers(context));
            for (Artist author : song.getSingers(context)) {
                SongArtistDataAccessLayer.insertSong_Singer(context, song.songId, author.artistId);
            }
            ChordDataAccessLayer.insertListOfChords(context, song.getChords(context));
            for (Chord chord : song.getChords(context)) {
                SongChordDataAccessLayer.insertSong_Chord(context, song.songId, chord.chordId);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void insertFullSongListSync(Context context, List<Song> songs) {
        for (Song song : songs) {
            insertFullSongSync(context, song);
        }
    }

    public static String insertSong(Context context, Song song) {
        LOGD(TAG, "Adding a song");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Songs.SONG_ID, song.songId);
        cv.put(HopAmChuanDBContract.Songs.SONG_TITLE, song.title);
        cv.put(HopAmChuanDBContract.Songs.SONG_CONTENT, song.getContent(context));
        cv.put(HopAmChuanDBContract.Songs.SONG_LINK, song.link);
        cv.put(HopAmChuanDBContract.Songs.SONG_FIRST_LYRIC, song.firstLyric);
        cv.put(HopAmChuanDBContract.Songs.SONG_DATE,(new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT)).format(song.date));
        cv.put(HopAmChuanDBContract.Songs.SONG_TITLE_ASCII, StringUtils.removeAccients(song.title));
        cv.put(HopAmChuanDBContract.Songs.SONG_RHYTHM, song.rhythm);
        cv.put(HopAmChuanDBContract.Songs.SONG_LASTVIEW, song.lastView);
        cv.put(HopAmChuanDBContract.Songs.SONG_ISFAVORITE, song.isFavorite);

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
     * Notice: foreign keys constraint MUST BE RIGHT, or the this function
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
//        int contentCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_CONTENT);
        int firstlyricCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_FIRST_LYRIC);
        int linkCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_LINK);
        int dateCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_DATE);
        int titleAsciiCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_TITLE_ASCII);
        int rhythmCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_RHYTHM);
        int isFavoriteCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_ISFAVORITE);
        int lastViewCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_LASTVIEW);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                int _id = c.getInt(idCol);
                int id = c.getInt(songidCol);
                String title = c.getString(titleCol);
//                String content = c.getString(contentCol);
                String firstLyric = c.getString(firstlyricCol);
                String link = c.getString(linkCol);
                Date date = (new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT)).parse(c.getString(dateCol));
//                List<Artist> authors = getAuthorsBySongId(context, id);
//                List<Artist> singers = getSingersBySongId(context, id);
//                List<Chord> chords = getChordsBySongId(context, id);
                String titleAscii = c.getString(titleAsciiCol);
                String rhythm = c.getString(rhythmCol);
                int isFavorite = c.getInt(isFavoriteCol);
                int lastView = c.getInt(lastViewCol);


                if (c != null) {
                    c.close();
                }
                return new Song(_id, id, title, link, firstLyric, date,
                        titleAscii, lastView, isFavorite, rhythm);
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

    /**
     * Get content for lazy load
     * @param context
     * @param songId
     * @return
     */
    public static String getSongContent(Context context, int songId) {
        LOGD(TAG, "Get Song Content");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Cursor c = resolver.query(uri,
                Query.Projections.SONG_CONTENT_PROJECTION,    // projection
                HopAmChuanDBContract.Songs.SONG_ID + "=?",                           // selection string
                new String[]{String.valueOf(songId)},                           // selection args of strings
                null);                          //  sort order

        int contentCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_CONTENT);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String content = c.getString(contentCol);
            return content;
        }
        c.close();
        return "Error: could not get song content (songId=" + songId + ")";
    }

    /**
     * for testing purpose
     * Note : limit = 0 : No limit
     */
    public static Song getAllSongs(Context context, int limit) {
        throw new UnsupportedOperationException();
    }

    public static List<Song> searchSongByTitle(Context context, String title, int limit) {
        LOGD(TAG, "search Song Title");
        String keyword = StringUtils.removeAccients(title);
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Songs.CONTENT_URI;
        Cursor c = resolver.query(uri,
                Query.Projections.SONG_ID_PROJECTION,                      // projection
                HopAmChuanDBContract.Songs.SONG_TITLE_ASCII + " LIKE ?", // selection string
                new String[]{keyword + "%"},                   // selection args of strings
                "LENGTH(" + HopAmChuanDBContract.Songs.SONG_TITLE_ASCII + ") LIMIT " + limit);                                                  //  sort order

        int songIdCol = c.getColumnIndex(HopAmChuanDBContract.Songs.SONG_ID);
        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int songId = c.getInt(songIdCol);
            songs.add(getSongById(context, songId));
        }
        c.close();
        return songs;
    }


}
