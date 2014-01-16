package com.hqt.hac.config;

public class Config {
    public static final int DEFAULT_SONG_ID = 0;
    public static final int DEFAULT_PLAYLIST_ID_INSERTED_BY_USER = 0;
    public static final int DEFAULT_SEARCH_LIMIT = 50;
    public static final int DEFAULT_SEARCH_ARTIST_LIMIT = 2;
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** length of toast counting in millisecond */
    public static final long TOAST_LENGTH_LONG = 3500;
    public static final long TOAST_LENGTH_SHORT = 2000;
    public static final int FRET_POSITION_PERIOD = 8;

    /** Link Config To Contact Server */
    public static final String SITE = "http://hopamchuan.com";
    public static final String PUBLIC_KEY = "75648c19d65dbe9e4fe5e0c1ae891689";
    public static final String PRIVATE_KEY = "3cbb068480cdcfe421154dec77e2439f";

    /** Link for Restful Service */
    public static final String SERVICE_LASTEST_VERSION_APP = SITE + "/api/ver";
    public static final String SERVICE_GET_SONGS_BETWEEN_DATE = SITE + "/api/song_from_to";
    public static final String SERVICE_GET_SONGS_FROM_DATE = SITE + "/api/song_from";
    public static final String SERVICE_GET_PLAYLISTS = SITE + "/api/playlists";
    public static final String SERVICE_SYNC_PLAYLIST = SITE + "/api/put_playlists";
    public static final String SERVICE_SYNC_FAVORITE = SITE + "/api/put_favorite";
    public static final String SERVICE_GET_PROFILE = SITE + "/api/get_profile";
    public static final String SERVICE_GET_MP3_LINK = SITE  + "/api/get_mp3_link";

    /** Song detail fragment **/
    public static final int DEFAULT_RELATED_SONGS_COUNT = 3;

    /** Song full view config **/
    public static final float SONG_CONTENT_DEFAULT_FONT_SIZE = 30;
    public static final int SONG_CONTENT_DEFAULT_PADDING = 20;
    public static final int SONG_AUTO_SCROLL_MIN_NEV_SPEED = 1;
    public static final int SONG_AUTO_SCROLL_MAX_NEV_SPEED = 10;
    public static final int SONG_AUTO_SCROLL_DEGREE = 10;

    /** Fragment manager **/
    public static final int FRAGMENT_TAG_MIN = 10000;
    public static final int FRAGMENT_TAG_MAX = 99999;

    /** Timeout **/
    public static final int LOADING_SMOOTHING_DELAY = 300;
    public static final int SPLASH_SCREEN_TIMEOUT = 2000;
    public static final int DEFAULT_SONG_NUM_PER_LOAD = 10;

    /** Language Locale Code */
    public static final String LANGUAGE_DEFAULT = "vi";
    public static final String LANGUAGE_VIETNAMESE = "vi";
    public static final String LANGUAGE_ENGLISH = "en";

    /** Bundle keys **/
    public static final String BUNDLE_STREAM_LINK_NAME = "STREAM_URL";

    /** App Info **/
    public static final String GOOGLE_PLAY_REF_LINK = "http://play.google.com/store/apps/details?id=";
    public static final String[] SUPPORT_EMAILS = new String[]{"huynhquangthao@gmail.com", "trungdq88@gmail.com"};
    public static final String FACEBOOK_LINK = "https://www.facebook.com/HopAmChuan";
    public static final String UPDATE_DATE_FORMAT = "dd MMM yyyy";

    /** Paths **/
    public static final String TEMPLATE_FILE_NAME = "song.tmp";


    public static final int ESTIMATE_SIZE_PER_SONG = 2500;
    public static final String BUNDLE_IS_CHORD_SEARCH = "is_chord_search";
    public static final String BUNDLE_KEYWORD = "search_key_word";
    public static final long AUTO_UPDATE_SONGS_DELAY = 10000;
    public static final String BUNDLE_AUTO_UPDATE_SONG = "updateSong";
    public static final long AUTO_SYNC_SONGS_DELAY = 15000;
    public static final int MAX_LONG_WORK_TIMEOUT = 5000;
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int SONG_AUTO_SCROLL_DEFAULT_SPEED = 3;
}
