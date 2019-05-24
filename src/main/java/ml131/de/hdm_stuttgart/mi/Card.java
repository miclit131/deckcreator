package ml131.de.hdm_stuttgart.mi;

import java.util.HashMap;

public class Card {
    HashMap<String,Object> cardFeature = new HashMap<>();


    public Card(String name,
                int cmc,
                String type,
                String effect,
                String format,
                String rarity,
                String pictureLink,
                String cardmarketLink,
                String colors){

        cardFeature.put("cmc",cmc);
        cardFeature.put("type",type);
        cardFeature.put("effect",effect);
        cardFeature.put("format",format);
        cardFeature.put("rarity",rarity);
        cardFeature.put("pictureLink",pictureLink);
        cardFeature.put("cardmarketLink",cardmarketLink);
        cardFeature.put("colors",colors);
    }


}
