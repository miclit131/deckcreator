package ml131.de.hdm_stuttgart.mi.util;

import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyNoInternetConnectionException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class InternetUtil {

    /***
     * URL encoder turns a string value into a UTF-8 formatted URL
     * @param value: string value to be formatted
     * @return url-formatted input string
     */
    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    /***
     * Helper function for that determines if we have a valid internet connection open.
     * @param url: Test url to be pinged
     * @param timeout: ping timeout
     */
    // inspired by https://stackoverflow.com/questions/32432372/android-unable-to-connect-to-internet-from-app?lq=1
    public static void pingURL(String url, int timeout) throws LogfriendlyNoInternetConnectionException {
        try{
            url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if(!(200 <= responseCode && responseCode <= 399)){
                throw new LogfriendlyNoInternetConnectionException("Connection failed, response code invalid");
            }
        }catch(IOException e){
            throw new LogfriendlyNoInternetConnectionException("Connection failed", e);
        }
    }

}
