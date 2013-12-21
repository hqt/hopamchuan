package com.chord.helper;

import android.content.Context;
import android.view.SurfaceView;

import com.example.view.AndroidSurface;

/**
 * Created by Huynh Quang Thao on 10/29/13.
 */
public class ChordHelper {

    public static AndroidSurface drawChord(Context context, String chord, int pos) {
        AndroidSurface surface = new AndroidSurface(context, chord);
        return surface;
    }
}
