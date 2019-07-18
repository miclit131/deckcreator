package ml131.de.hdm_stuttgart.mi.search;


import com.google.gson.stream.JsonReader;
import javafx.scene.control.Hyperlink;
import ml131.de.hdm_stuttgart.mi.datamodel.Card;
import ml131.de.hdm_stuttgart.mi.datamodel.SearchResult;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyException;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyIOException;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyInvalidColorException;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyThreadException;
import ml131.de.hdm_stuttgart.mi.interfaces.Color;
import ml131.de.hdm_stuttgart.mi.datamodel.CardFilter;
import ml131.de.hdm_stuttgart.mi.datamodel.color.ColorFactory;
import ml131.de.hdm_stuttgart.mi.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/***
 * Core component containing the search logic
 */
public class SearchEngine{

    // set this value to true for comparing the performance boost of threading
    private final static boolean runSequential = false;

    // cache for accummulating card information
    private static HashMap<String, Object> temporaryCard;

    // keeps track for each temporaryCard if any of the card components violates a filter check
    private boolean cardCheckFailed;

    // specified separately for each search to be executed
    private CardFilter currentFilter;

    // thread-safe datastructure for storing retrieved cards
    private List<Card> queue = Collections.synchronizedList(new ArrayList<>());

    // thread collection turning temporaryCard objects to actual Cards, this is slow and needs to be done async.
    private ArrayList<Thread> workers;

    // result list of current search
    private ArrayList<Card> retrievedCards;

    // Necessary to create color objects
    private ColorFactory cf;

    private int processedValidCards;

    private List<String> cardNames;

    private List<String> cardTypes;

    private String language = "German";

    private Logger logger;


    public SearchEngine(CardFilter filter){
        logger = LogManager.getLogger(SearchEngine.class);
        currentFilter = filter;
        cf = new ColorFactory();
    }


    /***
     * Entry-point of search, we start parsing the file and searching for cards passing the filter. With numCards and
     * the size of the result list and the offset from where the program starts collecting can be controled. This depends
     * on the current pagination state.
     * @param reader: file handle containing all card information
     * @param numCards: the max. number of cards the user wishes to retrieve
     * @param offset: offset for pagination
     * @return SearchResult: with cards and information how many relevant cards exist
     */
    public SearchResult parseSetEdition(JsonReader reader, int numCards, int offset) throws LogfriendlyException {
        logger.info(String.format("Start seraching for %s cards starting from valid card %s", numCards, (offset+1)));
        queue = Collections.synchronizedList(new ArrayList<>());
        workers = new ArrayList<>();
        retrievedCards =  new ArrayList<>();
        processedValidCards = 0;
        cardNames = new ArrayList<>();
        cardTypes = new ArrayList<>();

        try{
            reader.beginObject();
            int i = 0;
            while (reader.hasNext()) {
                reader.nextName();
                logger.debug(String.format("Parsing set number %6s", i));
                ++i;
                parseCardSection(reader, numCards, offset);
            }

            // Collect results here if cards were assembled asynchronously
            if(!runSequential) {
                logger.trace("Collecting cards from worker threads");
                reader.endObject();
                for (Thread worker : workers) {
                    try {
                        worker.join();
                    } catch (InterruptedException error) {
                        throw new LogfriendlyThreadException("Error when collecting results", error);
                    }
                }
                retrievedCards.addAll(queue);
            }
            logger.info(String.format("Search finished, %3s cards out of %6s valid vards retrieved",
                    retrievedCards.size(), processedValidCards));
            return new SearchResult(retrievedCards, processedValidCards);
        }catch(IOException e){
            throw new LogfriendlyIOException(String.format("Error when parsing %3s",reader.getPath()), e);
        }
    }

