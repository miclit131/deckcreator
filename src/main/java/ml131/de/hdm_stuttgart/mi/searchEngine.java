package ml131.de.hdm_stuttgart.mi;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.message.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.json.Json;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchEngine {
   static JSONParser parser = new JSONParser();
    static InputStream inputStream;
    static int cardcount=0;
    static HashMap<String,Object> temporaryCard = new HashMap<>();
    static CardFilter currentFilter=new CardFilter();
    static String language="German";

    static boolean checkFilterAndSave(String filterKey,JsonReader reader)throws IOException {
        String cardTypeText = reader.nextString();
        if (searchEngine.currentFilter.keySetting.get(filterKey + "IsSet") &&
                cardTypeText.contains(searchEngine.currentFilter.cardFilter.get(filterKey).toString())) {
            searchEngine.temporaryCard.put(filterKey, cardTypeText);
            System.out.println(filterKey+" "+cardTypeText);
            return true;
        } else if (!searchEngine.currentFilter.keySetting.get(filterKey + "IsSet")) {
            searchEngine.temporaryCard.put(filterKey, cardTypeText);
            return true;
        } else {
            return false;
        }
    }
    static void runTilEndObject(JsonReader reader)throws IOException{
        while(reader.hasNext()){
            reader.skipValue();
        }
    }
    static void fillCurrentFilter(String cmc,
                                  String type,
                                  String effect,
                                  String color,
                                  String format,
                                  String name,
                                  String rarity){

        Map<String,Object> filterImage = new HashMap<>();
        if(!cmc.equals("")){filterImage.put("cmt",cmc);}
        if(!type.equals("")){filterImage.put("type",type);}
        if(!effect.equals("")){filterImage.put("effect",effect);}
        if(!color.equals("")){filterImage.put("color",color);}
        if(!format.equals("")){filterImage.put("format",format);}
        if(!name.equals("")){filterImage.put("name",name);}
        if(!rarity.equals("")){filterImage.put("rarity",rarity);}
       searchEngine.currentFilter=new CardFilter(filterImage);
    }
    static {
        try {
            inputStream = new FileInputStream("/home/ml131/Desktop/stuff/Standard.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    static Reader inputStreamReader = new InputStreamReader(inputStream);
   static JsonReader reader = new JsonReader(inputStreamReader);


    static void enterSetEdition(JsonReader reader) throws IOException{
        reader.beginObject();
        while(reader.hasNext()) {
            reader.nextName();
            enterCardSection(reader);
        }
        reader.endObject();
    }
    static void enterCardSection(JsonReader reader) throws IOException{
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("colors")){
                ArrayList<String> cardColors=new ArrayList<>();
                reader.beginObject();
                while(reader.hasNext()){
                    cardColors.add(reader.nextString());
                    System.out.println("added color");
                }
                reader.endObject();
                searchEngine.temporaryCard.put("colors",cardColors);
            }
            if (name.equals("cards")) {
                enterForeigenData(reader);
            }else if(name.equals("")){

            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }
    static void enterForeigenData(JsonReader reader) throws IOException {

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();

            while (reader.hasNext()) {
                String jsonName = reader.nextName();
                if (jsonName.equals("foreignData")) {

                    reader.beginArray();
                    boolean languageNotFound = true;
                    while (reader.hasNext() && languageNotFound) {
                        reader.beginObject();
                      cardinformation:  while (reader.hasNext()) {
                            String currentTokenName = reader.nextName();
                            if (currentTokenName.equals("language") && reader.nextString().equals(searchEngine.language)) {
                                languageNotFound = false;
                            }
                            //aus irgendeinem grund muss language 2 mal geskipt werden
                            if(currentTokenName.equals(("language"))){
                                currentTokenName=reader.nextName();
                            }

                            if (!languageNotFound&&!currentTokenName.equals("language")){
                            switch (currentTokenName) {
                                case "type":
                                   if(!checkFilterAndSave("type",reader)){break cardinformation;}
                                    break;
                                case "name":
                                    cardcount++;
                                    if(!checkFilterAndSave("name",reader)){
                                        break cardinformation;
                                    }
                                    //searchEngine.temporaryCard.put("name",reader.nextString());
                                    //System.out.println(reader.nextString());
                                    break;
                                case "text":
                                    if(!checkFilterAndSave("text",reader)){break cardinformation;}
                                    //searchEngine.temporaryCard.put("effect",reader.nextString());
                                    break;
                                case "multiverseId":
                                    reader.skipValue();
                                    break;
                                default:
                                    //programm geht immer in den default branch
                                    reader.skipValue();
                                    break;
                            }

                        }else {
                               reader.skipValue();
                            }
                    }
                    searchEngine.runTilEndObject(reader);
                    reader.endObject();
                    }


                    while (reader.hasNext() && !languageNotFound) {
                        reader.skipValue();
                    }
                    reader.endArray();

                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();

    }
    }
