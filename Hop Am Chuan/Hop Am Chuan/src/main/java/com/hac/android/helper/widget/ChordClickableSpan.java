package com.hac.android.helper.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hac.android.utils.DialogUtils;
import com.hac.chorddroid.helper.DrawHelper;
import com.hac.android.guitarchord.R;

/**
 * Created by Dinh Quang Trung on 11/29/13.
 */
public class ChordClickableSpan extends ClickableSpan {
    // Activity and Context are used to display ChordDialog
    private Activity theActivity;
    private Context context;

    // Chord position
    private int position = 0;
    // Chord transpose
    private int transpose = 0;
    // Set default image size (the actual size)
    private int defaultActualSize = 300;

    public ChordClickableSpan(Activity theActivity, Context context) {
        this.theActivity = theActivity;
        this.context = context;
    }

    @Override
    public void onClick(View view) {

        // Set default position and tranpose;
        position = 0;
        transpose = 0;

        // When click event is fired
        TextView tv = (TextView) view;
        Spanned s = (Spanned) tv.getText();

        // Get chord name
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        final String chordName = s.subSequence(start, end).toString().replace("[", "").replace("]", "");

        // Create chord dialog
        final  Dialog dialog = DialogUtils.createDialog(
                theActivity,
                R.string.chord_finger_position,
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                R.layout.chordsurfaceview_toast);
//        final Dialog dialog = new Dialog(theActivity);
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.chordsurfaceview_toast, null);

        // Chord view
        final ImageView chord = (ImageView) dialog.findViewById(R.id.chordViewA);

        final ImageView prevBtn = (ImageView) dialog.findViewById(R.id.prevPositionBtn);
        final ImageView nextBtn = (ImageView) dialog.findViewById(R.id.nextPositionBtn);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position--;
                if (position < 0) {
                    position = 8;
                }
                BitmapDrawable bitmapDrawable = DrawHelper.getBitmapDrawable(Resources.getSystem(), defaultActualSize, defaultActualSize, chordName, position, transpose);
                chord.setImageDrawable(bitmapDrawable);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position++;
                if (position > 8) {
                    position = 0;
                }
                BitmapDrawable bitmapDrawable = DrawHelper.getBitmapDrawable(Resources.getSystem(), defaultActualSize, defaultActualSize, chordName, position, transpose);
                chord.setImageDrawable(bitmapDrawable);
            }
        });

        chord.setOnTouchListener(new OnSwipeTouchListener() {
            public void onSwipeRight() {
                position--;
                if (position < 0) {
                    position = 8;
                }
                BitmapDrawable bitmapDrawable = DrawHelper.getBitmapDrawable(Resources.getSystem(), defaultActualSize, defaultActualSize, chordName, position, transpose);
                chord.setImageDrawable(bitmapDrawable);
            }
            public void onSwipeLeft() {
                position++;
                if (position > 8) {
                    position = 0;
                }
                BitmapDrawable bitmapDrawable = DrawHelper.getBitmapDrawable(Resources.getSystem(), defaultActualSize, defaultActualSize, chordName, position, transpose);
                chord.setImageDrawable(bitmapDrawable);
            }
            public void onSwipeTop() {
                transpose++;
                position = 0;
                if (transpose > 11) {
                    transpose = 0;
                }
                BitmapDrawable bitmapDrawable = DrawHelper.getBitmapDrawable(Resources.getSystem(), defaultActualSize, defaultActualSize, chordName, position, transpose);
                chord.setImageDrawable(bitmapDrawable);
            }
            public void onSwipeBottom() {
                transpose--;
                position = 0;
                if (transpose < 0) {
                    transpose = 11;
                }
                BitmapDrawable bitmapDrawable = DrawHelper.getBitmapDrawable(Resources.getSystem(), defaultActualSize, defaultActualSize, chordName, position, transpose);
                chord.setImageDrawable(bitmapDrawable);
            }

        });

        BitmapDrawable bitmapDrawable = DrawHelper.getBitmapDrawable(Resources.getSystem(), defaultActualSize, defaultActualSize, chordName, position, transpose);
        chord.setImageDrawable(bitmapDrawable);

        dialog.show();

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        // Remove underline
        ds.setUnderlineText(false);
    }
}
