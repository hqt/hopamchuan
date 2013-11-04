package com.hqt.hac.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class HacUtils {
    private static String TAG = makeLogTag(HacUtils.class);

    /**
     * TODO: do this for song format
     * @param context
     * @param richTextView
     */
    public static void setSongFormatted(final Context context, TextView richTextView, String songContent) {

        // this is the text we'll be operating on
        SpannableString text = new SpannableString(songContent);

        // make "Lorem" (characters 0 to 5) red
//        text.setSpan(new ForegroundColorSpan(RED), 0, 5, 0);

        // make "ipsum" (characters 6 to 11) one and a half time bigger than the textbox
//        text.setSpan(new RelativeSizeSpan(1.5f), 6, 11, 0);

        // make "dolor" (characters 12 to 17) display a toast message when touched
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // TODO add check if widget instanceof TextView
                TextView tv = (TextView) view;
                // TODO add check if tv.getText() instanceof Spanned
                Spanned s = (Spanned) tv.getText();
                int start = s.getSpanStart(this);
                int end = s.getSpanEnd(this);

                String text = "-" + s.subSequence(start, end).toString() + "-";
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        };

        text.setSpan(clickableSpan, 12, 17, 0);

        // make "sit" (characters 18 to 21) struck through
//        text.setSpan(new StrikethroughSpan(), 18, 21, 0);

        // make "amet" (characters 22 to 26) twice as big, green and a link to this site.
        // it's important to set the color after the URLSpan or the standard
        // link color will override it.
//        text.setSpan(new RelativeSizeSpan(2f), 22, 26, 0);
//        text.setSpan(new URLSpan("http://www.chrisumbel.com"), 22, 26, 0);
//        text.setSpan(new ForegroundColorSpan(GREEN), 22, 26, 0);

        // make our ClickableSpans and URLSpans work
        richTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // shove our styled text into the TextView
        richTextView.setText(text, TextView.BufferType.SPANNABLE);


        Log.i("TextViewDebug", "Width: ");
        Log.i("TextViewDebug", "Text Size: " + richTextView.getTextSize());
        Log.i("TextViewDebug", "f: " + (richTextView.getWidth() / richTextView.getTextSize()));
    }
}
