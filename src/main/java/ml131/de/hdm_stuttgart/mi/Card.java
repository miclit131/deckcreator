package ml131.de.hdm_stuttgart.mi;

import java.util.ArrayList;
import java.util.HashMap;

public class Card {
    HashMap<String,Object> cardFeature = new HashMap<>();

    private String name;
    private String convertedManaCost;
    private String type;
    private String effect;
    private String format;
    private String rarity;
    private String pictureLink;
    private String cardmarketLink;
    private ArrayList<String> colors;
    private String language;
    private String manaCost;


    public Card(String name,String type,String manaCost){
        this.name=name;
        this.type=type;
        this.manaCost=manaCost;
    }

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
        this.name=name;
        cardFeature.put("convertedManaCost",convertedManaCost);
        cardFeature.put("type",type);
        this.type=type;
        cardFeature.put("effect",effect);
        cardFeature.put("format",format);
        cardFeature.put("rarity",rarity);
        cardFeature.put("pictureLink",pictureLink);
        cardFeature.put("cardmarketLink",cardmarketLink);
        cardFeature.put("colors",colors);
        cardFeature.put("language",language);
        cardFeature.put("manaCost",manaCost);
        this.manaCost=manaCost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
