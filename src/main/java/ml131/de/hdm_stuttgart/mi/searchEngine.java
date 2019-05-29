package ml131.de.hdm_stuttgart.mi;



import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class searchEngine {

    static int cardcount = 0;
    static HashMap<String, Object> temporaryCard = new HashMap<>();
    public static CardFilter currentFilter = new CardFilter();
    static ArrayList<Card> currentResults=new ArrayList<>();
    static String language = "German";
    static boolean cardCheckFailed = false;


    static boolean checkFilterAndSaveToTemporaryCard(String filterKey, JsonReader reader) throws IOException {
        String cardTypeText = reader.nextString();
        //filter is set and fullFilled
        if (searchEngine.currentFilter.keySetting.get(filterKey + "IsSet") &&
                cardTypeText.contains(searchEngine.currentFilter.cardFilter.get(filterKey).toString())) {
            searchEngine.temporaryCard.put(filterKey, cardTypeText);
            System.out.println(filterKey + " " + cardTypeText);
            return true;
            //filter isn't set
        } else if (!searchEngine.currentFilter.keySetting.get(filterKey + "IsSet")) {
            searchEngine.temporaryCard.put(filterKey, cardTypeText);
            return true;
            //filter is set but failed
        } else {
            searchEngine.cardCheckFailed = true;
            return false;
        }
    }

    static void saveToTemporaryCard(String filterKey, JsonReader reader) throws IOException {
        String cardTypeText = reader.nextString();
        searchEngine.temporaryCard.put(filterKey, cardTypeText);
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

        Map<String, Object> filterImage = new HashMap<>();
        if (!cmc.equals("")) {
            filterImage.put("cmt", cmc);
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
    }


    public static void enterSetEdition(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            reader.nextName();
            enterCardSection(reader);
        }
        reader.endObject();
        searchRequestedResults.saveResult(currentResults,currentFilter);
    }

    static void enterCardSection(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("cards")) {
                enterForeigenData(reader);
            } else if (name.equals("")) {

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
                if (jsonName.equals("colors")) {
                    ArrayList<String> cardColors = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        cardColors.add(reader.nextString());
                    }
                    reader.endArray();
                    jsonName = reader.nextName();
                    searchEngine.temporaryCard.put("colors", cardColors);
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
                                        reader.skipValue();
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
            if (!cardCheckFailed&&temporaryCard.size()==8) {
                //write to card to collection
                Card card = new Card(temporaryCard.get("name").toString(),
                        temporaryCard.get("convertedManaCost").toString(),
                        temporaryCard.get("type").toString(),
                        temporaryCard.get("text").toString(),
                        FileManager.format,
                        temporaryCard.get("rarity").toString(),
                        "https://img.scryfall.com/cards/normal/en/exp/43.jpg?1517813031",
                        temporaryCard.get("cardmarketLink").toString(),
                        (ArrayList<String>) temporaryCard.get("colors"),
                        searchEngine.language,
                        temporaryCard.get("manaCost").toString());
                //save everything in a resultList
                searchEngine.currentResults.add(card);
                System.out.println(card.cardFeature);
            }
            cardCheckFailed = false;
            temporaryCard = new HashMap<>();
        }
        reader.endArray();

    }
}
