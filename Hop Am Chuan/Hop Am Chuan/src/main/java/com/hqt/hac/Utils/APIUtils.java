package com.hqt.hac.utils;

import android.util.Log;
import com.hqt.hac.config.Config;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Get Data from Hop Am Chuan Site
 * this method relates to Network that should put to different thread to process
 */
public class APIUtils {

    public static String TAG = makeLogTag(APIUtils.class);

    public static void checkAccount(String username, String password) {
        String url = "";
        HttpClient httpClient = new DefaultHttpClient();

        /**
         * In a POST request, we don't pass the values in the URL.
         * Therefore we use only the web page URL as the parameter of the HtpPost arguemnt
         */
        HttpPost httpPost = new HttpPost(url);

        /**
         * Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
         * uniquely separate by the other end.
         * To achieve that we use BasicNameValuePair
         * Things we need to pass with the POST request
         */
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
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

                /**
                 * according to the JAVA APIUtils, InputStrem constructor do nothing.
                 * So we can't initialize InputStream although it is not an interface
                 */
                InputStream inputStream = httpResponse.getEntity().getContent();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder stringBuilder = new StringBuilder();

                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }

                Log.i("Debug", stringBuilder.toString());
                return stringBuilder.toString();

            } catch (ClientProtocolException cpe) {
                System.out.println("First Exception caz of HttpResponese :" + cpe);
                cpe.printStackTrace();
            } catch (IOException ioe) {
                System.out.println("Second Exception caz of HttpResponse :" + ioe);
                ioe.printStackTrace();
            }

        } catch (UnsupportedEncodingException uee) {
            System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
            uee.printStackTrace();
        }
        return null;
    }

    public static final String generateRequestLink(String url, String encodeJsonData) {
        StringBuilder builder = new StringBuilder(url);
        // create signature
        String signature = "";

        builder.append("?");
        // append public key
        builder.append("publicKey=" + Config.PUBLIC_KEY);
        // append private key
        builder.append("&privateKey=" + Config.PRIVATE_KEY);
        // append signature
        builder.append("&signature=" + signature);
        // append Json Data that already encode
        builder.append("&jsondata=" + encodeJsonData);
        return builder.toString();
    }


}
