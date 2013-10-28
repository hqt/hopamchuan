package com.hqt.hac.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    /**
     * Reference this link : {@see <a href=
     * "http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html"
     * >Android Developer</a>} Using to detect network on Android Device if Wifi
     * | 3G -> can synchronize data
     */
    public static boolean isNetworkConnected(Context ctx) {

        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable()
                && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return true;
        } else if (mobile.isAvailable()
                && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return true;
        } else {
            return false;
        }

    }
}