package com.hqt.hac.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hac_library.helper.ChordHelper;
import com.hqt.hac.helper.adapter.PlaylistListAdapter;
import com.hqt.hac.helper.widget.ChordClickableSpan;
import com.hqt.hac.helper.widget.DialogFactory;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistSongDataAccessLayer;
import com.hqt.hac.view.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * How to use:
 * HacUtils.setSongFormatted(theContext, yourTextView, songContent, yourActivity);
 * HacUtils.transposeTextView(theContext, theTextView, transposeDistance, theActivity);
 */
public class HacUtils {
    /** Current selected song for song lists (favorite fragment, song list fragment,
      playlist detail fragment **/
    public static Song selectedSong = null;

    /**
     * Format song lyric with clickable chord sign.
     *
     * @param context
     * @param richTextView the TextView that display the song lyric
     * @param songContent
     * @param theActivity the activity that contain the text view
     */
    public static void setSongFormatted(final Context context, TextView richTextView, String songContent, final Activity theActivity) {
        // This is the text we'll be operating on
        SpannableString text = new SpannableString(songContent);

        // Search for chord sign
        Pattern pattern = Pattern.compile("\\[.*?\\]");
        Matcher matcher = pattern.matcher(songContent);

        while (matcher.find()) {
            // Set event handler
            ClickableSpan clickableSpan = new ChordClickableSpan(theActivity, context);
            // Set the ClickableSpan to the text view
            text.setSpan(clickableSpan, matcher.start(), matcher.end(), 0);
        }

        // Make our ClickableSpans and URLSpans work
        richTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Shove our styled text into the TextView
        richTextView.setText(text, TextView.BufferType.SPANNABLE);
    }

    /**
     * Transpose all chord sign in the TextView
     * Notice: this require a TextView that already have content
     * @param context
     * @param textView
     * @param distance
     * @param theActivity
     */
    public static void transposeTextView(Context context, TextView textView, int distance, final Activity theActivity) {
        String content = textView.getText().toString();
        Pattern pattern = Pattern.compile("\\[.*?\\]");
        Matcher matcher = pattern.matcher(content);
        // Check all occurrences
        int stackChange = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String chordName = matcher.group().replace("[", "").replace("]", "");
            String newChordName = ChordHelper.transpose(chordName, distance);
            content = content.substring(0, start + stackChange) + "[" + newChordName + "]" + content.substring(end + stackChange);
            stackChange += newChordName.length() - chordName.length();
        }
        HacUtils.setSongFormatted(context, textView, content, theActivity);
    }

    /**
     * Set a formatted song lyric with two line chord style
     *
     * @param applicationContext
     * @param testTextView
     * @param songContent
     * @param theActivity
     */
    public static void setSongFormattedTwoLines(Context applicationContext, TextView testTextView, String songContent, Activity theActivity) {
        songContent = StringUtils.formatLyricTwoLines(songContent);
        setSongFormatted(applicationContext, testTextView, songContent, theActivity);
    }

    public static void setRightMenuEvents(final Activity activity, final PopupWindow pw) {
        // Popup menu item
        final Button favoriteBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_addtofavorite);
        final Button playlistBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_addtoplaylist);
        Button shareBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_share);

        // "Add to Favorite" button
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteDataAccessLayer.addSongToFavorite(activity.getApplicationContext(),
                        selectedSong.songId);
                pw.dismiss();
            }
        });

        // "Add to playlist" dialog
        final Dialog dialog = DialogFactory.createDialog(activity, R.string.title_add_to_playlist_dialog,
                activity.getLayoutInflater(), R.layout.addtoplaylist_dialog);

        ListView mPlaylists = (ListView) dialog.findViewById(R.id.playlist_list);

        final List<Playlist> playlists = PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext());

        final PlaylistListAdapter playlistAdapter = new PlaylistListAdapter(activity, playlists);
        mPlaylists.setAdapter(playlistAdapter);

        // Add click event item for this ListView
        mPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistSongDataAccessLayer.insertPlaylist_Song(
                        activity.getApplicationContext(),
                        playlists.get(position).playlistId, selectedSong.songId);

                dialog.dismiss();
            }
        });

        // Event to add new playlist
        LinearLayout addNewPlaylistBtn = (LinearLayout) dialog.findViewById(R.id.playlist_list_header);
        addNewPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: open new playlist popup
                LOGE("DEBUG", "new playlist");
            }
        });

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
                dialog.setTitle(selectedSong.title);

                // Refresh playlists
                playlistAdapter.setPlaylists(PlaylistDataAccessLayer.getAllPlayLists(activity.getApplicationContext()));
                dialog.show();
            }
        });
    }

}
