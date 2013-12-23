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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Date;
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
    /**
     * Format song lyric with clickable chord sign.
     *
     * @param context
     * @param richTextView the TextView that display the song lyric
     * @param songContent
     * @param theActivity  the activity that contain the text view
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
     *
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

}
