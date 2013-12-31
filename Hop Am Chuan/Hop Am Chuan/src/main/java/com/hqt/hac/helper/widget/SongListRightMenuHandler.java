package com.hqt.hac.helper.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.PlaylistListAdapter;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistSongDataAccessLayer;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.view.R;

import java.util.Date;
import java.util.List;

public class SongListRightMenuHandler {
    /**
     * Current selected song for song lists (favorite fragment, song list fragment,
     * playlist detail fragment **/
    public static Song selectedSong = null;

    /*** The ImageView for visual feedback when user like/unlike a song */
    public static ImageView theStar;

    /*** The list view control for Playlist */
    private static ListView mListView;

    /*** Playlist list for "Add to playlist" dialog */
    private static List<Playlist> playlists;

    /*** Activity for dialogs */
    private static Activity activity;

    /*** The popup window */
    private static PopupWindow popupWindow;

    /*** The popup window's controls */
    private static Button favoriteBtn;
    private static Button playlistBtn;
    private static Button shareBtn;


    /*** Dialog for playlist list */
    private static Dialog playlistListDialog;

    /*** Dialog for new playlist */
    private static Dialog newPlaylistDialog;

    /*** New playlist dialog controls */
    private static EditText txtNewPlaylistName;
    private static EditText txtNewPlaylistDescription;

    /*** Adapter for playlist list */
    private static PlaylistListAdapter playlistAdapter;


    public static void setRightMenuEvents(final Activity _activity, final PopupWindow _pw) {

        activity = _activity;
        popupWindow = _pw;

        // Popup menu item
        favoriteBtn = (Button) popupWindow.getContentView().findViewById(R.id.song_list_menu_addtofavorite);
        playlistBtn = (Button) popupWindow.getContentView().findViewById(R.id.song_list_menu_addtoplaylist);
        shareBtn = (Button) popupWindow.getContentView().findViewById(R.id.song_list_menu_share);

        // "Add to Favorite" button
        favoriteBtn.setOnClickListener(new ToggleFavorite());

        // "Add to playlist" dialog
        playlistListDialog = DialogUtils.createDialog(activity, R.string.title_add_to_playlist_dialog,
                activity.getLayoutInflater(), R.layout.dialog_addtoplaylist);

        mListView = (ListView) playlistListDialog.findViewById(R.id.playlist_list);

        playlists = PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext());

        playlistAdapter = new PlaylistListAdapter(activity, playlists);
        mListView.setAdapter(playlistAdapter);

        // Add click event item for this ListView
        mListView.setOnItemClickListener(new AddToPlaylistOnClick());

        /***** New playlist dialog *****/
        newPlaylistDialog = DialogUtils.createDialog(activity, R.string.new_playlist,
                activity.getLayoutInflater(), R.layout.dialog_newplaylist);

        Button createPlaylistBtn = (Button) newPlaylistDialog.findViewById(R.id.btnCreatePlaylist);

        createPlaylistBtn.setOnClickListener(new NewPlaylistOnClick());
        /**************/


        // Event to add new playlist
        LinearLayout addNewPlaylistBtn = (LinearLayout) playlistListDialog.findViewById(R.id.playlist_list_header);

        txtNewPlaylistName = (EditText) newPlaylistDialog.findViewById(R.id.txtNewPlaylistName);
        txtNewPlaylistDescription = (EditText) newPlaylistDialog.findViewById(R.id.txtNewPlaylistDescription);

        addNewPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNewPlaylistName.setText("");
                txtNewPlaylistDescription.setText("");
                txtNewPlaylistName.requestFocus();
                newPlaylistDialog.show();
            }
        });

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                playlistListDialog.setTitle(R.string.title_add_to_playlist_dialog);

                // Refresh playlists
                playlistAdapter.setPlaylists(PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext()));
                playlistListDialog.show();
            }
        });
    }

    public static void openPopupMenu(View _view, Song _song, ImageView _theStar) {
        /** Store the song that user clicked on the right menu **/
        selectedSong = _song;
        /** The Image View for visual feedback as user selection **/
        theStar = _theStar;

        /** Set label for favorite button **/
        if (_song.isFavorite == 0) {
            favoriteBtn.setText(R.string.song_detail_menu_favorite);
        } else {
            favoriteBtn.setText(R.string.song_detail_menu_unfavorite);
        }

        // int availableHeight = popupWindow.getMaxAvailableHeight(_view);
        int height = popupWindow.getHeight();
        /* LOGE(TAG, "HQT POPUP Height: " + height);
        if (availableHeight < popupWindow.getHeight()) {
            int[] loc_int = new int[2];
            // popupWindow.showAsDropDown(view, 10, 10);
            LOGE(TAG, "Not Enough Room Space");
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 35, 35);
        } else {

        }
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 35, 35);*/

        popupWindow.showAsDropDown(_view);
    }

    public static void openPopupMenu(View _view, final Song _song, ImageView _theStar,
                                     final int playlistId, final SongListAdapter mAdapter) {
        playlistBtn.setText(R.string.remove_from_playlist);

        // Override add playlist button
        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

                // Remove from playlist
                PlaylistSongDataAccessLayer.removePlaylist_Song(
                        activity.getApplicationContext(), playlistId, _song.songId);

                // Update UI
                mAdapter.remove(_song.songId);
            }
        });

        openPopupMenu(_view, _song, _theStar);
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

        @Override
        public void onClick(View view) {
            if (txtNewPlaylistName.getText().toString().isEmpty()) {
                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.please_enter_playlist_name),
                        Toast.LENGTH_LONG);
                msg.show();
            } else {
                // Add new playlist
                Playlist newPlaylist = new Playlist(Config.DEFAULT_PLAYLIST_ID_INSERTED_BY_USER,
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
                mListView.setAdapter(playlistAdapter);
                mListView.setOnItemClickListener(new AddToPlaylistOnClick());

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

                // Update UI
                theStar.setImageResource(R.drawable.star);
                selectedSong.isFavorite = 0;
            }
            // If not in favorite
            else {
                FavoriteDataAccessLayer.addSongToFavorite(activity.getApplicationContext(),
                        selectedSong.songId);
                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.added_to_favorite),
                        Toast.LENGTH_LONG);
                msg.show();

                // Update UI
                theStar.setImageResource(R.drawable.star_liked);
                selectedSong.isFavorite = (int) (new Date()).getTime();
            }

            popupWindow.dismiss();
        }
    }
}
