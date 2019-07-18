package ml131.de.hdm_stuttgart.mi.search;

import javafx.scene.control.Hyperlink;
import ml131.de.hdm_stuttgart.mi.interfaces.Color;

import java.util.List;

/***
 * Helper class for storing all required values needed for instantiating a Card object, such that we can
 * pass a single object to the thread creating the object.
 */
class ParameterContainer{

    String name="";
    String convertedManaCost="";
    String type="";
    String effect="";
    String format="";
    String rarity="";
    Hyperlink pictureLink=null;
    List<Color> colors=null;
    String language="";
    String manaCost="";
    String multiverseId="";
    Integer count=null;

}