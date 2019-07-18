package ml131.de.hdm_stuttgart.mi.datamodel;

import java.util.ArrayList;
import java.util.List;


/***
 * Conainer storing all necessary information related to a finished search.
 */
public class SearchResult {

    private final List<Card> cards;
    private final Integer hits;
    private String msg;

    public SearchResult(){
        this.cards = new ArrayList<>();
        this.hits = 0;
    }


    /**
     * Each search result consists of a list of cards together with the total number of cards. This is necessary because
     * we don't always return all available cards (that pass the filter), so we need the information how many cards are
     * valid in total such that we can still show the correct pagination and allow for querying other parts/pages.
     * @param cards: retrieved cards
     * @param hits: total available number of cards
     */
    public SearchResult(List<Card> cards, Integer hits) {
        this.cards = cards;
        this.hits = hits;
    }

    /***
     * Each searchResult has an optional message that can be used to dispatch the result. It's used to determine if
     * the decklist (msg="import") or the serach result list (msg="search") should be updated. Refer also  to
     * Controller.onSearchResultRetrieved.
     * @param msg: Message
     */
    public void setMsg(String msg){
        this.msg = msg;
    }

    public Integer getHits() {
        return hits;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        StringBuilder cards= new StringBuilder();

        // don't include all cards because list can get huge
        int include = Math.max(2, this.cards.size());

        for(int i=0; i<include; ++i){
            cards.append(this.cards.get(i).toString());
        }

        return "SearchResult{" +
                "cards=" + cards.toString() +
                ", hits=" + hits +
                ", msg='" + msg + '\'' +
                '}';
    }
}
