package ml131.de.hdm_stuttgart.mi.interfaces;

import ml131.de.hdm_stuttgart.mi.datamodel.SearchResult;

/***
 * Interface used for asynchronous comminication between producers of serach result (SearchResultBroadcaster) and
 * consumers of search results (SearchResultListener). Producers must allow for adding multiple listeners and
 * informing them with the result once its available.
 */
public interface SearchResultBroadcaster {

    void addListener(SearchResultListener listener);

    void notifyListeners(SearchResult result);

}
