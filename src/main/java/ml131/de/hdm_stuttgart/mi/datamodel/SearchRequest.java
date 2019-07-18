package ml131.de.hdm_stuttgart.mi.datamodel;

import java.util.Objects;


/***
 *  Conainer storing all necessary information required to start a search.
 */
public class SearchRequest {

    private final CardFilter filter;

    private final Integer currentPage;

    private final Integer pageSize;

    private String msg;

    /***
     * Each search request specifies what card the requestor is looking for with the filter and which part of the
     * search result should be retrieved. A message is included to later identify what
     * @param filter: which type of card(s) should be searched for
     * @param currentPage: offset for when to start collecting cards
     * @param pageSize: offset for number of cards to collect
     * @param msg: supposed to be returned as is within a SearchResult, See SearchResult.setMsg for more information
     */
    public SearchRequest(CardFilter filter, Integer currentPage, Integer pageSize, String msg) {
        this.filter = filter;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }


    public CardFilter getFilter() {
        return filter;
    }


    public Integer getCurrentPage() {
        return currentPage;
    }


    public Integer getPageSize() {
        return pageSize;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchRequest that = (SearchRequest) o;
        return filter.equals(that.filter) &&
                currentPage.equals(that.currentPage) &&
                pageSize.equals(that.pageSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, currentPage, pageSize);
    }


    @Override
    public String toString() {
        return "SearchRequest{" +
                "filter=" + filter +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", msg='" + msg + '\'' +
                '}';
    }

}
