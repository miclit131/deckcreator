package ml131.de.hdm_stuttgart.mi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class searchRequestedResults {
    static int resultcount=0;
    static ArrayList<HashMap<String,ArrayList<Object>>> results = new ArrayList<>();
    CardFilter appliedFilter=new CardFilter();



    public static void saveResult(ArrayList<Card> result,CardFilter appliedFilter){
        ArrayList<Object> searchValues=new ArrayList<>();
        HashMap<String,Card> cardHashMap= new HashMap<>();

        for (Card card: result
             ) {
           cardHashMap.put(card.cardFeature.get("name").toString(),card);
        }

        searchValues.add(cardHashMap);
        searchValues.add(appliedFilter);
        searchRequestedResults.results.add(new HashMap<String, ArrayList<Object>>(){{
            put("searchnumber:"+resultcount,searchValues);
        }});

      //  result.add("requestcount"+resultcount,new HashMap<String,Object>(){})
resultcount++;
    }
}
