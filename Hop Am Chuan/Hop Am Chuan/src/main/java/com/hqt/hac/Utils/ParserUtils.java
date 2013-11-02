package com.hqt.hac.Utils;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hqt.hac.Utils.LogUtils.LOGE;
import static com.hqt.hac.Utils.LogUtils.makeLogTag;

public class ParserUtils {

    private static String TAG = makeLogTag(ParserUtils.class);

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

    public static List<Artist> downloadAllArtistsFromNetwork() {
        return null;
    }

    /**
     * Get all song object from resourse, contains authors, singers and chords.
     * TODO: add a JSON string as a parameter (or stream)
     * @param context
     * @return
     */
    public static List<Song> getAllSongsFromResource(Context context) {
        Reader reader;

        // TODO: Change this, the R.raw.update is just for test purpose.
        InputStream stream = context.getResources()
                .openRawResource(R.raw.update);

        reader = new BufferedReader(new InputStreamReader(stream), 8092);

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(reader).getAsJsonArray();

        return parseSongsFromJsonArray(jsonArray);
    }


    public static List<Song> downloadAllChordsFromNetwork() {
        return null;
    }

    /**
     * Test purpose (use chord.json)
     * @param context
     * @return
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
                Date date = new SimpleDateFormat().parse(object.get("date").getAsString());
                Song song = new Song(songId, title, link, content, lyric, date);

                // TrungDQ: just a little more work to get it right.
                JsonArray authorArray = object.get("authors").getAsJsonArray();
                List<Artist> authors = parseArtistsFromJsonArray(authorArray);

                JsonArray singerArray = object.get("singers").getAsJsonArray();
                List<Artist> singers = parseArtistsFromJsonArray(singerArray);

                JsonArray chordArray = object.get("chords").getAsJsonArray();
                List<Chord> chords = parseChordsFromJsonArray(chordArray);

                song.authors = authors;
                song.singers = singers;
                song.chords = chords;

                songs.add(song);
            }
            catch (Exception e) {
                LOGE(TAG, element + " cannot parse to Song");
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
                String relations = object.get("relations").getAsString();
                Chord chord = new Chord(chordId, name, relations);
                chords.add(chord);
            }
            catch (Exception e) {
                LOGE(TAG, element + " cannot parse to chord");
            }
        }
        return chords;
    }
}
