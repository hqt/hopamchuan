package com.hqt.hac.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.hqt.hac.utils.EncodingUtils;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * App Preferences
 * @author Huynh Quang Thao
 */
public class PrefStore {

    public static final String TAG = makeLogTag(PrefStore.class);

    ///////////////////////////////////////////////////
    ///////////////  PREFERENCE KEY   /////////////////
    /** preference key that mark this is first run or not */
    public static final String PREF_IS_FIRST_RUN = "is_first_run";

    /** Preference key containing the HopAmChuan account username. */
    public static final String PREF_LOGIN_USER = "login_user";

    /** Preference key containing the HopAmChuan account email. */
    public static final String PREF_LOGIN_EMAIL = "login_email";

    /** Preference key containing the HopAmChuan account password. */
    public static final String PREF_LOGIN_PASSWORD = "login_password";

    /** Preference key containing currently latest version on system */
    public static final String PREF_LATEST_VERSION = "latest_database_version";

    /** Preference key containg currently image of account */
    public static final String PREF_USER_IMAGE = "user_image";

    ///////////////////////////////////////////////////////////////
    /////////////////   DEFAULT VALUE   ///////////////////////////
    /** Default value for {@link PrefStore#PREF_IS_FIRST_RUN} */
    public static final boolean DEFAULT_FIRST_RUN = true;

    /** Default value for {@link PrefStore#PREF_LOGIN_USER} */
    public static final String DEFAULT_LOGIN_USER = "ĐĂNG NHẬP";

    /** Default value for {@link PrefStore#PREF_LOGIN_EMAIL} */
    public static final String DEFAULT_LOGIN_EMAIL= "Bằng tài khoản hopamchuan.com";

    /** Default value for {@link PrefStore#PREF_LOGIN_PASSWORD} */
    public static final String DEFAULT_LOGIN_PASSWORD = "@password";

    /** Default value for {@link PrefStore#PREF_LATEST_VERSION} */
    public static final int DEFAULT_LATEST_VERSION = 2;

    /** Default value for {@link PrefStore#PREF_USER_IMAGE} */
    public static final String DEFAULT_USER_IMAGE = "image_data";


    ////////////////////////////////////////////////////////////////////
    /////////////////////////////  GETTER //////////////////////////////
    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public static String getLoginUsername(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LOGIN_USER, DEFAULT_LOGIN_USER);
    }

    public static String getEmail(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LOGIN_EMAIL, DEFAULT_LOGIN_EMAIL);
    }

    public static String getLoginPassword(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LOGIN_PASSWORD, DEFAULT_LOGIN_PASSWORD);
    }

    public static byte[] getUserImage(Context ctx) {
        String strImg =  getSharedPreferences(ctx).getString(PREF_USER_IMAGE, DEFAULT_USER_IMAGE);
        return EncodingUtils.decodeDataUsingBase64(strImg);
    }

    public static void setUserImage(Context ctx, byte[] image) {
//        LOGE(TAG, "OLD LENGTH: " + image.length);
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        String strImg = EncodingUtils.encodeByteArrUsingBase64(image);
        editor.putString(PREF_USER_IMAGE, strImg);
        editor.commit();
//        LOGE(TAG, "NEW LENGTH: " + PrefStore.getUserImage(ctx).length);
    }


    public static int getLatestVersion(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_LATEST_VERSION, DEFAULT_LATEST_VERSION);
    }

    ////////////////////////////////////////////////////////////////////
    /////////////////////////////  SETTER //////////////////////////////
    public static void setLoginUsername(Context ctx, String username) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGIN_USER, username);
        editor.commit();
    }

    public static void setEmail(Context ctx, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGIN_EMAIL, email);
        editor.commit();
    }

    public static void setLoginPassword(Context ctx, String password) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGIN_PASSWORD, password);
        editor.commit();
    }


    public static void setLatestVersion(Context ctx, int version) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_LATEST_VERSION, version);
        // editor.putInt(PREF_LATEST_VERSION, 1);      // debugging purpose
        editor.commit();
    }

    public static void setDeployedApp(Context ctx) {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_IS_FIRST_RUN, false);
        editor.commit();
    }


    //////////////////////////////////////////////////////////////////////
    ///////////////////// QUERY DATA EXIST ///////////////////////////////
    public static boolean isLoginUsernameSet(Context ctx) {
        return getLoginUsername(ctx) != null;
    }

    public static boolean isLoginInformationSet(Context ctx) {
        return isLoginUsernameSet(ctx) && getLoginPassword(ctx) != null;
    }

    public static boolean isFirstRun(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_IS_FIRST_RUN, DEFAULT_FIRST_RUN);
    }
}