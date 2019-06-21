package ml131.de.hdm_stuttgart.mi;


//import org.json.simple.JSONObject;

import javafx.application.HostServices;
import ml131.de.hdm_stuttgart.mi.JavaFxUI.AlertBox;
import ml131.de.hdm_stuttgart.mi.JavaFxUI.UserInterface;
import ml131.de.hdm_stuttgart.mi.exceptions.ConnectionNotFoundException;
import ml131.de.hdm_stuttgart.mi.exceptions.ExceptionCluster;
import ml131.de.hdm_stuttgart.mi.exceptions.ExceptionCluster;
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
    public static void main( String[] args ) throws IOException, ExceptionCluster {
        System.out.println("Hello World!");
// bug type filter works but not name filter

        UserInterface.fxWindow();
        System.out.println("it worked!");


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
    }

    }



