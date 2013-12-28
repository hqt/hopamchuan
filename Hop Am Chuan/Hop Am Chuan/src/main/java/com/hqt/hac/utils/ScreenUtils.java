package com.hqt.hac.utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class ScreenUtils {

    public static final int NUMBER_IMAGE_VERTICAL_NORMAL_SCREEN = 3;
    public static final int NUMBER_IMAGE_HORIZONTAL_NORMAL_SCREEN = 2;

    public static Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
