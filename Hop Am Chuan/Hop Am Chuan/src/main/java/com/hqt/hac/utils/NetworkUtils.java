package com.hqt.hac.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class NetworkUtils {

    public static String TAG = makeLogTag(NetworkUtils.class);

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

    /** Get Data Fom URL Using GET Method */
    public static final String getResponseFromGetRequest(String url) {
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);

        /** after prepare for data. prepare for sending */
        try {
            /**
             * HttpResponse is an interface just like HttpPost
             * therefore we can't initialize them
             */
            HttpResponse httpResponse = httpClient.execute(httpGet);

            /**
             * according to the JAVA API, InputStream constructor do nothing.
             * So we can't initialize InputStream although it is not an interface
             */
            InputStream inputStream = httpResponse.getEntity().getContent();

            /** buffer for performance */
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            /** StringBuilder for performance */
            StringBuilder stringBuilder = new StringBuilder();

            String bufferedStrChunk = null;

            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }

            Log.i(TAG, stringBuilder.toString());
            return stringBuilder.toString();

        } catch (ClientProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}