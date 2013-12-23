package com.hqt.hac.helper.widget;

import android.app.Activity;
import android.widget.Button;
import android.widget.PopupWindow;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

/**
 * Created by Dinh Quang Trung on 12/24/13.
 */
public class PlaylistRightMenuHandler {
    /**
     * Current selected playlist
     */
    public static Playlist selectedPlaylist;

    /**
     * Activity for dialogs
     */
    static Activity activity;

    /**
     * The popup window
     */

    static PopupWindow pw;

    public static void setRightMenuEvents(final Activity _activity, final PopupWindow _pw) {

        activity = _activity;
        pw = _pw;

        // Popup menu item
        final Button btnRenamePlaylist = (Button) pw.getContentView().findViewById(R.id.btnRenamePlaylist);
        final Button btnDeletePlaylist = (Button) pw.getContentView().findViewById(R.id.btnDeletePlaylist);

        // "Rename playlist" button


        // "Add to playlist" dialog

    }

}
