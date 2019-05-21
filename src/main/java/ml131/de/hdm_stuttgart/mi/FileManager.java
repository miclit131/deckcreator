package ml131.de.hdm_stuttgart.mi;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    JSONParser parser = new JSONParser();

    public FileManager() throws FileNotFoundException {
    }

    public static void createFile(String filename){
        JsonObjectBuilder bobTheBuilder = Json.createObjectBuilder();
        bobTheBuilder.add("vorname", "Paul");

        JsonObject jo = bobTheBuilder.build();

        try {
            FileWriter fw = new FileWriter(filename);
            JsonWriter jsonWriter = Json.createWriter(fw);
            jsonWriter.writeObject(jo);
            jsonWriter.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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



}
