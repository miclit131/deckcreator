package ml131.de.hdm_stuttgart.mi;

import com.google.gson.stream.JsonReader;
import ml131.de.hdm_stuttgart.mi.exceptions.ConnectionNotFoundException;
import ml131.de.hdm_stuttgart.mi.exceptions.ExceptionCluster;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class FileManager {

    public static String format;
    private static InputStream inputStream;

    static {
        try {
            inputStream = new FileInputStream("/home/ml131/Desktop/stuff/Modern.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void getFormat(){
        format=searchEngine.currentFilter.cardFilter.get("format").toString();
    }

    public static JsonReader openConnectionToFile(String format)throws IOException{

            try {
                inputStream = new FileInputStream("/home/ml131/Desktop/stuff/"+format+".json");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        Reader inputStreamReader = new InputStreamReader(inputStream);
        com.google.gson.stream.JsonReader reader = new com.google.gson.stream.JsonReader(inputStreamReader);
        return reader;
    }


    public static JSONObject database() {

        JSONParser parser = new JSONParser();
        try {
            Object obj;
            obj = parser.parse(new FileReader("/home/ml131/Desktop/stuff/Standard.json"));

            JSONObject jsonObject = (JSONObject) obj;

            String name = (String) jsonObject.get("name");
            System.out.println(name);


            return jsonObject;
            // loop array

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean pingURL(String url, int timeout) throws ExceptionCluster {
        url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if(200 <= responseCode && responseCode <= 399){
                return (200 <= responseCode && responseCode <= 399);}else{
                throw new ConnectionNotFoundException("Connection failed");}
        } catch (IOException exception) {
            return false;
        }
    }
}

