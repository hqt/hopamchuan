package com.hqt.hac.model.dao;

import android.content.Context;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;

import java.util.List;

public class PlaylistDataAccessLayer {

    public static List<Playlist> getAllPlayLists(Context context) {
        throw new UnsupportedOperationException();
    }

    public static List<Song> getAllSongsInPlaylist(Context context, int playlistId) {
        throw new UnsupportedOperationException();
    }

    public static int getNumberOfSongsInPlaylist(Context context, int id) {
        throw new UnsupportedOperationException();
    }

    public static Playlist getPlaylistById(Context context, int playlistId) {
        throw new UnsupportedOperationException();
    }

    public static void removePlaylistById(Context context, int playlistId) {
        throw new UnsupportedOperationException();
    }

    public static String addSongToPlaylist(Context context, int playlistId, int songId) {
        throw new UnsupportedOperationException();
    }

    public static void removeSongFromPlaylist(Context context, int playlistId, int songId) {
        throw new UnsupportedOperationException();
    }
}
