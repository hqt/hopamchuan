package com.hqt.hac.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Created by Quang Trung on 11/3/13.
 */
public class HacUtils {
    private static String TAG = makeLogTag(ParserUtils.class);
//    LOGE(TAG, element.toString() + "cannot parse to Artist");

    public static void setSongFormatted(final Context context, TextView richTextView) {

        // this is the text we'll be operating on
        SpannableString text = new SpannableString("Lorem ipsum dolor sit amet");

        // make "Lorem" (characters 0 to 5) red
        text.setSpan(new ForegroundColorSpan(RED), 0, 5, 0);

        // make "ipsum" (characters 6 to 11) one and a half time bigger than the textbox
        text.setSpan(new RelativeSizeSpan(1.5f), 6, 11, 0);

        // make "dolor" (characters 12 to 17) display a toast message when touched
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "dolor", Toast.LENGTH_LONG).show();
            }
        };
        text.setSpan(clickableSpan, 12, 17, 0);

        // make "sit" (characters 18 to 21) struck through
        text.setSpan(new StrikethroughSpan(), 18, 21, 0);

        // make "amet" (characters 22 to 26) twice as big, green and a link to this site.
        // it's important to set the color after the URLSpan or the standard
        // link color will override it.
        text.setSpan(new RelativeSizeSpan(2f), 22, 26, 0);
        text.setSpan(new URLSpan("http://www.chrisumbel.com"), 22, 26, 0);
        text.setSpan(new ForegroundColorSpan(GREEN), 22, 26, 0);

        // make our ClickableSpans and URLSpans work
        richTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // shove our styled text into the TextView
        richTextView.setText(text, TextView.BufferType.SPANNABLE);
    }
}
