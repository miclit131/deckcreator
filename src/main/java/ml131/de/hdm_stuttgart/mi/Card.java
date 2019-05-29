package ml131.de.hdm_stuttgart.mi;

import java.util.ArrayList;
import java.util.HashMap;

public class Card {
    HashMap<String,Object> cardFeature = new HashMap<>();


    public Card(String name,
                String convertedManaCost,
                String type,
                String effect,
                String format,
                String rarity,
                String pictureLink,
                String cardmarketLink,
                ArrayList<String> colors,
                String language,
                String manaCost){

        cardFeature.put("name",name);
        cardFeature.put("convertedManaCost",convertedManaCost);
        cardFeature.put("type",type);
        cardFeature.put("effect",effect);
        cardFeature.put("format",format);
        cardFeature.put("rarity",rarity);
        cardFeature.put("pictureLink",pictureLink);
        cardFeature.put("cardmarketLink",cardmarketLink);
        cardFeature.put("colors",colors);
        cardFeature.put("language",language);
        cardFeature.put("manaCost",manaCost);
    }


}
