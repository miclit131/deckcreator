package ml131.de.hdm_stuttgart.mi;

import ml131.de.hdm_stuttgart.mi.core.Model;
import ml131.de.hdm_stuttgart.mi.datamodel.Card;
import ml131.de.hdm_stuttgart.mi.datamodel.CardFilter;
import ml131.de.hdm_stuttgart.mi.datamodel.SearchRequest;
import ml131.de.hdm_stuttgart.mi.datamodel.SearchResult;
import ml131.de.hdm_stuttgart.mi.datamodel.color.ColorFactory;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyException;
import ml131.de.hdm_stuttgart.mi.interfaces.Color;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class JUnitTests extends JavafxViewTest {

    private List<String> cardNames;
    private CardFilter filter;
    private Model searcher;
    private ColorFactory cf;

    @Before
    public void setUp() {
        filter = new CardFilter();
        cardNames = new ArrayList<>();
        searcher = new Model();
        cf = new ColorFactory();
    }


    @Test
    public void amountOfCardsFilteredTest() throws LogfriendlyException {
        // Tests if searcher returns number of requested documents
        int currentPage = 4;
        int pageSize = 3;
        SearchRequest searchRequest = new SearchRequest(new CardFilter(), currentPage, pageSize, "");
        SearchResult result = searcher.runSyncSearch(searchRequest);
        assertEquals(result.getCards().size(), pageSize);
    }

    @Test
    public void paginationTest() throws LogfriendlyException {
        int currentPage = 12;
        int pageSize = 3;

        SearchRequest firstSearchRequest = new SearchRequest(new CardFilter(), currentPage, pageSize, "");
        SearchResult firstResult = searcher.runSyncSearch(firstSearchRequest);

        SearchRequest secondSearchRequest = new SearchRequest(new CardFilter(), currentPage + 1, pageSize, "");
        SearchResult secondResult = searcher.runSyncSearch(secondSearchRequest);

        for (Card card : firstResult.getCards()) {
            assertFalse(secondResult.getCards().contains(card));
        }

        for (Card card : secondResult.getCards()) {
            assertFalse(firstResult.getCards().contains(card));
        }
        assertEquals(firstResult.getHits(), secondResult.getHits());
    }

    @Test
    public void correctColorsRetrievedTest() throws LogfriendlyException {
        // Tests if requested results are all of the desired color
        int currentPage = 0;
        int pageSize = 1;

        List<String> testColors = new ArrayList<>();
        testColors.add("black");
        testColors.add("blue");
        testColors.add("white");
        testColors.add("green");
        testColors.add("red");
        testColors.add("colorless");

        for (String color : testColors) {

            Color filtercolor = cf.getColor(color);
            List<Color> colorFilter = Collections.singletonList(filtercolor);
            CardFilter filter = new CardFilter();
            filter.configureColors(colorFilter);

            SearchRequest request = new SearchRequest(filter, currentPage, pageSize, "");
            SearchResult response = searcher.runSyncSearch(request);

            assertTrue(response.getCards().get(0).getColors().contains(filtercolor));
        }
    }

    @Test
    public void nameFilterSuccessTest() throws LogfriendlyException {
        // Checks if specific single card can be retrieved
        String specificCardName = "Akademie-Sceada";
        SearchResult result = search(getConfiguredFilter("name", specificCardName));
        assertEquals(1, result.getCards().size());
        assertEquals(result.getCards().get(0).getName(), specificCardName);
    }

    @Test
    public void nameFilterFailureTest() throws LogfriendlyException {
        // checks if empty result list is returned if card name does not exist
        String specificCardName = "Diese Karte existiert bestimmt nicht";
        SearchResult result = search(getConfiguredFilter("name", specificCardName));
        assertTrue(result.getCards().isEmpty());
    }

    /**
     * Check if a list of pre-defined correct cards is retrieved by the search.
     * Correct cards are cards that are actual existing in the Magic card game.
     *
     * @throws LogfriendlyException
     */
    @Test
    public void multipleNameFilterSuccessTest() throws LogfriendlyException {

        cardNames.add("Abenteuerlust");
        cardNames.add("Adeliz Glutwind");
        cardNames.add("Aesthirgleiter");

        filter.configureNameList(cardNames);
        SearchResult result = search(filter);

        for (String cardName: cardNames) {
            assertTrue( convertResultListToCardNameList(result.getCards()).contains(cardName));
        }
    }

    /**
     * Check if a list of pre-defined cards, comprising of one false card,
     * is retrieved by the search.
     *
     * @throws LogfriendlyException
     */
    @Test
    public void multipleNameFilterFailureTest_0() throws LogfriendlyException {

        cardNames.add("Abenteuerlust");
        cardNames.add("Adeliz Glutwind");
        cardNames.add("Pickachu");

        filter.configureNameList(cardNames);
        SearchResult result = search(filter);

        int actualCorrectCards = 0;
        int expectedCorrectCards = 2;

        for (Card card: result.getCards()) {
            if(card.getName().equals("Abenteuerlust") || card.getName().equals("Adeliz Glutwind")) {
                actualCorrectCards++;
            }
        }

        assertEquals(2, actualCorrectCards, expectedCorrectCards);
    }


    @Test
    public void effectFilterSuccessTest() throws LogfriendlyException {
        // checks if result list is correct, i.e. if each card contains effect filter text
        String effectText = "die Bonuskosten des";
        SearchResult result = search(getConfiguredFilter("text", effectText));

        for (Card card : result.getCards()) {
            assertTrue(card.getEffect().contains(effectText) || card.getEffect().equals(""));
        }
    }

    @Test
    public void effectFilterFailureTest() throws LogfriendlyException {
        // checks if result list is empty because specified effect doesn't exist
        String effectText = "diesen Text gibt es bestimmt nicht";
        SearchResult result = search(getConfiguredFilter("text", effectText));
        assertTrue(result.getCards().isEmpty());
    }


    @Test
    public void typeFilterSuccessTest() throws LogfriendlyException {
        // checks if result list is correct, i.e. if each card is of the right type
        String typevalue = "Kreatur";
        SearchResult result = search(getConfiguredFilter("type", typevalue));
        assertFalse(result.getCards().isEmpty());

        for (Card card : result.getCards()) {
            assertTrue(card.getType().contains(typevalue));
        }
    }

    @Test
    public void typeFilterFailureTest() throws LogfriendlyException {
        // checks if emoty result list is returned because type does not exist
        String typevalue = "Dothraki";
        SearchResult result = search(getConfiguredFilter("type", typevalue));
        assertTrue(result.getCards().isEmpty());
    }


    @Test
    public void reusingOldResultsTest() throws LogfriendlyException {
        // Checks if searcher keeps track of past results, test with timeout, the second search should happen
        // almost immediately
        CardFilter emptyFilter = new CardFilter();

        SearchRequest firstRequest = new SearchRequest(emptyFilter, 0, 10, "");
        SearchResult result = searcher.runSyncSearch(firstRequest);

        long startTime = System.currentTimeMillis();
        SearchRequest secondRequest = new SearchRequest(emptyFilter, 2, 3, "");
        SearchResult secondResult = searcher.runSyncSearch(secondRequest);
        long durationSecondSearch = System.currentTimeMillis() - startTime;


        for (Card card : secondResult.getCards()) {
            assertFalse(result.getCards().contains(card));
        }

        //re-run first search request
        startTime = System.currentTimeMillis();
        SearchResult thirdResult = searcher.runSyncSearch(firstRequest);
        long durationThirdSearch = System.currentTimeMillis() - startTime;

        //Third search is supposed to be much faster because of cached results
        assertTrue(durationThirdSearch < durationSecondSearch);

        for (Card card : thirdResult.getCards()) {
            assertTrue(result.getCards().contains(card));
        }
    }

    /***
     * Helper function to avoid redundancy, creates a filter and configures it
     * @param key: filter type
     * @param value: filter value
     * @return preconfigured card filter
     */
    private CardFilter getConfiguredFilter(String key, String value) {
        filter.configureFilterValue(key, value);
        return filter;
    }


    /***
     * Helper function for tests that test specific filter functionalities
     * @param filter: depends on test
     * @return expected correct search result according to filder
     * @throws LogfriendlyException: if parsing error happes
     */
    private SearchResult search(CardFilter filter) throws LogfriendlyException {
        SearchRequest request = new SearchRequest(filter, 0, 10, "");
        return searcher.runSyncSearch(request);
    }


    /**
     * Helper function to convert a list of cards into a list of card names.
     * @param actualResult: list of results of a search
     * @return converted list of card names
     */
    private static HashSet<String> convertResultListToCardNameList(List<Card> actualResult) {
        HashSet<String> convertedList = new HashSet<>();

        for(Card card: actualResult) {
            convertedList.add(card.getName());
        }
        return convertedList;
    }
}