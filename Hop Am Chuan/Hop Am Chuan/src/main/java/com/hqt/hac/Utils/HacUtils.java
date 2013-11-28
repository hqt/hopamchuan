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
     * TODO: do this for song format
     *
     * @param context
     * @param richTextView
     * @param testTextView
     */
    public static void setSongFormatted(final Context context, TextView richTextView, String songContent, final Activity testTextView) {
        // this is the text we'll be operating on
        SpannableString text = new SpannableString(songContent);

        Pattern pattern = Pattern.compile("\\[.*?\\]");
        Matcher matcher = pattern.matcher(songContent);
        // Check all occurrences
        while (matcher.find()) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    // Get text
                    // TODO add check if widget instanceof TextView
                    TextView tv = (TextView) view;
                    // TODO add check if tv.getText() instanceof Spanned
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);

                    String chordName = s.subSequence(start, end).toString().replace("[", "").replace("]", "");

                    // Show toast


                    final Dialog dialog = new Dialog(testTextView);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.chordsurfaceview_toast, null);
                    final ChordSurfaceView chord = (ChordSurfaceView) layout.findViewById(R.id.chordViewA);
                    chord.drawChord(chordName);

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

//                    final Toast toast = new Toast(context);
//                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//                    View layout = inflater.inflate(R.layout.chordsurfaceview_toast, null);
//                    final ChordSurfaceView chord = (ChordSurfaceView) layout.findViewById(R.id.chordViewA);
//                    chord.drawChord(chordName);
//
//                    Button btnDismiss = (Button) layout.findViewById(R.id.close);
//                    btnDismiss.setText("X!");
//                    btnDismiss.setOnClickListener(new Button.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            chord.nextPosition();
//                        }
//                    });
//
//                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                    toast.setDuration(Toast.LENGTH_LONG);
//                    toast.setView(layout);
//                    toast.show();
                }
            };
            text.setSpan(clickableSpan, matcher.start(), matcher.end(), 0);
        }

        // make our ClickableSpans and URLSpans work
        richTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // shove our styled text into the TextView
        richTextView.setText(text, TextView.BufferType.SPANNABLE);
    }

    public static void transposeTextView(Context context, TextView textView, int distance, final Activity testTextView) {
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
        HacUtils.setSongFormatted(context, textView, content, testTextView);
    }
}
