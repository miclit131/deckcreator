package ml131.de.hdm_stuttgart.mi;


//import org.json.simple.JSONObject;

import ml131.de.hdm_stuttgart.mi.JavaFxUI.UserInterface;

import java.text.ParseException;

import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;
//import javax.json.JsonReader;



/**
 * A simple http://logging.apache.org/log4j/2.x demo,
 * see file resources/log4j2.xml for configuration options
 * and A1.log containing debugging output.
 */

public class App {


    /**
     * Your application's main entry point.
     *
     * @param args Yet unused
     */
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );


// bug type filter works but not name filter
        //String name="Sonnenkamm-R";
       String name="Akad";
        //searchEngine.currentFilter.nameIsSet=true;
        searchEngine.currentFilter.keySetting.put("nameIsSet",true);
        searchEngine.currentFilter.cardFilter.put("name",name);

      /*  String type = "Vampir";
        searchEngine.currentFilter.typeIsSet=true;
        searchEngine.currentFilter.keySetting.put("typeIsSet",true);
        searchEngine.currentFilter.cardFilter.put("type",type); */

       // searchEngine.enterSetEdition(FileManager.reader);
        searchEngine.enterSetEdition(FileManager.openConnectionToFile("Modern"));
        System.out.println(searchEngine.cardcount);

        UserInterface.fxWindow();
        System.out.println("it worked!");
        //int multiverseId = 1;
        //Card card = CardAPI.getCard(multiverseId);
        //System.out.println(card.getName());

       // FileManager.createFile("testy");

//ArrayList a = searchEngine.applyFilterToJson();
       // System.out.println(a.toString());
//das ganze file lässt sich auslesen
// benötige struktur vom json um gezielt zu suchen und auszugeben
// readlink -f file.txt gibt im terminal den dateipfade vom file raus

// Structure DOM -> cards JSONArray -> jeder karten eintrag = structure -> foreigenData = JSONArray aus structures die
        // informationen über die karte in verschiedenen sprachen enthält -> um den namen einer karte zu erhalten
        // muss man in die foreignData structure einsteigen und einen array glied raussuchen und dann innerhalb dieses
        // arrayteil structure .get("name") anwenden

        System.out.println("ende");
        // JSONObject jsonObject= FileManager.database();
        //System.out.println(jsonObject.get(card));
  /*

        JSONArray cards = (JSONArray) jsonObject.get("cards");
        Iterator<String> iterator = cards.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
            */
        }
       // List<Card> cards = CardAPI.getAllCards();

     //   System.out.println(cards);

        //List<io.magicthegathering.javasdk.resource.Card> cards = CardAPI.getAllCards();
       // List<Card> cards = CardAPI.getAllCards();
       // System.out.println(cards);
           // ArrayList<Card> cards = (ArrayList<Card>) CardAPI.getAllCards();
       /* for(int i=2;i==100;i++){
            System.out.println(cards.get(i).getName());
      } */

    }



