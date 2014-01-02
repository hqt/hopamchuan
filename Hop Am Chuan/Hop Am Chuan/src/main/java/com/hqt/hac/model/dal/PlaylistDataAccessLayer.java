package com.hqt.hac.model.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.hqt.hac.config.Config;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.helper.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                Date playlistDate = (new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT)).parse(c.getString(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_DATE)));
                int playlistIsPublic = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_PUBLIC));
                int playlistNumOfSongs = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_NUMOFSONGS));

                Playlist playlist = new Playlist(id, playlistId, playlistName, playlistDescription, playlistDate, playlistIsPublic, playlistNumOfSongs);
                playlists.add(playlist);
            } catch (Exception e) {
                LOGE(TAG, "Fail to parse: " + c.toString());
                e.printStackTrace();
            }
        }
        c.close();
        return playlists;
    }

    public static List<Song> getAllSongsFromPlaylist(Context context, int playlistId) {
        LOGD(TAG, "Get All Songs from playlist " + playlistId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
        Uri playlistUri = Uri.withAppendedPath(uri, "songs/" + playlistId + "");
        Cursor c = resolver.query(playlistUri,
                Query.Projections.PLAYLISTSONG_PROJECTION,    // projection
                null,                           // selection string
                null,                           // selection args of strings
                null);                          //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                // int id = c.getInt(c.getColumnIndex(HopAmChuanDBContract.PlaylistSongs._ID));
                int songId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.PlaylistSongs.SONG_ID));
                // int _playlistId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;
    }

    /**
     * Get songs from playlist
     * Use offset and count for pagination, infinity scrolling...
     * @param context
     * @param playlistId
     * @param offset
     * @param count
     * @return
     */
    public static List<Song> getSongsFromPlaylist(Context context, int playlistId, int offset, int count) {
        LOGD(TAG, "get "+count+" Songs From Playlist" + playlistId);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.PlaylistSongs.CONTENT_URI;
        Cursor c = resolver.query(uri,
                Query.Projections.PLAYLISTSONG_PROJECTION,              // projection
                HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID + " = ?",// selection string
                new String[]{String.valueOf(playlistId)},               // selection args of strings
                HopAmChuanDBContract.PlaylistSongs._ID + " DESC LIMIT  " + offset + ", " + count);                    //  sort order

        List<Song> songs = new ArrayList<Song>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                // int id = c.getInt(c.getColumnIndex(HopAmChuanDBContract.PlaylistSongs._ID));
                int songId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.PlaylistSongs.SONG_ID));
                // int _playlistId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.PlaylistSongs.PLAYLIST_ID));

                songs.add(SongDataAccessLayer.getSongById(context, songId));
            }
            catch(Exception e) {
                LOGE(TAG, "error when parse song " + e.getMessage());
            }
        }
        c.close();
        return songs;
    }

    public static Playlist getPlaylistById(Context context, int playlistId) {
        LOGD(TAG, "Get Playist By Id");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
        Uri playlistUri = Uri.withAppendedPath(uri, "get/" + playlistId + "");

        Cursor c = resolver.query(playlistUri,
                Query.Projections.PLAYLIST_PROJECTION,    // projection
                null,                             // selection string
                null,                             // selection args of strings
                null);                            //  sort order


        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int id = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist._ID));
                int _playlistId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_ID));
                String playlistName = c.getString(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_NAME));
                String playlistDescription = c.getString(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_DESCRIPTION));
                Date playlistDate = (new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT)).parse(c.getString(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_DATE)));
                int playlistIsPublic = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_PUBLIC));
                int playlistNumOfSongs = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_NUMOFSONGS));

                c.close();
                return new Playlist(_playlistId, playlistName, playlistDescription, playlistDate, playlistIsPublic, playlistNumOfSongs);
            } catch (Exception e) {
                LOGE(TAG, "Fail to parse: " + c.toString());
                e.printStackTrace();
            }
        }
        c.close();
        return null;
    }

    public static boolean insertAllPlaylist(Context context, List<Playlist> playlists) {
        boolean status = true;
        for (Playlist playlist : playlists) {
            String res = insertPlaylist(context, playlist);
            if (res == null) status = false;
        }
        return status;
    }

    public static String insertPlaylist(Context context, Playlist playlist) {
        LOGD(TAG, "Adding an playlist: " + playlist.toString());

        try {
            int playlistId = playlist.playlistId;
            // If the playlist is inserted by user (playlistId = 0), then find a new id for it.
            if (playlistId == 0) {
                playlistId = getMaxPlaylistId(context) + 1;
            } else {
                // This means the playlist is inserted by sync action, keep the id as it is.
            }

            ContentValues cv = new ContentValues();
            cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_ID, playlistId);
            cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_NAME, playlist.playlistName);
            cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_DATE, (new SimpleDateFormat(Config.DEFAULT_DATE_FORMAT)).format(playlist.date));
            cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_DESCRIPTION, playlist.playlistDescription);
            cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_PUBLIC, playlist.isPublic);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
            Uri insertedUri = resolver.insert(uri, cv);
            LOGD(TAG, "inserted uri: " + insertedUri);
            return insertedUri.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static int renamePlaylist(Context context, int playlistId, String newName, String newDesc) {
        LOGD(TAG, "Renaming playlist " + playlistId);

        ContentValues cv = new ContentValues();
        cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_NAME, newName);
        cv.put(HopAmChuanDBContract.Playlist.PLAYLIST_DESCRIPTION, newDesc);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
        Uri updateUri = Uri.withAppendedPath(uri, playlistId + "");
        int updatedUriResult = resolver.update(updateUri, cv, HopAmChuanDBContract.Playlist.PLAYLIST_ID + "=" + playlistId, null);
        LOGD(TAG, "Updated uri: " + updatedUriResult);
        return updatedUriResult;
    }

    public static void removeAllPlaylists(Context context) {
        List<Playlist> playlists = getAllPlayLists(context);
        for (Playlist p : playlists) {
            removePlaylistById(context, p.playlistId);
        }
    }

    public static void removePlaylistById(Context context, int playlistId) {
        LOGD(TAG, "Delete playlist");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
        Uri deleteUri = Uri.withAppendedPath(uri, playlistId + "");
        resolver.delete(deleteUri, null, null);

        PlaylistSongDataAccessLayer.removePlaylist_Song(context, playlistId);
    }

    public static int getMaxPlaylistId(Context context) {
        LOGD(TAG, "Get max playlist id");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = HopAmChuanDBContract.Playlist.CONTENT_URI;
        Cursor c = resolver.query(uri,
                new String[]{HopAmChuanDBContract.Playlist.PLAYLIST_ID},      // projection
                null,      // selection string
                null,           // selection args of strings
                HopAmChuanDBContract.Playlist.PLAYLIST_ID + " DESC LIMIT 1");                                          //  sort order

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            try {
                int _playlistId = c.getInt(c.getColumnIndex(HopAmChuanDBContract.Playlist.PLAYLIST_ID));
                c.close();
                return  _playlistId;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();
        return 0;
    }

}
