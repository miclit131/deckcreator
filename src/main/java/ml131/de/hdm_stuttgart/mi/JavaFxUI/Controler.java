package ml131.de.hdm_stuttgart.mi.JavaFxUI;

import ml131.de.hdm_stuttgart.mi.FileManager;
import ml131.de.hdm_stuttgart.mi.searchEngine;
import ml131.de.hdm_stuttgart.mi.FileManager;

import java.io.IOException;

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

}
