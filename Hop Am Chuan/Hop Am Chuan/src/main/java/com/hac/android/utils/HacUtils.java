package com.hac.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.TextView;

import com.hac.android.config.PrefStore;
import com.hac.android.helper.widget.ChordClickableSpan;
import com.hac.android.model.dal.FavoriteDataAccessLayer;
import com.hac.android.model.dal.PlaylistDataAccessLayer;
import com.hac.chorddroid.helper.ChordHelper;
import com.hac.android.guitarchord.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param theActivity  the mActivity that contain the text view
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

    /**
     * Logout current user, reset all favorite, playlist
     * @param activity
     */
    public static void logout(final Activity activity, final AfterLogoutDelegate callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.logout_button))
                .setMessage(activity.getString(R.string.logout_confirm))
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PrefStore.setLoginUsername(null);
                        PrefStore.setLoginPassword(null);
                        PrefStore.setEmail(null);
                        PrefStore.setUserImage(null);

                        // Remove all playlist, favorites
                        PlaylistDataAccessLayer.removeAllPlaylists(activity.getApplicationContext());
                        FavoriteDataAccessLayer.removeAllFavorites(activity.getApplicationContext());

                        // Callback
                        if (callback != null) {
                            callback.onAfterLogout();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean isLoggedIn() {
        Bitmap checkLoggedIn = EncodingUtils.decodeByteToBitmap(PrefStore.getUserImage());
        return checkLoggedIn != null;
    }

    /////////////////////////////
    // Interface
    /////////////////////////////
    public interface AfterLogoutDelegate {
        public void onAfterLogout();
    }
}
