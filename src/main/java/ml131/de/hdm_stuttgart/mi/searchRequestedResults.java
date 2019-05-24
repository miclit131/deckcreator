package ml131.de.hdm_stuttgart.mi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class searchRequestedResults {
    static int resultcount=0;
    CardFilter appliedFilter=new CardFilter();
    HashMap<String,HashMap<String,Object>> result =new HashMap<>();

    public void saveResult(ArrayList<Card> result,CardFilter appliedFilter){

      //  result.add("requestcount"+resultcount,new HashMap<String,Object>(){})
resultcount++;
    }
}
