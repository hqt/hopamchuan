package com.hqt.hac.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.hqt.hac.provider.helper.Query;
import com.hqt.hac.provider.helper.SelectionBuilder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.provider.HopAmChuanDBContract.Artists;
import static com.hqt.hac.provider.HopAmChuanDBContract.Chords;
import static com.hqt.hac.provider.HopAmChuanDBContract.Songs;
import static com.hqt.hac.provider.HopAmChuanDBContract.Playlist;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsAuthors;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsSingers;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsChords;

import static com.hqt.hac.utils.LogUtils.LOGV;
import static com.hqt.hac.utils.LogUtils.makeLogTag;
import static com.hqt.hac.provider.HopAmChuanDBContract.Tables;

/**
 *
 */
public class HopAmChuanProvider extends ContentProvider {

    private static final String TAG = makeLogTag(HopAmChuanDatabase.class);

    /**
     * ContentProvider will heavily use SQLiteHelper for modifying database
     */
    private HopAmChuanDatabase mOpenHelper;

    /**
     * Setup URIs
     * Provide a mechanism to identify all incoming uri patterns
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int SONGS = 100;
    private static final int SONGS_ID = 101;

    private static final int ARTISTS = 200;
    private static final int ARTISTS_ID = 201;
    private static final int SINGER_TO_SONGS = 202; // get all song from one singer id
    private static final int AUTHOR_TO_SONGS = 203; // get all song form one author id

    private static final int CHORDS = 300;
    private static final int CHORDS_ID = 301;

    private static final int SONGS_AUTHORS = 400;
    private static final int SONGS_AUTHORS_ID = 401;

    private static final int SONGS_SINGERS = 500;
    private static final int SONGS_SINGERS_ID = 501;

    private static final int SONGS_CHORDS = 600;
    private static final int SONGS_CHORDS_ID = 601;

    private static final int PLAYLIST = 700;
    private static final int PLAYLIST_ID = 701;
    private static final int PLAYLIST_ALL = 702;

    private static final int PLAYLIST_SONGS = 800;
    private static final int PLAYLIST_SONGS_ID = 801;

    private static final int FAVORITES = 900;
    private static final int FAVORITES_SONG_ID = 901;

    private static final int SEARCH_INDEX =10;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // get authority that already define in database contract
        final String authority = HopAmChuanDBContract.CONTENT_AUTHORITY;

        /**
         * Songs table
         */
        matcher.addURI(authority, "songs", SONGS);
        matcher.addURI(authority, "songs/#", SONGS_ID);

        /**
         * Artists table
         */
        matcher.addURI(authority, "artists", ARTISTS);
        matcher.addURI(authority, "artists/#", ARTISTS_ID);
        matcher.addURI(authority, "artists/singer/songs/#", SINGER_TO_SONGS);
        matcher.addURI(authority, "artists/author/songs/#", AUTHOR_TO_SONGS);

        /**
         * chords table
         */
        matcher.addURI(authority, "chords", CHORDS);
        matcher.addURI(authority, "chords/#", CHORDS_ID);

        /**
         * SongsSingers table
         */
        matcher.addURI(authority, "songs_singers", SONGS_SINGERS);
        matcher.addURI(authority, "songs_singers/#", SONGS_SINGERS_ID);

        /**
         * SongsAuthors table
         */
        matcher.addURI(authority, "songs_authors", SONGS_AUTHORS);
        matcher.addURI(authority, "songs_authors/#", SONGS_AUTHORS_ID);

        /**
         * SongsChords table
         */
        matcher.addURI(authority, "songs_chords", SONGS_CHORDS);
        matcher.addURI(authority, "songs_chords/#", SONGS_CHORDS_ID);

        /**
         * Playlist Table
         */
        matcher.addURI(authority, "playlist", PLAYLIST);
        matcher.addURI(authority, "playlist/all", PLAYLIST_ALL);
        matcher.addURI(authority, "playlist/#", PLAYLIST_ID);

        /**
         * PlaylistSongs table
         */
        matcher.addURI(authority, "playlist_songs", PLAYLIST_SONGS);
        matcher.addURI(authority, "playlist_songs/#", PLAYLIST_SONGS_ID);

        /**
         * Favorite table
         */
        matcher.addURI(authority, "favorites", FAVORITES);
        matcher.addURI(authority, "favorites/#", FAVORITES_SONG_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HopAmChuanDatabase(getContext());
        return true;
    }

