package ml131.de.hdm_stuttgart.mi;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.message.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class searchEngine {
   static JSONParser parser = new JSONParser();
    static InputStream inputStream;
    static int cardcount=0;

    static {
        try {
            inputStream = new FileInputStream("/home/ml131/Desktop/stuff/Standard.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Reader inputStreamReader = new InputStreamReader(inputStream);
   static JsonReader reader = new JsonReader(inputStreamReader);

    public static ArrayList applyFilterToJson(){
        ArrayList a = new ArrayList();

        try(FileReader reader = new FileReader("/home/ml131/Desktop/stuff/Standard.json")){

            //  Object obj = jsonParser.parse(reader);
            //JSONObject jsonObject = (JSONObject) obj;
//DOM ebene
            JSONObject obj = (JSONObject) parser.parse(reader);
            JSONObject structure = (JSONObject) obj.get("DOM");
//cards ebene
            JSONArray cards = (JSONArray) structure.get("cards");
//innerhalb der ersten karte structure

            JSONObject cardsStructure = (JSONObject) cards.get(0);
//karten informationsblock aus dem structure der ersten karte
            JSONArray foreignData = (JSONArray) cardsStructure.get("foreignData");
            JSONObject cardInformation= (JSONObject)foreignData.get(0);
            cardInformation.get("name");

            System.out.println(foreignData.get(0));
            //System.out.println(cardsStructure.get("artist"));
            // System.out.println(cards.get(1));
            int b =0;
            for(int i=0;i<cardsStructure.size();i++){
                cardsStructure=(JSONObject)cards.get(i);
                foreignData = (JSONArray) cardsStructure.get("foreignData");
                cardInformation= (JSONObject)foreignData.get(0);
                a.add(cardInformation.get("name"));
                b++;
            }
            System.out.println(b);
            System.out.println(structure.get("baseSetSize"));
            // String test = (String) obj.get("DOM");

            System.out.println(obj.getClass());



            //JSONObject test = (JSONObject)obj.get("DOM");
            //System.out.println(test);
            //System.out.println(obj);

            //String name = (String) jsonObject.get("cards");
            // System.out.println(name);

            // JSONArray cardlist = (JSONArray) obj;
            //  System.out.println(cardlist);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();}
        catch (IOException e) {
            e.printStackTrace();}
        catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        return a;}

        static void enterObjectinJson(JsonReader reader) throws IOException{

                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("cards")) {
                        enterArrayInJson(reader);
                    }else if(name.equals("")){

                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
        }

        static void enterArrayInJson(JsonReader reader) throws IOException {

        reader.beginArray();
        while(reader.hasNext()){
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("foreignData")) {

                    reader.beginArray();
                    boolean german=true;
                   while(reader.hasNext()&&german){


                       reader.beginObject();
                       while (reader.hasNext()) {
                           String name2 = reader.nextName();
                           if(name2.equals("language")&&reader.nextString().equals("German")){
                              german=false;
                              reader.skipValue();
                           }

                           if (name2.equals("name")&&!german) {
                               cardcount++;
                               String cardname = reader.nextString();
                               System.out.println(cardname);
                           } else {
                               reader.skipValue();
                           }
                       }
                       reader.endObject();
                   }

                    while(reader.hasNext()&&!german){
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

    static void prettyprint2(JsonReader reader) throws IOException{

reader.beginObject();
        while(reader.hasNext()) {
            reader.nextName();
            enterObjectinJson(reader);
        }
reader.endObject();

    }

    static void prettyprint(JsonReader reader) throws IOException {
        int cardCount=0;

        while (reader.hasNext()) {

            JsonToken token = reader.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    reader.beginArray();
                   // writer.beginArray();
                    break;
                case END_ARRAY:
                    reader.endArray();
                   // writer.endArray();
                    break;
                case BEGIN_OBJECT:
                    reader.beginObject();
                   // writer.beginObject();
                    break;
                case END_OBJECT:
                    reader.endObject();
                   // writer.endObject();
                    break;
                case NAME:

                    if(reader.nextName().equals("DOM")){
                        enterObjectinJson(reader);
                    }
                    if(reader.nextName().equals("GRN")){
                        enterObjectinJson(reader);
                    }
                    if(reader.nextName().equals("M19")){
                        enterObjectinJson(reader);
                    }if(reader.nextName().equals("RIX")){
                    enterObjectinJson(reader);
                }
                    if(reader.nextName().equals("foreignData")){
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            if (name.equals("name")) {
                                cardCount++;
                                name = reader.nextString();
                                System.out.println(name);
                            } else {
                                reader.skipValue();
                            }
                        }
                    }
                    //String name = reader.nextName();
                    //System.out.println(name);
                   // writer.name(name);
                    break;
                case STRING:
                    String s = reader.nextString();
                   // writer.value(s);
                    break;
                case NUMBER:
                    String n = reader.nextString();
                    //writer.value(new BigDecimal(n));
                    break;
                case BOOLEAN:
                    boolean b = reader.nextBoolean();
                    //writer.value(b);
                    break;
                case NULL:
                    reader.nextNull();
                    //writer.nullValue();
                    break;
                case END_DOCUMENT:
                    System.out.println(cardCount);
                    return;
            }

        }

    }

}
