package com.hqt.hac.utils;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hqt.hac.config.Config;
import com.hqt.hac.model.*;
import com.hqt.hac.view.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ParserUtils {

    private static String TAG = makeLogTag(ParserUtils.class);

    //region Parse Data from String
    ///////////////////////////////////////////////////////////////
    /////////////// PARSE DATA FROM STRING ////////////////////////

    public static List<Song> parseAllSongsFromJSONString(String json) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();
        return parseSongsFromJsonArray(jsonArray);
    }

    public static List<Playlist> parseAllPlaylistFromJSONString(String json) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();
        return parseAllPlaylistFromJSONArray(jsonArray);
    }

    public static List<Integer> parseAllSongIdsFromJSONString(String json) {
        throw new UnsupportedOperationException();
    }

    public static DBVersion getDBVersionDetail(String json) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject object = (JsonObject)jsonParser.parse(json);
            int no = object.get("id").getAsInt();
            Date date = new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT).parse(object.get("date").getAsString());
            int number = object.get("song_number").getAsInt();
            return new DBVersion(no, date, number);
        } catch (Exception e) {
            // parse error : maybe account is not exist
            e.printStackTrace();
            return null;
        }
    }

    public static HACAccount parseAccountFromJSONString(String json) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject object = (JsonObject)jsonParser.parse(json);
            String username = object.get("username").getAsString();
            String password = object.get("password").getAsString();
            String email = object.get("email").getAsString();
            String link = object.get("avatar_image_data").getAsString();
            LOGE(TAG, "Parse Account Detail:\n" + "Username: " + username + "\t" + password + "\t" + email);
            byte[] image = EncodingUtils.decodeDataUsingBase64(link);
            return new HACAccount(username, password, email, image);
        } catch (Exception e) {
            // parse error : maybe account is not exist
            e.printStackTrace();
            return null;
        }
    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    //endregion

    //region Get Data From Resource For Testing Purpose
    /**
     * Test purpose (use artist.json)
     * @param context
     * @return
     */
    public static List<Artist> getAllArtistsFromRescource(Context context) {
        Reader reader;
        InputStream stream = context.getResources()
                .openRawResource(R.raw.artist);
        reader = new BufferedReader(new InputStreamReader(stream), 8092);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(reader).getAsJsonArray();

        return parseArtistsFromJsonArray(jsonArray);
    }

    /**
     * Get all song object from resourse, contains authors, singers and chords.
     * TODO: add a JSON string as a parameter (or stream)
     */
    public static List<Song> getAllSongsFromResource(Context context) {
        Reader reader;

        InputStream stream = context.getResources()
                .openRawResource(R.raw.update);

        reader = new BufferedReader(new InputStreamReader(stream), 8092);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(reader).getAsJsonArray();

        return parseSongsFromJsonArray(jsonArray);
    }

    /**
     * Test purpose (use chord.json)
     */
    public static List<Chord> getAllChordsFromResource(Context context) {
        Reader reader;
        InputStream stream = context.getResources()
                .openRawResource(R.raw.chord);
        reader = new BufferedReader(new InputStreamReader(stream), 8092);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(reader).getAsJsonArray();

        return parseChordsFromJsonArray(jsonArray);
    }
    //endregion

    //region Private Method for Parser
    /////////////////////////////////////////////
    /////////////// private method //////////////
    /////////////// parsing data ////////////////

    private static List<Artist> parseArtistsFromJsonArray(JsonArray jsonArray) {
        List<Artist> songs = new ArrayList<Artist>();
        for (JsonElement element : jsonArray) {
            try{
                JsonObject object = element.getAsJsonObject();
                int artistId = object.get("artist_id").getAsInt();
                String artistName = object.get("artist_name").getAsString();
                String artistAscii = object.get("artist_ascii").getAsString();
                Artist artist = new Artist(artistId, artistName, artistAscii);
                songs.add(artist);
            }
            catch (Exception e) {
                LOGE(TAG, element.toString() + "cannot parse to Artist");
            }
        }
        return songs;
    }

    private static List<Song> parseSongsFromJsonArray(JsonArray jsonArray) {
        List<Song> songs = new ArrayList<Song>();
        for (JsonElement element : jsonArray) {
            try{
                JsonObject object = element.getAsJsonObject();
                int songId = object.get("song_id").getAsInt();
                String title = object.get("title").getAsString();
                String link = object.get("link").getAsString();
                String content = object.get("content").getAsString();
                String lyric = object.get("firstlyric").getAsString();
                Date date = new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT).parse(object.get("date").getAsString());
                Song song = new Song(Config.DEFAULT_SONG_ID, songId, title, link, content, lyric, date);

                // TrungDQ: just a little more work to get it right.
                JsonArray authorArray = object.get("authors").getAsJsonArray();
                List<Artist> authors = parseArtistsFromJsonArray(authorArray);

                JsonArray singerArray = object.get("singers").getAsJsonArray();
                List<Artist> singers = parseArtistsFromJsonArray(singerArray);

                JsonArray chordArray = object.get("chords").getAsJsonArray();
                List<Chord> chords = parseChordsFromJsonArray(chordArray);

                song.setAuthors(authors);
                song.setSingers(singers);
                song.setChords(chords);

                songs.add(song);
            }
            catch (Exception e) {
                LOGE(TAG, element + " cannot parse to Song: " + e.toString());
            }
        }
        return songs;
    }

    private static List<Chord> parseChordsFromJsonArray(JsonArray jsonArray) {
        List<Chord> chords = new ArrayList<Chord>();
        for (JsonElement element : jsonArray) {
            try{
                JsonObject object = element.getAsJsonObject();
                int chordId = object.get("chord_id").getAsInt();
                String name = object.get("name").getAsString();
                Chord chord = new Chord(chordId, name);
                chords.add(chord);
            }
            catch (Exception e) {
                LOGE(TAG, element + " cannot parse to chord");
            }
        }
        return chords;
    }

    private static List<Playlist> parseAllPlaylistFromJSONArray(JsonArray jsonArray) {
        List<Playlist> playlists = new ArrayList<Playlist>();
        for (JsonElement element : jsonArray) {
            try {
                JsonObject object = element.getAsJsonObject();
                int playlistId = object.get("playlist_id").getAsInt();
                String name = object.get("name").getAsString();
                String description = object.get("description").getAsString();
                Date date = new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT).parse(object.get("date").getAsString());
                int isPublic = object.get("public").getAsInt();
                List<Integer> songIds = parseAllSongsFromJSONArray(object.get("song_ids").getAsJsonArray());
                Playlist playlist = new Playlist(playlistId, name, description, date, isPublic);
                playlists.add(playlist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return playlists;
    }

    /** parse all song ids from array
     * use this method for parse playlist or parse favorite
     */
    private static List<Integer> parseAllSongsFromJSONArray(JsonArray jsonArray) {
        List<Integer> ids = new ArrayList<Integer>();
        for (JsonElement element : jsonArray) {
            try {
                int id = element.getAsInt();
                ids.add(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ids;
    }
    //endregion
}