    private void deleteDatabase() {
        // TODO: wait for content provider operations to finish, then tear down
        mOpenHelper.close();
        Context context = getContext();
        HopAmChuanDatabase.deleteDatabase(context);
        mOpenHelper = new HopAmChuanDatabase(getContext());
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case SONGS:
                return HopAmChuanDBContract.Songs.CONTENT_TYPE;
            case SONGS_ID:
                return HopAmChuanDBContract.Songs.CONTENT_ITEM_TYPE;
            case ARTISTS:
                return HopAmChuanDBContract.Artists.CONTENT_TYPE;
            case ARTISTS_ID:
                return HopAmChuanDBContract.Artists.CONTENT_ITEM_TYPE;
            case SINGER_TO_SONGS:
                return Artists.CONTENT_ITEM_TYPE;
            case CHORDS:
                return HopAmChuanDBContract.Chords.CONTENT_TYPE;
            case CHORDS_ID:
                return HopAmChuanDBContract.Chords.CONTENT_ITEM_TYPE;
            case SONGS_AUTHORS:
                return HopAmChuanDBContract.SongsAuthors.CONTENT_TYPE;
            case SONGS_AUTHORS_ID:
                return HopAmChuanDBContract.SongsAuthors.CONTENT_ITEM_TYPE;
            case SONGS_SINGERS:
                return HopAmChuanDBContract.SongsSingers.CONTENT_TYPE;
            case SONGS_SINGERS_ID:
                return HopAmChuanDBContract.SongsSingers.CONTENT_ITEM_TYPE;
            case SONGS_CHORDS:
                return HopAmChuanDBContract.SongsChords.CONTENT_TYPE;
            case SONGS_CHORDS_ID:
                return HopAmChuanDBContract.SongsChords.CONTENT_ITEM_TYPE;
            case PLAYLIST:
                return HopAmChuanDBContract.Playlist.CONTENT_TYPE;
            case PLAYLIST_ID:
                return HopAmChuanDBContract.Playlist.CONTENT_ITEM_TYPE;
            case PLAYLIST_SONGS:
                return HopAmChuanDBContract.PlaylistSongs.CONTENT_TYPE;
            case PLAYLIST_SONGS_ID:
                return HopAmChuanDBContract.PlaylistSongs.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return HopAmChuanDBContract.Favorites.CONTENT_TYPE;
            case FAVORITES_SONG_ID:
                return HopAmChuanDBContract.Favorites.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SelectionBuilder builder = buildExpandedSelection(uri);
        return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
    }

    /**
     * insert to database using Uri
     * so that we should process Uri before doing some stuff with them
     */
    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");

