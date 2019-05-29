package ml131.de.hdm_stuttgart.mi;

import java.util.HashMap;
import java.util.Map;

public class CardFilter {
/*
    Boolean cmcIsSet=false;
    Boolean typeIsSet=false;
    Boolean effectIsSet=false;
    Boolean formatIsSet=false;
    Boolean nameIsSet=false;
    Boolean rarityIsSet=false;
    Boolean colorIsSet=false;*/
    HashMap<String,Boolean> keySetting= new HashMap<>();{{
        keySetting.put("cmcIsSet",false);
        keySetting.put("typeIsSet",false);
        keySetting.put("textIsSet",false);
        keySetting.put("formatIsSet",false);
        keySetting.put("nameIsSet",false);
        keySetting.put("rarityIsSet",false);
        keySetting.put("colorIsSet",false);

     } }
    HashMap<String,Object> cardFilter = new HashMap<>();
    String[] color=new String[6];

    public CardFilter(){}

    public CardFilter(Map<String,Object> filterSet){
        for (Map.Entry<String, Object> entry : filterSet.entrySet()) {
            cardFilter.put(entry.getKey(), entry.getValue());

            switch (entry.getKey()) {
                case "cmc":
                   // cmcIsSet = true;
                    keySetting.put("cmcIsSet",true);
                    break;
                case "type":
                    //typeIsSet = true;
                    keySetting.put("typeIsSet",true);
                    break;
                case "effect":
                   // effectIsSet = true;
                    keySetting.put("textIsSet",true);
                    break;
                case "format":
                    //formatIsSet = true;
                    keySetting.put("formatIsSet",true);
                    break;
                case "name":
                   // nameIsSet = true;
                    keySetting.put("nameIsSet",true);
                    break;
                case "rarity":
                    //rarityIsSet = true;
                    keySetting.put("rarityIsSet",true);
                    break;
                case "color":
                    //colorIsSet = true;
                    keySetting.put("colorIsSet",true);
                    break;
                default:
                    System.out.println("unexpected value in cardfilter " + entry.getKey());
            }
        }
    }
    public HashMap getFilter (){
        return cardFilter;
    }
}
