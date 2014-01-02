package com.hqt.hac.model.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanDBContract.SongsAuthors;
import com.hqt.hac.provider.HopAmChuanDBContract.SongsSingers;
import com.hqt.hac.provider.helper.Query;
import com.hqt.hac.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.provider.helper.Query.Projections;
import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ArtistDataAccessLayer {

    private static final String TAG = makeLogTag(ArtistDataAccessLayer.class);

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

    public static void removeArtistByid(Context context, int artistId) {
        LOGD(TAG, "Delete Artist");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, artistId + "");
        resolver.delete(deleteUri, null, null);
    }

    public static Artist getArtistByName(Context context, String name) {
        LOGD(TAG, "Get Artist By Id");
        String artistName = StringUtils.removeAcients(name);
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;

        Cursor c = resolver.query(uri,
                Projections.ARTIST_PROJECTION,                      // projection
                HopAmChuanDBContract.Artists.ARTIST_ASCII + " = ?",   // selection string
                new String[]{artistName},                             // selection args of strings
                null);                            //  sort order

        int idCol = c.getColumnIndex(HopAmChuanDBContract.Artists._ID);
        int artistidCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ID);
        int nameCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_NAME);
        int asciiCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ASCII);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int _id = c.getInt(idCol);
            int _artistId = c.getInt(artistidCol);
            String _name = c.getString(nameCol);
            String _ascii = c.getString(asciiCol);
            c.close();
            return new Artist(_id, _artistId, _name, _ascii);
        }
        c.close();
        return null;
    }

    /**
     * Search artist by name, use offset and count for pagination or infinity scrolling...
     * @param context
     * @param name
     * @param offset
     * @param count
     * @return
     */
    public static List<Artist> searchArtistByName(Context context, String name, int offset, int count) {
        LOGD(TAG, "search " + count + " Artist(s) with name '" + name + "' from position " + offset);
        String artistName = StringUtils.removeAcients(name);
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;

        Cursor c = resolver.query(uri,
                Projections.ARTIST_PROJECTION,                          // projection
                HopAmChuanDBContract.Artists.ARTIST_ASCII + " LIKE ?",  // selection string
                new String[]{"%" + artistName + "%"},                   // selection args of strings
                "LENGTH(" + HopAmChuanDBContract.Artists.ARTIST_ASCII + ") LIMIT " + offset + ", " + count); //  sort order

        int idCol = c.getColumnIndex(HopAmChuanDBContract.Artists._ID);
        int artistidCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ID);
        int nameCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_NAME);
        int asciiCol = c.getColumnIndex(HopAmChuanDBContract.Artists.ARTIST_ASCII);

        List<Artist> result = new ArrayList<Artist>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int _id = c.getInt(idCol);
            int _artistId = c.getInt(artistidCol);
            String _name = c.getString(nameCol);
            String _ascii = c.getString(asciiCol);
            result.add(new Artist(_id, _artistId, _name, _ascii));
        }
        c.close();
        return result;
    }


    public static Artist getArtistById(Context context, int artistId) {
        LOGD(TAG, "Get Artist By Id");
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
            int _id = c.getInt(idCol);
            int _artistId = c.getInt(artistidCol);
            String _name = c.getString(nameCol);
            String _ascii = c.getString(asciiCol);
            c.close();
            return new Artist(_id, _artistId, _name, _ascii);
        }
        c.close();
        return null;
    }

    public static List<Song> findAllSongsByAuthor(Context context, int ArtistId) {
        LOGD(TAG, "Get All Songs by Author");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "author/songs/" + ArtistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONGAUTHOR_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int id = c.getInt(c.getColumnIndex(SongsAuthors._ID));
                int songId = c.getInt(c.getColumnIndex(SongsAuthors.SONG_ID));
                int artistId = c.getInt(c.getColumnIndex(SongsAuthors.ARTIST_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;
    }

    public static List<Song> findAllSongsBySinger(Context context, int ArtistId) {
        LOGD(TAG, "Get All Songs by Singer");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "singer/songs/" + ArtistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONGSINGER_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order


        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int id = c.getInt(c.getColumnIndex(SongsSingers._ID));
                int songId = c.getInt(c.getColumnIndex(SongsSingers.SONG_ID));
                int artistId = c.getInt(c.getColumnIndex(SongsSingers.ARTIST_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;

    }

    public static List<Song> getRandomSongsByAuthor(Context context, int artistId, int limit) {
        LOGD(TAG, "get Random Songs By Author");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "author/songs/" + artistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONGAUTHOR_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                "RANDOM() LIMIT " + limit);         //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int songId = c.getInt(c.getColumnIndex(SongsAuthors.SONG_ID));
                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;
    }

    public static List<Song> getRandomSongsBySinger(Context context, int artistId, int limit) {
        LOGD(TAG, "Get All Songs by Singer");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        Uri artistUri = Uri.withAppendedPath(uri, "singer/songs/" + artistId + "");
        Cursor c = resolver.query(artistUri,
                Projections.SONGSINGER_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                "RANDOM() LIMIT " + limit);         //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int songId = c.getInt(c.getColumnIndex(SongsSingers.SONG_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;
    }


    public static List<Song> searchSongByAuthor(Context context, String name, int limit) {
        LOGD(TAG, "search Song By Author");

        Artist artist = ArtistDataAccessLayer.getArtistByName(context, name);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = SongsAuthors.CONTENT_URI;
        Cursor c = resolver.query(uri,
                Query.Projections.SONGAUTHOR_PROJECTION,                 // projection
                SongsAuthors.ARTIST_ID + "=?",                           // selection string
                new String[]{String.valueOf(artist.artistId)},           // selection args of strings
                "RANDOM()  LIMIT " + limit);                             //  sort order

        int songIdCol = c.getColumnIndex(SongsAuthors.SONG_ID);
        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int songId = c.getInt(songIdCol);
            songs.add(SongDataAccessLayer.getSongById(context, songId));
        }
        c.close();
        return songs;
    }

    public static List<Song> searchSongBySinger(Context context, String name, int limit) {
        LOGD(TAG, "search Song By Singer");

        Artist artist = ArtistDataAccessLayer.getArtistByName(context, name);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = SongsSingers.CONTENT_URI;
        Cursor c = resolver.query(uri,
                Query.Projections.SONGAUTHOR_PROJECTION,                 // projection
                SongsSingers.ARTIST_ID + "=?",                           // selection string
                new String[]{String.valueOf(artist.artistId)},           // selection args of strings
                "RANDOM()  LIMIT " + limit);                             //  sort order

        int songIdCol = c.getColumnIndex(SongsSingers.SONG_ID);
        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int songId = c.getInt(songIdCol);
            songs.add(SongDataAccessLayer.getSongById(context, songId));
        }
        c.close();
        return songs;
    }

    public static List<Song> searchSongByArtist(Context context, String name, int limit) {
        List<Song> songs = new ArrayList<Song>();
        songs.addAll(searchSongByAuthor(context, name, limit / 2));
        songs.addAll(searchSongBySinger(context, name, limit / 2));
        return songs;
    }

}
