package com.hqt.hac.provider.helper;

import android.provider.BaseColumns;

import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanDatabase;

import static com.hqt.hac.provider.HopAmChuanDatabase.Tables;
import static com.hqt.hac.provider.HopAmChuanDBContract.Songs;
import static com.hqt.hac.provider.HopAmChuanDBContract.Artists;
import static com.hqt.hac.provider.HopAmChuanDBContract.Chords;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsAuthors;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsSingers;
import static com.hqt.hac.provider.HopAmChuanDBContract.SongsChords;
import static com.hqt.hac.provider.HopAmChuanDBContract.Favorites;
import static com.hqt.hac.provider.HopAmChuanDBContract.Playlist;

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
        String SONGS_SONG_LINK = HopAmChuanDatabase.Tables.SONGS + "." + Songs.SONG_LINK;
        String SONGS_SONG_CONTENT = HopAmChuanDatabase.Tables.SONGS + "." + Songs.SONG_CONTENT;
        String SONGS_SONG_FIRST_LYRIC = HopAmChuanDatabase.Tables.SONGS + "." + Songs.SONG_FIRST_LYRIC;

        String ARTIST_ARTIST_ID = Tables.ARTISTS + "." + Artists.ARTIST_ID;



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

    }
}
