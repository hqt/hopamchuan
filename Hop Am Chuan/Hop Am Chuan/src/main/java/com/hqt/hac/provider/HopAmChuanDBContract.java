package com.hqt.hac.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;


import static com.hqt.hac.provider.helper.Query.URI.*;

public class HopAmChuanDBContract {

    /**
     * List All Tables in this database
     */
    public static interface Tables {
        String ARTIST = "ArtistTbl";
        String CHORD = "ChordTbl";
        String SONG = "SongTbl";
        String SONG_AUTHOR = "Song_Author_Tbl";
        String SONG_CHORD = "Song_Chord_Tbl";
        String SONG_SINGER = "Song_Singer_Tbl";
        String PLAYLIST = "Playlist_Tbl";
        String PLAYLIST_SONG = "Playlist_Song_Tbl";
        String FAVORITE = "Favorite_Tbl";
    }

    interface ArtistsColumns {
        /** unique number identifying artist
         * synchronize with website
         */
        String ARTIST_ID = "artist_id";
        /** Name of artist */
        String ARTIST_NAME = "artist_name";
        /** Name of artist without unicode
         * use this field for full text search
         */
        String ARTIST_ASCII = "artist_ascii";
    }

    interface ChordsColumns {
        /** unique number identifying chord
         * synchronized with website
         */
        String CHORD_ID = "chord_id";
        String CHORD_NAME = "chord_name";
        String CHORD_RELATION = "chord_relations";
    }

    interface SongsColumns {
        /** unique number identifying songs
         * synchronized with website
         */
        String SONG_ID = "song_id";
        String SONG_TITLE = "song_title";
        String SONG_LINK = "song_link";
        String SONG_CONTENT = "song_content";
        String SONG_FIRST_LYRIC = "song_first_lyric";
        String SONG_DATE = "song_date";
    }

    interface PlaylistColumns {
        String PLAYLIST_ID = "playlist_id";
        String PLAYLIST_NAME = "playlist_name";
        String PLAYLIST_DESCRIPTION = "playlist_description";
        String PLAYLIST_DATE = "playlist_date";
        String PLAYLIST_PUBLIC = "playlist_public";
    }


    public static final String CONTENT_AUTHORITY = "com.hqt.hac.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /**
     * Following is the inner class that describe tables in database
     * includes :
     *      Table columns name (by using implements columns keyword)
     *      CONTENT_URI (use for content provider using later)
     *      MIME-TYPE (CONTENT_TYPE and CONTENT_ITEM_TYPE)
     *      Some helper method for each class to process URI such as :
     *          a) Get Id from URI (for example : content://com.hqt.hac.provider/songs/10)
     *          b) Building URI link from Id (for example : return content://com.hqt.hac.provider/songs/10)
     *
     * @author Huynh Quang Thao
     */

    /**
     * Artist class
     */
    public static final class Artists implements ArtistsColumns,BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.artists";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.artists";

        /** Build {@link Uri} for requested {@link #ARTIST_ID}. */
        public static Uri buildArtistUri(String ArtistId) {
            return CONTENT_URI.buildUpon().appendPath(ArtistId).build();
        }

        /** Read {@link #ARTIST_ID} from {@link com.hqt.hac.provider.HopAmChuanDBContract.Artists} {@link Uri}. */
        public static String getArtistId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**
     * Songs class
     */
    public static final class Songs implements SongsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONGS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.songs";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.songs";

        /** Build {@link Uri} for requested {@link #SONG_ID}. */
        public static Uri buildSongUri(String SongId) {
            return CONTENT_URI.buildUpon().appendPath(SongId).build();
        }

        /** Read {@link #SONG_ID} from {@link com.hqt.hac.provider.HopAmChuanDBContract.Songs} {@link Uri}. */
        public static String getSongId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /**
     * Chords Class
     */
    public static final class Chords implements ChordsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHORDS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.chords";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.chords";

        /** Build {@link Uri} for requested {@link #CHORD_ID}. */
        public static Uri buildChordUri(String ChordId) {
            return CONTENT_URI.buildUpon().appendPath(ChordId).build();
        }

        /** Read {@link #CHORD_ID} from {@link com.hqt.hac.provider.HopAmChuanDBContract.Chords} {@link Uri}. */
        public static String getChordId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**
     * SongsChords class : class that list all chords of a song
     */
    public static final class SongsChords implements SongsColumns, ChordsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONGS_CHORDS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.songs_chords";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.songs_chords";

        /** Build {@link Uri} for requested {@link #SONG_ID}. */
        public static Uri buildSongsChordsUri(String SongChordId) {
            return CONTENT_URI.buildUpon().appendPath(SongChordId).build();
        }

    }

    /**
     * SongsAuthors class
     */
    public static final class SongsAuthors implements SongsColumns, ArtistsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONGS_AUTHORS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.songs_authors";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.songs_authors";

        /** Build {@link Uri} for requested {@link #SONG_ID}. */
        public static Uri buildSongsAuthorsUri(String SongAuthorId) {
            return CONTENT_URI.buildUpon().appendPath(SongAuthorId).build();
        }
    }

    /**
     * SongsSingers class
     */
    public static final class SongsSingers implements SongsColumns, ArtistsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONGS_SINGERS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.songs_singers";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.songs_singers";

        /** Build {@link Uri} for requested {@link #SONG_ID}. */
        public static Uri buildSongsSingersUri(String SongSingerId) {
            return CONTENT_URI.buildUpon().appendPath(SongSingerId).build();
        }
    }

    public static final class Playlist implements BaseColumns, PlaylistColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYLIST).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.playlist";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.playlist";
        public static final String PLAYLIST_NUMOFSONGS = "countcolumn";

        /** Build {@link Uri} for requested {@link #PLAYLIST_ID}. */
        public static Uri buildPlaylistUri(String PlaylistId) {
            return CONTENT_URI.buildUpon().appendPath(PlaylistId).build();
        }
        /** Read {@link #PLAYLIST_ID} from {@link com.hqt.hac.provider.HopAmChuanDBContract.Playlist} {@link Uri}. */
        public static String getPlaylistId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PlaylistSongs implements BaseColumns, PlaylistColumns, SongsColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYLIST_SONGS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.songs_playlist_songs";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.songs_playlist_songs";

        public static Uri buildPlaylistSongsUri(String playlistSongsId) {
            return CONTENT_URI.buildUpon().appendPath(playlistSongsId).build();
        }
    }

    public static final class Favorites implements BaseColumns, SongsColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.hqt.hac.favorites";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.hqt.hac.favorites";

        /** Build {@link Uri} for requested {@link }. */
        public static Uri buildFavoriteUri(String songId) {
            return CONTENT_URI.buildUpon().appendPath(songId).build();
        }
        /** Read {@link #SONG_ID} from {@link com.hqt.hac.provider.HopAmChuanDBContract.Chords} {@link Uri}. */
        public static String getSongId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
    }

    // prevent create objet
    private HopAmChuanDBContract(){}
}
