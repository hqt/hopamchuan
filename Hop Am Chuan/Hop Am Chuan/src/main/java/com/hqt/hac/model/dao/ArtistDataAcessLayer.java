package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hqt.hac.provider.helper.Query.Projections;
import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;
import static com.hqt.hac.provider.HopAmChuanDBContract.Songs;

public class ArtistDataAcessLayer {

    private static final String TAG = makeLogTag(ArtistDataAcessLayer.class);

    public static String insertArtist(Context context, Artist artist) {
        LOGD(TAG, "Adding an artist");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Artists.ARTIST_ID, artist.artistId);
        cv.put(HopAmChuanDBContract.Artists.ARTIST_NAME, artist.artistName);
        cv.put(HopAmChuanDBContract.Artists.ARTIST_ASCII, artist.artistAscii);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

    public static void insertListOfArtists(Context context, List<Artist> artists) {
        for (Artist artist : artists) {
            insertArtist(context, artist);
        }
    }

    public static void deleteArtistByid(Context context, int artistId) {
        LOGD(TAG, "Delete Artist");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, artistId + "");
        resolver.delete(deleteUri, null, null);
    }


    public static Artist getArtistById(Context context, int artistId) {
        LOGD(TAG, "Get Artist By Id");
        // TODO: NOT TEST YET
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, artistId + "");

        Cursor c = resolver.query(artistUri,
                                Projections.ARTIST_PROJECTION,    // projection
                                null,                             // selection string
                                null,                             // selection args of strings
                                null);                            //  sort order

        int idCol = c.getColumnIndex(HopAmChuanDBContract.Artists._ID);
        int artistidCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ID);
        int nameCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_NAME);
        int asciiCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ASCII);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int id = c.getInt(idCol);
            int ArtistId = c.getInt(artistidCol);
            String name = c.getString(nameCol);
            String ascii = c.getString(asciiCol);
            return new Artist(id, ArtistId, name, ascii);
        }
        return null;
    }

    public static List<Song> findAllSongsByAuthor(Context context, int ArtistId) {
        LOGD(TAG, "Get All Songs by Author");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "author/songs/" + ArtistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONG_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

       return parseSongsFromCursor(c);
    }

    public static List<Song> findAllSongsBySinger(Context context, int ArtistId) {
        LOGD(TAG, "Get All Songs by Singer");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "singer/songs/" + ArtistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONG_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order


        return parseSongsFromCursor(c);

    }

    public static List<Song> getRandomSongsByAuthor(Context context, int artistId, int limit) {
        throw new UnsupportedOperationException();
    }

    public static List<Song> getRandomSongsBySinger(Context context, int artistId, int limit) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get all songs, can be authors or singers
     * TODO : Later
     */
    public static List<Song> getRandomSongsByArtist(Context context, int artistId, int limit) {
        throw new UnsupportedOperationException();
    }

    private static List<Song> parseSongsFromCursor(Cursor c) {
        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            // TODO: Error when parsing date. see more detail later
            int id = c.getInt(c.getColumnIndex(Songs._ID));
            int songId = c.getInt(c.getColumnIndex(Songs.SONG_ID));
            String title = c.getString(c.getColumnIndex(Songs.SONG_TITLE));
            String link = c.getString(c.getColumnIndex(Songs.SONG_LINK));
            String content = c.getString(c.getColumnIndex(Songs.SONG_CONTENT));
            String lyrics = c.getString(c.getColumnIndex(Songs.SONG_FIRST_LYRIC));
            Date date = new Date();
            Song song = new Song(id, songId, title, link, content, lyrics, date);
            songs.add(song);
           /* try {
                int id = c.getInt(c.getColumnIndex(Songs._ID));
                int songId = c.getInt(c.getColumnIndex(Songs.SONG_ID));
                String title = c.getString(c.getColumnIndex(Songs.SONG_TITLE));
                String link = c.getString(c.getColumnIndex(Songs.SONG_LINK));
                String content = c.getString(c.getColumnIndex(Songs.SONG_CONTENT));
                String lyrics = c.getString(c.getColumnIndex(Songs.SONG_FIRST_LYRIC));
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                        parse(c.getString(c.getColumnIndex(Songs.SONG_DATE)));
                Song song = new Song(id, songId, title, link, content, lyrics, date);
                songs.add(song);
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }*/
        }
        return songs;
    }

    /**
     * Just query all AuthorsSongs Table
     * TODO: in future
     */
    public static List<Artist> getAllAuthors(Context context, int authorId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Just query all SingersSongs Table
     * TODO: in future
     */
    public static List<Artist> getAllSingers(Context context, int singerId) {
        throw new UnsupportedOperationException();
    }

}
