package com.hqt.hac.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.view.BunnyApplication;

import java.io.File;

/** contain useful methods for loading resource from system */
public class ResourceUtils {

    public static String[] loadStringArray(int ResourceId) {
        return BunnyApplication.getAppContext().getResources().getStringArray(ResourceId);
    }

    public static Drawable getDrawableFromResId(int id) {
        return BunnyApplication.getAppContext().getResources().getDrawable(id);
    }

    public static boolean isDatabaseFileExist() {
        File file = new File("/data/data/com.hqt.hac.view/databases/" + HopAmChuanDatabase.DATABASE_NAME);
        return file.exists();
    }

}
