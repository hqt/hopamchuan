package com.hqt.hac.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;
import static com.hqt.hac.provider.HopAmChuanDBContract.ArtistsColumns;
import static com.hqt.hac.provider.HopAmChuanDBContract.ChordsColumns;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsColumns;

/**
 * helper for managing {@link SQLiteDatabase} that stores data for
 * {@link com.hqt.hac.provider.HopAmChuanProvider}.
 */
public class HopAmChuanDatabase extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(HopAmChuanDatabase.class);

    private final Context mContext;

    private static final String DATABASE_NAME = "hopamchuan.db";
    private static final int DATABASE_VERSION = 1;

    // NOTE: carefully update onUpgrade() when bumping database versions
    // to make sure user data is saved

    /**
     * List All Tables in this database
     */
    public static interface Tables {
        String ARTISTS = "ArtistTbl";
        String CHORDS = "ChordTbl";
        String SONGS = "SongTbl";
        String SONGS_AUTHORS = "Songs_Authors_Tbl";
        String SONGS_CHORDS = "Songs_Chords_Tbl";
        String SONGS_SINGERS = "Songs_Singers_Tbl";
        String PLAYLIST = "Playlist_Tbl";
        String PLAYLIST_SONGS = "Playlist_Songs_Tbl";
        String FAVORITES = "Favorites_Tbl";
    }

    /** {@code REFERENCES} clauses. */
    private interface References {
        String ARTIST_ID = "REFERENCES " + Tables.ARTISTS + "(" + HopAmChuanDBContract.Artists.ARTIST_ID + ")";
        String CHORD_ID = "REFERENCES " + Tables.CHORDS + "(" + HopAmChuanDBContract.Chords.CHORD_ID + ")";
        String SONG_ID = "REFERENCES " + Tables.SONGS + "(" + HopAmChuanDBContract.Songs.SONG_ID + ")";
        String PLAYLIST_ID = "REFERENCES " + Tables.PLAYLIST + "(" + HopAmChuanDBContract.Playlist.PLAYLIST_ID + ")";
    }

    public HopAmChuanDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public HopAmChuanDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Called when the database connection is being configured
     * to enable features such as write-ahead logging or foreign key support.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
    }

    /**
     * Called when the database has been opened. The implementation should check isReadOnly() before updating the database.
     This method is called after the database connection has been configured
     and after the database schema has been created, upgraded or downgraded as necessary.
     If the database connection must be configured in some way before the schema is created, upgraded, or downgraded
     do it in onConfigure(SQLiteDatabase) instead.
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

    }

    /**
     * Called when database is created for the first time
     * create all suitable tables here
     */
    @Override
    public synchronized void  onCreate(SQLiteDatabase db) {

        /**
         * base table :
         * Artist Chords Songs
         */
        db.execSQL("CREATE TABLE " + Tables.ARTISTS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ArtistsColumns.ARTIST_ID + " INTEGER,"
                + ArtistsColumns.ARTIST_NAME + " TEXT NOT NULL,"
                + ArtistsColumns.ARTIST_ASCII + " TEXT NOT NULL,"
                + "UNIQUE (" + ArtistsColumns.ARTIST_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.CHORDS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ChordsColumns.CHORD_ID + " INTEGER,"
                + ChordsColumns.CHORD_NAME + " TEXT NOT NULL,"
                + ChordsColumns.CHORD_RELATION + " TEXT,"
                + "UNIQUE (" + ChordsColumns.CHORD_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SONGS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SongsColumns.SONG_ID + " INTEGER,"
                + SongsColumns.SONG_TITLE + " TEXT NOT NULL,"
                + SongsColumns.SONG_LINK + " TEXT,"
                + SongsColumns.SONG_CONTENT + " TEXT,"
                + SongsColumns.SONG_FIRST_LYRIC + " TEXT,"
                + SongsColumns.SONG_DATE + " TEXT,"
                + "UNIQUE (" + SongsColumns.SONG_ID + ") ON CONFLICT REPLACE)");

        /**
         * Derivative tables :
         * Songs - Authors   ||    Songs - Chords  ||   Songs - Singers
         */
        db.execSQL("CREATE TABLE " + Tables.SONGS_AUTHORS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SongsColumns.SONG_ID + " INTEGER " + References.SONG_ID + ","
                + ArtistsColumns.ARTIST_ID + " INTEGER " + References.ARTIST_ID + ","
                + "UNIQUE (" + SongsColumns.SONG_ID + ","
                        + ArtistsColumns.ARTIST_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SONGS_SINGERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SongsColumns.SONG_ID + " INTEGER " + References.SONG_ID + ","
                + ArtistsColumns.ARTIST_ID + " INTEGER " + References.ARTIST_ID + ","
                + "UNIQUE (" + SongsColumns.SONG_ID + ","
                        + ArtistsColumns.ARTIST_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SONGS_CHORDS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SongsColumns.SONG_ID + " INTEGER " + References.SONG_ID + ","
                + ChordsColumns.CHORD_ID + " INTEGER " + References.CHORD_ID + ","
                + "UNIQUE (" + SongsColumns.SONG_ID + ","
                        + ChordsColumns.CHORD_ID + ") ON CONFLICT REPLACE)");

        /**
         * Playlist table and Playlist-Songs table
         */
        db.execSQL("CREATE TABLE " + Tables.PLAYLIST + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_ID + " INTEGER,"
                + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_NAME + " TEXT,"
                + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_DESCRIPTION + " TEXT,"
                + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_DATE + " TEXT,"
                + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_PUBLIC + " INTEGER,"
                + "UNIQUE (" + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.PLAYLIST_SONGS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_ID + " INTEGER " + References.PLAYLIST_ID + ","
                + SongsColumns.SONG_ID + " INTEGER " + References.SONG_ID + ","
                + "UNIQUE (" + HopAmChuanDBContract.PlaylistColumns.PLAYLIST_ID + ","
                + SongsColumns.SONG_ID + ") ON CONFLICT REPLACE)");


        db.execSQL("CREATE TABLE " + Tables.FAVORITES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SongsColumns.SONG_ID + " INTEGER " + References.SONG_ID + ")");

        // Full-text search index. Update using updateSessionSearchIndex method.
        // Use the porter tokenizer for simple stemming, so that "frustration" matches "frustrated."

    }

    /**
     * Work when upgrade database version (update database schema)
     * often : save old data
     * drop tables.
     * create tables
     * return old data has been saved
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);

        // drop all tables

        // create new tables
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
