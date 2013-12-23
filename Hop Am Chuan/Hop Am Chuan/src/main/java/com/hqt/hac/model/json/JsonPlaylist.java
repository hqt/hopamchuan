package com.hqt.hac.model.json;

import android.content.Context;
import com.hqt.hac.model.Playlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ThaoHQSE60963 on 12/24/13.
 */
public class JsonPlaylist {
    int playlist_id;
    String name;
    String description;
    String date;
    int isPublic;
    List<Integer> song_ids;

    /** Convert Normal Playlist to Json Playlist */
    public static JsonPlaylist convert(Playlist playlist, Context context) {
        int playlist_id = playlist.playlistId;
        String name = playlist.playlistName;
        String description = playlist.playlistDescription;
        String date = playlist.date.toString();
        int isPublic = playlist.isPublic;
        List<Integer> song_ids = playlist.getAllSongIds(context);
        return new JsonPlaylist(playlist_id, name, description, date, isPublic, song_ids);
    }

    /** Convert normal Playlists to Json Playlists */
    public static List<JsonPlaylist> convert(List<Playlist> playlists, Context context) {
        List<JsonPlaylist> res = new ArrayList<JsonPlaylist>();
        for (Playlist p : playlists) {
            JsonPlaylist json = convert(p, context);
            res.add(json);
        }
        return res;
    }

    public JsonPlaylist(int playlist_id, String name, String description, String date, int isPublic, List<Integer> song_ids) {
        this.playlist_id = playlist_id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.isPublic = isPublic;
        this.song_ids = song_ids;
    }

    @Override
    public String toString() {
        return "JsonPlaylist{" +
                "playlist_id=" + playlist_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", isPublic=" + isPublic +
                ", song_ids=" + song_ids +
                '}';
    }
}

