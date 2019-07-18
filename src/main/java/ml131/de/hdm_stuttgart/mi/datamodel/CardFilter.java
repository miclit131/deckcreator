package ml131.de.hdm_stuttgart.mi.datamodel;

import ml131.de.hdm_stuttgart.mi.datamodel.color.ColorFactory;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyInvalidColorException;
import ml131.de.hdm_stuttgart.mi.interfaces.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class CardFilter {

    private Map<String, String> singleValueFilter;
    private List<Color> colorFilter;
    private List<String> nameFilter;
    private Logger logger;
    private Color colorless;


    public CardFilter(){
        logger = LogManager.getLogger(CardFilter.class);
        nameFilter = new ArrayList<>();
        colorFilter = new ArrayList<>();
        singleValueFilter = new HashMap<>();
        try {
            colorless = new ColorFactory().getColor("colorless");
        } catch (LogfriendlyInvalidColorException e) {
            e.printStackTrace();
        }
    }


    /**
     * Adds a single string filter to the CardFilter. Filters must be first configured before they can be used.
     * @param filterKey: type of piece of information
     * @param filterValue: value to be checked against
     */
    public void configureFilterValue(String filterKey, String filterValue){
        if(this.singleValueFilter == null){
            this.singleValueFilter = new HashMap<>();
        }
        if(filterKey.equals("name")){
            configureNameList(filterValue);
        }
        singleValueFilter.put(filterKey, filterValue);
    }


    /***
     * Sets list of allowed card names, anything else will be rejected by the filter.
     * @param names: allowed names.
     */
    public void configureNameList(List<String> names){
        nameFilter = names;
    }


    /***
     * Wrapper function for single value names. This happens if we get the name from the textbox. Other scenario is
     * getting a name list from the imported deck list.
     * @param name: card name
     */
    private void configureNameList(String name){
        List<String> names = new ArrayList<>();
        names.add(name);
        configureNameList(names);
    }


    /***
     * Sets the list of allowed color. For any other color the filter rejects the card.
     * @param filterValues: allowed color
     */
    public void configureColors(List<Color> filterValues){
        if(colorFilter == null){
            colorFilter = new ArrayList<>();
        }
        colorFilter.addAll(filterValues);
    }


    /***
     * Receives a card name and searches in the list of filter names if it matches any. If yes, it passes the test.
     * @param name: search value
     * @return true if it matches any of the required names
     */
    public boolean checkCardName(String name){
        logger.trace(String.format("checking card name %3s", name));
        if(nameFilter.size()>0){
            for (String tmpFilterValue : nameFilter) {
                if (name.toLowerCase().contains(tmpFilterValue.toLowerCase())) {
                    logger.trace("Matching card name found, value accepted");
                    return true;
                }
            }
            logger.trace("No matching card name found, value rejected.");
            return false;
        }else{
            logger.trace("No card names setup in filter, value accepted.");
            return true;
        }
    }

    /***
     * Checks if there is a color overlap between the color of a card and the values set in the color filter. Corner
     * case: check is also passed if there is no color and the filter is set to COLORLESS.
     * @param filterValues: color values of card to be checked.
     * @return true if color filter is set and overlap exist
     */
    public boolean checkColors(List<Color> filterValues){
        List<Color> allowedColors = colorFilter;

        // color filter is unset, test on unset filter can never fail
        if(allowedColors.size() == 0){
            return true;
        }

        boolean passedFilter = false;
        for(Color tmp : filterValues){
            if(allowedColors.contains(tmp)){
                passedFilter = true;
                logger.trace(String.format("%3s card color matched, color list accepted", tmp.getColor()));
                break;
            }
        }

        // colorless cards have no values in filter values because they are colorless
        if(allowedColors.contains(colorless) && filterValues.size() == 0){
            passedFilter = true;
            logger.trace("This card has no color and hence is colorless, color list accepted.");
        }

        logger.trace("Color list rejected.");
        return passedFilter;
    }

    /***
     * Checks if a specific card text contains the filter value, if the filter is set. This method is applicable for
     * any filter that is a single string
     * @param filterKey: filter type
     * @param cardTypeText: input to be evaluated
     * @return true if filter is set and filter value is contained by cardTypeText, i.e. true if filter says "ok"
     */
    public boolean checkSingleValue(String filterKey, String cardTypeText) {
        boolean filterIsSet = singleValueFilter.containsKey(filterKey);

        if(filterKey.equals("name")){
            return checkCardName(cardTypeText);
        }else{
            if(filterIsSet){
                String filterValue = singleValueFilter.get(filterKey);
                if (cardTypeText.contains(filterValue)) {
                    logger.trace(String.format("filterkey:%6s { %30s matched cardFilter : %12s }", filterKey,
                            cardTypeText, filterValue));
                    return true;
                } else {
                    logger.trace(String.format("filterkey:%6s { %30s failed cardFilter : %12s }", filterKey,
                            cardTypeText, filterValue));
                    return false;
                }
            }else{
                return true;
            }
        }
    }

    /***
     * Checks if current filter is set to check only for colorless cards
     * @return true if colorfilter contains only one value, Color.COLORLESS
     */
    public boolean filterColorlessOnly(){
        return this.colorFilter.size() == 1 && colorFilter.contains(colorless);
    }

    /**
     * Returns if the card is a colorless card by checking if it looks like {X} where X is a number
     * @param manaCostValue: string value to be checked
     * @return whether card is a colorless card
     */
    public boolean cardIsColorless(String manaCostValue){
        String[] values = manaCostValue.split("\\{"); // {..} is potentially colorless, {..}{..} is not
        boolean isPotentiallyColorlessCard = values.length == 2;
        logger.trace("mana cost value %3s belongs potentially to a colorless card");
        String tmp = manaCostValue.replace("{","").replace("}", "");
        boolean finalcheck = (isPotentiallyColorlessCard && isNumeric(tmp)) || tmp.equals("X");
        if(finalcheck){
            logger.trace("card is colorless, check passed");
        }else{
            logger.trace("card is NOT colorless, check NOT passed");
        }
        return finalcheck;
    }

    public boolean filterIsSet(String filterKey){
        return this.singleValueFilter.containsKey(filterKey) || (filterKey.equals("color") && !this.colorFilter.isEmpty());
    }

    /**
     * Helper for checking if card is colorless
     * @param str: probes if string is a number
     * @return true if card is a number
     */
    private static boolean isNumeric(String str) {
        // https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardFilter that = (CardFilter) o;
        return singleValueFilter.equals(that.singleValueFilter) &&
                colorFilter.equals(that.colorFilter) &&
                nameFilter.equals(that.nameFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(singleValueFilter, colorFilter, nameFilter);
    }


    @Override
    public String toString() {
        return "CardFilter{" +
                "singleValueFilter=" + singleValueFilter +
                ", colorFilter=" + colorFilter +
                ", nameFilter=" + nameFilter +
                '}';
    }


}
