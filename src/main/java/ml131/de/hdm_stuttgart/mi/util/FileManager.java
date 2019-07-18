package ml131.de.hdm_stuttgart.mi.util;

import com.google.gson.stream.JsonReader;

import java.io.*;


public class FileManager {

    public static String format;
    private static InputStream inputStream;
    private static String cardFilePath;

    static {
        ClassLoader classLoader = FileManager.class.getClassLoader();
        File file = new File(classLoader.getResource("Standard.json").getFile());
        cardFilePath = file.getAbsolutePath();
    }


    public static JsonReader openConnectionToFile(String format){

        try {
            inputStream = new FileInputStream(cardFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Reader inputStreamReader = new InputStreamReader(inputStream);
        return new JsonReader(inputStreamReader);
    }

}

