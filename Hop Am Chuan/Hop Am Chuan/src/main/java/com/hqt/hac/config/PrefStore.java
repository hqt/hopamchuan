package com.hqt.hac.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.hqt.hac.utils.EncodingUtils;
import com.hqt.hac.view.BunnyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    /** Preference key containing currently latest date on system */
    public static final String PREF_LATEST_UPDATE_DATE = "latest_update_date";

    /** Preference key containg currently image of account */
    public static final String PREF_USER_IMAGE = "user_image";

    /** Preference key contain currently language */
    public static final String PREF_LANGUAGE = "system_language";

    /** Preference key contain auto update or not */
    public static final String PREF_AUTO_UPDATE = "auto_update";

    /** Preference key contain auto sync or not */
    public static final String PREF_AUTO_SYNC = "auto_sync";

    /** Preference key contain is 3g or not */
    public static final String PREF_IS_MOBILE_NETWORK = "is_mobile_network";


    ///////////////////////////////////////////////////////////////
    /////////////////   DEFAULT VALUE   ///////////////////////////
    /** Default value for {@link PrefStore#PREF_IS_FIRST_RUN} */
    public static final boolean DEFAULT_FIRST_RUN = true;

    /** Default value for {@link PrefStore#PREF_LOGIN_USER} (return empty string for multi-language concept) */
    public static final String DEFAULT_LOGIN_USER = "";

    /** Default value for {@link PrefStore#PREF_LOGIN_EMAIL} (return empty string for multi-language concept) */
    public static final String DEFAULT_LOGIN_EMAIL= "";

    /** Default value for {@link PrefStore#PREF_LOGIN_PASSWORD} */
    public static final String DEFAULT_LOGIN_PASSWORD = "@password";

    /** Default value for {@link PrefStore#PREF_LATEST_VERSION} */
    public static final int DEFAULT_LATEST_VERSION = 1;

    /** Default value for {@link PrefStore#PREF_LATEST_UPDATE_DATE} (return empty string for multi-language concept) */
    public static final String DEFAULT_PREF_LATEST_UPDATE_DATE = "";

    /** Default value for {@link PrefStore#PREF_USER_IMAGE} */
    public static final String DEFAULT_USER_IMAGE = "image_data";

    /** Default value for {@link PrefStore#PREF_LANGUAGE} */
    public static final String DEFAULT_LANGUAGE = "vi";

    /** Default value for {@link PrefStore#PREF_AUTO_UPDATE} */
    public static final boolean DEFAULT_AUTO_UPDATE = false;
    /** Default value for {@link PrefStore#PREF_AUTO_SYNC} */
    public static final boolean DEFAULT_AUTO_SYNC = false;
    /** Default value for {@link PrefStore#PREF_IS_MOBILE_NETWORK} */
    public static final boolean DEFAULT_AUTO_MOBILE_NETWORK = false;

    ////////////////////////////////////////////////////////////////////
    /////////////////////////////  GETTER //////////////////////////////
    public static SharedPreferences getSharedPreferencesWithContext(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(BunnyApplication.getAppContext());
    }

    public static String getLoginUsername() {
        return getSharedPreferences().getString(PREF_LOGIN_USER, DEFAULT_LOGIN_USER);
    }

    public static String getEmail() {
        return getSharedPreferences().getString(PREF_LOGIN_EMAIL, DEFAULT_LOGIN_EMAIL);
    }

    public static String getLoginPassword() {
        return getSharedPreferences().getString(PREF_LOGIN_PASSWORD, DEFAULT_LOGIN_PASSWORD);
    }

    public static byte[] getUserImage() {
        String strImg =  getSharedPreferences().getString(PREF_USER_IMAGE, DEFAULT_USER_IMAGE);
        return EncodingUtils.decodeDataUsingBase64(strImg);
    }

    public static int getLatestVersion() {
        return getSharedPreferences().getInt(PREF_LATEST_VERSION, DEFAULT_LATEST_VERSION);
    }

    public static String getLastedUpdateDate() {
        return getSharedPreferences().getString(PREF_LATEST_UPDATE_DATE, DEFAULT_PREF_LATEST_UPDATE_DATE);
    }

    public static String getSystemLanguage() {
        return getSharedPreferences().getString(PREF_LANGUAGE, DEFAULT_LANGUAGE);
    }

    public static boolean isAutoUpdate() {
        return getSharedPreferences().getBoolean(PREF_AUTO_UPDATE, DEFAULT_AUTO_UPDATE);
    }

    public static boolean isAutoSync() {
        return getSharedPreferences().getBoolean(PREF_AUTO_SYNC, DEFAULT_AUTO_SYNC);
    }
    public static boolean isMobileNetwork() {
        return getSharedPreferences().getBoolean(PREF_IS_MOBILE_NETWORK, DEFAULT_AUTO_MOBILE_NETWORK);
    }

    ////////////////////////////////////////////////////////////////////
    /////////////////////////////  SETTER //////////////////////////////
    public static void setLoginUsername(String username) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_LOGIN_USER, username);
        editor.commit();
    }

    public static void setEmail(String email) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_LOGIN_EMAIL, email);
        editor.commit();
    }

    public static void setLoginPassword(String password) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_LOGIN_PASSWORD, password);
        editor.commit();
    }

    public static void setLastestVersion(int version) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(PREF_LATEST_VERSION, version);
        editor.commit();
    }

    public static void setLastedUpdate(Date date) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_LATEST_UPDATE_DATE, (new SimpleDateFormat(Config.UPDATE_DATE_FORMAT)).format(date));
        editor.commit();
    }

    public static void setDeployedApp() {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREF_IS_FIRST_RUN, false);
        editor.commit();
    }

    public static void setUserImage(byte[] image) {
//        LOGE(TAG, "OLD LENGTH: " + image.length);
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        String strImg = EncodingUtils.encodeByteArrUsingBase64(image);
        editor.putString(PREF_USER_IMAGE, strImg);
        editor.commit();
//        LOGE(TAG, "NEW LENGTH: " + PrefStore.getUserImage(ctx).length);
    }

    public static void setSystemLanguage(String language) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_LANGUAGE, language);
        editor.commit();
    }

    public static void setAutoUpdate(boolean state) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREF_AUTO_UPDATE, state);
        editor.commit();
    }

    public static void setAutoSyc(boolean state) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREF_AUTO_SYNC, state);
        editor.commit();
    }

    public static void setCanMobileNetwork(boolean state) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREF_IS_MOBILE_NETWORK, state);
        editor.commit();
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////// QUERY DATA EXIST ///////////////////////////////
    public static boolean isLoginUsernameSet() {
        return getLoginUsername() != null;
    }

    public static boolean isLoginInformationSet() {
        return isLoginUsernameSet() && getLoginPassword() != null;
    }

    public static boolean isFirstRun() {
        return getSharedPreferences().getBoolean(PREF_IS_FIRST_RUN, DEFAULT_FIRST_RUN);
    }
}