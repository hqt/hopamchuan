package com.hqt.hac.helper.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.test.RenamingDelegatingContext;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistSongDataAccessLayer;
import com.hqt.hac.model.json.JsonPlaylist;
import com.hqt.hac.utils.APIUtils;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.utils.UIUtils;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;

public class SyncSongAsyncTask extends AsyncTask<Void, Integer, Integer> {

    Activity activity;
    Context context;
    ProgressDialog dialog;

    public SyncSongAsyncTask(Activity activity) {
        this.activity = activity;
        context = activity.getBaseContext();
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Progress");

}
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        String username = PrefStore.getLoginUsername(context);
        String password = PrefStore.getLoginPassword(context);
        boolean res;

       // sync playlist
        publishProgress(0);
        List<Playlist> oldPlaylists = PlaylistDataAccessLayer.getAllPlayLists(context);
        List<JsonPlaylist> jsonPlaylists = JsonPlaylist.convert(oldPlaylists, context);
        List<Playlist> newPlaylists = APIUtils.syncPlaylist(username, password, jsonPlaylists);
        /*if (newPlaylists == null || newPlaylists.size() == newPlaylists.size()) {
            return 0;
        }*/
        LOGD("TRUNGDQ", "newPlaylists: " + newPlaylists.size());
        // update playlist
        publishProgress(1);
        if (newPlaylists != null) {
            // delete all playlists in system

            // TrungDQ: No need to send old playlists, this cause the duplicate playlist situation.
            // PlaylistDataAccessLayer.removeAllPlaylists(context, oldPlaylists);
            PlaylistDataAccessLayer.removeAllPlaylists(context);

            // insert all song of its playlist to database
            for (Playlist playlist : newPlaylists) {
                // insert playlist
                PlaylistDataAccessLayer.insertPlaylist(context, playlist);
                // insert songs of playlist
                List<Integer> ids = playlist.getAllSongIds(activity.getBaseContext());
                LOGD("TRUNGDQ", "Playlist " + playlist.playlistId + ": songs: " + playlist.getAllSongIds(activity.getBaseContext()));

                // TrungDQ: p.playlistId, not p.id, this cause the duplicate playlist situation also.
                // res = PlaylistSongDataAccessLayer.insertPlaylist_Song(context, p.id, ids);
                res = PlaylistSongDataAccessLayer.insertPlaylist_Song(context, playlist.playlistId, ids);
                if (!res) return 1;
            }
        }

        // sync favorite
        publishProgress(2);
       int[] favorite = FavoriteDataAccessLayer.getAllFavoriteSongIds(context);
        List<Integer> newFavorite = APIUtils.syncFavorite(username, password, favorite);
        /*if (newFavorite == null || newFavorite.size() == favorite.length) {
            return 2;
        }*/

        // update favorite
        publishProgress(3);
        res = FavoriteDataAccessLayer.syncFavorites(context, newFavorite);
        // res = FavoriteDataAccessLayer.addAllSongIdsToFavorite(context, newFavorite);
        if (!res) return 1;
        return 3;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        switch(progress[0]) {
            case 0:
                dialog.setMessage("Sync Playlist ...");
                break;
            case 1:
                dialog.setMessage("Update Playlist ...");
                break;
            case 2:
                dialog.setMessage("Sync Favorite ...");
                break;
            case 3:
                dialog.setMessage("Update Favorite ...");
                break;
            default:
                // do nothing
        }
        dialog.show();
    }

    @Override
    protected void onPostExecute(Integer result) {
        dialog.dismiss();

        switch (result) {
            // SYSTEM ERROR
            case 1: {
                AlertDialog dialog = DialogUtils.showAlertDialog(activity, "Error", "System Fail. Restart Application");
                dialog.show();
                break;
            }

            // successfully
            case 3: {

            }

            default:
                // do nothing
        }
    }
}
