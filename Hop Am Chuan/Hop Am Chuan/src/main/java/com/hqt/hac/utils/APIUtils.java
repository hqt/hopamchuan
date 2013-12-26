package com.hqt.hac.utils;

import com.hqt.hac.config.Config;
import com.hqt.hac.model.json.DBVersion;
import com.hqt.hac.model.json.HACAccount;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.json.JsonPlaylist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
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

        // old code using GET
        // String url = generateRequestLink(Config.SERVICE_GET_PROFILE, params);
        // String jsonString = NetworkUtils.getResponseFromGetRequest(url);

        // new code using POST
        Map post_params = generatePostRequestParams(params);

        String jsonString = "-1";
        //NetworkUtils.stimulateNetwork(10);

        jsonString = NetworkUtils.getResponseFromPOSTRequest(Config.SERVICE_GET_PROFILE, post_params);

        if (jsonString.equals("-1")) {
            // Wrong password
            return new HACAccount(null, null, null, null);
        } else {
            // Parse json. can login true or server error
            return ParserUtils.parseAccountFromJSONString(jsonString);
        }
    }

    /**
     * Get the latest version of database on server
     */
    public static DBVersion getLatestDatabaseVersion(int currentVersion) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("from_ver", currentVersion + "");

        // old code using GET
        // String url = generateRequestLink(Config.SERVICE_LASTEST_VERSION_APP, params);
        // String jsonData = NetworkUtils.getResponseFromGetRequest(url);

        // new code using POST
        Map post_params = generatePostRequestParams(params);
        String jsonData = NetworkUtils.getResponseFromPOSTRequest(Config.SERVICE_LASTEST_VERSION_APP, post_params);

        LOGE(TAG, "Version Json: " + jsonData);
        DBVersion ver = ParserUtils.getDBVersionDetail(jsonData);
        return ver;
    }

    /**
     * Base on current version on System to get the latest song
     */
    public static List<Song> getAllSongsFromVersion(int version) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("from_ver", version + "");

        // old code using GET
        // String url = generateRequestLink(Config.SERVICE_GET_SONGS_FROM_DATE, params);
        // String jsonString = NetworkUtils.getResponseFromGetRequest(url);

        // new code using POST
        Map post_params = generatePostRequestParams(params);
        String jsonString = NetworkUtils.getResponseFromPOSTRequest(Config.SERVICE_GET_SONGS_FROM_DATE, post_params);
        LOGE("TRUNGDQ", "Song list: " + jsonString);
        return ParserUtils.parseAllSongsFromJSONString(jsonString);
    }

    /**
     * synchronize all playlist to servere
     * and get again all playlist (include playlist already define on server) to user
     */
    public static List<Playlist> syncPlaylist(String username, String password, List<JsonPlaylist> playlists) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        params.put("local_playlists", EncodingUtils.encodeListToJSONString(playlists));
        LOGE(TAG, "Encoding Playlist: " + EncodingUtils.encodeListToJSONString(playlists));

        // old code using GET
        // String url = generateRequestLink(Config.SERVICE_SYNC_PLAYLIST, params);
        //String jsonString = NetworkUtils.getResponseFromGetRequest(url);

        // new code using POST
        Map post_params = generatePostRequestParams(params);
        String jsonString = NetworkUtils.getResponseFromPOSTRequest(Config.SERVICE_SYNC_PLAYLIST, post_params);

        LOGE(TAG, "Playlist JSON: " + jsonString);
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
        params.put("local_favorite", EncodingUtils.encodeObjectToJSONString(favorite));
        LOGE(TAG, "Encoding favorites: " + EncodingUtils.encodeObjectToJSONString(favorite));

        // old code using get
        // String url = generateRequestLink(Config.SERVICE_SYNC_FAVORITE, params);
        //String jsonString = NetworkUtils.getResponseFromGetRequest(url);

        // new code using post request
        Map post_params = generatePostRequestParams(params);
        String jsonString = NetworkUtils.getResponseFromPOSTRequest(Config.SERVICE_SYNC_FAVORITE, post_params);

        LOGE(TAG, "Favorite JSON: " + jsonString);
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

    private static final Map<String, String> generatePostRequestParams(Map<String, String> parameters) {
        // convert json object to string
        String jsonData = EncodingUtils.encodeMapToJSONString(parameters);
        // encode this string to unreadable (still can decode)
        String encodeJsonData = EncodingUtils.encodeDataUsingBase64(jsonData);
        // remove special characters to comparable with url
        encodeJsonData = EncodingUtils.encodeParameterUsingUrlLenCode(encodeJsonData);
        // create signature base on encode json data and private key
        String signature = EncodingUtils.encodeDataUsingHMAC_MD5(encodeJsonData, Config.PRIVATE_KEY);

        Map<String, String> params = new HashMap<String, String>();
        params.put("publicKey", Config.PUBLIC_KEY);
        params.put("privateKey", Config.PRIVATE_KEY);
        params.put("signature", signature);
        params.put("jsondata", encodeJsonData);

        return params;
    }
}