package ml131.de.hdm_stuttgart.mi.JavaFxUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ml131.de.hdm_stuttgart.mi.Card;
import ml131.de.hdm_stuttgart.mi.FileManager;
import ml131.de.hdm_stuttgart.mi.searchEngine;
import ml131.de.hdm_stuttgart.mi.FileManager;

import java.io.IOException;
import java.util.ArrayList;

public class Controler {
    Boolean cmcIsSet=false;
    Boolean typeIsSet=false;
    Boolean effectIsSet=false;
    Boolean formatIsSet=false;
    Boolean nameIsSet=false;
    Boolean rarityIsSet=false;
    Boolean colorIsSet=false;

    String cmc;
    String type;
    String effect;
    String color;
    String format;
    String name;
    String rarity;

    void setUpAndSearch() throws IOException {
        searchEngine.fillCurrentFilter(cmc,type, effect,color,format,name,rarity);
        FileManager.getFormat();
        searchEngine.enterSetEdition(FileManager.openConnectionToFile(FileManager.format));
    }

    public static ObservableList<Card> getCards(){
        ObservableList<Card> cards= FXCollections.observableArrayList();
        cards.addAll(searchEngine.currentResults);

        return cards;
    }

}
