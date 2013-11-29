package com.hqt.hac.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hac_library.components.ChordSurfaceView;
import com.hac_library.helper.ChordHelper;
import com.hqt.hac.view.R;
import com.hqt.hac.view.test.TestTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class HacUtils {
    private static String TAG = makeLogTag(HacUtils.class);

    /**
     * Format song lyric with clickable chord sign.
     * TODO: blur content
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
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    // When click event is fired
                    TextView tv = (TextView) view;
                    Spanned s = (Spanned) tv.getText();

                    // Get chord name
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);
                    String chordName = s.subSequence(start, end).toString().replace("[", "").replace("]", "");

                    // Create chord dialog
                    final Dialog dialog = new Dialog(theActivity);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.chordsurfaceview_toast, null);

                    // Chord view
                    final ChordSurfaceView chord = (ChordSurfaceView) layout.findViewById(R.id.chordViewA);
                    chord.drawChord(chordName);

                    // TODO: assign button later
                    // Buttons
                    Button btnDismiss = (Button) layout.findViewById(R.id.close);
                    btnDismiss.setText("X!");
                    btnDismiss.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chord.nextPosition();
                        }
                    });

                    dialog.setContentView(layout);
                    dialog.setTitle("Hợp âm " + chordName);
                    dialog.show();

                }
            };
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
}
