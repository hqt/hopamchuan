package com.hqt.hac.provider.helper;

import android.provider.BaseColumns;

import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanDatabase;

import static com.hqt.hac.provider.HopAmChuanDBContract.Songs;
import static com.hqt.hac.provider.HopAmChuanDBContract.Artists;
import static com.hqt.hac.provider.HopAmChuanDBContract.Chords;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsAuthors;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsSingers;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsChords;
import static com.hqt.hac.provider.HopAmChuanDBContract.Favorites;
import static com.hqt.hac.provider.HopAmChuanDBContract.Playlist;
import static com.hqt.hac.provider.HopAmChuanDBContract.PlaylistSongs;
import static com.hqt.hac.provider.HopAmChuanDBContract.Tables;

public class Query {

    /**
     * Use this interface to get Projection columns
     * when to use query method to query a table
     *
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
                Songs.SONG_CONTENT,
                Songs.SONG_DATE,
                Songs.SONG_LINK,
                Songs.SONG_FIRST_LYRIC,
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
    }


    /**
     * {@code REFERENCES} clauses.
     * Using when create database
     * */
    public interface References {
        String ARTIST_ID = "REFERENCES " + Tables.ARTISTS + "(" + HopAmChuanDBContract.Artists.ARTIST_ID + ")";
        String CHORD_ID = "REFERENCES " + Tables.CHORDS + "(" + HopAmChuanDBContract.Chords.CHORD_ID + ")";
        String SONG_ID = "REFERENCES " + Tables.SONGS + "(" + HopAmChuanDBContract.Songs.SONG_ID + ")";
        String PLAYLIST_ID = "REFERENCES " + Tables.PLAYLIST + "(" + HopAmChuanDBContract.Playlist.PLAYLIST_ID + ")";
    }

    /**
     * Qualified when join table
     * use this class for prevent confusion
     */
    public interface Qualified {
        String SONGS_SONG_ID = Tables.SONGS+ "." + Songs.SONG_ID;
        String SONGS_SONG_TITLE = Tables.SONGS + "." + Songs.SONG_TITLE;
        String SONGS_SONG_DATE = Tables.SONGS + "." + Songs.SONG_DATE;

        String ARTIST_ARTIST_ID = Tables.ARTISTS + "." + Artists.ARTIST_ID;

        String PLAYLIST_SONG_PLAYLIST_ID = Tables.PLAYLIST_SONGS + "." + PlaylistSongs.PLAYLIST_ID;
        String PLAYLIST_SONG_SONG_ID = Tables.PLAYLIST_SONGS + "." + PlaylistSongs.SONG_ID;

        String PLAYLIST_NUMOFSONGS = Tables.PLAYLIST + "." + Playlist.PLAYLIST_NUMOFSONGS;
        String PLAYLIST_PLAYLIST_ID = Tables.PLAYLIST + "." + Playlist.PLAYLIST_ID;

        String PLAYLIST_SONG_COUNT = "Playlist_Count_Tbl";
        String PLAYLIST_SONG_COUNT_PLAYLIST_ID = PLAYLIST_SONG_COUNT + "." + Playlist.PLAYLIST_ID;

        String SONGSINGER_ARTIST_ID = Tables.SONGS_SINGERS + "." + SongsSingers.ARTIST_ID;
        String SONGSINGER_SONG_ID = Tables.SONGS_SINGERS + "." + SongsSingers.SONG_ID;

        String SONGAUTHOR_ARTIST_ID = Tables.SONGS_AUTHORS + "." + SongsAuthors.ARTIST_ID;
        String SONGAUTHOR_SONG_ID = Tables.SONGS_AUTHORS + "." + SongsAuthors.SONG_ID;

    }
    /**
     * sub query : using for module
     */
    public interface Subquery {
        /**
         * join three different tables
         */
        String SONG_AUTHOR_JOIN_AUTHOR_SONG = Tables.SONGS
                + " INNER JOIN " + Tables.SONGS_AUTHORS   + " ON " + Qualified.SONGAUTHOR_SONG_ID + "=" + Qualified.SONGS_SONG_ID
                + " INNER JOIN " + Tables.ARTISTS + " ON " + Qualified.SONGAUTHOR_ARTIST_ID + "=" + Qualified.ARTIST_ARTIST_ID;


        /**
         * join three different tables
         */
        String SONG_SINGER_JOIN_SINGER_SONG = Tables.SONGS
                + " INNER JOIN " + Tables.SONGS_SINGERS + " ON " + Qualified.SONGSINGER_SONG_ID + "=" + Qualified.SONGS_SONG_ID
                + " INNER JOIN " + Tables.ARTISTS + " ON " + Qualified.SONGSINGER_ARTIST_ID + "=" + Qualified.ARTIST_ARTIST_ID;

        String ALL_PLAYLIST_ID_AND_COUNT = "(SELECT " + Qualified.PLAYLIST_SONG_PLAYLIST_ID + ","
                + " COUNT(IFNULL(" + Qualified.PLAYLIST_SONG_SONG_ID + ", 0)) AS " + Playlist.PLAYLIST_NUMOFSONGS
                + " FROM " + Tables.PLAYLIST_SONGS
                + " GROUP BY " + Qualified.PLAYLIST_SONG_PLAYLIST_ID + ") "
                + " AS " + Qualified.PLAYLIST_SONG_COUNT;

        String PLAYLIST_JOIN_PLAYLIST_SONG_COUNT = Tables.PLAYLIST
                + " LEFT JOIN " + ALL_PLAYLIST_ID_AND_COUNT
                + " ON " + Qualified.PLAYLIST_SONG_COUNT_PLAYLIST_ID + " = " + Qualified.PLAYLIST_PLAYLIST_ID;
    }

    /*
    (SELECT ps.playlist_id, COUNT(ps.song_id) AS c
    FROM Playlist_Song ps
    GROUP BY ps.playlist_id) p2 /// moi cai playlist se co duoc cai count    bang phu : playlistId COunt

SELECT p1.*, p2.c FROM Playlist p1 INNER JOIN
    XXXX
ON p1.playlist_id = p2.playlist_id;
     */
}
