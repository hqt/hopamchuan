package com.hqt.hac.helper.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hqt.hac.helper.adapter.PlaylistListAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistSongDataAccessLayer;
import com.hqt.hac.view.R;

import java.util.Date;
import java.util.List;

/**
 * Created by Dinh Quang Trung on 12/24/13.
 */
public class SongListRightMenuHandler {
    /**
     * Current selected song for song lists (favorite fragment, song list fragment,
     * playlist detail fragment *
     */
    public static Song selectedSong = null;

    /**
     * The list view control
     */
    static ListView mPlaylists;

    /**
     * Playlist list for "Add to playlist" dialog
     */
    static List<Playlist> playlists;

    /**
     * Activity for dialogs
     */
    static Activity activity;

    /**
     * The popup window
     */

    static PopupWindow pw;

    /**
     * Dialog for playlist list
     */
    static Dialog playlistListDialog;

    /**
     * Dialog for new playlist
     */
    static Dialog newPlaylistDialog;

    /**
     * Adapter for playlist list
     */
    static PlaylistListAdapter playlistAdapter;


    public static void setRightMenuEvents(final Activity _activity, final PopupWindow _pw) {

        activity = _activity;
        pw = _pw;

        // Popup menu item
        final Button favoriteBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_addtofavorite);
        final Button playlistBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_addtoplaylist);
        Button shareBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_share);

        // "Add to Favorite" button
        favoriteBtn.setOnClickListener(new ToggleFavorite());

        // "Add to playlist" dialog
        playlistListDialog = DialogFactory.createDialog(activity, R.string.title_add_to_playlist_dialog,
                activity.getLayoutInflater(), R.layout.dialog_addtoplaylist);

        mPlaylists = (ListView) playlistListDialog.findViewById(R.id.playlist_list);

        playlists = PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext());

        playlistAdapter = new PlaylistListAdapter(activity, playlists);
        mPlaylists.setAdapter(playlistAdapter);

        // Add click event item for this ListView
        mPlaylists.setOnItemClickListener(new AddToPlaylistOnClick());

        /***** New playlist dialog *****/
        newPlaylistDialog = DialogFactory.createDialog(activity, R.string.new_playlist,
                activity.getLayoutInflater(), R.layout.dialog_newplaylist);

        Button createPlaylistBtn = (Button) newPlaylistDialog.findViewById(R.id.btnCreatePlaylist);

        createPlaylistBtn.setOnClickListener(new NewPlaylistOnClick());
        /**************/


        // Event to add new playlist
        LinearLayout addNewPlaylistBtn = (LinearLayout) playlistListDialog.findViewById(R.id.playlist_list_header);
        addNewPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPlaylistDialog.show();
            }
        });

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
                playlistListDialog.setTitle(selectedSong.title);

                // Refresh playlists
                playlistAdapter.setPlaylists(PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext()));
                playlistListDialog.show();
            }
        });
    }

    private static class AddToPlaylistOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Insert
            PlaylistSongDataAccessLayer.insertPlaylist_Song(
                    activity.getApplicationContext(),
                    playlists.get(position).playlistId, selectedSong.songId);

            // Show message
            Toast msg = Toast.makeText(activity.getApplicationContext(),
                    activity.getString(R.string.add_to_playlist_success)
                            + " " + playlists.get(position).playlistName,
                    Toast.LENGTH_LONG);
            msg.show();

            // Close dialog
            playlistListDialog.dismiss();
        }
    }

    private static class NewPlaylistOnClick implements View.OnClickListener {
        final EditText txtNewPlaylistName = (EditText) newPlaylistDialog.findViewById(R.id.txtNewPlaylistName);
        final EditText txtNewPlaylistDescription = (EditText) newPlaylistDialog.findViewById(R.id.txtNewPlaylistDescription);

        @Override
        public void onClick(View view) {
            if (txtNewPlaylistName.getText().toString().isEmpty()) {
                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.please_enter_playlist_name),
                        Toast.LENGTH_LONG);
                msg.show();
            } else {
                // Add new playlist
                Playlist newPlaylist = new Playlist(0,
                        txtNewPlaylistName.getText().toString(),
                        txtNewPlaylistDescription.getText().toString(),
                        new Date(),
                        1);

                PlaylistDataAccessLayer.insertPlaylist(activity.getApplicationContext(), newPlaylist);

                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.create_playlist_success) + " " +newPlaylist.playlistName,
                        Toast.LENGTH_LONG);
                msg.show();

                // Refresh playlist list
                // We have to re-set the adapter for onItemClick event.
                playlists = PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext());
                playlistAdapter = new PlaylistListAdapter(activity, playlists);
                mPlaylists.setAdapter(playlistAdapter);
                mPlaylists.setOnItemClickListener(new AddToPlaylistOnClick());

                // Close dialog
                newPlaylistDialog.dismiss();
            }
        }
    }

    private static class ToggleFavorite implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // If already in favorite
            if (FavoriteDataAccessLayer.isInFavorite(
                    activity.getApplicationContext(), selectedSong.songId) > 0) {
                FavoriteDataAccessLayer.removeSongFromFavorite(
                        activity.getApplicationContext(), selectedSong.songId);

                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.removed_from_favorite),
                        Toast.LENGTH_LONG);
                msg.show();

            }
            // If not in favorite
            else {
                FavoriteDataAccessLayer.addSongToFavorite(activity.getApplicationContext(),
                        selectedSong.songId);
                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.added_to_favorite),
                        Toast.LENGTH_LONG);
                msg.show();
            }

            pw.dismiss();
        }
    }
}
