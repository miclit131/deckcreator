package ml131.de.hdm_stuttgart.mi.core;

import ml131.de.hdm_stuttgart.mi.datamodel.SearchRequest;
import ml131.de.hdm_stuttgart.mi.datamodel.SearchResult;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyException;
import ml131.de.hdm_stuttgart.mi.interfaces.SearchResultBroadcaster;
import ml131.de.hdm_stuttgart.mi.interfaces.SearchResultListener;
import ml131.de.hdm_stuttgart.mi.search.SearchEngine;
import ml131.de.hdm_stuttgart.mi.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


/***
 * Main Model component responsible for answering search queries and maintaining a history of search results.
 */
public class Model implements SearchResultBroadcaster {

    // registered interested listeners
    private List<SearchResultListener> listeners;

    // history of executed searches, to avoid executing the same search repeatedly
    private Map<SearchRequest, SearchResult> searchResults;

    private List<String> cardNames;

    private List<String> cardTypes;

    private Logger logger;

    public Model() {
        listeners = new ArrayList<>();
        searchResults = new HashMap<>();
        logger = LogManager.getLogger(Model.class);
    }

    /***
     * Performs asynchronous search and informs listeners with search result when finished.
     * @param searchRequest: specificiation of search interest
     */
    public void runAsyncSearch(SearchRequest searchRequest){
        if(listeners.isEmpty()){
            logger.warn("Running search with no listeners, this can lead to unexpected behaviour!");
        }
        logger.debug(String.format("Start running async. search %s", searchRequest));


        // Anonymous function below replaced by lambda expression below below
        // Thread t = new Thread(new Runnable() {
        //    public void run() { .. } })
        Thread t = new Thread(() -> {
            if(this.containsResultsFor(searchRequest)){
                logger.info("Reusing preexisting search result.");
                SearchResult searchResult = searchResults.get(searchRequest);
                notifyListeners(searchResult);
            }else{
                logger.info("Starting new search.");
                SearchEngine se = new SearchEngine(searchRequest.getFilter());
                try {
                    SearchResult searchResult = se.parseSetEdition(FileManager.openConnectionToFile("Standard"),
                            searchRequest.getPageSize(), searchRequest.getCurrentPage());
                    searchResult.setMsg(searchRequest.getMsg());
                    searchResults.put(searchRequest, searchResult);

                    if(cardNames == null)
                        cardNames = se.getCardNames();

                    if(cardTypes == null) {
                        cardTypes = new ArrayList<>();
                        Set<String> uniqueTypes = new HashSet<>(se.getCardTypes()); // reomve duplicates
                        cardTypes.addAll(uniqueTypes);
                    }
                    notifyListeners(searchResult);
                } catch (LogfriendlyException error) {
                    logger.error(error.getMessage());
                }

            }
        });
        t.start();
    }

    /***
     * Used only for unit testing. It replicates the functionality of runAsyncSearch
     * @return search result of which the contents are analyzed in the different tests
     */
    public SearchResult runSyncSearch(SearchRequest searchRequest) throws LogfriendlyException {
        if(this.containsResultsFor(searchRequest)) {
            return searchResults.get(searchRequest);
        }else{
            SearchEngine se = new SearchEngine(searchRequest.getFilter());
            SearchResult searchResult = se.parseSetEdition(FileManager.openConnectionToFile("Standard"),
                    searchRequest.getPageSize(), searchRequest.getCurrentPage());
            searchResult.setMsg(searchRequest.getMsg());
            searchResults.put(searchRequest, searchResult);

            if (cardNames == null)
                cardNames = se.getCardNames();

            if (cardTypes == null) {
                cardTypes = new ArrayList<>();
                Set<String> uniqueTypes = new HashSet<>(se.getCardTypes()); // reomve duplicates
                cardTypes.addAll(uniqueTypes);
            }
            return searchResult;
        }
    }

    /***
     * convenience function for checking if we have performed search specified by searchRequest already earlier.
     * @param searchRequest: search specification
     * @return true if we already have results for searchRequest
     */
    public boolean containsResultsFor(SearchRequest searchRequest){
        return this.searchResults.containsKey(searchRequest);
    }


    /***
     * Convenience function that returns search result for a specified request, otherwise returns empty search result
     * @param request: search specification
     * @return search result if available
     */
    public SearchResult getResults(SearchRequest request){
        if(containsResultsFor(request)){
            logger.info(String.format("Returning old results for search query: %s", request));
            return this.searchResults.get(request);
        }else{
            logger.warn("Returning empty search result, please use Model.containsResultsFor first");
            return new SearchResult();
        }
    }

    /***
     * Required for autocomplete functionality
     * @return list of cardNames collected during first search executed
     */
    public List<String> getCardNames() {
        return cardNames;
    }

    /***
     * Required for autocomplete functionality
     * @return list of card types collected during first search executed
     */
    public List<String> getCardTypes() {
        return cardTypes;
    }

    @Override
    public void addListener(SearchResultListener listener) {
        logger.debug(String.format("Listener %3s added.", listener.getClass().toString()));
        this.listeners.add(listener);
    }


    @Override
    public void notifyListeners(SearchResult result) {
        for(SearchResultListener listener : listeners){
            listener.onSearchResultRetrieved(result);
            logger.debug(String.format("Listener of type %3s notified.", listener.getClass().toString()));
        }
    }



}
