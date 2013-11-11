package com.hqt.hac.provider.helper;

import android.provider.BaseColumns;

import com.hqt.hac.provider.HopAmChuanDBContract;

import static com.hqt.hac.provider.HopAmChuanDBContract.Artists;
import static com.hqt.hac.provider.HopAmChuanDBContract.Chords;
import static com.hqt.hac.provider.HopAmChuanDBContract.Playlist;
import static com.hqt.hac.provider.HopAmChuanDBContract.PlaylistSongs;
import static com.hqt.hac.provider.HopAmChuanDBContract.Songs;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsAuthors;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsChords;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsSingers;
import static com.hqt.hac.provider.HopAmChuanDBContract.Tables;

public class Query {

    /**
     * this constant string is used for building URI
     * constants that append to Database URI
     * for example : content://com.hqt.hac.provider/artists
     */
    public static interface URI {
        final String PATH_ARTISTS = "artists";
        final String PATH_SONGS = "songs";
        final String PATH_CHORDS = "chords";
        final String PATH_SONGS_AUTHORS = "songs_authors";
        final String PATH_SONGS_SINGERS = "songs_singers";
        final String PATH_SONGS_CHORDS = "songs_chords";
        final String PATH_PLAYLIST = "playlist";
        final String PATH_PLAYLIST_SONGS = "playlist_songs";
        final String PATH_FAVORITES = "favorites";

        final String PATH_AT = "at";
        final String PATH_AFTER = "after";
        final String PATH_BETWEEN = "between";
        final String PATH_SEARCH = "search";
        final String PATH_SEARCH_SUGGEST = "search_suggest_query";
        final String PATH_SEARCH_INDEX = "search_index";
    }


    /**
     * Use this interface to get Projection columns
     * when to use query method to query a table
     * <p/>
     * Trick : When programmer wants to debug sql query
     * can add a dummy column.
     * Locat will throws exception, and print out that query.
     * we can copy that query and query directly into console to show result
     */
    public interface Projections {
        String[] SONG_PROJECTION = {
                BaseColumns._ID,
                Songs.SONG_ID,
                Songs.SONG_TITLE,
                Songs.SONG_DATE,
                Songs.SONG_LINK,
                Songs.SONG_FIRST_LYRIC,
                Songs.SONG_LASTVIEW,
                Songs.SONG_ISFAVORITE,
                Songs.SONG_TITLE_ASCII,
                Songs.SONG_RHYTHM,
                // "Fucking Column"     :
                // Add this temporary column for debug purpose
                // logcat will print out query and we can easily trace it.
        };

        String[] ARTIST_PROJECTION = {
                BaseColumns._ID,
                Artists.ARTIST_ID,
                Artists.ARTIST_NAME,
                Artists.ARTIST_ASCII,
        };

        String[] PLAYLIST_PROJECTION = {
                BaseColumns._ID,
                Playlist.PLAYLIST_ID,
                Playlist.PLAYLIST_NAME,
                Playlist.PLAYLIST_DESCRIPTION,
                Playlist.PLAYLIST_DATE,
                Playlist.PLAYLIST_PUBLIC,
                Playlist.PLAYLIST_NUMOFSONGS,
        };
        String[] CHORD_PROJECTION = {
                BaseColumns._ID,
                Chords.CHORD_ID,
                Chords.CHORD_NAME,
        };
        String[] SONGAUTHOR_PROJECTION = {
                BaseColumns._ID,
                SongsAuthors.SONG_ID,
                SongsAuthors.ARTIST_ID,
        };
        String[] SONGSINGER_PROJECTION = {
                BaseColumns._ID,
                SongsSingers.SONG_ID,
                SongsSingers.ARTIST_ID,
        };
        String[] PLAYLISTSONG_PROJECTION = {
                BaseColumns._ID,
                PlaylistSongs.PLAYLIST_ID,
                PlaylistSongs.SONG_ID,
        };
        String[] SONG_CONTENT_PROJECTION = {
                Songs.SONG_CONTENT
        };
        String[] SONG_ID_PROJECTION = {
                Songs.SONG_ID
        };
    }


    /**
     * {@code REFERENCES} clauses.
     * Using when create database
     */
    public interface References {
        String ARTIST_ID = "REFERENCES " + Tables.ARTIST + "(" + HopAmChuanDBContract.Artists.ARTIST_ID + ")";
        String CHORD_ID = "REFERENCES " + Tables.CHORD + "(" + HopAmChuanDBContract.Chords.CHORD_ID + ")";
        String SONG_ID = "REFERENCES " + Tables.SONG + "(" + HopAmChuanDBContract.Songs.SONG_ID + ")";
        String PLAYLIST_ID = "REFERENCES " + Tables.PLAYLIST + "(" + HopAmChuanDBContract.Playlist.PLAYLIST_ID + ")";
    }

