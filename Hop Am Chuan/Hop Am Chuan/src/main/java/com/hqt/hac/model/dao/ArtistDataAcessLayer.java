package com.hqt.hac.model.dao;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.Utils.LogUtils.LOGD;
import static com.hqt.hac.Utils.LogUtils.makeLogTag;
import static com.hqt.hac.provider.HopAmChuanDBContract.Chords;
import static com.hqt.hac.provider.HopAmChuanDBContract.Songs;
import static com.hqt.hac.provider.HopAmChuanDBContract.Artists;

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

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, artistId + "");

        Cursor c = resolver.query(artistUri,
                                null,    // projection
                                null,    // selection string
                                null,    // selection args of strings
                                null);   //  sort order

        int idCol = c.getColumnIndex(HopAmChuanDBContract.Artists._ID);
        int artistidCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ID);
        int nameCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_NAME);
        int asciiCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ASCII);

        for (c.moveToFirst(); !c.moveToLast(); c.moveToNext()) {
            int id = c.getInt(idCol);
            int ArtistId = c.getInt(artistidCol);
            String name = c.getString(nameCol);
            String ascii = c.getString(asciiCol);
            return new Artist(id, ArtistId, name, ascii);
        }
        return null;
    }

    public static List<Song> findAllSongsByArtist(Context context, int ArtistId) {
        LOGD(TAG, "Get All Songs by Artist");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "songs/" + ArtistId + "");
        Cursor c = resolver.query(artistUri,
                null,    // projection
                null,    // selection string
                null,    // selection args of strings
                null);   //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.moveToNext(); c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(Songs.SONG_ID));
        }



        throw new UnsupportedOperationException();
    }

    /**
     * Just query all AuthorsSongs Table
     */
    public static List<Artist> getAllAuthors(Context context, int authorId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Just query all SingersSongs Table
     */
    public static List<Artist> getAllSingers(Context context, int singerId) {
        throw new UnsupportedOperationException();
    }

}
