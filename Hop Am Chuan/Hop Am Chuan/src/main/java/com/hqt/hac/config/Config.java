package com.hqt.hac.config;

public class Config {
    public static final int DEFAULT_SONG_ID = -1;
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




}
