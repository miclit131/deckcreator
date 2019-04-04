package ml131.de.hdm_stuttgart.mi;

import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple http://logging.apache.org/log4j/2.x demo,
 * see file resources/log4j2.xml for configuration options
 * and A1.log containing debugging output.
 */

public class App {
    private static Logger log = LogManager.getLogger(App.class);

    /**
     * Your application's main entry point.
     *
     * @param args Yet unused
     */
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        int multiverseId = 411061;
      //  Card card = CardAPI.getCard(multiverseId);
      //  System.out.println(card.getName());
       ArrayList<Card> cards = (ArrayList<Card>) CardAPI.getAllCards();
        for(int i=2;i==100;i++){
            System.out.println(cards.get(i).getName());
        }
        log.debug("You may configure 'src/main/resources/log4j2.xml' ");
        log.debug("for adapting both console and 'A1.log' file output");
    }

    /**
     * This method purely exists for providing Junit tests.
     *
     * @param a first parameter
     * @param b second parameter
     * @return the sum of both parameters.
     */
    public static int add(final int a, final int b) {
        return a + b;
    }
}