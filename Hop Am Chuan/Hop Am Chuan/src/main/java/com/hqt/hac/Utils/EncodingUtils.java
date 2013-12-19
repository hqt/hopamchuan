package com.hqt.hac.utils;

import android.util.Base64;
import com.google.gson.Gson;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Use this class to encode data before sending to server
 */
public class EncodingUtils {

    public static String encodeDataUsingHMAC_MD5(String data, String keyValue)
    {
        String encodeData = null;
        try
        {
            SecretKeySpec key = new SecretKeySpec((keyValue).getBytes("UTF-8"), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);

            byte[] bytes = mac.doFinal(data.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();

            for (int i=0; i<bytes.length; i++) {
                String hex = Integer.toHexString(0xFF &  bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            encodeData = hash.toString();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch(InvalidKeyException e){
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeData ;
    }

    public static String encodeDataUsingBase64(String data) {
        byte[] encodedBytes = Base64.encode(data.getBytes(), Base64.DEFAULT);
        return new String(encodedBytes);
    }

    public static byte[] decodeDataUsingBase64(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }

    /**
     * only need to keep in mind to encode only the individual query string parameter name
     * and/or value not the entire URL
     * for sure not the query string parameter separator character &
     * nor the parameter name-value separator character =.
     */
    public static String encodeParameterUsingUrlLenCode(String parameter) {
        try {
            return URLEncoder.encode(parameter, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeMapToJSONString(Map map) {
        return new Gson().toJson(map);
    }

    public static String encodeListToJSONString(List list) {
        return new Gson().toJson(list);
    }

    public static String encodeObjectToJSONString(Object object) {
        return new Gson().toJson(object);
    }
}
