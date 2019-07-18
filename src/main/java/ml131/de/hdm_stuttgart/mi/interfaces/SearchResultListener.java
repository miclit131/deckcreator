package ml131.de.hdm_stuttgart.mi.interfaces;

import ml131.de.hdm_stuttgart.mi.datamodel.SearchResult;

/***
 * Interface used for asynchronous comminication between producers of serach result (SearchResultBroadcaster) and
 * consumers of search results (SearchResultListener). Consumers offer a standard method (onSearchResultRetrieved)
 * that is used by the broadcaster such that they can get notified.
 */
public interface SearchResultListener {
    void onSearchResultRetrieved(SearchResult result);
}
