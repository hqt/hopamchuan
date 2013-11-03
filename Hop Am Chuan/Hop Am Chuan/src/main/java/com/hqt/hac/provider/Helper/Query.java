package com.hqt.hac.provider.helper;

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
     * Qualified when join table
     * use this class for prevent confusion
     */
    public interface Qualified {
        String SONGS_SONG_ID = HopAmChuanDatabase.Tables.SONGS+ "." + Songs.SONG_ID;
        String SONGS_SONG_TITLE = HopAmChuanDatabase.Tables.SONGS + "." + Songs.SONG_TITLE;
        String SONGS_SONG_DATE = HopAmChuanDatabase.Tables.SONGS + "." + Songs.SONG_DATE;
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
     * subquery : using for module
     */
    public interface Subquery {
        /**
         * get all songs from singer id
         */
        String SINGER_SONG_ID = "(SELECT " + Qualified.SONGSINGER_SONG_ID
                + " FROM " + Tables.SONGS_SINGERS + " INNER JOIN " + Tables.ARTISTS + " ON "
                + Qualified.SONGSINGER_ARTIST_ID + "=" + Qualified.ARTIST_ARTIST_ID + ")";

        /**
         * get all songs from author id
         */
        String AUTHOR_SONG_ID = "(SELECT " + Qualified.SONGAUTHOR_SONG_ID
                + " FROM " + Tables.SONGS_AUTHORS + " INNER JOIN " + Tables.ARTISTS + " ON "
                + Qualified.SONGAUTHOR_ARTIST_ID + "=" + Qualified.ARTIST_ARTIST_ID + ")";



    }
}
