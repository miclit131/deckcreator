package ml131.de.hdm_stuttgart.mi.search;

import ml131.de.hdm_stuttgart.mi.datamodel.Card;

import java.util.List;


/***
 * Thread creating an actual card instance. This is expensive because the image is loaded online and resized inside
 * the constructor. Users of this class must ensure that the queue they pass in is threadsafe.
 */
class CardInstantiationThread extends Thread {

    private ParameterContainer params;
    private final List<Card> queue;

    CardInstantiationThread(ParameterContainer params, List<Card> queue){
        this.params = params;
        this.queue = queue;
    }

    public void run() {
        Card tmp = new Card(params.name, params.convertedManaCost, params.type, params.effect, params.format,
                params.rarity, params.pictureLink, params.colors, params.language,
                params.manaCost, params.multiverseId, params.count);
        queue.add(tmp);
    }
}