        /**
         * Calling getWritableDatabase() make sure the database is always in a sensible state.
         * does not need to call db.beginTransaction() and db.endTransaction() --> mess up many things
         */
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db == null) throw new NullPointerException("db object is null");

        /**
         * can handle empty fields or validates data here
         */

        /**
         * process URI using UriMatcher for recognize Uri link
         */
        final int match = sUriMatcher.match(uri);
        boolean syncToNetwork = !HopAmChuanDBContract.hasCallerIsSyncAdapterParameter(uri);
        switch(match) {
            case ARTISTS:
                db.insertOrThrow(Tables.ARTIST, null, values);
                notifyChange(uri, syncToNetwork);
                return Artists.buildArtistUri(values.getAsString(HopAmChuanDBContract.ArtistsColumns.ARTIST_ID));
            case SONGS:
                db.insertOrThrow(Tables.SONG, null, values);
                notifyChange(uri, syncToNetwork);
                return Songs.buildSongUri(values.getAsString(HopAmChuanDBContract.SongsColumns.SONG_ID));
            case CHORDS:
                db.insertOrThrow(Tables.CHORD, null, values);
                notifyChange(uri, syncToNetwork);
                return Chords.buildChordUri(values.getAsString(HopAmChuanDBContract.ChordsColumns.CHORD_ID));
            case PLAYLIST:
                db.insertOrThrow(Tables.PLAYLIST, null, values);
                notifyChange(uri, syncToNetwork);
                return Playlist.buildPlaylistUri(values.getAsString(HopAmChuanDBContract.PlaylistColumns.PLAYLIST_ID));
            case SONGS_AUTHORS:
                db.insertOrThrow(Tables.SONG_AUTHOR, null, values);
                notifyChange(uri, syncToNetwork);
                return SongsAuthors.buildSongsAuthorsUri(values.getAsString(HopAmChuanDBContract.ArtistsColumns.ARTIST_ID));
            case SONGS_SINGERS:
                db.insertOrThrow(Tables.SONG_SINGER, null, values);
                notifyChange(uri, syncToNetwork);
                return SongsSingers.buildSongsSingersUri(values.getAsString(HopAmChuanDBContract.ArtistsColumns.ARTIST_ID));
            case SONGS_CHORDS:
                db.insertOrThrow(Tables.SONG_CHORD, null, values);
                notifyChange(uri, syncToNetwork);
                return SongsChords.buildSongsChordsUri(values.getAsString(HopAmChuanDBContract.SongsColumns.SONG_ID));

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * @param uri   can known table and id
     * @param selection     can known parameter list
     * @param selectionArgs can known parameter values
     */
    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        LOGV(TAG, "delete(uri=" + uri + ")");
        if (uri == HopAmChuanDBContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            notifyChange(uri, false);
            return 1;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri, !HopAmChuanDBContract.hasCallerIsSyncAdapterParameter(uri));
        return retVal;
    }

    /**
     * @param uri   can known table and id
     * @param values    object (column value) for update
     * @param selection     can known parameter list
     * @param selectionArgs can known parameter values
     */
    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if (match == SEARCH_INDEX) {
            // update the search index
            // TODO: implement update search index here
            return 1;
        }

        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        boolean syncToNetwork = !HopAmChuanDBContract.hasCallerIsSyncAdapterParameter(uri);
        notifyChange(uri, syncToNetwork);
        return retVal;
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    private void notifyChange(Uri uri, boolean syncToNetwork) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null, syncToNetwork);
    }


    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    public static SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ARTISTS: {
                return builder.table(Tables.ARTIST);
            }
            case ARTISTS_ID: {
                final String artistId = Artists.getArtistId(uri);
                return builder.table(Tables.ARTIST)
                        .where(Artists.ARTIST_ID + "=?", artistId);
            }
            case SONGS: {
                return builder.table(Tables.SONG);
            }
            case SONGS_ID: {
                final String songId = Songs.getSongId(uri);
                return builder.table(Tables.SONG)
                        .where(Songs.SONG_ID + "=?", songId);
            }
            case CHORDS: {
                return builder.table(Tables.CHORD);
            }
            case CHORDS_ID: {
                final String chordId = Chords.getChordId(uri);
                return builder.table(Tables.CHORD)
                        .where(Chords.CHORD_ID + "=?", chordId);
            }
            case SONGS_CHORDS: {
                return builder.table(Tables.SONG_CHORD);
            }
            case SONGS_SINGERS: {
                return builder.table(Tables.SONG_SINGER);
            }
            case SONGS_AUTHORS: {
                return builder.table(Tables.SONG_AUTHOR);
            }
            case PLAYLIST: {
                return builder.table(Tables.PLAYLIST);
            }
            case PLAYLIST_ID: {
                final String playlistId = Playlist.getPlaylistId(uri);
                return builder.table(Tables.PLAYLIST)
                        .where(Playlist.PLAYLIST_ID + "=?", playlistId);
            }
            case PLAYLIST_SONGS: {
                return builder.table(Tables.PLAYLIST_SONG);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    public static SelectionBuilder buildExpandedSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ARTISTS: {
                return builder.table(Tables.ARTIST);
            }
            case ARTISTS_ID: {
                final String artistId = Artists.getArtistId(uri);
                return builder.table(Tables.ARTIST)
                        .where(Artists.ARTIST_ID + "=?", artistId);
            }

            // get all songs from singer id
            case SINGER_TO_SONGS: {
                final List<String> segments = uri.getPathSegments();
                final String singerId = segments.get(3);
                return builder.table(Query.Subquery.SONG_SINGER_JOIN_SINGER_SONG)   // join 3 different tables
                        // mapToTable for Projections not return ambiguous
                        .mapToTable(Songs._ID, Tables.SONG)
                        .mapToTable(Songs.SONG_ID, Tables.SONG)
                        .mapToTable(Songs.SONG_TITLE, Tables.SONG)
                        .mapToTable(Songs.SONG_CONTENT, Tables.SONG)
                        .mapToTable(Songs.SONG_LINK, Tables.SONG)
                        .mapToTable(Songs.SONG_FIRST_LYRIC, Tables.SONG)
                        .mapToTable(Songs.SONG_DATE, Tables.SONG)
                        .where(Query.Qualified.SONGSINGER_ARTIST_ID + "=?", singerId);
            }

            /**
             * Get all songs from author Id
             * Using Map_to_Table to avoid conflict when client use PROJECTION_COLUMN
             * Raw query should be
             * SELECT
             *      SongTbl._id, SongTbl.song_id, SongTbl.song_title, SongTbl.song_content,
             *      SongTbl.song_date, SongTbl.song_link, SongTbl.song_first_lyric
             * FROM Songs_Authors_Tbl
             * INNER JOIN ArtistTbl ON Songs_Authors_Tbl.artist_id=ArtistTbl.artist_id
             * INNER JOIN SongTbl ON Songs_Authors_Tbl.song_id=SongTbl.song_id
             * WHERE (Songs_Authors_Tbl.artist_id=?)
             */
             case AUTHOR_TO_SONGS: {
                final List<String> segments = uri.getPathSegments();
                final String authorId = segments.get(3);
                return builder.table(Query.Subquery.SONG_AUTHOR_JOIN_AUTHOR_SONG)   // join 3 different tables
                        // mapToTable for Projections not return ambiguous
                        .mapToTable(Songs._ID, Tables.SONG)
                        .mapToTable(Songs.SONG_ID, Tables.SONG)
                        .mapToTable(Songs.SONG_TITLE, Tables.SONG)
                        .mapToTable(Songs.SONG_CONTENT, Tables.SONG)
                        .mapToTable(Songs.SONG_LINK, Tables.SONG)
                        .mapToTable(Songs.SONG_FIRST_LYRIC, Tables.SONG)
                        .mapToTable(Songs.SONG_DATE, Tables.SONG)
                        .where(Query.Qualified.SONGAUTHOR_ARTIST_ID + "=?", authorId);
            }

            case SONGS: {
                return builder.table(Tables.SONG);
            }
            case SONGS_ID: {
                final String songId = Songs.getSongId(uri);
                return builder.table(Tables.SONG)
                        .where(Songs.SONG_ID + "=?", songId);
            }
            case CHORDS: {
                return builder.table(Tables.CHORD);
            }
            case CHORDS_ID: {
                final String chordId = Chords.getChordId(uri);
                return builder.table(Tables.CHORD)
                        .where(Chords.CHORD_ID + "=?", chordId);
            }
            case SONGS_CHORDS: {
                return builder.table(Tables.SONG_CHORD);
            }
            case SONGS_SINGERS: {
                return builder.table(Tables.SONG_SINGER);
            }
            case SONGS_AUTHORS: {
                return builder.table(Tables.SONG_AUTHOR);
            }
            case PLAYLIST: {
                return builder.table(Tables.PLAYLIST);
            }
            case PLAYLIST_SONGS: {
                return builder.table(Tables.PLAYLIST_SONG);
            }

            /**
             * SELECT
             * Playlist_Tbl._id, Playlist_Tbl.playlist_id, Playlist_Tbl.playlist_name,
             * Playlist_Tbl.playlist_description, Playlist_Tbl.playlist_date,
             * Playlist_Tbl.playlist_public, Playlist_Count_Tbl.countcolumn
             * FROM Playlist_Tbl LEFT JOIN (SELECT Playlist_Songs_Tbl.playlist_id,
             * COUNT(IFNULL(Playlist_Songs_Tbl.song_id, 0)) AS countcolumn FROM Playlist_Songs_Tbl
             * GROUP BY Playlist_Songs_Tbl.playlist_id)  AS Playlist_Count_Tbl
             * ON Playlist_Count_Tbl.playlist_id = Playlist_Tbl.playlist_id
             *
             */
            case PLAYLIST_ALL: {
                return  builder.table(Query.Subquery.PLAYLIST_JOIN_PLAYLIST_SONG_COUNT)
                        .mapToTable(Playlist._ID, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_ID, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_NAME, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_DESCRIPTION, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_DATE, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_NUMOFSONGS, Query.Qualified.PLAYLIST_SONG_COUNT)
                        .mapToTable(Playlist.PLAYLIST_PUBLIC, Tables.PLAYLIST);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

}
