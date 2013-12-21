package com.hqt.hac.utils;

import com.hqt.hac.config.Config;
import com.hqt.hac.model.DBVersion;
import com.hqt.hac.model.HACAccount;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Get Data from Hop Am Chuan Site
 * this method relates to Network that should put to different thread to process
 */
public class APIUtils {

    public static String TAG = makeLogTag(APIUtils.class);

    /**
     * validate account to server. return a account object will all properties if successfully login
     */
    public static HACAccount validateAccount(String username, String password) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        String url = generateRequestLink(Config.SERVICE_GET_PROFILE, params);
        String jsonString = NetworkUtils.getResponseFromGetRequest(url);
        return ParserUtils.parseAccountFromJSONString(jsonString);
    }

    /**
     * Get the latest version of database on server
     */
    public static int getLatestDatabaseVersion(int currentVersion) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("from_ver", currentVersion + "");
        String url = generateRequestLink(Config.SERVICE_LASTEST_VERSION_APP, params);
        String jsonData = NetworkUtils.getResponseFromGetRequest(url);
        DBVersion ver = ParserUtils.getDBVersionDetail(jsonData);
        return ver.no;
    }

    /**
     * Base on current version on System to get the latest song
     */
    public static List<Song> getAllSongsFromVersion(int version) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("from_ver", version + "");
        String url = generateRequestLink(Config.SERVICE_GET_SONGS_FROM_DATE, params);
        String jsonString = NetworkUtils.getResponseFromGetRequest(url);
        return ParserUtils.parseAllSongsFromJSONString(jsonString);
    }

    /**
     * synchronize all playlist to server
     * and get again all playlist (include playlist already define on server) to user
     */
    public static List<Playlist> syncPlaylist(String username, String password, List<Playlist> playlists) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        params.put("local_playlists", EncodingUtils.encodeListToJSONString(playlists));
        String url = generateRequestLink(Config.SERVICE_SYNC_PLAYLIST, params);
        String jsonString = NetworkUtils.getResponseFromGetRequest(url);
        return ParserUtils.parseAllPlaylistFromJSONString(jsonString);
    }

    /**
     * Synchronize all favorite songs to server
     * and get again all songs in favorites (include songs already defien on server) to user
     */
    public static List<Integer> syncFavorite(String username, String password, int[] favorite) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        params.put("local_favorites", EncodingUtils.encodeObjectToJSONString(favorite));
        String url = generateRequestLink(Config.SERVICE_SYNC_FAVORITE, params);
        String jsonString = NetworkUtils.getResponseFromGetRequest(url);
        return ParserUtils.parseAllSongIdsFromJSONString(jsonString);
    }

    private static final String generateRequestLink(String url, Map<String, String> parameters) {
        StringBuilder builder = new StringBuilder(url);
        // convert json object to string
        String jsonData = EncodingUtils.encodeMapToJSONString(parameters);
        // encode this string to unreadable (still can decode)
        String encodeJsonData = EncodingUtils.encodeDataUsingBase64(jsonData);
        // remove special characters to comparable with url
        encodeJsonData = EncodingUtils.encodeParameterUsingUrlLenCode(encodeJsonData);
        // create signature base on encode json data and private key
        String signature = EncodingUtils.encodeDataUsingHMAC_MD5(encodeJsonData, Config.PRIVATE_KEY);

        // append public key
        builder.append("?publicKey=" + Config.PUBLIC_KEY);
        // append private key
        builder.append("&privateKey=" + Config.PRIVATE_KEY);
        // append signature
        builder.append("&signature=" + signature);
        // append Json Data that already encode
        builder.append("&jsondata=" + encodeJsonData);
        return builder.toString();
    }
}