    /**
     * Qualified when join table
     * use this class for prevent confusion
     */
    public interface Qualified {
        String SONGS_SONG_ID = Tables.SONG + "." + Songs.SONG_ID;
        String SONGS_SONG_TITLE = Tables.SONG + "." + Songs.SONG_TITLE;
        String SONGS_SONG_DATE = Tables.SONG + "." + Songs.SONG_DATE;

        String ARTIST_ARTIST_ID = Tables.ARTIST + "." + Artists.ARTIST_ID;

        String PLAYLIST_SONG_PLAYLIST_ID = Tables.PLAYLIST_SONG + "." + PlaylistSongs.PLAYLIST_ID;
        String PLAYLIST_SONG_SONG_ID = Tables.PLAYLIST_SONG + "." + PlaylistSongs.SONG_ID;

        String PLAYLIST_NUMOFSONGS = Tables.PLAYLIST + "." + Playlist.PLAYLIST_NUMOFSONGS;
        String PLAYLIST_PLAYLIST_ID = Tables.PLAYLIST + "." + Playlist.PLAYLIST_ID;

        String PLAYLIST_SONG_COUNT = "Playlist_Count_Tbl";
        String PLAYLIST_SONG_COUNT_PLAYLIST_ID = PLAYLIST_SONG_COUNT + "." + Playlist.PLAYLIST_ID;

        String SONGSINGER_ARTIST_ID = Tables.SONG_SINGER + "." + SongsSingers.ARTIST_ID;
        String SONGSINGER_SONG_ID = Tables.SONG_SINGER + "." + SongsSingers.SONG_ID;

        String SONGAUTHOR_ARTIST_ID = Tables.SONG_AUTHOR + "." + SongsAuthors.ARTIST_ID;
        String SONGAUTHOR_SONG_ID = Tables.SONG_AUTHOR + "." + SongsAuthors.SONG_ID;

        Object SONGCHORD_CHORD_ID = Tables.SONG_CHORD + "." + SongsChords.CHORD_ID;
        Object SONGCHORD_SONG_ID = Tables.SONG_CHORD + "." + SongsChords.SONG_ID;
        ;
        Object CHORD_CHORD_ID = Tables.CHORD + "." + Chords.CHORD_ID;
    }

    /**
     * sub query : using for module
     */
    public interface Subquery {

        String ALL_PLAYLIST_ID_AND_COUNT = "(SELECT " + Qualified.PLAYLIST_SONG_PLAYLIST_ID + ","
                + " COUNT(IFNULL(" + Qualified.PLAYLIST_SONG_SONG_ID + ", 0)) AS " + Playlist.PLAYLIST_NUMOFSONGS
                + " FROM " + Tables.PLAYLIST_SONG
                + " GROUP BY " + Qualified.PLAYLIST_SONG_PLAYLIST_ID + ") "
                + " AS " + Qualified.PLAYLIST_SONG_COUNT;

        String PLAYLIST_JOIN_PLAYLIST_SONG_COUNT = Tables.PLAYLIST
                + " LEFT JOIN " + ALL_PLAYLIST_ID_AND_COUNT
                + " ON " + Qualified.PLAYLIST_SONG_COUNT_PLAYLIST_ID + " = " + Qualified.PLAYLIST_PLAYLIST_ID;

        /**
         * Artist LEFT JOIN Song_Author ON Artist.ArtistId = Song_Author.ArtistId
         */
        String AUTHOR_JOIN_SONG_AUTHOR = Tables.ARTIST
                + " LEFT JOIN " + Tables.SONG_AUTHOR
                + " ON " + Qualified.ARTIST_ARTIST_ID + " = " + Qualified.SONGAUTHOR_ARTIST_ID;
        /**
         * Artist LEFT JOIN Song_Singer ON Artist.ArtistId = Song_Author.ArtistId
         */
        String AUTHOR_JOIN_SONG_SINGER = Tables.ARTIST
                + " LEFT JOIN " + Tables.SONG_SINGER
                + " ON " + Qualified.ARTIST_ARTIST_ID + " = " + Qualified.SONGSINGER_ARTIST_ID;

        String CHORD_JOIN_SONG_CHORD = Tables.CHORD
                + " LEFT JOIN " + Tables.SONG_CHORD
                + " ON " + Qualified.CHORD_CHORD_ID + " = " + Qualified.SONGCHORD_CHORD_ID;
    }
}
