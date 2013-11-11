package com.hqt.hac.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;

/** contain useful methods for loading resource from system */
public class ResourceUtils {

    public static String[] loadStringArray(Context context, int ResourceId) {
        return context.getResources().getStringArray(ResourceId);
    }

    public static Drawable getDrawableFromResId(Context context, int id) {
        return context.getResources().getDrawable(id);
    }
}
