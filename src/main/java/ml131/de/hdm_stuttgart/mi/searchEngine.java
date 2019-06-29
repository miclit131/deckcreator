package ml131.de.hdm_stuttgart.mi;


import com.google.gson.stream.JsonReader;
import javafx.scene.control.Hyperlink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class searchEngine {

    private final static boolean runSequential = false;
    private static Map<String, Object> filterImage = new HashMap<>();
    private static Logger log = LogManager.getLogger(searchEngine.class);
    static String logMessage;
    public static int cardcount = 0;
    static HashMap<String, Object> temporaryCard = new HashMap<>();
    public static CardFilter currentFilter = new CardFilter();
    public static ArrayList<Card> currentResults=new ArrayList<>();
    public static int cardcountFirstSearch=0;

    private static List queue = Collections.synchronizedList(new LinkedList<String>());

    private static ArrayList<Thread> workers = new ArrayList<>();

    public static int includedPages = 0;
    static String language = "German";
    static boolean cardCheckFailed = false;


    static boolean checkFilterAndSaveToTemporaryCard(String filterKey, JsonReader reader) throws IOException {
        Boolean filterIsSet=searchEngine.currentFilter.keySetting.get(filterKey + "IsSet");
        String filterValue=(String)currentFilter.cardFilter.get(filterKey);
        String cardTypeText = reader.nextString();
        if (filterIsSet &&
                cardTypeText.contains(filterValue)) {
            searchEngine.temporaryCard.put(filterKey, cardTypeText);
            logMessage=String.format("filterkey:%6s { %30s matched cardFilter : %12s }",filterKey,cardTypeText,filterValue);
            log.trace(logMessage);
            return true;
        } else if (!filterIsSet) {
            searchEngine.temporaryCard.put(filterKey, cardTypeText);
            logMessage=String.format("filterkey:%6s { %28s / has been added to temporary card }",filterKey,cardTypeText);
            log.trace(logMessage);
            return true;
            //filter is set but failed
        } else {
            searchEngine.cardCheckFailed = true;
            logMessage=String.format("filterkey:%6s { %30s failed cardFilter : %12s }",filterKey,cardTypeText,filterValue);
            log.trace(logMessage);
            return false;
        }
    }

    static void saveToTemporaryCard(String filterKey, JsonReader reader) throws IOException {
        String cardTypeText = reader.nextString();
        searchEngine.temporaryCard.put(filterKey, cardTypeText);
        logMessage=String.format("filterkey:%6s { %28s / has been added to temporary card }",filterKey,cardTypeText);
        log.trace(logMessage);
    }

    static void runTilEndObject(JsonReader reader) throws IOException {
        while (reader.hasNext()) {
            reader.skipValue();
        }
    }

    public static void fillCurrentFilter(String cmc,
                                         String type,
                                         String effect,
                                         String color,
                                         String format,
                                         String name,
                                         String rarity) {


        if (!cmc.equals("")) {
            searchEngine.filterImage.put("cmc", cmc);
        }
        if (!type.equals("")) {
            filterImage.put("type", type);
        }
        if (!effect.equals("")) {
            filterImage.put("effect", effect);
        }
        if (!color.equals("")) {
            filterImage.put("color", color);
        }
        if (!format.equals("")) {
            filterImage.put("format", format);
        }
        if (!name.equals("")) {
            filterImage.put("name", name);
        }
        if (!rarity.equals("")) {
            filterImage.put("rarity", rarity);
        }
        searchEngine.currentFilter = new CardFilter(filterImage);
        logMessage=String.format("CurrentFilter created with filterImage : %s",filterImage);
        log.info(logMessage);

    }


    public static void enterSetEdition(JsonReader reader, int pageSize, int currentPage) throws IOException {
        queue = Collections.synchronizedList(new LinkedList<String>());
        workers = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            reader.nextName();
            enterCardSection(reader, pageSize, currentPage);
        }

        if(!runSequential) {
            reader.endObject();
            searchRequestedResults.saveResult(currentResults, currentFilter);
            for (int i = 0; i < workers.size(); ++i) {
                try {
                    workers.get(i).join();
                } catch (Exception e) {
                    System.out.println();
                }
            }

            currentResults.addAll(queue);
        }
    }

    static void enterCardSection(JsonReader reader, int pageSize, int currentPage) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("cards")) {
                enterForeigenData(reader, pageSize, currentPage);
            } else if (name.equals("")) {

            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    static void enterForeigenData(JsonReader reader, int pageSize, int currentPage) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();

            while (reader.hasNext()) {

                String jsonName = reader.nextName();
                if (jsonName.equals("colors")) {
                    ArrayList<String> cardColors = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        cardColors.add(reader.nextString());
                    }
                    reader.endArray();
                    jsonName = reader.nextName();
                    searchEngine.temporaryCard.put("colors", cardColors);
                    logMessage=String.format("filterkey:%6s { %28s / has been added to temporary card }","colors",cardColors);
                    log.trace(logMessage);

                }


                if (jsonName.equals("purchaseUrls")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String jsonName2 = reader.nextName();
                        if (jsonName2.equals("cardmarket")) {
                            searchEngine.saveToTemporaryCard("cardmarketLink", reader);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    jsonName = reader.nextName();
                }

                if (jsonName.equals("rarity")) {
                    searchEngine.checkFilterAndSaveToTemporaryCard("rarity", reader);
                    jsonName = reader.nextName();
                }

                if (jsonName.equals("manaCost")) {
                    searchEngine.saveToTemporaryCard("manaCost", reader);
                    reader.nextName();
                }
                if (jsonName.equals("convertedManaCost")) {
                    searchEngine.saveToTemporaryCard("convertedManaCost", reader);
                    jsonName = reader.nextName();
                }

                if (jsonName.equals("foreignData")) {

                    reader.beginArray();
                    boolean languageNotFound = true;
                    while (reader.hasNext() && languageNotFound) {
                        reader.beginObject();
                        cardinformation:
                        while (reader.hasNext()) {
                            String currentTokenName = reader.nextName();
                            if (currentTokenName.equals("language") && reader.nextString().equals(searchEngine.language)) {
                                languageNotFound = false;
                            }
                            //aus irgendeinem grund muss language 2 mal geskipt werden
                            if (currentTokenName.equals(("language"))) {
                                currentTokenName = reader.nextName();
                            }

                            if (!languageNotFound && !currentTokenName.equals("language")) {
                                switch (currentTokenName) {
                                    case "type":
                                        if (!checkFilterAndSaveToTemporaryCard("type", reader)) {
                                            break cardinformation;
                                        }
                                        break;
                                    case "name":
                                        cardcount++;
                                        cardcountFirstSearch++;

                                        if (!checkFilterAndSaveToTemporaryCard("name", reader)) {
                                            break cardinformation;
                                        }
                                        //searchEngine.temporaryCard.put("name",reader.nextString());
                                        //System.out.println(reader.nextString());
                                        break;
                                    case "text":
                                        if (!checkFilterAndSaveToTemporaryCard("text", reader)) {
                                            break cardinformation;
                                        }
                                        //searchEngine.temporaryCard.put("effect",reader.nextString());
                                        break;
                                    case "multiverseId":
                                        saveToTemporaryCard("multiverseId", reader);
                                        break;
                                    default:
                                        //programm geht immer in den default branch
                                        reader.skipValue();
                                        break;
                                }

                            } else {
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
            Hyperlink url = new Hyperlink("https://img.scryfall.com/cards/normal/en/exp/43.jpg?1517813031");
            url.setOnAction(e->{

                try {
                    new ProcessBuilder("x-www-browser", url.getText()).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            includedPages += 1;
            boolean includeCard = includedPages <= currentPage*pageSize && includedPages >= (currentPage-1)*pageSize;
            if (!cardCheckFailed && temporaryCard.size()==9 && includeCard) {

                if(runSequential) {
                    //write to card to collection
                    Card card = new Card(temporaryCard.get("name").toString(),
                            temporaryCard.get("convertedManaCost").toString(),
                            temporaryCard.get("type").toString(),
                            temporaryCard.get("text").toString(),
                            FileManager.format,
                            temporaryCard.get("rarity").toString(),
                            url,
                            temporaryCard.get("cardmarketLink").toString(),
                            (ArrayList<String>) temporaryCard.get("colors"),
                            searchEngine.language,
                            temporaryCard.get("manaCost").toString(),
                            temporaryCard.get("multiverseId").toString());
                    searchEngine.currentResults.add(card);
                } else {
                    ParameterContainer params = new ParameterContainer();
                    params.name = temporaryCard.get("name").toString();
                    params.convertedManaCost = temporaryCard.get("convertedManaCost").toString();
                    params.type = temporaryCard.get("type").toString();
                    params.effect = temporaryCard.get("text").toString();
                    params.format = FileManager.format;
                    params.rarity = temporaryCard.get("rarity").toString();
                    params.pictureLink = url;
                    params.cardmarketLink = temporaryCard.get("cardmarketLink").toString();
                    params.colors = (ArrayList<String>) temporaryCard.get("colors");
                    params.language = searchEngine.language;
                    params.manaCost = temporaryCard.get("manaCost").toString();
                    params.multiverseId = temporaryCard.get("multiverseId").toString();

                    Thread t = new CardInstantiationThread(params, queue);
                    t.start();
                    log.info("thread "+t+" has been initialized and started");
                    workers.add(t);
                }
                // System.out.println(card.cardFeature);
//                logMessage=String.format("{TemporaryCard : %s has been COMPLETED and ADDED to currentResults Values :\n %s }\n",temporaryCard.get("name"),card.cardFeature);
                log.info(logMessage);
            }else{
                logMessage=String.format("{TemporaryCard: FAILED FILTER missing %d entries, last image of card :\n %s }\n",8-temporaryCard.size(),temporaryCard);
                log.debug(logMessage);
            }
            cardCheckFailed = false;
            temporaryCard = new HashMap<>();
        }
        reader.endArray();
    }
}

class CardInstantiationThread extends Thread {

    private ParameterContainer params;
    private final List<Card> queue;

    CardInstantiationThread(ParameterContainer params, List queue){
        this.params = params;
        this.queue = queue;
    }

    public void run() {
        Card tmp = new Card(params.name, params.convertedManaCost, params.type, params.effect, params.format,
                params.rarity, params.pictureLink, params.cardmarketLink, params.colors, params.language,
                params.manaCost, params.multiverseId);
        queue.add(tmp);
    }

}

class ParameterContainer{

    String name="";
    String convertedManaCost="";
    String type="";
    String effect="";
    String format="";
    String rarity="";
    Hyperlink pictureLink=null;
    String cardmarketLink="";
    ArrayList<String> colors=null;
    String language="";
    String manaCost="";
    String multiverseId="";


}