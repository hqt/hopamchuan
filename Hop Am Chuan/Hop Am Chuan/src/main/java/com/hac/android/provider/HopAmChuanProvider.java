package com.hac.android.provider;

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

import com.hac.android.provider.helper.Query;
import com.hac.android.provider.helper.SelectionBuilder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.hac.android.provider.HopAmChuanDBContract.Artists;
import static com.hac.android.provider.HopAmChuanDBContract.Chords;
import static com.hac.android.provider.HopAmChuanDBContract.Playlist;
import static com.hac.android.provider.HopAmChuanDBContract.PlaylistSongs;
import static com.hac.android.provider.HopAmChuanDBContract.Songs;
import static com.hac.android.provider.HopAmChuanDBContract.SongsAuthors;
import static com.hac.android.provider.HopAmChuanDBContract.SongsChords;
import static com.hac.android.provider.HopAmChuanDBContract.SongsSingers;
import static com.hac.android.provider.HopAmChuanDBContract.Tables;
import static com.hac.android.provider.helper.Query.URI.PATH_ARTISTS;
import static com.hac.android.provider.helper.Query.URI.PATH_CHORDS;
import static com.hac.android.provider.helper.Query.URI.PATH_PLAYLIST;
import static com.hac.android.provider.helper.Query.URI.PATH_PLAYLIST_SONGS;
import static com.hac.android.provider.helper.Query.URI.PATH_SONGS;
import static com.hac.android.provider.helper.Query.URI.PATH_SONGS_AUTHORS;
import static com.hac.android.provider.helper.Query.URI.PATH_SONGS_CHORDS;
import static com.hac.android.provider.helper.Query.URI.PATH_SONGS_SINGERS;
import static com.hac.android.utils.LogUtils.LOGV;
import static com.hac.android.utils.LogUtils.makeLogTag;

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
    private static final int AUTHORS_BY_SONG_ID = 102;
    private static final int SINGERS_BY_SONG_ID = 103;
    private static final int CHORDS_BY_SONG_ID = 104;
    private static final int SONG_IS_FAVORITE = 105;
    private static final int SONG_ALL_FAVORITE = 106;

    private static final int ARTISTS = 200;
    private static final int ARTISTS_ID = 201;
    private static final int SINGER_ID_TO_SONG_IDS = 202; // get all song from one singer id
    private static final int AUTHOR_ID_TO_SONG_IDS = 203; // get all song form one author id
    private static final int SINGER_ID_TO_RAND_SONG_IDS = 204; // get random @limit song from a singer
    private static final int AUTHOR_ID_TO_RAND_SONG_IDS = 205; // get random @limit song from a author

    private static final int CHORDS = 300;
    private static final int CHORDS_ID = 301;
    private static final int CHORDS_NAME = 302;

    private static final int SONGS_AUTHORS = 400;
    private static final int SONGS_AUTHORS_ID = 401;

    private static final int SONGS_SINGERS = 500;
    private static final int SONGS_SINGERS_ID = 501;

    private static final int SONGS_CHORDS = 600;
    private static final int SONGS_CHORDS_ID = 601;

    private static final int PLAYLIST = 700;
    private static final int PLAYLIST_ID = 701;
    private static final int PLAYLIST_ALL = 702;
    private static final int PLAYLIST_GET_ID = 703;
    private static final int PLAYLIST_GET_SONGS = 704;

    private static final int PLAYLIST_SONGS = 800;
    private static final int PLAYLIST_SONGS_ID = 801;

    private static final int SEARCH_INDEX = 10;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // get authority that already define in database contract
        final String authority = HopAmChuanDBContract.CONTENT_AUTHORITY;

        /**
         * Songs table
         */
        matcher.addURI(authority, PATH_SONGS, SONGS);
        matcher.addURI(authority, PATH_SONGS + "/author/#", AUTHORS_BY_SONG_ID);
        matcher.addURI(authority, PATH_SONGS + "/singer/#", SINGERS_BY_SONG_ID);
        matcher.addURI(authority, PATH_SONGS + "/chord/#", CHORDS_BY_SONG_ID);
        matcher.addURI(authority, PATH_SONGS + "/isfavorite/#", SONG_IS_FAVORITE);
        matcher.addURI(authority, PATH_SONGS + "/isfavorite/all", SONG_ALL_FAVORITE);
        matcher.addURI(authority, PATH_SONGS + "/#", SONGS_ID);

        /**
         * Artists table
         */
        matcher.addURI(authority, PATH_ARTISTS, ARTISTS);
        matcher.addURI(authority, PATH_ARTISTS + "/#", ARTISTS_ID);
        matcher.addURI(authority, PATH_ARTISTS + "/singer/songs/#", SINGER_ID_TO_SONG_IDS);
        matcher.addURI(authority, PATH_ARTISTS + "/author/songs/#", AUTHOR_ID_TO_SONG_IDS);
        matcher.addURI(authority, PATH_ARTISTS + "/singer/randsongs/#/#", SINGER_ID_TO_RAND_SONG_IDS);
        matcher.addURI(authority, PATH_ARTISTS + "/author/randsongs/#/#", AUTHOR_ID_TO_RAND_SONG_IDS);

        /**
         * chords table
         */
        matcher.addURI(authority, PATH_CHORDS, CHORDS);
        matcher.addURI(authority, PATH_CHORDS + "/#", CHORDS_ID);
        matcher.addURI(authority, PATH_CHORDS + "/name/*", CHORDS_NAME);

        /**
         * SongsSingers table
         */
        matcher.addURI(authority, PATH_SONGS_SINGERS, SONGS_SINGERS);
        matcher.addURI(authority, PATH_SONGS_SINGERS + "/#", SONGS_SINGERS_ID);

        /**
         * SongsAuthors table
         */
        matcher.addURI(authority, PATH_SONGS_AUTHORS, SONGS_AUTHORS);
        matcher.addURI(authority, PATH_SONGS_AUTHORS + "/#", SONGS_AUTHORS_ID);

        /**
         * SongsChords table
         */
        matcher.addURI(authority, PATH_SONGS_CHORDS, SONGS_CHORDS);
        matcher.addURI(authority, PATH_SONGS_CHORDS + "/#", SONGS_CHORDS_ID);

        /**
         * Playlist Table
         */
        matcher.addURI(authority, PATH_PLAYLIST, PLAYLIST);
        matcher.addURI(authority, PATH_PLAYLIST + "/all", PLAYLIST_ALL);
        matcher.addURI(authority, PATH_PLAYLIST + "/#", PLAYLIST_ID);
        matcher.addURI(authority, PATH_PLAYLIST + "/get/#", PLAYLIST_GET_ID);
        matcher.addURI(authority, PATH_PLAYLIST + "/songs/#", PLAYLIST_GET_SONGS);

        /**
         * PlaylistSongs table
         */
        matcher.addURI(authority, PATH_PLAYLIST_SONGS, PLAYLIST_SONGS);
        matcher.addURI(authority, PATH_PLAYLIST_SONGS + "/#", PLAYLIST_SONGS_ID);

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
        switch (match) {
            case SONGS:
                return HopAmChuanDBContract.Songs.CONTENT_TYPE;
            case SONGS_ID:
                return HopAmChuanDBContract.Songs.CONTENT_ITEM_TYPE;
            case ARTISTS:
                return HopAmChuanDBContract.Artists.CONTENT_TYPE;
            case ARTISTS_ID:
                return HopAmChuanDBContract.Artists.CONTENT_ITEM_TYPE;
            case SINGER_ID_TO_SONG_IDS:
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
        switch (match) {
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
            case PLAYLIST_SONGS:
                db.insertOrThrow(Tables.PLAYLIST_SONG, null, values);
                notifyChange(uri, syncToNetwork);
                return PlaylistSongs.buildPlaylistSongsUri(values.getAsString(HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID));
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * @param uri           can known table and id
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
        final SelectionBuilder builder = buildExpandedSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri, !HopAmChuanDBContract.hasCallerIsSyncAdapterParameter(uri));
        return retVal;
    }

    /**
     * @param uri           can known table and id
     * @param values        object (column value) for update
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

        final SelectionBuilder builder = buildExpandedSelection(uri);
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

            /**
             * Get all song_id by singer Id
             * SELECT * FROM Song_Author WHERE ArtistId = @artistId
             *
             * [ ] Option 1: join table
             * [x] Option 2: no join table and use multi query
             */
            case SINGER_ID_TO_SONG_IDS: {
                final List<String> segments = uri.getPathSegments();
                // provider/authors(0)/singer(1)/songs(2)/#(3)
                final String singerId = segments.get(3);
                return builder.table(Tables.SONG_SINGER)
                        .where(SongsSingers.ARTIST_ID + "=?", singerId);
            }

            /**
             * Get all song_id by author Id
             * SELECT * FROM Song_Author WHERE ArtistId = @artistId
             *
             * [ ] Option 1: join table
             * [x] Option 2: no join table and use multi query
             */
            case AUTHOR_ID_TO_SONG_IDS: {
                final List<String> segments = uri.getPathSegments();
                // provider/authors(0)/author(1)/songs(2)/#(3)
                final String authorId = segments.get(3);
                return builder.table(Tables.SONG_AUTHOR)
                        .where(SongsAuthors.ARTIST_ID + "=?", authorId);
            }
            case SINGER_ID_TO_RAND_SONG_IDS: {
                final List<String> segments = uri.getPathSegments();
                // provider/artists(0)/singer(1)/randsongs(2)/#(3)/#(4)
                final String singerId = segments.get(3);
                final String limit = segments.get(4);
                return builder.table(Tables.SONG_AUTHOR)
                        .where(SongsAuthors.ARTIST_ID + "=?", singerId);
            }
            case AUTHOR_ID_TO_RAND_SONG_IDS: {
                final List<String> segments = uri.getPathSegments();
                // provider/artists(0)/author(1)/randsongs(2)/#(3)/#(4)
                final String authorId = segments.get(3);
                final String limit = segments.get(4);
                return builder.table(Tables.SONG_AUTHOR)
                        .where(SongsAuthors.ARTIST_ID + "=?", authorId);
            }

            case SONGS: {
                return builder.table(Tables.SONG);
            }
            case SONGS_ID: {
                final String songId = Songs.getSongId(uri);
                return builder.table(Tables.SONG)
                        .where(Songs.SONG_ID + "=?", songId);
            }
            case SONG_IS_FAVORITE: {
                final String songId = uri.getPathSegments().get(2);
                return builder.table(Tables.SONG)
                        .where(Songs.SONG_ID + "=? AND " + Songs.SONG_ISFAVORITE + ">0", songId);
            }
            case SONG_ALL_FAVORITE: {
                return builder.table(Tables.SONG)
                        .where(Songs.SONG_ISFAVORITE + "> 0");
            }

            /**
             * [x] Option 1: join table
             * [ ] Option 2: no join table and use multi query
             */
            case AUTHORS_BY_SONG_ID: {
                final List<String> segments = uri.getPathSegments();
                final String songId = segments.get(2); // provider/song/author/#
                return builder.table(Query.Subquery.AUTHOR_JOIN_SONG_AUTHOR)   // join Artist & Song_Author
                        // mapToTable for Projections not return ambiguous
                        .mapToTable(Artists._ID, Tables.ARTIST)
                        .mapToTable(Artists.ARTIST_ID, Tables.ARTIST)
                        .mapToTable(Artists.ARTIST_NAME, Tables.ARTIST)
                        .mapToTable(Artists.ARTIST_ASCII, Tables.ARTIST)
                        .where(Query.Qualified.SONGAUTHOR_SONG_ID + "=?", songId);
            }
            /**
             * [x] Option 1: join table
             * [ ] Option 2: no join table and use multi query
             */
            case SINGERS_BY_SONG_ID: {
                final List<String> segments = uri.getPathSegments();
                final String songId = segments.get(2); // provider/song/singer/#
                return builder.table(Query.Subquery.AUTHOR_JOIN_SONG_SINGER)   // join Artist & Song_Singer
                        // mapToTable for Projections not return ambiguous
                        .mapToTable(Artists._ID, Tables.ARTIST)
                        .mapToTable(Artists.ARTIST_ID, Tables.ARTIST)
                        .mapToTable(Artists.ARTIST_NAME, Tables.ARTIST)
                        .mapToTable(Artists.ARTIST_ASCII, Tables.ARTIST)
                        .where(Query.Qualified.SONGSINGER_SONG_ID + "=?", songId);
            }
            /**
             * [x] Option 1: join table
             * [ ] Option 2: no join table and use multi query
             */
            case CHORDS_BY_SONG_ID: {
                final List<String> segments = uri.getPathSegments();
                final String songId = segments.get(2); // provider/song/chord/#
                return builder.table(Query.Subquery.CHORD_JOIN_SONG_CHORD)
                        // mapToTable for Projections not return ambiguous
                        .mapToTable(Chords._ID, Tables.CHORD)
                        .mapToTable(Chords.CHORD_ID, Tables.CHORD)
                        .mapToTable(Chords.CHORD_NAME, Tables.CHORD)
                        .where(Query.Qualified.SONGCHORD_SONG_ID + "=?", songId);
            }

            case CHORDS: {
                return builder.table(Tables.CHORD);
            }
            case CHORDS_ID: {
                final String chordId = Chords.getChordId(uri);
                return builder.table(Tables.CHORD)
                        .where(Chords.CHORD_ID + "=?", chordId);
            }
            case CHORDS_NAME: {
                final String chordName = Chords.getChordName(uri);
                return builder.table(Tables.CHORD)
                        .where(Chords.CHORD_NAME + "=?", chordName);
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
             *
             * SELECT
             *      Playlist_Tbl._id, Playlist_Tbl.playlist_id, Playlist_Tbl.playlist_name,
             *      Playlist_Tbl.playlist_description, Playlist_Tbl.playlist_date,
             *      Playlist_Tbl.playlist_public, Playlist_Count_Tbl.countcolumn
             * FROM Playlist_Tbl
             * LEFT JOIN
             *      (SELECT Playlist_Songs_Tbl.playlist_id,
             *      COUNT(IFNULL(Playlist_Songs_Tbl.song_id, 0)) AS countcolumn FROM Playlist_Songs_Tbl
             *      GROUP BY Playlist_Songs_Tbl.playlist_id)
             *      AS Playlist_Count_Tbl
             * ON Playlist_Count_Tbl.playlist_id = Playlist_Tbl.playlist_id
             *
             */
            case PLAYLIST_ALL: {
                return builder.table(Query.Subquery.PLAYLIST_JOIN_PLAYLIST_SONG_COUNT)
                        .mapToTable(Playlist._ID, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_ID, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_NAME, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_DESCRIPTION, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_DATE, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_NUMOFSONGS, Query.Qualified.PLAYLIST_SONG_COUNT)
                        .mapToTable(Playlist.PLAYLIST_PUBLIC, Tables.PLAYLIST);
            }
            case PLAYLIST_ID: {
                final String playlistId = Playlist.getPlaylistId(uri);
                return builder.table(Tables.PLAYLIST)
                        .where(Playlist.PLAYLIST_ID + "=?", playlistId);
            }
            case PLAYLIST_GET_ID: {
                final List<String> segments = uri.getPathSegments();
                final String playlistId =  segments.get(2); // provider/playlist(0)/get(1)/#(2)
                return builder.table(Query.Subquery.PLAYLIST_JOIN_PLAYLIST_SONG_COUNT)
                        .mapToTable(Playlist._ID, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_ID, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_NAME, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_DESCRIPTION, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_DATE, Tables.PLAYLIST)
                        .mapToTable(Playlist.PLAYLIST_NUMOFSONGS, Query.Qualified.PLAYLIST_SONG_COUNT)
                        .mapToTable(Playlist.PLAYLIST_PUBLIC, Tables.PLAYLIST)
                        .where(Query.Qualified.PLAYLIST_PLAYLIST_ID + "=?", playlistId);
            }
            /**
             * [ ] Option 1: join table
             * [x] Option 2: no join table and use multi query
             */
            case PLAYLIST_GET_SONGS: {
                final List<String> segments = uri.getPathSegments();
                final String playlistId =  segments.get(2); // provider/playlist(0)/songs(1)/#(2)
                return builder.table(Tables.PLAYLIST_SONG)
                        .where(PlaylistSongs.PLAYLIST_ID + "=?", playlistId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

}
