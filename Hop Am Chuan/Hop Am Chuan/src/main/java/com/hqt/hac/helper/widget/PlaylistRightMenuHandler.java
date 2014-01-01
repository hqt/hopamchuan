package com.hqt.hac.helper.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hqt.hac.helper.adapter.PlaylistManagerAdapter;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.view.R;

import java.lang.ref.WeakReference;

public class PlaylistRightMenuHandler {

    /*** Current selected playlist */
    public static Playlist selectedPlaylist;

    /*** Activity for dialogs */
    private static WeakReference<Activity> refActivity;

    /*** The popup window */
    private static PopupWindow pw;

    /*** Dialog for renaming */
    private static Dialog renPlaylistDialog;

    /*** Adapter to change after renaming / deleting */
    private static PlaylistManagerAdapter adapter;

    /*** Controls */
    private static EditText txtNewPlaylistName;
    private static EditText txtNewPlaylistDescription;

    public static Activity getActivity() {
        return refActivity.get();
    }

    public static void setRightMenuEvents(final Activity _activity, final PopupWindow _pw, PlaylistManagerAdapter _adapter) {

        refActivity = new WeakReference<Activity>(_activity);
        pw = _pw;
        adapter = _adapter;

        // Popup menu item
        final Button btnRenamePlaylist = (Button) pw.getContentView().findViewById(R.id.btnRenamePlaylist);
        final Button btnDeletePlaylist = (Button) pw.getContentView().findViewById(R.id.btnDeletePlaylist);

        // "Rename playlist" button
        renPlaylistDialog = DialogUtils.createDialog(_activity, R.string.rename_playlist,
                _activity.getLayoutInflater(), R.layout.dialog_newplaylist);

        // Load for existing data
        txtNewPlaylistName = (EditText) renPlaylistDialog.findViewById(R.id.txtNewPlaylistName);
        txtNewPlaylistDescription = (EditText) renPlaylistDialog.findViewById(R.id.txtNewPlaylistDescription);

        Button saveBtn = (Button) renPlaylistDialog.findViewById(R.id.btnCreatePlaylist);
        saveBtn.setText(R.string.rename_playlist_button);
        saveBtn.setOnClickListener(new RenamePlaylistEvent());

        // Rename button
        btnRenamePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load existing data
                txtNewPlaylistName.setText(selectedPlaylist.playlistName);
                txtNewPlaylistDescription.setText(selectedPlaylist.playlistDescription);

                // Show dialog
                renPlaylistDialog.show();

                // Shutdown popup menu
                pw.dismiss();
            }
        });

        btnDeletePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = refActivity.get();
                if (activity == null) {

                }
                ConfirmDialogEvent dialogClickListener = new ConfirmDialogEvent();
                AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                builder.setMessage(activity.getString(R.string.are_you_sure_delete_playlist)
                        + " \"" + selectedPlaylist.playlistName + "\"?")
                        .setPositiveButton(activity.getString(R.string.delete_playlist), dialogClickListener)
                        .setNegativeButton(activity.getString(R.string.login_cancelbtn), dialogClickListener).show();

                // Shutdown popup menu
                pw.dismiss();
            }
        });


    }

    private static class RenamePlaylistEvent implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Activity activity = refActivity.get();
            if (activity == null) {

            }

            if (txtNewPlaylistName.getText().toString().isEmpty()) {
                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.please_enter_playlist_name),
                        Toast.LENGTH_LONG);
                msg.show();
            } else {
                // Rename playlist
                selectedPlaylist.playlistName = txtNewPlaylistName.getText().toString();
                selectedPlaylist.playlistDescription = txtNewPlaylistDescription.getText().toString();

                PlaylistDataAccessLayer.renamePlaylist(activity.getApplicationContext(),
                        selectedPlaylist.playlistId,
                        selectedPlaylist.playlistName,
                        selectedPlaylist.playlistDescription);

                Toast msg = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.rename_playlist_success) + " " + selectedPlaylist.playlistName,
                        Toast.LENGTH_LONG);
                msg.show();

                // Refresh playlist list
                adapter.setPlayLists(PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext()));

                // Close dialog
                renPlaylistDialog.dismiss();
            }
        }
    }

    private static class ConfirmDialogEvent implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Activity activity = refActivity.get();
            if (activity == null) {

            }

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    // "Yes" button clicked
                    PlaylistDataAccessLayer.removePlaylistById(activity.getApplicationContext(), selectedPlaylist.playlistId);

                    // Refresh playlist list
                    adapter.setPlayLists(PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext()));
                    adapter.notifyDataSetChanged();

                    // Show notification
                    Toast msg = Toast.makeText(activity.getApplicationContext(),
                            activity.getString(R.string.playlist_deleted_success) + " " + selectedPlaylist.playlistName,
                            Toast.LENGTH_LONG);
                    msg.show();

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // "No" button clicked
                    break;
            }
        }
    }

    ;
}
