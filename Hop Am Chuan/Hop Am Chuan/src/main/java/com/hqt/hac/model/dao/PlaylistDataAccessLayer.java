package com.hqt.hac.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.helper.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class PlaylistDataAccessLayer {
    private static final String TAG = makeLogTag(PlaylistDataAccessLayer.class);
    public static List<Playlist> getAllPlayLists(Context context) {
        LOGD(TAG, "Get All Playlists");
        List<Playlist> playlists = new ArrayList<Playlist>();

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;

        Uri playlistUri = Uri.withAppendedPath(uri, "all");
        Cursor c = resolver.query(playlistUri,
                Query.Projections.PLAYLIST_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int id = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist._ID));
                int playlistId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_ID));
                String playlistName = c.getString(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_NAME));
                String playlistDescription = c.getString(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_DESCRIPTION));
                // Date playlistDate = (new SimpleDateFormat()).parse(c.getString(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_DATE)));
                Date playlistDate = new Date();
                int playlistIsPublic = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_PUBLIC));
                int playlistNumOfSongs = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_NUMOFSONGS));

                Playlist playlist = new Playlist(id, playlistId, playlistName, playlistDescription, playlistDate, playlistIsPublic, playlistNumOfSongs);
                playlists.add(playlist);
            } catch (Exception e) {
                LOGE(TAG, "Fail to parse: " + c.toString());
                e.printStackTrace();
            }
        }

        return playlists;
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

    public static String addNewPlaylist(Context context, Playlist playlist) {
        LOGD(TAG, "Adding an playlist");

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_ID, playlist.playlistId);
        cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_NAME, playlist.playlistName);
        cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_DATE, playlist.date.toString());
        cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_DESCRIPTION, playlist.playlistDescription);
        cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_PUBLIC, playlist.isPublic);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
        Uri insertedUri = resolver.insert(uri, cv);
        LOGD(TAG, "inserted uri: " + insertedUri);
        return insertedUri.toString();
    }

    public static void removePlaylistById(Context context, int playlistId) {
        LOGD(TAG, "Delete playlist");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, playlistId + "");
        resolver.delete(deleteUri, null, null);
    }

    public static String addSongToPlaylist(Context context, int playlistId, int songId) {
        throw new UnsupportedOperationException();
    }

    public static void removeSongFromPlaylist(Context context, int playlistId, int songId) {
        throw new UnsupportedOperationException();
    }


}
