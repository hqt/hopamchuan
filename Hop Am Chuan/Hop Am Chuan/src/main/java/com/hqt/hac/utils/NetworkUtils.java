package com.hqt.hac.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.view.BunnyApplication;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class NetworkUtils {

    public static String TAG = makeLogTag(NetworkUtils.class);

    /**
     * Reference this link : {@see <a href=
     * "http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html"
     * >Android Developer</a>} Using to detect network on Android Device if Wifi | 3G -> can synchronize data
     */
    public static boolean isNetworkConnected() {
        Context ctx = BunnyApplication.getAppContext();
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

    /** should use this method. because base on user setting */
    public static boolean isDeviceNetworkConnected() {
        // TrungDQ: Bug 1: inverted setting for check box in SettingActivity
        if (!PrefStore.isMobileNetwork()) {
            return isWifiConnect();
        } else {
            return isNetworkConnected();
        }
    }

    /** wifi connect or not (not including 3G) */
    public static boolean isWifiConnect() {
        Context ctx = BunnyApplication.getAppContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isAvailable()
                && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    /** Get Data Fom URL Using GET Method */
    public static String getResponseFromGetRequest(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        /** after prepare for data. prepare for sending */
        try {
            /**
             * HttpResponse is an interface just like HttpGet
             * therefore we can't initialize them
             */
            HttpResponse httpResponse = httpClient.execute(httpGet);
            return parseHttpResponse(httpResponse);

        } catch (ClientProtocolException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /** Get Data Fom URL Using POST Method */
    public static String getResponseFromPOSTRequest(String url, Map<String, String> params) {

        HttpClient httpClient = new DefaultHttpClient();

        /**
         * In a POST request, we don't pass the values in the URL.
         * Therefore we use only the web page URL as the parameter of the HtpPost arguments
         */
        HttpPost httpPost = new HttpPost(url);

        /**
         * Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
         * uniquely separate by the other end.
         * To achieve that we use BasicNameValuePair
         */
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());

            /**
             * We add the content that we want to pass with the POST request to as name-value pairs
             * Now we put those sending details to an ArrayList with type safe of NameValuePair
             */
            nameValuePairList.add(basicNameValuePair);
        }

        /** after prepare for data. prepare for sending */
        try {

            /**
             *  UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
             *  This is typically useful while sending an HTTP POST request.
             */
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

            /** setEntity() hands the entity (here it is urlEncodedFormEntity) to the request. */
            httpPost.setEntity(urlEncodedFormEntity);

            try {
                /**
                 * HttpResponse is an interface just like HttpPost
                 * therefore we can't initialize them
                 */
                HttpResponse httpResponse = httpClient.execute(httpPost);
                return parseHttpResponse(httpResponse);


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String parseHttpResponse(HttpResponse httpResponse) {
        /**
         * according to the JAVA API, InputStream constructor do nothing.
         * So we can't initialize InputStream although it is not an interface
         */
        InputStream inputStream;
        try {
            inputStream = httpResponse.getEntity().getContent();
            /** buffer for performance */
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            /** StringBuilder for performance */
            StringBuilder stringBuilder = new StringBuilder();

            String bufferedStrChunk;

            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }

            Log.i(TAG, stringBuilder.toString());
            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** sleep for predefine second as we working on slow network @_@ */
    public static void stimulateNetwork(int milisecond) {
        try {
            Thread.sleep(milisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** use this method to determine thread signature when running on multi-thread */
    public static long getThreadId() {
        Thread t = Thread.currentThread();
        return t.getId();
    }

    /** get signature of current thread */
    public static String getThreadSignature() {
        Thread t = Thread.currentThread();
        long id = t.getId();
        String name = t.getName();
        long priority = t.getPriority();
        String groupname = t.getThreadGroup().getName();
        return (name + ":(id)" + id + ":(priority)" + priority
                + ":(group)" + groupname);
    }
}