    /***
     * Cards are grouped into different set editions, and there exist other information that are
     * not interesting for us. This method parses into the card information fields.
     * @param reader: file handle containing all card information
     * @param numCards: the max. number of cards the user wishes to retrieve
     * @param offset: offset for pagination
     * @throws IOException: if something went wrong with the reader or the file
     */
    private void parseCardSection(JsonReader reader, int numCards, int offset) throws IOException, LogfriendlyInvalidColorException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("cards")) {
                parseCards(reader, numCards, offset);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    /***
     * Iterates over all cards from a set edition and using the filter to conditionally add
     * cards to the result list.
     * @param reader: file handle containing all card information
     * @param numCards: the max. number of cards the user wishes to retrieve
     * @param offset: offset for pagination
     * @throws IOException: if something went wrong with the reader or the file
     */
    private void parseCards(JsonReader reader, int numCards, int offset) throws IOException, LogfriendlyInvalidColorException {
        // if we don't have numCards and offset for deck imports, include everything
        boolean ignorePagination = numCards == -1 && offset == -1;

        // include only cards that are relevant for the current page, specified by offset
        // don't include any cards that belong to pages before current page
        int upperBound = (offset + 1) * numCards;
        int lowerBound = (offset) * numCards;

        if(ignorePagination){
            logger.debug("Start parsing cards, all matching cards included");
        }else{
            logger.debug("Start parsing cards with offset");
        }

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();

            temporaryCard = new HashMap<>();
            cardCheckFailed = false;

            while (reader.hasNext()) {
                String field = reader.nextName();
                switch (field) {
                    case "colors":
                        List<Color> cardColors = parseColors(reader);
                        if (!currentFilter.checkColors(cardColors)) {
                            cardCheckFailed = true;
                        }
                        temporaryCard.put("colors", cardColors);
                        break;
                    case "rarity":
                        String value = reader.nextString();
                        saveToTemporaryCard(field, value);
                        if (!currentFilter.checkSingleValue(field, value)) {
                            cardCheckFailed = true;
                        }
                        break;
                    case "manaCost":
                        // extra check needed because the field "colors" doesn't cover colorless cards, because
                        // absense of color (which is the same as being colorless) is not covered
                        String manaCostValue = reader.nextString();
                        //check if we want to retrieve colorless-only cards
                        if (currentFilter.filterColorlessOnly()) {
                            // if yes, then do the actual colorless check
                            if (currentFilter.cardIsColorless(manaCostValue)) {
                                ArrayList<Color> colorless = new ArrayList<>();
                                colorless.add(cf.getColor("colorless"));
                                temporaryCard.put("colors", colorless);
                                cardCheckFailed = false; // undo false alarm
                                logger.trace("Current card is colorless and filter is on colorless only, " +
                                        "undo false alarm");
                            }
                        }
                        saveToTemporaryCard("manaCost", manaCostValue);
                        break;
                    case "convertedManaCost":
                        String convertedManaCostValue = reader.nextString();
                        saveToTemporaryCard("convertedManaCost", convertedManaCostValue);
                        break;
                    case "foreignData":
                        parseForeignData(reader);
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();

            // all cards have a multiverseId
            boolean allPropertiesCollected = temporaryCard.size()>=7;

            // if we have a card with no field text then the card has slipped through the filter earlier. Correcting this here.
            if(!temporaryCard.containsKey("text") && currentFilter.filterIsSet("text")){
                cardCheckFailed = true;
            }

            if(!temporaryCard.containsKey("colors") && currentFilter.filterIsSet("colors")){
                cardCheckFailed = true;
            }

            if(allPropertiesCollected) {
                if (!cardCheckFailed) {
                    logger.trace(String.format("Card %3s passed all tests", temporaryCard.get("multiverseId")));

                    boolean smallerThanUpperBound = processedValidCards < upperBound;
                    // ..or cards belonging to pages after the current page
                    boolean largerThanLowerBound = processedValidCards >= lowerBound;
                    boolean paginationTest = (smallerThanUpperBound && largerThanLowerBound) || ignorePagination;

                    if (paginationTest) {
                        logger.trace("Card passed range test for pagination");
                        createCardInstance();
                    } else {
                        logger.trace("Card failed range test for pagination");
                    }
                    processedValidCards++;
                    logger.trace(String.format("%3s valid cards processed.", processedValidCards));
                }
                temporaryCard = new HashMap<>();
                cardCheckFailed = false;
            }
        }
        reader.endArray();

        // When we arrive here the card has finished parsing, reset current card information
        temporaryCard = new HashMap<>();
        cardCheckFailed = false;
    }

    /***
     * All information that are language specific, e.g. "instant" vs. "Spontanzauber" for the property "type", are
     * stored in the Foreign data block, which is parsed here.
     * @param reader: file handle containing all card information
     * @throws IOException: if something went wrong with the reader or the file
     */
    private void parseForeignData(JsonReader reader) throws IOException {
        logger.trace("Start parsing foreign data field.");
        reader.beginArray();
        boolean languageNotFound = true;
        while (reader.hasNext() && languageNotFound) {
            reader.beginObject();
            while (reader.hasNext()) {
                String field = reader.nextName();

                if (field.equals("language") && reader.nextString().equals(language)) {
                    logger.trace(String.format("Matching language %3s found", language));
                    languageNotFound = false;
                }
                //because of some reason we need to parse language twice
                if (field.equals(("language"))) {
                    logger.trace("Skipping redundant language field");
                    field = reader.nextName();
                }

                if (!languageNotFound && !field.equals("language")) {
                    String value = "";
                    switch (field) {
                        // the same action for any of the three cases, grouped together
                        case "type":
                        case "name":
                        case "text":
                            // read value for card property
                            value = reader.nextString();
                            saveToTemporaryCard(field, value);
                            // use filter to check if the value for the field is valid, if not mark this card
                            // to be not included in result list
                            if(!currentFilter.checkSingleValue(field, value)){
                                cardCheckFailed = true;
                            }
                            break;
                        case "multiverseId":
                            String multiverseId = reader.nextString();
                            saveToTemporaryCard("multiverseId", multiverseId);
                            break;
                        default:
                            // we always enter default branch
                            reader.skipValue();
                            break;
                    }

                    // Populate list of all card names/types, later used for autocomplete fuctionality
                    if(field.equals("name")){
                        cardNames.add(value);
                    }else if(field.equals("type")){
                        cardTypes.add(value);
                    }

                } else {
                    reader.skipValue();
                }
            }
            runTilEndObject(reader);
            reader.endObject();
        }
        if(!languageNotFound){
            runTilEndObject(reader);
        }
//        while (reader.hasNext() && !languageNotFound) {
//            reader.skipValue();
//        }
        reader.endArray();
    }


    /***
     * Parses the color attribute of a card and returns Color objects. This turns for example {G} into an ArrayList
     * with the single value Color.GREEN.
     * @param reader: file handle containing all card information
     * @return list of color to which the card belong to
     * @throws IOException: if something went wrong with the reader or the file
     */
    private List<Color> parseColors(JsonReader reader) throws IOException, LogfriendlyInvalidColorException {
        logger.trace("Start parsing color");
        ArrayList<Color> cardColors = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            String shortcut = reader.nextString();
            Color color = cf.getColor(shortcut);
            logger.trace(String.format("Converted %3s to %6s", shortcut, color.getColor()));
            cardColors.add(color);
        }
        reader.endArray();
        logger.trace("Done parsing color");
        return cardColors;
    }

    /***
     * Depending on SearchEngine.runSequential a new card is created synchronously or in parallel with a
     * new worker assigned to it, the results are collected later and the parsing continues seamlessly.
     */
    private void createCardInstance() {
        // Text is optional, not every card has one
        String text = "";

        if(temporaryCard.containsKey("text")){
            text = temporaryCard.get("text").toString();
        }else{
            logger.trace("no text found, using default empty text");
        }

        // Special cards like land cards don't cost any mana
        String manaCost = "";
        if(temporaryCard.containsKey("manaCost")){
            manaCost = temporaryCard.get("manaCost").toString();
        }else{
            logger.trace("No mana cost found, using default empty string");
        }

        String convertedManaCost = "";
        if(temporaryCard.containsKey("convertedManaCost")){
            temporaryCard.get("convertedManaCost").toString();
        }else{
            logger.trace("No converted mana cost found, using default empty string");
        }


        // the card url is an external information that is added here
        Hyperlink tmpURL = new Hyperlink("https://www.cardmarket.com/de/Magic/Products/Search?searchString=");
        tmpURL.setOnAction(e -> {
            try {
                new ProcessBuilder("x-www-browser", tmpURL.getText()).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        if (runSequential) {
            //write to card to collection
            retrievedCards.add(new Card(temporaryCard.get("name").toString(),
                    convertedManaCost,
                    temporaryCard.get("type").toString(),
                    text,
                    FileManager.format,
                    temporaryCard.get("rarity").toString(),
                    tmpURL,
                    (ArrayList<Color>) temporaryCard.get("colors"),
                    language,
                    manaCost,
                    temporaryCard.get("multiverseId").toString(),
                    1));
        } else {
            // create job and launch asynchronously
            ParameterContainer params = new ParameterContainer();
            params.name = temporaryCard.get("name").toString();
            params.convertedManaCost = convertedManaCost;
            params.type = temporaryCard.get("type").toString();
            params.effect = text;
            params.format = FileManager.format;
            params.rarity = temporaryCard.get("rarity").toString();
            params.pictureLink = tmpURL;
            params.colors = (ArrayList<Color>)temporaryCard.get("colors");
            params.language = language;
            params.manaCost = manaCost;
            params.multiverseId = temporaryCard.get("multiverseId").toString();
            params.count = 1;

            Thread t = new CardInstantiationThread(params, queue);
            t.start();
            workers.add(t);
        }
    }

    /***
     * Helper function that adds card information and writes log message.
     * @param filterKey: type of card information
     * @param cardTypeText: actual card information
     */
    private void saveToTemporaryCard(String filterKey, String cardTypeText) {
        SearchEngine.temporaryCard.put(filterKey, cardTypeText);
        logger.trace(String.format("filterkey:%6s { %28s / has been added to temporary card }",filterKey,cardTypeText));
    }

    /***
     * Helper function that skips all values and runs to the end of the object
     * @param reader: file handle containing all card information
     * @throws IOException: if something went wrong with the reader or the file
     */
    private void runTilEndObject(JsonReader reader) throws IOException {
        logger.trace("Moving pointer of parser till the end of object.");
        while (reader.hasNext()) {
            reader.skipValue();
        }
    }

    /***
     * Getters below are called to retrieve data for autocomplete function
     * @return list of card names retrieved from first search
     */
    public List<String> getCardNames() {
        return cardNames;
    }
    public List<String> getCardTypes() {
        return cardTypes;
    }

}

